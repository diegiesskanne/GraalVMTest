package de.eso.bytebuddy;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.util.Arrays;

import static net.bytebuddy.matcher.ElementMatchers.*;

public final class BootstrapAgent {

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

    agentBuilder
        .type(is(Class.class))
        .transform(
            (builder, typeDescription, classLoader, module) ->
                builder.visit(Advice.to(ClassForNameInterceptor.class).on(named("forName"))))
        .installOn(instrumentation);

    agentBuilder
        .type(is(Proxy.class))
        .transform(
            (builder, typeDescription, classLoader, module) ->
                builder.visit(
                    Advice.to(ProxyNewProxyInstanceInterceptor.class)
                        .on(named("newProxyInstance"))))
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

      System.out.println("Class#forName " + s);
    }
  }
}
