package com.jfbank.qualitymarket.model;

import java.io.Serializable;

/**
 * 功能：退换货售后进度查询列表对象<br>
 * 作者：赵海<br>
 * 时间： 2017/4/20 0020<br>.
 * 版本：1.4.3
 */

public class SalesProgressBean implements Serializable {

    String createTime;//Date	申请时间
    String firstPayment;//BigDecimal	首付
    int identification;//String	退货或换货标识		1：退货2：换货
    int refundOrderStatus;//	String	退换货状态		如果identification为1（即退货）订单状态 0-审核退货中, 1-待寄送商品, 2-拒绝退货  3-退货受理中 4-退货完成 5-退款完成，6-已取消 如果identification为2（即换货）订单状态 0-换货审核中, 1-待寄送换货, 2-拒绝换货, 3-待收取换货, 4-换货完成,5-已取消,6-待商户发货,7-待买家收货

    String productImage;//	String	商品图片
    String monthPay;//	BigDecimal	月供
    int cancelButtonDisplay;//String	取消申请按钮是否显示		取消申请按钮是否显示 0 不显示  1 显示
    String productPrice;//	BigDecimal	商品价格
    String productName;//	String	商品名称
    String refundOrderId;//String	退换货订单号
    int monthnum;//int	分期月数
    String originalOrderId;//商品订单ID;
    String  orderId;
    private String skuParameters;//

    public String getSkuParameters() {
        return skuParameters;
    }

    public void setSkuParameters(String skuParameters) {
        this.skuParameters = skuParameters;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOriginalOrderId() {
        return originalOrderId;
    }

    public void setOriginalOrderId(String originalOrderId) {
        this.originalOrderId = originalOrderId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getFirstPayment() {
        return firstPayment;
    }

    public void setFirstPayment(String firstPayment) {
        this.firstPayment = firstPayment;
    }

    public int getIdentification() {
        return identification;
    }

    public void setIdentification(int identification) {
        this.identification = identification;
    }

    public int getRefundOrderStatus() {
        return refundOrderStatus;
    }

    public void setRefundOrderStatus(int refundOrderStatus) {
        this.refundOrderStatus = refundOrderStatus;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getMonthPay() {
        return monthPay;
    }

    public void setMonthPay(String monthPay) {
        this.monthPay = monthPay;
    }

    public int getCancelButtonDisplay() {
        return cancelButtonDisplay;
    }

    public void setCancelButtonDisplay(int cancelButtonDisplay) {
        this.cancelButtonDisplay = cancelButtonDisplay;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getRefundOrderId() {
        return refundOrderId;
    }

    public void setRefundOrderId(String refundOrderId) {
        this.refundOrderId = refundOrderId;
    }

    public int getMonthnum() {
        return monthnum;
    }

    public void setMonthnum(int monthnum) {
        this.monthnum = monthnum;
    }
}
