package com.test.xcamera.mointerface;

/**
 * Created by zll on 2019/7/17.
 */

public interface MoRequestCallback {
    void onSuccess();
    default void onFailed(){}
}
