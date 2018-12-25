package de.eso.bytebuddy;

import net.bytebuddy.implementation.bind.annotation.Argument;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

public final class TestInterceptor {
  @RuntimeType
  public static String interceptor(@Argument(0) Class<?> clazz) throws Exception {

    System.out.println("wuuurst");

    return "";
  }
}
