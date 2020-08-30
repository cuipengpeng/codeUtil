package com.test.xcamera.mointerface;

/**
 * Created by zll on 2019/11/11.
 */

public interface MoStartTakeVideoCallback {
    int SD_NONSUPPORT = 0;
    int SD_LOW = 1;

    void onSuccess(String uri, int reason);
    void onFailed(int reason);
}
