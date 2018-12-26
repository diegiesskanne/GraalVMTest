package de.eso.bytebuddy;

import net.bytebuddy.implementation.bind.annotation.Argument;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.util.concurrent.Callable;

public final class ClassForNameInterceptor {
  public static Class<?> forName(
      @Origin Class<?> type, @SuperCall Callable<Class<?>> zuper, @Argument(0) String s) {
    System.out.println("INTERCEPTOR " + type.getSimpleName() + s);

    try {
      return zuper.call();
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }
}
