package de.eso.graalvm;

import de.eso.api.ILogger;

public class LoggerImpl implements ILogger {
  @Override
  public void debug(String msg) {
    System.out.println(msg);
  }

  @Override
  public void info(String msg) {
    System.out.println(msg);
  }

  @Override
  public void trace(String msg) {}
}
