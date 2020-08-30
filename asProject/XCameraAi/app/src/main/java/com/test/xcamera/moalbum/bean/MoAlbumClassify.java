package com.test.xcamera.moalbum.bean;

import java.io.Serializable;

/**
 * Created by zll on 2019/11/21.
 */

public class MoAlbumClassify implements Serializable{
    // 当前相册的类型 0:APP相册  1:相机相册  2:手机其他相册
    private int mType;
    private String mName;
    private String mCount;
    private String mThumbnailUri;

    public int getmType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmCount() {
        return mCount;
    }

    public void setmCount(String mCount) {
        this.mCount = mCount;
    }

    public String getmThumbnailUri() {
        return mThumbnailUri;
    }

    public void setmThumbnailUri(String mThumbnailUri) {
        this.mThumbnailUri = mThumbnailUri;
    }

    public boolean isCameraAlbum() {
        return mType == 1;
    }

    @Override
    public String toString() {
        return "MoAlbumClassify{" +
                "mType='" + mType + '\'' +
                "mName='" + mName + '\'' +
                ", mCount='" + mCount + '\'' +
                ", mThumbnailUri='" + mThumbnailUri + '\'' +
                '}';
    }
}
