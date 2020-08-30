package com.test.xcamera.mointerface;

import com.test.xcamera.bean.MoShotSetting;

/**
 * Created by zll on 2019/9/26.
 */

public interface MoSynvShotSettingCallback {
    void onSuccess(MoShotSetting shotSetting);
    void onFailed();
}
