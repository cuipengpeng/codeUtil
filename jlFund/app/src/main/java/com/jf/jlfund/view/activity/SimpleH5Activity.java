package com.jf.jlfund.view.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jf.jlfund.R;
import com.jf.jlfund.base.BaseActivity;
import com.jf.jlfund.utils.LogUtils;
import com.jf.jlfund.weight.CommonTitleBar;

import butterknife.BindView;

public class SimpleH5Activity extends BaseActivity {

    @BindView(R.id.commonTitleBar_bannerH5)
    CommonTitleBar commonTitleBar;
    @BindView(R.id.webView_bannerH5)
    WebView webView;

    String url;
    String title;

    @Override
    protected void init() {
        if (getIntent() != null) {
            url = getIntent().getStringExtra(PARAM_URL);
            title = getIntent().getStringExtra(PARAM_TITLE);
        }
        LogUtils.e("SimpleH5: " + url + " --- " + title);
        if (!TextUtils.isEmpty(title)) {
            commonTitleBar.setPrimaryTitle(title);
        }
        initWebView();
        webView.loadUrl(url);
    }

    private void initWebView() {
        webView.getSettings().setUseWideViewPort(true); // //适配直接加载 http**.jpg 图片资源大小不适配的问题
        webView.getSettings().setLoadWithOverviewMode(true);    //适配直接加载 http**.jpg 图片资源大小不适配的问题
        /**
         * 放置WebView跨域访问漏洞【1.添加如下设置、2.清单文件中当前Activity标签下添加 exported=false 属性】
         * API(15)及以下默认为true，API16 及以上默认为false
         */
        webView.getSettings().setAllowFileAccessFromFileURLs(false);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(false);

//        webView.getSettings().setDisplayZoomControls(true);// 设置显示缩放按钮
//        webView.getSettings().setSupportZoom(true); // 支持缩放
//        webView.getSettings().setBuiltInZoomControls(true);
//        webView.getSettings().setLoadsImagesAutomatically(true);
//        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);//适应内容大小
//        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setJavaScriptEnabled(true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();      //接受所有证书
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                LogUtils.e("onReceivedError: " + error.getDescription().toString());
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (commonTitleBar != null)
                    commonTitleBar.setPrimaryTitle(title);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_simple_h5;
    }

    @Override
    protected void doBusiness() {

    }

    public static String PARAM_URL = "PARAM_URL";
    public static String PARAM_TITLE = "PARAM_TITLE";


    public static void open(Context context, String url) {
        open(context, url, "");
    }

    public static void open(Context context, String url, String htmlTitle) {
        Intent intent = new Intent(context, SimpleH5Activity.class);
        intent.putExtra(PARAM_URL, url);
        intent.putExtra(PARAM_TITLE, htmlTitle);
        context.startActivity(intent);
    }
}
