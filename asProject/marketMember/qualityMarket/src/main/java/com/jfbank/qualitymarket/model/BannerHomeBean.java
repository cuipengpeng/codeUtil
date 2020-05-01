package com.jfbank.qualitymarket.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 功能：<br>
 * 作者：RaykerTeam
 * 时间： 2016/11/16 0016<br>.
 * 版本：1.0.0
 */
public class BannerHomeBean extends BaseBannerBean implements Parcelable {
    private String name;//名称	N
    private String image;//图片链接	N
    private String url;//跳转地址	N

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.image);
        dest.writeString(this.url);
    }

    public BannerHomeBean() {
    }

    protected BannerHomeBean(Parcel in) {
        this.name = in.readString();
        this.image = in.readString();
        this.url = in.readString();
    }

    public static final Parcelable.Creator<BannerHomeBean> CREATOR = new Parcelable.Creator<BannerHomeBean>() {
        public BannerHomeBean createFromParcel(Parcel source) {
            return new BannerHomeBean(source);
        }

        public BannerHomeBean[] newArray(int size) {
            return new BannerHomeBean[size];
        }
    };

    @Override
    public String getPicUrl() {
        return this.image;
    }
}
