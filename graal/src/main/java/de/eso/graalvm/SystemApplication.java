package de.eso.graalvm;

import de.eso.api.Application;
import de.eso.api.ManifestBuilder;

public final class SystemApplication implements Application {
  @Override
  public void configure(ManifestBuilder manifest) {
    manifest //
        .append(null)
        .append(null);
  }
}
