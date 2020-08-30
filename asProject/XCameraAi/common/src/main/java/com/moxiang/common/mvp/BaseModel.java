package com.moxiang.common.mvp;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;

import com.moxiang.common.base.IRepositoryManager;
import com.moxiang.common.base.RepositoryManager;

/**
 * Created by admin on 2019/10/14.
 */

public class BaseModel implements IModel,LifecycleObserver {
    protected IRepositoryManager mRepositoryManager;

    public BaseModel() {
        mRepositoryManager = new RepositoryManager();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy(LifecycleOwner owner) {
        owner.getLifecycle().removeObserver(this);
    }

    @Override
    public void onDestroy() {

    }
}
