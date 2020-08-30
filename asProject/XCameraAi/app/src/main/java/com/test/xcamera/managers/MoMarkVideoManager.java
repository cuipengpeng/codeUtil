package com.test.xcamera.managers;

/**
 * 视频打mark的时间戳
 * Created by zll on 2019/11/20.
 */

public class MoMarkVideoManager {
    private static final String TAG = "MoMarkVideoManager";
    private static MoMarkVideoManager singleton = null;
    private static Object lock = new Object();
    private long mCurrentTime;

    public static MoMarkVideoManager getInstance() {
        synchronized (lock) {
            if (singleton == null) {
                singleton = new MoMarkVideoManager();
            }
        }
        return singleton;
    }

    public long getCurrentTime() {
        return mCurrentTime;
    }

    public void setCurrentTime(long time) {
        mCurrentTime = time;
    }
}
