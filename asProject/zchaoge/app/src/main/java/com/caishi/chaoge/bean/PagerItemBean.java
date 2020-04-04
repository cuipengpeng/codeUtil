package com.caishi.chaoge.bean;

/**
 * Created by moon.zhong on 2015/5/25.
 */
public class PagerItemBean {

    public int selectImg = 0;
    public int unSelectImg = 0;
    public String mContent;
    public boolean isSelect = false;

    public PagerItemBean(int selectImg, int unSelectImg, String mContent) {
        this.selectImg = selectImg;
        this.unSelectImg = unSelectImg;
        this.mContent = mContent;
    }

    public PagerItemBean(int selectImg, int unSelectImg) {
        this.selectImg = selectImg;
        this.unSelectImg = unSelectImg;
    }

    public PagerItemBean(String mContent) {
        this.mContent = mContent;
        this.isSelect = false;
    }

}
