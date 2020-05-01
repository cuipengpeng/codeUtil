package com.jfbank.qualitymarket.helper;

/**
 * 功能：<br>
 * 作者：RaykerTeam
 * 时间： 2016/11/17 0017<br>.
 * 版本：1.0.0
 */

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.squareup.picasso.Transformation;

public class PicassoRoundTransform implements Transformation {
    int rx;
    int ry;

    public PicassoRoundTransform(int rx, int ry) {
        this.rx = rx;
        this.ry = ry;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        int widthLight = source.getWidth();
        int heightLight = source.getHeight();

        Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paintColor = new Paint();
        paintColor.setFlags(Paint.ANTI_ALIAS_FLAG);
        paintColor.setColor(Color.argb(255,255,255,255));
        RectF rectF = new RectF(new Rect(0, 0, widthLight, heightLight));
        canvas.drawRoundRect(rectF,rx, ry, paintColor);

        Paint paintImage = new Paint();
        paintImage.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(source, 0, 0, paintImage);
        source.recycle();
        return output;
    }

    @Override
    public String key() {
        return "roundcorner";
    }


}
