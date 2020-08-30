package com.test.xcamera.bean;

/**
 * Created by zll on 2019/10/16.
 */

public class MoFPVValues {
    private String mName;
    private int mMax;
    private int mMin;
    private int mSelect;
    private int mAuto;

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public int getmMax() {
        return mMax;
    }

    public void setmMax(int mMax) {
        this.mMax = mMax;
    }

    public int getmMin() {
        return mMin;
    }

    public void setmMin(int mMin) {
        this.mMin = mMin;
    }

    public int getmSelect() {
        return mSelect;
    }

    public void setmSelect(int mSelect) {
        this.mSelect = mSelect;
    }

    public int getmAuto() {
        return mAuto;
    }

    public void setmAuto(int mAuto) {
        this.mAuto = mAuto;
    }

    @Override
    public String toString() {
        return "MoFPVValues{" +
                "mName='" + mName + '\'' +
                ", mMax=" + mMax +
                ", mMin=" + mMin +
                ", mSelect=" + mSelect +
                ", mAuto=" + mAuto +
                '}';
    }
}
