package com.test.xcamera.mointerface;

import com.test.xcamera.bean.MoSyncCameraInfo;

/**
 * Created by zll on 2019/7/4.
 */

public interface MoSyncCameraInfoCallback {
    void onSuccess(MoSyncCameraInfo info);
    void onFailed();
}

