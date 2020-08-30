package com.test.xcamera.view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by smz on 2020/1/14.
 *
 * 带事件拦截的ConstraintLayout
 */
public class ConstraintLayoutEx extends ConstraintLayout {
    private boolean intercept = false;
    public ConstraintLayoutEx(Context context) {
        super(context);
    }

    public ConstraintLayoutEx(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setIntercept(boolean intercept) {
        this.intercept = intercept;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return this.intercept;
    }
}
