package com.test.xcamera.api;
/**
 * creat by mz  2019.9.24
 */
public interface CallBack<T> {
    void onSuccess(T t);
    void onFailure(Throwable e);
    void onCompleted();
}
