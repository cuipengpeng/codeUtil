package com.test.xcamera.bean;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by zll on 2019/7/3.
 */

public class MoData implements Serializable {
    private String mID;

    public String getmID() {
        return mID;
    }

    public void setmID(String mID) {
        this.mID = mID;
    }

    protected void parseData(JSONObject jsonObject) {
        if (jsonObject == null) return;

        try {
            mID = jsonObject.optString("id");
        } catch (Exception e) {
        }
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", mID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
