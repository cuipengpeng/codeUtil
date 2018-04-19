package com.jfbank.qualitymarket.model;

/**
 * @author 崔朋朋
 */
public class ResponseBean  {
    int status;//服务器端响应状态码
    String statusDetail;//状态详情

    public ResponseBean() {
        super();
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


}
