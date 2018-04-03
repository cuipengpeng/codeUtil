package com.jf.jlfund.bean;

import java.util.List;

/**
 * Created by 55 on 2017/12/14.
 */

public class MakeMoneyBannerBean {

    private List<MaxBanner> maxBanner;
    private List<MinBanner> minBanner;

    public MakeMoneyBannerBean() {
    }

    public List<MaxBanner> getMaxBanner() {
        return maxBanner;
    }

    public void setMaxBanner(List<MaxBanner> maxBanner) {
        this.maxBanner = maxBanner;
    }

    public List<MinBanner> getMinBanner() {
        return minBanner;
    }

    public void setMinBanner(List<MinBanner> minBanner) {
        this.minBanner = minBanner;
    }

    @Override
    public String toString() {
        return "MakeMoneyBannerBean{" +
                "maxBanner=" + maxBanner +
                ", minBanner=" + minBanner +
                '}';
    }

    public static class MaxBanner{
        private String imgUrl;
        private String addressUrl;
        private String isForWhite;
        private String skipType;
        private String isForAccount;
        private String htmlTitle;

        public MaxBanner() {
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public String getAddressUrl() {
            return addressUrl;
        }

        public void setAddressUrl(String addressUrl) {
            this.addressUrl = addressUrl;
        }

        public String getIsForWhite() {
            return isForWhite;
        }

        public void setIsForWhite(String isForWhite) {
            this.isForWhite = isForWhite;
        }

        public String getSkipType() {
            return skipType;
        }

        public void setSkipType(String skipType) {
            this.skipType = skipType;
        }

        public String getIsForAccount() {
            return isForAccount;
        }

        public void setIsForAccount(String isForAccount) {
            this.isForAccount = isForAccount;
        }

        public String getHtmlTitle() {
            return htmlTitle;
        }

        public void setHtmlTitle(String htmlTitle) {
            this.htmlTitle = htmlTitle;
        }

        @Override
        public String toString() {
            return "MaxBanner{" +
                    "imgUrl='" + imgUrl + '\'' +
                    ", addressUrl='" + addressUrl + '\'' +
                    ", isForWhite='" + isForWhite + '\'' +
                    ", skipType='" + skipType + '\'' +
                    ", isForAccount='" + isForAccount + '\'' +
                    ", htmlTitle='" + htmlTitle + '\'' +
                    '}';
        }
    }

    public static class MinBanner{
        private String imgUrl;
        private String addressUrl;
        private String isForWhite;
        private String skipType;
        private String isForAccount;
        private String htmlTitle;

        public MinBanner() {
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public String getAddressUrl() {
            return addressUrl;
        }

        public void setAddressUrl(String addressUrl) {
            this.addressUrl = addressUrl;
        }

        public String getIsForWhite() {
            return isForWhite;
        }

        public void setIsForWhite(String isForWhite) {
            this.isForWhite = isForWhite;
        }

        public String getSkipType() {
            return skipType;
        }

        public void setSkipType(String skipType) {
            this.skipType = skipType;
        }

        public String getIsForAccount() {
            return isForAccount;
        }

        public void setIsForAccount(String isForAccount) {
            this.isForAccount = isForAccount;
        }

        public String getHtmlTitle() {
            return htmlTitle;
        }

        public void setHtmlTitle(String htmlTitle) {
            this.htmlTitle = htmlTitle;
        }

        @Override
        public String toString() {
            return "MinBanner{" +
                    "imgUrl='" + imgUrl + '\'' +
                    ", addressUrl='" + addressUrl + '\'' +
                    ", isForWhite='" + isForWhite + '\'' +
                    ", skipType='" + skipType + '\'' +
                    ", isForAccount='" + isForAccount + '\'' +
                    ", htmlTitle='" + htmlTitle + '\'' +
                    '}';
        }
    }
}
