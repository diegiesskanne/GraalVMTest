package de.eso.de.eso.bytebuddy;

import de.eso.de.eso.annotation.Bind;
import net.bytebuddy.build.Plugin;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;

import java.io.IOException;

import static net.bytebuddy.matcher.ElementMatchers.anyOf;
import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;

public final class HookInstallingPlugin implements Plugin {
  @Override
  public boolean matches(TypeDescription target) {
    return target.getName().endsWith("Test");
  }

  @Override
  public DynamicType.Builder<?> apply(
      DynamicType.Builder<?> builder,
      TypeDescription typeDescription,
      ClassFileLocator classFileLocator) {
    return builder
        .method(isAnnotatedWith(anyOf(Bind.class)))
        .intercept(MethodDelegation.to(SampleInterceptor.class));
  }

  @Override
  public void close() throws IOException {}
}
