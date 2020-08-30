package com.test.xcamera.bean;

import java.util.ArrayList;

/**
 * Created by zll on 2019/10/16.
 */

public class MoFPVParameter {
    private boolean mAuto = false;
    private ArrayList<MoFPVValues> moFPVValues;

    public boolean getmAuto() {
        return mAuto;
    }

    public void setmAuto(boolean mAuto) {
        this.mAuto = mAuto;
    }

    @Override
    public String toString() {
        return "MoFPVParameter{" +
                "mAuto=" + mAuto +
                ", moFPVValues=" + moFPVValues +
                '}';
    }

    public ArrayList<MoFPVValues> getMoFPVValues() {
        return moFPVValues;
    }

    public void setMoFPVValues(ArrayList<MoFPVValues> moFPVValues) {
        this.moFPVValues = moFPVValues;
    }
}
