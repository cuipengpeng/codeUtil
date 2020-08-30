package com.test.xcamera.phonealbum.bean;

public class VideoFrameBean {
    private int mType;
    private int mFrameWidth;
    private long mTime;
    private String mMediaPath;

    public int getFrameWidth() {
        return mFrameWidth;
    }

    public void setFrameWidth(int mFrameWidth) {
        this.mFrameWidth = mFrameWidth;
    }

    public int getType() {
        return mType;
    }

    public void setType(int mType) {
        this.mType = mType;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long mTime) {
        this.mTime = mTime;
    }

    public String getMediaPath() {
        return mMediaPath;
    }

    public void setMediaPath(String mMediaPath) {
        this.mMediaPath = mMediaPath;
    }
}
