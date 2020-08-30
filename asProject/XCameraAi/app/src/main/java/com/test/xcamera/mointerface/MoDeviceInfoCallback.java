package com.test.xcamera.mointerface;

import com.test.xcamera.bean.MoDeviceInfo;

/**
 * Created by zll on 2019/9/26.
 */

public interface MoDeviceInfoCallback {
    void onSuccess(MoDeviceInfo deviceInfo);
    void onFailed();
}
