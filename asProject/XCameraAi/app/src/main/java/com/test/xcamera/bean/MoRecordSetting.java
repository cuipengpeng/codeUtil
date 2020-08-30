package com.test.xcamera.bean;

import org.json.JSONObject;

/**
 * 录视频设置
 * Created by zll on 2019/9/26.
 */

public class MoRecordSetting extends MoShotSetting {
    private int mProportion; // 1: 16:9  2: 4:3  3: 1:1  4: 19.5:9
    private int mResolution; // 1: 12M  2: 4K  3: 2K  4: 1080p
    private int mFrameRate;  // 1:24  2:30  3:48  4:60  5:120  6:240
    private int mType;  //  0:jpg/mp4  1:mov  2:jpeg and raw
    private int mAwb;  // 白平衡
    private int mISO;  // 感光度
    private int mEV;  // 曝光补偿
    private int mInternal; // 延时摄影间隔
    private int mSpeed; // 慢动作倍率
    private int mDuration; // 延时摄影时长
    private int mWDR;  // 宽动态
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
    private int interval;
    private int beauty_smooth;
    private int beauty_light;
    private int beauty;

    public int getBeauty() {
        return beauty;
    }
    public int getBeauty_smooth() {
        return beauty_smooth;
    }

    public int getBeauty_light() {
        return beauty_light;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
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

    public void setmEV(int mEV) {
        this.mEV = mEV;
    }

    public int getmInternal() {
        return mInternal;
    }

    public void setmInternal(int mInternal) {
        this.mInternal = mInternal;
    }

    public int getmDuration() {
        return mDuration;
    }

    public void setmDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    public int getmWDR() {
        return mWDR;
    }

    public void setmWDR(int mWDR) {
        this.mWDR = mWDR;
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

    public int getmSpeed() {
        return mSpeed;
    }

    public void setmSpeed(int mSpeed) {
        this.mSpeed = mSpeed;
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

    public static MoRecordSetting parse(JSONObject jsonObject) {
        MoRecordSetting recordSetting = new MoRecordSetting();
        recordSetting.parseData(jsonObject);
        return recordSetting;
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
            if (jsonObject.has("internal")) {
                mInternal = jsonObject.optInt("internal");
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("interval")) {
                interval = jsonObject.optInt("interval");
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("speed")) {
                mSpeed = jsonObject.optInt("speed");
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("duration")) {
                mDuration = jsonObject.optInt("duration");
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("WDR")) {
                mWDR = jsonObject.optInt("WDR");
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
            if (jsonObject.has("progress")) {
                progress = jsonObject.optInt("progress");
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("zoom")) {
                zoom = jsonObject.optInt("zoom");
            }
        } catch (Exception e) {
        }

        beauty_smooth = jsonObject.optInt("beauty_smooth", 0);
        beauty_light = jsonObject.optInt("beauty_light", 0);
        beauty = jsonObject.optInt("beauty", 0);
    }

    @Override
    public String toString() {
        return "MoRecordSetting{" +
                "mProportion=" + mProportion +
                ", mResolution=" + mResolution +
                ", mFrameRate=" + mFrameRate +
                ", mType=" + mType +
                ", mAwb=" + mAwb +
                ", mISO=" + mISO +
                ", mEV=" + mEV +
                ", mInternal=" + mInternal +
                ", mSpeed=" + mSpeed +
                ", mDuration=" + mDuration +
                ", mWDR=" + mWDR +
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
                ", interval=" + interval +
                ", beauty_smooth=" + beauty_smooth +
                ", beauty_light=" + beauty_light +
                '}';
    }
}
