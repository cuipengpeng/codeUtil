package com.test.xcamera.mointerface;

import com.test.xcamera.bean.MoSettingModel;

/**
 * Created by zll on 2019/9/26.
 */

public interface MoSyncSettingCallback {
    void onSuccess(MoSettingModel model);
    default void onFailed() {}
}
