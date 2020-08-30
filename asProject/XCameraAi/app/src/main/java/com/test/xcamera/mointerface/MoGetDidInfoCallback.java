package com.test.xcamera.mointerface;

/**
 * Created by zll on 2019/10/14.
 */

public interface MoGetDidInfoCallback {
    void onSuccess(String did, String nonce);
    void onFailed();
}
