package com.jfbank.qualitymarket.bean;

public class CategoryLevel3Bean {
    private String modelType;// 标签类型（0：普通标签，1：二级商品类别标签）,
    private String name;
    private String image;
    private String keyWord;

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }
}
