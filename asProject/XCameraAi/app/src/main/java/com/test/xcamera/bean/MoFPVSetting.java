package com.test.xcamera.bean;

/**
 * FPV拍摄参数设置
 * Created by zll on 2019/10/15.
 */

public class MoFPVSetting {
    private int mType; // 0:has parameter  1:no parameter
    private int mSelect = 1; // 0:selected  1:unselected
    private String mTitle;
    private int mResourceID;
    private MoFPVParameter moFPVParameter;
    private boolean mVisiable;

    /**
     * 是否展示分割线
     * */
    public boolean mShowSplit;

    public int getmType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public int getmResourceID() {
        return mResourceID;
    }

    public void setmResourceID(int mResourceID) {
        this.mResourceID = mResourceID;
    }

    public int getResourceID() {
        return mResourceID;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getmSelect() {
        return mSelect;
    }

    public void setmSelect(int mSelect) {
        this.mSelect = mSelect;
    }

    public MoFPVParameter getMoFPVParameter() {
        return moFPVParameter;
    }

    public void setMoFPVParameter(MoFPVParameter moFPVParameter) {
        this.moFPVParameter = moFPVParameter;
    }

    public boolean ismVisiable() {
        return mVisiable;
    }

    public void setmVisiable(boolean mVisiable) {
        this.mVisiable = mVisiable;
    }

    @Override
    public String toString() {
        return "MoFPVSetting{" +
                "mType=" + mType +
                ", mSelect=" + mSelect +
                ", mTitle='" + mTitle + '\'' +
                ", mResourceID=" + mResourceID +
                ", moFPVParameter=" + moFPVParameter +
                ", mVisiable=" + mVisiable +
                '}';
    }
}
