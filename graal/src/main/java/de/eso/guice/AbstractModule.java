package de.eso.guice;

public abstract class AbstractModule {
  void bind(Class<?> clazz) {}

  abstract void configure();
}
