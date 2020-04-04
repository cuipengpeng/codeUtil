package com.caishi.chaoge.http;

import android.util.Log;

import com.caishi.chaoge.base.BaseBean;
import com.caishi.chaoge.utils.LogUtil;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * BaseObserver
 */
public abstract class BaseObserver<T> implements Observer<BaseBean<T>> {

    private static final String TAG = "BaseObserver";

    @Override
    public void onSubscribe(Disposable d) {
        Log.d(TAG, d.toString());
    }

    @Override
    public void onNext(BaseBean<T> value) {
        if (value.isSuccess()) {
            T t = value.getData();
            onRequestSuccess(value.getState(), value.getMsg(), t);
        } else {
            onRequestError(value.getState(), value.getMsg());
        }
    }

    @Override
    public void onError(Throwable e) {
        LogUtil.e(TAG, "onError:" + e.toString());
        try {
            onRequestError(-500, e.toString());
        } catch (Exception e1) {
            LogUtil.e(TAG, "onError:catch====" + e1.toString());
        }

        if (e.toString().contains("java.net.SocketTimeoutException")) {
            onRequestError(-101, e.toString());
        }

    }

    @Override
    public void onComplete() {
        Log.d(TAG, "onComplete");
    }

    protected abstract void onRequestSuccess(int state, String msg, T t);

    protected abstract void onRequestError(int state, String msg);
}
