package de.eso.graalvm;

import com.google.common.reflect.AbstractInvocationHandler;

import java.lang.reflect.Method;

final class DynamicInvocationHandlerGuavaImpl extends AbstractInvocationHandler {
    protected Object handleInvocation(Object proxy, Method method, Object[] args) {
        return 42;
    }
}
