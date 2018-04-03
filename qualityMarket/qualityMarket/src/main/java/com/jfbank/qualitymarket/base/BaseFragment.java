package com.jfbank.qualitymarket.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jfbank.qualitymarket.R;
import com.jph.takephoto.app.TakePhotoFragment;
import com.sptalkingdata.sdk.SPAgent;
import com.umeng.analytics.MobclickAgent;


/**
 * 文 件 名：BaseFragment
 * 功    能：BaseFragment基类
 * 作    者：赵海
 * 时    间：2016/7/11
 **/
public abstract class BaseFragment extends TakePhotoFragment {
    public BaseActivity mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = (BaseActivity) getActivity();
        mContext.setAgent(false);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getPageName()); //统计页面，"MainScreen"为页面名称，可自定义
        SPAgent.onPageStart(mContext, getPageName());
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getPageName());
        SPAgent.onPageEnd(mContext, getPageName());
    }

    public abstract String getPageName();

}
