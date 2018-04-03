package com.jf.jlfund.bean;

/**
 * Created by 55 on 2018/1/25.
 */

public class FundHoldDetailBean {
    private String fundcode;
    private String fundname;
    private String yestincome;
    private String addincome;       //累计收益
    private String fundvolbalance;      //持有金额
    private String unconfbalance;       //待确认金额
    private String fundvolunivalence;       //持仓成本
    private String availablevol;        //持有份额
    private String nav;                 //最新净值
    private String navdate;             //最新净值日期
    private String daygrowth;           //日涨幅
    private int funddealing;         //在途交易笔数或待到账金额数
    private String funddealingtype;     //在途交易类型（0交易1待到账2无交易和待到账）
    private String totalfundvolbalance_mode1;       //基金份额余额（含当日变动）
    private String fundstate;       //基金状态
    private String declarestate;       //申购状态    1-开放 2-暂停
    private String withdrawstate;       //赎回状态   1开放  2暂停
    private String showtype;        //0 展示手续费  1  展示昨日收益

    public FundHoldDetailBean() {
    }

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

    public String zgetYestincome() {
        return yestincome;
    }

    public void setYestincome(String yestincome) {
        this.yestincome = yestincome;
    }

    public String getAddincome() {
        return addincome;
    }

    public void setAddincome(String addincome) {
        this.addincome = addincome;
    }

    public String getFundvolbalance() {
        return fundvolbalance;
    }

    public void setFundvolbalance(String fundvolbalance) {
        this.fundvolbalance = fundvolbalance;
    }

    public String getUnconfbalance() {
        return unconfbalance;
    }

    public void setUnconfbalance(String unconfbalance) {
        this.unconfbalance = unconfbalance;
    }

    public String getFundvolunivalence() {
        return fundvolunivalence;
    }

    public void setFundvolunivalence(String fundvolunivalence) {
        this.fundvolunivalence = fundvolunivalence;
    }

    public String getAvailablevol() {
        return availablevol;
    }

    public void setAvailablevol(String availablevol) {
        this.availablevol = availablevol;
    }

    public String getNav() {
        return nav;
    }

    public void setNav(String nav) {
        this.nav = nav;
    }

    public String getNavdate() {
        return navdate;
    }

    public void setNavdate(String navdate) {
        this.navdate = navdate;
    }

    public String getDaygrowth() {
        return daygrowth;
    }

    public void setDaygrowth(String daygrowth) {
        this.daygrowth = daygrowth;
    }

    public int getFunddealing() {
        return funddealing;
    }

    public void setFunddealing(int funddealing) {
        this.funddealing = funddealing;
    }

    public String getFunddealingtype() {
        return funddealingtype;
    }

    public void setFunddealingtype(String funddealingtype) {
        this.funddealingtype = funddealingtype;
    }

    public String getTotalfundvolbalance_mode1() {
        return totalfundvolbalance_mode1;
    }

    public void setTotalfundvolbalance_mode1(String totalfundvolbalance_mode1) {
        this.totalfundvolbalance_mode1 = totalfundvolbalance_mode1;
    }

    public String getFundstate() {
        return fundstate;
    }

    public void setFundstate(String fundstate) {
        this.fundstate = fundstate;
    }

    public String getDeclarestate() {
        return declarestate;
    }

    public void setDeclarestate(String declarestate) {
        this.declarestate = declarestate;
    }

    public String getWithdrawstate() {
        return withdrawstate;
    }

    public void setWithdrawstate(String withdrawstate) {
        this.withdrawstate = withdrawstate;
    }

    public String getYestincome() {
        return yestincome;
    }

    public String getShowtype() {
        return showtype;
    }

    public void setShowtype(String showtype) {
        this.showtype = showtype;
    }

    @Override
    public String toString() {
        return "FundHoldDetailBean{" +
                "fundcode='" + fundcode + '\'' +
                ", fundname='" + fundname + '\'' +
                ", yestincome='" + yestincome + '\'' +
                ", addincome='" + addincome + '\'' +
                ", fundvolbalance='" + fundvolbalance + '\'' +
                ", unconfbalance='" + unconfbalance + '\'' +
                ", fundvolunivalence='" + fundvolunivalence + '\'' +
                ", availablevol='" + availablevol + '\'' +
                ", nav='" + nav + '\'' +
                ", navdate='" + navdate + '\'' +
                ", daygrowth='" + daygrowth + '\'' +
                ", funddealing=" + funddealing +
                ", funddealingtype='" + funddealingtype + '\'' +
                ", totalfundvolbalance_mode1='" + totalfundvolbalance_mode1 + '\'' +
                ", fundstate='" + fundstate + '\'' +
                ", declarestate='" + declarestate + '\'' +
                ", withdrawstate='" + withdrawstate + '\'' +
                ", showtype='" + showtype + '\'' +
                '}';
    }
}
