package com.test.xcamera.phonealbum.bean;

import java.io.Serializable;

public class BaseThumbBean implements Serializable {
    //视频在整个view的坐标宽度
    private int viewStartIndex;
    //每帧时间宽度
    private int viewFrameWidth;

    public int getViewStartIndex() {
        return viewStartIndex;
    }

    public void setViewStartIndex(int viewStartIndex) {
        this.viewStartIndex = viewStartIndex;
    }

    public int getViewFrameWidth() {
        return viewFrameWidth;
    }

    public void setViewFrameWidth(int viewFrameWidth) {
        this.viewFrameWidth = viewFrameWidth;
    }
}
