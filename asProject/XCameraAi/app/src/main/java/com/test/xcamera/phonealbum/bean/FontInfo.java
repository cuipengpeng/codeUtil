package com.test.xcamera.phonealbum.bean;

import java.io.Serializable;

/**
 * Created by CaoZhiChao on 2019/7/11 19:21
 */
public class FontInfo implements Serializable {
    private String fontFileName;

    private String imageName;

    public String getFontFileName() {
        return fontFileName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setFontFileName(String fontFileName) {
        this.fontFileName = fontFileName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
