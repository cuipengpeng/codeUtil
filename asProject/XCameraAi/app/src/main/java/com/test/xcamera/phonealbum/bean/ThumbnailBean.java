package com.test.xcamera.phonealbum.bean;

import java.util.ArrayList;
import java.util.List;

public class ThumbnailBean extends BaseThumbBean{
    private List<Object> imgList = new ArrayList<>();
    private List<VideoFrameBean> videoFrameList = new ArrayList<>();
    private float zOrder = -1f;
    private boolean selected = false;
    private boolean emptyView = false;
    private int itemWidth=0;
    private int mType;
    private int mFrameWidth;
    private long mTime;
    private long mTrimInTime=0;
    private String mMediaPath;

    private int clipPosition=0;//视频片段
    private boolean isClipStart=false; //视频片段第一针
    private boolean isClipEnd=false;//视频片段最后一帧

    public long getTrimInTime() {
        return mTrimInTime;
    }

    public void setTrimInTime(long mTrimInTime) {
        this.mTrimInTime = mTrimInTime;
    }

    public int getClipPosition() {
        return clipPosition;
    }

    public void setClipPosition(int clipPosition) {
        this.clipPosition = clipPosition;
    }

    public boolean isClipStart() {
        return isClipStart;
    }

    public void setClipStart(boolean clipStart) {
        isClipStart = clipStart;
    }

    public boolean isClipEnd() {
        return isClipEnd;
    }

    public void setClipEnd(boolean clipEnd) {
        isClipEnd = clipEnd;
    }

    public int getType() {
        return mType;
    }

    public void setType(int mType) {
        this.mType = mType;
    }

    public int getFrameWidth() {
        return mFrameWidth;
    }

    public void setFrameWidth(int mFrameWidth) {
        this.mFrameWidth = mFrameWidth;
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

    public int getItemWidth() {
        return itemWidth;
    }

    public void setItemWidth(int itemWidth) {
        this.itemWidth = itemWidth;
    }

    public List<VideoFrameBean> getVideoFrameList() {
        return videoFrameList;
    }

    public void setVideoFrameList(List<VideoFrameBean> videoFrameList) {
        this.videoFrameList = videoFrameList;
    }

    public boolean isEmptyView() {
        return emptyView;
    }

    public void setEmptyView(boolean emptyView) {
        this.emptyView = emptyView;
    }

    public float getzOrder() {
        return zOrder;
    }

    public void setzOrder(float zOrder) {
        this.zOrder = zOrder;
    }

    public List<Object> getImgList() {
        return imgList;
    }

    public void setImgList(List<Object> imgList) {
        this.imgList = imgList;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public ThumbnailBean clone(){
        ThumbnailBean mediaData = new ThumbnailBean();
        mediaData.setEmptyView(this.emptyView);
        mediaData.setzOrder(this.zOrder);
        mediaData.setImgList(this.imgList);
        mediaData.setTrimInTime(this.mTrimInTime);
        return mediaData;
    }

}
