package com.jfbank.qualitymarket.bean;

import android.text.TextUtils;

import java.util.ArrayList;

/**
 * 作者：Rainbean on 2016/11/11 0011 16:35
 * <p>
 * 邮箱：rainbean@126.com
 */

public class CategoryLevel1Bean
{

    private String upCategoryName;
    private String picUrl;
    private String name;
    private String h5Url;
    private ArrayList<CategoryLevel2Bean> labelList;
    private String upCategoryImage;
    private String upCategoroyType;
    private String appPage;
    private String appParams;

    public String getUpCategoryName() {
        return upCategoryName;
    }

    public void setUpCategoryName(String upCategoryName) {
        this.upCategoryName = upCategoryName;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getH5Url() {
        return h5Url;
    }

    public void setH5Url(String h5Url) {
        this.h5Url = h5Url;
    }

    public ArrayList<CategoryLevel2Bean> getLabelList() {
        return labelList;
    }

    public void setLabelList(ArrayList<CategoryLevel2Bean> labelList) {
        this.labelList = labelList;
    }

    public String getUpCategoryImage() {
        return upCategoryImage;
    }

    public void setUpCategoryImage(String upCategoryImage) {
        this.upCategoryImage = upCategoryImage;
    }

    public String getUpCategoroyType() {
        return upCategoroyType;
    }

    public void setUpCategoroyType(String upCategoroyType) {
        this.upCategoroyType = upCategoroyType;
    }

    public String getAppPage() {
        return appPage;
    }

    public void setAppPage(String appPage) {
        this.appPage = appPage;
    }

    public String getAppParams() {
        return appParams;
    }

    public void setAppParams(String appParams) {
        this.appParams = appParams;
    }

    @Override
    public boolean equals(Object obj) {
        return TextUtils.equals(upCategoroyType,((CategoryLevel1Bean)obj).upCategoroyType);
    }
}
