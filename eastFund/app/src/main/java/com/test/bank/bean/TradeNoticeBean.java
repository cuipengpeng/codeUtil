package com.test.bank.bean;

import java.util.ArrayList;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2017/12/12<br>
*/
public class TradeNoticeBean {

    private ArrayList<Chag_rate_list> chag_rate_list;
    private float tru_fee_ratio;
    private BuyDate buyDate;
    private float sale_fee_ratio;
    private float mng_fee_ratio;
    private SellDate sellDate;
    private ArrayList<Call_rate_list> call_rate_list;

    public static class Chag_rate_list {
        private String purchase_amount_interval;
        private String riscountRatio;
        private String ratio;

        public String getPurchase_amount_interval() {
            return purchase_amount_interval;
        }

        public void setPurchase_amount_interval(String purchase_amount_interval) {
            this.purchase_amount_interval = purchase_amount_interval;
        }

        public String getRiscountRatio() {
            return riscountRatio;
        }

        public void setRiscountRatio(String riscountRatio) {
            this.riscountRatio = riscountRatio;
        }

        public String getRatio() {
            return ratio;
        }

        public void setRatio(String ratio) {
            this.ratio = ratio;
        }
    }

    public static class BuyDate {
        private String profitDate;
        private String confirmDate;

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
    }

    public static class SellDate {
        private String toAccount;
        private String confirmDate;

        public String getToAccount() {
            return toAccount;
        }

        public void setToAccount(String toAccount) {
            this.toAccount = toAccount;
        }

        public String getConfirmDate() {
            return confirmDate;
        }

        public void setConfirmDate(String confirmDate) {
            this.confirmDate = confirmDate;
        }
    }

    public static class Call_rate_list {
        private String purchase_amount_interval;
        private String ratio;

        public String getPurchase_amount_interval() {
            return purchase_amount_interval;
        }

        public void setPurchase_amount_interval(String purchase_amount_interval) {
            this.purchase_amount_interval = purchase_amount_interval;
        }

        public String getRatio() {
            return ratio;
        }

        public void setRatio(String ratio) {
            this.ratio = ratio;
        }
    }

    public ArrayList<Chag_rate_list> getChag_rate_list() {
        return chag_rate_list;
    }

    public void setChag_rate_list(ArrayList<Chag_rate_list> chag_rate_list) {
        this.chag_rate_list = chag_rate_list;
    }

    public float getTru_fee_ratio() {
        return tru_fee_ratio;
    }

    public void setTru_fee_ratio(float tru_fee_ratio) {
        this.tru_fee_ratio = tru_fee_ratio;
    }

    public BuyDate getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(BuyDate buyDate) {
        this.buyDate = buyDate;
    }

    public float getSale_fee_ratio() {
        return sale_fee_ratio;
    }

    public void setSale_fee_ratio(float sale_fee_ratio) {
        this.sale_fee_ratio = sale_fee_ratio;
    }

    public float getMng_fee_ratio() {
        return mng_fee_ratio;
    }

    public void setMng_fee_ratio(float mng_fee_ratio) {
        this.mng_fee_ratio = mng_fee_ratio;
    }

    public SellDate getSellDate() {
        return sellDate;
    }

    public void setSellDate(SellDate sellDate) {
        this.sellDate = sellDate;
    }

    public ArrayList<Call_rate_list> getCall_rate_list() {
        return call_rate_list;
    }

    public void setCall_rate_list(ArrayList<Call_rate_list> call_rate_list) {
        this.call_rate_list = call_rate_list;
    }



}