package com.test.xcamera.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 周 on 2020/4/22.
 */

public class DataChild implements Parcelable {
    private int status;
    private String targetUrl;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.status);
        dest.writeString(this.targetUrl);
    }

    public DataChild() {
    }

    protected DataChild(Parcel in) {
        this.status = in.readInt();
        this.targetUrl = in.readString();
    }

    public static final Parcelable.Creator<DataChild> CREATOR = new Parcelable.Creator<DataChild>() {
        @Override
        public DataChild createFromParcel(Parcel source) {
            return new DataChild(source);
        }

        @Override
        public DataChild[] newArray(int size) {
            return new DataChild[size];
        }
    };
}
