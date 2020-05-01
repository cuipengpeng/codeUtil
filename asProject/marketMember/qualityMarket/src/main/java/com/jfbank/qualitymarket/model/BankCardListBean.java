package com.jfbank.qualitymarket.model;

import java.util.List;

/**
 * 功能：<br>
 * 作者：赵海<br>
 * 时间： 2017/8/2 0002<br>.
 * 版本：1.2.0
 */

public class BankCardListBean {
    /**
     * creditline : 1
     * status : 1
     * statusDetail : 操作成功
     * data : [{"id":"0","uid":"0","uname":"","bankName":"","branchName":"","bankCardNum":"6214830282198109","bankCode":"","areaCode":"","areaName":"","provCode":"","provName":"","channel":"wanka","useTime":"","delFlag":"0","ybBindStatus":"0","ybRequestid":"","ybIdentityid":"","bankphone":"17600209794","idNumber":"","type":"0"}]
     * idName : 赵海
     * function : bankcardshownew
     * idNumber : 510824198909242556
     */

    private String creditline;
    private int status;
    private String statusDetail;
    private String idName;
    private String function;
    private String idNumber;
    /**
     * id : 0
     * uid : 0
     * uname :
     * bankName :
     * branchName :
     * bankCardNum : 6214830282198109
     * bankCode :
     * areaCode :
     * areaName :
     * provCode :
     * provName :
     * channel : wanka
     * useTime :
     * delFlag : 0
     * ybBindStatus : 0
     * ybRequestid :
     * ybIdentityid :
     * bankphone : 17600209794
     * idNumber :
     * type : 0
     */

    private List<BankCard> data;

    public String getCreditline() {
        return creditline;
    }

    public void setCreditline(String creditline) {
        this.creditline = creditline;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusDetail() {
        return statusDetail;
    }

    public void setStatusDetail(String statusDetail) {
        this.statusDetail = statusDetail;
    }

    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public List<BankCard> getData() {
        return data;
    }

    public void setData(List<BankCard> data) {
        this.data = data;
    }
}
