package com.test.xcamera.phonealbum.bean;

import java.io.Serializable;

public class VideoParamMusicData implements Serializable {
    /*音乐图片地址*/
    private String imagePath;
    /*音频文件地址*/
    private String filePath;
    /*音乐名称*/
    private String name;
    /*音乐时长 秒*/
    private float duration;
    /*引入视频入点秒*/
    private float inPoint;
    /*引入视频出点秒*/
    private float outPoint;
    private float trimIn;

    private float trimOut;
    /*引入视频出点*/
    private float volume;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public float getInPoint() {
        return inPoint;
    }

    public void setInPoint(float inPoint) {
        this.inPoint = inPoint;
    }

    public float getOutPoint() {
        return outPoint;
    }

    public void setOutPoint(float outPoint) {
        this.outPoint = outPoint;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public float getTrimIn() {
        return trimIn;
    }

    public void setTrimIn(float trimIn) {
        this.trimIn = trimIn;
    }

    public float getTrimOut() {
        return trimOut;
    }

    public void setTrimOut(float trimOut) {
        this.trimOut = trimOut;
    }
}
