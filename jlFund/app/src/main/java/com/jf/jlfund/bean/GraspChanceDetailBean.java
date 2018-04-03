package com.jf.jlfund.bean;

/**
 * Created by 55 on 2017/12/19.
 */

public class GraspChanceDetailBean {
    private String yestincome;  //昨日涨幅
    private String fundtype;    //基金类型
    private String fundnav;     //基金净值
    private String tradedate;   //净值对应日期
    private String custrisk;    //风险等级
    private String fundsname;   //基金名称
    private String fundcode;    //基金代码
    private String description; //内部介绍
    private Integer fundstate;  //基金状态      1,2,3,6,7 显示收益走势图
    private Integer declarestate;  //申购状态    1-开放 2-暂停【按钮状态可点不可点】
    private Integer withdrawstate;  //申赎回状态   1开放  2暂停

    public GraspChanceDetailBean() {
    }

    public boolean isShowLineChart() {
        if (fundstate == 1 || fundstate == 2 || fundstate == 3 || fundstate == 6 || fundstate == 7) {
            return true;
        }
        return false;
    }

    public String getYestincome() {
        return yestincome;
    }

    public void setYestincome(String yestincome) {
        this.yestincome = yestincome;
    }

    public String getFundtype() {
        return fundtype;
    }

    public void setFundtype(String fundtype) {
        this.fundtype = fundtype;
    }

    public String getFundnav() {
        return fundnav;
    }

    public void setFundnav(String fundnav) {
        this.fundnav = fundnav;
    }

    public String getTradedate() {
        return tradedate;
    }

    public void setTradedate(String tradedate) {
        this.tradedate = tradedate;
    }

    public String getCustrisk() {
        return custrisk;
    }

    public void setCustrisk(String custrisk) {
        this.custrisk = custrisk;
    }

    public String getFundsname() {
        return fundsname;
    }

    public void setFundsname(String fundsname) {
        this.fundsname = fundsname;
    }

    public String getFundcode() {
        return fundcode;
    }

    public void setFundcode(String fundcode) {
        this.fundcode = fundcode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getFundstate() {
        return fundstate;
    }

    public void setFundstate(Integer fundstate) {
        this.fundstate = fundstate;
    }

    public Integer getDeclarestate() {
        return declarestate;
    }

    public void setDeclarestate(Integer declarestate) {
        this.declarestate = declarestate;
    }

    public Integer getWithdrawstate() {
        return withdrawstate;
    }

    public void setWithdrawstate(Integer withdrawstate) {
        this.withdrawstate = withdrawstate;
    }

    @Override
    public String toString() {
        return "GraspChanceDetailBean{" +
                "yestincome='" + yestincome + '\'' +
                ", fundtype='" + fundtype + '\'' +
                ", fundnav='" + fundnav + '\'' +
                ", tradedate='" + tradedate + '\'' +
                ", custrisk='" + custrisk + '\'' +
                ", fundsname='" + fundsname + '\'' +
                ", fundcode='" + fundcode + '\'' +
                ", description='" + description + '\'' +
                ", fundstate=" + fundstate +
                ", declarestate=" + declarestate +
                ", withdrawstate=" + withdrawstate +
                '}';
    }
}
