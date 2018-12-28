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
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

    // Proxy#newProxyInstance -- Object newProxyInstance(ClassLoader loader, Class<?>[] interfaces,
    // InvocationHandler h)
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
    static void intercept(@Advice.Argument(0) String s) {
      ClassEvent build = ImmutableClassEvent.build(EventType.CLASS_FOR_NAME, s);
      ClassWriter.write(build);
    }
  }

  static class ClassNewInstance {
    @Advice.OnMethodEnter
    static void intercept(@Advice.This Class<?> thiz) {
      ClassEvent build =
          ImmutableClassEvent.build(EventType.CLASS_NEW_INSTANCE, thiz.getCanonicalName());
      ClassWriter.write(build);
    }
  }

  static class ClassGetDeclaredMethods {
    @Advice.OnMethodEnter
    static void intercept(@Advice.This Class<?> thiz) {
      ClassEvent build =
          ImmutableClassEvent.build(EventType.CLASS_GET_DECLARED_METHODS, thiz.getCanonicalName());
      ClassWriter.write(build);
    }
  }

  static class ClassGetField {
    @Advice.OnMethodEnter
    static void intercept(@Advice.This Class<?> thiz, @Advice.Argument(0) String fieldName) {
      ClassEvent build =
          ImmutableClassEvent.buildFieldNames(
              EventType.CLASS_GET_FIELD,
              thiz.getCanonicalName(),
              Collections.singletonList(fieldName));
      ClassWriter.write(build);
    }
  }

  static class ClassGetFields {
    @Advice.OnMethodEnter
    static void intercept(@Advice.This Class<?> thiz) {
      ClassEvent build =
          ImmutableClassEvent.build(EventType.CLASS_GET_FIELDS, thiz.getCanonicalName());
      ClassWriter.write(build);
    }
  }

  static class ClassGetDeclaredMethod {
    @Advice.OnMethodEnter
    static void intercept(@Advice.This Class<?> thiz, @Advice.Argument(0) String methodName) {
      ClassEvent build =
          ImmutableClassEvent.buildMethodNames(
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
          ImmutableClassEvent.buildMethodNames(
              EventType.CLASS_GET_METHOD,
              thiz.getCanonicalName(),
              Collections.singletonList(methodName));
      ClassWriter.write(build);
    }
  }

  static class ClassGetConstructors {
    @Advice.OnMethodEnter
    static void intercept(@Advice.This Class<?> thiz) {
      ClassEvent build =
          ImmutableClassEvent.build(EventType.CLASS_GET_CONSTRUCTORS, thiz.getCanonicalName());
      ClassWriter.write(build);
    }
  }

  static class ClassGetMethods {
    @Advice.OnMethodEnter
    static void intercept(@Advice.This Class<?> thiz) {
      ClassEvent build =
          ImmutableClassEvent.build(EventType.CLASS_GET_METHODS, thiz.getCanonicalName());
      ClassWriter.write(build);
    }
  }

  static class ClassGetDeclaredFields {
    @Advice.OnMethodEnter
    static void intercept(@Advice.This Class<?> thiz) {
      ClassEvent build =
          ImmutableClassEvent.build(EventType.CLASS_GET_DECLARED_FIELDS, thiz.getCanonicalName());
      ClassWriter.write(build);
    }
  }

  static class ClassGetDeclaredField {
    @Advice.OnMethodEnter
    static void intercept(@Advice.This Class<?> thiz, @Advice.Argument(0) String methodName) {
      ClassEvent build =
          ImmutableClassEvent.buildMethodNames(
              EventType.CLASS_GET_DECLARED_FIELD,
              thiz.getCanonicalName(),
              Collections.singletonList(methodName));
      ClassWriter.write(build);
    }
  }
}
