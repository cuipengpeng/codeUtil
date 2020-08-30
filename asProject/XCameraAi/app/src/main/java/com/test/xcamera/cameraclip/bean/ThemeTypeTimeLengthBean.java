package com.test.xcamera.cameraclip.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class ThemeTypeTimeLengthBean implements Parcelable {

    private VideoSegment.ThemeType themeType;
    private long timeLength;

    public VideoSegment.ThemeType getThemeType() {
        return themeType;
    }

    public void setThemeType(VideoSegment.ThemeType themeType) {
        this.themeType = themeType;
    }

    public long getTimeLength() {
        return timeLength;
    }

    public void setTimeLength(long timeLength) {
        this.timeLength = timeLength;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.themeType == null ? -1 : this.themeType.ordinal());
        dest.writeLong(this.timeLength);
    }

    public ThemeTypeTimeLengthBean() {
    }

    protected ThemeTypeTimeLengthBean(Parcel in) {
        int tmpThemeType = in.readInt();
        this.themeType = tmpThemeType == -1 ? null : VideoSegment.ThemeType.values()[tmpThemeType];
        this.timeLength = in.readLong();
    }

    public static final Creator<ThemeTypeTimeLengthBean> CREATOR = new Creator<ThemeTypeTimeLengthBean>() {
        @Override
        public ThemeTypeTimeLengthBean createFromParcel(Parcel source) {
            return new ThemeTypeTimeLengthBean(source);
        }

        @Override
        public ThemeTypeTimeLengthBean[] newArray(int size) {
            return new ThemeTypeTimeLengthBean[size];
        }
    };
}
