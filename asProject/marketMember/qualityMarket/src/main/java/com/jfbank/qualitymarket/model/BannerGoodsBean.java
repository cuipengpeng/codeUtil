package com.jfbank.qualitymarket.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 功能：商品Binner对象<br>
 * 作者：zhaohai
 * 时间： 2016/11/16 0016<br>.
 * 版本：1.0.0
 */
public class BannerGoodsBean extends BaseBannerBean implements Parcelable {
    private String name;//名称	N
    private String picUrl;//图片地址	N
    private String h5Url;//h5页面url	N
    private String appPage;//app页面标识	N
    private String appParams;//app页面参数	N

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getH5Url() {
        return h5Url;
    }

    public void setH5Url(String h5Url) {
        this.h5Url = h5Url;
    }

    public String getAppPage() {
        return appPage;
    }

    public void setAppPage(String appPage) {
        this.appPage = appPage;
    }

    public String getAppParams() {
        return appParams;
    }

    public void setAppParams(String appParams) {
        this.appParams = appParams;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.picUrl);
        dest.writeString(this.h5Url);
        dest.writeString(this.appPage);
        dest.writeString(this.appParams);
    }

    public BannerGoodsBean() {
    }

    protected BannerGoodsBean(Parcel in) {
        this.name = in.readString();
        this.picUrl = in.readString();
        this.h5Url = in.readString();
        this.appPage = in.readString();
        this.appParams = in.readString();
    }

    public static final Parcelable.Creator<BannerGoodsBean> CREATOR = new Parcelable.Creator<BannerGoodsBean>() {
        public BannerGoodsBean createFromParcel(Parcel source) {
            return new BannerGoodsBean(source);
        }

        public BannerGoodsBean[] newArray(int size) {
            return new BannerGoodsBean[size];
        }
    };

}
