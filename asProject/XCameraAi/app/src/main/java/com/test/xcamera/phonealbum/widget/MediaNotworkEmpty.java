package com.test.xcamera.phonealbum.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.test.xcamera.R;

/**
 * 没有网络
 */
public class MediaNotworkEmpty extends LinearLayout {
    private OnClickListener mOnClickListener;

    public void setOnRestClickListener(OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    public MediaNotworkEmpty(Context context) {
        this(context, null);
    }

    public MediaNotworkEmpty(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    private void init(Context context) {
        View root = inflate(context, R.layout.view_media_network_empty, this);
        TextView reset=root.findViewById(R.id.reset);
        reset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnClickListener!=null){
                    mOnClickListener.onClick(v);
                }
            }
        });
    }

}
