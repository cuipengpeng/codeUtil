package com.jfbank.qualitymarket.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huoyanzhengxin.zhengxin.print.FingerPrint;
import com.huoyanzhengxin.zhengxin.print.FingerPrintHelp;
import com.huoyanzhengxin.zhengxin.print.PermissionUtil;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.base.BaseActivity;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
import com.jfbank.qualitymarket.model.DiscoverBean;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.ImageDownLoader;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.StringUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 欢迎启动页面
 *
 * @author 崔朋朋
 */
public class WelcomeActivity extends BaseActivity {
    public static final String TAG = WelcomeActivity.class.getName();

    @InjectView(R.id.tv_welcomeActivity_jumpToMainPage)
    TextView jumpToMainPageTextView;
    @InjectView(R.id.iv_welcomeActivity_ad)
    ImageView adImageView;
    @InjectView(R.id.rl_welcomeActivity_welcomePage)
    RelativeLayout welcomePageRelativeLayout;

    private boolean canJumpToMainPage = false;
    private boolean requestSuccess = false;
    private int screenWidth = 1080;
    private int screenHeight = 1920;
    private String adClickUrl = "";
    private String appCacheStartAdFileName = "";
    private File savedStartAdDir = null;
    private  DiscoverBean.QualityDiscoveryBanner appStartAd = null;
    private SharedPreferences sharedPreferences;

    private FingerPrint fingerPrint;
    private PermissionUtil permissionUtil;

    @OnClick({R.id.iv_welcomeActivity_ad, R.id.tv_welcomeActivity_jumpToMainPage})
    public void OnViewClick(View v) {
        switch (v.getId()) {
            case R.id.iv_welcomeActivity_ad:
//                adClickUrl = "https://sctest.9fbank.com/views/detail.html?productNo=pzsc1472117209923&isActivity=0";

                if(StringUtil.notEmpty(adClickUrl)){
                    canJumpToMainPage = false;
                    Intent webIntent = new Intent(this, WebViewActivity.class);
                    webIntent.putExtra(WebViewActivity.KEY_OF_HTML_URL, adClickUrl);
                    webIntent.putExtra(WebViewActivity.KEY_OF_WEB_VIEW_GO_BACK, true);
                    webIntent.putExtra(WebViewActivity.KEY_OF_WEB_VIEW_SHOWDELETE_ICON, false);
                    webIntent.putExtra(LoginActivity.KEY_OF_COME_FROM, TAG);
                    Intent []intents=new Intent[]{new Intent(this,MainActivity.class),webIntent};
                    startActivities(intents);
                    finish();
                }
                break;

            case R.id.tv_welcomeActivity_jumpToMainPage:
                if(canJumpToMainPage){
                    canJumpToMainPage = false;
                    startMainActivity();
                }
                break;
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
        setContentView(R.layout.activity_welcome);
        ButterKnife.inject(this);

        fingerPrint = FingerPrintHelp.getFingerPrint(getApplicationContext());
        permissionUtil = new PermissionUtil(this);
        //注意此处的上下文必须是activity的；在第一个activity中调用下面的代码：虚线之间的代码是匹配的动态权限
        if (Build.VERSION.SDK_INT < 23) {
            fingerPrint.startFirstSDK();
        } else {
            permissionUtil.startFirstSDK(fingerPrint);
        }


        //获取屏幕分辨率
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowMgr = (WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowMgr.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;


        sharedPreferences = getSharedPreferences(ConstantsUtil.GLOBAL_CONFIG_FILE_NAME, MODE_PRIVATE);
        appCacheStartAdFileName = sharedPreferences.getString(ConstantsUtil.KEY_OF_APP_START_AD, "");
        adClickUrl = sharedPreferences.getString(ConstantsUtil.KEY_OF_APP_START_AD_CLICK_URL, "");
        canJumpToMainPage = true;
        if(sharedPreferences.getBoolean(ConstantsUtil.KEY_OF_APP_START_AD_OFFLINE, false)){
//            new File(appCacheStartAdFileName).delete();
            getSharedPreferences(ConstantsUtil.GLOBAL_CONFIG_FILE_NAME, MODE_PRIVATE).edit()
                    .putString(ConstantsUtil.KEY_OF_APP_START_AD_OFFLINE_TIME, "1971-01-01 00:00:00").commit();
        }

        savedStartAdDir = getCacheDir();
        getAppStartAD();

        //显示欢迎页， 隐藏广告页
        welcomePageRelativeLayout.setVisibility(View.VISIBLE);
        adImageView.setVisibility(View.INVISIBLE);
        jumpToMainPageTextView.setVisibility(View.INVISIBLE);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                boolean inPeriod = false;
                long currentTime = System.currentTimeMillis();
                try {
                    //当前时间在上下线时间中
                    if(currentTime > simpleDateFormat.parse(sharedPreferences.getString(ConstantsUtil.KEY_OF_APP_START_AD_ONLINE_TIME, "1971-01-01 00:00:00")).getTime()
                            && currentTime< simpleDateFormat.parse(sharedPreferences.getString(ConstantsUtil.KEY_OF_APP_START_AD_OFFLINE_TIME, "1971-01-01 00:00:00")).getTime()){
                        inPeriod = true;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //图片下载成功 或者      缓存图片存在并且当前时间在上线时间和下线时间之间
                if(requestSuccess ||  (new File(appCacheStartAdFileName).exists() && inPeriod) ){
                    if(!requestSuccess){
                        loadCacheStartAd();
                    }
                    //显示广告页，隐藏欢迎页
//                    welcomePageRelativeLayout.setVisibility(View.GONE);
                    adImageView.setVisibility(View.VISIBLE);
                    jumpToMainPageTextView.setVisibility(View.VISIBLE);
                    
                    new CountDownTimer(3080, 1000) {

                        public void onTick(long millisUntilFinished) {
                            LogUtil.printLog("millisUntilFinished="+millisUntilFinished);
                            jumpToMainPageTextView.setText("跳过 " + millisUntilFinished / 1000);
                        }

                        public void onFinish() {
                            if(canJumpToMainPage){
                                canJumpToMainPage = false;
                                startMainActivity();
                            }
                        }
                    }.start();

                }else {
                    startMainActivity();
                }
            }
        }, 2000);//网络请求和图片加载用时2s
    }


    /**
     * 动态申请权限的回调方法
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        permissionUtil.onRequestPermissionsResult(fingerPrint, requestCode, permissions, grantResults);
    }

    /**
     * 启动主页面
     */
    private void startMainActivity() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected String getPageName() {
        return getString(R.string.str_pagename_welcome);
    }



    /**
     * 获取启动页广告
     */
    public void getAppStartAD() {
        Map<String,String> params = new HashMap<>();
        params.put("resolvingPower", screenWidth+"*"+screenHeight);

        HttpRequest.post(mContext,HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.APP_START_AD, params,
                new AsyncResponseCallBack() {

                    @Override
                    public void onResult(String arg2) {
                        String jsonStr = new String(arg2);

                        if(StringUtil.isNull(jsonStr)){
                            return ;
                        }

                        LogUtil.printLog("获取启动页广告：" + jsonStr);
                        final JSONObject jsonObject = JSON.parseObject(jsonStr);

                        if (ConstantsUtil.RESPONSE_SUCCEED == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {

                            appStartAd = JSON.parseObject(jsonObject.getJSONArray(ConstantsUtil.RESPONSE_DATA_JSON_ARRAY_FIELD_NAME).get(0).toString(), DiscoverBean.QualityDiscoveryBanner.class);
                            adClickUrl = appStartAd.getUrl();

                            final String downLoadedFileName = "AppStartAd_"+System.currentTimeMillis() + ".png";
                            getSharedPreferences(ConstantsUtil.GLOBAL_CONFIG_FILE_NAME, MODE_PRIVATE).edit()
                                    .putString(ConstantsUtil.KEY_OF_APP_START_AD_ONLINE_TIME, appStartAd.getOnlineTime())
                                    .putString(ConstantsUtil.KEY_OF_APP_START_AD_OFFLINE_TIME, appStartAd.getOfflineTime())
                                    .putString(ConstantsUtil.KEY_OF_APP_START_AD, new File(savedStartAdDir , downLoadedFileName).getAbsolutePath())
                                    .putBoolean(ConstantsUtil.KEY_OF_APP_START_AD_OFFLINE, false).commit();
                            adImageView.setVisibility(View.VISIBLE);
                            Picasso.with(WelcomeActivity.this).load(appStartAd.getImage()).resize(screenWidth, screenHeight).transform(new Transformation() {
                                @Override
                                public Bitmap transform(Bitmap bitmap) {
                                    ImageDownLoader.saveBitmapToDisk(WelcomeActivity.this, bitmap, savedStartAdDir,downLoadedFileName);
                                    return bitmap;
                                }

                                @Override
                                public String key() {
                                    return "123";
                                }
                            }).into(adImageView, new com.squareup.picasso.Callback(){

                                        @Override
                                        public void onSuccess() {
                                            //图片加载成功表明本次网络请求成功
                                            requestSuccess = true;
                                            getSharedPreferences(ConstantsUtil.GLOBAL_CONFIG_FILE_NAME, MODE_PRIVATE).edit()
                                                    .putString(ConstantsUtil.KEY_OF_APP_START_AD_CLICK_URL, appStartAd.getUrl()).commit();
                                        }

                                        @Override
                                        public void onError() {
                                            adClickUrl = sharedPreferences.getString(ConstantsUtil.KEY_OF_APP_START_AD_CLICK_URL, "");
                                        }
                                    }
                             );
                        }else if(ConstantsUtil.RESPONSE_NO_DATA == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)){
                            //有网请求回来无数据时，只显示一次缓存图片
                            getSharedPreferences(ConstantsUtil.GLOBAL_CONFIG_FILE_NAME, MODE_PRIVATE).edit()
                                    .putBoolean(ConstantsUtil.KEY_OF_APP_START_AD_OFFLINE, true).commit();
                        }
                    }

                    @Override
                    public void onFailed(String path, String msg) {
                    }
                });
    }

    /**
     * 加载缓存的广告页图片
     */
    private void loadCacheStartAd() {
        Bitmap bitmap = BitmapFactory.decodeFile(new File(appCacheStartAdFileName).getAbsolutePath());
        if(bitmap != null){
            adImageView.setImageBitmap(bitmap);
        }
    }

}
