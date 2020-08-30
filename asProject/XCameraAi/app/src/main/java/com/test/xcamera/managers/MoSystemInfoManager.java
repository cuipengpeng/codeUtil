package com.test.xcamera.managers;

import android.content.Intent;

import com.test.xcamera.bean.MoSystemInfo;
import com.test.xcamera.mointerface.MoSystemInfoCallback;
import com.test.xcamera.utils.BroadcastUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zll on 2019/10/10.
 */

public class MoSystemInfoManager {
    private static final String TAG = "MoSystemInfoManager";
    private static MoSystemInfoManager singleton = null;
    private static Object lock = new Object();

    private int mBattery;
    private int mPhotoCount;
    private int mVideoTime;
    private long mSDCard;

    private Timer mTimer;

    public static MoSystemInfoManager getInstance() {
        synchronized (lock) {
            if (singleton == null) {
                singleton = new MoSystemInfoManager();
            }
        }
        return singleton;
    }

    public void startTimer() {
//        if (mTimer == null) {
//            mTimer = new Timer();
//            mTimer.schedule(timerTask, 0, 30 * 1000);
//        }
    }

    public void release() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    public int getmBattery() {
        return mBattery;
    }

    public int getmPhotoCount() {
        return mPhotoCount;
    }

    public int getmVideoTime() {
        return mVideoTime;
    }

    public long getmSDCard() {
        return mSDCard;
    }

    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            getSystemInfo();
        }
    };

    public void getSystemInfo() {
        ConnectionManager.getInstance().getSystemInfo(new MoSystemInfoCallback() {
            @Override
            public void onSuccess(MoSystemInfo systemInfo) {
                if (systemInfo != null) {
                    setValue(systemInfo);
                }
            }

            @Override
            public void onFailed() {

            }
        });
    }

    private void setValue(MoSystemInfo systemInfo) {
        mBattery = systemInfo.getmBattery();
        mPhotoCount = systemInfo.getmImageCount();
        mVideoTime = systemInfo.getmVideoCount();
        mSDCard = systemInfo.getmSDCardCapacity();

        Intent intent = new Intent();
        intent.putExtra("systemInfo", systemInfo);
        BroadcastUtils.sendBroadcast(BroadcastUtils.GET_SYSTEM_INFO, intent);
    }
}
