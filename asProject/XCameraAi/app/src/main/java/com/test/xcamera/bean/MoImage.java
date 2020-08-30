package com.test.xcamera.bean;

import org.json.JSONObject;

/**
 * 图片
 * Created by zll on 2019/7/3.
 */

public class MoImage extends MoAlbumItem {
    public int result;
    public int reason;
    private String mUri;
    private int mWidth;
    private int mHeight;
    private long mSize;
    private int iso;
    private int shutter;
    private int ev;
    private int awb;
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

    public long getmSize() {
        return mSize;
    }

    public void setmSize(long mSize) {
        this.mSize = mSize;
    }

    public static MoImage parse(JSONObject jsonObject) {
        MoImage image = new MoImage();
        image.parseData(jsonObject);
        return image;
    }

    public int getIso() {
        return iso;
    }

    public void setIso(int iso) {
        this.iso = iso;
    }

    public int getShutter() {
        return shutter;
    }

    public void setShutter(int shutter) {
        this.shutter = shutter;
    }

    public int getEv() {
        return ev;
    }

    public void setEv(int ev) {
        this.ev = ev;
    }

    public int getAwb() {
        return awb;
    }

    public void setAwb(int awb) {
        this.awb = awb;
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
            mSize = jsonObject.optLong("size");
        } catch (Exception e) {
        }
        //////
        try {
            iso = jsonObject.optInt("iso");
        } catch (Exception e) {
        }

        try {
            shutter = jsonObject.optInt("shutter");
        } catch (Exception e) {
        }

        try {
            ev = jsonObject.optInt("EV");
        } catch (Exception e) {
        }

        try {
            awb = jsonObject.optInt("AWB");
        } catch (Exception e) {
        }

        try {
            reason = jsonObject.optInt("reason");
        } catch (Exception e) {
        }
    }

    @Override
    public String toString() {
        return "MoImage{" +
                "mUri='" + mUri + '\'' +
                ", mWidth=" + mWidth +
                ", mHeight=" + mHeight +
                ", mSize=" + mSize +
                '}';
    }
}
