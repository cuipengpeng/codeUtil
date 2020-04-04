package com.caishi.chaoge.http;

import android.util.Log;

import com.caishi.chaoge.utils.LogUtil;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * BaseObserver
 */
public abstract class DownloadObserver<T> implements Observer<ResponseBody> {

    private static final String TAG = "BaseObserver";

    @Override
    public void onSubscribe(Disposable d) {
        Log.d(TAG, d.toString());
    }

    @Override
    public void onNext(ResponseBody value) {
        if (null != value) {
            onRequestSuccess(value);
        } else {
            onRequestError(100,"ResponseBody is null");
        }
    }

    @Override
    public void onError(Throwable e) {
        LogUtil.e(TAG, "onError:" + e.toString());
        if (e.toString().contains("java.net.SocketTimeoutException")) {
            onRequestError(-101, e.toString());
        }

    }

    @Override
    public void onComplete() {
        Log.d(TAG, "onComplete");
    }

    protected abstract void onRequestSuccess(ResponseBody responseBody);

    protected abstract void onRequestError(int code,String msg);
}
