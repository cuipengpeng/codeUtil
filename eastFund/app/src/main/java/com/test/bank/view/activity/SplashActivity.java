package com.test.bank.view.activity;

import android.os.Handler;

import com.test.bank.R;
import com.test.bank.base.BaseActivity;
import com.test.bank.utils.SPUtil;

public class SplashActivity extends BaseActivity {

    @Override
    protected void init() {
        SPUtil.getInstance().putLong(SPUtil.KEY_SMS_CODE_TIMESTAMP_FORGET_RESET_LOGIN_PWD, -1); //重新进入app之后倒计时重置
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MainActivity.open(SplashActivity.this, 1);
                finish();
            }
        }, 1000);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void doBusiness() {

    }
}
