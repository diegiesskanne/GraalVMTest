package de.eso.de.eso.bytebuddy;

import net.bytebuddy.implementation.bind.annotation.Origin;

final class SampleInterceptor {
  public static String intercept(@Origin Class<?> type) {
    return type.getSimpleName();
  }
}
