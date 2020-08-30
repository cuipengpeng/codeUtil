package com.test.xcamera.bean;

import org.json.JSONObject;

/**
 * 矩形
 * Created by zll on 2019/7/3.
 */

public class MoRect extends MoData {
    private int mX;
    private int mY;
    private int mWidth;
    private int mHeight;

    public int getmX() {
        return mX;
    }

    public void setmX(int mX) {
        this.mX = mX;
    }

    public int getmY() {
        return mY;
    }

    public void setmY(int mY) {
        this.mY = mY;
    }

    public int getmWidth() {
        return mWidth;
    }

    public void setmWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public int getmHeight() {
        return mHeight;
    }

    public void setmHeight(int mHeight) {
        this.mHeight = mHeight;
    }

    public static MoRect parse(JSONObject jsonObject) {
        MoRect rect = new MoRect();
        rect.parseData(jsonObject);
        return rect;
    }

    @Override
    protected void parseData(JSONObject jsonObject) {
        super.parseData(jsonObject);

        try {
            mX = jsonObject.optInt("x");
        } catch (Exception e){}

        try {
            mY = jsonObject.optInt("y");
        } catch (Exception e){}

        try {
            mWidth = jsonObject.optInt("width");
        } catch (Exception e){}

        try {
            mHeight = jsonObject.optInt("height");
        } catch (Exception e){}
    }

    @Override
    public String toString() {
        return "MoRect{" +
                "mX=" + mX +
                ", mY=" + mY +
                ", mWidth=" + mWidth +
                ", mHeight=" + mHeight +
                '}';
    }
}
