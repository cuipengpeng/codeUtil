package com.jf.jlfund.weight.dialog;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.jf.jlfund.utils.ActivityManager;
import com.jf.jlfund.utils.DensityUtil;


/**
 * Created by 55 on 2017/11/9.
 */

public class CustomProgressBar extends View {

    private static final String TAG = "CustomProgressBar";

    private RectF bgRectF;
    private RectF proRectF;

    private Paint bgPaint;  //后台的进度画笔
    private Paint proPaint;   //前台进度的画笔
    private Paint textPaint; //百分比的画笔

    private int mWidth;
    private int mHeight;

    private int textColor = Color.parseColor("#0084ff");       //字体颜色
    private int bgColor = Color.parseColor("#ebebeb");   //进度条背景色
    private int proColor = Color.parseColor("#0084ff"); //进度条颜色


    private int textHeight; //百分比文字高度
    private int progressBarHeight = DensityUtil.dip2px(8);  //progressBar的高度:35dp


    public CustomProgressBar(Context context) {
        super(context);
        init(null);
    }

    public CustomProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        initPaint();
    }

    private void initPaint() {
        bgPaint = new Paint();
        bgPaint.setColor(bgColor);
        bgPaint.setAntiAlias(true);

        proPaint = new Paint();
        proPaint.setAntiAlias(true);
        proPaint.setColor(proColor);

        textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(DensityUtil.dip2px(14));
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        Log.e(TAG, "onSizeChanged: " + mWidth + " -- " + mHeight);
        if (bgRectF == null || proRectF == null) {
            initRectF();
        }
    }

    private void initRectF() {
        textHeight = calculPercentTxtHeight();
        int borderTop = textHeight + textHeight / 2 + DensityUtil.dip2px(10);
        Log.e(TAG, "initRectF: textHeight: " + textHeight + " borderTop: " + borderTop + " mWidth: " + mWidth + " mHeight: " + mHeight);
        bgRectF = new RectF(0, borderTop, mWidth - 3, mHeight);
        proRectF = new RectF(0, borderTop, mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText((int) (currPercent * 100) + "%", mWidth / 2, textHeight + textHeight / 2, textPaint);
        canvas.drawRoundRect(bgRectF, 35, 35, bgPaint);
        proRectF.set(bgRectF.left, proRectF.top, progressWidth, proRectF.bottom);
        canvas.drawRoundRect(proRectF, 35, 35, proPaint);
    }


    private float currPercent = 0.0f;
    private int progressWidth = 0;

    public void setPercent(float percent) {
        if (percent <= 1) {
            currPercent = percent;
            progressWidth = (int) (mWidth * currPercent);
        } else {
            currPercent = 1;
            progressWidth = mWidth;
        }
        Log.e(TAG, "setProgress: percent: " + percent + " currPercent: " + currPercent + " progressWidth: " + progressWidth);
        post(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        });
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        Log.e(TAG, "onMeasure: widthSize-> " + widthSize + "  widthMode-> " + widthMode + "  heightSize-> " + heightSize + "  heightMode: " + heightMode);
        float finalHeight = textHeight + DensityUtil.dip2px(15) + DensityUtil.dip2px(progressBarHeight);     //最终高度为字体高度+10dp的margin+15dp的进度条高度
        Log.e(TAG, "onMeasure: finalHeight: " + finalHeight);
        setMeasuredDimension(widthSize, (int) finalHeight);
        mWidth = widthSize;
        mHeight = (int) finalHeight;
        if (bgRectF == null || proRectF == null) {       //在gone-->visible的时候不走onSizeChanged方法。。。。
            initRectF();
        }
    }

    private int calculPercentTxtHeight() {
        Rect textRectF = new Rect();
        textPaint.getTextBounds("20%", 0, "20%".length(), textRectF);
        return textRectF.height();
    }

}
