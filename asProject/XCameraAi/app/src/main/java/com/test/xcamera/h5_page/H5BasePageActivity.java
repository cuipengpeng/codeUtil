package com.test.xcamera.h5_page;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.BuildConfig;
import com.test.xcamera.R;
import com.test.xcamera.widget.ChrysanthemumView;

import butterknife.BindView;

/**
 * Created by zhouxuecheng on
 * Create Time 2020/7/20
 * e-mail zhouxuecheng1991@163.com
 */

public abstract class H5BasePageActivity extends MOBaseActivity implements View.OnClickListener {
    public static final String H5_URL = "h5Url";

    @BindView(R.id.communityWebView)
    protected WebView webView;
    protected String h5Url;
    @BindView(R.id.mChrysanthemumView)
    protected ChrysanthemumView mChrysanthemumView;
    @BindView(R.id.closeAdvertisingImage)
    protected ImageView closeAdvertisingImage;
    @BindView(R.id.title)
    protected TextView title;
    @BindView(R.id.advertisingTitleRl)
    protected RelativeLayout advertisingTitleRl;

    protected ValueCallback<Uri> uploadMessage;
    protected ValueCallback<Uri[]> uploadMessageAboveL;


    @Override
    public int initView() {
        return R.layout.activity_advertising_layout;
    }

    @Override
    public void initData() {
        if (this instanceof CommunityActivity) {
            advertisingTitleRl.setVisibility(View.GONE);
        }
        initWebView();
        initWebViewSetting();
        closeAdvertisingImage.setOnClickListener(this);
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
    }

    private void initWebView() {
        //设置WebViewClient类
        webView.setWebViewClient(new WebViewClient() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                //设置加载前的函数,可以做url拦截
                h5OverrideUrlLoading(view, request);
                if (request.getUrl().toString().startsWith("http://") ||
                        request.getUrl().toString().startsWith("https://")) {
                    view.loadUrl(request.getUrl().toString());
                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //设置结束加载函数
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }
        });
    }

    private void initWebViewSetting() {
        WebSettings settings = webView.getSettings();
        settings.setTextZoom(100);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        settings.setJavaScriptEnabled(true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        settings.setSupportZoom(false);//是否可以缩放，默认true
        settings.setBuiltInZoomControls(false);
        settings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
        settings.setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
        settings.setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
        settings.setAppCacheEnabled(false);//是否使用缓存
        settings.setDomStorageEnabled(false);//DOM Storage
        if (this instanceof CommunityActivity ||
                this instanceof AdvertisingActivity) {
            settings.setAllowFileAccess(true);
            settings.setUserAgentString("User-Agent:" + "morange/" + BuildConfig.VERSION_NAME + "(android; version : " + Build.VERSION.RELEASE + ")");//设置用户代理
        }
    }

    protected void setH5ChromeClient() {
        //设置WebChromeClient类
        webView.setWebChromeClient(new WebChromeClient() {

            //获取网站标题
            @Override
            public void onReceivedTitle(WebView view, String webtitle) {
                if (title != null) {
                    title.setText(webtitle);
                }
            }

            //获取加载进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (mChrysanthemumView == null) {
                    return;
                }
                webViewLoading(view, newProgress);
            }

            //For Android API < 11 (3.0 OS)
            public void openFileChooser(ValueCallback<Uri> valueCallback) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            //For Android API >= 11 (3.0 OS)
            public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            //For Android API >= 21 (5.0 OS)
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                uploadMessageAboveL = filePathCallback;
                openImageChooserActivity();
                return true;
            }
        });
    }

    protected void openImageChooserActivity() {

    }

    protected abstract void webViewLoading(WebView view, int newProgress);

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public abstract boolean h5OverrideUrlLoading(WebView view, WebResourceRequest request);

    /**
     * 打开 h5 页面
     *
     * @param context
     * @param url     需要展示的连接
     */
    public static void openActivity(Context context, String url, Class classes) {
        Intent intent = new Intent(context, classes);
        intent.putExtra(H5_URL, url);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
