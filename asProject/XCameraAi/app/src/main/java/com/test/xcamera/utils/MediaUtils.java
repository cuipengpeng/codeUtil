package com.test.xcamera.utils;

/**
 * Created by smz on 2019/12/24.
 *
 * 媒体类的工具
 */

public class MediaUtils {

    /**
     * 判断媒体类型是否为视频
     * */
    public static boolean isVideo(String type) {
        return ("video".equals(type)
                || "slowmotionvideo".equals(type)
                || "lapsevideo".equals(type)
                || "tracklapsevideo".equals(type)
                || "videobeauty".equals(type));
    }

    /**
     * 判断媒体类型是否为图片
     * */
    public static boolean isPhoto(String type) {
        return ("photo".equals(type)
                || "longexplorephoto".equals(type)
                || "photobeauty".equals(type));
    }
}
