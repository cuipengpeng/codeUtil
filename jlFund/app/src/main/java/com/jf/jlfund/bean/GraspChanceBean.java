package com.jf.jlfund.bean;

/**
 * Created by 55 on 2017/12/7.
 */

public class GraspChanceBean {
    private String id;
    private String wealthTypeName;
    private String fundscode;
    private String fundsName;
    private String title;
    private String wealthType;
    private String profit;
    private int stars;
    private int everyBuyNo; //大家都在买排行榜

    public GraspChanceBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWealthTypeName() {
        return wealthTypeName;
    }

    public void setWealthTypeName(String wealthTypeName) {
        this.wealthTypeName = wealthTypeName;
    }

    public String getFundscode() {
        return fundscode;
    }

    public void setFundscode(String fundscode) {
        this.fundscode = fundscode;
    }

    public String getFundsName() {
        return fundsName;
    }

    public void setFundsName(String fundsName) {
        this.fundsName = fundsName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWealthType() {
        return wealthType;
    }

    public void setWealthType(String wealthType) {
        this.wealthType = wealthType;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }

    public int getEveryBuyNo() {
        return everyBuyNo;
    }

    public void setEveryBuyNo(int everyBuyNo) {
        this.everyBuyNo = everyBuyNo;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    @Override
    public String toString() {
        return "GraspChanceBean{" +
                "id='" + id + '\'' +
                ", wealthTypeName='" + wealthTypeName + '\'' +
                ", fundscode='" + fundscode + '\'' +
                ", fundsName='" + fundsName + '\'' +
                ", title='" + title + '\'' +
                ", wealthType='" + wealthType + '\'' +
                ", profit='" + profit + '\'' +
                ", stars=" + stars +
                '}';
    }
}
