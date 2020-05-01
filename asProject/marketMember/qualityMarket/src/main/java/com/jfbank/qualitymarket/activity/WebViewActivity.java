package com.jfbank.qualitymarket.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
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

import com.alibaba.fastjson.JSON;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.base.BaseActivity;
import com.jfbank.qualitymarket.constants.EventBusConstants;
import com.jfbank.qualitymarket.js.JsRequstInterface;
import com.jfbank.qualitymarket.js.JsResponseInterface;
import com.jfbank.qualitymarket.listener.DialogListener;
import com.jfbank.qualitymarket.listener.IWebJsLisenter;
import com.jfbank.qualitymarket.model.ShareContentBean;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.DialogUtils;
import com.jfbank.qualitymarket.util.FaceDetectUtil;
import com.jfbank.qualitymarket.util.ImageFactory;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.PermissionUtils;
import com.jfbank.qualitymarket.util.PhotoUtils;
import com.jfbank.qualitymarket.util.StringUtil;
import com.jfbank.qualitymarket.util.TDUtils;
import com.jfbank.qualitymarket.util.ToastUtil;
import com.jfbank.qualitymarket.util.WindowInputUtils;
import com.jph.takephoto.model.TResult;
import com.sensetime.stlivenesslibrary.ui.LivenessActivity;
import com.sptalkingdata.sdk.SPAgent;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * H5展示页面
 *
 * @author 崔朋朋
 */
public class WebViewActivity extends BaseActivity implements IWebJsLisenter, DownloadListener {
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
    @InjectView(R.id.rl_title)
    RelativeLayout rlTitle;
    @InjectView(R.id.tv_title)
    TextView tvTitle;
    @InjectView(R.id.iv_back)
    ImageView ivBack;
    @InjectView(R.id.tv_right_menu)
    TextView tvRightMenu;
    @InjectView(R.id.wv_webViewActivity_loadWebUrl)
    WebView webView;
    Map<String, String> cacheHeaderMap = new HashMap<String, String>();
    @InjectView(R.id.rl_webViewActivity_error)
    RelativeLayout errorRelativeLayout;
    boolean isOnRefresh = false;
    boolean isSetTitle = true;
    @InjectView(R.id.ll_title)
    LinearLayout llTitle;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;
    private boolean isAnimStart = false;
    private int currentProgress;
    private boolean webViewGoBack = false;//是否直接返回上一页 true:可以回退网页html false ：直接返回上一页
    public static SimpleDateFormat sdf = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
    public static String WEBVIEW_CACHE_STRING = "max-age=" + ConstantsUtil.WEBVIEW_CACHE_TIME;
    /**
     * 网编请求时加载框
     */
    private String loadUrl = HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.H5_PAGE_SUB_URL + HttpRequest.H5_PAGE_GOODS_DETAIL;
    private ShareContentBean shareContentBean = null;
    private JsRequstInterface jsRequstInterface;
    JsResponseInterface jsResponseInterface;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mFilePathCallback;
    boolean isShowTItle = true;

    /**
     * 点击事件
     *
     * @param v
     */
    @OnClick({R.id.iv_back, R.id.tv_right_menu, R.id.rl_webViewActivity_error})
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back://返回
                onBack();
                break;
            case R.id.tv_right_menu://分享
                //页面加载完成js才会传递分享的参数
                if (shareContentBean != null) {
                    CommonUtils.oneKeyShare(this, shareContentBean.getTitle(), shareContentBean.getContent(), "", shareContentBean.getBase64DecodeUrl(), false);
                    TDUtils.onEvent(mContext, "100032", "点击右上角分享", TDUtils.getInstance().putUserid().buildParams());
                }
                break;
            case R.id.rl_webViewActivity_error://加载错误
                webView.loadUrl(loadUrl);
                setWebViewVisible();
                break;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        setAgent(false);
        EventBus.getDefault().register(this);
        ButterKnife.inject(this);
        if (!MainActivity.isNetworkAvailable(this)) {//网络异常
            Toast.makeText(this, ConstantsUtil.H5_PAGE_FAIL_TO_CONNECT_SERVER, Toast.LENGTH_SHORT).show();
        }
        initWebViewSettings();//初始化webview设置
        setWebClientCallBack();//设置web监听回调
        initViewAndData();//初始化View和data
        //  初始化Html调用Android(Java)的方法接口
        jsRequstInterface = new JsRequstInterface(this, webView, this);//新建JS调用Android(Java)的方法接口类
        webView.addJavascriptInterface(jsRequstInterface, ConstantsUtil.JS_INTERFACE_OBJECT);//添加JS调用Android(Java)的方法接口

        //  初始化Android(Java)调用Html的方法接口
        jsResponseInterface = new JsResponseInterface(this, webView);

        LogUtil.printLog("loadUrl：" + loadUrl);
        webView.loadUrl(loadUrl, cacheHeaderMap);
    }

    /**
     * 初始化View和data
     */
    private void initViewAndData() {

        setWebViewVisible();//设置webview可见
        Intent intent = getIntent();
        String htmlUrl = intent.getStringExtra(KEY_OF_HTML_URL);
        webViewGoBack = intent.getBooleanExtra(KEY_OF_WEB_VIEW_GO_BACK, false);
        isShowTItle = intent.getBooleanExtra(KEY_OF_WEB_VIEW_SHOW_TITLE, true);
        WindowInputUtils.assistActivity(this, false);
        if (!isShowTItle) {//不显示标题
            llTitle.setVisibility(View.GONE);
            CommonUtils.setStatusColor(this);
        } else {
            CommonUtils.setTitle(this, rlTitle);
        }
        tvRightMenu.setVisibility(View.GONE);
        if (StringUtil.notEmpty(htmlUrl)) {
            // 其他html页面的展示
            loadUrl = htmlUrl;
            Log.e("urlLoad1", loadUrl);
            if (!loadUrl.contains("fromChannel=")) {
                loadUrl = loadUrl + (loadUrl.contains("?") ? "&" : "?") + "fromChannel=pzsc";
            }
            if (!loadUrl.contains("appVerion")) {
                loadUrl = loadUrl + "&appVerion=" + CommonUtils.getVersionCode(mContext);
            }
            if (!loadUrl.contains("token=") && AppContext.isLogin && AppContext.user != null) {
                loadUrl = loadUrl + (loadUrl.contains("?") ? "&" : "?") + "mobile=" + AppContext.user.getMobile() + "&uid=" + AppContext.user.getUid() + "&token=" + AppContext.user.getToken();
            }
            Log.e("urlLoad", loadUrl);
        }
        if (getIntent().getBooleanExtra(KEY_OF_WEB_VIEW_SHOWDELETE_ICON, false)) {//只显示删除按钮
            Drawable drawable = getResources().getDrawable(R.mipmap.ic_delete_gray);
            ivBack.setImageDrawable(drawable);
            webViewGoBack = false;
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
        if (MainActivity.isNetworkAvailable(this)) {
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
        String dir = getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
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
                if (isSetTitle) {
                    setTitleName(title);
                }
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
                getPic();
                return true;
            }

            // The undocumented magic method override
            // Eclipse will swear at you if you try to put @Override here
            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                getPic();
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
                if( url.contains("")){
                    rlTitle.setVisibility(View.GONE);
                }else{
                    rlTitle.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setAlpha(1.0f);
                isOnRefresh = false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:hideTitle(true)");
                if (isSetTitle) {
                    setTitleName(view.getTitle());
                }
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

    /**
     * url拦截处理
     *
     * @param view
     * @param url
     */
    private void urlLoadOperate(WebView view, String url) {
        if (url.startsWith("tel:")) {//支持拨打电话
            if (TextUtils.equals(tvTitle.getText().toString(), "联系客服")) {
                CommonUtils.callContactService(WebViewActivity.this, url);
            } else {
                CommonUtils.callPhone(WebViewActivity.this, "确定拨打" + url.substring(4) + "电话么？", url);
            }
        } else if (url.startsWith("jfbank://")) {
            jsRequstInterface.jsInteract(url);
        } else {   // 在同一个webview中加载html页面
            view.loadUrl(url);
        }
    }

    /**
     * 拍照选择
     */
    private void getPic() {
        if (webView.getUrl().contains("uploadCard")) {
            openCamera();
        } else {
            DialogUtils.showSheetDialog(mContext, "选择图片", new String[]{"拍照", "相册"}, new DialogListener.DialogItemLisenter() {
                @Override
                public void onDialogClick(int position) {
                    reSetUpFileCallback();
                    if (position == 0) {
                        openCamera();
                    } else {
                        getTakePhoto().onPickFromGallery();
                    }
                }
            });
        }
    }

    /**
     * dianjifanhuianniu
     */
    public void onBack() {
        if (webViewGoBack && webView.canGoBack()) {
            String currentUrl = webView.getUrl();
            if (currentUrl.contains("registernotprizeapp.html") || currentUrl.contains("registerandprizeapp.html") || currentUrl.contains("activity-11/doubleapp.html")) {
                finish();
            } else {
                webView.goBack();
            }
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
    protected String getPageName() {
        return getString(R.string.str_pagename_h5);
    }


    /**
     * 设置webview可见
     */
    private void setWebViewVisible() {
        webView.setVisibility(View.VISIBLE);
        errorRelativeLayout.setVisibility(View.GONE);
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

    @Override
    public void setShare(String shareContent) {
        shareContentBean = JSON.parseObject(shareContent, ShareContentBean.class);
        tvRightMenu.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == EXTRA_REQUST_PICK_CONTACT && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                jsResponseInterface.selectedContact(data, requestSelectContactName);
            } else {
                Toast.makeText(this, "请重新选择联系人", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == JsRequstInterface.REQUEST_CODE_TAKE_IDCARD && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                try {
                    jsResponseInterface.jsHandlerCamera(CommonUtils.Image2Base64String(ImageFactory.compressAndGenImage(data.getStringExtra("path"), PhotoUtils.getFile(this).getPath(), 800)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (requestCode == JsRequstInterface.EXTRA_QUESTCODE_FACEFUNC) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    jsResponseInterface.jsHandlerFunc(FaceDetectUtil.storageFolder);
                } else {
                    ToastUtil.show("人脸识别出错，建议重试");
                }
            } else if (resultCode == RESULT_CANCELED) {
                ToastUtil.show("取消人脸识别");
            } else if (resultCode == RESULT_FIRST_USER) {
                ToastUtil.show("人脸识别出错，建议重试");
            } else if (resultCode == LivenessActivity.RESULT_CAMERA_ERROR_NOPRERMISSION_OR_USED) {
                ToastUtil.show("请开启相机权限，再进行人脸识别");
            } else if (resultCode == LivenessActivity.RESULT_CREATE_HANDLE_ERROR) {
                ToastUtil.show("加载库文件出现错误,稍后重试");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    @Subscriber(tag = EventBusConstants.EVENTT_UPDATE_TOKAN_RESET, mode = ThreadMode.MAIN)
    public void updateLogin(Object params) {
        if (!webView.getUrl().contains("token=") && AppContext.isLogin && AppContext.user != null) {
            String url = webView.getUrl() + (webView.getUrl().contains("?") ? "&" : "?") + "mobile=" + AppContext.user.getMobile() + "&uid=" + AppContext.user.getUid() + "&token=" + AppContext.user.getToken();
            webView.loadUrl(url);
            webView.loadUrl(url);
        }

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        webView.clearCache(true);
        webView.clearHistory();
        SPAgent.onPageEnd(mContext, tvTitle.getText().toString().trim());
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        view.removeAllViews();
        super.onDestroy();
    }

    private String requestSelectContactName = null;

    @Override
    public void openContacts(final String requestName) {
        requestSelectContactName = requestName;
        PermissionUtils.requestPermission(mContext, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.GET_ACCOUNTS}, new PermissionUtils.OnPermissionListener() {
            @Override
            public void noNeedPermission() {
                startPickContact();
            }

            @Override
            public void denyPermisson(String[] permissions) {
                PermissionUtils.startPermission(mContext, permissions, EXTRA_REQUST_SELECTCONTACTS);
            }
        });

    }

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

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.handlePermissionCallback(requestCode, permissions, grantResults, new PermissionUtils.OnPermissionResultListener() {
            @Override
            public void onResult(boolean iGranted) {
                if (iGranted) {
                    if (requestCode == EXTRA_REQUST_UPLOADCONTACTS) {//上传通讯录
                        jsResponseInterface.responseAllContact();
                    }
                    if (requestCode == EXTRA_REQUST_SELECTCONTACTS) {
                        startPickContact();
                    }
                } else {
                    if (requestCode == EXTRA_REQUST_UPLOADCONTACTS) {//上传通讯录
                    }
                    if (requestCode == EXTRA_REQUST_SELECTCONTACTS) {
                        PermissionUtils.showDialogPermison(mContext, "需要开启读取通讯录权限，确定要开启么？");
                    }
                }
            }
        });
    }

    @Override
    public void requstAllContacts() {
        PermissionUtils.requestPermission(mContext, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.GET_ACCOUNTS}, new PermissionUtils.OnPermissionListener() {
            @Override
            public void noNeedPermission() {
                jsResponseInterface.responseAllContact();
            }

            @Override
            public void denyPermisson(String[] permissions) {
                PermissionUtils.startPermission(mContext, permissions, EXTRA_REQUST_UPLOADCONTACTS);
            }
        });
    }

    @Override
    public void onRefreshWebUrl() {
        isOnRefresh = true;
    }

    @Override
    public void setH5Title(String title) {
        isSetTitle = false;
        setTitleName(title);
    }

    /**
     * \
     * 修改标题
     *
     * @param title
     */
    private void setTitleName(String title) {
        String oldTitle = tvTitle.getText().toString();
        if (!TextUtils.isEmpty(oldTitle)) {
            if (!TextUtils.equals(title, oldTitle)) {
                SPAgent.onPageEnd(mContext, oldTitle);
                SPAgent.onPageStart(mContext, title);
            } else {
                SPAgent.onPageStart(mContext, title);
            }
        } else {
            SPAgent.onPageStart(mContext, title);
        }
        tvTitle.setText(title);
    }

    @Override
    public void openCamera() {
        getTakePhoto().onPickFromCapture(PhotoUtils.getOutputUri(mContext));
    }

    @Override
    public void setCallBack(int statusClose) {
        if (statusClose == 1) {
            webViewGoBack = false;
        } else {
            webViewGoBack = true;
        }
    }

    @Override
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

    @Override
    public void takeCancel() {
        super.takeCancel();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                reSetUpFileCallback();
                ToastUtil.show("取消选择图片");
            }
        });
    }

    @Override
    public void takeFail(TResult result, final String msg) {
        super.takeFail(result, msg);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.show(msg);
                reSetUpFileCallback();
            }
        });

    }

    @Override
    public void takeSuccess(final TResult result) {
        super.takeSuccess(result);
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String picUrl = result.getImage().getOriginalPath();
                returnPicToH5(picUrl);
            }
        });

    }

    private void reSetUpFileCallback() {
        if (mFilePathCallback != null)
            mFilePathCallback.onReceiveValue(null);
        if (null != mUploadMessage)
            mUploadMessage.onReceiveValue(null);
        mFilePathCallback = null;
        mUploadMessage = null;
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        if (url != null && url.startsWith("http://"))
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
}
