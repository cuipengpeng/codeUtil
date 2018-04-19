package com.jfbank.qualitymarket.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 功能：<br>
 * 作者：RaykerTeam
 * 时间： 2016/11/16 0016<br>.
 * 版本：1.0.0
 */
public class PrudouctBean implements Parcelable {
    private String proName;//	产品名称	N
    private String proImage;//	产品图片	N
    private String price;//	价格	N
    private String monthAmount;//	月供金额	N
    private String firstPayment;//	首付金额	N
    private String monthnum;//	最大分期数	N
    private String productNo;//pzsc1471857276558",
    private String sort;// 2,
    private String categoryId;//quqpmulo20160830221744228",
    private String   urlType;//	String	链接类型(1,商品编号;2,页面链接)
    private String  urlContent;//	String	链接内容
    private String   picture;//	String	图片(存储uuid)

    public String getUrlType() {
        return urlType;
    }

    public void setUrlType(String urlType) {
        this.urlType = urlType;
    }

    public String getUrlContent() {
        return urlContent;
    }

    public void setUrlContent(String urlContent) {
        this.urlContent = urlContent;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public String getProImage() {
        return proImage;
    }

    public void setProImage(String proImage) {
        this.proImage = proImage;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMonthAmount() {
        return monthAmount;
    }

    public void setMonthAmount(String monthAmount) {
        this.monthAmount = monthAmount;
    }

    public String getFirstPayment() {
        return firstPayment;
    }

    public void setFirstPayment(String firstPayment) {
        this.firstPayment = firstPayment;
    }

    public String getMonthnum() {
        return monthnum;
    }

    public void setMonthnum(String monthnum) {
        this.monthnum = monthnum;
    }

    public PrudouctBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.proName);
        dest.writeString(this.proImage);
        dest.writeString(this.price);
        dest.writeString(this.monthAmount);
        dest.writeString(this.firstPayment);
        dest.writeString(this.monthnum);
        dest.writeString(this.productNo);
        dest.writeString(this.sort);
        dest.writeString(this.categoryId);
        dest.writeString(this.urlType);
        dest.writeString(this.urlContent);
        dest.writeString(this.picture);
    }

    protected PrudouctBean(Parcel in) {
        this.proName = in.readString();
        this.proImage = in.readString();
        this.price = in.readString();
        this.monthAmount = in.readString();
        this.firstPayment = in.readString();
        this.monthnum = in.readString();
        this.productNo = in.readString();
        this.sort = in.readString();
        this.categoryId = in.readString();
        this.urlType = in.readString();
        this.urlContent = in.readString();
        this.picture = in.readString();
    }

    public static final Creator<PrudouctBean> CREATOR = new Creator<PrudouctBean>() {
        @Override
        public PrudouctBean createFromParcel(Parcel source) {
            return new PrudouctBean(source);
        }

        @Override
        public PrudouctBean[] newArray(int size) {
            return new PrudouctBean[size];
        }
    };
}
