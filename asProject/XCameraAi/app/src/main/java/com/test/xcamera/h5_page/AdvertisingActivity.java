package com.test.xcamera.h5_page;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import com.test.xcamera.home.HomeActivity;

/**
 * Created by 周 on 2020/4/21.
 * 开屏广告页面
 */

public class AdvertisingActivity extends H5BasePageActivity implements View.OnClickListener {

    @Override
    public void initData() {
        super.initData();
        h5Url = getIntent().getStringExtra(H5_URL);
        setH5ChromeClient();

        if (!TextUtils.isEmpty(h5Url)) {
            webView.loadUrl(h5Url);
        }
        closeAdvertisingImage.setOnClickListener(this);

    }

    @Override
    protected void webViewLoading(WebView view, int newProgress) {
        if (newProgress < 100) {
            mChrysanthemumView.setVisibility(View.VISIBLE);
            mChrysanthemumView.startAnimation();
        } else if (newProgress == 100) {
            mChrysanthemumView.setVisibility(View.GONE);
            mChrysanthemumView.stopAnimation();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean h5OverrideUrlLoading(WebView view, WebResourceRequest request) {
        if (request.getUrl().toString().contains("morange://goback")) {
            finish();
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}
