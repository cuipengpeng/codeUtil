package com.test.xcamera.utils.proxy.animation;

import android.view.View;


import com.test.xcamera.utils.proxy.Perform;

import java.lang.reflect.Proxy;

public class BounceInAnimationFactory {
    public static void proxy(Perform perform, View view) {
        AnimationInvocationHandler handler = new AnimationInvocationHandler(perform, view);
        Perform action = (Perform) Proxy.newProxyInstance(perform.getClass().getClassLoader(),
                perform.getClass().getInterfaces(),
                handler);
        action.perform();
    }
}
