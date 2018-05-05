package com.test.bank.bean;

/**
 * Created by 55 on 2018/1/22.
 * 基金交易结果【基金申购与赎回的bean】
 */

public class FundTradeResultBean {
    private String tradeId;

    public FundTradeResultBean() {
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    @Override
    public String toString() {
        return "FundTradeResultBean{" +
                "tradeId='" + tradeId + '\'' +
                '}';
    }
}
