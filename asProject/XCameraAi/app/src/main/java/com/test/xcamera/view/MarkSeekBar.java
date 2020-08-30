package com.test.xcamera.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.test.xcamera.R;

import java.util.ArrayList;

/**
 * Created by 周 on 2019/10/25.
 */
public class MarkSeekBar extends SeekBar {

    private final Bitmap markPointIcon;
    private long maxPro;
    private ArrayList<Long> markPointList;

    public MarkSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        markPointIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.markpointicon);
    }

    /**
     * 设置当前需要mark的点和总的大小
     *
     * @param maxPro
     * @param currentPro
     */
    public void setCurrentMarkPoint(long maxPro, ArrayList<Long> currentPro) {
        this.maxPro = maxPro;
        this.markPointList = currentPro;
        invalidate();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int measuredHeight = this.getMeasuredHeight();
        int measuredWidth = this.getMeasuredWidth();
        if (markPointList == null)
            return;
        for (int i = 0; i < markPointList.size(); i++) {
            long aLong = markPointList.get(i);
            float left = ((float) aLong / (float) maxPro) * measuredWidth;
            float top = measuredHeight / 4;
            canvas.drawBitmap(markPointIcon, left, top, null);
        }
//        for (long currentPro : markPointList) {
//            float left = (measuredWidth * currentPro) / maxPro;
//            float top = measuredHeight / 4;
//            canvas.drawBitmap(markPointIcon, left, top, null);
//        }
    }
}
