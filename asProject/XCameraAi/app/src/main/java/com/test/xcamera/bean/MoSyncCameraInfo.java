package com.test.xcamera.bean;

/**
 * 同步相机模式

 * Created by zll on 2019/9/26.
 */

public class MoSyncCameraInfo extends MoDataEx {
    public int mode;
    public int progress;
    public long progress_time;
    public int expectation_time;

    @Override
    public String toString() {
        return "MoSyncCameraInfo{" +
                "mode=" + mode +
                ", progress=" + progress +
                ", progress_time=" + progress_time +
                ", expectation_time=" + expectation_time +
                '}';
    }
}
