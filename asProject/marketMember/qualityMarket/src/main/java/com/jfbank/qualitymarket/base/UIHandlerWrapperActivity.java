package com.jfbank.qualitymarket.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2017/5/11<br>
*/
public abstract class UIHandlerWrapperActivity extends BaseUIHandlerActivity {
    public static final String TAG = UIHandlerWrapperActivity.class.getName();


    private int layoutResId = -1;
    private  Bundle savedInstanceState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        savedInstanceState = savedInstanceState;
    }

    @Override
    public void setContentView(int layoutResID) {
        this.layoutResId = layoutResID;
        super.onCreate(savedInstanceState);
    }


    @Override
    protected int getLayoutResID() {
        return layoutResId;
    }

}
