package com.test.xcamera.phonealbum.widget.subtitle;



import com.test.xcamera.phonealbum.widget.subtitle.bean.Mark;

import java.util.List;

public class Bounds {
    public Mark originMark;
    public Mark destMark;
    public List<Mark> overlapped;
    public float left;
    public float top;
    public float right;
    public float bottom;
    public int trackId;

    @Override
    public String toString() {
        return "Bounds{" +
                "originMark=" + originMark +
                "destMark=" + destMark +
                "overlapped=" + overlapped +
                ", left=" + left +
                ", top=" + top +
                ", right=" + right +
                ", bottom=" + bottom +
                ", trackId=" + trackId +
                '}';
    }
}
