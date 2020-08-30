package com.test.xcamera.phonealbum.widget.subtitle;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;

/**
 * 滚动view, 通过滚动器通知内部子view更新界面达到滚动效果
 * Created by liutao on 19/11/2016.
 */

public class ScrollView extends FrameLayout {
    private static final String TAG = ScrollView.class.getSimpleName();

    private static final int MSG_SCROLL_FORWARD = 0;
    private static final int MSG_SCROLL_BACKWARD = 1;
    private static final int MSG_PAUSE = 2;

    /** 滚动器 */
    private WheelHorizontalScroller mScroller;
    /** 手动滚动执行中 */
    private boolean isManualScrollingPerformed;
    /** 自动滚动执行中 */
    private boolean isAutoScrollingPerformed;
    /** 大刻度代表10，小刻度代表1，小刻度再等分 */
    private int mMinScaleCount = 4;
    /** 是否调整最终刻度位置停靠在标准刻度线上 */
    private boolean isJustifyOffset = true;

    private float mDownFocusX;
    private float mDownFocusY;
    private boolean isDisallowIntercept;

    private ScrollHandler mHandler;
    private OnWheelScrollListener<Integer> mOnWheelListener;

    /** 指示刻度线值 */
    protected int mCurrentValue;
    /** 刻度最大值 */
    protected int mMaxValue = -1;
    /** 刻度最小值 */
    protected int mMinValue = -1;
    /** 滚动偏移量 */
    protected float mScrollOffset;
    /** 小刻度宽度 */
    protected int mLineDividerWidth;

    public ScrollView(Context context) {
        this(context, null);
    }

    public ScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mLineDividerWidth = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                8f,
                getResources().getDisplayMetrics());
        mScroller = new WheelHorizontalScroller(context, mScrollingListener);
        mScroller.setInterpolator(new AccelerateDecelerateInterpolator());
        mHandler = new ScrollHandler(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownFocusX = event.getX();
                mDownFocusY = event.getY();
                stopScroll();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isDisallowIntercept &&
                        Math.abs(event.getY() - mDownFocusY) < Math.abs(event.getX() - mDownFocusX)) {
                    isDisallowIntercept = true;
                    if (getParent() != null) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                isDisallowIntercept = false;
                break;
        }
        return mScroller.onTouchEvent(event);
    }

    @Override
    protected void onDetachedFromWindow() {
        mHandler.removeMessages(MSG_SCROLL_FORWARD);
        mHandler.removeMessages(MSG_SCROLL_BACKWARD);
        mHandler.removeMessages(MSG_PAUSE);
        super.onDetachedFromWindow();
    }

    WheelHorizontalScroller.ScrollingListener mScrollingListener = new WheelHorizontalScroller.ScrollingListener() {
        @Override
        public void onStarted() {
            isManualScrollingPerformed = true;
            notifyScrollingListenersAboutStart();
        }

        @Override
        public void onScroll(int distance) {
            doScroll(distance);
        }

        @Override
        public void onFinished() {
            if (thatExceed()) {
                return;
            }
            if (isManualScrollingPerformed) {
                notifyScrollingListenersAboutEnd();
                isManualScrollingPerformed = false;
            }
            if (isJustifyOffset) {
                mScrollOffset = 0;
            }
            notifyChildrenEndScroll();
        }

        @Override
        public void onJustify() {
            if (thatExceed()) {
                return;
            }
            if (!isJustifyOffset) {
                return;
            }
            if (Math.abs(mScrollOffset) > WheelHorizontalScroller.MIN_DELTA_FOR_SCROLLING) {
                if (mScrollOffset < -mLineDividerWidth / 2) {
                    mScroller.scroll((int) (mLineDividerWidth + mScrollOffset), 0);
                } else if (mScrollOffset > mLineDividerWidth / 2) {
                    mScroller.scroll((int) (mScrollOffset - mLineDividerWidth), 0);
                } else {
                    mScroller.scroll((int) mScrollOffset, 0);
                }
            }
        }
    };

    private void doScroll(int distance) {
        mScrollOffset += distance;
        int offsetCount = (int) (mScrollOffset / mLineDividerWidth);
        if (0 != offsetCount) {
            // 显示在范围内
            int oldValueIndex = Math.min(Math.max(mMinValue, mCurrentValue), mMaxValue);
            mCurrentValue -= offsetCount;
            mScrollOffset -= offsetCount * mLineDividerWidth;
        }
        notifyChildrenStartScroll();
    }

    private boolean thatExceed() {
        // 越界后需要回滚的大小值
        int outRange = 0;
        if (mCurrentValue < mMinValue) {
            outRange = (mCurrentValue - mMinValue) * mLineDividerWidth;
        } else if (mCurrentValue > mMaxValue) {
            outRange = (mCurrentValue - mMaxValue) * mLineDividerWidth;
        }
        if (0 != outRange) {
            mScrollOffset = 0;
            mScroller.scroll(-outRange, 0);
            return true;
        }
        return false;
    }

    /**
     * 通知子view开始滚动
     */
    private void notifyChildrenStartScroll() {
        for (int i = 0, len = getChildCount(); i < len; i++) {
            View child = getChildAt(i);
            if (child instanceof OnScrollListener) {
                ((OnScrollListener) child).onScroll(mScrollOffset, mCurrentValue);
            }
        }
    }

    /**
     * 通知子view停止滚动
     */
    private void notifyChildrenEndScroll() {
        for (int i = 0, len = getChildCount(); i < len; i++) {
            View child = getChildAt(i);
            if (child instanceof OnScrollListener) {
                ((OnScrollListener) child).onFinished(mScrollOffset, mCurrentValue);
            }
        }
    }

    /**
     * Adds wheel changing listener
     *
     * @param listener the listener
     */
    public void setScrollingListener(OnWheelScrollListener<Integer> listener) {
        mOnWheelListener = listener;
    }

    /**
     * Removes wheel changing listener
     */
    public void removeScrollingListener() {
        mOnWheelListener = null;
    }

    /**
     * Notifies changing listeners
     *
     * @param oldValue the old wheel value
     * @param newValue the new wheel value
     */
    protected void notifyScrollingListeners(int oldValue, int newValue) {
        if (null != mOnWheelListener) {
            mOnWheelListener.onChanged(this, oldValue, newValue);
        }
    }

    private void notifyScrollingListenersAboutStart() {
        if (null != mOnWheelListener) {
            mOnWheelListener.onScrollingStarted(this);
        }
    }

    private void notifyScrollingListenersAboutEnd() {
        if (null != mOnWheelListener) {
            mOnWheelListener.onScrollingFinished(this);
        }
    }

    /**
     * 设置刻度数据
     */
    public void setScaleList(int max) {
        mMinValue = 0;
        mMaxValue = max;
        for (int i = 0, len = getChildCount(); i < len; i++) {
            View view = getChildAt(i);
            if (view instanceof DataProtocol) {
                ((DataProtocol) view).setScaleWidth(mLineDividerWidth);
                ((DataProtocol) view).setScaleList(max);
            }
        }
    }

    public void selectScale(int scale, float offset) {
        for (int i = 0, len = getChildCount(); i < len; i++) {
            View view = getChildAt(i);
            if (view instanceof DataProtocol) {
                ((DataProtocol) view).selectScale(scale, offset);
            }
        }
    }

    /**
     * 开始平滑向前滚动
     */
    public void startForwardScroll() {
        mScrollOffset = 0;
        mHandler.sendEmptyMessage(MSG_SCROLL_FORWARD);
    }

    /**
     * 开始平滑向后滚动
     */
    public void startBackwardScroll() {
        mScrollOffset = 0;
        mHandler.sendEmptyMessage(MSG_SCROLL_BACKWARD);
    }

    /**
     * 停止滚动
     */
    public void stopScroll() {
        mHandler.sendEmptyMessage(MSG_PAUSE);
    }

    /**
     * 设置最小刻度下还可细分的刻度数目
     * @param count
     */
    public void setMinScaleCount(int count) {
        mMinScaleCount = count;
    }

    /**
     * 设置最终停靠位置对准刻度线
     * @param enable
     */
    public void enableJustifyOffset(boolean enable) {
        isJustifyOffset = enable;
    }

    private static class ScrollHandler extends Handler {

        private WeakReference<ScrollView> mScrollViewRef;
        private long mInterval;
        private float mMinScaleOffset;

        public ScrollHandler(ScrollView view) {
            mScrollViewRef = new WeakReference<>(view);
            mInterval = 100 / view.mMinScaleCount;
            mMinScaleOffset = view.mLineDividerWidth / view.mMinScaleCount;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ScrollView view = mScrollViewRef.get();
            if (view == null) {
                return;
            }
            switch (msg.what) {
                case MSG_SCROLL_FORWARD:
                    view.isAutoScrollingPerformed = true;
                    view.mScrollOffset -= mMinScaleOffset;
                    if (view.mScrollOffset <= -view.mLineDividerWidth) {
                        view.mCurrentValue += 1;
                        view.mScrollOffset = 0;
                    }
                    if (view.mCurrentValue >= view.mMaxValue) {
                        view.mCurrentValue = view.mMaxValue;
                        view.mScrollOffset = 0;
                        sendEmptyMessage(MSG_PAUSE);
                        return;
                    }
                    view.notifyChildrenStartScroll();
                    sendEmptyMessageDelayed(MSG_SCROLL_FORWARD, mInterval);
                    break;
                case MSG_SCROLL_BACKWARD:
                    view.isAutoScrollingPerformed = true;
                    view.mScrollOffset += mMinScaleOffset;
                    if (view.mScrollOffset >= view.mLineDividerWidth) {
                        view.mCurrentValue -= 1;
                        view.mScrollOffset = 0;
                    }
                    if (view.mCurrentValue <= view.mMinValue) {
                        view.mCurrentValue = view.mMinValue;
                        view.mScrollOffset = 0;
                        sendEmptyMessage(MSG_PAUSE);
                        return;
                    }
                    view.notifyChildrenStartScroll();
                    sendEmptyMessageDelayed(MSG_SCROLL_BACKWARD, mInterval);
                    break;
                case MSG_PAUSE:
                    view.isAutoScrollingPerformed = false;
                    removeMessages(MSG_PAUSE);
                    removeMessages(MSG_SCROLL_FORWARD);
                    removeMessages(MSG_SCROLL_BACKWARD);
                    view.notifyChildrenEndScroll();
                    break;
                default:
                    break;
            }
        }
    }

    interface OnWheelScrollListener<T> {
        /**
         * Callback method to be invoked when current item changed
         *
         * @param wheel    the wheel view whose state has changed
         * @param oldValue the old value of current item
         * @param newValue the new value of current item
         */
        void onChanged(ScrollView wheel, T oldValue, T newValue);

        /**
         * Callback method to be invoked when scrolling started.
         *
         * @param wheel the wheel view whose state has changed.
         */
        void onScrollingStarted(ScrollView wheel);

        /**
         * Callback method to be invoked when scrolling ended.
         *
         * @param wheel the wheel view whose state has changed.
         */
        void onScrollingFinished(ScrollView wheel);
    }
}
