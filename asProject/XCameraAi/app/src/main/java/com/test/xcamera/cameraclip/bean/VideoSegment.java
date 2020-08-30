package com.test.xcamera.cameraclip.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class VideoSegment implements Parcelable {

    private boolean selected=false;
    private int downloadCount = 0;
    private String videoSegmentId;
    //所在文件开始时间
    private long create_time;
    //片段开始时间
    private long start_time;
    //片段结束时间
    private long end_time;
    //所在文件的结束时间
    private long close_time;
    private String rec_mode;
    //相机下载路径
    private String filePath;
    private VideoTag videoTag;
    private float score;
    private boolean addMarkScore=false;
    //下载到手机的路径。
    private String videoSegmentFilePath;
    private long videoSegmentDuration;
    private ThemeType themeType;
    //填充模板时长时：视频片段时长>模板时长，需根据mark、云台静止等打分项进行裁剪
    private List<VideoScoreType> videoScoreTypeList = new ArrayList<>();
    private List<Integer> rotationList = new ArrayList<>();
    private boolean petCloseUp = false;

    public List<Integer> getRotationList() {
        return rotationList;
    }

    public void setRotationList(List<Integer> rotationList) {
        this.rotationList = rotationList;
    }

    public boolean isPetCloseUp() {
        return petCloseUp;
    }

    public void setPetCloseUp(boolean petCloseUp) {
        this.petCloseUp = petCloseUp;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public List<VideoScoreType> getVideoScoreTypeList() {
        return videoScoreTypeList;
    }

    public void setVideoScoreTypeList(List<VideoScoreType> videoScoreTypeList) {
        this.videoScoreTypeList = videoScoreTypeList;
    }

    public ThemeType getThemeType() {
        return themeType;
    }

    public void setThemeType(ThemeType themeType) {
        this.themeType = themeType;
    }

    public String getVideoSegmentFilePath() {
        return videoSegmentFilePath;
    }

    public void setVideoSegmentFilePath(String videoSegmentFilePath) {
        this.videoSegmentFilePath = videoSegmentFilePath;
    }

    public long getVideoSegmentDuration() {
        return videoSegmentDuration;
    }

    public void setVideoSegmentDuration(long videoSegmentDuration) {
        this.videoSegmentDuration = videoSegmentDuration;
    }

    public long getClose_time() {
        return close_time;
    }

    public void setClose_time(long close_time) {
        this.close_time = close_time;
    }


    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isAddMarkScore() {
        return addMarkScore;
    }

    public void setAddMarkScore(boolean addMarkScore) {
        this.addMarkScore = addMarkScore;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

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

    public VideoTag getVideoTag() {
        return videoTag;
    }

    public void setVideoTag(VideoTag videoTag) {
        this.videoTag = videoTag;
    }

    public String getVideoSegmentId() {
        return videoSegmentId;
    }

    public void setVideoSegmentId(String videoSegmentId) {
        this.videoSegmentId = videoSegmentId;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "start_time="+start_time +"--  end_time="+end_time+"-- score="+score;
    }

    public enum ThemeType {
        SINGLE_PERSON, DOUBLE_PERSON, MULTI_PERSON, PET, PERSON_AND_PET, NONE
    }
    public enum VideoTempleteType {
        THEME, SCENE
    }

    public VideoSegment clone(){
        VideoSegment newVideoSegment = new VideoSegment();
        newVideoSegment.setDownloadCount(this.getDownloadCount());
        newVideoSegment.setVideoSegmentId(this.getVideoSegmentId());
        newVideoSegment.setCreate_time(this.getCreate_time());
        newVideoSegment.setStart_time(this.getStart_time());
        newVideoSegment.setEnd_time(this.getEnd_time());
        newVideoSegment.setClose_time(this.getClose_time());
        newVideoSegment.setRec_mode(this.getRec_mode());
        newVideoSegment.setFilePath(this.getFilePath());
        newVideoSegment.setVideoTag(this.getVideoTag());
        newVideoSegment.setScore(this.getScore());
        newVideoSegment.setAddMarkScore(this.isAddMarkScore());
        newVideoSegment.setVideoSegmentFilePath(this.getVideoSegmentFilePath());
        newVideoSegment.setVideoSegmentDuration(this.getVideoSegmentDuration());
        newVideoSegment.setThemeType(this.getThemeType());
        for(int i=0; i<this.getVideoScoreTypeList().size(); i++){
            newVideoSegment.getVideoScoreTypeList().add(this.getVideoScoreTypeList().get(i).clone());
        }
        for(int i=0; i<this.getRotationList().size(); i++){
            newVideoSegment.getRotationList().add(this.getRotationList().get(i).intValue());
        }
        newVideoSegment.setPetCloseUp(this.isPetCloseUp());
        return newVideoSegment;
    }

    public VideoSegment() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
        dest.writeInt(this.downloadCount);
        dest.writeString(this.videoSegmentId);
        dest.writeLong(this.create_time);
        dest.writeLong(this.start_time);
        dest.writeLong(this.end_time);
        dest.writeLong(this.close_time);
        dest.writeString(this.rec_mode);
        dest.writeString(this.filePath);
        dest.writeSerializable(this.videoTag);
        dest.writeFloat(this.score);
        dest.writeByte(this.addMarkScore ? (byte) 1 : (byte) 0);
        dest.writeString(this.videoSegmentFilePath);
        dest.writeLong(this.videoSegmentDuration);
        dest.writeInt(this.themeType == null ? -1 : this.themeType.ordinal());
        dest.writeTypedList(this.videoScoreTypeList);
        dest.writeList(this.rotationList);
        dest.writeByte(this.petCloseUp ? (byte) 1 : (byte) 0);
    }

    protected VideoSegment(Parcel in) {
        this.selected = in.readByte() != 0;
        this.downloadCount = in.readInt();
        this.videoSegmentId = in.readString();
        this.create_time = in.readLong();
        this.start_time = in.readLong();
        this.end_time = in.readLong();
        this.close_time = in.readLong();
        this.rec_mode = in.readString();
        this.filePath = in.readString();
        this.videoTag = (VideoTag) in.readSerializable();
        this.score = in.readFloat();
        this.addMarkScore = in.readByte() != 0;
        this.videoSegmentFilePath = in.readString();
        this.videoSegmentDuration = in.readLong();
        int tmpThemeType = in.readInt();
        this.themeType = tmpThemeType == -1 ? null : ThemeType.values()[tmpThemeType];
        this.videoScoreTypeList = in.createTypedArrayList(VideoScoreType.CREATOR);
        this.rotationList = new ArrayList<Integer>();
        in.readList(this.rotationList, Integer.class.getClassLoader());
        this.petCloseUp = in.readByte() != 0;
    }

    public static final Creator<VideoSegment> CREATOR = new Creator<VideoSegment>() {
        @Override
        public VideoSegment createFromParcel(Parcel source) {
            return new VideoSegment(source);
        }

        @Override
        public VideoSegment[] newArray(int size) {
            return new VideoSegment[size];
        }
    };
}
