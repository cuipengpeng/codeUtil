package com.test.xcamera.bean;

import java.io.Serializable;

public class SideKeyBean implements Serializable {


    /**
     * date : 1581481973975
     * video_icon : mox://resourceitem?uri=%2Fapp%2Fsd%2FDCIM%2F100MOCAM%2FNORM0015.MP4&size=62917425&type=video
     * video_thumbnail : mox://resourceitem?uri=%2Fapp%2Fsd%2FDCIM%2F100MOCAM%2FNORM0015.MP4.THM&size=118713&type=image
     * xml_uri : mox://resourceitem?uri=%2Fapp%2Fsd%2Fmedia%2F20200212.xml&size=11066&type=xml
     */

    private long date;
    private String video_icon;
    private String video_thumbnail;
    private String xml_uri;
    private boolean selected;


    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getVideo_icon() {
        return video_icon;
    }

    public void setVideo_icon(String video_icon) {
        this.video_icon = video_icon;
    }

    public String getVideo_thumbnail() {
        return video_thumbnail;
    }

    public void setVideo_thumbnail(String video_thumbnail) {
        this.video_thumbnail = video_thumbnail;
    }

    public String getXml_uri() {
        return xml_uri;
    }

    public void setXml_uri(String xml_uri) {
        this.xml_uri = xml_uri;
    }

    @Override
    public String toString() {
        return "sideKeyBean = {" +
                "--mDate=" + date +
                " --mVideoIcon='" + video_icon +
                " --video_thumbnail = "+video_thumbnail+
                " --xml_uri = "+xml_uri+"}";
    }
}
