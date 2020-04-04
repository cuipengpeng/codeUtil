package com.caishi.chaoge.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class OriginalTranscribeBean implements Serializable {
    public ArrayList<LrcBean> lrcList;
    public ArrayList<String> lrcStringList;
    public String fileName = "";
    public String mp3Path = "";
    public String pngPath = "";
    public String fontColor = "";
    public int fontSpecial;
    public int colorFlag = 0;
    public float personVolume = 2f;
    public float bgVolume = 1f;
    public float videoBgVolume = 1f;
    public String fontPath = "";
    public ArrayList<String> colorList = null;

}
