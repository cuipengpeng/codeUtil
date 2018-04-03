package com.jf.jlfund.bean;

import java.io.Serializable;
import java.util.ArrayList;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2018/1/8<br>
*/
public class GetOutBankCardInfoBean implements Serializable {

    private static final long serialVersionUID = -3450064362986273896L;

    private String max_buy_money;
    private String holdmin;
    private ArrayList<Call_rate_list> call_rate_list;
    private String bank_card;
    private String subscribestate;
    private String max_redemption_stare;
    private String declarestate;
    private String redemption_rate;
    private String fundstate;
    private String valuagrstate;
    private Object risklevel;
    private String bank_logo_url;
    private String confirmeddate;
    private String buy_rate;
    private ArrayList<Chag_rate_list> chag_rate_list;
    private String withdrawstate;
    private int limit_each;
    private String max_money;
    private String per_min;//最低赎回份额
    private String unpaidincome;
    private String bank_name;
    private String redeemrefunddate;
    private String incomedate;
    private int fundType;
    private String pert_val_low_lim;//最低购买金额
    private String SingleLimit;
    private String SingleDayLimit;

    public String getSingleLimit() {
        return SingleLimit;
    }

    public void setSingleLimit(String singleLimit) {
        SingleLimit = singleLimit;
    }

    public String getSingleDayLimit() {
        return SingleDayLimit;
    }

    public void setSingleDayLimit(String singleDayLimit) {
        SingleDayLimit = singleDayLimit;
    }


    public int getFundType() {
        return fundType;
    }

    public void setFundType(int fundType) {
        this.fundType = fundType;
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

    public static class Chag_rate_list {
        private String purchase_amount_interval;
        private String ratio;
        private String riscountRatio;

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

        public String getRiscountRatio() {
            return riscountRatio;
        }

        public void setRiscountRatio(String riscountRatio) {
            this.riscountRatio = riscountRatio;
        }
    }

    public String getMax_buy_money() {
        return max_buy_money;
    }

    public void setMax_buy_money(String max_buy_money) {
        this.max_buy_money = max_buy_money;
    }

    public String getHoldmin() {
        return holdmin;
    }

    public void setHoldmin(String holdmin) {
        this.holdmin = holdmin;
    }

    public ArrayList<Call_rate_list> getCall_rate_list() {
        return call_rate_list;
    }

    public void setCall_rate_list(ArrayList<Call_rate_list> call_rate_list) {
        this.call_rate_list = call_rate_list;
    }

    public String getBank_card() {
        return bank_card;
    }

    public void setBank_card(String bank_card) {
        this.bank_card = bank_card;
    }

    public String getSubscribestate() {
        return subscribestate;
    }

    public void setSubscribestate(String subscribestate) {
        this.subscribestate = subscribestate;
    }

    public String getMax_redemption_stare() {
        return max_redemption_stare;
    }

    public void setMax_redemption_stare(String max_redemption_stare) {
        this.max_redemption_stare = max_redemption_stare;
    }

    public String getDeclarestate() {
        return declarestate;
    }

    public void setDeclarestate(String declarestate) {
        this.declarestate = declarestate;
    }

    public String getRedemption_rate() {
        return redemption_rate;
    }

    public void setRedemption_rate(String redemption_rate) {
        this.redemption_rate = redemption_rate;
    }

    public String getFundstate() {
        return fundstate;
    }

    public void setFundstate(String fundstate) {
        this.fundstate = fundstate;
    }

    public String getValuagrstate() {
        return valuagrstate;
    }

    public void setValuagrstate(String valuagrstate) {
        this.valuagrstate = valuagrstate;
    }

    public Object getRisklevel() {
        return risklevel;
    }

    public void setRisklevel(Object risklevel) {
        this.risklevel = risklevel;
    }

    public String getBank_logo_url() {
        return bank_logo_url;
    }

    public void setBank_logo_url(String bank_logo_url) {
        this.bank_logo_url = bank_logo_url;
    }

    public String getConfirmeddate() {
        return confirmeddate;
    }

    public void setConfirmeddate(String confirmeddate) {
        this.confirmeddate = confirmeddate;
    }

    public String getBuy_rate() {
        return buy_rate;
    }

    public void setBuy_rate(String buy_rate) {
        this.buy_rate = buy_rate;
    }

    public ArrayList<Chag_rate_list> getChag_rate_list() {
        return chag_rate_list;
    }

    public void setChag_rate_list(ArrayList<Chag_rate_list> chag_rate_list) {
        this.chag_rate_list = chag_rate_list;
    }

    public String getWithdrawstate() {
        return withdrawstate;
    }

    public void setWithdrawstate(String withdrawstate) {
        this.withdrawstate = withdrawstate;
    }

    public int getLimit_each() {
        return limit_each;
    }

    public void setLimit_each(int limit_each) {
        this.limit_each = limit_each;
    }

    public String getMax_money() {
        return max_money;
    }

    public void setMax_money(String max_money) {
        this.max_money = max_money;
    }

    public String getPer_min() {
        return per_min;
    }

    public void setPer_min(String per_min) {
        this.per_min = per_min;
    }

    public String getUnpaidincome() {
        return unpaidincome;
    }

    public void setUnpaidincome(String unpaidincome) {
        this.unpaidincome = unpaidincome;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getRedeemrefunddate() {
        return redeemrefunddate;
    }

    public void setRedeemrefunddate(String redeemrefunddate) {
        this.redeemrefunddate = redeemrefunddate;
    }

    public String getIncomedate() {
        return incomedate;
    }

    public void setIncomedate(String incomedate) {
        this.incomedate = incomedate;
    }

    public String getPert_val_low_lim() {
        return pert_val_low_lim;
    }

    public void setPert_val_low_lim(String pert_val_low_lim) {
        this.pert_val_low_lim = pert_val_low_lim;
    }


}
