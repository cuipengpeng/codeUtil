package com.jfbank.qualitymarket.bean;

import java.util.ArrayList;

public class CategoryLevel2Bean {
    private String tagType;
    private String tagName;
    private ArrayList<CategoryLevel3Bean> taglistData;
    private String type;//标签类型（0：普通标签，1：二级商品类别标签）0按keyword来搜索,  1 按modelType来搜索
    private String upTagType;

    public String getTagType() {
        return tagType;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public ArrayList<CategoryLevel3Bean> getTaglistData() {
        return taglistData;
    }

    public void setTaglistData(ArrayList<CategoryLevel3Bean> taglistData) {
        this.taglistData = taglistData;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUpTagType() {
        return upTagType;
    }

    public void setUpTagType(String upTagType) {
        this.upTagType = upTagType;
    }
}
