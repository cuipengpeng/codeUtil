package com.test.xcamera.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by zll on 2019/10/24.
 */

public class DoubleClickRectView extends View {
    private Paint mPaint = new Paint();
    private Rect mRect;
    private DoubleClickLocationCallback mDoubleClickLocationCallback;
    private boolean mIsHide = false;

    public DoubleClickRectView(Context context) {
        super(context);
    }

    public DoubleClickRectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DoubleClickRectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDoubleClickLocationCallback(DoubleClickLocationCallback clickLocationCallback) {
        mDoubleClickLocationCallback = clickLocationCallback;
    }

    public void setData(MotionEvent event) {
        mIsHide = false;
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        int left = x - 200;
        int top = y - 200;
        int right = x + 200;
        int bottom = y + 200;
        mRect = new Rect(left, top, right, bottom);
        int scaleX = (left * 640) / 1905;
        int scaleY = (top * 360) / 1080;
        int width = ((right - left) * 640) / 1905;
        int heght = ((bottom - top) * 360) / 1080;
        int[] rectInfo = new int[]{scaleX, scaleY, width, heght};
        if (mDoubleClickLocationCallback != null) {
            mDoubleClickLocationCallback.location(rectInfo);
        }
        invalidate();
    }

    public void hide() {
        mIsHide = true;
        mRect = new Rect(0, 0, 0, 0);
        invalidate();
    }

    public boolean isHide() {
        return mIsHide;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mRect != null) {
            mPaint.setColor(Color.GREEN);
            mPaint.setStyle(Paint.Style.STROKE);//设置空心
            canvas.drawRect(mRect, mPaint);
        }
    }

    public interface DoubleClickLocationCallback {
        void location(int[] rectInfo);
    }
}
