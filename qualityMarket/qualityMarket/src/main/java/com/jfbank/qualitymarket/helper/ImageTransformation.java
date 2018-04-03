package com.jfbank.qualitymarket.helper;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

/**
 * 功能：<br>
 * 作者：赵海<br>
 * 时间： 2017/8/9 0009<br>.
 * 版本：1.2.0
 */

public class ImageTransformation implements Transformation {
    String url;

    public ImageTransformation(String url) {
        this.url = url;
    }

    @Override
    public Bitmap transform(Bitmap bitmap) {
        return bitmap;
    }

    @Override
    public String key() {
        return url;
    }
}