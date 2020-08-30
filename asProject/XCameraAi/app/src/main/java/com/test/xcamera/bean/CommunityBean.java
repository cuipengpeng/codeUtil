package com.test.xcamera.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 周 on 2020/4/22.
 */

public class CommunityBean implements Parcelable {

    private int code;
    private String message;
    private DataChild data;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataChild getData() {
        return data;
    }

    public void setData(DataChild data) {
        this.data = data;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.code);
        dest.writeString(this.message);
        dest.writeParcelable(this.data, flags);
    }

    public CommunityBean() {
    }

    protected CommunityBean(Parcel in) {
        this.code = in.readInt();
        this.message = in.readString();
        this.data = in.readParcelable(DataChild.class.getClassLoader());
    }

    public static final Parcelable.Creator<CommunityBean> CREATOR = new Parcelable.Creator<CommunityBean>() {
        @Override
        public CommunityBean createFromParcel(Parcel source) {
            return new CommunityBean(source);
        }

        @Override
        public CommunityBean[] newArray(int size) {
            return new CommunityBean[size];
        }
    };
}
