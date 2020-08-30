package com.test.xcamera.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.test.xcamera.album.SwipeRefreshView;

/**
 * Created by zhouxuecheng on
 * Create Time 2019/12/30
 * e-mail zhouxuecheng1991@163.com
 */

public class ConstomSwipeRefreshView extends SwipeRefreshView {
    private int page;

    public ConstomSwipeRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setInterceptFlag(int page) {
        this.page = page;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (page == 0)
            return super.onInterceptTouchEvent(ev);
        else
            return false;
    }
}
