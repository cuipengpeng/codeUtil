package com.jf.jlfund.view.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.jf.jlfund.R;
import com.jf.jlfund.base.BaseLocalDataActivity;
import com.jf.jlfund.inter.JsRequstInterface;
import com.jf.jlfund.utils.ConstantsUtil;
import com.jf.jlfund.utils.LogUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import butterknife.BindView;

/**
 * H5展示页面
 *
 * @author 崔朋朋
 */
public class WebViewActivity extends BaseLocalDataActivity {
    /**
     * Html加载地址
     */
    public static final String KEY_OF_HTML_URL = "htmlKey";//
    /**
     * Html加载标题
     */
    public static final String KEY_OF_HTML_TITLE = "htmlTitleKey";//
    /**
     * 是否点击直接返回上一页 true：可以回到html5上一页  false:直接关掉网页
     */
    public static final String KEY_OF_WEB_VIEW_GO_BACK = "webViewGoBackKey";
    /**
     * 是否点击直接返回上一页 true：可以回到html5上一页  false:直接关掉网页
     */
    public static final String KEY_OF_WEB_VIEW_SHOWDELETE_ICON = "webViewShowDeleteIcon";
    public static final String KEY_OF_WEB_VIEW_SHOW_TITLE = "webViewShowTitle";
    private static final int EXTRA_REQUST_PICK_CONTACT = 0x001;//调用通讯录
    private static final int EXTRA_REQUST_UPLOADCONTACTS = 0x002;//上传通讯录
    private static final int EXTRA_REQUST_SELECTCONTACTS = 0x002;//选择通讯录
    protected String TAG = WebViewActivity.class.getName();
    @BindView(R.id.wv_webViewActivity_loadWebUrl)
    WebView webView;
    Map<String, String> cacheHeaderMap = new HashMap<String, String>();
    boolean isOnRefresh = false;
    boolean isSetTitle = true;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private boolean isAnimStart = false;
    private int currentProgress;
    private boolean webViewGoBack = false;//是否直接返回上一页 true:可以回退网页html false ：直接返回上一页
    public static SimpleDateFormat sdf = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
    public static String WEBVIEW_CACHE_STRING = "max-age="; //+ ConstantsUtil.WEBVIEW_CACHE_TIME;
    /**
     * 网编请求时加载框
     */
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mFilePathCallback;
    boolean isShowTItle = true;
    private String loadUrl;
    private JsRequstInterface jsRequstInterface;
    private String mTitle = "";

    public Handler pdfHtmlHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Intent intent = new Intent(WebViewActivity.this, PDFViewerActivity.class);
                    intent.putExtra(PDFViewerActivity.KEY_OF_PDF_URL, ((String) msg.obj));
                    intent.putExtra(PDFViewerActivity.KEY_OF_TITLE, mTitle);
                    startActivity(intent);
                    break;
                case 2:
                    break;

            }
        }
    };

    @Override
    protected String getPageTitle() {
        return null;
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_webview;
    }

    @Override
    protected void initPageData() {
        loadUrl = getIntent().getStringExtra(WebViewActivity.KEY_OF_HTML_URL);
//        if (!MainActivity.isNetworkAvailable(this)) {//网络异常
//            Toast.makeText(this, ConstantsUtil.H5_PAGE_FAIL_TO_CONNECT_SERVER, Toast.LENGTH_SHORT).show();
//        }
        initWebViewSettings();//初始化webview设置
        setWebClientCallBack();//设置web监听回调
        initViewAndData();//初始化View和data

        LogUtils.printLog("loadUrl：" + loadUrl);
        webView.loadUrl(loadUrl, cacheHeaderMap);
    }

    /**
     * 初始化View和data
     */
    private void initViewAndData() {
        Intent intent = getIntent();
        String htmlUrl = intent.getStringExtra(KEY_OF_HTML_URL);
        webViewGoBack = intent.getBooleanExtra(KEY_OF_WEB_VIEW_GO_BACK, false);
        isShowTItle = intent.getBooleanExtra(KEY_OF_WEB_VIEW_SHOW_TITLE, true);
//        WindowInputUtils.assistActivity(this, false);
        if (getIntent().getBooleanExtra(KEY_OF_WEB_VIEW_SHOWDELETE_ICON, false)) {//只显示删除按钮
//            Drawable drawable = getResources().getDrawable(R.mipmap.ic_delete_gray);
//            ivBack.setImageDrawable(drawable);
//            webViewGoBack = false;
        }
    }

    /**
     * 初始化webview设置
     */
    private void initWebViewSettings() {
        //关闭硬件加速的canvas,避免webview加载网页的时候 可能会出现黑屏现象
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webView.setBackgroundColor(Color.TRANSPARENT);// 先设置背景色为transparent
        webView.setSaveEnabled(false);

        /**
         * 放置WebView跨域访问漏洞【1.添加如下设置、2.清单文件中当前Activity标签下添加 exported=false 属性】
         * API(15)及以下默认为true，API16 及以上默认为false
         */
        webView.getSettings().setAllowFileAccessFromFileURLs(false);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(false);


        WebSettings webSettings = webView.getSettings();
        // 设置WebView对JavaScript的支持
        webSettings.setJavaScriptEnabled(true);
        jsRequstInterface = new JsRequstInterface(this, webView);//新建JS调用Android(Java)的方法接口类
        webView.addJavascriptInterface(jsRequstInterface, ConstantsUtil.JS.JS_INTERFACE_OBJECT);//添加JS调用Android(Java)的方法接口
        webSettings.setDatabaseEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//>=5.0  处理部分网页图片不显示问题
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        //自适应屏幕
        webSettings.setUseWideViewPort(true);
        // don'data show the zoom controls
        webSettings.setDisplayZoomControls(false);
        //设置内核标识
        String ua = webSettings.getUserAgentString();
        webSettings.setUserAgentString(ua);

        //设置WebView缓存模式
//        if (MainActivity.isNetworkAvailable(this)) {
//            //网络可用
//            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
//        }else {
//            //网络不可用
//            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        }
        // 最重要的方法，一定要设置，这就是出不来的主要原因
        webSettings.setDomStorageEnabled(true);        // 开启 DOM storage API 功能
        webSettings.setAppCacheMaxSize(20 * 1024 * 1024);
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
        String dir = getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        // 设置定位的数据库路径
        webSettings.setGeolocationDatabasePath(dir);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00")); // 设置时区为GMT+8:00
        long currentDate = new Date().getTime();
        currentDate = currentDate; //+ ConstantsUtil.WEBVIEW_CACHE_TIME * 1000;
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
                mTitle = title;
                baseTitleTextView.setText(title);
            }

//            // 配置权限（同样在WebChromeClient中实现）
//            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissionsCallback callback) {
//                super.onGeolocationPermissionsShowPrompt(origin, callback);
//            }

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
                return true;
            }

            // The undocumented magic method override
            // Eclipse will swear at you if you try to put @Override here
            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
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
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setAlpha(1.0f);
                isOnRefresh = false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.e("onPageFinished_URL", url + "");
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                showNetworkErrorView();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("shouldOverrideUrl", url);
                return true;
            }

//            @Override
//            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
//                return super.shouldInterceptRequest(view, request);
//            }
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

    /**
     * dianjifanhuianniu
     */
    public void onBack() {
        if (webViewGoBack && webView.canGoBack()) {
            String currentUrl = webView.getUrl();
            webView.goBack();
        } else {
            finish();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isOnRefresh) {
            webView.reload();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (!isShowTItle) {
                return false;
            } else {
                onBack();
                return true;
            }
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 返回图片给H5
     *
     * @param path
     */
    public void returnPicToH5(String path) {
        if (!TextUtils.isEmpty(path)) {// 采集图片，并返回图片地址
            if (mFilePathCallback != null) {// 5.0兼容
                // 5.0的回调
                Uri[] results = null;
                results = new Uri[]{Uri.fromFile(new File(path))};// Uri.fromFile(new
                mFilePathCallback.onReceiveValue(results);
                mFilePathCallback = null;
                return;
            } else {// 其他版本
                if (null == mUploadMessage) {
                    mUploadMessage.onReceiveValue(null);
                    return;
                }
                Uri result = Uri.fromFile(new File(path));// Uri.fromFile(new
                // File(path));
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
                return;
            }
        } else {// 非采集图片
            if (mFilePathCallback != null)
                mFilePathCallback.onReceiveValue(null);
            if (null != mUploadMessage)
                mUploadMessage.onReceiveValue(null);
            mFilePathCallback = null;
            mUploadMessage = null;
            return;
        }
    }

//    public void updateLogin(Object params) {
//        if (!webView.getUrl().contains("token=") && AppContext.isLogin && AppContext.user != null) {
//            String url = webView.getUrl() + (webView.getUrl().contains("?") ? "&" : "?") + "mobile=" + AppContext.user.getMobile() + "&uid=" + AppContext.user.getUid() + "&token=" + AppContext.user.getToken();
//            webView.loadUrl(url);
//            webView.loadUrl(url);
//        }
//
//    }

    @Override
    protected void onDestroy() {
        webView.clearCache(true);
        webView.clearHistory();
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        view.removeAllViews();
        super.onDestroy();
    }

    private String requestSelectContactName = null;

    /**
     * 跳转到通讯录
     */
    private void startPickContact() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent,
                EXTRA_REQUST_PICK_CONTACT);
    }

    public void setCallBack(int statusClose) {
        if (statusClose == 1) {
            webViewGoBack = false;
        } else {
            webViewGoBack = true;
        }
    }

    public void onH5Back(int statusClose) {
        if (statusClose == 1) {
            finish();
        } else {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                finish();
            }
        }
    }

    private void reSetUpFileCallback() {
        if (mFilePathCallback != null)
            mFilePathCallback.onReceiveValue(null);
        if (null != mUploadMessage)
            mUploadMessage.onReceiveValue(null);
        mFilePathCallback = null;
        mUploadMessage = null;
    }

}
