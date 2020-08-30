package com.test.xcamera.h5_page;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import com.test.xcamera.R;
import com.test.xcamera.utils.CameraToastUtil;

/**
 * Created by zhouxuecheng on
 * Create Time 2020/7/20
 * e-mail zhouxuecheng1991@163.com
 * <p>
 * 落地页面
 */

public class SqueezePageActivity extends H5BasePageActivity {

    @Override
    public void initData() {
        super.initData();
        h5Url = getIntent().getStringExtra(H5_URL);
        setH5ChromeClient();
        if (!TextUtils.isEmpty(h5Url)) {
            webView.loadUrl(h5Url);
        }
    }


    @Override
    protected void webViewLoading(WebView view, int newProgress) {
        //h5加载进度
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean h5OverrideUrlLoading(WebView view, WebResourceRequest request) {
        //做拦截事件
        String uri = request.getUrl().toString();
        if (!TextUtils.isEmpty(uri) && uri.startsWith("openapp.jdmobile://")) {
            //京东
            return openActivity(uri);
        } else if (!TextUtils.isEmpty(uri) && uri.startsWith("tmall://")) {
            //天猫
            return openActivity(uri);
        } else if (!TextUtils.isEmpty(uri) && uri.startsWith("taobao://")) {
            //淘宝
            return openActivity(uri);
        } else if (!TextUtils.isEmpty(uri) && uri.startsWith("tbopen://")) {
            //淘宝
            return openActivity(uri);
        } else if (!TextUtils.isEmpty(uri) && uri.startsWith("tmast://")) {
            //应用宝
            return openActivity(uri);
        }

        return false;
    }

    private boolean openActivity(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return true;
        } catch (Exception e) {
            CameraToastUtil.show(getResources().getString(R.string.no_app), getApplication());
            e.printStackTrace();
            return false;
        }
    }
}
