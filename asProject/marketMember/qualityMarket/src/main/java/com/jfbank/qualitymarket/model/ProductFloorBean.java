package com.jfbank.qualitymarket.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能：<br>
 * 作者：RaykerTeam
 * 时间： 2016/11/16 0016<br>.
 * 版本：1.0.0
 */
public class ProductFloorBean implements Parcelable {
    private String name;
    private String h5Url;
    private String appPage;
    private String appParams;
    private List<PrudouctBean> prudouct;
    private List<BannerGoodsBean> banner;
    private int  layoutType;
    private String   image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(int layoutType) {
        this.layoutType = layoutType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<PrudouctBean> getPrudouct() {
        return prudouct;
    }

    public void setPrudouct(List<PrudouctBean> prudouct) {
        this.prudouct = prudouct;
    }

    public List<BannerGoodsBean> getBanner() {
        return banner;
    }

    public void setBanner(List<BannerGoodsBean> banner) {
        this.banner = banner;
    }

    public ProductFloorBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.h5Url);
        dest.writeString(this.appPage);
        dest.writeString(this.appParams);
        dest.writeTypedList(this.prudouct);
        dest.writeTypedList(this.banner);
        dest.writeInt(this.layoutType);
    }

    protected ProductFloorBean(Parcel in) {
        this.name = in.readString();
        this.h5Url = in.readString();
        this.appPage = in.readString();
        this.appParams = in.readString();
        this.prudouct = in.createTypedArrayList(PrudouctBean.CREATOR);
        this.banner = in.createTypedArrayList(BannerGoodsBean.CREATOR);
        this.layoutType = in.readInt();
    }

    public static final Creator<ProductFloorBean> CREATOR = new Creator<ProductFloorBean>() {
        @Override
        public ProductFloorBean createFromParcel(Parcel source) {
            return new ProductFloorBean(source);
        }

        @Override
        public ProductFloorBean[] newArray(int size) {
            return new ProductFloorBean[size];
        }
    };
}
