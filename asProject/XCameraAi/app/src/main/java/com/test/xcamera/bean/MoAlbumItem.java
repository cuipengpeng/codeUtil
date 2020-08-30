package com.test.xcamera.bean;

import android.util.Log;

import com.test.xcamera.utils.MediaUtils;

import org.json.JSONObject;

/**
 * 相册item
 * Created by zll on 2019/7/3.
 */
public class MoAlbumItem extends MoData {
    private String mType;
    private MoImage mImage;
    private MoVideo mVideo;
    private MoImage mThumbnail;
    private long mCreateTime;
    public boolean isChecked = false;
    private String mDownloadFileName;
    private boolean mIsCamrea = false;
    private int rotate;

    public int getRotate() {
        return rotate;
    }

    public void setRotate(int rotate) {
        this.rotate = rotate;
    }

    public void setCollect(int collect) {
        this.collect = collect;
    }

    private int collect;

    public int getCollect() {
        return collect;
    }

    public String getmDownloadFileName() {
        return mDownloadFileName;
    }

    public void setmDownloadFileName(String mDownloadFileName) {
        this.mDownloadFileName = mDownloadFileName;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    public MoImage getmImage() {
        return mImage;
    }

    public void setmImage(MoImage mImage) {
        this.mImage = mImage;
    }

    public MoVideo getmVideo() {
        return mVideo;
    }

    public void setmVideo(MoVideo mVideo) {
        this.mVideo = mVideo;
    }

    public MoImage getmThumbnail() {
        return mThumbnail;
    }

    public void setmThumbnail(MoImage mThumbnail) {
        this.mThumbnail = mThumbnail;
    }

    public long getmCreateTime() {
        return mCreateTime;
    }

    public void setmCreateTime(long mCreateTime) {
        this.mCreateTime = mCreateTime;
    }

    public boolean isImage() {
        return MediaUtils.isPhoto(mType);
    }

    public boolean isVideo() {
        return MediaUtils.isVideo(mType);
    }

    public boolean ismIsCamrea() {
        return mIsCamrea;
    }

    public void setmIsCamrea(boolean mIsCamrea) {
        this.mIsCamrea = mIsCamrea;
    }

    public static MoAlbumItem parse(JSONObject jsonObject) {
        MoAlbumItem albumItem = new MoAlbumItem();
        albumItem.parseData(jsonObject);
        return albumItem;
    }

    @Override
    protected void parseData(JSONObject jsonObject) {
        super.parseData(jsonObject);
        Log.i("JSONTEST", "parseData: " + jsonObject);
        try {
            if (jsonObject.has("type")) {
                mType = jsonObject.optString("type");
                setmIsCamrea(true);
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("collect")) {
                collect = jsonObject.optInt("collect");
                setmIsCamrea(true);
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("image")) {
                JSONObject imageObj = jsonObject.optJSONObject("image");
                if (imageObj != null) {
                    mImage = MoImage.parse(imageObj);
                    setmIsCamrea(true);
                }
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("video")) {
                JSONObject videoObj = jsonObject.optJSONObject("video");
                if (videoObj != null) {
                    mVideo = MoVideo.parse(videoObj);
                    setmIsCamrea(true);
                }
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("thumbnail")) {
                JSONObject imageObj = jsonObject.optJSONObject("thumbnail");
                if (imageObj != null) {
                    mThumbnail = MoImage.parse(imageObj);
                    setmIsCamrea(true);
                }
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("create_time")) {
                long time = jsonObject.optLong("create_time");
                setmCreateTime(time);
                setmIsCamrea(true);
            }
        } catch (Exception e) {
        }

        try {
            rotate = jsonObject.optInt("rotate");
        } catch (Exception e) {
        }
    }

    @Override
    public String toString() {
        return "MoAlbumItem{" +
                "mType='" + mType + '\'' +
//                ", mImage=" + mImage +
//                ", mVideo=" + mVideo +
                ", mVideo=" + mThumbnail +
                '}';
    }
}
