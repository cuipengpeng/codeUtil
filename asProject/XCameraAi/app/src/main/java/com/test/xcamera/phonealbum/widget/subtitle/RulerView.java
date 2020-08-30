package com.test.xcamera.phonealbum.widget.subtitle;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.test.xcamera.R;


/**
 * 刻度尺view
 */

public class RulerView extends View implements OnScrollListener, DataProtocol {

    private static final String TAG = RulerView.class.getSimpleName();
    private int mTextSize;
    private int mTextColor;

    private int mMaxLineHeight;
    private int mMidLineHeight;
    private int mMinLineHeight;

    private int mMaxLineColor;
    private int mMidLineColor;
    private int mMinLineColor;

    private int mLineStrokeWidth;
    private int mLineDividerWidth;

    private int mCurrentValue;
    private int mMaxValue = -1;
    private int mMinValue = -1;

    private Paint mMaxLinePaint;
    private Paint mMidLinePaint;
    private Paint mMinLinePaint;
    private TextPaint mTextPaint;

    /** 刻度文字水平偏移量 */
    private float mTextHorizontalOffset;
    /** 刻度文字垂直偏移量 */
    private float mTextVerticalOffset;
    /** 刻度尺移动偏移量 */
    private float mScrollOffset;

    public RulerView(Context context) {
        this(context, null);
    }

    public RulerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        int defaultTextSize = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                8f,
                getResources().getDisplayMetrics());
        int defaultMaxLineHeight = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                14f,
                getResources().getDisplayMetrics());
        int defaultMidLineHeight = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                4f,
                getResources().getDisplayMetrics());
        int defaultMinLineHeight = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                2f,
                getResources().getDisplayMetrics());
        int defaultLineStrokeWidth = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                1f,
                getResources().getDisplayMetrics());
        int defaultLineDividerWidth = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                5f,
                getResources().getDisplayMetrics());

        mTextHorizontalOffset = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                3f,
                getResources().getDisplayMetrics());

        mTextVerticalOffset = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                6.5f,
                getResources().getDisplayMetrics());

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RulerView);
        mTextSize = array.getDimensionPixelSize(
                R.styleable.RulerView_android_textSize,
                defaultTextSize);
        mTextColor = array.getColor(
                R.styleable.RulerView_android_textColor,
                ContextCompat.getColor(context, R.color.white));
        mMaxLineHeight = array.getDimensionPixelSize(
                R.styleable.RulerView_maxLineHeight,
                defaultMaxLineHeight);
        mMidLineHeight = array.getDimensionPixelSize(
                R.styleable.RulerView_midLineHeight,
                defaultMidLineHeight);
        mMinLineHeight = array.getDimensionPixelSize(
                R.styleable.RulerView_minLineHeight,
                defaultMinLineHeight);

        mLineStrokeWidth = array.getDimensionPixelSize(
                R.styleable.RulerView_lineStrokeWidth,
                defaultLineStrokeWidth);

        mLineDividerWidth = array.getDimensionPixelSize(
                R.styleable.RulerView_lineDividerWidth,
                defaultLineDividerWidth);

        mMaxLineColor = array.getColor(
                R.styleable.RulerView_maxLineColor,
                ContextCompat.getColor(context, R.color.white));
        mMidLineColor = array.getColor(
                R.styleable.RulerView_midLineColor,
                ContextCompat.getColor(context, R.color.white));
        mMinLineColor = array.getColor(
                R.styleable.RulerView_minLineColor,
                ContextCompat.getColor(context, R.color.white));

        array.recycle();
        initPainters();
    }

    private void initPainters() {
        mMaxLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMidLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMinLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mMaxLinePaint.setStrokeWidth(mLineStrokeWidth);
        mMidLinePaint.setStrokeWidth(mLineStrokeWidth);
        mMinLinePaint.setStrokeWidth(mLineStrokeWidth);
        mMaxLinePaint.setColor(mMaxLineColor);
        mMidLinePaint.setColor(mMidLineColor);
        mMinLinePaint.setColor(mMinLineColor);
        mMaxLinePaint.setStyle(Paint.Style.FILL);
        mMidLinePaint.setStyle(Paint.Style.FILL);
        mMinLinePaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setColor(mTextColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawRuler(canvas);
    }

    private void drawRuler(Canvas canvas) {
        final float halfWidth = (getWidth() - getPaddingLeft() - getPaddingRight()) / 2f;
        // 根据间隔计算当前一半宽度的个数
        final int halfCount = (int) Math.ceil(halfWidth / mLineDividerWidth);
        final int currentValue = mCurrentValue;
        final float distanceX = mScrollOffset;
        int value;
        float positionX;

        for (int i = 0; i < halfCount; i++) {
            positionX = halfWidth + i * mLineDividerWidth + distanceX;
            value = currentValue + i;
            // 画屏幕右侧区域
            drawRulerLine(positionX, value, canvas);

            positionX = halfWidth - (i + 1) * mLineDividerWidth + distanceX;
            value = currentValue - (i + 1);
            // 画屏幕左侧区域
            drawRulerLine(positionX, value, canvas);
        }
    }

    private void drawRulerLine(float positionX, int value, Canvas canvas) {
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int positionY = getHeight() - getPaddingBottom();
        if (positionX > width || value < mMinValue || value > mMaxValue) {
            return;
        }
        if (value % 10 == 0) {
            canvas.drawLine(
                    positionX,
                    positionY,
                    positionX,
                    positionY - mMaxLineHeight,
                    mMaxLinePaint);
            canvas.drawText(
                    TimeUtils.getDuration(value * 100),
                    positionX + mTextHorizontalOffset,
                    positionY - mTextVerticalOffset,
                    mTextPaint);
        } else if (value % 2 == 0) {
            canvas.drawLine(
                    positionX,
                    positionY,
                    positionX,
                    positionY - mMidLineHeight,
                    mMidLinePaint);
        } else {
            canvas.drawLine(
                    positionX,
                    positionY, positionX,
                    positionY - mMinLineHeight,
                    mMinLinePaint);
        }
    }

    @Override
    public void setScaleList(int max) {
        mMinValue = 0;
        mMaxValue = max;
        invalidate();
    }

    @Override
    public void setScaleWidth(int width) {
        mLineDividerWidth = width;
    }

    @Override
    public void selectScale(int currentValue, float offset) {
        if (currentValue < mMinValue) {
            currentValue = mMinValue;
        } else if (currentValue > mMaxValue) {
            currentValue = mMaxValue;
        }
        mCurrentValue = currentValue;
        mScrollOffset = offset;
        invalidate();
    }

    @Override
    public void onScroll(float scrollOffset, int currentValue) {
        mScrollOffset = scrollOffset;
        mCurrentValue = currentValue;
        invalidate();
    }

    @Override
    public void onFinished(float scrollOffset, int currentValue) {
        mScrollOffset = scrollOffset;
        mCurrentValue = currentValue;
        invalidate();
    }
}
