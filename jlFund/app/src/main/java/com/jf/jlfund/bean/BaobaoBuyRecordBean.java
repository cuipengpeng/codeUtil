package com.jf.jlfund.bean;

import java.util.ArrayList;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2018/1/8<br>
*/
public class BaobaoBuyRecordBean {


    private ArrayList<TradeHistory> tradeHistory;

    public static class TradeHistory {
        private String fundcode;
        private Object confirmshare;
        private Object trademoney;
        private Object payStart;
        private String tradeshare;
        private Object canceltradedate;
        private String tradeno;
        private Object tradeincomedate;
        private int tradetype;  //交易类型  (1普赎2快赎3申购)'
        private Object factorage;
        private Object comfirmmoney;
        private Object confirmnav;
        private String tradeaffirmdate;
        private int paystatus;//扣款状态(0支付成功、1支付失败、2未支付、3已到账)
        private String tradedate;
        private int tradestatus;
        private String tradetoacctdate;
        private String fundname;

        public String getFundcode() {
            return fundcode;
        }

        public void setFundcode(String fundcode) {
            this.fundcode = fundcode;
        }

        public Object getConfirmshare() {
            return confirmshare;
        }

        public void setConfirmshare(Object confirmshare) {
            this.confirmshare = confirmshare;
        }

        public Object getTrademoney() {
            return trademoney;
        }

        public void setTrademoney(Object trademoney) {
            this.trademoney = trademoney;
        }

        public Object getPayStart() {
            return payStart;
        }

        public void setPayStart(Object payStart) {
            this.payStart = payStart;
        }


        public Object getCanceltradedate() {
            return canceltradedate;
        }

        public void setCanceltradedate(Object canceltradedate) {
            this.canceltradedate = canceltradedate;
        }

        public String getTradeno() {
            return tradeno;
        }

        public void setTradeno(String tradeno) {
            this.tradeno = tradeno;
        }

        public Object getTradeincomedate() {
            return tradeincomedate;
        }

        public void setTradeincomedate(Object tradeincomedate) {
            this.tradeincomedate = tradeincomedate;
        }

        public int getTradetype() {
            return tradetype;
        }

        public void setTradetype(int tradetype) {
            this.tradetype = tradetype;
        }

        public Object getFactorage() {
            return factorage;
        }

        public void setFactorage(Object factorage) {
            this.factorage = factorage;
        }

        public Object getComfirmmoney() {
            return comfirmmoney;
        }

        public void setComfirmmoney(Object comfirmmoney) {
            this.comfirmmoney = comfirmmoney;
        }

        public Object getConfirmnav() {
            return confirmnav;
        }

        public void setConfirmnav(Object confirmnav) {
            this.confirmnav = confirmnav;
        }

        public String getTradeaffirmdate() {
            return tradeaffirmdate;
        }

        public void setTradeaffirmdate(String tradeaffirmdate) {
            this.tradeaffirmdate = tradeaffirmdate;
        }

        public String getTradedate() {
            return tradedate;
        }

        public void setTradedate(String tradedate) {
            this.tradedate = tradedate;
        }

        public int getTradestatus() {
            return tradestatus;
        }

        public void setTradestatus(int tradestatus) {
            this.tradestatus = tradestatus;
        }

        public String getTradetoacctdate() {
            return tradetoacctdate;
        }

        public void setTradetoacctdate(String tradetoacctdate) {
            this.tradetoacctdate = tradetoacctdate;
        }

        public int getPaystatus() {
            return paystatus;
        }

        public void setPaystatus(int paystatus) {
            this.paystatus = paystatus;
        }

        public String getTradeshare() {
            return tradeshare;
        }

        public void setTradeshare(String tradeshare) {
            this.tradeshare = tradeshare;
        }

        public String getFundname() {
            return fundname;
        }

        public void setFundname(String fundname) {
            this.fundname = fundname;
        }
    }

    public ArrayList<TradeHistory> getTradeHistory() {
        return tradeHistory;
    }

    public void setTradeHistory(ArrayList<TradeHistory> tradeHistory) {
        this.tradeHistory = tradeHistory;
    }


}
