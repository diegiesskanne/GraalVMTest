package de.eso.bytebuddy;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * JVM-Agent for intercepting calls to Class and Proxy in order to log them out via
 * ByteCode-Manipulation
 */
public final class BootstrapAgent {

  public static void premain(String arg, Instrumentation instrumentation) throws Exception {
    File tempFolder = Files.createTempDirectory("tmp").toFile();

    // Writer.write(Writer.Type.CLASS_FOR_NAME, "FUG");

    AgentBuilder agentBuilder =
        new AgentBuilder.Default() //
            .disableClassFormatChanges() //
            .ignore(none())
            .with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE)
            .with(AgentBuilder.RedefinitionStrategy.REDEFINITION)
            .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
            .enableBootstrapInjection(instrumentation, tempFolder);

    // Load Writer into Bootstrap-Classloader, in order to access it from @Advice
    Map<TypeDescription.ForLoadedType, byte[]> forLoadedType = new HashMap<>();
    forLoadedType.putIfAbsent(
        new TypeDescription.ForLoadedType(Writer.class),
        ClassFileLocator.ForClassLoader.read(Writer.class));
    forLoadedType.putIfAbsent(
        new TypeDescription.ForLoadedType(Writer.Type.class),
        ClassFileLocator.ForClassLoader.read(Writer.Type.class));
    ClassInjector.UsingInstrumentation.of(
            tempFolder, ClassInjector.UsingInstrumentation.Target.BOOTSTRAP, instrumentation)
        .inject(forLoadedType);

    agentBuilder
        .type(is(Class.class))
        // Class#forName
        .transform(
            (builder, typeDescription, classLoader, module) ->
                builder.visit(
                    Advice.to(ClassForNameInterceptor.class) //
                        .on(named("forName").and(ElementMatchers.takesArgument(0, String.class)))))
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
                builder.visit(
                    Advice.to(ClassGetDeclaredMethod.class)
                        .on(
                            named("getDeclaredMethod")
                                .and(ElementMatchers.takesArgument(0, String.class)))))
        // Class#getConstructors()
        .transform(
            (builder, typeDescription, classLoader, module) ->
                builder.visit(
                    Advice.to(ClassGetConstructors.class) //
                        .on(named("getConstructors"))))
        // Class#getMethod(String name, Class<?>... parameterTypes)
        .transform(
            (builder, typeDescription, classLoader, module) ->
                builder.visit(
                    Advice.to(ClassGetMethod.class) //
                        .on(
                            named("getMethod")
                                .and(ElementMatchers.takesArgument(0, String.class)))))
        // Class#getMethods()
        .transform(
            (builder, typeDescription, classLoader, module) ->
                builder.visit(Advice.to(ClassGetMethods.class).on(named("getMethods"))))
        // Class#getField(String s)
        .transform(
            (builder, typeDescription, classLoader, module) ->
                builder.visit(
                    Advice.to(ClassGetField.class)
                        .on(
                            named("getField")
                                .and(
                                    ElementMatchers.takesArgument(
                                        0, ElementMatchers.is(String.class))))))
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
                builder.visit(
                    Advice.to(ClassGetDeclaredField.class)
                        .on(
                            named("getDeclaredField")
                                .and(
                                    ElementMatchers.takesArgument(
                                        0, ElementMatchers.is(String.class))))))
        .installOn(instrumentation);

    // Proxy#newProxyInstance
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

  /**
   * public static Object newProxyInstance(ClassLoader loader, Class<?>[] interfaces,
   * InvocationHandler h)
   */
  static class ProxyNewProxyInstanceInterceptor {
    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(1) Class<?>[] interfaces) {
      System.out.println("Proxy#newProxyInstance " + Arrays.toString(interfaces));
    }
  }

  static class ClassForNameInterceptor {
    @Advice.OnMethodEnter
    static void intercept(@Advice.Argument(0) String s) {
      // https://github.com/raphw/byte-buddy/issues/143
      // currently I do not know how to use outside variables. Any use from outside the static
      // method will result in an Error
      // System.out.println("Class#forName " + s);
      Writer.write(Writer.Type.CLASS_FOR_NAME, s);

      // Writer.writeOut("X");
    }
  }

  static class ClassNewInstance {
    @Advice.OnMethodEnter
    static void intercept(@Advice.This Class<?> thiz) {
      System.out.println("Class#newInstance " + thiz.getCanonicalName());
    }
  }

  static class ClassGetDeclaredMethods {
    @Advice.OnMethodEnter
    static void intercept(@Advice.This Class<?> thiz) {
      System.out.println("Class#getDeclaredMethods " + thiz.getCanonicalName());
    }
  }

  static class ClassGetField {
    @Advice.OnMethodEnter
    static void intercept(@Advice.This Class<?> thiz, @Advice.Argument(0) String fieldName) {
      System.out.println("Class#getField " + thiz.getCanonicalName() + "#" + fieldName);
    }
  }

  static class ClassGetFields {
    @Advice.OnMethodEnter
    static void intercept(@Advice.This Class<?> thiz) {
      System.out.println("Class#getFields " + thiz.getCanonicalName());
    }
  }

  static class ClassGetDeclaredMethod {
    @Advice.OnMethodEnter
    static void intercept(@Advice.This Class<?> thiz, @Advice.Argument(0) String methodName) {
      System.out.println("Class#getDeclaredMethod " + thiz.getCanonicalName() + "#" + methodName);
    }
  }

  static class ClassGetMethod {
    @Advice.OnMethodEnter
    static void intercept(@Advice.This Class<?> thiz, @Advice.Argument(0) String methodName) {
      System.out.println("Class#getMethod " + thiz.getCanonicalName() + "#" + methodName);
    }
  }

  static class ClassGetConstructors {
    @Advice.OnMethodEnter
    static void intercept(@Advice.This Class<?> thiz) {
      System.out.println("Class#getConstructors " + thiz.getCanonicalName());
    }
  }

  static class ClassGetMethods {
    @Advice.OnMethodEnter
    static void intercept(@Advice.This Class<?> thiz) {
      System.out.println("Class#getMethods " + thiz.getCanonicalName());
    }
  }

  static class ClassGetDeclaredFields {
    @Advice.OnMethodEnter
    static void intercept(@Advice.This Class<?> thiz) {
      System.out.println("Class#getDeclaredFields " + thiz.getCanonicalName());
    }
  }

  static class ClassGetDeclaredField {
    @Advice.OnMethodEnter
    static void intercept(@Advice.This Class<?> thiz, @Advice.Argument(0) String methodName) {
      System.out.println("Class#getDeclaredField " + thiz.getCanonicalName() + "#" + methodName);
    }
  }
}
