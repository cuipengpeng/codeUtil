package com.test.xcamera.bean;

import org.json.JSONObject;

/**
 * Created by zll on 2019/9/26.
 */

public class MoDeviceInfo extends MoData {


    private String did; //设备did
    private String mFirmwareVersion;  //相机固件版本
    private String mPtzVersion;//云台版本

//    public String getSn() {
//        return sn;
//    }

//    private String sn;

    public String getmPtzVersion() {
        return mPtzVersion;
    }

    public void setmPtzVersion(String mPtzVersion) {
        this.mPtzVersion = mPtzVersion;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getmFirmwareVersion() {
        return mFirmwareVersion;
    }

    public void setmFirmwareVersion(String mFirmwareVersion) {
        this.mFirmwareVersion = mFirmwareVersion;
    }

    public static MoDeviceInfo parse(JSONObject jsonObject) {
        MoDeviceInfo deviceInfo = new MoDeviceInfo();
        deviceInfo.parseData(jsonObject);
        return deviceInfo;
    }

    @Override
    protected void parseData(JSONObject jsonObject) {
        super.parseData(jsonObject);

        try {
            if (jsonObject.has("did")) {
                did = jsonObject.optString("did");
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("camera_firmware_version")) {
                mFirmwareVersion = jsonObject.optString("camera_firmware_version");
            }
        } catch (Exception e) {
        }
        try {
            if (jsonObject.has("camera_ptz_version")) {
                mPtzVersion = jsonObject.optString("camera_ptz_version");
            }
        } catch (Exception e) {
        }

//        try {
//            if (jsonObject.has("serial_num")) {
//                sn = jsonObject.optString("serial_num");
//            }
//        } catch (Exception e) {
//        }
    }

    @Override
    public String toString() {
        return "MoDeviceInfo{" +
                "did='" + did + '\'' +
                ", mFirmwareVersion='" + mFirmwareVersion + '\'' +
                '}';
    }
}
