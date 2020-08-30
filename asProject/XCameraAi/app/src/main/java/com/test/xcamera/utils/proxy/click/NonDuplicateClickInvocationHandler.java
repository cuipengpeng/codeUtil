package com.test.xcamera.utils.proxy.click;


import com.test.xcamera.utils.proxy.NonDuplicateClickUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


public class NonDuplicateClickInvocationHandler implements InvocationHandler {

    private Object mProxy;

    public NonDuplicateClickInvocationHandler(Object proxy) {
        mProxy = proxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (NonDuplicateClickUtils.isDuplicateClick()) {
            return null;
        }
        return method.invoke(mProxy, args);
    }
}
