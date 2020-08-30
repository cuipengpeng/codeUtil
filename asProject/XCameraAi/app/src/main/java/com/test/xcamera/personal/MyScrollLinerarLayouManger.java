package com.test.xcamera.personal;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;

/**
 * Created by DELL on 2019/7/7.
 */

public class MyScrollLinerarLayouManger extends GridLayoutManager {
    private boolean mCanVerticalScroll = true;

    public MyScrollLinerarLayouManger(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MyScrollLinerarLayouManger(Context context, int spanCount) {
        super(context, spanCount);
    }

    public MyScrollLinerarLayouManger(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public boolean canScrollVertically() {
        if (!mCanVerticalScroll){
            return false;
        }else {
            return super.canScrollVertically();
        }
    }

    public void setmCanVerticalScroll(boolean b) {
        mCanVerticalScroll = b;

    }
}
