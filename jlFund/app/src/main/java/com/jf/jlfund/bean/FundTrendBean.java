package com.jf.jlfund.bean;

import java.util.List;

/**
 * Created by 55 on 2017/12/18.
 */

public class FundTrendBean {

    private List<TrendInfo> last1Month;
    private List<TrendInfo> last3Month;
    private List<TrendInfo> last6Month;
    private List<TrendInfo> last12Month;

    public FundTrendBean() {
    }

    public List<TrendInfo> getLast1Month() {
        return last1Month;
    }

    public void setLast1Month(List<TrendInfo> last1Month) {
        this.last1Month = last1Month;
    }

    public List<TrendInfo> getLast3Month() {
        return last3Month;
    }

    public void setLast3Month(List<TrendInfo> last3Month) {
        this.last3Month = last3Month;
    }

    public List<TrendInfo> getLast6Month() {
        return last6Month;
    }

    public void setLast6Month(List<TrendInfo> last6Month) {
        this.last6Month = last6Month;
    }

    public List<TrendInfo> getLast12Month() {
        return last12Month;
    }

    public void setLast12Month(List<TrendInfo> last12Month) {
        this.last12Month = last12Month;
    }

    @Override
    public String toString() {
        return "FundTrendBean{" +
                "last1Month=" + last1Month +
                ", last3Month=" + last3Month +
                ", last6Month=" + last6Month +
                ", last12Month=" + last12Month +
                '}';
    }

    /**
     * 走势信息
     */
    public static class TrendInfo {
        private String tradedate;   //走势图日期
        private String fundChng;   //基金收益率
        private String hs300Chng;   //沪深300收益率

        private BuyTradeInfo buytradeinfo;
        private SellTradeInfo selltradeinfo;

        public TrendInfo() {
        }

        public String getTradedate() {
            return tradedate;
        }

        public void setTradedate(String tradedate) {
            this.tradedate = tradedate;
        }

        public String getFundChng() {
            return fundChng;
        }

        public void setFundChng(String fundChng) {
            this.fundChng = fundChng;
        }

        public String getHs300Chng() {
            return hs300Chng;
        }

        public void setHs300Chng(String hs300Chng) {
            this.hs300Chng = hs300Chng;
        }

        public BuyTradeInfo getBuytradeinfo() {
            return buytradeinfo;
        }

        public void setBuytradeinfo(BuyTradeInfo buytradeinfo) {
            this.buytradeinfo = buytradeinfo;
        }

        public SellTradeInfo getSelltradeinfo() {
            return selltradeinfo;
        }

        public void setSelltradeinfo(SellTradeInfo selltradeinfo) {
            this.selltradeinfo = selltradeinfo;
        }

        @Override
        public String toString() {
            return "TrendInfo{" +
                    "tradedate='" + tradedate + '\'' +
                    ", fundChng='" + fundChng + '\'' +
                    ", hs300Chng='" + hs300Chng + '\'' +
                    ", buytradeinfo=" + buytradeinfo +
                    ", selltradeinfo=" + selltradeinfo +
                    '}';
        }
    }

    /**
     * 交易买点信息
     */
    public static class BuyTradeInfo {
        private String tradetype;   //0买入1卖出
        private String buyIndex;   //买入点下标

        public BuyTradeInfo() {
        }

        public String getTradetype() {
            return tradetype;
        }

        public void setTradetype(String tradetype) {
            this.tradetype = tradetype;
        }

        public String getBuyIndex() {
            return buyIndex;
        }

        public void setBuyIndex(String buyIndex) {
            this.buyIndex = buyIndex;
        }

        @Override
        public String toString() {
            return "BuyTradeInfo{" +
                    "tradetype='" + tradetype + '\'' +
                    ", buyIndex='" + buyIndex + '\'' +
                    '}';
        }
    }

    /**
     * 交易卖点信息
     */
    public static class SellTradeInfo {
        private String tradetype;   //0买入1卖出
        private String sellIndex;   //卖入点下标

        public SellTradeInfo() {
        }

        public String getTradetype() {
            return tradetype;
        }

        public void setTradetype(String tradetype) {
            this.tradetype = tradetype;
        }

        public String getSellIndex() {
            return sellIndex;
        }

        public void setSellIndex(String sellIndex) {
            this.sellIndex = sellIndex;
        }

        @Override
        public String toString() {
            return "SellTradeInfo{" +
                    "tradetype='" + tradetype + '\'' +
                    ", sellIndex='" + sellIndex + '\'' +
                    '}';
        }
    }
}
