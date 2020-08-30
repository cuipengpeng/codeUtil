package com.test.xcamera.bean;

import org.json.JSONObject;

/**
 * 系统信息
 * Created by zll on 2019/9/26.
 */

public class MoSystemInfo extends MoData {
    public int result;
    private int mBattery;
    private int sdStatus;
    private int mVideoCount;
    private int mImageCount;
    private long mSDCardCapacity;
    private int ptzMode;
    private int ptzSensitivity;

    public int getPtzMode() {
        return ptzMode;
    }

    public void setPtzMode(int ptzMode) {
        this.ptzMode = ptzMode;
    }

    public int getPtzSensitivity() {
        return ptzSensitivity;
    }

    public void setPtzSensitivity(int ptzSensitivity) {
        this.ptzSensitivity = ptzSensitivity;
    }

    public int getSdStatus() {
        return sdStatus;
    }

    public void setSdStatus(int sdStatus) {
        this.sdStatus = sdStatus;
    }

    public int getmBattery() {
        return mBattery;
    }

    public void setmBattery(int mBattery) {
        this.mBattery = mBattery;
    }

    public int getmVideoCount() {
        return mVideoCount;
    }

    public void setmVideoCount(int mVideoCount) {
        this.mVideoCount = mVideoCount;
    }

    public int getmImageCount() {
        return mImageCount;
    }

    public void setmImageCount(int mImageCount) {
        this.mImageCount = mImageCount;
    }

    public long getmSDCardCapacity() {
        return mSDCardCapacity;
    }

    public void setmSDCardCapacity(long mSDCardCapacity) {
        this.mSDCardCapacity = mSDCardCapacity;
    }

    public static MoSystemInfo parse(JSONObject jsonObject) {
        MoSystemInfo systemInfo = new MoSystemInfo();
        systemInfo.parseData(jsonObject);
        return systemInfo;
    }

    @Override
    protected void parseData(JSONObject jsonObject) {
        super.parseData(jsonObject);

        try {
            if (jsonObject.has("sd_card_status")) {
                sdStatus = jsonObject.optInt("sd_card_status");
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("battery")) {
                mBattery = jsonObject.optInt("battery");
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("ptz_mode")) {
                ptzMode = jsonObject.optInt("ptz_mode");
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("ptz_sensitivity")) {
                ptzSensitivity = jsonObject.optInt("ptz_sensitivity");
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("available_video_count")) {
                mVideoCount = jsonObject.optInt("available_video_count");
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("available_image_count")) {
                mImageCount = jsonObject.optInt("available_image_count");
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("available_sd_card_capacity")) {
                mSDCardCapacity = jsonObject.optLong("available_sd_card_capacity", 0);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public String toString() {
        return "MoSystemInfo{" +
                "result=" + result +
                ", mBattery=" + mBattery +
                ", sdStatus=" + sdStatus +
                ", mVideoCount=" + mVideoCount +
                ", mImageCount=" + mImageCount +
                ", mSDCardCapacity=" + mSDCardCapacity +
                ", ptzMode=" + ptzMode +
                ", ptzSensitivity=" + ptzSensitivity +
                '}';
    }
}
