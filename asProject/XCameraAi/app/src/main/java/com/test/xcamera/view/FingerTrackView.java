package com.test.xcamera.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by zll on 2019/9/20.
 */

public class FingerTrackView extends View {
    private static final String TAG = "RectView";
    private Paint mPaint = null;
    private int StrokeWidth = 5;
    private Rect rect = new Rect(0, 0, 0, 0);
    private RectLocationListener mRectLocationListener;
    private boolean mNeedDraw = false;
    private boolean mIsHide = false;

    public FingerTrackView(Context context) {
        super(context);
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
    }

    public FingerTrackView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
    }

    public void setRectLocationListener(RectLocationListener locationListener) {
        mRectLocationListener = locationListener;
    }

    public void setNeedDraw(boolean needDraw) {
        mNeedDraw = needDraw;
    }

    public void hide() {
        mIsHide = true;
        rect = new Rect(0, 0, 0, 0);
        invalidate();
    }

    public boolean isHide() {
        return mIsHide;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //设置无锯齿
        mPaint.setAntiAlias(true);
//        canvas.drawARGB(50, 255, 227, 0);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(StrokeWidth);
        mPaint.setAlpha(100);
        canvas.drawRect(rect, mPaint);
    }

    public boolean onTouch(MotionEvent event) {
        mIsHide = false;
        int x = (int) event.getX();
        int y = (int) event.getY();
        //  Log.d(TAG, "onTouchEvent: x = " + x + "  y = " + y);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mNeedDraw = true;
                rect.right += StrokeWidth;
                rect.bottom += StrokeWidth;
//                invalidate(rect);
                rect.left = x;
                rect.top = y;
                rect.right = rect.left;
                rect.bottom = rect.top;
                Log.d(TAG, "ACTION_DOWN: x = " + x + "  y = " + y);
            case MotionEvent.ACTION_MOVE:
                if (mNeedDraw) {
                    Rect old =
                            new Rect(rect.left, rect.top, rect.right + StrokeWidth, rect.bottom + StrokeWidth);
                    rect.right = x;
                    rect.bottom = y;
                    old.union(x, y);
                    if (rect.right - rect.left > 150 && rect.bottom - rect.top > 150) {
                        invalidate(old);
                        Log.d(TAG, "ACTION_MOVE: 22222 = " + rect.left + " t = " + rect.top + " r = " + rect.right + " b = " + rect.bottom);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mRectLocationListener != null) {
                    if (rect.right - rect.left > 150 && rect.bottom - rect.top > 150) {
                        int scaleX = (rect.left * 640) / 1905;
                        int scaleY = (rect.top * 360) / 1080;
                        int width = ((rect.right - rect.left) * 640) / 1905;
                        int heght = ((rect.bottom - rect.top) * 360) / 1080;
                        mRectLocationListener.location(scaleX, scaleY, width, heght);
                        Log.d(TAG, "ACTION_UP: l = " + rect.left + " t = " + rect.top + " r = " + rect.right + " b = " + rect.bottom
                                + " x = " + scaleX + " y = " + scaleY + " width = " + width + " height = " + heght);
                    }
                }
                mNeedDraw = false;
                break;
        }
        return true;
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        int x = (int) event.getX();
//        int y = (int) event.getY();
//        //  Log.d(TAG, "onTouchEvent: x = " + x + "  y = " + y);
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                rect.right += StrokeWidth;
//                rect.bottom += StrokeWidth;
//                invalidate(rect);
//                rect.left = x;
//                rect.top = y;
//                rect.right = rect.left;
//                rect.bottom = rect.top;
//                //   Log.d(TAG, "ACTION_DOWN: x = " + x + "  y = " + y);
//            case MotionEvent.ACTION_MOVE:
//                Rect old =
//                        new Rect(rect.left, rect.top, rect.right + StrokeWidth, rect.bottom + StrokeWidth);
//                rect.right = x;
//                rect.bottom = y;
//                old.union(x, y);
//                invalidate(old);
//                Log.d(TAG, "ACTION_UP: 22222 = " + rect.left + " t = " + rect.top + " r = " + rect.right + " b = " + rect.bottom);
//                break;
//
//            case MotionEvent.ACTION_UP:
//                if (mRectLocationListener != null) {
//                    int scaleX = (rect.left * 640) / 1905;
//                    int scaleY = (rect.top * 360) / 1080;
//                    int width = ((rect.right - rect.left) * 640) / 1905;
//                    int heght = ((rect.bottom - rect.top) * 360) / 1080;
//                    mRectLocationListener.location(scaleX, scaleY, width, heght);
//                    Log.d(TAG, "ACTION_UP: l = " + rect.left + " t = " + rect.top + " r = " + rect.right + " b = " + rect.bottom
//                            + " x = " + scaleX + " y = " + scaleY + " width = " + width + " height = " + heght);
//                }
//                break;
//            default:
//                break;
//        }
//        return true;
//    }

    public interface RectLocationListener {
        void location(int x, int y, int width, int height);
    }
}
