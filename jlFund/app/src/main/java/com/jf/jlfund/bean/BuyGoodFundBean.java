package com.jf.jlfund.bean;

/**
 * Created by 55 on 2017/12/7.
 */

public class BuyGoodFundBean {
    private Integer id;
    private String imgUrl;
    private String profit;
    private String title;
    private String address_url;
    private String wealthTypeName;
    private String wealthType;
    private String fundscode;
    private String secondTitle;
    private String skipType;

    public BuyGoodFundBean() {
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

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress_url() {
        return address_url;
    }

    public void setAddress_url(String address_url) {
        this.address_url = address_url;
    }

    public String getWealthTypeName() {
        return wealthTypeName;
    }

    public void setWealthTypeName(String wealthTypeName) {
        this.wealthTypeName = wealthTypeName;
    }

    public String getWealthType() {
        return wealthType;
    }

    public void setWealthType(String wealthType) {
        this.wealthType = wealthType;
    }

    public String getFundscode() {
        return fundscode;
    }

    public void setFundscode(String fundscode) {
        this.fundscode = fundscode;
    }

    public String getSecondTitle() {
        return secondTitle;
    }

    public void setSecondTitle(String secondTitle) {
        this.secondTitle = secondTitle;
    }

    public String getSkipType() {
        return skipType;
    }

    public void setSkipType(String skipType) {
        this.skipType = skipType;
    }

    @Override
    public String toString() {
        return "BuyGoodFundBean{" +
                "id=" + id +
                ", imgUrl='" + imgUrl + '\'' +
                ", profit='" + profit + '\'' +
                ", title='" + title + '\'' +
                ", address_url='" + address_url + '\'' +
                ", wealthTypeName='" + wealthTypeName + '\'' +
                ", wealthType='" + wealthType + '\'' +
                ", fundscode='" + fundscode + '\'' +
                ", secondTitle='" + secondTitle + '\'' +
                ", skipType='" + skipType + '\'' +
                '}';
    }
}
