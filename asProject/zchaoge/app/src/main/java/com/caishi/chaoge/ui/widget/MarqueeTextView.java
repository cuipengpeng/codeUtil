package com.caishi.chaoge.ui.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Darren on 2015/2/23.
 * 设置所有的TextView都有跑马灯效果
 */
public class MarqueeTextView extends AppCompatTextView {

    public MarqueeTextView(Context context) {
        super(context);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //TextView默认设置是第一个获取到的光标，
    //如果想让所有的TextView都有跑马灯效果,则让所有的TextView都获取到光标就行了
    //这里return true 就是让所有的TextView都获取到光标
    @Override
    public boolean isFocused() {
        return true;
    }
}