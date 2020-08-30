package com.test.xcamera.bean;

import org.json.JSONObject;

/**
 * 一键成片视频片段
 * Created by zll on 2019/10/25.
 */

public class MoVideoSegment extends MoData{
    private int mVideoSize;
    private int mVideoDuration;
    private String mVideoIcon;
    private int mVideoSegmentNum;
    private int mVideoScore;

    private long mVideoCreateTime;


    private long mVideoStartTime;
    private long mVideoEndTime;

    public long getmVideoCreateTime() {
        return mVideoCreateTime;
    }

    public void setmVideoCreateTime(long mVideoCreateTime) {
        this.mVideoCreateTime = mVideoCreateTime;
    }

    public long getmVideoStartTime() {
        return mVideoStartTime;
    }

    public void setmVideoStartTime(long mVideoStartTime) {
        this.mVideoStartTime = mVideoStartTime;
    }

    public long getmVideoEndTime() {
        return mVideoEndTime;
    }

    public void setmVideoEndTime(long mVideoEndTime) {
        this.mVideoEndTime = mVideoEndTime;
    }

    public int getmVideoSize() {
        return mVideoSize;
    }

    public void setmVideoSize(int mVideoSize) {
        this.mVideoSize = mVideoSize;
    }

    public int getmVideoDuration() {
        return mVideoDuration;
    }

    public void setmVideoDuration(int mVideoDuration) {
        this.mVideoDuration = mVideoDuration;
    }

    public String getmVideoIcon() {
        return mVideoIcon;
    }

    public void setmVideoIcon(String mVideoIcon) {
        this.mVideoIcon = mVideoIcon;
    }

    public int getmVideoSegmentNum() {
        return mVideoSegmentNum;
    }

    public void setmVideoSegmentNum(int mVideoSegmentNum) {
        this.mVideoSegmentNum = mVideoSegmentNum;
    }

    public int getmVideoScore() {
        return mVideoScore;
    }

    public void setmVideoScore(int mVideoScore) {
        this.mVideoScore = mVideoScore;
    }

    public static MoVideoSegment parse(JSONObject jsonObject) {
        MoVideoSegment videoSegment = new MoVideoSegment();
        videoSegment.parseData(jsonObject);
        return videoSegment;
    }

    @Override
    protected void parseData(JSONObject jsonObject) {
        super.parseData(jsonObject);
        try {
            if (jsonObject.has("video_size")) {
                mVideoSize = jsonObject.optInt("video_size");
            }
        } catch (Exception e){}

        try {
            if (jsonObject.has("video_length")) {
                mVideoDuration = jsonObject.optInt("video_length");
            }
        } catch (Exception e){}

        try {
            if (jsonObject.has("video_icon")) {
                mVideoIcon = jsonObject.optString("video_icon");
            }
        } catch (Exception e){}

        try {
            if (jsonObject.has("video_segment_num")) {
                mVideoSegmentNum = jsonObject.optInt("video_segment_num");
            }
        } catch (Exception e){}

        try {
            if (jsonObject.has("video_score")) {
                mVideoScore = jsonObject.optInt("video_score");
            }
        } catch (Exception e){}

        try {
            if (jsonObject.has("video_start_time")) {
                mVideoStartTime = jsonObject.optLong("video_start_time");
            }
        } catch (Exception e){}
        try {
            if (jsonObject.has("video_end_time")) {
                mVideoEndTime = jsonObject.optLong("video_end_time");
            }
        } catch (Exception e){}
        try {
            if (jsonObject.has("video_create_time")) {
                mVideoCreateTime = jsonObject.optLong("video_create_time");
            }
        } catch (Exception e){}
    }

    @Override
    public String toString() {
        return "MoVideoSegment{" +
                "mVideoSize=" + mVideoSize +
                ", mVideoDuration=" + mVideoDuration +
                ", mVideoIcon='" + mVideoIcon + '\'' +
                ", mVideoSegmentNum=" + mVideoSegmentNum +
                ", mVideoScore=" + mVideoScore +
                ", mVideoStartTime=" + mVideoStartTime +
                ", mVideoEndTime=" + mVideoEndTime +
                ", video_create_time=" + mVideoCreateTime +
                '}';
    }
}
