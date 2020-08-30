package com.test.xcamera.bean;

public class VideoFragment {


    /**
     * video_id : 10001
     * video_create_time : 1576475384000
     * video_start_time : 1576475385000
     * video_end_time : 1576475389600
     * video_type : 1
     * label_combination : desk,dog,food
     * lapse : 1
     */

    private int video_id;
    private float score;
    private long video_create_time;
    private long video_start_time;
    private long video_end_time;
    private int video_type;
    private String label_combination;
    private int lapse;
    private boolean addedMark = false;

    public boolean isAddedMark() {
        return addedMark;
    }

    public void setAddedMark(boolean addedMark) {
        this.addedMark = addedMark;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }
    public int getVideo_id() {
        return video_id;
    }

    public void setVideo_id(int video_id) {
        this.video_id = video_id;
    }

    public long getVideo_create_time() {
        return video_create_time;
    }

    public void setVideo_create_time(long video_create_time) {
        this.video_create_time = video_create_time;
    }

    public long getVideo_start_time() {
        return video_start_time;
    }

    public void setVideo_start_time(long video_start_time) {
        this.video_start_time = video_start_time;
    }

    public long getVideo_end_time() {
        return video_end_time;
    }

    public void setVideo_end_time(long video_end_time) {
        this.video_end_time = video_end_time;
    }

    public int getVideo_type() {
        return video_type;
    }

    public void setVideo_type(int video_type) {
        this.video_type = video_type;
    }

    public String getLabel_combination() {
        return label_combination;
    }

    public void setLabel_combination(String label_combination) {
        this.label_combination = label_combination;
    }

    public int getLapse() {
        return lapse;
    }

    public void setLapse(int lapse) {
        this.lapse = lapse;
    }
}
