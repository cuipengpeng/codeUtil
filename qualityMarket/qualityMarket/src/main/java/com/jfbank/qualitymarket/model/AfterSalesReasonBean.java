package com.jfbank.qualitymarket.model;

import java.io.Serializable;
import java.util.List;

/**
 * 功能：售后原因<br>
 * 作者：赵海<br>
 * 时间： 2017/4/21 0021<br>.
 * 版本：1.2.0
 */

public class AfterSalesReasonBean implements Serializable {

    private  int typeOfSupport;

    public int getTypeOfSupport() {
        return typeOfSupport;
    }

    public void setTypeOfSupport(int typeOfSupport) {
        this.typeOfSupport = typeOfSupport;
    }

    /**
     * parameterDes : 7天无理由
     * isOrNotDefault : 0
     */

    private List<ExchangeLcaModel> exchangeLca;
    /**
     * parameterDes : 7天无理由
     * isOrNotDefault : 1
     */

    private List<RefundLcaModel> refundLca;

    public List<ExchangeLcaModel> getExchangeLca() {
        return exchangeLca;
    }

    public void setExchangeLca(List<ExchangeLcaModel> exchangeLca) {
        this.exchangeLca = exchangeLca;
    }

    public List<RefundLcaModel> getRefundLca() {
        return refundLca;
    }

    public void setRefundLca(List<RefundLcaModel> refundLca) {
        this.refundLca = refundLca;
    }

    public static class ExchangeLcaModel implements Serializable {
        private String parameterDes;
        private String isOrNotDefault;

        public String getParameterDes() {
            return parameterDes;
        }

        public void setParameterDes(String parameterDes) {
            this.parameterDes = parameterDes;
        }

        public String getIsOrNotDefault() {
            return isOrNotDefault;
        }

        public void setIsOrNotDefault(String isOrNotDefault) {
            this.isOrNotDefault = isOrNotDefault;
        }
    }

    public static class RefundLcaModel implements Serializable {
        private String parameterDes;
        private String isOrNotDefault;

        public String getParameterDes() {
            return parameterDes;
        }

        public void setParameterDes(String parameterDes) {
            this.parameterDes = parameterDes;
        }

        public String getIsOrNotDefault() {
            return isOrNotDefault;
        }

        public void setIsOrNotDefault(String isOrNotDefault) {
            this.isOrNotDefault = isOrNotDefault;
        }
    }
}
