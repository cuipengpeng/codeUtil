package com.test.xcamera.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.SeekBar;

/**
 * Created by zhouxuecheng on
 * Create Time 2019/10/31
 * e-mail zhouxuecheng1991@163.com
 */

@SuppressLint("AppCompatCustomView")
public class VerticalSeekBarFilter extends SeekBar {
    private OnSeekBarChangeListener mOnSeekBarChangeListener;
    private int i = 0;
    private float x;
    private Paint paint = new Paint();
    private TouchSeekBar touchSeekBar;

    public VerticalSeekBarFilter(Context context) {
        super(context);
    }


    public VerticalSeekBarFilter(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public VerticalSeekBarFilter(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setTextSize(100);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.parseColor("#ffffff"));
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        mOnSeekBarChangeListener = l;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }


    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(heightMeasureSpec, widthMeasureSpec);

        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());

    }

    @Override
    protected void onDraw(Canvas c) {

        //将SeekBar转转90度
        c.rotate(-90);
        //将旋转后的视图移动回来
        c.translate(-getHeight(), 0);


        super.onDraw(c);

        c.drawText("hahaha", 100, -80, paint);
    }

    void onStartTrackingTouch() {
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onStartTrackingTouch(this);
        }
    }

    void onProgressChanged() {
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onProgressChanged(this, i, true);
        }
    }

    void onStopTrackingTouch() {
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onStopTrackingTouch(this);
        }
    }

    @Override

    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onStartTrackingTouch();
                x = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                //获取滑动的距离
                i = getMax() - (int) (getMax() * event.getY() / getHeight());
                //设置进度
                x = event.getY();
                setSeekBarProgress(i);
                if (touchSeekBar != null) {
                    touchSeekBar.move((int) x,i);
                }

                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                onStopTrackingTouch();
                break;
            case MotionEvent.ACTION_CANCEL:
                onStopTrackingTouch();
                break;
        }
        return true;
    }

    public interface TouchSeekBar {
        void move(int x, int offSet);
    }

    public void setTouchSeekBar(TouchSeekBar touchSeekBar) {
        this.touchSeekBar = touchSeekBar;
    }

    public void setSeekBarProgress(int i) {
        setProgress(i);

        Log.i("Progress", getProgress() + "");

        //每次拖动SeekBar都会调用

        onSizeChanged(getWidth(), getHeight(), 0, 0);

        Log.i("getWidth()", getWidth() + "");

        Log.i("getHeight()", getHeight() + "");
        onProgressChanged();
    }
}
