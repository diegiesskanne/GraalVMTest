package de.eso.graalvm;

import com.google.common.reflect.AbstractInvocationHandler;
import io.reactivex.Observable;

import java.lang.reflect.Method;

final class DynamicInvocationHandlerGuavaObs extends AbstractInvocationHandler {
  @Override
  protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
      return Observable.just(42L).mergeWith(Observable.never());
  }
}
