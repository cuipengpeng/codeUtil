package com.caishi.chaoge.bean;

import android.graphics.Bitmap;

import com.caishi.chaoge.utils.Utils;

import java.io.Serializable;

public class OriginalBean implements Serializable {
    public boolean isSelect;//是否选中
    public boolean isEditor = true;//是否可以编辑
    public int editFlag;//0字体 1颜色
    public String specialColor = "#000000";//特殊字体颜色
    //    public Bitmap selectBitmap = Utils.RGB2Bitmap(specialColor);//选中的图片
    public String text;//内容
    public String fontPath;//字体文件路径
    public long startTime;//开始时间
    public long endTime;//结束时间


}
