package com.test.xcamera.mointerface;

/**
 * Created by zll on 2019/10/14.
 */

public interface MoGetActivateStatuCallback {

    /**
     * @param status 0:未激活  1:正在激活中  3:已激活
     * @param activeID 激活ID
     */
    void onSuccess(int status, String activeID);
    void onFailed();
}
