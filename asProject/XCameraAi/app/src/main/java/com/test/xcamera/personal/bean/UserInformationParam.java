package com.test.xcamera.personal.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 请求用户信息产生修改
 */
public class UserInformationParam implements Parcelable {
    //    昵称修改
    public final static int USER_INFORMATION_TYPE_NAME=1;
    //    简介修改
    public final static int USER_INFORMATION_INTRODUCE=2;
    private String tile;
    private String name;
    private String value;
    private int type;

    public String getTile() {
        return tile;
    }

    public void setTile(String tile) {
        this.tile = tile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tile);
        dest.writeString(this.name);
        dest.writeString(this.value);
        dest.writeInt(this.type);
    }

    public UserInformationParam() {
    }

    protected UserInformationParam(Parcel in) {
        this.tile = in.readString();
        this.name = in.readString();
        this.value = in.readString();
        this.type = in.readInt();
    }

    public static final Parcelable.Creator<UserInformationParam> CREATOR = new Parcelable.Creator<UserInformationParam>() {
        @Override
        public UserInformationParam createFromParcel(Parcel source) {
            return new UserInformationParam(source);
        }

        @Override
        public UserInformationParam[] newArray(int size) {
            return new UserInformationParam[size];
        }
    };
}
