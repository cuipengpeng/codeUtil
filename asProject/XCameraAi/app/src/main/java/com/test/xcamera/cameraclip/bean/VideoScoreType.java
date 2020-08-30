package com.test.xcamera.cameraclip.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoScoreType implements Parcelable {
    private long create_time;
    private long start_time;
    private long end_time;
    private String rec_mode;
    private ScoreType scoreType;


    public String getRec_mode() {
        return rec_mode;
    }

    public void setRec_mode(String rec_mode) {
        this.rec_mode = rec_mode;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    public ScoreType getScoreType() {
        return scoreType;
    }

    public void setScoreType(ScoreType scoreType) {
        this.scoreType = scoreType;
    }

    public enum ScoreType {
        MARK,TRACE_VIDEO, PIZ_STILL
    }

    public enum CameraMode {
        HI_PDT_WORKMODE_NORM_REC("0"),   //普通录像
        HI_PDT_WORKMODE_LOOP_REC("1"),           //循环录像
        HI_PDT_WORKMODE_LPSE_REC("2"),            //延时摄影
        HI_PDT_WORKMODE_SLOW_REC("3"),          //慢动作
        HI_PDT_WORKMODE_BEAUTY_REC("4"),
        HI_PDT_WORKMODE_TEMPLATE_REC("5"),
        HI_PDT_WORKMODE_QUICK_REC("6"),
        HI_PDT_WORKMODE_TRACK_LPSE_REC("7"),
        HI_PDT_WORKMODE_SING_PHOTO("8"),
        HI_PDT_WORKMODE_LONGEXP_PHOTO("9"),
        HI_PDT_WORKMODE_LPSE_PHOTO("10"),
        HI_PDT_WORKMODE_BURST("11"),
        HI_PDT_WORKMODE_RECSNAP("12"),
        HI_PDT_WORKMODE_PLAYBACK("13"),
        HI_PDT_WORKMODE_UVC("14"),
        HI_PDT_WORKMODE_USB_STORAGE("15"),
        HI_PDT_WORKMODE_SUSPEND("16"),
        HI_PDT_WORKMODE_HDMI_PREVIEW("17"),
        HI_PDT_WORKMODE_HDMI_PLAYBACK("18"),
        HI_PDT_WORKMODE_UPGRADE("19"),
        HI_PDT_WORKMODE_BUTT("20");

        private String value;

        CameraMode(String value) {    //    必须是private的，否则编译错误
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }


    public VideoScoreType clone(){
        VideoScoreType newVideoScoreType = new VideoScoreType();
        newVideoScoreType.setCreate_time(this.getCreate_time());
        newVideoScoreType.setStart_time(this.getStart_time());
        newVideoScoreType.setEnd_time(this.getEnd_time());
        newVideoScoreType.setRec_mode(this.getRec_mode());
        newVideoScoreType.setScoreType(this.getScoreType());
        return newVideoScoreType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.create_time);
        dest.writeLong(this.start_time);
        dest.writeLong(this.end_time);
        dest.writeString(this.rec_mode);
        dest.writeInt(this.scoreType == null ? -1 : this.scoreType.ordinal());
    }

    public VideoScoreType() {
    }

    protected VideoScoreType(Parcel in) {
        this.create_time = in.readLong();
        this.start_time = in.readLong();
        this.end_time = in.readLong();
        this.rec_mode = in.readString();
        int tmpScoreType = in.readInt();
        this.scoreType = tmpScoreType == -1 ? null : ScoreType.values()[tmpScoreType];
    }

    public static final Creator<VideoScoreType> CREATOR = new Creator<VideoScoreType>() {
        @Override
        public VideoScoreType createFromParcel(Parcel source) {
            return new VideoScoreType(source);
        }

        @Override
        public VideoScoreType[] newArray(int size) {
            return new VideoScoreType[size];
        }
    };
}
