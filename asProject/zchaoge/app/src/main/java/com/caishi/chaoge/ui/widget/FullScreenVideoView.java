package com.caishi.chaoge.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

import com.caishi.chaoge.R;

/**
 */
public class FullScreenVideoView extends VideoView {
    public FullScreenVideoView(Context context) {
        super(context);
    }

    public FullScreenVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullScreenVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
//        float dimension = getResources().getDimension(R.dimen._48dp);
        setMeasuredDimension(width, height );
    }
}
