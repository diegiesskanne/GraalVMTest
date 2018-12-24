package de.eso.graalvm;

public interface ProxyHandle<T> {
  T unwrap();
}
