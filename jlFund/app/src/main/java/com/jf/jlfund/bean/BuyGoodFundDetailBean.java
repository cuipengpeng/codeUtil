package com.jf.jlfund.bean;

import java.util.List;

/**
 * Created by 55 on 2017/12/15.
 */

public class BuyGoodFundDetailBean {
    private Integer id;
    private String imgUrl;
    private String reason;
    private String title;
    private String secondTitle;
    private String innerTitle;
    private String wealthType;
    private String wealthTypeName;
    private List<BuyGoodFundDetailInnerFundBean> fundList;

    public BuyGoodFundDetailBean() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSecondTitle() {
        return secondTitle;
    }

    public void setSecondTitle(String secondTitle) {
        this.secondTitle = secondTitle;
    }

    public String getInnerTitle() {
        return innerTitle;
    }

    public void setInnerTitle(String innerTitle) {
        this.innerTitle = innerTitle;
    }

    public String getWealthType() {
        return wealthType;
    }

    public void setWealthType(String wealthType) {
        this.wealthType = wealthType;
    }

    public String getWealthTypeName() {
        return wealthTypeName;
    }

    public void setWealthTypeName(String wealthTypeName) {
        this.wealthTypeName = wealthTypeName;
    }

    public List<BuyGoodFundDetailInnerFundBean> getFundList() {
        return fundList;
    }

    public void setFundList(List<BuyGoodFundDetailInnerFundBean> fundList) {
        this.fundList = fundList;
    }

    @Override
    public String toString() {
        return "BuyGoodFundDetailBean{" +
                "id=" + id +
                ", imgUrl='" + imgUrl + '\'' +
                ", reason='" + reason + '\'' +
                ", title='" + title + '\'' +
                ", secondTitle='" + secondTitle + '\'' +
                ", innerTitle='" + innerTitle + '\'' +
                ", wealthType='" + wealthType + '\'' +
                ", wealthTypeName='" + wealthTypeName + '\'' +
                ", fundList=" + fundList +
                '}';
    }

    public static class BuyGoodFundDetailInnerFundBean {
        private String profit;   //收益
        private String fund_code;
        private String fundstate;
        private String fundsname;
        private Integer declarestate;

        public BuyGoodFundDetailInnerFundBean() {
        }

        public String getProfit() {
            return profit;
        }

        public void setProfit(String profit) {
            this.profit = profit;
        }

        public String getFund_code() {
            return fund_code;
        }

        public void setFund_code(String fund_code) {
            this.fund_code = fund_code;
        }

        public String getFundstate() {
            return fundstate;
        }

        public void setFundstate(String fundstate) {
            this.fundstate = fundstate;
        }

        public String getFundsname() {
            return fundsname;
        }

        public void setFundsname(String fundsname) {
            this.fundsname = fundsname;
        }

        public Integer getDeclarestate() {
            return declarestate;
        }

        public void setDeclarestate(Integer declarestate) {
            this.declarestate = declarestate;
        }

        @Override
        public String toString() {
            return "BuyGoodFundDetailInnerFundBean{" +
                    "profit=" + profit +
                    ", fund_code='" + fund_code + '\'' +
                    ", fundstate=" + fundstate +
                    ", fundsname='" + fundsname + '\'' +
                    ", declarestate=" + declarestate +
                    '}';
        }
    }
}
