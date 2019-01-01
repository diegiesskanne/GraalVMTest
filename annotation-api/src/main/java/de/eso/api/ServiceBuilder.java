package de.eso.api;

public interface ServiceBuilder {
  ServiceBuilder serviceImpl(Class<? extends Service> serviceClass);

  /**
   * Register Service as PUBLIC-APi
   *
   * @param publicApi must be an interface
   */
  ServiceBuilder asPublicApi(Class<?> publicApi);

  /**
   * Register Service as PRIVATE-APi
   *
   * @param privateApi must be an interface
   */
  ServiceBuilder asPrivateApi(Class<?> privateApi);

  ServiceDeclaration build();
}
