package com.test.bank.bean;

/**
 * Created by 55 on 2017/12/28.
 */

public class UserInfoBean {
    private String bankImgx2;
    private String bankImgx3;
    private String riskLevel;
    private Boolean bindBankCard;
    private String bankBg;
    private String bankName;
    private String bankCard;
    private Boolean isWhite;

    public UserInfoBean() {
    }

    public String getBankImgx2() {
        return bankImgx2;
    }

    public void setBankImgx2(String bankImgx2) {
        this.bankImgx2 = bankImgx2;
    }

    public String getBankImgx3() {
        return bankImgx3;
    }

    public void setBankImgx3(String bankImgx3) {
        this.bankImgx3 = bankImgx3;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Boolean getBindBankCard() {
        return bindBankCard;
    }

    public void setBindBankCard(Boolean bindBankCard) {
        this.bindBankCard = bindBankCard;
    }

    public String getBankBg() {
        return bankBg;
    }

    public void setBankBg(String bankBg) {
        this.bankBg = bankBg;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    public Boolean getIsWhite() {
        return isWhite;
    }

    public void setIsWhite(Boolean isWhite) {
        this.isWhite = isWhite;
    }

    @Override
    public String toString() {
        return "UserInfoBean{" +
                "bankImgx2='" + bankImgx2 + '\'' +
                ", bankImgx3='" + bankImgx3 + '\'' +
                ", riskLevel='" + riskLevel + '\'' +
                ", bindBankCard='" + bindBankCard + '\'' +
                ", bankBg='" + bankBg + '\'' +
                ", bankName='" + bankName + '\'' +
                ", bankCard='" + bankCard + '\'' +
                ", isWhite='" + isWhite + '\'' +
                '}';
    }
}
