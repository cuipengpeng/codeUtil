package com.test.xcamera.bean;

import android.util.Log;

import com.test.xcamera.enumbean.ShootMode;
import com.test.xcamera.managers.ShotModeManager;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * 同步设置
 * Created by zll on 2019/9/26.
 */

public class MoShotSetting extends MoData {
    public static final HashMap<Integer, ShootMode> MODE_MAP = new HashMap<>();
    static {
        MODE_MAP.put(1, ShootMode.PHOTO);
        MODE_MAP.put(2, ShootMode.LONG_EXPLORE);
        MODE_MAP.put(3, ShootMode.VIDEO);
        MODE_MAP.put(4, ShootMode.SLOW_MOTION);
        MODE_MAP.put(5, ShootMode.TRACKLASPEVIDEO);
        MODE_MAP.put(6, ShootMode.LAPSE_VIDEO);
        MODE_MAP.put(9, ShootMode.VIDEO_QUICK);
        MODE_MAP.put(10, ShootMode.PHOTO);
        MODE_MAP.put(11, ShootMode.UPGRADE);
    }

    private ShootMode mMode;
    // 1.photo:拍照 2.delayphoto:延时拍照 3.longexplorephoto:长曝光
    // 4.video:视频  5.slowmotionvideo:慢动作  6.lapsevideo:缩时摄影
    /**
     * 需要重新选择模式
     */
    public boolean needCmd;
    public int result;
    public int mode;

    private MoSnapShotSetting mMoSnapShotSetting;
    private MoRecordSetting mMoRecordSetting;
    private MoPreviewSetting mMoPreviewSetting;

    public ShootMode getmMode() {
        return mMode;
    }

    public void setmMode(ShootMode mMode) {
        this.mMode = mMode;
    }

    public MoSnapShotSetting getmMoSnapShotSetting() {
        return mMoSnapShotSetting;
    }

    public void setmMoSnapShotSetting(MoSnapShotSetting mMoSnapShotSetting) {
        this.mMoSnapShotSetting = mMoSnapShotSetting;
    }

    public MoRecordSetting getmMoRecordSetting() {
        return mMoRecordSetting;
    }

    public void setmMoRecordSetting(MoRecordSetting mMoRecordSetting) {
        this.mMoRecordSetting = mMoRecordSetting;
    }

    public MoPreviewSetting getmMoPreviewSetting() {
        return mMoPreviewSetting;
    }

    public void setmMoPreviewSetting(MoPreviewSetting mMoPreviewSetting) {
        this.mMoPreviewSetting = mMoPreviewSetting;
    }

    public static MoShotSetting parse(JSONObject jsonObject) {
        MoShotSetting shotSetting = new MoShotSetting();
        shotSetting.parseData(jsonObject);
        return shotSetting;
    }

    @Override
    protected void parseData(JSONObject jsonObject) {
        super.parseData(jsonObject);

        try {
            if (jsonObject.has("mode")) {
                int m = jsonObject.optInt("mode");
                this.mode = m;
                Log.e("=====", "parseData  mode==>" + mode);
                needCmd = false;
                if (m == 1) {
                    mMode = ShootMode.PHOTO;
                } else if (m == 2) {
                    mMode = ShootMode.LONG_EXPLORE;
                } else if (m == 3) {
                    mMode = ShootMode.VIDEO;
                } else if (m == 4) {
                    mMode = ShootMode.SLOW_MOTION;
                } else if (m == 5) {
                    mMode = ShootMode.TRACKLASPEVIDEO;
                } else if (m == 6) {
                    mMode = ShootMode.LAPSE_VIDEO;
                } else if (m == 7) {
                    mMode = ShootMode.PHOTO_BEAUTY;
                } else if (m == 8) {
                    mMode = ShootMode.VIDEO_BEAUTY;
                } else {  //如果不在其中 则默认进入ShootMode.VIDEO
                    needCmd = true;
                    mMode = ShootMode.VIDEO;
                    this.mode = 3;
                }

                ShotModeManager.getInstance().setmShootMode(mMode);
            }
        } catch (Exception e) {
        }

        try {
            if (jsonObject.has("setting")) {
                JSONObject object = jsonObject.optJSONObject("setting");
                if (object != null) {
                    if (ShotModeManager.getInstance().isVideo()) {
                        mMoRecordSetting = MoRecordSetting.parse(object);
                    } else {
                        mMoSnapShotSetting = MoSnapShotSetting.parse(object);
                    }
                }
            }
        } catch (Exception e) {
        }

//        try {
//            if (jsonObject.has("preview_setting")) {
//                JSONObject object = jsonObject.optJSONObject("preview_setting");
//                if (object != null) {
//                    mMoPreviewSetting = MoPreviewSetting.parse(object);
//                }
//            }
//        } catch (Exception e) {
//        }
    }

    @Override
    public String toString() {
        return "MoShotSetting{" +
                "mMode name=" + mMode.name() +
                "mMode ordinal=" + mMode.ordinal() +
                ", mMoSnapShotSetting=" + mMoSnapShotSetting +
                ", mMoRecordSetting=" + mMoRecordSetting +
                ", mMoPreviewSetting=" + mMoPreviewSetting +
                '}';
    }
}
