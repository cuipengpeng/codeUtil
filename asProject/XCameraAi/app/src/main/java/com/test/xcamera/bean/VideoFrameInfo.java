package com.test.xcamera.bean;

import com.test.xcamera.constants.AACTypeConstants;
import com.test.xcamera.constants.LogcatConstants;
import com.test.xcamera.constants.PayloadTypeConstants;
import com.test.xcamera.constants.VideoNaluTypeConstants;
import com.test.xcamera.enumbean.AACType;
import com.test.xcamera.enumbean.PayloadType;
import com.test.xcamera.enumbean.VideoNaluType;
import com.test.xcamera.managers.DataManager;
import com.test.xcamera.utils.BytesUtil;
import com.moxiang.common.logging.Logcat;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 帧信息
 * Created by zll on 2019/5/9.
 */

public class VideoFrameInfo implements Serializable {
    private int mDataSize;
    //    // 帧类型
//    private PayloadType mPayloadType;
    // frame info
    private int mSeq;
    private long mTimeMs;
    private long mTimeTs;
    // video
    private VideoNaluType mNaluType;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mVideoFps;
    private int mMirror;
    private int mRotate;

    // 音视频数据
    private byte[] mFrameData;

    private int mRectCount;
    // rect
    private ArrayList<FrameRectInfo> mFrameRectInfos;

    private int mRes;
    // 扩展的数据长度
    private int mExtSize;

    private int mVideoSize;
    private int mAudioSize;
    private long mParseTime;

    public VideoFrameInfo(byte[] frameInfo, int dataSize) {
        this.mVideoSize = BytesUtil.byteArrayToInt(frameInfo, DataManager.HEAD_COUNT, false);
        byte[] frameDetail = BytesUtil.subBytes(frameInfo, DataManager.HEAD_COUNT + 4, mVideoSize);
        this.mSeq = BytesUtil.byteArrayToInt(frameDetail, 0, false);
        this.mTimeMs = BytesUtil.longFrom8Bytes(frameDetail, 4, true);
        this.mTimeTs = BytesUtil.longFrom8Bytes(frameDetail, 12, true);
        int naluType = BytesUtil.byteArrayToShort(frameDetail, 20, false);
        this.mNaluType = getVideoNaluType(naluType);
        this.mVideoWidth = BytesUtil.byteArrayToShort(frameDetail, 22, false);
        this.mVideoHeight = BytesUtil.byteArrayToShort(frameDetail, 24, false);
        this.mVideoFps = BytesUtil.byteArrayToShort(frameDetail, 26, false);
        this.mMirror = frameDetail[28] & 0x1;
        int rotate = frameDetail[29];
        switch (rotate) {
            case 0:
                mRotate = 0;
                break;
            case 1:
                mRotate = 90;
                break;
            case 2:
                mRotate = 180;
                break;
            case 3:
                mRotate = 270;
                break;
        }
        this.mFrameData = BytesUtil.subBytes(frameDetail, 30, frameDetail.length - 30);

//        Log.e("=====", String.format("mRotate:%d  mMirror:%d", mRotate, mMirror));

        int rectBegin = DataManager.HEAD_COUNT + 4 + mVideoSize;
        this.mRectCount = BytesUtil.byteArrayToInt(frameInfo, rectBegin, false);
        if (mRectCount > 0) {
            mFrameRectInfos = new ArrayList<>();
            try {
                getRectInfos(frameInfo, rectBegin, mRectCount);
            } catch (Exception e) {
//                StringBuilder sb = new StringBuilder();
//                for (byte b : frameInfo)
//                    sb.append(String.format("%02x ", new Integer(b & 0xff)));

                Logcat.v().tag(LogcatConstants.FPV_PREVIEW).msg("跟踪框越界==> rectBegin:" + rectBegin
                        + "dataLen==>" + frameInfo.length + "  mRectCount:" + mRectCount).out();
            }
        }
        this.mParseTime = System.currentTimeMillis();
    }

    public int getRotate() {
        return mRotate;
    }

    public int getmSeq() {
        return mSeq;
    }

    public long getmTimeMs() {
        return mTimeMs;
    }

    public long getmTimeTs() {
        return mTimeTs;
    }

    public int getMirror() {
        return mMirror;
    }

    public VideoNaluType getmNaluType() {
        return mNaluType;
    }

    public int getmVideoWidth() {
        return mVideoWidth;
    }

    public int getmVideoHeight() {
        return mVideoHeight;
    }

    public int getmVideoFps() {
        return mVideoFps;
    }

    public byte[] getmFrameData() {
        return mFrameData;
    }

    public void setmFrameData(byte[] mFrameData) {
        this.mFrameData = mFrameData;
    }

    public ArrayList<FrameRectInfo> getmFrameRectInfos() {
        return mFrameRectInfos;
    }

    public void setmFrameRectInfos(ArrayList<FrameRectInfo> mFrameRectInfos) {
        this.mFrameRectInfos = mFrameRectInfos;
    }

    public int getmRectCount() {
        return mRectCount;
    }

    public long getmParseTime() {
        return mParseTime;
    }

    public void setmParseTime(long mParseTime) {
        this.mParseTime = mParseTime;
    }

    /**
     * 获取上传的类型
     *
     * @param type
     * @return
     */
    private PayloadType getPayloadType(int type) {
        PayloadType payloadType = null;
        switch (type) {
            case PayloadTypeConstants.PT_H264:
                payloadType = PayloadType.PT_H264;
                break;
        }
        return payloadType;
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

    /**
     * 获取视频类型
     *
     * @param type
     * @return
     */
    private VideoNaluType getVideoNaluType(int type) {
        VideoNaluType videoNaluType = null;
        switch (type) {
            case VideoNaluTypeConstants.VIDEO_NALU_BSLICE:
                videoNaluType = VideoNaluType.VIDEO_NALU_BSLICE;
                break;
            case VideoNaluTypeConstants.VIDEO_NALU_PSLICE:
                videoNaluType = VideoNaluType.VIDEO_NALU_PSLICE;
                break;
            case VideoNaluTypeConstants.VIDEO_NALU_ISLICE:
                videoNaluType = VideoNaluType.VIDEO_NALU_ISLICE;
                break;
            case VideoNaluTypeConstants.VIDEO_NALU_IDRSLICE:
                videoNaluType = VideoNaluType.VIDEO_NALU_IDRSLICE;
                break;
            case VideoNaluTypeConstants.VIDEO_NALU_VPS:
                videoNaluType = VideoNaluType.VIDEO_NALU_VPS;
                break;
            case VideoNaluTypeConstants.VIDEO_NALU_SPS:
                videoNaluType = VideoNaluType.VIDEO_NALU_SPS;
                break;
            case VideoNaluTypeConstants.VIDEO_NALU_PPS:
                videoNaluType = VideoNaluType.VIDEO_NALU_PPS;
                break;
            case VideoNaluTypeConstants.VIDEO_NALU_SEI:
                videoNaluType = VideoNaluType.VIDEO_NALU_SEI;
                break;
        }
        return videoNaluType;
    }

    /**
     * 获取区域位置
     *
     * @param frameInfo
     */
    private void getRectInfos(byte[] frameInfo, int begin, int count) {

        begin += 4;

        for (int i = 0; i < count && begin < frameInfo.length - 8 * 4; i++) {
            int id = BytesUtil.byteArrayToInt(frameInfo, begin, false);
            begin += 4;

            //TODO 測試用
//            int status = 0;
            int status = BytesUtil.byteArrayToInt(frameInfo, begin, false);
            begin += 4;

            byte[] sX = BytesUtil.subBytes(frameInfo, begin, 8);
            String startX = new String(sX, 0, sX.length);
            startX = startX.substring(0, 6);
            begin += 8;

            byte[] sY = BytesUtil.subBytes(frameInfo, begin, 8);
            String startY = new String(sY, 0, sY.length);
            startY = startY.substring(0, 6);
            begin += 8;

            byte[] eX = BytesUtil.subBytes(frameInfo, begin, 8);
            String endX = new String(eX, 0, eX.length);
            endX = endX.substring(0, 6);
            begin += 8;

            byte[] eY = BytesUtil.subBytes(frameInfo, begin, 8);
            String endY = new String(eY, 0, eY.length);
            endY = endY.substring(0, 6);
            begin += 8;

            FrameRectInfo frameRectInfo = new FrameRectInfo();
            frameRectInfo.setmID(id);
            frameRectInfo.setStatus(status);
            frameRectInfo.setmStartX(startX);
            frameRectInfo.setmStartY(startY);
            frameRectInfo.setmEndX(endX);
            frameRectInfo.setmEndY(endY);
            mFrameRectInfos.add(frameRectInfo);
        }
    }

    @Override
    public String toString() {
        return "VideoFrameInfo{" +
                "mDataSize=" + mDataSize +
                ", mSeq=" + mSeq +
                ", mTimeMs=" + mTimeMs +
                ", mTimeTs=" + mTimeTs +
                ", mNaluType=" + mNaluType +
                ", mVideoWidth=" + mVideoWidth +
                ", mVideoHeight=" + mVideoHeight +
                ", mVideoFps=" + mVideoFps +
                ", mRectCount=" + mRectCount +
                ", mFrameRectInfos=" + mFrameRectInfos +
                ", mRes=" + mRes +
                ", mExtSize=" + mExtSize +
                ", mVideoSize=" + mVideoSize +
                ", mAudioSize=" + mAudioSize +
                ", mParseTime=" + mParseTime +
                '}';
    }
}
