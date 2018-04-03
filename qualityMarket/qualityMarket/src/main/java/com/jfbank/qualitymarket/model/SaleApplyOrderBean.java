package com.jfbank.qualitymarket.model;

import java.io.Serializable;

/**
 * 功能：SaleApplyOrderBean售后可申请对象<br>
 * 作者：赵海<br>
 * 时间： 2017/4/20 0020<br>.
 * 版本：1.2.0
 */

public class SaleApplyOrderBean  implements Serializable{
    String firstPayment;//BigDecimal	首付	N
    String productPrice;//BigDecimal	商品价格
    String productName;//String	商品名称
    String productImage;//String	商品图片
    int buycount;//String	商品数目
    String monthPay;//BigDecimal	月供	N
    String monthNumber;//int	分期月数	N
    String orderTime;//String	下单时间	N
    int aftermarketButton;//	String	         按钮状态	N	0：申请售后按钮置灰 1：退款成功按钮置灰  2：申请售后按钮可点击
    int orderStatus;//String	订单状态
    String orderId;//String	订单ID
    private String skuParameters;//

    public String getSkuParameters() {
        return skuParameters;
    }

    public void setSkuParameters(String skuParameters) {
        this.skuParameters = skuParameters;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public int getBuycount() {
        return buycount;
    }

    public void setBuycount(int buycount) {
        this.buycount = buycount;
    }

    public String getFirstPayment() {
        return firstPayment;
    }

    public void setFirstPayment(String firstPayment) {
        this.firstPayment = firstPayment;
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

    public String getMonthPay() {
        return monthPay;
    }

    public void setMonthPay(String monthPay) {
        this.monthPay = monthPay;
    }

    public String getMonthNumber() {
        return monthNumber;
    }

    public void setMonthNumber(String monthNumber) {
        this.monthNumber = monthNumber;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public int getAftermarketButton() {
        return aftermarketButton;
    }

    public void setAftermarketButton(int aftermarketButton) {
        this.aftermarketButton = aftermarketButton;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
