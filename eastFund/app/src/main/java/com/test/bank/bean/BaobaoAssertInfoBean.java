package com.test.bank.bean;

import java.io.Serializable;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2018/1/8<br>
*/
public class BaobaoAssertInfoBean implements Serializable{


    private String SingleDayLimit;
    private String totalcountincome;
    private String fundCode;
    private String totalyestincome;
    private boolean isStopQuickRedeem;
    private String havingMoney;
    private String msg;
    private boolean isNewMsg;
    private boolean isWorkDay;
    private String content;
    private String maxRedemptionStare;
    private String fundvolbalance;
    private String SingleLimit;
    private String unConfirmMoney;

    public String getSingleDayLimit() {
        return SingleDayLimit;
    }

    public void setSingleDayLimit(String SingleDayLimit) {
        this.SingleDayLimit = SingleDayLimit;
    }

    public String getTotalcountincome() {
        return totalcountincome;
    }

    public void setTotalcountincome(String totalcountincome) {
        this.totalcountincome = totalcountincome;
    }

    public String getFundCode() {
        return fundCode;
    }

    public void setFundCode(String fundCode) {
        this.fundCode = fundCode;
    }

    public String getTotalyestincome() {
        return totalyestincome;
    }

    public void setTotalyestincome(String totalyestincome) {
        this.totalyestincome = totalyestincome;
    }

    public boolean getIsStopQuickRedeem() {
        return isStopQuickRedeem;
    }

    public void setIsStopQuickRedeem(boolean isStopQuickRedeem) {
        this.isStopQuickRedeem = isStopQuickRedeem;
    }

    public String getHavingMoney() {
        return havingMoney;
    }

    public void setHavingMoney(String havingMoney) {
        this.havingMoney = havingMoney;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean getIsNewMsg() {
        return isNewMsg;
    }

    public void setIsNewMsg(boolean isNewMsg) {
        this.isNewMsg = isNewMsg;
    }

    public boolean getIsWorkDay() {
        return isWorkDay;
    }

    public void setIsWorkDay(boolean isWorkDay) {
        this.isWorkDay = isWorkDay;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMaxRedemptionStare() {
        return maxRedemptionStare;
    }

    public void setMaxRedemptionStare(String maxRedemptionStare) {
        this.maxRedemptionStare = maxRedemptionStare;
    }

    public String getFundvolbalance() {
        return fundvolbalance;
    }

    public void setFundvolbalance(String fundvolbalance) {
        this.fundvolbalance = fundvolbalance;
    }

    public String getSingleLimit() {
        return SingleLimit;
    }

    public void setSingleLimit(String SingleLimit) {
        this.SingleLimit = SingleLimit;
    }

    public String getUnConfirmMoney() {
        return unConfirmMoney;
    }

    public void setUnConfirmMoney(String unConfirmMoney) {
        this.unConfirmMoney = unConfirmMoney;
    }

}
