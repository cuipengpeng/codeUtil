package com.test.xcamera.bean;

import org.json.JSONObject;

/**
 * 预览设置
 * Created by zll on 2019/9/26.
 */

public class MoPreviewSetting extends MoShotSetting {
    private int mProportion; // 1: 16:9  2: 4:3  3: 1:1
    private int mResolution; // 1: 4K  2: 2K  3: 1080p

    public int getmProportion() {
        return mProportion;
    }

    public void setmProportion(int mProportion) {
        this.mProportion = mProportion;
    }

    public int getmResolution() {
        return mResolution;
    }

    public void setmResolution(int mResolution) {
        this.mResolution = mResolution;
    }

    public static MoPreviewSetting parse(JSONObject jsonObject) {
        MoPreviewSetting previewSetting = new MoPreviewSetting();
        previewSetting.parseData(jsonObject);
        return previewSetting;
    }

    @Override
    protected void parseData(JSONObject jsonObject) {
        super.parseData(jsonObject);

        try {
            if (jsonObject.has("proportion")) {
                mProportion = jsonObject.optInt("proportion");
            }
        }catch (Exception e){}

        try {
            if (jsonObject.has("resolution")) {
                mResolution = jsonObject.optInt("resolution");
            }
        } catch (Exception e){}
    }

    @Override
    public String toString() {
        return "MoPreviewSetting{" +
                "mProportion=" + mProportion +
                ", mResolution=" + mResolution +
                '}';
    }
}
