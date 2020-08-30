package com.test.xcamera.bean;

public class VideoFragmentTag {


    /**
     * video_create_time : 1576475392200
     * video_start_time : 1576475482200
     * video_end_time : 1576475488200
     * video_type : 0
     * segment_type : trace
     */

    private long video_create_time;
    private long video_start_time;
    private long video_end_time;
    private int video_type;
    private String segment_type;

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

    public String getSegment_type() {
        return segment_type;
    }

    public void setSegment_type(String segment_type) {
        this.segment_type = segment_type;
    }
}
