package com.test.xcamera.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 周 on 2020/4/22.
 */

public class UserInfo implements Parcelable {
    private int code;
    private String message;

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

    public UserInfoData getData() {
        return data;
    }

    public void setData(UserInfoData data) {
        this.data = data;
    }

    private UserInfoData data;

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

    public UserInfo() {
    }

    protected UserInfo(Parcel in) {
        this.code = in.readInt();
        this.message = in.readString();
        this.data = in.readParcelable(UserInfoData.class.getClassLoader());
    }

    public static final Parcelable.Creator<UserInfo> CREATOR = new Parcelable.Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel source) {
            return new UserInfo(source);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };
}
