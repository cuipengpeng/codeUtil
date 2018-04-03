package com.jf.jlfund.bean;

/**
 * 赚钱页面-活期+
 * Created by 55 on 2017/12/14.
 */

public class MakeMoneyCurrentPlusBean {

    private Integer id;
    private String innerTitle;
    private String fundCode;
    private String addressUrl;
    private Integer structureType;
    private Integer skipType;
    private String wealthTypeName;
    private String slogan;
    private String secondTitle;
    private String wealthType;
    private Double profit;

    public MakeMoneyCurrentPlusBean() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInnerTitle() {
        return innerTitle;
    }

    public void setInnerTitle(String innerTitle) {
        this.innerTitle = innerTitle;
    }

    public String getFundCode() {
        return fundCode;
    }

    public void setFundCode(String fundCode) {
        this.fundCode = fundCode;
    }

    public String getAddressUrl() {
        return addressUrl;
    }

    public void setAddressUrl(String addressUrl) {
        this.addressUrl = addressUrl;
    }

    public Integer getStructureType() {
        return structureType;
    }

    public void setStructureType(Integer structureType) {
        this.structureType = structureType;
    }

    public Integer getSkipType() {
        return skipType;
    }

    public void setSkipType(Integer skipType) {
        this.skipType = skipType;
    }

    public String getWealthTypeName() {
        return wealthTypeName;
    }

    public void setWealthTypeName(String wealthTypeName) {
        this.wealthTypeName = wealthTypeName;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getWealthType() {
        return wealthType;
    }

    public void setWealthType(String wealthType) {
        this.wealthType = wealthType;
    }

    public Double getProfit() {
        return profit;
    }

    public void setProfit(Double profit) {
        this.profit = profit;
    }

    public String getSecondTitle() {
        return secondTitle;
    }

    public void setSecondTitle(String secondTitle) {
        this.secondTitle = secondTitle;
    }

    @Override
    public String toString() {
        return "MakeMoneyCurrentPlusBean{" +
                "id=" + id +
                ", innerTitle='" + innerTitle + '\'' +
                ", fundCode='" + fundCode + '\'' +
                ", addressUrl='" + addressUrl + '\'' +
                ", structureType=" + structureType +
                ", skipType=" + skipType +
                ", wealthTypeName='" + wealthTypeName + '\'' +
                ", slogan='" + slogan + '\'' +
                ", secondTitle='" + secondTitle + '\'' +
                ", wealthType='" + wealthType + '\'' +
                ", profit=" + profit +
                '}';
    }
}
