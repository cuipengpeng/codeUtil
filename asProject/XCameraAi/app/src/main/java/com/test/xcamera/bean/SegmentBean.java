package com.test.xcamera.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by smz on 2019/11/7.
 */

public class SegmentBean implements Parcelable {

    private String fileName;
    private long    mVideoDuration;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getmVideoDuration() {
        return mVideoDuration;
    }

    public void setmVideoDuration(long mVideoDuration) {
        this.mVideoDuration = mVideoDuration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fileName);
        dest.writeLong(this.mVideoDuration);
    }

    public SegmentBean() {
    }

    protected SegmentBean(Parcel in) {
        this.fileName = in.readString();
        this.mVideoDuration = in.readLong();
    }

    public static final Parcelable.Creator<SegmentBean> CREATOR = new Parcelable.Creator<SegmentBean>() {
        @Override
        public SegmentBean createFromParcel(Parcel source) {
            return new SegmentBean(source);
        }

        @Override
        public SegmentBean[] newArray(int size) {
            return new SegmentBean[size];
        }
    };
}
