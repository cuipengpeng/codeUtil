package com.jf.jlfund.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by 55 on 2017/12/23.
 */

public class CompatScrollView extends ScrollView {
    public CompatScrollView(Context context) {
        super(context);
    }

    public CompatScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CompatScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onSimpleScrollChangeListener != null) {
            onSimpleScrollChangeListener.onScrollChange(t);
        }

    }

    private OnSimpleScrollChangeListener onSimpleScrollChangeListener;

    public void setOnSimpleScrollChangeListener(OnSimpleScrollChangeListener onSimpleScrollChangeListener) {
        this.onSimpleScrollChangeListener = onSimpleScrollChangeListener;
    }

    public interface OnSimpleScrollChangeListener {
        void onScrollChange(int y);
    }
}
