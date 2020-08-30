package com.test.xcamera.mointerface;

/**
 * Created by zll on 2019/7/17.
 *
 * 设置白平衡模式回调
 */

public interface AwbModeCallback {
    void onSuccess(int awb);
    default void onFailed(){}
}
