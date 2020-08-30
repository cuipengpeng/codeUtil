package com.test.xcamera.phonealbum.widget.subtitle;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;


import com.test.xcamera.R;
import com.test.xcamera.phonealbum.widget.subtitle.bean.Mark;

import java.lang.ref.WeakReference;

/**
 * 游标卡尺，drag view 即游标，拖动游标到目标位置。
 * 内部有一个滚动器，通知子view更新界面，达到滚动的效果
 */

public abstract class CaliperLayout extends FrameLayout {
    private static final String TAG = CaliperLayout.class.getSimpleName();

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
    private boolean isJustifyOffset = false;

    private float mDownFocusX;
    private float mDownFocusY;
    private boolean isDisallowIntercept;
    /** drag view是否被长按 */
    private boolean isPressed;
    /** 游标 */
    private DragView mDragView;
    /**
     * 是针对 ViewGroup 中的拖拽和重新定位 views 操作时提供了一系列非常有用的方法和状态追踪。基本上使用在自定义ViewGroup处理拖拽中！
     */
    private ViewDragHelper mDragHelper;
    /**
     * 可能需要实现一些手势监听相关的功能，如：单击、双击、长按、滑动、缩放等。这些都是很常用的手势
     */
    private GestureDetector mGestureDetector;
    private Bounds mBounds;

    private ScrollHandler mHandler;
    private OnWheelScrollListener mOnWheelListener;
    private OnDragViewScrollListener mOnDragViewScrollListener;

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
    /** 一个占位的view */
    protected View mAnchorView;

    public CaliperLayout(Context context) {
        this(context, null);
    }

    public CaliperLayout(Context context, AttributeSet attrs) {
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
        mDragHelper = ViewDragHelper.create(this, 1f, new DragHelperCallback());
        mGestureDetector = new GestureDetector(context, new OnLongClickGestureListener());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDragView = findViewById(R.id.drag_view);
        mDragView.setVisibility(INVISIBLE);
        mAnchorView = findViewById(R.id.anchor_view);
        mAnchorView.setVisibility(INVISIBLE);
        for (int i = 0, len = getChildCount(); i < len; i++) {
            View child = getChildAt(i);
            if (child instanceof DataProtocol) {
                ((DataProtocol) child).setScaleWidth(mLineDividerWidth);
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
            return false;
        }
        return mDragHelper.shouldInterceptTouchEvent(ev);
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
                        Math.abs(event.getY() - mDownFocusY) <
                                Math.abs(event.getX() - mDownFocusX)) {
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
        mDragHelper.processTouchEvent(event);
        mGestureDetector.onTouchEvent(event);
        if (mDragView.getVisibility() != VISIBLE) {
            mScroller.onTouchEvent(event);
        }
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        mHandler.removeMessages(MSG_SCROLL_FORWARD);
        mHandler.removeMessages(MSG_SCROLL_BACKWARD);
        mHandler.removeMessages(MSG_PAUSE);
        super.onDetachedFromWindow();
    }

    WheelHorizontalScroller.ScrollingListener mScrollingListener =
            new WheelHorizontalScroller.ScrollingListener() {
        @Override
        public void onStarted() {
            isManualScrollingPerformed = true;
            float value = Math.max(mMinValue,
                    Math.min(mMaxValue, mCurrentValue - mScrollOffset / mLineDividerWidth));
            notifyScrollingListenersAboutStart(value);
        }

        @Override
        public void onScroll(int distance) {
            int conditionsValue = (int) (mCurrentValue - ((mScrollOffset + distance) / mLineDividerWidth));
            if (conditionsValue > mMaxValue + 5 || conditionsValue < mMinValue - 5) {
                return;
            }
            doScroll(distance);
            float value = Math.max(mMinValue,
                    Math.min(mMaxValue, mCurrentValue - mScrollOffset / mLineDividerWidth));
            notifyScrollingListeners(value);
        }

        @Override
        public void onFinished() {
            if (thatExceed()) {
                return;
            }
            if (isManualScrollingPerformed) {
                float value = Math.max(mMinValue,
                        Math.min(mMaxValue, mCurrentValue - mScrollOffset / mLineDividerWidth));
                notifyScrollingListenersAboutEnd(value);
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
            mCurrentValue -= offsetCount;
            mScrollOffset -= offsetCount * mLineDividerWidth;
        }
        notifyChildrenStartScroll();
    }

    private boolean thatExceed() {
        // 越界后需要回滚的大小值
        int outRange = 0;
        if (mCurrentValue < mMinValue) {
            outRange = Math.round((mCurrentValue - mMinValue) * mLineDividerWidth);
        } else if (mCurrentValue > mMaxValue) {
            outRange = Math.round((mCurrentValue - mMaxValue) * mLineDividerWidth);
        }
        if (0 != outRange) {
            mScrollOffset = 0;
            mScroller.scroll(-outRange, 0);
            return true;
        }
        return false;
    }

    public float getScrollOffset() {
        return mScrollOffset;
    }

    /**
     * 通知子view开始滚动
     */
    protected void notifyChildrenStartScroll() {
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
    protected void notifyChildrenEndScroll() {
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
    public void setWheelScrollingListener(OnWheelScrollListener listener) {
        mOnWheelListener = listener;
    }

    /**
     * Removes wheel changing listener
     */
    public void removeScrollingListener() {
        mOnWheelListener = null;
    }

    public void setDragViewScrollingListener(OnDragViewScrollListener listener) {
        mOnDragViewScrollListener = listener;
    }

    protected void notifyScrollingListeners(float value) {
        if (null != mOnWheelListener) {
            mOnWheelListener.onChanged(this, value);
        }
    }

    private void notifyScrollingListenersAboutStart(float value) {
        if (null != mOnWheelListener) {
            mOnWheelListener.onScrollingStarted(this, value);
        }
    }

    private void notifyScrollingListenersAboutEnd(float value) {
        if (null != mOnWheelListener) {
            mOnWheelListener.onScrollingFinished(this, value);
        }
    }

    private void notifyDragViewScrollingListenersPressed(Bounds bounds) {
        if (mOnDragViewScrollListener != null && isDragViewVisible()) {
            mOnDragViewScrollListener.onPressed(bounds == null ? null : bounds.destMark);
        }
    }

    private void notifyDragViewScrollingListeners(float value, Bounds bounds) {
        if (mOnDragViewScrollListener != null && isDragViewVisible()) {
            mOnDragViewScrollListener.onChanged(value, bounds == null ? null : bounds.destMark);
        }
    }

    private void notifyDragViewScrollingListenersAboutEnd(float value, Bounds bounds) {
        if (mOnDragViewScrollListener != null && isDragViewVisible()) {
            mOnDragViewScrollListener.onDragFinished(value, bounds == null ? null : bounds.destMark);
        }
    }

    /**
     * 设置刻度数据
     * @param max
     */
    public void setScaleList(int max) {
        if (max < 0) {
            return;
        }
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
        mCurrentValue = scale;
        mScrollOffset = -offset * mLineDividerWidth;
        for (int i = 0, len = getChildCount(); i < len; i++) {
            View view = getChildAt(i);
            if (view instanceof DataProtocol) {
                ((DataProtocol) view).selectScale(mCurrentValue, mScrollOffset);
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

    public View getAnchorView() {
        return mAnchorView;
    }

    private static class ScrollHandler extends Handler {

        private WeakReference<CaliperLayout> mScrollViewRef;
        private long mInterval;
        private float mMinScaleOffset;

        public ScrollHandler(CaliperLayout view) {
            mScrollViewRef = new WeakReference<>(view);
            mInterval = 100 / view.mMinScaleCount;
            mMinScaleOffset = view.mLineDividerWidth / view.mMinScaleCount;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CaliperLayout view = mScrollViewRef.get();
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
                    view.notifyDragViewScrollingListeners(view.calculateDragViewLeftBound(), view.mBounds);
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
                    view.notifyDragViewScrollingListeners(view.calculateDragViewLeftBound(), view.mBounds);
                    view.notifyChildrenStartScroll();
                    sendEmptyMessageDelayed(MSG_SCROLL_BACKWARD, mInterval);
                    break;
                case MSG_PAUSE:
                    view.isAutoScrollingPerformed = false;
                    removeMessages(MSG_PAUSE);
                    removeMessages(MSG_SCROLL_FORWARD);
                    removeMessages(MSG_SCROLL_BACKWARD);
                    view.notifyDragViewScrollingListenersAboutEnd(view.calculateDragViewLeftBound(), view.mBounds);
                    view.notifyChildrenEndScroll();
                    break;
                default:
                    break;
            }
        }
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        int[] lo = new int[2];
        float startValue;
        float endValue;

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mDragView;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            Log.d(TAG, "clampViewPositionHorizontal, isPressed: " + isPressed);
            if (!isPressed) {
                return 0;
            }
            int leftBound = getPaddingLeft();
            int rightBound = (int) (getWidth() - getPaddingLeft() - getPaddingRight() -
                    mDragView.getWidth() - mDragView.getAdjustOffset());
            child.getLocationOnScreen(lo);
            Log.d(TAG, "Drag view left: " + lo[0]);

            final float halfWidth = (getWidth() - getPaddingLeft() - getPaddingRight()) / 2f;
            startValue = mCurrentValue - (halfWidth - child.getTranslationX()) / mLineDividerWidth;
            if (mBounds != null && mBounds.originMark != null) {
                endValue = startValue + mBounds.originMark.getDuration();
            } else {
                endValue = 0;
            }
//            // 边缘位置自动滚动
//            if (isPressed && !isAutoScrollingPerformed && dx > 0 && lo[0] > rightBound) {
//                startForwardScroll();
//            }
//            if (isPressed && !isAutoScrollingPerformed && dx < 0 && lo[0] < getPaddingLeft() + getCorrectionOffset()) {
//                startBackwardScroll();
//            }
            notifyDragViewScrollingListeners(calculateDragViewLeftBound(), mBounds);

            float leftMost = -startValue * mLineDividerWidth + mScrollOffset;
            float rightMost = (mMaxValue - endValue) * mLineDividerWidth + mScrollOffset;
            return (int) Math.min(Math.max(left, leftMost), rightMost);
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return 0;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            Log.d(TAG, "onViewReleased, isPressed" + isPressed);
            if (!isPressed) {
                return;
            }

            final float halfWidth = (getWidth() -
                    getPaddingLeft() -
                    getPaddingRight()) / 2f;
            int[] lo = new int[2];
            mDragView.getLocationOnScreen(lo);
            updateBounds(lo[0],
                    lo[0] + mDragView.getAdjustOffset() + mDragView.getWidth());

            float leftBound = calculateDragViewLeftBound();
            deliverDragViewAnchorPosition();
            notifyDragViewScrollingListenersAboutEnd(leftBound, mBounds);
            hideDragView();
            Log.d(TAG, "hideDragView");
            stopScroll();
            isPressed = false;
            mBounds = null;
        }
    }

    private class OnLongClickGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            mBounds = processLongPress(e.getX(), e.getY());
            Log.d(TAG, "club_onLongPress");
            if (mBounds == null) {
                return;
            }
            isPressed = true;
            showDragViewInRightPosition(mBounds);
            notifyDragViewScrollingListenersPressed(mBounds);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            processSingleTap(e.getX(), e.getY());
            Log.d(TAG, "onSingleTapUp");
            return super.onSingleTapUp(e);
        }

    }

    /**
     * 计算drag view在时间轴上显示的位置，处理长按事件
     * @param touchX
     * @param touchY
     * @return
     */
    protected abstract Bounds processLongPress(float touchX, float touchY);

    /** 计算触摸区域包含的笔迹区域，处理点击事件 */
    protected abstract Bounds processSingleTap(float touchX, float touchY);

    /**
     * drag view释放时，通知停靠位置
     */
    protected abstract void deliverDragViewAnchorPosition();

    protected int getCorrectionOffset() {
        return 0;
    }

    protected void showDragViewInRightPosition(Bounds bounds) {
        if (bounds == null) {
            return;
        }
        Log.d(TAG, "club_Bounds: " + bounds);
        mDragView.updateBounds(bounds.left, bounds.top, bounds.right, bounds.bottom);
        mDragView.setVisibility(VISIBLE);
    }

    /**
     * drag view覆盖整个时间轴面板，使得ViewDragHelper在调用tryCaptureView的时候返回true
     * 否则无法拖动drag view.
     */
    protected void hideDragView() {
        mDragView.updateBounds(0, 0, getWidth(), getHeight());
        mDragView.setTranslationX(0);
        mDragView.setTranslationY(0);
        mDragView.setVisibility(INVISIBLE);
    }

    private boolean isDragViewVisible() {
        return mDragView.getVisibility() == VISIBLE;
    }

    /**
     * 计算左边移动距离
     * @return
     */
    private float calculateDragViewLeftBound() {
        final float halfWidth = (getWidth() -
                getPaddingLeft() -
                getPaddingRight()) / 2f;
        int[] lo = new int[2];
        mDragView.getLocationOnScreen(lo);
        float leftBound = (lo[0] - halfWidth) /
                mLineDividerWidth + mCurrentValue;
        leftBound = Math.max(mMinValue, Math.min(leftBound, mMaxValue));
        if (mBounds != null && mBounds.destMark != null) {
            Mark mark = mBounds.destMark;
            float duration = mark.getDuration();
            if (leftBound + duration > mMaxValue) {
                leftBound = mMaxValue - duration;
                mark.setEnd(mMaxValue);
                mark.setStart(leftBound);
                mark.setEndPositionInClip(mMaxValue);
                mark.setStartPositionInClip(leftBound);
            } else {
                mark.setStart(leftBound);
                mark.setEnd(leftBound + duration);
                mark.setEndPositionInClip(leftBound + duration);
                mark.setStartPositionInClip(leftBound);
            }
            Log.d(TAG, "calculateDragViewLeftBound, mark duration: " + duration);
        }
        return leftBound;
    }

    private void updateBounds(float left, float right) {
        if (mBounds == null) {
            return;
        }
        mBounds.left = left;
        mBounds.right = right;
    }

    public interface OnWheelScrollListener {
        /**
         * Callback method to be invoked when current item changed
         *
         * @param wheel    the wheel view whose state has changed
         */
        void onChanged(CaliperLayout wheel, float value);

        /**
         * Callback method to be invoked when scrolling started.
         *
         * @param wheel the wheel view whose state has changed.
         */
        void onScrollingStarted(CaliperLayout wheel, float value);

        /**
         * Callback method to be invoked when scrolling ended.
         *
         * @param wheel the wheel view whose state has changed.
         */
        void onScrollingFinished(CaliperLayout wheel, float value);
    }

    public interface OnDragViewScrollListener {

        void onPressed(Mark mark);

        void onChanged(float value, Mark mark);

        void onDragFinished(float value, Mark mark);
    }
}
