package com.jfbank.qualitymarket.net;

import com.jfbank.qualitymarket.model.SaveBaseInfoBean;

/**
 * 功能：接口请求返回对象<br>
 * 作者：赵海<br>
 * 时间： 2017/4/14 0014<br>.
 * 版本：1.2.0
 */

public class Response<T> extends BaseResponse {

    private T data;
    private int pageCount;
    private int pageNo;
    //秒杀返回结果
    private int buynum;
    private int diffSeconds;
    private String function;

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getBuynum() {
        return buynum;
    }

    public void setBuynum(int buynum) {
        this.buynum = buynum;
    }

    public int getDiffSeconds() {
        return diffSeconds;
    }

    public void setDiffSeconds(int diffSeconds) {
        this.diffSeconds = diffSeconds;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
