package de.eso.graalvm;

import de.eso.api.Service;
import de.eso.api.ServiceBuilder;
import de.eso.api.ServiceDeclaration;

import java.util.List;

public class ServiceBuilderImpl implements ServiceBuilder {
  private ServiceBuilderImpl() {}

  static ServiceBuilder create() {
    return new ServiceBuilderImpl();
  }

  @Override
  public ServiceBuilder serviceImpl(Class<? extends Service> serviceClass) {
    return this;
  }

  @Override
  public ServiceBuilder asPublicApi(Class<?> publicApi) {
    return this;
  }

  @Override
  public ServiceBuilder asPrivateApi(Class<?> privateApi) {
    return this;
  }

  @Override
  public ServiceDeclaration build() {
    return new ServiceDeclaration() {
      @Override
      public Class<? extends Service> serviceImpl() {
        return null;
      }

      @Override
      public List<Class<?>> publicApis() {
        return null;
      }

      @Override
      public List<Class<?>> privateApis() {
        return null;
      }
    };
  }
}
