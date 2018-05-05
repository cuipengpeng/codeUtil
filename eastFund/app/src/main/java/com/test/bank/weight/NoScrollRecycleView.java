package com.test.bank.weight;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 */
public class NoScrollRecycleView extends RecyclerView {
    private static final String TAG = "NoScrollGridView";

    public NoScrollRecycleView(Context context) {
        this(context,null);
    }

    public NoScrollRecycleView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public NoScrollRecycleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSpec;

        if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
            heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        } else {
            heightSpec = heightMeasureSpec;
        }
        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}
