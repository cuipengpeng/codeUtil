package com.test.xcamera.activity;

import com.test.xcamera.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 相机模式
 * 1.photo:拍照 2.delayphoto:延时拍照 3.longexplorephoto:长曝光
 * 4.video:视频  5.slowmotionvideo:慢动作  6.lapsevideo:缩时摄影
 */
public enum CameraMode {
    PHOTO(1, "photo", R.string.fpv_mode_photo, null),
    LONG_EXPLORE(2, "longexplorephoto", R.string.fpv_mode_longexplore, null),
    VIDEO(3, "video", R.string.fpv_mode_video, null),
    SLOW_MOTION(4, "slowmotionvideo", R.string.fpv_mode_slowmotion, null),
    LAPSE(6, "lapsevideo", R.string.fpv_mode_lapse, null),
    BEAUTY_PHOTO(7, "photobeauty", R.string.fpv_mode_beauty, null),
    BEAUTY_VIDEO(8, "videobeauty", R.string.fpv_mode_beauty, null);

    public static List<CameraMode> mCameraMode = new ArrayList<>(
            Arrays.asList(BEAUTY_PHOTO, LAPSE, SLOW_MOTION, VIDEO, PHOTO, LONG_EXPLORE));

    //与固件对接的指令
    public String cmd;
    //显示用的
    public int text;
    public int mode;
    public Object extra;

    CameraMode(int mode, String cmd, int text, Object extra) {
        this.mode = mode;
        this.cmd = cmd;
        this.text = text;
        this.extra = extra;
    }

    /**
     * 判断是否是视频
     *
     * @return true 为视频
     */
    public static boolean isVideo(CameraMode mode) {
        return mode == VIDEO || mode == SLOW_MOTION || mode == LAPSE || mode == BEAUTY_VIDEO;
    }

    /**
     * 根据指令名 获取模式
     * <p>
     * 1.photo:拍照 2.delayphoto:延时拍照 3.longexplorephoto:长曝光
     * 4.video:视频  5.slowmotionvideo:慢动作  6.lapsevideo:缩时摄影
     *
     * @return 没找到 返回null
     */
    public static CameraMode getMode(int mode) {
        for (int i = 0; i < values().length; i++)
            if (values()[i].mode == mode)
                return values()[i];

        return null;
    }
}
