package com.test.xcamera.bean;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.test.xcamera.activity.CameraMode;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by smz on 2020/1/13.
 * <p>
 * 同步拍照、录像数据的模型类
 */

public class MoSettingModel implements Serializable{
    public int msg_id;
    public String action;
    public int result = -1;
    public int mode;
    public boolean isVideo;

    public SnapshotModel snapshotModel;
    public VideoModel videoModel;

    public void parse(JSONObject object) {
        String setting = object.optString("setting");
        if (!TextUtils.isEmpty(setting)) {
            Gson gson = new Gson();
            CameraMode mode = CameraMode.getMode(this.mode);
            if (this.isVideo = CameraMode.isVideo(mode)) {
                this.videoModel = gson.fromJson(setting, VideoModel.class);
            } else
                this.snapshotModel = gson.fromJson(setting, SnapshotModel.class);
        }
    }

    /**
     * 同步时 拍照参数
     */
    public class SnapshotModel implements Serializable{
        public int proportion;
        public int resolution;
        public int frame_rate;
        public int type;
        public int awb_mode;
        public int awb;
        public int exposure_mode;
        public int iso;
        public int ev;
        public int shutter;
        public int delaytime;
        public int longexpotime;
        public int HDR;
        public int dis;
        public int LDC;
        public int color;
        public int ptz_mode;
        public int ptz_speed;
        public int super_high_quality;
        public int antiflicker;
        public int progress;
        public int progress_time;
        public int track_status;
        public int zoom;

        @Override
        public String toString() {
            return "SnapshotModel{" +
                    "proportion=" + proportion +
                    ", resolution=" + resolution +
                    ", frame_rate=" + frame_rate +
                    ", type=" + type +
                    ", awb_mode=" + awb_mode +
                    ", awb=" + awb +
                    ", exposure_mode=" + exposure_mode +
                    ", iso=" + iso +
                    ", ev=" + ev +
                    ", shutter=" + shutter +
                    ", delaytime=" + delaytime +
                    ", longexpotime=" + longexpotime +
                    ", HDR=" + HDR +
                    ", dis=" + dis +
                    ", LDC=" + LDC +
                    ", color=" + color +
                    ", ptz_mode=" + ptz_mode +
                    ", ptz_speed=" + ptz_speed +
                    ", super_high_quality=" + super_high_quality +
                    ", antiflicker=" + antiflicker +
                    ", progress=" + progress +
                    ", progress_time=" + progress_time +
                    ", track_status=" + track_status +
                    ", zoom=" + zoom +
                    '}';
        }
    }

    /**
     * 同步时 视频参数
     */
    public class VideoModel implements Serializable{
        public int proportion;
        public int resolution;
        public int frame_rate;
        public int type;
        public int awb_mode;
        public int awb;
        public int exposure_mode;
        public int iso;
        public int ev;
        public int speed;
        public int WDR;
        public int dis;
        public int LDC;
        public int color;
        public int ptz_mode;
        public int ptz_speed;
        public int super_high_quality;
        public int antiflicker;
        public int progress;
        public int progress_time;
        public int track_status;
        public int zoom;
        public int shutter;

        @Override
        public String toString() {
            return "VideoModel{" +
                    "proportion=" + proportion +
                    ", resolution=" + resolution +
                    ", frame_rate=" + frame_rate +
                    ", type=" + type +
                    ", awb_mode=" + awb_mode +
                    ", awb=" + awb +
                    ", exposure_mode=" + exposure_mode +
                    ", iso=" + iso +
                    ", ev=" + ev +
                    ", speed=" + speed +
                    ", WDR=" + WDR +
                    ", dis=" + dis +
                    ", LDC=" + LDC +
                    ", color=" + color +
                    ", ptz_mode=" + ptz_mode +
                    ", ptz_speed=" + ptz_speed +
                    ", super_high_quality=" + super_high_quality +
                    ", antiflicker=" + antiflicker +
                    ", progress=" + progress +
                    ", progress_time=" + progress_time +
                    ", track_status=" + track_status +
                    ", zoom=" + zoom +
                    ", shutter=" + shutter +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "MoSettingModel{" +
                "msg_id=" + msg_id +
                ", action='" + action + '\'' +
                ", result=" + result +
                ", mode=" + mode +
                ", isVideo=" + isVideo +
                ", snapshotModel=" + snapshotModel +
                ", videoModel=" + videoModel +
                '}';
    }
}
