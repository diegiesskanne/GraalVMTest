package de.eso.bytebuddy;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * JVM-Agent for intercepting calls to Class and Proxy in order to log them out via
 * ByteCode-Manipulation
 */
public final class BootstrapAgent {
  private BootstrapAgent() {}

  public static void premain(String arg, Instrumentation instrumentation) throws Exception {
    File tempFolder = Files.createTempDirectory("tmp").toFile();

    AgentBuilder agentBuilder =
        new AgentBuilder.Default() //
            .disableClassFormatChanges() //
            .ignore(none())
            .with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE)
            .with(AgentBuilder.RedefinitionStrategy.REDEFINITION)
            .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
            .enableBootstrapInjection(instrumentation, tempFolder);

    // Load ClassWriter into Bootstrap-Classloader, in order to access it from @Advice
    Map<TypeDescription.ForLoadedType, byte[]> forLoadedType = new HashMap<>();

    forLoadedType.putIfAbsent(
        new TypeDescription.ForLoadedType(EventType.class),
        ClassFileLocator.ForClassLoader.read(EventType.class));
    forLoadedType.putIfAbsent(
        new TypeDescription.ForLoadedType(ClassEvent.class),
        ClassFileLocator.ForClassLoader.read(ClassEvent.class));
    forLoadedType.putIfAbsent(
        new TypeDescription.ForLoadedType(ImmutableClassEvent.class),
        ClassFileLocator.ForClassLoader.read(ImmutableClassEvent.class));
    forLoadedType.putIfAbsent(
        new TypeDescription.ForLoadedType(ImmutableClassEvent.Builder.class),
        ClassFileLocator.ForClassLoader.read(ImmutableClassEvent.Builder.class));
    forLoadedType.putIfAbsent(
        new TypeDescription.ForLoadedType(ClassWriter.class),
        ClassFileLocator.ForClassLoader.read(ClassWriter.class));
    forLoadedType.putIfAbsent(
        new TypeDescription.ForLoadedType(BootstrapAgent.class),
        ClassFileLocator.ForClassLoader.read(BootstrapAgent.class));
    ClassInjector.UsingInstrumentation.of(
            tempFolder, ClassInjector.UsingInstrumentation.Target.BOOTSTRAP, instrumentation)
        .inject(forLoadedType);

    agentBuilder
        .type(is(Class.class))
        // Class#forName
        .transform(
            (builder, typeDescription, classLoader, module) ->
                builder.visit(createWrapper(ClassForNameInterceptor.class, "forName")))
        // Class#newInstance
        .transform(
            (builder, typeDescription, classLoader, module) ->
                builder.visit(
                    Advice.to(ClassNewInstance.class) //
                        .on(named("newInstance"))))
        // Class#getDeclaredMethods
        .transform(
            (builder, typeDescription, classLoader, module) ->
                builder.visit(
                    Advice.to(ClassGetDeclaredMethods.class) //
                        .on(named("getDeclaredMethods"))))
        // Class#getDeclaredMethod(String name, Class<?>... parameterTypes)
        .transform(
            (builder, typeDescription, classLoader, module) ->
                builder.visit(createWrapper(ClassGetDeclaredMethod.class, "getDeclaredMethod")))
        // Class#getConstructors()
        .transform(
            (builder, typeDescription, classLoader, module) ->
                builder.visit(
                    Advice.to(ClassGetConstructors.class) //
                        .on(named("getConstructors"))))
        // Class#getMethod(String name, Class<?>... parameterTypes)
        .transform(
            (builder, typeDescription, classLoader, module) ->
                builder.visit(createWrapper(ClassGetMethod.class, "getMethod")))
        // Class#getMethods()
        .transform(
            (builder, typeDescription, classLoader, module) ->
                builder.visit(Advice.to(ClassGetMethods.class).on(named("getMethods"))))
        // Class#getField(String s)
        .transform(
            (builder, typeDescription, classLoader, module) ->
                builder.visit(createWrapper(ClassGetField.class, "getField")))
        // Class#getFields()
        .transform(
            (builder, typeDescription, classLoader, module) ->
                builder.visit(Advice.to(ClassGetFields.class).on(named("getFields"))))
        // Class#getDeclaredFields()
        .transform(
            (builder, typeDescription, classLoader, module) ->
                builder.visit(
                    Advice.to(ClassGetDeclaredFields.class) //
                        .on(named("getDeclaredFields"))))
        // Class#getDeclaredField(String s)
        .transform(
            (builder, typeDescription, classLoader, module) ->
                builder.visit(Advice.to(ClassGetDeclaredField.class).on(named("getDeclaredField"))))
        .installOn(instrumentation);

    // Proxy#newProxyInstance -- Object newProxyInstance(ClassLoader, Class<?>[], InvocationHandler)
    agentBuilder
        .type(is(Proxy.class))
        .transform(
            (builder, typeDescription, classLoader, module) ->
                builder.visit(
                    Advice.to(ProxyNewProxyInstanceInterceptor.class)
                        .on(
                            named("newProxyInstance")
                                .and(ElementMatchers.takesGenericArgument(1, Class[].class)))))
        .installOn(instrumentation);
  }

  static AsmVisitorWrapper createWrapper(Class<?> adviceClass, String methodName) {
    return Advice.to(adviceClass)
        .on(named(methodName).and(ElementMatchers.takesArgument(0, String.class)));
  }

  static class ProxyNewProxyInstanceInterceptor {
    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(1) Class<?>[] interfaces) {
      System.out.println("Proxy#newProxyInstance " + Arrays.toString(interfaces));
    }
  }

  static class ClassForNameInterceptor {
    @Advice.OnMethodEnter
    static void onEnter(@Advice.Argument(0) String className) {
      ClassEvent build = ClassEvent.build(EventType.CLASS_FOR_NAME, className);
      ClassWriter.write(build);
    }
  }

  static class ClassNewInstance {
    @Advice.OnMethodEnter
    static void onEnter(@Advice.This Class<?> thiz) {
      ClassEvent build = ClassEvent.build(EventType.CLASS_NEW_INSTANCE, thiz.getCanonicalName());
      ClassWriter.write(build);
    }
  }

  static class ClassGetDeclaredMethods {
    @Advice.OnMethodExit
    static void onExit(@Advice.This Class<?> thiz) {
      //      List<String> declaredMethods =
      //          Arrays.stream(methods).map(Method::getName).collect(Collectors.toList());
      ClassEvent build =
          ClassEvent.build(EventType.CLASS_GET_DECLARED_METHODS, thiz.getCanonicalName());
      ClassWriter.write(build);
    }
  }

  static class ClassGetField {
    @Advice.OnMethodEnter
    static void onEnter(@Advice.This Class<?> thiz, @Advice.Argument(0) String fieldName) {
      ClassEvent build =
          ClassEvent.buildFieldNames(
              EventType.CLASS_GET_FIELD,
              thiz.getCanonicalName(),
              Collections.singletonList(fieldName));
      ClassWriter.write(build);
    }
  }

  static class ClassGetFields {
    @Advice.OnMethodExit
    static void onExit(@Advice.This Class<?> thiz, @Advice.Return Field[] fields) {
      List<String> fieldNames =
          Arrays.stream(fields).map(Field::getName).collect(Collectors.toList());
      ClassEvent build =
          ClassEvent.buildFieldNames(
              EventType.CLASS_GET_FIELDS, thiz.getCanonicalName(), fieldNames);
      ClassWriter.write(build);
    }
  }

  static class ClassGetDeclaredMethod {
    @Advice.OnMethodEnter
    static void onEnter(@Advice.This Class<?> thiz, @Advice.Argument(0) String methodName) {
      ClassEvent build =
          ClassEvent.buildMethodNames(
              EventType.CLASS_GET_DECLARED_METHOD,
              thiz.getCanonicalName(),
              Collections.singletonList(methodName));
      ClassWriter.write(build);
    }
  }

  static class ClassGetMethod {
    @Advice.OnMethodEnter
    static void intercept(@Advice.This Class<?> thiz, @Advice.Argument(0) String methodName) {
      ClassEvent build =
          ClassEvent.buildMethodNames(
              EventType.CLASS_GET_METHOD,
              thiz.getCanonicalName(),
              Collections.singletonList(methodName));
      ClassWriter.write(build);
    }
  }

  static class ClassGetConstructors {
    @Advice.OnMethodExit
    static void onExit(@Advice.This Class<?> thiz, @Advice.Return Executable[] executables) {
      List<String> methodNames =
          Arrays.stream(executables).map(Executable::getName).collect(Collectors.toList());
      ClassEvent build =
          ClassEvent.buildMethodNames(
              EventType.CLASS_GET_CONSTRUCTORS, thiz.getCanonicalName(), methodNames);
      ClassWriter.write(build);
    }
  }

  static class ClassGetMethods {
    @Advice.OnMethodExit
    static void onExit(@Advice.This Class<?> thiz, @Advice.Return Executable[] methods) {
      List<String> methodNames =
          Arrays.stream(methods).map(Executable::getName).collect(Collectors.toList());
      ClassEvent build =
          ClassEvent.buildMethodNames(
              EventType.CLASS_GET_METHODS, thiz.getCanonicalName(), methodNames);
      ClassWriter.write(build);
    }
  }

  static class ClassGetDeclaredFields {
    @Advice.OnMethodExit
    static void onExit(@Advice.This Class<?> thiz) {
      ClassEvent build =
          ClassEvent.build(EventType.CLASS_GET_DECLARED_FIELDS, thiz.getCanonicalName());
      ClassWriter.write(build);
    }
  }

  static class ClassGetDeclaredField {
    @Advice.OnMethodEnter
    static void onEnter(@Advice.This Class<?> thiz, @Advice.Argument(0) String methodName) {
      ClassEvent build =
          ClassEvent.buildMethodNames(
              EventType.CLASS_GET_DECLARED_FIELD,
              thiz.getCanonicalName(),
              Collections.singletonList(methodName));
      ClassWriter.write(build);
    }
  }
}
