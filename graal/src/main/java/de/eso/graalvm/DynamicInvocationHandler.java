package de.eso.graalvm;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

final class DynamicInvocationHandler implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        return 42;
    }
}