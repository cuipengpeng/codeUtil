package com.test.xcamera.utils;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

/**
 * 用于RecyclerView点击item后，item居中
 */
public class CenterLayoutManager extends LinearLayoutManager {
    public CenterLayoutManager(Context context) {
        super(context);
    }

    public CenterLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public CenterLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     *
     * @param recyclerView 目标recyclerView
     * @param state new RecyclerView.State()
     * @param position 需要滑动的item的position
     */
    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        try {
            CenterSmoothScroll centerSmoothScroll = new CenterSmoothScroll(recyclerView.getContext());
            centerSmoothScroll.setTargetPosition(position);
            startSmoothScroll(centerSmoothScroll);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class CenterSmoothScroll extends LinearSmoothScroller {

        public CenterSmoothScroll(Context context) {
            super(context);
        }
        //RecyclerView的中心点和item的中心点的相差即item需要移动的距离和方向
        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2);
        }
        //计算每个像素滑动的速度
        @Override
        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
            return 100f/displayMetrics.densityDpi;
        }
    }
}
