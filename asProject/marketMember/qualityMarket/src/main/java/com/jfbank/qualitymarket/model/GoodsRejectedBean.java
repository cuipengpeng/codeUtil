package com.jfbank.qualitymarket.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2017/4/20<br>
*/
public class GoodsRejectedBean implements Serializable {

    public QualityRefundOrderMap qualityRefundOrderMap;

    public List<GoodsRejectedBean.LogisticsLca> getLogisticsLca() {
        return LogisticsLca;
    }

    public void setLogisticsLca(List<GoodsRejectedBean.LogisticsLca> logisticsLca) {
        LogisticsLca = logisticsLca;
    }

    public List<LogisticsLca> LogisticsLca;

    public static class LogisticsLca implements Serializable {
        String parameterDes;//"全峰快递"
        String parameterName;//QFKD

        public String getParameterDes() {
            return parameterDes;
        }

        public void setParameterDes(String parameterDes) {
            this.parameterDes = parameterDes;
        }

        public String getParameterName() {
            return parameterName;
        }

        public void setParameterName(String parameterName) {
            this.parameterName = parameterName;
        }
    }

    public static class QualityRefundOrderMap implements Serializable{
        private ArrayList<Data> data;//物流信息
        private String freight; //运费
        private String deliveryDate; //发货时间
        private String express;//快递公司
        private String logisticCode;//物流单号
        private String countDownDays;//倒计时天
        private String returnOrderId;//退货单编号
        private String refundAmount;//退款金额
        private ArrayList<String> pictureArray;//图片数组
        private String reason;//退换货原因
        private String refusedReason;//商家拒绝理由
        private int identification;//退换货标识    1即退货   2即换货
        private String applyTime;//申请时间
        private String countDownHours;//倒计时小时
        private String picCount;//图片数量
        private String explain;//说明
        private int orderStatus;//订单状态
        private String recipientPhone;// 收货人电话
        private String recipientName;// 收货人姓名
        private String addressInfo;// 地址信息
        private String cancelDesc;//关闭原因
        private String finishTime;//关闭时间
        private String     zipCode;//邮政编码
        private String refundTime;//退款时间

        public String getRefundTime() {
            return refundTime;
        }

        public void setRefundTime(String refundTime) {
            this.refundTime = refundTime;
        }

        public String getZipCode() {
            return zipCode;
        }

        public void setZipCode(String zipCode) {
            this.zipCode = zipCode;
        }

        public String getRefusedReason() {
            return refusedReason;
        }

        public void setRefusedReason(String refusedReason) {
            this.refusedReason = refusedReason;
        }
        public String getCancelDesc() {
            return cancelDesc;
        }

        public void setCancelDesc(String cancelDesc) {
            this.cancelDesc = cancelDesc;
        }


        public String getFinishTime() {
            return finishTime;
        }

        public void setFinishTime(String finishTime) {
            this.finishTime = finishTime;
        }


        public ArrayList<Data> getData() {
            return data;
        }

        public void setData(ArrayList<Data> data) {
            this.data = data;
        }

        public String getFreight() {
            return freight;
        }

        public void setFreight(String freight) {
            this.freight = freight;
        }

        public String getDeliveryDate() {
            return deliveryDate;
        }

        public void setDeliveryDate(String deliveryDate) {
            this.deliveryDate = deliveryDate;
        }

        public String getExpress() {
            return express;
        }

        public void setExpress(String express) {
            this.express = express;
        }

        public String getLogisticCode() {
            return logisticCode;
        }

        public void setLogisticCode(String logisticCode) {
            this.logisticCode = logisticCode;
        }

        public String getRecipientPhone() {
            return recipientPhone;
        }

        public void setRecipientPhone(String recipientPhone) {
            this.recipientPhone = recipientPhone;
        }

        public String getRecipientName() {
            return recipientName;
        }

        public void setRecipientName(String recipientName) {
            this.recipientName = recipientName;
        }

        public String getAddressInfo() {
            return addressInfo;
        }

        public void setAddressInfo(String addressInfo) {
            this.addressInfo = addressInfo;
        }

        public String getCountDownDays() {
            return countDownDays;
        }

        public void setCountDownDays(String countDownDays) {
            this.countDownDays = countDownDays;
        }

        public String getReturnOrderId() {
            return returnOrderId;
        }

        public void setReturnOrderId(String returnOrderId) {
            this.returnOrderId = returnOrderId;
        }

        public String getRefundAmount() {
            return refundAmount;
        }

        public void setRefundAmount(String refundAmount) {
            this.refundAmount = refundAmount;
        }

        public ArrayList<String> getPictureArray() {
            return pictureArray;
        }

        public void setPictureArray(ArrayList<String> pictureArray) {
            this.pictureArray = pictureArray;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public int getIdentification() {
            return identification;
        }

        public void setIdentification(int identification) {
            this.identification = identification;
        }

        public String getApplyTime() {
            return applyTime;
        }

        public void setApplyTime(String applyTime) {
            this.applyTime = applyTime;
        }

        public String getCountDownHours() {
            return countDownHours;
        }

        public void setCountDownHours(String countDownHours) {
            this.countDownHours = countDownHours;
        }

        public String getPicCount() {
            return picCount;
        }

        public void setPicCount(String picCount) {
            this.picCount = picCount;
        }

        public String getExplain() {
            return explain;
        }

        public void setExplain(String explain) {
            this.explain = explain;
        }

        public int getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(int orderStatus) {
            this.orderStatus = orderStatus;
        }
    }

    public QualityRefundOrderMap getQualityRefundOrderMap() {
        return qualityRefundOrderMap;
    }

    public void setQualityRefundOrderMap(QualityRefundOrderMap qualityRefundOrderMap) {
        this.qualityRefundOrderMap = qualityRefundOrderMap;
    }

    public static class Data {
        private String content;//物流信息内容
        private String msgTime;//信息时间
        private String operator;//操作人

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getMsgTime() {
            return msgTime;
        }

        public void setMsgTime(String msgTime) {
            this.msgTime = msgTime;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }
    }

}


