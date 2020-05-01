package com.jfbank.qualitymarket.model;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * 功能：<br>
 * 作者：赵海<br>
 * 时间： 2017/5/16 0016<br>.
 * 版本：1.2.0
 */

public class OrderInfoBean implements Serializable {

    /**
     * activityNo :
     * attribution :
     * curDownPaymentRatio : 10
     * curMonthNum : 12
     * downpaymentMonth : 179.90
     * isActivity : 0
     * isCanVAT : 1
     * jdPrice : 1799.00
     * monthMoney : 159.22
     * notEmpty :
     * phone :
     * productImages : https://img13.360buyimg.com/n2/jfs/t2905/355/1353320309/153208/342f1f1a/573d53d2N578e1b89.jpg
     * productName : 小米5 全网通 高配版 3GB内存 64GB ROM 淡紫色 移动联通电信4G手机
     * productNo : pzsc1472127073729
     * rate : 1.50
     */

    private String activityNo;
    private String attribution;
    private String curDownPaymentRatio;
    private String curMonthNum;
    private String downpaymentMonth;
    private String isActivity;
    private String isCanVAT;
    private String jdPrice;
    private String monthMoney;
    private String notEmpty;
    private String phone;
    private String productImages;
    private String productName;
    private String productNo;
    private String rate;

    public String getActivityNo() {
        return activityNo;
    }

    public void setActivityNo(String activityNo) {
        this.activityNo = activityNo;
    }

    public String getAttribution() {
        return attribution;
    }

    public void setAttribution(String attribution) {
        this.attribution = attribution;
    }

    public String getCurDownPaymentRatio() {
        return curDownPaymentRatio;
    }

    public void setCurDownPaymentRatio(String curDownPaymentRatio) {
        this.curDownPaymentRatio = curDownPaymentRatio;
    }

    public String getCurMonthNum() {
        return curMonthNum;
    }

    public void setCurMonthNum(String curMonthNum) {
        this.curMonthNum = curMonthNum;
    }

    public String getDownpaymentMonth() {
        return downpaymentMonth;
    }

    public void setDownpaymentMonth(String downpaymentMonth) {
        this.downpaymentMonth = downpaymentMonth;
    }

    public String getIsActivity() {
        return isActivity;
    }

    public void setIsActivity(String isActivity) {
        this.isActivity = isActivity;
    }

    public String getIsCanVAT() {
        return isCanVAT;
    }

    public void setIsCanVAT(String isCanVAT) {
        this.isCanVAT = isCanVAT;
    }

    public String getJdPrice() {
        return jdPrice;
    }

    public void setJdPrice(String jdPrice) {
        this.jdPrice = jdPrice;
    }

    public String getMonthMoney() {
        return monthMoney;
    }

    public void setMonthMoney(String monthMoney) {
        this.monthMoney = monthMoney;
    }

    public int getNotEmpty() {
        if (TextUtils.isEmpty(notEmpty)) {
            return 0;
        } else {
            try {
                return Integer.parseInt(notEmpty);
            } catch (Exception e) {
                return 0;
            }
        }
    }

    public void setNotEmpty(String notEmpty) {
        this.notEmpty = notEmpty;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProductImages() {
        return productImages;
    }

    public void setProductImages(String productImages) {
        this.productImages = productImages;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public int getRate() {
        if (TextUtils.isEmpty(rate)){
            return 0;
        }else{
            try {
                return Integer.parseInt(notEmpty);
            } catch (Exception e) {
                return 0;
            }
        }
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
