package com.test.xcamera.personal;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;


import butterknife.BindView;
import butterknife.OnClick;

/**
 * 用户协议
 */
public class PersonAgreeActivity extends MOBaseActivity {
    public static final int mType_User = 0;/*用户协议*/
    public static final int mType_Privacy = 1;/*隐私协议*/

    @BindView(R.id.tv_middle_title)
    TextView mTvMiddleTitle;
    private int mType;
    private WebView mWebView;


    public static void startPersonAgreeActivity(Context context, int mType) {
        Intent intent = new Intent(context, PersonAgreeActivity.class);
        intent.putExtra("param", mType);
        context.startActivity(intent);
    }

    @OnClick({R.id.left_iv_title})
    public void onViewClicked(View view) {


        switch (view.getId()) {

            case R.id.left_iv_title:
                setResult(100);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(100);
        super.onBackPressed();
    }

    @Override
    public int initView() {
        return R.layout.activity_person_agree;
    }

    @Override
    public void initData() {
        mType = getIntent().getIntExtra("param", 0);
        mWebView = findViewById(R.id.mWebView);
        mWebView.setBackgroundColor(0);
        if (mType == mType_User) {
            //加载手机本地的html页面
            mWebView.loadUrl("file:////android_asset/用户协议.html");
        } else {
            mWebView.loadUrl("file:////android_asset/隐私政策.html");
        }
    }
}
