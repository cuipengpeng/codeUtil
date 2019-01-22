package com.downloader;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Handler的封装
 * 子类应为静态内部类
 *
 * @param <T> 外部类
 */
public abstract class BaseHandler<T> extends Handler {
    private final WeakReference<T> mReference;

    public BaseHandler(T t) {
        super(Looper.getMainLooper());
        mReference = new WeakReference<T>(t);
    }

    @Override
    public void handleMessage(Message msg) {
        T t = mReference.get();
        if (t != null) {
            handleMessage(t, msg);
        }
    }

    protected abstract void handleMessage(T t, Message msg);
}

