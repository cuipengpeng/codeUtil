package com.test.bank.bean;

/**
 * Created by 55 on 2017/2/28.
 */

public class MessageBean {

    public MessageBean() {
    }

    private String fundcode;
    private String fundname;
    private String tradetype;   //交易类型
    private String tradestatus; //交易状态
    private String paystatus;
    private String tradeno; //订单编号
    private String msg_content;
    private String msg_createtime;
    private String showModule;  //来源（分为活期+和基金）    是否是宝宝（1-非，2-是）


    public String getFundcode() {
        return fundcode;
    }

    public void setFundcode(String fundcode) {
        this.fundcode = fundcode;
    }

    public String getFundname() {
        return fundname;
    }

    public void setFundname(String fundname) {
        this.fundname = fundname;
    }

    public String getTradetype() {
        return tradetype;
    }

    public void setTradetype(String tradetype) {
        this.tradetype = tradetype;
    }

    public String getTradestatus() {
        return tradestatus;
    }

    public void setTradestatus(String tradestatus) {
        this.tradestatus = tradestatus;
    }

    public String getTradeno() {
        return tradeno;
    }

    public void setTradeno(String tradeno) {
        this.tradeno = tradeno;
    }

    public String getMsg_content() {
        return msg_content;
    }

    public void setMsg_content(String msg_content) {
        this.msg_content = msg_content;
    }

    public String getMsg_createtime() {
        return msg_createtime;
    }

    public void setMsg_createtime(String msg_createtime) {
        this.msg_createtime = msg_createtime;
    }

    public String getShowModule() {
        return showModule;
    }

    public void setShowModule(String showModule) {
        this.showModule = showModule;
    }

    public String getPaystatus() {
        return paystatus;
    }

    public void setPaystatus(String paystatus) {
        this.paystatus = paystatus;
    }

    @Override
    public String toString() {
        return "MessageBean{" +
                "fundcode='" + fundcode + '\'' +
                ", fundname='" + fundname + '\'' +
                ", tradetype='" + tradetype + '\'' +
                ", tradestatus='" + tradestatus + '\'' +
                ", paystatus='" + paystatus + '\'' +
                ", tradeno='" + tradeno + '\'' +
                ", msg_content='" + msg_content + '\'' +
                ", msg_createtime='" + msg_createtime + '\'' +
                ", showModule='" + showModule + '\'' +
                '}';
    }
}
