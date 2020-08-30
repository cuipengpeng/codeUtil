package com.test.xcamera.phonealbum.bean;

import java.io.Serializable;

/**
 * 贴纸
 */
public class VideoParamStickerData  implements Serializable {
    /** 贴纸 */
    private String mPackagePath;
    /*贴纸开始*/
    protected float mStart;
    /*贴纸结束*/
    protected float mEnd;
    /*贴纸在视频上坐标x*/
    protected float position_x;
    /*贴纸在视频坐标Y*/
    protected float position_y;

    public String getPackagePath() {
        return mPackagePath;
    }

    public void setPackagePath(String mPackagePath) {
        this.mPackagePath = mPackagePath;
    }

    public float getStart() {
        return mStart;
    }

    public void setStart(float mStart) {
        this.mStart = mStart;
    }

    public float getEnd() {
        return mEnd;
    }

    public void setEnd(float mEnd) {
        this.mEnd = mEnd;
    }

    public float getPosition_x() {
        return position_x;
    }

    public void setPosition_x(float position_x) {
        this.position_x = position_x;
    }

    public float getPosition_y() {
        return position_y;
    }

    public void setPosition_y(float position_y) {
        this.position_y = position_y;
    }
}
