package com.test.bank.bean;

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
    private long start_time;
    private long end_time;
    private List<String> forwardFaceList = new ArrayList<>();
    private List<String> happyFaceList = new ArrayList<>();

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
}
