package com.test.xcamera.bean;

import com.test.xcamera.constants.AACTypeConstants;
import com.test.xcamera.enumbean.AACType;
import com.test.xcamera.managers.DataManager;
import com.test.xcamera.utils.BytesUtil;

/**
 * Created by zll on 2019/7/15.
 */

public class AudioFrameInfo {
    private int mSeq;
    private long mTimeMs;
    private long mTimeTs;
    private int mSamplerate;
    private int mBitWidth;
    private int mChnCnt;
    private AACType mAacType;
    private int mAudioSize;
    private byte[] mAudioData;

    public AudioFrameInfo(byte[] frameInfo, int len) {
        this.mAudioSize = BytesUtil.byteArrayToInt(frameInfo, DataManager.HEAD_COUNT, false);
        byte[] audioDetail = BytesUtil.subBytes(frameInfo, DataManager.HEAD_COUNT + 4, mAudioSize);
        this.mSeq = BytesUtil.byteArrayToInt(audioDetail, 0, false);
        this.mTimeMs = BytesUtil.longFrom8Bytes(audioDetail, 4, true);
        this.mTimeTs = BytesUtil.longFrom8Bytes(audioDetail, 12, true);
        this.mSamplerate = BytesUtil.byteArrayToShort(audioDetail, 20, false);
        this.mBitWidth = BytesUtil.byteArrayToShort(audioDetail, 22, false);
        this.mChnCnt = BytesUtil.byteArrayToShort(audioDetail, 24, false);
        int aacType = BytesUtil.byteArrayToShort(audioDetail, 26, false);
        this.mAacType = getAACType(aacType);
        this.mAudioData = BytesUtil.subBytes(audioDetail, 28, audioDetail.length  - 28);
    }

    public int getmSeq() {
        return mSeq;
    }

    public void setmSeq(int mSeq) {
        this.mSeq = mSeq;
    }

    public long getmTimeMs() {
        return mTimeMs;
    }

    public void setmTimeMs(long mTimeMs) {
        this.mTimeMs = mTimeMs;
    }

    public long getmTimeTs() {
        return mTimeTs;
    }

    public void setmTimeTs(long mTimeTs) {
        this.mTimeTs = mTimeTs;
    }

    public int getmSamplerate() {
        return mSamplerate;
    }

    public void setmSamplerate(int mSamplerate) {
        this.mSamplerate = mSamplerate;
    }

    public int getmBitWidth() {
        return mBitWidth;
    }

    public void setmBitWidth(int mBitWidth) {
        this.mBitWidth = mBitWidth;
    }

    public int getmChnCnt() {
        return mChnCnt;
    }

    public void setmChnCnt(int mChnCnt) {
        this.mChnCnt = mChnCnt;
    }

    public AACType getmAacType() {
        return mAacType;
    }

    public void setmAacType(AACType mAacType) {
        this.mAacType = mAacType;
    }

    public byte[] getmAuidoData() {
        return mAudioData;
    }

    public void setmAudioData(byte[] mAuidoData) {
        this.mAudioData = mAuidoData;
    }

    /**
     * 获取音频类型
     *
     * @param type
     * @return
     */
    private AACType getAACType(int type) {
        AACType aacType = null;
        switch (type) {
            case AACTypeConstants.AAC_TYPE_AACLC:
                aacType = AACType.AAC_TYPE_AACLC;
                break;
            case AACTypeConstants.AAC_TYPE_EAAC:
                aacType = AACType.AAC_TYPE_EAAC;
                break;
            case AACTypeConstants.AAC_TYPE_EAACPLUS:
                aacType = AACType.AAC_TYPE_EAACPLUS;
                break;
            case AACTypeConstants.AAC_TYPE_AACLD:
                aacType = AACType.AAC_TYPE_AACLD;
                break;
            case AACTypeConstants.AAC_TYPE_AACELD:
                aacType = AACType.AAC_TYPE_AACELD;
                break;
        }
        return aacType;
    }

    @Override
    public String toString() {
        return "AudioFrameInfo{" +
                "mSeq=" + mSeq +
                ", mTimeMs=" + mTimeMs +
                ", mTimeTs=" + mTimeTs +
                ", mSamplerate=" + mSamplerate +
                ", mBitWidth=" + mBitWidth +
                ", mChnCnt=" + mChnCnt +
                ", mAacType=" + mAacType +
                ", mAudioSize=" + mAudioSize +
                ", mAudioData=" + mAudioData.length +
                '}';
    }
}
