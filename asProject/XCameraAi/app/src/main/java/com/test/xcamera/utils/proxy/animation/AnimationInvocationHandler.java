package com.test.xcamera.utils.proxy.animation;

import android.view.View;


import com.test.xcamera.utils.proxy.NonDuplicateClickUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class AnimationInvocationHandler implements InvocationHandler {

    private Object mProxy;
    private View mView;
    private Object mResult;

    public AnimationInvocationHandler(Object proxy, View view) {
        mProxy = proxy;
        mView = view;
    }

    @Override
    public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
        if (NonDuplicateClickUtils.isDuplicateClick()) {
            return null;
        }
        BounceInAnimation.bounceIn(mView, new BounceInAnimation.OnAnimationEndListener() {
            @Override
            public void onAnimationEnd() {
                try {
                    mResult = method.invoke(mProxy, args);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
        return mResult;
    }
}
