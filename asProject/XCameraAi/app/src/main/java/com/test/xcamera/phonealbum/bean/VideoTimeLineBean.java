package com.test.xcamera.phonealbum.bean;

public class VideoTimeLineBean extends BaseThumbBean{
    private float mTime;
    public float getTime() {
        return mTime;
    }

    public void setTime(float mTime) {
        this.mTime = mTime;
    }
    private boolean emptyView;

    public boolean isEmptyView() {
        return emptyView;
    }

    public void setEmptyView(boolean emptyView) {
        this.emptyView = emptyView;
    }
}
