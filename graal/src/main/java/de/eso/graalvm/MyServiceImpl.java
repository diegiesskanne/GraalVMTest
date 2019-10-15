package de.eso.graalvm;

import de.eso.api.Service;
import io.reactivex.Observable;

public final class MyServiceImpl implements MyService, Service {
  public final String publicString = "LMS";

  void test(String s) {
    System.out.println("MyServiceImpl#test " + s);
  }
  
  public void testMethod(int a){
	  int b = a + 1;
  }

  @Override
  public void start() {}

  @Override
  public void stop() {}

  @Override
  public Observable<Long> getTime() {
    return null;
  }
}
