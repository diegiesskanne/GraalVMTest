package de.eso.graalvm;

public interface Service {
  default void start() {}

  default void stop() {}
}
