package com.test.xcamera.phonealbum.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.test.xcamera.R;
import com.test.xcamera.utils.DensityUtils;

public class TimeSpan extends RelativeLayout {
    private String TAG = "NvsTimelineTimeSpan";
    private float prevRawX = 0;
    private int currentPosition = -1;
    private boolean mCanMoveHandle = false;
    private OnTrimInChangeListener mOnTrimInChangeListener;
    private OnTrimOutChangeListener mOnTrimOutChangeListener;
    private OnMarginChangeListener mMarginChangeListener;
    //
    private int mHandleWidth = 0;
    private long mInPoint = 0;//裁剪入点
    private long mOutPoint = 0;//裁剪出点
    private double mPixelPerMicrosecond = 0D;//每秒所显示的像素值
    public boolean hasSelected = true;
    private int mMinTimeSpanPixel = 0;//最小timespan宽度
    private int mMaxTimeSpanPixel = 0;//最大timespan宽度
    private int originLeft = 0;
    private int originRight = 0;
    private int parentWidth = -1;
    private int dragDirection = 0;
    private static final int LEFT = 0x16;
    private static final int CENTER = 0x17;
    private static final int RIGHT = 0x18;

    private View timeSpanshadowView;
    private ImageView leftHandleView;
    private ImageView rightHandleView;
    private OnTrimChangeListener mOnTrimChangeListener;
    private final float thumbnailImageWidth = 70f;

    public TimeSpan(Context context) {
        this(context, null);
    }

    public TimeSpan(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeSpan(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.timespan, this);
        leftHandleView = view.findViewById(R.id.leftHandle);
        rightHandleView = view.findViewById(R.id.rightHandle);
        timeSpanshadowView = view.findViewById(R.id.timeSpanShadow);
        mHandleWidth = leftHandleView.getLayoutParams().width;
        mMinTimeSpanPixel = DensityUtils.dp2px(context, thumbnailImageWidth);
    }


    private void IsSelectedTimeSpan() {
        if (leftHandleView == null || rightHandleView == null) {
            return;
        }

//        if (hasSelected) {
//            leftHandleView.setVisibility(View.VISIBLE);
//            rightHandleView.setVisibility(View.VISIBLE);
//        } else {
//            leftHandleView.setVisibility(View.INVISIBLE);
//            rightHandleView.setVisibility(View.INVISIBLE);
//        }
    }

    public void setHasSelected(boolean hasSelected) {
        this.hasSelected = hasSelected;
        leftHandleView.setVisibility(View.VISIBLE);
        rightHandleView.setVisibility(View.VISIBLE);
        timeSpanshadowView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!hasSelected) {
            return false;//未被选中，不作编辑操作
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("####", "ACTION_DOWN--1");
                mCanMoveHandle = !(mHandleWidth < ev.getX() && ev.getX() < getWidth() - mHandleWidth);
                Log.d("####", "mCanMoveHandle = "+mCanMoveHandle);
                if(mCanMoveHandle){
                    getParent().requestDisallowInterceptTouchEvent(true);
                }else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                if(parentWidth <0){
                    parentWidth = ((ViewGroup)getParent()).getWidth();
                }
                originLeft = getLeft();
                originRight = getRight();
                prevRawX = (int) ev.getRawX();
                dragDirection = getDirection((int) ev.getX(), (int) ev.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                bringToFront();
                Log.d("####", "ACTION_MOVE--2");
//                getParent().requestDisallowInterceptTouchEvent(true);
                float tempRawX = ev.getRawX();
                int dx = (int) Math.floor(tempRawX - prevRawX + 0.5D);
                prevRawX = tempRawX;
                if (dragDirection == LEFT) {
                    Log.d("####", "left");
                    //左dragView的左边界
                    if (originLeft+dx <= 0) {
                        dx = -originLeft;
                    }
                    //左dragView的右边界
                    if (originRight - (originLeft+dx) <= mMinTimeSpanPixel) {
                        dx = originRight - originLeft - mMinTimeSpanPixel;
                    }
                    if (mOnTrimChangeListener != null) {
                        mOnTrimChangeListener.onChange(currentPosition, true, dx);
                    }
                    originLeft += dx;
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) getLayoutParams();
                    lp.width = originRight - originLeft;
                    lp.setMargins(originLeft, FrameLayout.LayoutParams.MATCH_PARENT, parentWidth -originRight, 0);
                    setLayoutParams(lp);
                    mInPoint = (long) Math.floor(originLeft / mPixelPerMicrosecond + 0.5D);
                    if (mOnTrimInChangeListener != null) {
                        mOnTrimInChangeListener.onChange(mInPoint, false);
                    }
                }
                if (dragDirection == RIGHT) {
                    Log.d("####", "right");
                    //右dragView的右边界
                    if (originRight+dx >= parentWidth) {
                        dx = parentWidth-originRight;
                    }
                    //右dragView的左边界
                    if (originRight+dx - originLeft <= mMinTimeSpanPixel) {
                        dx = originRight - originLeft - mMinTimeSpanPixel;
                    }

                    if (mOnTrimChangeListener != null) {
                        mOnTrimChangeListener.onChange(currentPosition, false, dx);
                    }
                    originRight += dx;
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) getLayoutParams();
                    lp.width = originRight - originLeft;
                    lp.setMargins(originLeft, FrameLayout.LayoutParams.MATCH_PARENT, parentWidth -originRight, 0);
                    setLayoutParams(lp);
                    mOutPoint = (long) Math.floor(originRight / mPixelPerMicrosecond + 0.5D);
                    if (mOnTrimOutChangeListener != null) {
                        mOnTrimOutChangeListener.onChange(mOutPoint, false);
                    }

                }
                if (mMarginChangeListener != null) {
                    mMarginChangeListener.onChange(originLeft, originRight - originLeft);
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d("####", "ACTION_UP--3");
                getParent().requestDisallowInterceptTouchEvent(false);
                if ((dragDirection == LEFT) && mOnTrimInChangeListener != null) {
                    mOnTrimInChangeListener.onChange(mInPoint, true);
                }
                if ((dragDirection == RIGHT) && mOnTrimOutChangeListener != null) {
                    mOnTrimOutChangeListener.onChange(mOutPoint, true);
                }
                Log.d("####", "mCanMoveHandle = "+mCanMoveHandle);
                break;
        }
//        return mCanMoveHandle;
        return true;
    }

    public interface OnMarginChangeListener {
        void onChange(int leftMargin, int timeSpanWidth);
    }

    public interface OnTrimInChangeListener {
        void onChange(long timeStamp, boolean isDragEnd);
    }

    public interface OnTrimOutChangeListener {
        void onChange(long timeStamp, boolean isDragEnd);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        IsSelectedTimeSpan();
    }

    //判断触摸方向是左边还是右边
    private int getDirection(int x, int y) {
        int left = getLeft();
        int right = getRight();

        if (x < mHandleWidth) {
            return LEFT;
        }
        if (right - left - x < mHandleWidth) {
            return RIGHT;
        }
        return CENTER;
    }


    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public interface OnTrimChangeListener {
        void onChange(int currentPosition, boolean dragLeft, int distance);
    }

    public void setOnTrimChangeListener(OnTrimChangeListener onTrimChangeListener){
        this.mOnTrimChangeListener = onTrimChangeListener;
    }

}
