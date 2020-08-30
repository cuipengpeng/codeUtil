package com.test.xcamera.managers;

import com.test.xcamera.bean.MoShotSetting;
import com.test.xcamera.enumbean.ShootMode;

/**
 * 拍摄模式管理类
 * Created by zll on 2019/5/28.
 */

public class ShotModeManager {
    private static final String TAG = "ShotModeManager";
    private static ShotModeManager singleton = null;
    private static Object lock = new Object();
    private ShootMode mShootMode;

    public static ShotModeManager getInstance() {
        synchronized (lock) {
            if (singleton == null) {
                singleton = new ShotModeManager();
            }
        }
        return singleton;
    }

    public ShotModeManager() {
        mShootMode = ShootMode.VIDEO;
    }

    public ShootMode getmShootMode() {
        return mShootMode;
    }

    public void setmShootMode(ShootMode mShootMode) {
        this.mShootMode = mShootMode;
    }

    public void syncShotMode(MoShotSetting shotSetting) {
        if (shotSetting != null) {
            mShootMode = shotSetting.getmMode();
        }
    }

    public boolean isVideo() {
        return isVideo(mShootMode);
    }

    public boolean isVideo(ShootMode mode) {
        return mode == ShootMode.VIDEO || mode == ShootMode.SLOW_MOTION
                || mode == ShootMode.LAPSE_VIDEO || mode == ShootMode.VIDEO_BEAUTY;
    }

    public boolean isPhoto(ShootMode mode) {
        return mode == ShootMode.PHOTO || mode == ShootMode.LONG_EXPLORE;
    }
}
