package com.test.xcamera.phonealbum.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.test.xcamera.R;
import com.test.xcamera.widget.ChrysanthemumView;

public class MediaDataLoading extends LinearLayout {
    private ChrysanthemumView mChrysanthemumView;
    public MediaDataLoading(Context context) {
        this(context, null);
    }

    public MediaDataLoading(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    private void init(Context context) {
        View root = inflate(context, R.layout.view_media_data_loading, this);
        mChrysanthemumView=root.findViewById(R.id.mChrysanthemumView);
    }
    public void start(){
        if(mChrysanthemumView!=null){
            mChrysanthemumView.startAnimation();
        }
    }
    public void stop(){
        if(mChrysanthemumView!=null){
            mChrysanthemumView.stopAnimation();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        stop();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        start();
        super.onAttachedToWindow();
    }

}
