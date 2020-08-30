package com.test.xcamera.phonealbum.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

@SuppressLint("AppCompatCustomView")
public class ButtonUpDown extends Button {
    public ButtonUpDown(Context context) {
        super(context);
    }

    public ButtonUpDown(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);

    }
}
