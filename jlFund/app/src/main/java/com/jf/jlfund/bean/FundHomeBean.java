package com.jf.jlfund.bean;

import java.util.List;

/**
 * Created by 55 on 2018/1/24.
 */

public class FundHomeBean {
    private String yestodayIncome;  //昨日总收益
    private String totaladdincome;  //总持有收益
    private String totalIncome;     //累计收益
    private String totalAmount;     //总金额
    private boolean hasHisotryTrade;    //是否有历史记录true有false无
    private List<HoldFundBean> funds;   //持有基金数组

    private List<GraspChanceBean> weBuy;

    public FundHomeBean() {
    }

    public String getYestodayIncome() {
        return yestodayIncome;
    }

    public void setYestodayIncome(String yestodayIncome) {
        this.yestodayIncome = yestodayIncome;
    }

    public String getTotaladdincome() {
        return totaladdincome;
    }

    public void setTotaladdincome(String totaladdincome) {
        this.totaladdincome = totaladdincome;
    }

    public String getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(String totalIncome) {
        this.totalIncome = totalIncome;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public boolean isHasHisotryTrade() {
        return hasHisotryTrade;
    }

    public void setHasHisotryTrade(boolean hasHisotryTrade) {
        this.hasHisotryTrade = hasHisotryTrade;
    }

    public List<HoldFundBean> getFunds() {
        return funds;
    }

    public void setFunds(List<HoldFundBean> funds) {
        this.funds = funds;
    }

    public List<GraspChanceBean> getWeBuy() {
        return weBuy;
    }

    public void setWeBuy(List<GraspChanceBean> weBuy) {
        this.weBuy = weBuy;
    }

    @Override
    public String toString() {
        return "FundHomeBean{" +
                "yestodayIncome='" + yestodayIncome + '\'' +
                ", totaladdincome='" + totaladdincome + '\'' +
                ", totalIncome='" + totalIncome + '\'' +
                ", totalAmount='" + totalAmount + '\'' +
                ", hasHisotryTrade=" + hasHisotryTrade +
                ", funds=" + funds +
                ", weBuy=" + weBuy +
                '}';
    }

    public static class HoldFundBean {
        private String yestodayProfit;  //昨日收益
        private String havingAmount;  //基金余额
        private String totalProfit;  //累计收益
        private int tradeInway;  //在途交易笔数
        private String fundname;  //在途交易笔数
        private String fundcode;  //在途交易笔数
        private String fundtype;  //基金类型
        private String fundstate;  //在途交易笔数
        private String declareState;  //申购状态    1-开放 2-暂停
        private String withDrawState;  //赎回状态   1开放  2暂停

        public HoldFundBean() {
        }

        public String getYestodayProfit() {
            return yestodayProfit;
        }

        public void setYestodayProfit(String yestodayProfit) {
            this.yestodayProfit = yestodayProfit;
        }

        public String getHavingAmount() {
            return havingAmount;
        }

        public void setHavingAmount(String havingAmount) {
            this.havingAmount = havingAmount;
        }

        public String getTotalProfit() {
            return totalProfit;
        }

        public void setTotalProfit(String totalProfit) {
            this.totalProfit = totalProfit;
        }

        public int getTradeInway() {
            return tradeInway;
        }

        public void setTradeInway(int tradeInway) {
            this.tradeInway = tradeInway;
        }

        public String getFundname() {
            return fundname;
        }

        public void setFundname(String fundname) {
            this.fundname = fundname;
        }

        public String getFundcode() {
            return fundcode;
        }

        public void setFundcode(String fundcode) {
            this.fundcode = fundcode;
        }

        public String getFundtype() {
            return fundtype;
        }

        public void setFundtype(String fundtype) {
            this.fundtype = fundtype;
        }

        public String getFundstate() {
            return fundstate;
        }

        public void setFundstate(String fundstate) {
            this.fundstate = fundstate;
        }

        public String getDeclareState() {
            return declareState;
        }

        public void setDeclareState(String declareState) {
            this.declareState = declareState;
        }

        public String getWithDrawState() {
            return withDrawState;
        }

        public void setWithDrawState(String withDrawState) {
            this.withDrawState = withDrawState;
        }

        @Override
        public String toString() {
            return "HoldFundBean{" +
                    "yestodayProfit='" + yestodayProfit + '\'' +
                    ", havingAmount='" + havingAmount + '\'' +
                    ", totalProfit='" + totalProfit + '\'' +
                    ", tradeInway='" + tradeInway + '\'' +
                    ", fundname='" + fundname + '\'' +
                    ", fundcode='" + fundcode + '\'' +
                    ", fundtype='" + fundtype + '\'' +
                    ", fundstate='" + fundstate + '\'' +
                    ", declareState='" + declareState + '\'' +
                    ", withDrawState='" + withDrawState + '\'' +
                    '}';
        }
    }

}
