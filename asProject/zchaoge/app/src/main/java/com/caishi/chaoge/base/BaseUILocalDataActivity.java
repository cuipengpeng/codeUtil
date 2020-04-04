package com.caishi.chaoge.base;

import com.caishi.chaoge.utils.LogUtil;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseUILocalDataActivity extends BaseUIActivity {

    @Override
    protected void initLocalDataView() {
        super.initLocalDataView();
        showContentView();
    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.getClass().getSimpleName()); //手动统计页面("SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this); //统计时长
        LogUtil.d(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getSimpleName()); //手动统计页面("SplashScreen"为页面名称，可自定义)，必须保证 onPageEnd 在 onPause 之前调用，因为SDK会在 onPause 中保存onPageEnd统计到的页面数据。
        MobclickAgent.onPause(this);
        LogUtil.d(TAG, "onPause()");
    }
}
