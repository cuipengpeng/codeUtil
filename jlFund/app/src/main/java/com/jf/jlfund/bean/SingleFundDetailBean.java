package com.jf.jlfund.bean;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2017/12/12<br>
*/
public class SingleFundDetailBean {
    private String fundnav;
    private String fundtype;
    private String fundcode;
//    0	其他
//    1	正常上市
//    2	暂停上市
//    3	终止上市
//    4	发行配售期间
//    5	发行失败
//    6	发行未上市
//    7	发行不流通
//    8	暂缓发行
//
//    备注：
//             
//    正常上市：    可以申购   可以赎回      正常
//    暂停上市       停止申购   停止赎回       和封闭期无关  正常
//    终止上市       停止申购   停止赎回       和封闭期无关   正常
//    发行配售期间    可以申购  不能赎回       认购期
//    发行失败                                   认购
//    发行未上市        募集结束但未上市    封闭期 
//    发行不流通         封闭期
    private int fundstate;// 基金状态

    private int declarestate;// 申购状态    1-开放 2-暂停
    private String fundsname;
    private String tradedate;
    private String yestincome;
    private int withdrawstate;//赎回状态   1开放  2暂停
    private int rating;//晨星评级
    private String custrisk;
    private String profitDate;
    private String confirmDate;
    private boolean collected;


    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public String getProfitDate() {
        return profitDate;
    }

    public void setProfitDate(String profitDate) {
        this.profitDate = profitDate;
    }

    public String getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(String confirmDate) {
        this.confirmDate = confirmDate;
    }

    public String getFundnav() {
        return fundnav;
    }

    public void setFundnav(String fundnav) {
        this.fundnav = fundnav;
    }

    public String getFundtype() {
        return fundtype;
    }

    public void setFundtype(String fundtype) {
        this.fundtype = fundtype;
    }

    public String getFundcode() {
        return fundcode;
    }

    public void setFundcode(String fundcode) {
        this.fundcode = fundcode;
    }

    public int getFundstate() {
        return fundstate;
    }

    public void setFundstate(int fundstate) {
        this.fundstate = fundstate;
    }

    public int getDeclarestate() {
        return declarestate;
    }

    public void setDeclarestate(int declarestate) {
        this.declarestate = declarestate;
    }

    public String getFundsname() {
        return fundsname;
    }

    public void setFundsname(String fundsname) {
        this.fundsname = fundsname;
    }

    public String getTradedate() {
        return tradedate;
    }

    public void setTradedate(String tradedate) {
        this.tradedate = tradedate;
    }

    public String getYestincome() {
        return yestincome;
    }

    public void setYestincome(String yestincome) {
        this.yestincome = yestincome;
    }

    public int getWithdrawstate() {
        return withdrawstate;
    }

    public void setWithdrawstate(int withdrawstate) {
        this.withdrawstate = withdrawstate;
    }

    public String getCustrisk() {
        return custrisk;
    }

    public void setCustrisk(String custrisk) {
        this.custrisk = custrisk;
    }
}
