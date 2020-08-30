package com.test.xcamera.phonealbum.widget.subtitle;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.widget.FrameLayout;


import com.test.xcamera.R;
import com.test.xcamera.phonealbum.widget.subtitle.bean.Mark;
import com.test.xcamera.utils.PUtil;

import java.util.List;

/**
 * 时间轴上有三条时间线，每条时间线上画上了一些笔迹，
 * 笔迹可以拖动。时间线的数目可以配置，最多有三条时间线。
 */

public class TimelineLayout extends CaliperLayout {
    private static final String TAG = TimelineLayout.class.getSimpleName();
    protected HighlightMarkView mTopMarkView;

    private Bounds mBounds;
    private SparseArray<HighlightMarkView> mMarkViewArray = new SparseArray<>();
    private OnAddMarkListener mOnAddMarkListener;
    private OnDragMarkListener mOnDragMarkListener;
    private HighlightMarkView.OnMarkEditCut mOnMarkEditCut;

    public void setOnMarkEditCut(HighlightMarkView.OnMarkEditCut mOnMarkEditCut) {
        this.mOnMarkEditCut = mOnMarkEditCut;
    }
    public TimelineLayout(Context context) {
        super(context);
    }

    public TimelineLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTopMarkView = findViewById(R.id.mHighlightMarkView);
        mTopMarkView.setOnMarkEditCut(new HighlightMarkView.OnMarkEditCut() {
            @Override
            public void onMarkEditCut(boolean isDown, int position, Mark mark) {
                if(mOnMarkEditCut!=null){
                    mOnMarkEditCut.onMarkEditCut(isDown,position,mark);
                }
            }

        });
        if (mTopMarkView != null) {
            mMarkViewArray.put(Mark.TRACK_TOP, mTopMarkView);
        }


    }
    public void updateTopTimeline(List<? extends Mark> properties) {
        if (mTopMarkView == null) {
            return;
        }
        mTopMarkView.addAll(properties);
    }
    @Override
    protected void onDetachedFromWindow() {
        mMarkViewArray.clear();
        super.onDetachedFromWindow();
    }



    public void setTopVisibility(int visibility) {

        if (mTopMarkView != null) {
            mTopMarkView.setVisibility(visibility);
        }
    }

    public HighlightMarkView getTopMarkView() {
        return mTopMarkView;
    }


    @Override
    public void selectScale(int scale, float offset) {
        super.selectScale(scale, offset);
        notifyChildrenStartScroll();
    }

    @Override
    protected void notifyChildrenStartScroll() {
        super.notifyChildrenStartScroll();

        final float halfWidth = (getWidth() - getPaddingLeft() - getPaddingRight()) / 2f;
        float horizontalPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                44f,
                getResources().getDisplayMetrics());
        float offset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                3f,
                getResources().getDisplayMetrics());
        float left = - mCurrentValue * mLineDividerWidth - horizontalPadding + halfWidth + mScrollOffset;

    }

    @Override
    protected void notifyChildrenEndScroll() {
        super.notifyChildrenEndScroll();
    }

    /**
     * 计算触摸点位置对应的笔迹区域
     * @param touchX x轴位置
     * @param touchY y轴位置
     * @return
     */
    @Override
    protected Bounds processLongPress(float touchX, float touchY) {
        mBounds = getMarkBounds(touchX, touchY);
        if (mBounds == null) {
            return null;
        }
        switch (mBounds.trackId) {
            case Mark.TRACK_TOP:
                Mark m = mBounds.destMark;
                if (m != null) {
                    m.setDraw(false);
                    mTopMarkView.invalidate();
                }
                if (mOnDragMarkListener != null) {
                    mOnDragMarkListener.onTopDragMarkPressed(mBounds.destMark);
                }
                break;

        }
        return mBounds;
    }

    @Override
    protected Bounds processSingleTap(float touchX, float touchY) {
        mBounds = getMarkBounds(touchX, touchY);
        if (mBounds == null) {
            return null;
        }
        if (mAnchorView != null) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mAnchorView.getLayoutParams();
            params.width = PUtil.dip2px(getContext(),1);
            params.leftMargin = (int) touchX;
            params.topMargin = (int) mBounds.top;
            mAnchorView.setLayoutParams(params);
        }

        if (mOnDragMarkListener != null) {
            mOnDragMarkListener.onTopDragMarkClicked(mBounds.overlapped);
        }
        return mBounds;
    }



    private Bounds getMarkBounds(float touchX, float touchY) {
        Bounds topBounds = null;
        Bounds middleBounds = null;
        Bounds bottomBounds = null;
        if (mTopMarkView != null) {
            topBounds = mTopMarkView.getMarkBounds(touchX, touchY);
        }

        if (topBounds != null) {
            return topBounds;
        } else if (middleBounds != null) {
            return middleBounds;
        } else if (bottomBounds != null) {
            return bottomBounds;
        } else {
            return null;
        }
    }

    /**
     * 更新笔迹的最新位置
     */
    @Override
    protected void deliverDragViewAnchorPosition() {
        if (mBounds == null || mBounds.destMark == null) {
            return;
        }
        HighlightMarkView markView = mMarkViewArray.get(mBounds.trackId);
        if (markView == null) {
            return;
        }
        // 重新将笔迹绘制出来
        Mark mark = mBounds.destMark;
        mark.setDraw(true);
        // 将绘制的过程放到回调中处理，因为有些笔迹绘制需要判断是否被覆盖。
        switch (mBounds.trackId) {
            case Mark.TRACK_TOP:
                if (mOnDragMarkListener != null) {
                    mOnDragMarkListener.onTopDragMarkReleased(markView, mBounds.originMark, mBounds.destMark);
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void showDragViewInRightPosition(Bounds bounds) {
        super.showDragViewInRightPosition(bounds);
    }

    @Override
    protected void hideDragView() {
        super.hideDragView();
    }


    public void setOnAddMarkListener(OnAddMarkListener listener) {
        mOnAddMarkListener = listener;
    }

    public void setOnDragMarkListener(OnDragMarkListener listener) {
        mOnDragMarkListener = listener;
    }

    protected Mark createMark(int trackId) {
        return null;
    }

    public interface OnAddMarkListener {
        void onTopAddMark(Mark mark);

    }

    public interface OnDragMarkListener {
        void onTopDragMarkPressed(Mark mark);
        void onTopDragMarkReleased(HighlightMarkView markView, Mark origin, Mark dest);
        void onTopDragMarkClicked(List<Mark> overlapped);

    }
}
