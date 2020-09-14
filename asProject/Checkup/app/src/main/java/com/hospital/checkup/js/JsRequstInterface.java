package com.hospital.checkup.js;

import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;
import android.webkit.WebView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.hospital.checkup.base.BaseApplication;
import com.hospital.checkup.listener.IWebJsLisenter;
import com.hospital.checkup.utils.LogUtils;
import com.hospital.checkup.view.activity.LoginActivity;
import com.hospital.checkup.view.activity.MainAcyivity;
import com.hospital.checkup.view.activity.TesterDetailActivity;

import java.util.logging.Handler;

public class JsRequstInterface {
    IWebJsLisenter iWebJsLisenter;
    private WebView webView;
    Activity activity;

    public static final int TYPE_OPEN_BLUE_TOOTH = 101; //打开蓝牙
    public static final int TYPE_OPEN_TESTER_DETAIL = 102;//查看试着详情
    public static final int TYPE_OPEN_MAIN_RECORD = 103; //打开主页面记录
    public static final int TYPE_LOGOUT = 104;//退出
    public static final int TYPE_ADD_DOCTOR = 105;//添加医生
    public static final int TYPE_ADD_TESTER = 106;//添加试者
    public static final int TYPE_GET_USER_INFO = 107;//获取用户信息
    public static final int TYPE_FINISH_PAGE = 108;//关闭页面
    public static final String KEY_OF_JSON_DATA = "jsonDataInJsKey";

    public JsRequstInterface(Activity activity, WebView webView, IWebJsLisenter iWebJsLisenter) {
        this.activity = activity;
        this.webView = webView;
        this.iWebJsLisenter = iWebJsLisenter;
    }

    @android.webkit.JavascriptInterface
    public String openBluetooth(int type, String json) {
        Toast.makeText(BaseApplication.applicationContext, type+"-----"+json, Toast.LENGTH_SHORT).show();
        LogUtils.printLog("json data = "+json);
        String str = "";
        switch (type){
            case TYPE_OPEN_BLUE_TOOTH:
                activity.startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                break;
            case TYPE_OPEN_TESTER_DETAIL:
                TesterDetailActivity.open(activity, json);
                break;
            case TYPE_OPEN_MAIN_RECORD:
                MainAcyivity.open(activity, MainAcyivity.BOTTOM_FRAGMENT01);
                break;
            case TYPE_LOGOUT:
                LoginActivity.open(activity);
                break;
            case TYPE_ADD_DOCTOR:
                setResult(TYPE_ADD_DOCTOR, json);
                break;
            case TYPE_ADD_TESTER:
                setResult(TYPE_ADD_TESTER, json);
                break;
            case TYPE_GET_USER_INFO:
                str = JSON.toJSONString(BaseApplication.userInfo);
                break;
            case TYPE_FINISH_PAGE:
                activity.finish();
                break;
        }

        return str;
    }

    private void setResult(int type, String json) {
        Intent intent = new Intent();
        intent.putExtra(KEY_OF_JSON_DATA, json);
        activity.setResult(type, intent);
        activity.finish();
    }

//    @android.webkit.JavascriptInterface
//    public void openBluetooth() {
//        Toast.makeText(BaseApplication.applicationContext, "openBluetooth", Toast.LENGTH_SHORT).show();
//        activity.startActivity(new Intent(activity, TesterDetailActivity.class));
//
////        activity.startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
//    }

    @android.webkit.JavascriptInterface
    public void openTesterDetail(String testerInfo) {
//        Toast.makeText(BaseApplication.applicationContext, testerInfo, Toast.LENGTH_SHORT).show();
        Toast.makeText(BaseApplication.applicationContext, "openTesterDetail", Toast.LENGTH_SHORT).show();
//        activity.startActivity(new Intent(activity, TesterDetailActivity.class));
    }

    @android.webkit.JavascriptInterface
    public void openMainMeasureTab() {

        Toast.makeText(BaseApplication.applicationContext, "openMainMeasureTab", Toast.LENGTH_SHORT).show();

//        MainAcyivity.open(activity, MainAcyivity.BOTTOM_FRAGMENT01);
    }

    @android.webkit.JavascriptInterface
    public void logout() {
        webView.loadUrl("javascript:javacalljswith('1111')");
    }

    @android.webkit.JavascriptInterface
    public String getUserInfo() {
        return JSON.toJSONString(BaseApplication.userInfo);
    }

    @android.webkit.JavascriptInterface
    public void addDoctor(String str) {
        Toast.makeText(BaseApplication.applicationContext, "------"+str, Toast.LENGTH_SHORT).show();
        webView.loadUrl("javascript:javacalljswith('啦啦啦啦')");
    }

}