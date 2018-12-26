package de.eso.graalvm;

public class MyServiceImpl implements Service {
  void test(String s) {
    System.out.println("ZUPER " + s);
  }

  @Override
  public void start() {}

  @Override
  public void stop() {}
}
