package de.eso.bytebuddy;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.util.Collections;

public final class BootstrapAgent {
  public static void premain(String arg, Instrumentation inst) throws Exception {
    File temp = Files.createTempDirectory("tmp").toFile();
    ClassInjector.UsingInstrumentation.of(
            temp, ClassInjector.UsingInstrumentation.Target.BOOTSTRAP, inst)
        .inject(
            Collections.singletonMap(
                new TypeDescription.ForLoadedType(ClassForNameInterceptor.class),
                ClassFileLocator.ForClassLoader.read(ClassForNameInterceptor.class)));
    new AgentBuilder.Default()
        .ignore(ElementMatchers.none())
        .enableBootstrapInjection(inst, temp)
        .type(ElementMatchers.nameContainsIgnoreCase("Class"))
        .transform(
            new AgentBuilder.Transformer() {
              @Override
              public DynamicType.Builder<?> transform(
                  DynamicType.Builder<?> builder,
                  TypeDescription typeDescription,
                  ClassLoader classLoader,
                  JavaModule module) {

                System.out.println("TRANSFORM " + typeDescription.getCanonicalName());

                // Class#forName :: Class<?> forName(String className)
                return builder
                    .method(
                        ElementMatchers.named("forName") //
                            .and(ElementMatchers.returns(Class.class)))
                    // .and(ElementMatchers.isDeclaredBy(ElementMatchers.named("Class")))
                    .intercept(MethodDelegation.to(ClassForNameInterceptor.class));
              }
            })
        .installOn(inst);
  }
}
