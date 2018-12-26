package de.eso.guice;

import de.eso.bytebuddy.WurstImpl;

public final class ModuleImpl extends AbstractModule {
  @Override
  void configure() {
    bind(WurstImpl.class);
  }
}
