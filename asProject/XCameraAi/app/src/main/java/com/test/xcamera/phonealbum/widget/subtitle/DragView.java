package com.test.xcamera.phonealbum.widget.subtitle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;

import com.test.xcamera.R;


/**
 * 自定义drag view比较麻烦的是drag view包含一个拖拽区域和一根指示线，
 * 指示线本身是有宽度的，如果设定drag view的宽度等于拖拽区域的宽度显然
 * 是不够的，多出的指示线无法绘制完全。因此在onDraw方法里调用inset扩展
 * x轴绘制区域，同时扩展了右边的绘制区域，因而updateBounds方法看起来
 * 比较奇怪，会做一些区域校正。
 *
 * Created by liutao on 29/11/2016.
 */

public class DragView extends View {

    private Bitmap mBitmap;
    private RectF mRectF = new RectF();
    private Rect mClipRect = new Rect();
    private Paint mRectPaint;
    private float mTopOffset;

    private float mRectCorner;

    public DragView(Context context) {
        this(context, null);
    }

    public DragView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mRectCorner = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                4f,
                getResources().getDisplayMetrics());
        mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectPaint.setColor(ContextCompat.getColor(getContext(), R.color.color_ffcf00));
        mTopOffset = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 23f, getResources().getDisplayMetrics());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.getClipBounds(mClipRect);
        // x轴两个方向均扩大了绘制区域
        mClipRect.inset(0, 0);
        canvas.clipRect(mClipRect);
        float right;
        canvas.drawRoundRect(mRectF, mRectCorner, mRectCorner, mRectPaint);
        canvas.drawRect(mRectF.left, mRectF.top, mRectF.left + mRectCorner, mRectF.bottom, mRectPaint);
//        canvas.drawBitmap(mBitmap, mRectF.left - mBitmap.getWidth() / 2f, mTopOffset, null);
    }

    public void updateBounds(float left, float top, float right, float bottom) {
        setLayoutParams(new FrameLayout.LayoutParams(
                Math.round(right - left),
                Math.round(bottom)));
        setTranslationX(Math.round(left));
        mRectF.set(0, top, right - left, bottom);
        invalidate();
    }

    public float getAdjustOffset() {
        return 0;
    }
}
