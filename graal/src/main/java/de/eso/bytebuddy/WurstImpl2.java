package de.eso.bytebuddy;

import de.eso.annotation.Bind;
import de.eso.api.ILogger;

final class WurstImpl2 {
  private final ILogger logger;

  public WurstImpl2() {
    logger =
        new ILogger() {
          @Override
          public void debug(String msg) {}

          @Override
          public void info(String msg) {}

          @Override
          public void trace(String msg) {}
        };
  }

  public WurstImpl2(ILogger logger) {
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
