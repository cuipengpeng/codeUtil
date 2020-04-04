package com.caishi.chaoge.bean;

public class ScenarioInfoBean {
    public int imgRes;
    public int imgSelectRes;
    public boolean isSelect = false;

    public ScenarioInfoBean(int imgRes, int imgSelectRes) {
        this.imgRes = imgRes;
        this.imgSelectRes = imgSelectRes;
    }
    public ScenarioInfoBean(int imgRes, int imgSelectRes,boolean isSelect) {
        this.imgRes = imgRes;
        this.imgSelectRes = imgSelectRes;
        this.isSelect = isSelect;
    }
}
