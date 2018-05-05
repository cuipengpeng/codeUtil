package com.test.bank.bean;

import com.test.bank.base.BaseBean;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2017/12/12<br>
*/
public class OptionalFundBean extends BaseBean{


    private long collectTime;
    private String daygrowth;
    private String fundCode;
    private String fundName;
    private String nav;

    public long getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(long collectTime) {
        this.collectTime = collectTime;
    }

    public String getDaygrowth() {
        return daygrowth;
    }

    public void setDaygrowth(String daygrowth) {
        this.daygrowth = daygrowth;
    }

    public String getFundCode() {
        return fundCode;
    }

    public void setFundCode(String fundCode) {
        this.fundCode = fundCode;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public String getNav() {
        return nav;
    }

    public void setNav(String nav) {
        this.nav = nav;
    }



}
