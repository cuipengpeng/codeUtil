package com.jfbank.qualitymarket.model;

import java.util.ArrayList;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2017/3/3<br>
*/
public class DiscoverBean {


    private String QualityDiscoveryName;//发现名称
    private ArrayList<QualityDiscoveryBanner> QualityDiscoveryBanner;//列表—发现object

    public static class QualityDiscoveryBanner {
        private String id;
        private String discoveryId;
        private String type;//'类型' 0商品详情, 商品编号放在url字段中   1 网址
        private String url;//点击地址链接
        private String picture;//图片地址
        private String sortNum;
        private String isLogin; //是否需要登录  0 ：不需要登陆，1：登陆
        private String realName;//是否实名 0 ：不实名，实名：1，

        //
        private String onlineTime;
        private String name;
        private String image;
        private String offlineTime;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getOfflineTime() {
            return offlineTime;
        }

        public void setOfflineTime(String offlineTime) {
            this.offlineTime = offlineTime;
        }

        public String getOnlineTime() {
            return onlineTime;
        }

        public void setOnlineTime(String onlineTime) {
            this.onlineTime = onlineTime;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDiscoveryId() {
            return discoveryId;
        }

        public void setDiscoveryId(String discoveryId) {
            this.discoveryId = discoveryId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }

        public String getSortNum() {
            return sortNum;
        }

        public void setSortNum(String sortNum) {
            this.sortNum = sortNum;
        }

        public String getIsLogin() {
            return isLogin;
        }

        public void setIsLogin(String isLogin) {
            this.isLogin = isLogin;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }
    }

    public String getQualityDiscoveryName() {
        return QualityDiscoveryName;
    }

    public void setQualityDiscoveryName(String QualityDiscoveryName) {
        this.QualityDiscoveryName = QualityDiscoveryName;
    }

    public ArrayList<QualityDiscoveryBanner> getQualityDiscoveryBanner() {
        return QualityDiscoveryBanner;
    }

    public void setQualityDiscoveryBanner(ArrayList<QualityDiscoveryBanner> QualityDiscoveryBanner) {
        this.QualityDiscoveryBanner = QualityDiscoveryBanner;
    }



}
