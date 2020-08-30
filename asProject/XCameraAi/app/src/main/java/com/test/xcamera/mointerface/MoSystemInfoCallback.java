package com.test.xcamera.mointerface;

import com.test.xcamera.bean.MoSystemInfo;

/**
 * Created by zll on 2019/9/26.
 */

public interface MoSystemInfoCallback {
    void onSuccess(MoSystemInfo systemInfo);
    void onFailed();
}
