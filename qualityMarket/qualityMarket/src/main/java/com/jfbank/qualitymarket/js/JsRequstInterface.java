package com.jfbank.qualitymarket.js;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.activity.CameraPhotoActivity;
import com.jfbank.qualitymarket.activity.ConfirmOrderActivity;
import com.jfbank.qualitymarket.activity.LoginActivity;
import com.jfbank.qualitymarket.activity.MainActivity;
import com.jfbank.qualitymarket.activity.SearchGoodsActivity;
import com.jfbank.qualitymarket.activity.WebViewActivity;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
import com.jfbank.qualitymarket.fragment.MyAccountFragment;
import com.jfbank.qualitymarket.listener.IWebJsLisenter;
import com.jfbank.qualitymarket.model.MenuNavBean;
import com.jfbank.qualitymarket.model.User;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.ActivityManager;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.DownloadFileUtil;
import com.jfbank.qualitymarket.util.FaceDetectUtil;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.StringUtil;
import com.jfbank.qualitymarket.util.TDUtils;
import com.jfbank.qualitymarket.util.UserUtils;
import com.jfbank.qualitymarket.widget.LoadingAlertDialog;
import com.jfbank.qualitymarket.widget.MyPopupDialog;
import com.sensetime.stlivenesslibrary.ui.LivenessActivity;
import com.tencent.smtt.sdk.WebView;

import org.json.JSONException;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

/**
 * webview的js请求java(Android)接口类
 *
 * @author 崔朋朋
 */
public class JsRequstInterface {
    public static final String TAG = JsRequstInterface.class.getName();
    public static final String CATEGORY_ID = "categoryId";
    public static final String CLASSIFY_ID = "classifyId";
    public static final String CATEGORY_LEVEL1_ID = "categoryLevel1Id";
    public static final String ORDER_JSON_STRING = "orderStr";
    public static final int EXTRA_QUESTCODE_FACEFUNC = 0x33;
    public static final int REQUEST_CODE_TAKE_IDCARD = 0x36;
    public static final String EXTRA_TAKE_IDCARD_TYPE = "take_idcard";
    IWebJsLisenter iWebJsLisenter;
    private WebView webView;
    Activity activity;
    private String downloadUrl;
    private String downloadFileName;
    private static MyPopupDialog dialog = null;
    public static final int UNDEFINE_DIALOG_EVENT = 1001;

    /**
     * js交互入口
     *
     * @param url
     */
    public void jsInteract(String url) {
        Log.e("sss", url);
        try {
            url = URLDecoder.decode(url, "utf-8");
            String titleType = "jfbank://hybrid-container/widget/nav_title?data=";
            if (url.startsWith(titleType)) {//修改标题
                String data = url.substring(titleType.length());
                iWebJsLisenter.setH5Title(JSON.parseObject(data).getString("title"));
                return;
            }
            titleType = "jfbank://hybrid-container/widget/nav_menu?data=";
            if (url.startsWith(titleType)) {//显示菜单
                String data = url.substring(titleType.length());
                MenuNavBean menuNavBean = JSON.parseObject(data, MenuNavBean.class);
                if (TextUtils.equals("showShare", menuNavBean.getAchieve())) {//为分享时
                    showShare(menuNavBean.getParamStr(data));
                }
                return;
            }
            titleType = "jfbank://hybrid-container/widget/appCamera?data=";
            if (url.startsWith(titleType)) {//显示菜单
                String data = url.substring(titleType.length());
                MenuNavBean menuNavBean = JSON.parseObject(data, MenuNavBean.class);
                if (TextUtils.equals("jsHandlerCamera", menuNavBean.getAction())) {//拍照
                    loadAppCamera(menuNavBean.getType());
                }
                return;
            }

            titleType = "jfbank://hybrid-container/widget/token_expired?data=";
            if (url.startsWith(titleType)) {//token失效
                String data = url.substring(titleType.length());
                MenuNavBean menuNavBean = JSON.parseObject(data, MenuNavBean.class);
                if (TextUtils.equals("tokenExpired", menuNavBean.getAchieve()) || TextUtils.equals("tokenExpired", menuNavBean.getAction())) {//token失效
                    tokenExpired(menuNavBean.getParamStr(data));
                }
                return;
            }
            titleType = "jfbank://hybrid-container/widget/button_click?data=";
            if (url.startsWith(titleType)) {//button点击
                String data = url.substring(titleType.length());
                MenuNavBean menuNavBean = JSON.parseObject(data, MenuNavBean.class);
                buttonClick(menuNavBean, data);
                return;
            }
            titleType = "jfbank://hybrid-container/widget/login?data=";
            if (url.startsWith(titleType)) {//关闭当前
                String data = url.substring(titleType.length());
                MenuNavBean menuNavBean = JSON.parseObject(data, MenuNavBean.class);
                if (TextUtils.equals("login", menuNavBean.getAchieve())) {//打开相机
                    login(menuNavBean.getParamStr(data));
                }
                return;
            }
            titleType = "jfbank://hybrid-container/widget/onClose?data=";
            if (url.startsWith(titleType)) {//s设置关闭当前页面方式
                String data = url.substring(titleType.length());
                int statusClose = JSON.parseObject(data).getInteger("onClose");
                iWebJsLisenter.setCallBack(statusClose);
                return;
            }
            titleType = "jfbank://hybrid-container/widget/onBack?data=";
            if (url.startsWith(titleType)) {//关闭当前页面
                String data = url.substring(titleType.length());
                int statusClose = JSON.parseObject(data).getInteger("onBack");
                iWebJsLisenter.onH5Back(statusClose);
                return;
            }
            titleType = "jfbank://hybrid-container/widget/appCamera?data=";
            if (url.startsWith(titleType)) {//button点击
                String data = url.substring(titleType.length());
                MenuNavBean menuNavBean = JSON.parseObject(data, MenuNavBean.class);
                if (TextUtils.equals("jsHandlerCamera", menuNavBean.getAction())) {//打开相机
                    jsHandlerCamera();
                }
                return;
            }
            titleType = "jfbank://hybrid-container/widget/appContact?data=";
            if (url.startsWith(titleType)) {//button点击
                String data = url.substring(titleType.length());
                MenuNavBean menuNavBean = JSON.parseObject(data, MenuNavBean.class);
                if (TextUtils.equals("getContactInfo", menuNavBean.getAction())) {//打开相机
                    getContactInfo();
                }
                return;
            }
            titleType = "jfbank://hybrid-container/widget/appFace?data=";
            if (url.startsWith(titleType)) {//button点击
                String data = url.substring(titleType.length());
                MenuNavBean menuNavBean = JSON.parseObject(data, MenuNavBean.class);
                if (TextUtils.equals("jsHandlerFunc", menuNavBean.getAction())) {//打开相机
                    jsHandlerFunc(activity);
//                    webView.loadUrl("javascript: jsHandlerFunc('" + 0 + "')");//测试拍照
                }
                return;
            }

            titleType = "jfbank://hybrid-container/widget/backPzsc";
            if (url.startsWith(titleType)) {//关闭当前
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.finish();
                    }
                });
                return;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 人脸识别
     */
    public static void jsHandlerFunc(final Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, LivenessActivity.class);
        /**
         * EXTRA_MOTION_SEQUENCE 动作检测序列配置，支持四种检测动作， BLINK(眨眼), MOUTH（张嘴）, NOD（点头）, YAW（摇头）, 各个动作以空格隔开。 推荐第一个动作为BLINK.
         * 默认配置为"BLINK MOUTH NOD YAW"
         */
        intent.putExtra(LivenessActivity.EXTRA_MOTION_SEQUENCE, "BLINK MOUTH NOD YAW");
        /**
         * SOUND_NOTICE 配置, 传入的soundNotice为boolean值，true为打开, false为关闭, 默认为true.
         */
        intent.putExtra(LivenessActivity.SOUND_NOTICE, true);
        /**
         * OUTPUT_TYPE 配置, 传入的outputType类型为singleImg （单图）或 multiImg （多图）, 默认为multiImg.
         */
        intent.putExtra(LivenessActivity.OUTPUT_TYPE, "multiImg");
        /**
         * COMPLEXITY 配置, 传入的complexity类型, 支持四种难度:easy, normal, hard, hell.默认为normal.
         */
        intent.putExtra(LivenessActivity.COMPLEXITY, "normal");
        File livenessFolder = new File(FaceDetectUtil.storageFolder);
        if (!livenessFolder.exists()) {
            livenessFolder.mkdirs();
        }
        // 开始检测之前请删除文件夹下保留的文件
        /**
         * EXTRA_RESULT_PATH 配置， 传入的storageFolder为sdcard下目录, 为了保存检测结果文件, 传入之前请确保该文件夹存在。
         */
        intent.putExtra(LivenessActivity.EXTRA_RESULT_PATH, FaceDetectUtil.storageFolder);
        activity.startActivityForResult(intent, EXTRA_QUESTCODE_FACEFUNC);
    }

    /**
     * 获取通讯录信息
     */
    public void getContactInfo() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iWebJsLisenter.openContacts("getContactInfo");
            }
        });
    }

    /**
     * H5点击交互
     *
     * @param menuNavBean
     * @param data
     */
    private void buttonClick(MenuNavBean menuNavBean, String data) {
        if (TextUtils.equals("loadAppCamera", menuNavBean.getAchieve())) {//立即下单
            loadAppCamera(menuNavBean.getParamStr(data));
            return;
        }
        if (TextUtils.equals("goOrder", menuNavBean.getAchieve())) {//立即下单
            goOrder(menuNavBean.getParamStr(data));
            return;
        }
        if (TextUtils.equals("goIndex", menuNavBean.getAchieve()) || TextUtils.equals("goIndex", menuNavBean.getAction())) {//去首页
            goIndex(menuNavBean.getParamStr(data));
            return;
        }
        if (TextUtils.equals("goProductDetail", menuNavBean.getAchieve())) {//去商品详情
            goProductDetail(menuNavBean.getParamStr(data));
            return;
        }
        if (TextUtils.equals("requestAllContact", menuNavBean.getAchieve())) {//
            requestAllContact();
            return;
        }
        if (TextUtils.equals("openAddressBook", menuNavBean.getAchieve())) {//
            openAddressBook();
            return;
        }
        if (TextUtils.equals("refresh", menuNavBean.getAchieve())) {//
            refresh();
            return;
        }
        if (TextUtils.equals("goProductList", menuNavBean.getAchieve())) {//
            goProductList(menuNavBean.getParamStr(data));
            return;
        }
        if (TextUtils.equals("h5CopyText", menuNavBean.getAchieve())) {//
            h5CopyText(menuNavBean.getParamStr(data));
            return;
        }
        if (TextUtils.equals("goMyAuthentication", menuNavBean.getAchieve())) {//
            goMyAuthentication(menuNavBean.getParamStr(data));
            return;
        }
        if (TextUtils.equals("goClassification", menuNavBean.getAchieve())) {//
            goClassification(menuNavBean.getParamStr(data));
            return;
        }
        if (TextUtils.equals("goActivity", menuNavBean.getAchieve())) {//
            goActivity(menuNavBean.getParamStr(data));
            return;
        }
        if (TextUtils.equals("downloadContract", menuNavBean.getAchieve())) {//
            downloadContract(menuNavBean.getParamStr(data));
            return;
        }
        if (TextUtils.equals("goJhbt", menuNavBean.getAchieve())) {//
            goJhbt(menuNavBean.getParamStr(data));
            return;
        }
        if (TextUtils.equals("goMy", menuNavBean.getAchieve())) {//
            goMy(menuNavBean.getParamStr(data));
            return;
        }
        if (TextUtils.equals("login", menuNavBean.getAchieve())) {//
            login(menuNavBean.getParamStr(data));
            return;
        }
        if (TextUtils.equals("share", menuNavBean.getAchieve())) {//
            share(menuNavBean.getParamStr(data));
            return;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    // 点击获取验证码后的倒计时任务
                    String pdfPath = DownloadFileUtil.getFile(downloadUrl, downloadFileName).getAbsolutePath();
                    Toast.makeText(activity, "下载文件成功：" + pdfPath, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri = Uri.fromFile(DownloadFileUtil.getFile(downloadUrl, downloadFileName));
                    intent.setDataAndType(uri, "application/pdf");
                    activity.startActivity(intent);
                    break;
                case 2:
                    // 发送取消订单请求
                    Toast.makeText(activity, "下载文件失败", Toast.LENGTH_SHORT).show();
                    break;

            }
        }

        ;
    };

    public JsRequstInterface() {
        super();
    }

    public JsRequstInterface(Activity activity, WebView webView, IWebJsLisenter iWebJsLisenter) {
        super();
        this.activity = activity;
        this.webView = webView;
        this.iWebJsLisenter = iWebJsLisenter;
    }

    public void jsHandlerCamera() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iWebJsLisenter.openCamera();
            }
        });
    }

    @android.webkit.JavascriptInterface
    public void requestAllContact() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iWebJsLisenter.requstAllContacts();
            }
        });
    }

    @android.webkit.JavascriptInterface
    public void openAddressBook() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iWebJsLisenter.openContacts("selectedContact");
            }
        });

    }

    @android.webkit.JavascriptInterface
    public void loadAppCamera(final String type) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!CameraPhotoActivity.isStartCameraAct) {
                    CameraPhotoActivity.isStartCameraAct = true;
                    Intent intent = new Intent(activity, CameraPhotoActivity.class);
                    intent.putExtra(EXTRA_TAKE_IDCARD_TYPE, type);
                    activity.startActivityForResult(intent, REQUEST_CODE_TAKE_IDCARD);
                }
            }
        });
    }

    @android.webkit.JavascriptInterface
    public void refresh() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iWebJsLisenter.onRefreshWebUrl();
            }
        });

    }

    /**
     * 搜索列表
     *
     * @param param
     */
    @android.webkit.JavascriptInterface
    public void goProductList(final String param) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = JSON.parseObject(param);
                // String productID = jsonObject.getString("productID");
                String achieve = jsonObject.getString(ConstantsUtil.JS_INTERFACE_FILE_NAME_ACHIEVE);
                String authority = jsonObject.getString(ConstantsUtil.JS_INTERFACE_FILE_NAME_AUTHORITY);
                LogUtil.printLog("goProductList---" + param);

                Intent intent = new Intent(activity, SearchGoodsActivity.class);
                intent.putExtra(CLASSIFY_ID, jsonObject.getString("classifyID"));
                activity.startActivity(intent);
            }
        });
    }

    /**
     * H5复制文字到系统
     *
     * @param copyText
     */
    @android.webkit.JavascriptInterface
    public void h5CopyText(final String copyText) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ClipboardManager clipboardManager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(ClipData.newPlainText(null, copyText));
            }
        });
    }

    /**
     * @param param
     */
    @android.webkit.JavascriptInterface
    public void goMyAuthentication(String param) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.finish();
            }
        });
    }

    /**
     * 商品分类
     *
     * @param param
     */
    @android.webkit.JavascriptInterface
    public void goClassification(final String param) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogUtil.printLog("goClassification---" + param);

                JSONObject jsonObject = JSON.parseObject(param);

                String classifyID = jsonObject.getString("classifyID");
                String achieve = jsonObject.getString(ConstantsUtil.JS_INTERFACE_FILE_NAME_ACHIEVE);
                String authority = jsonObject.getString(ConstantsUtil.JS_INTERFACE_FILE_NAME_AUTHORITY);

                Intent intent = new Intent(activity, WebViewActivity.class);
                intent.putExtra(CATEGORY_ID, param);
                activity.startActivity(intent);
            }
        });
    }

    /**
     * 活动页面banner
     *
     * @param param
     */
    @android.webkit.JavascriptInterface
    public void goActivity(final String param) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogUtil.printLog("goActivity---" + param);
                JSONObject jsonObject = JSON.parseObject(param);
//        String achieve = jsonObject.getString(ConstantsUtil.JS_INTERFACE_FILE_NAME_ACHIEVE);
//        String authority = jsonObject.getString(ConstantsUtil.JS_INTERFACE_FILE_NAME_AUTHORITY);
                // 活动页面
                JSONObject activityJsonObject = JSON.parseObject(param);
                // 统一由h5页面传递全部地址
                String loadUrl = activityJsonObject.getString("activityUrl");
                if (AppContext.isLogin && loadUrl.contains("activity-11/doubleapp.html")) {//双十一红包
                    loadUrl = loadUrl + "?uid=" + AppContext.user.getUid() + "&token=" + AppContext.user.getToken()
                            + "&mobile=" + AppContext.user.getMobile();
                }
                CommonUtils.startWebViewActivity(activity, loadUrl, true, false);
            }
        });
    }

    /**
     * 快去购物吧
     *
     * @param param
     */
    @android.webkit.JavascriptInterface
    public void goIndex(String param) {
        LogUtil.printLog("goIndex---" + param);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent mianIntent = new Intent(activity, MainActivity.class);
                activity.startActivity(mianIntent);
                activity.finish();
            }
        });
    }

    /**
     * token失效
     *
     * @param param
     */
    @android.webkit.JavascriptInterface
    public void tokenExpired(final String param) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogUtil.printLog("tokenExpired---" + param);
                JSONObject jsonObject = JSON.parseObject(param);
                String message = jsonObject.getString("message");
                jsonObject = JSON.parseObject(message);
                String errorMsg = jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME);
                UserUtils.tokenFailDialog(activity, errorMsg, TAG);
            }
        });
    }

    /**
     * 下载合同页面的pdf文件
     *
     * @param param
     */
    @android.webkit.JavascriptInterface
    public void downloadContract(final String param) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogUtil.printLog("downloadContract---" + param);
                JSONObject jsonObject = JSON.parseObject(param);
                downloadUrl = jsonObject.getString("url");
                downloadFileName = jsonObject.getString("text");
                DownloadFileUtil.downFile(handler, downloadUrl, downloadFileName);
            }
        });
    }

    /**
     * 商品详情
     *
     * @param param
     */
    @android.webkit.JavascriptInterface
    public void goProductDetail(final String param) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CommonUtils.startGoodsDeteail(activity, param);
            }
        });
    }

    @android.webkit.JavascriptInterface
    public void goJhbt(String param) {// 去激活白条
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (AppContext.isLogin && AppContext.user != null)
                    CommonUtils.startWebViewActivity(activity, MyAccountFragment.getOneCardUrlParamter(activity, AppContext.user.getUrl()), true, false, false);
            }
        });
    }

    @android.webkit.JavascriptInterface
    public void goMy(String param) {// 去我的界面
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(activity, MainActivity.class);
                intent.putExtra(MainActivity.KEY_OF_BOTTOM_MENU, MyAccountFragment.TAG);
                activity.startActivity(intent);
                activity.finish();
            }
        });
    }

    /**
     * 立即订单
     *
     * @param param
     */
    @android.webkit.JavascriptInterface
    public void goOrder(final String param) {

        LogUtil.printLog("goOrder---" + param);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (AppContext.isLogin) {
                    checkCreditLines(activity, param);
                } else {
                    Intent intent = new Intent();
                    intent.setClass(activity, LoginActivity.class);
                    intent.putExtra(LoginActivity.KEY_OF_COME_FROM, TAG);
                    activity.startActivity(intent);
                }
            }
        });
    }

    /**
     * 是否授信及额度是否够用
     *
     * @param activity
     */
    public void checkCreditLines(final Activity activity, final String param) {
        final LoadingAlertDialog mDialog = new LoadingAlertDialog(activity);
        mDialog.show(ConstantsUtil.NETWORK_REQUEST_IN);
        final JSONObject jsonObjectParams = JSON.parseObject(param);
        Map<String, String> params = new HashMap<>();
        params.put("uid", AppContext.user.getUid());
        params.put("token", AppContext.user.getToken());
        params.put("productTotal", MyAccountFragment.moneyDecimalFormat.format(jsonObjectParams.getDouble("jdPrice")));
        params.put("firstPayment", jsonObjectParams.getDouble("downpaymentMonth") + "");
        params.put("isActivity", jsonObjectParams.getString("isActivity"));
        params.put("activityProductNo", jsonObjectParams.getString("productNo"));
        if (jsonObjectParams.containsKey("activityNo")) {
            params.put("activityNo", jsonObjectParams.getString("activityNo"));
        } else {
            params.put("activityNo", "");
        }
        TDUtils.onEvent(activity, "100013", "点击立即下单", TDUtils.getInstance().putUserid().buildParams());
        HttpRequest.post(activity, HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.CHECK_CREDIT_LINES, params,
                new AsyncResponseCallBack() {

                    @Override
                    public void onResult(String arg2) {
                        if (mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        String jsonStr = new String(arg2);
                        LogUtil.printLog("查询授信及额度是否够用: " + jsonStr);
                        JSONObject jsonObject = JSON.parseObject(jsonStr);
                        User user = JSON.parseObject(jsonStr, User.class);
                        if (AppContext.user != null && AppContext.isLogin) {
                            AppContext.user.setUrl(user.getUrl());
                            AppContext.user.setMessage(user.getMessage());
                            AppContext.user.setStep(user.getStep());
                        }

                        if (ConstantsUtil.RESPONSE_SUCCEED == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            //        1未实名
                            //        2未设置交易密码
                            //        3用户无额度
                            //        4用户额度未激活
                            //        5用户额度已冻结
                            //        6未开卡
                            //        7已冻结
                            //        8额度状态未审批
                            //        9信用额度不足
                            //        0成功
                            if ("0".equals(AppContext.user.getStep())) {
                                launchConfirmOrderActivity(activity, param);
                            } else {
                                String errorMessage = jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME);
                                popupDialog(activity, errorMessage, Integer.valueOf(AppContext.user.getStep()));
                            }
                        } else if (ConstantsUtil.RESPONSE_TOKEN_FAIL == jsonObject
                                .getIntValue(ConstantsUtil.RESPONSE_STATUS_FIELD_NAME)) {
                            UserUtils.tokenFailDialog(activity,
                                    jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME), TAG);
                        } else {
                            String errorMessage = jsonObject.getString(ConstantsUtil.RESPONSE_MESSAGE_FIELD_NAME);
                            popupDialog(activity, errorMessage, UNDEFINE_DIALOG_EVENT);
                        }
                    }

                    @Override
                    public void onFailed(String path, String msg) {
                        if (mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        Toast.makeText(activity, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER + "是否授信及额度是否够用",
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }

    /**
     * 启动确认订单页面
     *
     * @param activity
     * @param param
     */
    private void launchConfirmOrderActivity(Activity activity, String param) {
        Intent intent = new Intent(activity, ConfirmOrderActivity.class);
        intent.putExtra(ORDER_JSON_STRING, param);
        ActivityManager.getInstance().finishOrderAllActivity();
        ActivityManager.getInstance().addOrderActivity(activity);
        activity.startActivity(intent);
    }

    /**
     * 弹出对话框
     * <p>
     * //	 * @param activity
     * //	 *            当前上下文
     * //	 * @param mTitle
     * //	 *            对话框标题
     * //	 * @param mContent
     * //	 *            对话框内容
     * //	 * @param mLeftBtn
     * //	 *            左侧按钮文本
     * //	 * @param mRightBtn
     * //	 *            右侧按钮文本
     * //	 * @param mOneBtn
     * //	 *            只有一个按钮时的文本
     * //	 * @param isTwoButton
     * //	 *            是否有两个按钮
     * //	 * @param dialogEvent
     * //	 *            对话框事件
     */
    public static void popupDialog(final Activity activity, String mContent, final int dialogEvent) {
//        1未实名
//        2未设置交易密码
//        3用户无额度
//        4用户额度未激活
//        5用户额度已冻结
//        6未开卡
//        7已冻结
//        8额度状态未审批
//        9信用额度不足
//        0成功

        switch (dialogEvent) {
            case 1:
            case 2:
            case 6:
                dialog = new MyPopupDialog(activity, "前去开卡", mContent, "否", "去开卡", null, true);
                break;
            case 3:
            case 4:
            case 8:
                dialog = new MyPopupDialog(activity, "前去激活", mContent, "否", "去激活", null, true);
                break;
            case 9:
                dialog = new MyPopupDialog(activity, "前去提额", mContent, "否", "去提额", null, true);
                break;
            case 5:
            case 7:
                dialog = new MyPopupDialog(activity, "万卡额度冻结", mContent, null, null, "确定", false);
                break;
            case ConstantsUtil.PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST_CODE:
            case ConstantsUtil.PERMISSION_CAMERA_REQUEST_CODE:
            case ConstantsUtil.PERMISSION_LOCATION_REQUEST_CODE:
                dialog = new MyPopupDialog(activity, null, mContent, null, null, "确定", false);
                break;
            default:
                dialog = new MyPopupDialog(activity, null, mContent, null, null, "确定", false);
                break;
        }

        dialog.setOnClickListen(new MyPopupDialog.OnClickListen() {

            @Override
            public void rightClick() {
                dialog.dismiss();
                Intent intent = new Intent();
                switch (dialogEvent) {
                    case 1://个人授信第一步
                    case 2:
                    case 6:
                    case 3:
                    case 4:
                    case 8:
                    case 9:
                        CommonUtils.startWebViewActivity(activity, MyAccountFragment.getOneCardUrlParamter(activity, AppContext.user.getUrl()), true, false, false);
                        break;
                    case ConstantsUtil.PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST_CODE:
                        MainActivity.checkPermission(activity, ConstantsUtil.PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                        break;
                    case ConstantsUtil.PERMISSION_CAMERA_REQUEST_CODE:
                        MainActivity.checkPermission(activity, ConstantsUtil.PERMISSION_CAMERA_REQUEST_CODE);
                        break;
                    case ConstantsUtil.PERMISSION_LOCATION_REQUEST_CODE:
                        MainActivity.checkPermission(activity, ConstantsUtil.PERMISSION_LOCATION_REQUEST_CODE);
                        break;
                    default:
                        dialog.dismiss();
                        break;
                }
            }

            @Override
            public void leftClick() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 双十一红包 抢红包登录判断
     *
     * @param param 登录参数
     */
    @android.webkit.JavascriptInterface
    public void login(final String param) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("双十一红包 抢红包登录返回参数", param);
                if (AppContext.isLogin) {// 登录
                    try {
                        final org.json.JSONObject redPacketJson = new org.json.JSONObject(param);
                        webView.loadUrl(CommonUtils.getBase64Url(redPacketJson.optString("url", "")) + "?uid=" + AppContext.user.getUid()
                                + "&token=" + AppContext.user.getToken() + "&mobile=" + AppContext.user.getMobile());
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage() + "");
                    }

                } else {// 跳转到等级界面
                    Intent intent = new Intent();
                    intent.setClass(activity, LoginActivity.class);
                    intent.putExtra(LoginActivity.KEY_OF_COME_FROM, TAG);
                    activity.startActivity(intent);
                }
            }
        });
    }

    @android.webkit.JavascriptInterface
    public void showShare(final String shareStr) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iWebJsLisenter.setShare(shareStr);
            }
        });
    }

    /**
     * //	 * @param context
     * //	 *            上下文
     * //	 * @param title
     * //	 *            分享的标题
     * //	 * @param content
     * //	 *            分享的内容
     * //	 * @param imagePathOrImageUrl
     * //	 *            分享的图标
     * //	 * @param urlAfterClick
     * //	 *            点击分享的item后响应的url地址
     * //	 * @param shareRecommendCode
     * //	 *            直接传值false
     */
    @android.webkit.JavascriptInterface
    public void share(final String shareStr) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                org.json.JSONObject shareJson;
                try {
                    shareJson = new org.json.JSONObject(shareStr);
                    LogUtil.printLog("红包分享:" + shareStr);
                    final String localTitle = shareJson.optString("title", "红包分享");
                    final String localContent = shareJson.optString("content", "红包分享");
                    final String urlAfterClick = shareJson.optString("url", "");
                    ShareSDK.initSDK(activity);
                    OnekeyShare oks = new OnekeyShare();
                    // 关闭sso授权
                    oks.disableSSOWhenAuthorize();

                    // 显示推荐码
                    // oks.setShareRecommendCode(shareRecommendCode);
                    oks.setSilent(false);

                    oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {

                        @Override
                        public void onShare(Platform platform, ShareParams paramsToShare) {

                            // // 本地分享数据
                            // // String localTitle =
                            // // context.getResources().getString(R.string.app_name);
                            // String localTitle = "分期购物上万卡商城";
                            // // String localContent = "亲，我是" + user.getNickName() +
                            // //
                            // ",我刚投的，看看这收益，相当不赖吧！十年大品牌，玖富真靠谱~新注册还送9999元体验金！~注册请填推荐码:";
                            // String localContent =
                            // "玖富万卡商城，京东品质保证，正品行货，爆款直降，白条购物，想分就分！";
                            // // String localUrl = "https://www.9fbank.com/";
                            String localUrl = HttpRequest.QUALITY_MARKET_WEB_URL + "views/share.html";

                            SharedPreferences sharedPreferences = activity
                                    .getSharedPreferences(ConstantsUtil.GLOBAL_CONFIG_FILE_NAME, Context.MODE_PRIVATE);
                            String localImagePath = sharedPreferences.getString(ConstantsUtil.APP_ICON_LOCAL_STORE_KEY, null);

                            if (StringUtil.notEmpty(urlAfterClick)) {
                                localUrl = urlAfterClick;
                            }

                            paramsToShare.setTitle(localTitle);
                            // 微信收藏和微信朋友圈该属性不显示
                            paramsToShare.setText(localContent);
                            // if (imagePathOrImageUrl != null &&
                            // !"".equals(imagePathOrImageUrl)) {
                            // paramsToShare.setImageUrl(imagePathOrImageUrl);
                            // //
                            // paramsToShare.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
                            // } else {
                            paramsToShare.setImagePath(localImagePath);
                            // }
                            // url仅在微信（包括好友和朋友圈）中使用
                            paramsToShare.setUrl(localUrl);
                            paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                            // titleUrl是标题的网络链接，site是分享此内容的网站名称，siteUrl是分享此内容的网站地址,仅在人人网和QQ空间使用
                            paramsToShare.setTitleUrl(localUrl);
                            paramsToShare.setSiteUrl(localUrl);
                            // 发短信用到的参数
                            paramsToShare.setAddress("");

                            // paramsToShare.setTitle("分享标题--Title");
                            // paramsToShare.setTitleUrl("http://mob.com");
                            // paramsToShare.setText("分享测试文--Text");
                            // paramsToShare.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
                        }
                    });
                    // 启动分享GUI
                    oks.show(activity);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}