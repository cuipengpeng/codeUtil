package com.jfbank.qualitymarket.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.ActivityManager;
import com.jfbank.qualitymarket.widget.LoadingAlertDialog;
import com.jph.takephoto.app.TakePhotoFragmentActivity;
import com.sptalkingdata.sdk.SPAgent;
import com.umeng.analytics.MobclickAgent;

/**
 * 文 件 名：BasePhotoActivity
 * 功    能：基类Activity
 * 作    者：赵海
 * 时    间：2016/7/6
 **/
public abstract class BaseActivity extends TakePhotoFragmentActivity {
    public BaseActivity mContext;
    private boolean isAgent = true;//是否启动统计功能。
    public LoadingAlertDialog mDialog;
    /**
     * 获取是否统计Activity界面
     *
     * @return
     */
    public boolean isAgent() {
        return isAgent;
    }

    /**
     * 设置Activity是否统计页面访问
     *
     * @param agent true：統計  false:不統計
     */
    public void setAgent(boolean agent) {
        isAgent = agent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().addActivity(this);
        mContext = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getPageName());
        if (isAgent) {
            SPAgent.onPageStart(mContext, getPageName());
        }
        MobclickAgent.onResume(this);
    }

    /**
     * 返回页面名称
     *
     * @return
     */
    protected abstract String getPageName();
    protected abstract int getLayoutResID();

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getPageName());
        MobclickAgent.onPause(this);
        if (isAgent) {
            SPAgent.onPageEnd(mContext, getPageName());
        }
    }

    @Override
    protected void onDestroy() {
        HttpRequest.cancelRequestNet(mContext);
        ActivityManager.getInstance().removeActivity(this);
        super.onDestroy();
    }
}
