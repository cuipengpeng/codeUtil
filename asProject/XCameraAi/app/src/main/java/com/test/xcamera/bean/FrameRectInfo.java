package com.test.xcamera.bean;

import android.support.annotation.NonNull;

/**
 * Created by zll on 2019/6/12.
 */

public class FrameRectInfo implements Comparable<FrameRectInfo>{
    private int mID;
    private int status;
    private String mStartX;
    private String mStatrY;
    private String mEndX;
    private String mEndY;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getmID() {
        return mID;
    }

    public void setmID(int mID) {
        this.mID = mID;
    }

    public String getmStartX() {
        return mStartX;
    }

    public void setmStartX(String mStartX) {
        this.mStartX = mStartX;
    }

    public String getmStartY() {
        return mStatrY;
    }

    public void setmStartY(String mStatrY) {
        this.mStatrY = mStatrY;
    }

    public String getmEndX() {
        return mEndX;
    }

    public void setmEndX(String mEndX) {
        this.mEndX = mEndX;
    }

    public String getmEndY() {
        return mEndY;
    }

    public void setmEndY(String mEndY) {
        this.mEndY = mEndY;
    }

    @Override
    public String toString() {
        return "FrameRectInfo{" +
                "mID=" + mID +
                ", status=" + status +
                ", mStartX='" + mStartX + '\'' +
                ", mStatrY='" + mStatrY + '\'' +
                ", mEndX='" + mEndX + '\'' +
                ", mEndY='" + mEndY + '\'' +
                '}';
    }

    //    public  int  getArea(){
//        int  wid= Math.abs(mStartX-mEndX);
//        int  high=Math.abs(mStatrY-mEndY);
//        return  wid*high;
//    }
//
    @Override
    public int compareTo(@NonNull FrameRectInfo o) {

//        int temp=this.getArea()-o.getArea();
//        if(temp>0){
//            return 1;
//        }else if(temp<0){
//            return  -1;
//        }
        return 0 ;
    }
}
