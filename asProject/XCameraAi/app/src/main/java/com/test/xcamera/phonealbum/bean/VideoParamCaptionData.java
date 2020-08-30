package com.test.xcamera.phonealbum.bean;

public class VideoParamCaptionData {
    /** 文字 */
    private String mText;
    /*字幕开始*/
    protected float mStart;
    /*字幕结束*/
    protected float mEnd;
    /*字幕在视频上坐标x*/
    protected float position_x;
    /*字幕在视频坐标Y*/
    protected float position_y;
    public String getText() {
        return mText;
    }

    public void setText(String mText) {
        this.mText = mText;
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
