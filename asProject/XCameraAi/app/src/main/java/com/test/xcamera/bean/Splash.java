package com.test.xcamera.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhouxuecheng on
 * Create Time 2020/6/9
 * e-mail zhouxuecheng1991@163.com
 */

public class Splash implements Parcelable {
    public int id;
    public String title;
    public String imageUrl;//广告图
    public String link;//需要跳转的url

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.imageUrl);
        dest.writeString(this.link);
    }

    public Splash() {
    }

    protected Splash(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.imageUrl = in.readString();
        this.link = in.readString();
    }

    public static final Parcelable.Creator<Splash> CREATOR = new Parcelable.Creator<Splash>() {
        @Override
        public Splash createFromParcel(Parcel source) {
            return new Splash(source);
        }

        @Override
        public Splash[] newArray(int size) {
            return new Splash[size];
        }
    };
}
