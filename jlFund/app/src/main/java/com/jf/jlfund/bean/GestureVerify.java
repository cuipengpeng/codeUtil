package com.jf.jlfund.bean;

/**
 *
 *
 */

public class GestureVerify {
    private String mobile;
    private long validTime;
    private String psd;
    private int countErrorNum=4;
    private String currentDate;

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public int getCountErrorNum() {
        return countErrorNum;
    }

    public void setCountErrorNum(int countErrorNum) {
        this.countErrorNum = countErrorNum;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public String getPsd() {
        return psd;
    }

    public void setPsd(String psd) {
        this.psd = psd;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public long getValidTime() {
        return validTime;
    }

    public void setValidTime(long validTime) {
        this.validTime = validTime;
    }
}
