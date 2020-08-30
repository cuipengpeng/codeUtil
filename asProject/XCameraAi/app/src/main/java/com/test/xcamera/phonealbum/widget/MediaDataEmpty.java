package com.test.xcamera.phonealbum.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.test.xcamera.R;

public class MediaDataEmpty extends LinearLayout {
    public MediaDataEmpty(Context context) {
        this(context, null);
    }

    public MediaDataEmpty(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    private void init(Context context) {
        View root = inflate(context, R.layout.view_media_data_empty, this);
    }
}
