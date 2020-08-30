package com.test.xcamera.bean;

import org.json.JSONObject;

/**
 * 一键成片今日精彩
 * Created by zll on 2019/10/25.
 */

public class MoVideoByDate extends MoData {
    private long mDate;
    private String mVideoIcon;

    private String  mVideoThumbnail; //缩略图


    public String getmVideoThumbnail() {
        return mVideoThumbnail;
    }

    public void setmVideoThumbnail(String mVideoThumbnail) {
        this.mVideoThumbnail = mVideoThumbnail;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    private boolean isCheck;

    public long getmDate() {
        return mDate;
    }

    public void setmDate(long mDate) {
        this.mDate = mDate;
    }

    public String getmVideoIcon() {
        return mVideoIcon;
    }

    public void setmVideoIcon(String mVideoIcon) {
        this.mVideoIcon = mVideoIcon;
    }

    public static MoVideoByDate parse(JSONObject jsonObject) {
        MoVideoByDate video = new MoVideoByDate();
        video.parseData(jsonObject);
        return video;
    }

    @Override
    protected void parseData(JSONObject jsonObject) {
        super.parseData(jsonObject);
        try {
            if (jsonObject.has("date")) {
                mDate = jsonObject.optLong("date");
            }
        } catch (Exception e){}

        try {
            if (jsonObject.has("video_icon")) {
                mVideoIcon = jsonObject.optString("video_icon");
            }
        } catch (Exception e){}

        try {
            if (jsonObject.has("video_thumbnail")) {
                mVideoThumbnail = jsonObject.optString("video_thumbnail");
            }
        } catch (Exception e){}

    }

    @Override
    public String toString() {
        return "MoVideoByDate{" +
                "mDate=" + mDate +
                ", mVideoIcon='" + mVideoIcon + '\'' +
                '}';
    }
}
