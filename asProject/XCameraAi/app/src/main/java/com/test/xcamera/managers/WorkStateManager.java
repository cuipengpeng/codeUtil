package com.test.xcamera.managers;

import com.test.xcamera.enumbean.WorkState;
import com.test.xcamera.viewcontrol.MoFPVRsolutionControl;
import com.test.xcamera.viewcontrol.MoFPVScaleControl;
import com.test.xcamera.viewcontrol.MoFPVSlowMotionControl;

/**
 * 工作状态管理
 * Created by zll on 2019/5/29.
 */

public class WorkStateManager {
    private static final String TAG = "WorkStateManager";
    private static WorkStateManager singleton = null;
    private static Object lock = new Object();
    private volatile WorkState mWorkState;
    private boolean mIsSelfie = false;

    /**
     * 判断是否正在切换模式
     * 切换模式有时间差 不做处理造成实际模式与UI不符
     */
    public boolean switchMode = false;
    public long switchTime = 0;
    public volatile long preSwitchTime;

    public void setSwitchMode(boolean switchMode) {
        this.switchMode = switchMode;
        switchTime = System.currentTimeMillis();
    }

    /**
     * switchMode为true时 如果超过1秒 则返回false
     * 防止为true时 无法操作界面
     */
    public boolean enableSwitch() {
        return (System.currentTimeMillis() - switchTime < 1000) && switchMode;
    }

    /**
     * 防止切换模式和改参数同时进行
     * */
    public boolean enableSetting() {
        return enableSwitch() || (System.currentTimeMillis() - preSwitchTime < 500);
    }

    /**
     * 是否正在修改相机参数
     * */
    public boolean isSetting() {
        return MoFPVRsolutionControl.getInstance().isSwiching()
                || MoFPVSlowMotionControl.getInstance().isSlowSetting()
                || MoFPVScaleControl.getInstance().isScaleFlag();
    }

    private boolean IsTrack = false; //开启时候跟踪

    public boolean isTrack() {
        return IsTrack;
    }

    public void setTrack(boolean track) {
        IsTrack = track;
    }

    public static WorkStateManager getInstance() {
        synchronized (lock) {
            if (singleton == null) {
                singleton = new WorkStateManager();
            }
        }
        return singleton;
    }

    public WorkStateManager() {
        mWorkState = WorkState.STANDBY;
    }

    public WorkState getmWorkState() {
        return mWorkState;
    }

    public void setmWorkState(WorkState mWorkState) {
        this.mWorkState = mWorkState;
    }

    public boolean ismIsSelfie() {
        return mIsSelfie;
    }

    public void setmIsSelfie(boolean mIsSelfie) {
        this.mIsSelfie = mIsSelfie;
    }
}
