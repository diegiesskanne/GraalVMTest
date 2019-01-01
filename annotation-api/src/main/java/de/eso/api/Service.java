package de.eso.api;

public interface Service {
  default void start() {}

  default void stop() {}
}
