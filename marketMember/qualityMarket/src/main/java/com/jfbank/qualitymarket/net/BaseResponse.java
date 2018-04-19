package com.jfbank.qualitymarket.net;

/**
 * 功能：BaseResponse接口请求基类<br>
 * 作者：赵海<br>
 * 时间： 2017/4/18 0018<br>.
 * 版本：1.2.0
 */

public class BaseResponse {
    private String statusDetail;
    private int status;

    public String getStatusDetail() {
        return statusDetail;
    }

    public void setStatusDetail(String statusDetail) {
        this.statusDetail = statusDetail;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
