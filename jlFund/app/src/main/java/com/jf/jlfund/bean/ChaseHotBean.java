package com.jf.jlfund.bean;

/**
 * Created by 55 on 2017/12/8.
 */

public class ChaseHotBean {
    private Integer id;
    private String imgUrl;
    private String title;
    private String fundsName;
    private String address_url;
    private String fundsCode;
    private String skipType;
    private String secondTitle;


    public ChaseHotBean() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFundsName() {
        return fundsName;
    }

    public void setFundsName(String fundsName) {
        this.fundsName = fundsName;
    }

    public String getAddress_url() {
        return address_url;
    }

    public void setAddress_url(String address_url) {
        this.address_url = address_url;
    }

    public String getFundsCode() {
        return fundsCode;
    }

    public void setFundsCode(String fundsCode) {
        this.fundsCode = fundsCode;
    }

    public String getSkipType() {
        return skipType;
    }

    public void setSkipType(String skipType) {
        this.skipType = skipType;
    }

    public String getSecondTitle() {
        return secondTitle;
    }

    public void setSecondTitle(String secondTitle) {
        this.secondTitle = secondTitle;
    }

    @Override
    public String toString() {
        return "ChaseHotBean{" +
                "id=" + id +
                ", imgUrl='" + imgUrl + '\'' +
                ", title='" + title + '\'' +
                ", fundsName='" + fundsName + '\'' +
                ", address_url='" + address_url + '\'' +
                ", fundsCode='" + fundsCode + '\'' +
                ", skipType='" + skipType + '\'' +
                ", secondTitle='" + secondTitle + '\'' +
                '}';
    }
}
