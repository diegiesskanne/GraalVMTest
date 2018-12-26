package de.eso.bytebuddy;

import de.eso.annotation.Bind;
import de.eso.api.ILogger;

public final class WurstImpl {
  private final ILogger logger;

  public WurstImpl(ILogger logger) {
    this.logger = logger;
  }

  @Bind
  public String wurst() {
    logger.debug("[TEST]");

    return "FML";
  }

  public String wurst2() {
    logger.info("[TEST][INFO]");

    return "FML2";
  }
}
