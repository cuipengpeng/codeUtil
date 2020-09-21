package com.hospital.checkup.view.fragment;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hospital.checkup.R;
import com.hospital.checkup.base.BaseApplication;
import com.hospital.checkup.js.JsRequstInterface;
import com.hospital.checkup.listener.IWebJsLisenter;
import com.hospital.checkup.utils.ConstantsUtil;
import com.hospital.checkup.utils.LogUtils;
import com.hospital.checkup.utils.NetworkUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WebViewFragment extends Fragment  implements IWebJsLisenter, DownloadListener {

    @BindView(R.id.v_base_statusBar)
    View vBaseStatusBar;
    @BindView(R.id.tv_base_title)
    TextView tvBaseTitle;
    @BindView(R.id.iv_base_back)
    ImageView ivBaseBack;
    @BindView(R.id.rl_base_back)
    RelativeLayout rlBaseBack;
    @BindView(R.id.tv_base_back)
    TextView tvBaseBack;
    @BindView(R.id.iv_base_rightMenu)
    ImageView ivBaseRightMenu;
    @BindView(R.id.tv_base_rightMenu)
    TextView tvBaseRightMenu;
    @BindView(R.id.rl_base_titleBar)
    RelativeLayout rlBaseTitleBar;
    @BindView(R.id.ll_title)
    LinearLayout llTitle;
    @BindView(R.id.wv_webViewActivity_loadWebUrl)
    WebView webView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.rl_webViewActivity_error)
    RelativeLayout errorRelativeLayout;


    private static final int QUIT_INTERVAL = 2000;
    private String mUrl = "";
    Map<String, String> cacheHeaderMap = new HashMap<String, String>();
    private int currentProgress;
    private boolean webViewGoBack = false;//是否直接返回上一页 true:可以回退网页html false ：直接返回上一页
    public static SimpleDateFormat sdf = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
    public static String WEBVIEW_CACHE_STRING = "max-age=" + ConstantsUtil.WEBVIEW_CACHE_TIME;
    /**
     * 网编请求时加载框
     */
    private boolean isAnimStart = false;
//    private String loadUrl = "https://www.baidu.com/";
    private JsRequstInterface jsRequstInterface;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mFilePathCallback;
    boolean isShowTItle = true;
    private boolean isOnRefresh;
    private long lastBackPressed;


    public static WebViewFragment newInstance(String url) {
        WebViewFragment webViewFragment = new WebViewFragment();
        webViewFragment.mUrl = url;
        return webViewFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View subScreenView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_webview, container, false);
        ButterKnife.bind(this, subScreenView);
        return subScreenView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        llTitle.setVisibility(View.GONE);
        initWebViewSettings();//初始化webview设置
        setWebClientCallBack();//设置web监听回调
        //  初始化Html调用Android(Java)的方法接口
        jsRequstInterface = new JsRequstInterface(getActivity(), webView, this);//新建JS调用Android(Java)的方法接口类
        webView.addJavascriptInterface(jsRequstInterface, ConstantsUtil.JS_INTERFACE_OBJECT);//添加JS调用Android(Java)的方法接口
        LogUtils.printLog("loadUrl：" + mUrl);
        webView.loadUrl(mUrl, cacheHeaderMap);
    }

    /**
     * 初始化webview设置
     */
    private void initWebViewSettings() {
        //关闭硬件加速的canvas,避免webview加载网页的时候 可能会出现黑屏现象
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webView.setBackgroundColor(Color.TRANSPARENT);// 先设置背景色为transparent
        webView.setSaveEnabled(false);
        webView.setDownloadListener(this);

        WebSettings webSettings = webView.getSettings();
        // 设置WebView对JavaScript的支持
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDatabaseEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//>=5.0  处理部分网页图片不显示问题
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        //自适应屏幕
        webSettings.setUseWideViewPort(true);
        // don't show the zoom controls
        webSettings.setDisplayZoomControls(false);
        //设置内核标识
        String ua = webSettings.getUserAgentString();
        webSettings.setUserAgentString(ua);

        //设置WebView缓存模式
        if (NetworkUtil.isNetworkAvailable(getActivity())) {
            //网络可用
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        }else {
            //网络不可用
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        // 最重要的方法，一定要设置，这就是出不来的主要原因
        webSettings.setDomStorageEnabled(true);        // 开启 DOM storage API 功能
        webSettings.setAppCacheMaxSize(20*1024*1024);
        webSettings.setAppCacheEnabled(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        //开启 database storage API 功能
        webSettings.setDatabaseEnabled(true);
        webSettings.setLoadWithOverviewMode(true);

        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setBlockNetworkImage(false);//阻止图片网络数据 false:解除数据阻止
        // 启用地理定位
        webSettings.setGeolocationEnabled(true);
        String dir = BaseApplication.applicationContext.getDir("database", Context.MODE_PRIVATE).getPath();
        // 设置定位的数据库路径
        webSettings.setGeolocationDatabasePath(dir);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00")); // 设置时区为GMT+8:00
        long currentDate = new Date().getTime();
        currentDate = currentDate + ConstantsUtil.WEBVIEW_CACHE_TIME * 1000;
        String cacheTime = sdf.format(currentDate);
        cacheHeaderMap.put("Expires", cacheTime);
        cacheHeaderMap.put("Pragma", WEBVIEW_CACHE_STRING);
        cacheHeaderMap.put("Cache-Control", WEBVIEW_CACHE_STRING);
    }

    /**
     * 设置web监听回调
     */
    private void setWebClientCallBack() {
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {//返回标题
                super.onReceivedTitle(view, title);
            }

            // 配置权限（同样在WebChromeClient中实现）
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                currentProgress = progressBar.getProgress();
                if (newProgress >= 100 && !isAnimStart) {
                    // 防止调用多次动画
                    isAnimStart = true;
                    progressBar.setProgress(newProgress);
                    // 开启属性动画让进度条平滑消失
                    startDismissAnimation(progressBar.getProgress());
                } else {
                    // 开启属性动画让进度条平滑递增
                    startProgressAnimation(newProgress);
                }
            }

            // android 5.0
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;
//                getPic();
                return true;
            }

            // The undocumented magic method override
            // Eclipse will swear at you if you try to put @Override here
            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
//                getPic();
            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                openFileChooser(uploadMsg);
            }

            // For Android 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooser(uploadMsg);
            }
        });

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                LogUtils.printLog("url= "+url);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setAlpha(1.0f);
                isOnRefresh = false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:hideTitle(true)");
                Log.e("onPageFinished_URL", url + "");
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                webView.setVisibility(View.GONE);
                errorRelativeLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("shouldOverrideUrl", url);
                urlLoadOperate(webView, url);
                return true;
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return super.shouldInterceptRequest(view, request);
            }
        });
    }

    /**
     * progressBar递增动画
     */
    private void startProgressAnimation(int newProgress) {
        ObjectAnimator animator = ObjectAnimator.ofInt(progressBar, "progress", currentProgress, newProgress);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    /**
     * progressBar消失动画
     */
    private void startDismissAnimation(final int progress) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(progressBar, "alpha", 1.0f, 0.0f);
        anim.setDuration(1500);  // 动画时长
        anim.setInterpolator(new DecelerateInterpolator());     // 减速
        // 关键, 添加动画进度监听器
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float fraction = valueAnimator.getAnimatedFraction();      // 0.0f ~ 1.0f
                int offset = 100 - progress;
                progressBar.setProgress((int) (progress + offset * fraction));
            }
        });

        anim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束
                progressBar.setProgress(0);
                progressBar.setVisibility(View.GONE);
                isAnimStart = false;
            }
        });
        anim.start();
    }

    public void onBack() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            long backPressed = System.currentTimeMillis();
            if (backPressed - lastBackPressed > QUIT_INTERVAL) {
                lastBackPressed = backPressed;
                Toast.makeText(BaseApplication.applicationContext, "再按一次退出", Toast.LENGTH_SHORT).show();
            } else {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(!hidden){
            webView.loadUrl(mUrl, cacheHeaderMap);
        }
        super.onHiddenChanged(hidden);
    }

    /**
     * url拦截处理
     *
     * @param view
     * @param url
     */
    private void urlLoadOperate(WebView view, String url) {
        if (url.startsWith("tel:")) {//支持拨打电话

        }  else {   // 在同一个webview中加载html页面
            view.loadUrl(url);
        }
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

    }

    @Override
    public void setShare(String shareStr) {

    }

    @Override
    public void openContacts(String requestName) {

    }

    @Override
    public void requstAllContacts() {

    }

    @Override
    public void onRefreshWebUrl() {

    }

    @Override
    public void setH5Title(String title) {

    }

    @Override
    public void openCamera() {

    }

    @Override
    public void setCallBack(int statusClose) {

    }

    @Override
    public void onH5Back(int statusClose) {

    }
}