package com.test.xcamera.watermark.utils;

import android.app.Application;
import android.content.Context;

import java.lang.ref.WeakReference;

public class ContextRef {

    public static Context get(){
        return getInstance().getContext();
    }

    private static class SingletonClassInstance {
        private static final ContextRef instance = new ContextRef();
    }

    private ContextRef() {
    }

    public static ContextRef getInstance() {
        return SingletonClassInstance.instance;
    }

    private WeakReference<Context> contextRef;
    private Application application;
    private boolean isApplicationContext;

    public void setContext(Application application, Context ctx) {
        this.application = application;
        clearContext();
        contextRef = new WeakReference<>(ctx);
    }

    public Context getContext() {
        if (contextRef == null) {
            if(application != null){
                isApplicationContext = true;
                return application.getApplicationContext();
            }
            return null;
        }
        return contextRef.get();
    }

    public void clearContext() {
        if (contextRef != null) {
            contextRef.clear();
            contextRef = null;
        }
    }

    public boolean isApplicationContext() {
        return isApplicationContext;
    }
}
