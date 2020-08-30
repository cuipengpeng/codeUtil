package com.test.xcamera.bean;

import org.json.JSONObject;

/**
 * 拍照设置
 * Created by zll on 2019/9/26.
 */

public class MoSnapShotSetting extends MoShotSetting {
    private int mProportion; // 1: 16:9  2: 4:3  3: 1:1  4: 19.5:9
    private int mResolution; // 1: 12M  2: 4K  3: 2K  4: 1080p
    private int mFrameRate;  // 1:24  2:30  3:48  4:60  5:120  6:240
    private int mType;  //  0:jpg/mp4  1:mov  2:jpeg and raw
    private int mAwb;  // 白平衡
    private int mISO;  // 感光度
    private int mEV;  // 曝光补偿
    private int mShutter;  // 快门速度
    private int mDelayTime; // 倒计时时间
    private int mLongExploreTime; // 长曝光时间
    private int mHDR;  // 高动态
    private int mDIS;  // 防抖
    private int mLDC;  // 畸变校正
    private int mColor; // 色彩
    private int mTrack; // 云台模式
    private int mSuperHighQuality; // 高画质
    private int mAntiFlicker; // 抗闪烁
    private int mPictureType; // 畸变校正
    private int trackStatus;
    private int zoom;
    private int progress;
    private int beauty_smooth;
    private int beauty_light;
    private int beauty;

    public int getBeauty() {
        return beauty;
    }

    public int getProgress() {
        return progress;
    }

    public int getTrackStatus() {
        return trackStatus;
    }

    public void setTrackStatus(int trackStatus) {
        this.trackStatus = trackStatus;
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }

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

    public int getmFrameRate() {
        return mFrameRate;
    }

    public void setmFrameRate(int mFrameRate) {
        this.mFrameRate = mFrameRate;
    }

    public int getmType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }

    public int getmAwb() {
        return mAwb;
    }

    public void setmAwb(int mAwb) {
        this.mAwb = mAwb;
    }

    public int getmISO() {
        return mISO;
    }

    public void setmISO(int mISO) {
        this.mISO = mISO;
    }

    public int getmEV() {
        return mEV;
    }

    public int getBeauty_smooth() {
        return beauty_smooth;
    }

    public int getBeauty_light() {
        return beauty_light;
    }

    public void setmEV(int mEV) {
        this.mEV = mEV;
    }

    public int getmShutter() {
        return mShutter;
    }

    public void setmShutter(int mShutter) {
        this.mShutter = mShutter;
    }

    public int getmDelayTime() {
        return mDelayTime;
    }

    public void setmDelayTime(int mDelayTime) {
        this.mDelayTime = mDelayTime;
    }

    public int getmLongExploreTime() {
        return mLongExploreTime;
    }

    public void setmLongExploreTime(int mLongExploreTime) {
        this.mLongExploreTime = mLongExploreTime;
    }

    public int getmHDR() {
        return mHDR;
    }

    public void setmHDR(int mHDR) {
        this.mHDR = mHDR;
    }

    public int getmDIS() {
        return mDIS;
    }

    public void setmDIS(int mDIS) {
        this.mDIS = mDIS;
    }

    public int getmLDC() {
        return mLDC;
    }

    public void setmLDC(int mLDC) {
        this.mLDC = mLDC;
    }

    public int getmColor() {
        return mColor;
    }

    public void setmColor(int mColor) {
        this.mColor = mColor;
    }

    public int getmTrack() {
        return mTrack;
    }

    public void setmTrack(int mTrack) {
        this.mTrack = mTrack;
    }

    public int getmSuperHighQuality() {
        return mSuperHighQuality;
    }

    public void setmSuperHighQuality(int mSuperHighQuality) {
        this.mSuperHighQuality = mSuperHighQuality;
    }

    public int getmAntiFlicker() {
        return mAntiFlicker;
    }

    public void setmAntiFlicker(int mAntiFlicker) {
        this.mAntiFlicker = mAntiFlicker;
    }

    public int getmPictureType() {
        return mPictureType;
    }

    public void setmPictureType(int mPictureType) {
        this.mPictureType = mPictureType;
    }

    public static MoSnapShotSetting parse(JSONObject jsonObject) {
        MoSnapShotSetting shotSetting = new MoSnapShotSetting();
        shotSetting.parseData(jsonObject);
        return shotSetting;
    }

    @Override
    protected void parseData(JSONObject jsonObject) {
        super.parseData(jsonObject);

        try {
            if (jsonObject.has("proportion")) {
                mProportion = jsonObject.optInt("proportion");
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("resolution")) {
                mResolution = jsonObject.optInt("resolution");
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("frame_rate")) {
                mFrameRate = jsonObject.optInt("frame_rate");
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("type")) {
                mType = jsonObject.optInt("type");
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("awb")) {
                mAwb = jsonObject.optInt("awb");
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("iso")) {
                mISO = jsonObject.optInt("iso");
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("ev")) {
                mEV = jsonObject.optInt("ev");
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("shutter")) {
                mShutter = jsonObject.optInt("shutter");
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("delaytime")) {
                mDelayTime = jsonObject.optInt("delaytime");
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("longexpotime")) {
                mLongExploreTime = jsonObject.optInt("longexpotime");
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("HDR")) {
                mHDR = jsonObject.optInt("HDR");
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("dis")) {
                mDIS = jsonObject.optInt("dis");
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("LDC")) {
                mLDC = jsonObject.optInt("LDC");
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("color")) {
                mColor = jsonObject.optInt("color");
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("track")) {
                mTrack = jsonObject.optInt("track");
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("super_high_quality")) {
                mSuperHighQuality = jsonObject.optInt("super_high_quality");
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("antiflicker")) {
                mAntiFlicker = jsonObject.optInt("antiflicker");
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("picture_type")) {
                mPictureType = jsonObject.optInt("picture_type");
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("track_status")) {
                trackStatus = jsonObject.optInt("track_status");
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("zoom")) {
                zoom = jsonObject.optInt("zoom");
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("progress")) {
                progress = jsonObject.optInt("progress");
            }
        } catch (Exception e) {
        }

        beauty_smooth = jsonObject.optInt("beauty_smooth", 0);
        beauty_light = jsonObject.optInt("beauty_light", 0);
        beauty = jsonObject.optInt("beauty", 0);
    }

    @Override
    public String toString() {
        return "MoSnapShotSetting{" +
                "mProportion=" + mProportion +
                ", mResolution=" + mResolution +
                ", mFrameRate=" + mFrameRate +
                ", mType=" + mType +
                ", mAwb=" + mAwb +
                ", mISO=" + mISO +
                ", mEV=" + mEV +
                ", mShutter=" + mShutter +
                ", mDelayTime=" + mDelayTime +
                ", mLongExploreTime=" + mLongExploreTime +
                ", mHDR=" + mHDR +
                ", mDIS=" + mDIS +
                ", mLDC=" + mLDC +
                ", mColor=" + mColor +
                ", mTrack=" + mTrack +
                ", mSuperHighQuality=" + mSuperHighQuality +
                ", mAntiFlicker=" + mAntiFlicker +
                ", mPictureType=" + mPictureType +
                ", trackStatus=" + trackStatus +
                ", zoom=" + zoom +
                ", progress=" + progress +
                ", beauty_smooth=" + beauty_smooth +
                ", beauty_light=" + beauty_light +
                '}';
    }
}
