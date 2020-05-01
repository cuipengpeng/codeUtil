package com.jfbank.qualitymarket.model;

import java.util.List;

/**
 * 功能：<br>
 * 作者：赵海<br>
 * 时间： 2017/2/27 0027<br>.
 * 版本：1.2.0
 */

public class MsProductBean {
    private String activityNo;
    private String activityName;
    /**
     * productNo : pzsc1471857335684
     * monthAmount : 0.49
     * proImage : https://img13.360buyimg.com/n2/g14/M09/1E/1E/rBEhV1NDnuoIAAAAAAHlybMjigsAALiPgHKMxIAAeXh248.jpg
     * buyingPrice : 11.00
     * viceTitle :
     * originPrice : 719.00
     * remainderCount : 10
     * productName : 索尼（SONY） DSC-W830 数码相机 银色（2010万有效像素 8倍光学变焦 25mm广角 全景扫描）
     */

    private List<ProductListModel> productList;

    public String getActivityNo() {
        return activityNo;
    }

    public void setActivityNo(String activityNo) {
        this.activityNo = activityNo;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public List<ProductListModel> getProductList() {
        return productList;
    }

    public void setProductList(List<ProductListModel> productList) {
        this.productList = productList;
    }

    public static class ProductListModel {
        private String productNo;
        private String monthAmount;
        private String proImage;
        private String buyingPrice;
        private String viceTitle;
        private String originPrice;
        private String remainderCount;
        private String productName;

        public String getProductNo() {
            return productNo;
        }

        public void setProductNo(String productNo) {
            this.productNo = productNo;
        }

        public String getMonthAmount() {
            return monthAmount;
        }

        public void setMonthAmount(String monthAmount) {
            this.monthAmount = monthAmount;
        }

        public String getProImage() {
            return proImage;
        }

        public void setProImage(String proImage) {
            this.proImage = proImage;
        }

        public String getBuyingPrice() {
            return buyingPrice;
        }

        public void setBuyingPrice(String buyingPrice) {
            this.buyingPrice = buyingPrice;
        }

        public String getViceTitle() {
            return viceTitle;
        }

        public void setViceTitle(String viceTitle) {
            this.viceTitle = viceTitle;
        }

        public String getOriginPrice() {
            return originPrice;
        }

        public void setOriginPrice(String originPrice) {
            this.originPrice = originPrice;
        }

        public String getRemainderCount() {
            return remainderCount;
        }

        public void setRemainderCount(String remainderCount) {
            this.remainderCount = remainderCount;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }
    }
}
