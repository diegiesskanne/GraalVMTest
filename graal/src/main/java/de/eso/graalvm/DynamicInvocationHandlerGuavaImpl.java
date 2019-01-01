package de.eso.graalvm;

import com.google.common.reflect.AbstractInvocationHandler;

import java.lang.reflect.Method;

final class DynamicInvocationHandlerGuavaImpl extends AbstractInvocationHandler {
  protected Object handleInvocation(Object proxy, Method method, Object[] args) {
    if (method.getReturnType() == void.class) {
      System.out.println("[DynamicInvocationHandlerGuavaImpl] void method called");
      return null;
    }
    return 42;
  }
}
