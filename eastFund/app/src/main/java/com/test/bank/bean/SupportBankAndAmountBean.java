package com.test.bank.bean;

/**
 * Created by 55 on 2018/2/9.
 */

public class SupportBankAndAmountBean {
    private String bankname;
    private String bankno;
    private String limitday;
    private String limitonce;
    private String icon;

    public SupportBankAndAmountBean() {
    }

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public String getBankno() {
        return bankno;
    }

    public void setBankno(String bankno) {
        this.bankno = bankno;
    }

    public String getLimitday() {
        return limitday;
    }

    public void setLimitday(String limitday) {
        this.limitday = limitday;
    }

    public String getLimitonce() {
        return limitonce;
    }

    public void setLimitonce(String limitonce) {
        this.limitonce = limitonce;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "SupportBankAndAmountBean{" +
                "bankname='" + bankname + '\'' +
                ", bankno='" + bankno + '\'' +
                ", limitday='" + limitday + '\'' +
                ", limitonce='" + limitonce + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }
}
