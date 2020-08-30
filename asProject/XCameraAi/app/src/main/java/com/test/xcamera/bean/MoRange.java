package com.test.xcamera.bean;

import org.json.JSONObject;

/**
 * 范围
 * Created by zll on 2019/7/3.
 */

public class MoRange extends MoData{
    private long mOffset;
    //文件长度，暂定最大值256k
    private int mLength;

    public long getmOffset() {
        return mOffset;
    }

    public void setmOffset(long mOffset) {
        this.mOffset = mOffset;
    }

    public int getmLength() {
        return mLength;
    }

    public void setmLength(int mLength) {
        this.mLength = mLength;
    }

    public static MoRange parse(JSONObject jsonObject) {
        MoRange range = new MoRange();
        range.parseData(jsonObject);
        return range;
    }

    @Override
    protected void parseData(JSONObject jsonObject) {
        super.parseData(jsonObject);

        try {
            mOffset = jsonObject.optLong("offset");
        } catch (Exception e){}

        try {
            mLength = jsonObject.optInt("length");
        } catch (Exception e){}
    }

    public JSONObject toJsonObject() {
        JSONObject object = super.toJsonObject();
        try {
            object.put("offset", mOffset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            object.put("length", mLength);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }


    @Override
    public String toString() {
        return "MoRange{" +
                "mOffset=" + mOffset +
                ", mLength=" + mLength +
                '}';
    }
}
