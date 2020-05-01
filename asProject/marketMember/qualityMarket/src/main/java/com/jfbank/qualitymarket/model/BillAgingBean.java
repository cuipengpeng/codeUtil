package com.jfbank.qualitymarket.model;

import java.util.List;

/**
 * 功能：<br>
 * 作者：赵海<br>
 * 时间： 2016/12/30 0030<br>.
 * 版本：1.2.0
 */

public class BillAgingBean {
    private String statusDetail;//	接口请求状态文字描述	N
    private String orderTime;//          下单时间
    private String productName;//          商品名称
    private String orderId;//                订单编号
    private String fenqibenjin;//	            分期本金
    private String fenqifuwufei;//	分期服务费
    private String hejiyinghuan;//	合计应还
    private String yihuan;//	已还
    private String weihuan;//   	未还
    private List<AgingBean> data;

    public List<AgingBean> getData() {
        return data;
    }

    public void setData(List<AgingBean> data) {
        this.data = data;
    }

    public String getStatusDetail() {
        return statusDetail;
    }

    public void setStatusDetail(String statusDetail) {
        this.statusDetail = statusDetail;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getFenqibenjin() {
        return fenqibenjin;
    }

    public void setFenqibenjin(String fenqibenjin) {
        this.fenqibenjin = fenqibenjin;
    }

    public String getFenqifuwufei() {
        return fenqifuwufei;
    }

    public void setFenqifuwufei(String fenqifuwufei) {
        this.fenqifuwufei = fenqifuwufei;
    }

    public String getHejiyinghuan() {
        return hejiyinghuan;
    }

    public void setHejiyinghuan(String hejiyinghuan) {
        this.hejiyinghuan = hejiyinghuan;
    }

    public String getYihuan() {
        return yihuan;
    }

    public void setYihuan(String yihuan) {
        this.yihuan = yihuan;
    }

    public String getWeihuan() {
        return weihuan;
    }

    public void setWeihuan(String weihuan) {
        this.weihuan = weihuan;
    }
}
