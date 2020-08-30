package com.test.xcamera.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * 视频
 * Created by zll on 2019/7/3.
 */

public class MoVideo extends MoAlbumItem {
    private String mUri;
    private int mWidth;
    private int mHeight;
    private MoImage mVideoThumbnail;
    private long mDuration;
    private long mSize;
    private int fps;
    private String speed;

    public long[] getMarkList() {
        return markList;
    }

    public void setMarkList(long[] markList) {
        this.markList = markList;
    }

    private long[] markList;

    public String getmUri() {
        return mUri;
    }

    public void setmUri(String mUri) {
        this.mUri = mUri;
    }

    public int getmWidth() {
        return mWidth;
    }

    public void setmWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public int getmHeight() {
        return mHeight;
    }

    public void setmHeight(int mHeight) {
        this.mHeight = mHeight;
    }

    public MoImage getmThumbnail() {
        return mVideoThumbnail;
    }

    public void setmThumbnail(MoImage mVideoThumbnail) {
        this.mVideoThumbnail = mVideoThumbnail;
    }

    public long getmDuration() {
        return mDuration;
    }

    public void setmDuration(long mDuration) {
        this.mDuration = mDuration;
    }


    public long getmSize() {
        return mSize;
    }

    public void setmSize(long mSize) {
        this.mSize = mSize;
    }

    public static MoVideo parse(JSONObject jsonObject) {
        MoVideo video = new MoVideo();
        video.parseData(jsonObject);
        return video;
    }

    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    @Override
    protected void parseData(JSONObject jsonObject) {
        super.parseData(jsonObject);

        try {
            mUri = jsonObject.optString("uri");
        } catch (Exception e) {
        }

        try {
            mWidth = jsonObject.optInt("width");
        } catch (Exception e) {
        }

        try {
            mHeight = jsonObject.optInt("height");
        } catch (Exception e) {
        }

        try {
            JSONObject imageObj = jsonObject.optJSONObject("thumbnail");
            if (imageObj != null) {
                mVideoThumbnail = MoImage.parse(imageObj);
            }
        } catch (Exception e) {
        }

        try {
            mDuration = jsonObject.optInt("duration");
        } catch (Exception e) {
        }
        try {
            JSONArray mark_list = jsonObject.optJSONArray("mark_list");
            markList = parseArray(mark_list);
        } catch (Exception e) {
        }

        try {
            mSize = jsonObject.optLong("size");
        } catch (Exception e) {
        }

        try {
            fps = jsonObject.optInt("fps");
        } catch (Exception e) {
        }

        try {
            speed = jsonObject.optString("speed");
        } catch (Exception e) {
        }
    }

    private long[] parseArray(JSONArray mark_list) throws JSONException {
        if (mark_list.length() > 0) {
            long[] list = new long[mark_list.length()];
            for (int i = 0; i < mark_list.length(); i++) {
                JSONObject mark = (JSONObject) mark_list.get(i);
                long markPoint = mark.optLong("mark");
                list[i] = markPoint;
            }
            return list;
        }
        return null;
    }

    @Override
    public String toString() {
        return "MoVideo{" +
                "mUri='" + mUri + '\'' +
                ", mWidth=" + mWidth +
                ", mHeight=" + mHeight +
                ", mThumbnail=" + mVideoThumbnail +
                ", mDuration=" + mDuration +
                ", mSize=" + mSize +
                ", fps=" + fps +
                ", speed=" + speed +
                ", markList=" + Arrays.toString(markList) +
                '}';
    }
}
