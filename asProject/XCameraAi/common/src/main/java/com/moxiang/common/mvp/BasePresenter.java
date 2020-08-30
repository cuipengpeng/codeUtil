package com.moxiang.common.mvp;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;

import java.lang.ref.WeakReference;

/**
 * Created by admin on 2019/10/14.
 */

public abstract class BasePresenter<M extends IModel, V extends IView> implements   LifecycleObserver {
    protected M mModel;
    protected WeakReference<V> mRootView;

    /**
     * 绑定View
     */
    @SuppressWarnings("unchecked")
    public void attachView(V view) {
        mRootView = new WeakReference<>(view);
        if (mModel == null) {
            mModel = createModel();
        }
    }

    /**
     * 解绑View
     */
    public void detachView() {
        if (null != mRootView) {
            mRootView.clear();
            mRootView = null;
        }
        this.mModel = null;
    }

    /**
     * 通过该方法创建Model
     */
    protected abstract M createModel();

    /**
     * 是否与View建立连接
     */
    protected boolean isViewAttached() {
        return null != mRootView && null != mRootView.get();
    }

    protected V getView() {
        return isViewAttached() ? mRootView.get() : null;
    }

    protected M getModel() {
        return mModel;
    }

    protected void showLoading() {
        getView().showLoading();
    }

    protected void dismissLoading() {
        getView().hideLoading();
    }

    public void onStart() {
        if (mRootView != null && mRootView instanceof LifecycleOwner) {
            ((LifecycleOwner) mRootView).getLifecycle().addObserver(this);
            if (mModel != null && mModel instanceof LifecycleObserver) {
                ((LifecycleOwner) mRootView).getLifecycle().addObserver((LifecycleObserver) mModel);
            }
        }
    }

    public void onDestroy() {
        if (mModel != null)
            mModel.onDestroy();
        this.mModel = null;
        this.mRootView = null;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy(LifecycleOwner owner) {
        owner.getLifecycle().removeObserver(this);
    }
}
