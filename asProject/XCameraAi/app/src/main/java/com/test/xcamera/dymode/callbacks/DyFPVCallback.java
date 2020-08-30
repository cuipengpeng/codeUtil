package com.test.xcamera.dymode.callbacks;

import com.test.xcamera.bean.MoShotSetting;

/**
 * Created by zll on 2020/1/17.
 */

public interface DyFPVCallback {
    void finishActivity();
    void deleteLastFrag();
    void record(boolean isRecord);
    void startTrack();
    void stopTrack();
    void syncShotSettings(MoShotSetting shotSetting);
    void setModelSuccess(int mode);
}
