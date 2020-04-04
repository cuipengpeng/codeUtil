package com.caishi.chaoge.ui.widget;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * 设置viewpager不可滑动 默认可以滑动
 * Created by lichao on 2017/7/12.
 */

public class CustomViewPager extends ViewPager {

    private boolean isScroll = true;
    private int dLastX = 0;
    private int dLastY = 0;
    private int iLastX = 0;
    private int iLastY = 0;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Sets scroll. true 可以滑动  false 不可以滑动
     *
     * @param isScroll the is scroll
     */
    public void setScroll(boolean isScroll) {
        this.isScroll = isScroll;
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//
//        int x = (int) ev.getX();
//        int y = (int) ev.getY();
//
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                dLastX = x;
//                dLastY = y;
//                getParent().requestDisallowInterceptTouchEvent(true);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                int dx = Math.abs(x - dLastX);
//                int dy = Math.abs(y - dLastY);
//                if (dx > dy) {
//                    if (x >= dLastX) {
//                        // 上一页
//                        if (ViewCompat.canScrollHorizontally(this, -1)) {
//                            getParent().requestDisallowInterceptTouchEvent(true);
//                        } else {
//                            getParent().requestDisallowInterceptTouchEvent(false);
//                        }
//                    } else {
//                        // 下一页
//                        if (ViewCompat.canScrollHorizontally(this, 1)) {
//                            getParent().requestDisallowInterceptTouchEvent(true);
//                        } else {
//                            getParent().requestDisallowInterceptTouchEvent(false);
//                        }
//                    }
//                }
////                else {
////                    Log.i("CustomViewPager:", " else  > dy");
////                    getParent().requestDisallowInterceptTouchEvent(false);
////                }
//                break;
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//                getParent().requestDisallowInterceptTouchEvent(false);
//                break;
//        }
//
//        return isScroll && super.dispatchTouchEvent(ev);
//    }
//
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        int x = (int) ev.getX();
//        int y = (int) ev.getY();
//
//        switch (ev.getAction()) {
//
//            case MotionEvent.ACTION_DOWN:
//                iLastX = x;
//                iLastY = y;
//                break;
//            case MotionEvent.ACTION_MOVE:
//                int dx = Math.abs(x - iLastX);
//                int dy = Math.abs(y - iLastY);
//
//                if (dx > dy) {
//                    // 上一页
//                    if (x >= iLastX) {
//                        return ViewCompat.canScrollHorizontally(this, -1);
//                    } else {
//                        // 下一页
//                        return ViewCompat.canScrollHorizontally(this, 1);
//                    }
//                }
//                break;
//        }
//        return isScroll && super.onInterceptTouchEvent(ev);
//    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isScroll && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return isScroll && super.onTouchEvent(ev);

    }
    @Override
    public void setCurrentItem(int item) {
        setCurrentItem(item, false);
    }
}