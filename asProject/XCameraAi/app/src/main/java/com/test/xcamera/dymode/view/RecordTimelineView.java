package com.test.xcamera.dymode.view;

/**
 * Created by zll on 2020/3/27.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.test.xcamera.activity.DyFPVActivity;
import java.util.concurrent.CopyOnWriteArrayList;

public class RecordTimelineView extends View {
    private final static String TAG = RecordTimelineView.class.getSimpleName();
    private static final int SET_DURATION = 11111;
    private static final int CLIP = 22222;
    private static final int DELETE = 33333;
    private static final int CLEAR = 44444;
    private static final int RESET = 55555;
    public static final int RECORD_MAX_TIME = 60 * 1000;
    public static final int RECORD_MIN_TIME = 15 * 1000;
    private int maxDuration;
    private int minDuration;
    private CopyOnWriteArrayList<DrawInfo> clipDurationList = new CopyOnWriteArrayList<>();
    private DrawInfo currentClipDuration = new DrawInfo();
    private Paint paint = new Paint();
    private RectF rectF = new RectF();
    private int durationColor;
    private int selectColor;
    private int offsetColor;
    private int backgroundColor;
    private boolean isSelected = false;
    private RecordProgressStateCallback mRecordProgressStateCallback;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case SET_DURATION:
//                    int duration = msg.arg1;
                    float duration = (float) msg.obj;
//                    if (isSelected) {
//                        for (DrawInfo info : clipDurationList) {
//                            if (info.drawType == DrawType.SELECT) {
//                                info.drawType = DrawType.DURATION;
//                                isSelected = false;
//                                break;
//                            }
//                        }
//                    }
                    currentClipDuration.drawType = DrawType.DURATION;

                    currentClipDuration.length = duration;
                    Log.d("test record", "currentDuration :" + duration + " ,cache TotalDuration :" + (getTimelineDuration() + duration));
                    invalidate();
                    break;
                case CLIP:
                    clipDurationList.add(currentClipDuration);
                    DrawInfo info = new DrawInfo();
                    info.length = px2dip(getContext(), maxDuration / 100);
                    info.drawType = DrawType.OFFSET;
                    clipDurationList.add(info);
                    currentClipDuration = new DrawInfo();
                    invalidate();
                    if (mRecordProgressStateCallback != null)
                        mRecordProgressStateCallback.clipSegment(getTimelineDuration());
                    break;
                case DELETE:
                    if (clipDurationList.size() >= 2) {
                        clipDurationList.remove(clipDurationList.size() - 1);
                        clipDurationList.remove(clipDurationList.size() - 1);
                    }
                    DyFPVActivity.testLog("record line view delete, callback: " + mRecordProgressStateCallback);
                    invalidate();
                    if (mRecordProgressStateCallback != null)
                        mRecordProgressStateCallback.deleteSegment(getTimelineDuration());
                    break;
                case CLEAR:
                    if (clipDurationList.size() >= 2) {
                        clipDurationList.clear();
                    }
                    invalidate();
                    break;
                case RESET:
                    invalidate();
                    break;
            }
        }
    };

    public RecordTimelineView(Context context) {
        super(context);
        init();
    }

    public RecordTimelineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecordTimelineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint.setAntiAlias(true);
    }

    public void setRecordProgressStateCallback(RecordProgressStateCallback deleteFlagCallback) {
        DyFPVActivity.testLog("record time line view set delete flag callback");
        mRecordProgressStateCallback = deleteFlagCallback;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (backgroundColor != 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                paint.setColor(getResources().getColor(backgroundColor));
                float l = 0;
                float t = 0;
                float r = getWidth();
                float b = getHeight();
                float rx = getWidth();
                float ry = getWidth();
                canvas.drawRoundRect(l, t, r, b, rx, ry, paint);
            } else {
                canvas.drawColor(getResources().getColor(backgroundColor));
            }
        }
        int lastTotalDuration = 0;
        for (int i = 0; i < clipDurationList.size(); i++) {
            DrawInfo info = clipDurationList.get(i);
            switch (info.drawType) {
                case OFFSET:
                    paint.setColor(getResources().getColor(offsetColor));
                    break;
                case DURATION:
                    paint.setColor(getResources().getColor(durationColor));
                    break;
                case SELECT:
                    paint.setColor(getResources().getColor(selectColor));
                    break;
                default:
                    paint.setColor(getResources().getColor(offsetColor));
            }

            if (info.drawType == DrawType.OFFSET) {
                float l = 0;
                float t = (lastTotalDuration - info.length) / (float) maxDuration * getHeight();
                float r = getWidth();
                float b = lastTotalDuration / (float) maxDuration * getHeight();
                canvas.drawRect(l, t, r, b, paint);
            } else {
                //第一个片段，在最上侧添加半圆
                if (i == 0) {
                    float l = 0;
                    float t = 0;
                    float r = getWidth();
                    float b = getWidth();
                    rectF.set(l, t, r, b);
                    canvas.drawArc(rectF, 180, 180, true, paint);
                    float bottom = (lastTotalDuration + info.length) / (float) maxDuration * getHeight();
                    if (bottom > getWidth() / 2) {
                        float l1 = 0;
                        float t1 = getWidth() / 2;
                        float r1 = getWidth();
                        float b1 = bottom;
                        canvas.drawRect(l1, t1, r1, b1, paint);
                    }

                } else {
                    float l = 0;
                    float t = lastTotalDuration / (float) maxDuration * getHeight();
                    float r = getHeight();
                    float b = (lastTotalDuration + info.length) / (float) maxDuration * getHeight();
                    canvas.drawRect(l, t, r, b, paint);
                }
                lastTotalDuration += info.length;
            }
        }
        if (currentClipDuration != null && currentClipDuration.length != 0) {
            paint.setColor(getResources().getColor(durationColor));

            float top = lastTotalDuration / (float) maxDuration * getHeight();
            float bottom = (lastTotalDuration + currentClipDuration.length) / (float) maxDuration * getHeight();

            //第一个片段，在最上侧添加半圆
            if (clipDurationList.size() == 0) {
                rectF.set(0, 0, getWidth(), getWidth());
                canvas.drawArc(rectF, 180, 180, true, paint);
                if (bottom > getWidth() / 2) {
                    float l = 0;
                    float t = top + getWidth() / 2;
                    float r = getWidth();
                    float b = bottom;
                    canvas.drawRect(l, t, r, b, paint);
                }
            } else {
                canvas.drawRect(0, top, getWidth(), bottom, paint);
            }

        }
        // 画15s
//        if (lastTotalDuration + currentClipDuration.length < minDuration) {
//            paint.setColor(getResources().getColor(offsetColor));
//            float l = 0;
//            float t = minDuration / (float) maxDuration * getHeight();
//            float r = getWidth();
//            float b = (minDuration + maxDuration / 300) / (float) maxDuration * getHeight();
//            canvas.drawRect(l, t, r, b, paint);
//        }
    }

    public void clipComplete() {
        mHandler.sendEmptyMessage(CLIP);
    }

    public void deleteLast() {
        mHandler.sendEmptyMessage(DELETE);
    }

    public void clear() {
        mHandler.sendEmptyMessage(CLEAR);
    }

    public void selectLast() {
        if (clipDurationList.size() >= 2) {
            DrawInfo info = clipDurationList.get(clipDurationList.size() - 2);
            info.drawType = DrawType.SELECT;
            invalidate();
            isSelected = true;
        }
    }

    public int getMaxDuration() {
        return this.maxDuration;
    }

    public void setMaxDuration(int duration) {
        this.maxDuration = duration;
    }

    public void resetMaxDuration(int duration) {
        this.maxDuration = duration;
        mHandler.sendEmptyMessage(RESET);
    }

    public void setMinDuration(int minDuration) {
        this.minDuration = minDuration;
    }

    public void setDuration(float duration) {
        Message message = Message.obtain();
        message.what = SET_DURATION;
        message.obj = duration;
        mHandler.sendMessage(message);
    }

    public void setColor(int duration, int select, int offset, int backgroundColor) {
        this.durationColor = duration;
        this.selectColor = select;
        this.offsetColor = offset;
        this.backgroundColor = backgroundColor;
    }

    /**
     * 获取时长控件显示的时间
     *
     * @return duration ms
     */
    public float getTimelineDuration() {
        float duration = 0;
        for (DrawInfo drawInfo : clipDurationList) {
            if (drawInfo.drawType == DrawType.DURATION) {
                duration += drawInfo.length;
            }
        }
        return duration;
    }

    class DrawInfo {
        float length;
        DrawType drawType = DrawType.DURATION;

        @Override
        public String toString() {
            return "DrawInfo{" +
                    "length=" + length +
                    ", drawType=" + drawType +
                    '}';
        }
    }

    enum DrawType {
        /**
         * 边界
         */
        OFFSET,
        /**
         * 时长
         */
        DURATION,
        /**
         * 当前
         */
        SELECT
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public interface RecordProgressStateCallback {
        void deleteSegment(float recordTime);
        void clipSegment(float recordTime);
    }
}

