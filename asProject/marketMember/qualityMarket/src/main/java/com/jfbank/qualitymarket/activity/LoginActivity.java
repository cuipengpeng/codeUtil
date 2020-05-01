package com.jfbank.qualitymarket.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huoyanzhengxin.zhengxin.print.FingerPrint;
import com.huoyanzhengxin.zhengxin.print.FingerPrintHelp;
import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.BuildConfig;
import com.jfbank.qualitymarket.R;
import com.jfbank.qualitymarket.base.BaseActivity;
import com.jfbank.qualitymarket.callback.AsyncResponseCallBack;
import com.jfbank.qualitymarket.config.CacheKeyConfig;
import com.jfbank.qualitymarket.constants.EventBusConstants;
import com.jfbank.qualitymarket.dao.StoreService;
import com.jfbank.qualitymarket.fragment.MyAccountFragment;
import com.jfbank.qualitymarket.helper.DiskLruCacheHelper;
import com.jfbank.qualitymarket.model.ResponseBean;
import com.jfbank.qualitymarket.model.User;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.test.CustomServerIpActivity;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.Des3;
import com.jfbank.qualitymarket.util.JpushUtil;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.TDUtils;
import com.jfbank.qualitymarket.util.ToastUtil;
import com.jfbank.qualitymarket.util.UpdateVersionUtils;

import org.simple.eventbus.EventBus;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 登录页面
 *
 * @author 崔朋朋
 */

public class LoginActivity extends BaseActivity implements TextWatcher {
    public static final String TAG = LoginActivity.class.getName();

    public static final String KEY_OF_COME_FROM = "comeFromKey";
    public static final String KEY_OF_BORROWMONEY = "comeFromBorrowMoney";
    public final static String TOKEN_FAIL_TAG = "tokenFailTag";

    private static final int PERMISSION_READ_PHONE_STATE = 0X01;
    @InjectView(R.id.rl_title)
    RelativeLayout rlTitle;
    @InjectView(R.id.tv_title)
    TextView tvTitle;
    @InjectView(R.id.et_loginActivity_username)
    EditText userNameEditText;
    @InjectView(R.id.et_loginActivity_passwd)
    EditText passwdEditText;
    @InjectView(R.id.btn_loginActivity_login)
    Button loginButton;
    @InjectView(R.id.btn_loginActivity_customServerIP)
    Button customServerIPButton;
    @InjectView(R.id.tv_loginActivity_register)
    TextView registerTextView;
    @InjectView(R.id.tv_loginActivity_forgetPasswd)
    TextView forgetPasswdTextView;
    @InjectView(R.id.iv_loginActivity_showPasswd)
    ImageView showPasswd;

    private String username;
    private String passwd;
    private boolean hidePasswd = true;
    private String comeFrom = "";
    private String IMEI;
    private FingerPrint fingerPrint;

    @OnClick({R.id.btn_loginActivity_login, R.id.tv_loginActivity_forgetPasswd, R.id.tv_loginActivity_register,
            R.id.iv_back, R.id.iv_loginActivity_showPasswd, R.id.btn_loginActivity_customServerIP})
    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.tv_loginActivity_forgetPasswd:
                Intent findPasswordIntent = new Intent(this, FindPasswordActivity.class);
                findPasswordIntent.putExtra(FindPasswordActivity.KEY_OF_COME_FROM, TAG);
                startActivity(findPasswordIntent);
                TDUtils.onEvent(mContext, "100012", "点击忘记密码");
                break;
            case R.id.tv_loginActivity_register:
                Intent registerIntent = new Intent(this, RegisterActivity.class);
                startActivity(registerIntent);
                TDUtils.onEvent(mContext, "100008", "点击注册");
                break;
            case R.id.btn_loginActivity_login:
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // 请求权限
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                            PERMISSION_READ_PHONE_STATE);
                } else {
                    disableLoginButton();
                    login(this, username, passwd);
                }

                break;
            case R.id.btn_loginActivity_customServerIP:
                // 自定义服务器IP
                Intent intent = new Intent(this, CustomServerIpActivity.class);
                startActivity(intent);
                break;

            case R.id.iv_back:
                launchActivity(false);
                break;
            case R.id.iv_loginActivity_showPasswd:
                // 显示和隐藏密码
                if (hidePasswd) {
                    showPasswd.setImageResource(R.mipmap.login_page_password_on);
                    hidePasswd = false;
                    passwdEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    passwdEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showPasswd.setImageResource(R.mipmap.login_page_password_off);
                    hidePasswd = true;
                }
                // 切换后将EditText光标置于末尾
                CharSequence charSequence = passwdEditText.getText();
                if (charSequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charSequence;
                    Selection.setSelection(spanText, charSequence.length());
                }
                break;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_READ_PHONE_STATE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                login(this, username, passwd);
            } else {
                // Toast.makeText(this, "", duration)
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        fingerPrint = FingerPrintHelp.getFingerPrint(getApplicationContext());
        fingerPrint.startTime();

        if (BuildConfig.DEBUG) {
            customServerIPButton.setVisibility(View.VISIBLE);
        } else {
            customServerIPButton.setVisibility(View.GONE);
        }
        CommonUtils.setTitle(this, rlTitle);
        tvTitle.setText(R.string.str_pagename_login);
        Intent intent = getIntent();
        comeFrom = intent.getStringExtra(KEY_OF_COME_FROM);
        userNameEditText.addTextChangedListener(this);
        passwdEditText.addTextChangedListener(this);
        String username = DiskLruCacheHelper.getAsString(CacheKeyConfig.CACHE_LOGIN_ACCOUNT);
        if (!TextUtils.isEmpty(username)) {
            userNameEditText.setText(username);
        }
        disableLoginButton();
    }

    @Override
    protected String getPageName() {
        return getString(R.string.str_pagename_login);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            launchActivity(false);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 启动指定的activity
     */
    private void launchActivity(boolean isReLogin) {
        if (TOKEN_FAIL_TAG.equals(comeFrom)) {//没有登录，直接关掉该界面，抢登，则跳转到个人中心
            if (!isReLogin) {
                AppContext.isLogin = false;
                AppContext.user = null;
            }
            launchMyAccountFragment();
        } else {//其他情况，则关掉当前界面
            finish();
        }
    }

    /**
     * 启动第四个tab页我的
     */
    public void launchMyAccountFragment() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.KEY_OF_BOTTOM_MENU, MyAccountFragment.TAG);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        finish();
    }

    /**
     * 登录方法
     *
     * @param activity 当前activity
     * @param username 登录用户名
     * @param passwd   登录密码
     */
    private void login(final Activity activity, final String username, final String passwd) {
        if (!RegisterActivity.checkPassword(passwd)) {
            Toast.makeText(activity, "密码只能为数字和字母", Toast.LENGTH_SHORT).show();
            enableLoginButton();
            return;
        }
        TDUtils.onEvent(mContext, "100012", "点击登录");
        IMEI = AppContext.getIMEI(this);
        Map<String, String> params = new HashMap<>();
        params.put("Uname", username);
        try {
            params.put("password", Des3.encode(passwd));
        } catch (Exception e) {
            e.printStackTrace();
        }
        params.put("equipmentNumber", IMEI);
        params.put("ver", AppContext.getAppVersionName(AppContext.mContext));
        params.put("Plat", ConstantsUtil.CHANNEL_CODE);
        params.put("channel", CommonUtils.getAppChannelId(this) + "");
        params.put("longitude", AppContext.location.getLongitude());// 经度
        params.put("dimension", AppContext.location.getLatitude());// 纬度

        HttpRequest.post(mContext, HttpRequest.QUALITY_MARKET_WEB_URL + HttpRequest.USER_LOGIN, params,
                new AsyncResponseCallBack() {

                    @Override
                    public void onResult(String arg2) {
                        int inputLength = fingerPrint.getStringLength(username, passwd);
                        fingerPrint.endTime(username, "android登录页面", inputLength);


                        String jsonStr = new String(arg2);
                        LogUtil.printLog("登录：" + jsonStr);
                        Log.e("TAG", jsonStr);
                        JSONObject jsonObject = JSON.parseObject(jsonStr);
                        ResponseBean responseBean = JSON.parseObject(jsonStr, ResponseBean.class);
                        if (ConstantsUtil.RESPONSE_SUCCEED == responseBean.getStatus()) {
                            AppContext.user = JSON.parseObject(jsonStr, User.class);
                            new StoreService(activity).saveUserInfo(AppContext.user);
                            new WebView(LoginActivity.this).clearCache(true);
                            setAliasAndTag(AppContext.user.getUid());
                            AppContext.isLogin = true;
                            EventBus.getDefault().post(new Object(), EventBusConstants.EVENTT_UPDATE_TOKAN_RESET);
                            DiskLruCacheHelper.put(CacheKeyConfig.CACHE_LOGIN_ACCOUNT, username);
                            launchActivity(true);
                        } else if (ConstantsUtil.RESPONSE_UPDATE_VERSION == responseBean.getStatus()) {
                            UpdateVersionUtils.updateVersion(LoginActivity.this, jsonObject);
                        } else {
                            ToastUtil.show(responseBean.getStatusDetail());
                        }
                        enableLoginButton();
                    }

                    @Override
                    public void onFailed(String path, String msg) {
                        Toast.makeText(AppContext.mContext, ConstantsUtil.ORIGIN_PAGE_FAIL_TO_CONNECT_SERVER,
                                Toast.LENGTH_SHORT).show();
                        enableLoginButton();
                    }
                });

    }

    /**
     * 设置极光推送的别名和Tag
     *
     * @param uid
     */
    private void setAliasAndTag(String uid) {
        if (null == uid || "".equals(uid)) {
            Toast.makeText(this, "别名的值获取失败", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!JpushUtil.isValidTagAndAlias(uid)) {
            Toast.makeText(this, "设置别名失败", Toast.LENGTH_SHORT).show();
            return;
        }

        // 调用 Handler 来异步设置别名
        Message message = Message.obtain();
        Bundle data = new Bundle();
        data.putString("alias", uid);
        data.putString("tag", "android");
        message.setData(data);
        message.what = MSG_SET_ALIAS;
        mHandler.sendMessageAtTime(message, 0);
    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.e(TAG, logs);
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.e(TAG, logs);
                    // 延迟 60 秒来调用 Handler 设置别名
                    Message message = Message.obtain();
                    Bundle data = new Bundle();
                    data.putString("alias", alias);
                    data.putString("tag", "android");
                    message.setData(data);
                    message.what = MSG_SET_ALIAS;
                    mHandler.sendMessageDelayed(message, 1000 * 2);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }
            //JpushUtil.showToast(logs, getApplicationContext());
            Log.e("TAG", JPushInterface.getUdid(getApplicationContext()) + "::" + IMEI);
        }
    };

    private static final int MSG_SET_ALIAS = 1001;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d(TAG, "Set alias in handler.");
                    // 调用 JPush 接口来设置别名。
                    Bundle bundle = msg.getData();
                    HashSet set = new HashSet<String>();
                    set.add(bundle.getString("tag"));
                    JPushInterface.setAliasAndTags(getApplicationContext(),
                            bundle.getString("alias"),
                            set,
                            mAliasCallback);
                    break;
                default:
                    Log.i(TAG, "Unhandled msg - " + msg.what);
            }
        }
    };


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        username = userNameEditText.getText().toString();
        passwd = passwdEditText.getText().toString();

        if (CommonUtils.isMobilePhoneVerify(username) && passwd.length() > 5) {
            enableLoginButton();
        } else {
            disableLoginButton();
        }
    }

    private void enableLoginButton() {
        loginButton.setEnabled(true);
        loginButton.setBackgroundResource(R.drawable.button_selector);
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    private void disableLoginButton() {
        loginButton.setEnabled(false);
        loginButton.setBackgroundResource(R.drawable.login_page_button_disabled);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
