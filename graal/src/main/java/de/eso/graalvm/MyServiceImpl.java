package de.eso.graalvm;

public class MyServiceImpl implements Service {
  public final String publicString = "LMS";

  void test(String s) {
    System.out.println("MyServiceImpl#test " + s);
  }

  @Override
  public void start() {}

  @Override
  public void stop() {}
}
