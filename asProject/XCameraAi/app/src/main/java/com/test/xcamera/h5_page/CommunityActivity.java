package com.test.xcamera.h5_page;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import com.test.xcamera.login.LoginActivty;
import com.test.xcamera.utils.SPUtils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by 周 on 2020/4/21.
 * 社区页面
 */

public class CommunityActivity extends H5BasePageActivity {

    private boolean isCanBack;
    private boolean loadError = false;
    public static LogingCallback logingCallback;

    @Override
    public void initData() {
        super.initData();
        logingCallback = new LogingCallback();
        h5Url = getIntent().getStringExtra(H5_URL);
        synCookie();
        setH5ChromeClient();
        if (!TextUtils.isEmpty(h5Url)) {
            webView.loadUrl(h5Url);
        }
    }

    private void synCookie() {
        try {
            URL url = new URL(h5Url);
            String host = url.getHost();
            synCookies(this, host);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置Cookie
     *
     * @param context
     * @param url
     */
    public void synCookies(Context context, String url) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        String token = (String) SPUtils.get(context, "token", "");
        cookieManager.setCookie(url, "access_token=" + token + ";domain=" + url);//cookies格式自定义
        CookieSyncManager.getInstance().sync();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        CookieSyncManager.getInstance().sync();
        if (webView != null) {
            webView.destroy();
            webView = null;
        }
        CommunityActivity.logingCallback = null;
    }

    @Override
    public void onBackPressed() {
        if (isCanBack)
            super.onBackPressed();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean h5OverrideUrlLoading(WebView view, WebResourceRequest request) {
        if (request.getUrl().toString().startsWith("morange://goback")) {
            finish();
            return true;
        } else if (request.getUrl().toString().startsWith("morange://login")) {
            startActivity(new Intent(getBaseContext(), LoginActivty.class));
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 4000) {
            if (null == uploadMessage && null == uploadMessageAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (uploadMessage != null) {
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != 4000 || uploadMessageAboveL == null)
            return;
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;
    }

    @Override
    protected void openImageChooserActivity() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), 4000);
    }

    @Override
    protected void webViewLoading(WebView view, int newProgress) {
        if (newProgress < 100) {
            isCanBack = true;
            mChrysanthemumView.setVisibility(View.VISIBLE);
            mChrysanthemumView.startAnimation();
        } else if (newProgress == 100) {
            if (!loadError)
                isCanBack = false;
            mChrysanthemumView.setVisibility(View.GONE);
            mChrysanthemumView.stopAnimation();
        }
    }

    public class LogingCallback {
        public void loginCallback(int i, String token) {
            int version = Build.VERSION.SDK_INT;
            String jsFuncation = "javascript:loginCallback(" + i + ",'" + token + "')";
            // 因为该方法在 Android 4.4 版本才可使用，所以使用时需进行版本判断
            if (version < 18) {
                webView.loadUrl(jsFuncation);
            } else {
                webView.evaluateJavascript(jsFuncation, null);
            }
        }
    }
}

