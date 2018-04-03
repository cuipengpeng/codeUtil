package com.jfbank.qualitymarket.model;

/**
 * 极光推送过来的消息
 * Created by ${彭爱军} on 2016/12/5.
 */

public class JpushMessageBean {
    private String goview;              // 页面跳转，orderdetail订单详情， mycenter  个人中心  CLASSIFY品类页     WEBONLY web页面
    private String orderId;             //订单需要的id
    private String redbagId;
    private String url;
    private String classifyID ;         //品类页

    public JpushMessageBean() {
    }

    public JpushMessageBean(String goview, String orderId, String redbagId, String url, String classifyID) {
        this.goview = goview;
        this.orderId = orderId;
        this.redbagId = redbagId;
        this.url = url;
        this.classifyID = classifyID;
    }

    public String getClassifyID() {
        return classifyID;
    }

    public void setClassifyID(String classifyID) {
        this.classifyID = classifyID;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGoview() {
        return goview;
    }

    public void setGoview(String goview) {
        this.goview = goview;
    }

    public String getRedbagId() {
        return redbagId;
    }

    public void setRedbagId(String redbagId) {
        this.redbagId = redbagId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "JpushMessageBean{" +
                "goview='" + goview + '\'' +
                ", orderId='" + orderId + '\'' +
                ", redbagId='" + redbagId + '\'' +
                '}';
    }
}
