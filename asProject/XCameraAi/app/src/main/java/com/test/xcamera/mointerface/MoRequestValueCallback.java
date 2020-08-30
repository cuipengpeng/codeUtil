package com.test.xcamera.mointerface;

/**
 * Created by zll on 2019/7/17.
 */

public interface MoRequestValueCallback {
    void onSuccess();
    default void onFailed(int errCode){}
}
