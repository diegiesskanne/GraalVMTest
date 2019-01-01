package de.eso.api;

import java.util.List;

public interface ServiceDeclaration {
  Class<? extends Service> serviceImpl();

  List<Class<?>> publicApis();

  List<Class<?>> privateApis();
}
