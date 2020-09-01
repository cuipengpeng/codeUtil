
package com.hospital.checkup.bean;

import java.io.Serializable;



public class UserInfoBean implements Serializable {
    private String token;
    private String mobile;      //Aes加密手机号码
    private String riskLevel;   //风险等级
    private Boolean isWhite;
    private Boolean bindBankCard;
    private String bankCard;    //银行卡后4位
    private String bankName;    //银行名称

    //local user config, must have default value
    private String gesturePassword = "";    //手势密码
    private boolean hiddenAccountMoney=false ;    //隐藏账户金额
    private boolean hasFinderprintPassword=false ;    //是否有指纹密码



    public UserInfoBean() {

    }

    public boolean isHasFinderprintPassword() {
        return hasFinderprintPassword;
    }

    public void setHasFinderprintPassword(boolean hasFinderprintPassword) {
        this.hasFinderprintPassword = hasFinderprintPassword;
    }

    public boolean isHiddenAccountMoney() {

        return hiddenAccountMoney;
    }

    public void setHiddenAccountMoney(boolean hiddenAccountMoney) {
        this.hiddenAccountMoney = hiddenAccountMoney;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public String getGesturePassword() {
        return gesturePassword;
    }

    public void setGesturePassword(String gesturePassword) {
        this.gesturePassword = gesturePassword;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Boolean getWhite() {
        return isWhite;
    }

    public void setWhite(Boolean white) {
        isWhite = white;
    }

    public Boolean getBindBankCard() {
        return bindBankCard;
    }

    public void setBindBankCard(Boolean bindBankCard) {
        this.bindBankCard = bindBankCard;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "token='" + token + '\'' +
                ", mobile='" + mobile + '\'' +
                ", riskLevel='" + riskLevel + '\'' +
                ", isWhite=" + isWhite +
                ", bindBankCard=" + bindBankCard +
                ", bankCard='" + bankCard + '\'' +
                ", bankName='" + bankName + '\'' +
                '}';
    }
}
