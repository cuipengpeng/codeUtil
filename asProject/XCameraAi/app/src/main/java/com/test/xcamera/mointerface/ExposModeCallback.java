package com.test.xcamera.mointerface;

/**
 * Created by zll on 2019/7/17.
 *
 * 设置曝光模式回调
 */

public interface ExposModeCallback {
    void onSuccess(int iso, int ev, int shut);
    default void onFailed(){}
}
