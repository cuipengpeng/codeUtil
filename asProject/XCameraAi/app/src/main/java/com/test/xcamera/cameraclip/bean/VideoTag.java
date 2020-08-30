package com.test.xcamera.cameraclip.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VideoTag implements Serializable {

    public VideoTag() {
    }

    public VideoTag(String videoSegmentId, String face_num, String object_detect, String ori_face, String gender_class, String object_class, String scene_class, String emotion, long start_time, long end_time) {
        this.videoSegmentId = videoSegmentId;
        this.face_num = face_num;
        this.object_detect = object_detect;
        this.ori_face = ori_face;
        this.gender_class = gender_class;
        this.object_class = object_class;
        this.scene_class = scene_class;
        this.emotion = emotion;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    private String videoSegmentId;
    public String face_num;
    public String object_detect;
    public String ori_face;
    public String gender_class;
    public String object_class;
    public String scene_class;
    public String emotion;
    private float no_face_out_ratio;
    private long start_time;
    private long end_time;
    private List<String> forwardFaceList = new ArrayList<>();
    private List<String> happyFaceList = new ArrayList<>();
    private List<Long> petCloseupList = new ArrayList<>();
    private List<Rotation> rotationList = new ArrayList<>();


    public List<Long> getPetCloseupList() {
        return petCloseupList;
    }

    public void setPetCloseupList(List<Long> petCloseupList) {
        this.petCloseupList = petCloseupList;
    }

    public List<Rotation> getRotationList() {
        return rotationList;
    }

    public void setRotationList(List<Rotation> rotationList) {
        this.rotationList = rotationList;
    }

    public List<String> getForwardFaceList() {
        return forwardFaceList;
    }

    public void setForwardFaceList(List<String> forwardFaceList) {
        this.forwardFaceList = forwardFaceList;
    }

    public List<String> getHappyFaceList() {
        return happyFaceList;
    }

    public void setHappyFaceList(List<String> happyFaceList) {
        this.happyFaceList = happyFaceList;
    }

    public String getVideoSegmentId() {
        return videoSegmentId;
    }

    public void setVideoSegmentId(String videoSegmentId) {
        this.videoSegmentId = videoSegmentId;
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

    public String getObject_class() {
        return object_class;
    }

    public void setObject_class(String object_class) {
        this.object_class = object_class;
    }

    public String getScene_class() {
        return scene_class;
    }

    public void setScene_class(String scene_class) {
        this.scene_class = scene_class;
    }

    public String getFace_num() {
        return face_num;
    }

    public void setFace_num(String face_num) {
        this.face_num = face_num;
    }

    public String getObject_detect() {
        return object_detect;
    }

    public void setObject_detect(String object_detect) {
        this.object_detect = object_detect;
    }

    public String getOri_face() {
        return ori_face;
    }

    public void setOri_face(String ori_face) {
        this.ori_face = ori_face;
    }

    public String getGender_class() {
        return gender_class;
    }

    public void setGender_class(String gender_class) {
        this.gender_class = gender_class;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public float getNo_face_out_ratio() {
        return no_face_out_ratio;
    }

    public void setNo_face_out_ratio(float no_face_out_ratio) {
        this.no_face_out_ratio = no_face_out_ratio;
    }

    public VideoTag clone(){
        VideoTag newVideoTag = new VideoTag();
        newVideoTag.setFace_num(this.getFace_num());
        newVideoTag.setObject_detect(this.getObject_detect());
        newVideoTag.setOri_face(this.getOri_face());
        newVideoTag.setGender_class(this.getGender_class());
        newVideoTag.setObject_class(this.getObject_class());
        newVideoTag.setScene_class(this.getScene_class());
        newVideoTag.setEmotion(this.getEmotion());
        newVideoTag.setNo_face_out_ratio(this.getNo_face_out_ratio());
        newVideoTag.setStart_time(this.getStart_time());
        newVideoTag.setEnd_time(this.getEnd_time());
        for(int i=0; i<this.getForwardFaceList().size(); i++){
            newVideoTag.getForwardFaceList().add(this.getForwardFaceList().get(i));
        }
        for(int i=0; i<this.getHappyFaceList().size(); i++){
            newVideoTag.getHappyFaceList().add(this.getHappyFaceList().get(i));
        }
        for(int i=0; i<this.getPetCloseupList().size(); i++){
            newVideoTag.getPetCloseupList().add(this.getPetCloseupList().get(i).longValue());
        }
        for(int i=0; i<this.getRotationList().size(); i++){
            newVideoTag.getRotationList().add(this.getRotationList().get(i).clone());
        }
        return newVideoTag;
    }

}
