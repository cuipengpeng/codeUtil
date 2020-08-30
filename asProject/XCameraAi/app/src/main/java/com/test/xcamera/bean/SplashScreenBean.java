package com.test.xcamera.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by zhouxuecheng on
 * Create Time 2020/6/9
 * e-mail zhouxuecheng1991@163.com
 */

public class SplashScreenBean implements Parcelable {
    public int code;
    public String message;
    public ArrayList<Splash> data;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.code);
        dest.writeString(this.message);
        dest.writeTypedList(this.data);
    }

    public SplashScreenBean() {
    }

    protected SplashScreenBean(Parcel in) {
        this.code = in.readInt();
        this.message = in.readString();
        this.data = in.createTypedArrayList(Splash.CREATOR);
    }

    public static final Parcelable.Creator<SplashScreenBean> CREATOR = new Parcelable.Creator<SplashScreenBean>() {
        @Override
        public SplashScreenBean createFromParcel(Parcel source) {
            return new SplashScreenBean(source);
        }

        @Override
        public SplashScreenBean[] newArray(int size) {
            return new SplashScreenBean[size];
        }
    };
}
