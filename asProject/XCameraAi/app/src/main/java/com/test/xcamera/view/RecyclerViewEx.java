package com.test.xcamera.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by smz on 2020/1/14.
 * <p>
 * 带拦截事件的RecyclerView
 */

public class RecyclerViewEx extends RecyclerView {
    private boolean intercept = false;

    public RecyclerViewEx(@NonNull Context context) {
        super(context);
    }

    public RecyclerViewEx(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setIntercept(boolean intercept) {
        this.intercept = intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (intercept) return true;
        return super.onTouchEvent(e);
    }
}
