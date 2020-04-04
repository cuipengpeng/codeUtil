package com.caishi.chaoge.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseApplication;
import com.caishi.chaoge.base.BaseUILocalDataActivity;
import com.caishi.chaoge.bean.LoginBean;
import com.caishi.chaoge.http.Account;
import com.caishi.chaoge.http.HttpRequest;
import com.caishi.chaoge.http.RequestURL;
import com.caishi.chaoge.ui.widget.LoadingAlertDialog;
import com.caishi.chaoge.utils.LogUtil;
import com.caishi.chaoge.utils.SPUtils;
import com.caishi.chaoge.utils.ToastUtils;
import com.caishi.chaoge.utils.Utils;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseUILocalDataActivity {

    @BindView(R.id.tv_loginActivity_userProtocal)
    TextView userProtocalTextView;
    @BindView(R.id.tv_login_back)
    TextView tv_login_back;
    @BindView(R.id.et_login_number)
    EditText et_login_number;
    @BindView(R.id.et_login_code)
    EditText et_login_code;
    @BindView(R.id.btn_login_code)
    Button btn_login_code;
    @BindView(R.id.btn_login_login)
    Button btn_login_login;
    @BindView(R.id.img_login_wx)
    ImageView img_login_wx;
    @BindView(R.id.img_login_qq)
    ImageView img_login_qq;
    @BindView(R.id.img_login_wb)
    ImageView img_login_wb;

    private String loginFlag;
    public static final String KEY_OF_COME_FROM = "comeFromKey";
    private int mComeFrom;
    private boolean countDownNow = false;
    public static final String USER_PROTOCAL_HTML = "http://api.chaogevideo.com/static/term/terms.html";
    public static final String TEXT_COLOR_GRAY = "#30000000";
    public static final String KEY_OF_ENABLE_LOGIN_BUTTON = "enableLoginButton";
    private CountDownTimer myCountDownTimer;
    private String partnerTypeId = "00";
    private LoadingAlertDialog loadingAlertDialog = null;

    @OnClick({R.id.tv_loginActivity_userProtocal, R.id.btn_login_code, R.id.btn_login_login,
            R.id.img_login_wx, R.id.img_login_qq, R.id.img_login_wb, R.id.tv_login_back})
    public void OnClickView(View v) {
        switch (v.getId()) {
            case R.id.tv_loginActivity_userProtocal:
                Intent intent = new Intent(this, WebviewActivity.class);
                intent.putExtra(WebviewActivity.KEY_OF_URL, USER_PROTOCAL_HTML);
                startActivity(intent);
                break;
            case R.id.btn_login_code:
                String userNumber = et_login_number.getText().toString().trim();
                if (TextUtils.isEmpty(userNumber)) {
                    ToastUtils.show(this, "请输入手机号");
                    return;
                }
                if (userNumber.length() != 11) {
                    ToastUtils.show(this, "请输入正确的手机号");
                    return;
                }
                myCountDownTimer = new CountDownTimer(60000, 1000) {
                    @Override
                    public void onFinish() {
                        countDownNow = false;
                        btn_login_code.setText("获取验证码");
                        enableVerifyCodeButton();

                        cancel();
                    }

                    @Override
                    public void onTick(long millisUntilFinished) {
                        countDownNow = true;
                        String str = millisUntilFinished / 1000 + "S后重试";
                        btn_login_code.setText(str + "");
                        disableVerifyCodeButton();
                    }
                }.start();
                getMobileVerifyCode(userNumber);
                break;

            case R.id.btn_login_login:
                String userCode = et_login_code.getText().toString().trim();
                String userNumber1 = et_login_number.getText().toString().trim();
                if (TextUtils.isEmpty(userNumber1)) {
                    ToastUtils.show(this, "请输入手机号");
                    return;
                }
                if (TextUtils.isEmpty(userCode)) {
                    ToastUtils.show(this, "请输入验证码");
                    return;
                }
                if (userNumber1.length() != 11) {
                    ToastUtils.show(this, "请输入正确的手机号");
                    return;
                }
                Utils.hideSoftInput(mContext);
                login(userCode, userNumber1);
                break;

            case R.id.img_login_wx:
                otherLogin(SHARE_MEDIA.WEIXIN);
                break;

            case R.id.img_login_qq:
                otherLogin(SHARE_MEDIA.QQ);
                break;

            case R.id.img_login_wb:
                otherLogin(SHARE_MEDIA.SINA);
                break;
            case R.id.tv_login_back:
                if(mComeFrom == MainActivity.SHOW_MY_FRAGMENT){
                    MainActivity.open(this, MainActivity.SHOW_HOME_FRAGMENT);
                }
                finish();
                break;
        }
    }

    @Override
    protected String getPageTitle() {
        return null;
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initPageData() {
        showBaseUITitle = false;
        mComeFrom = getIntent().getIntExtra(KEY_OF_COME_FROM, -1);

        et_login_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!countDownNow) {
                    if (s.length() == 11) {
                        enableVerifyCodeButton();
                    } else {
                        disableVerifyCodeButton();
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_login_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 4) {
                    enableLoginButton();
                } else {
                    disableLoginButton();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        loadingAlertDialog = new LoadingAlertDialog(this);
        disableLoginButton();
        disableVerifyCodeButton();
    }

    /**
     * 获取短信验证码
     * @param mobile
     */
    public static void getMobileVerifyCode(String mobile) {
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("mobile", mobile);

        HttpRequest.post(true, HttpRequest.APP_INTERFACE_WEB_URL+RequestURL.GET_VALIDATE_AUTHCODE, paramsMap, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onSuccess(String response) {
            }

            @Override
            public void onFailure(String t) {
            }
        });
    }

    /**
     * 正式登录
     *
     * @param userCode
     * @param userNumber
     */
    private void login(String userCode, String userNumber) {
        disableLoginButton();

        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("code", userCode);
        paramsMap.put("mobile", userNumber);

        HttpRequest.post(false, HttpRequest.APP_INTERFACE_WEB_URL+RequestURL.LOGIN, paramsMap, LoginActivity.this,true, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onSuccess(String response) {
                //避免表单重复提交，只有响应回来或者超时，才启动登陆按钮
                enableLoginButton();
                if(KEY_OF_ENABLE_LOGIN_BUTTON.equals(response)){
                    return;
                }

                Gson gson = new Gson();
                LoginBean loginBean = gson.fromJson(response, LoginBean.class);
                Account.sUserToken = loginBean.credential;
                Account.sUserId = loginBean.userId;
                SPUtils.writeCurrentLoginUserInfo(mContext, loginBean);

                MobclickAgent.onProfileSignIn(BaseApplication.loginBean.userId);//登录用户统计
                loginSuccessBack();
            }

            @Override
            public void onFailure(String t) {
                enableLoginButton();
            }
        });
    }

    private void loginSuccessBack() {
        if(mComeFrom == MainActivity.SHOW_MY_FRAGMENT){
            MainActivity.open(LoginActivity.this, MainActivity.SHOW_MY_FRAGMENT);
        }
        finish();
    }


    /**
     * 第三方登录
     */
    private void otherLogin(SHARE_MEDIA share_media) {
        loadingAlertDialog.show();
        UMShareAPI.get(mContext).getPlatformInfo(mContext, share_media, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
                LogUtil.d("onStart " + "授权开始");
            }

            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, final Map<String, String> map) {
                LogUtil.d("onComplete " + "授权完成");
//                for (Map.Entry<String, String> entry : map.entrySet()) {
//                    LogUtil.printLog(entry.getKey() + "=" + entry.getValue());
//                }

                //sdk是6.4.4的,但是获取值的时候用的是6.2以前的(access_token)才能获取到值,未知原因
                Map<String, String>  loginMap = new HashMap<>();
                loginMap.put("partnerUId", map.get("uid"));
                loginMap.put("nickName", map.get("name"));
                loginMap.put("accessToken", map.get("access_token"));
                loginMap.put("refreshToken", map.get("refresh_token") != null ? map.get("refresh_token") : "");
                loginMap.put("openId", map.get("openid") != null ? map.get("openid") : "");
                loginMap.put("expireTime", map.get("expires_in"));
                loginMap.put("portrait", map.get("iconurl"));
                loginMap.put("sex", map.get("gender"));
                switch (share_media) {
                    case QQ:
                        partnerTypeId = "01";
                        break;
                    case WEIXIN:
                        partnerTypeId = "02";
                        break;
                    case SINA:
                        partnerTypeId = "03";
                        break;
                }
                loginMap.put("partnerTypeId", partnerTypeId);

                HttpRequest.post(false, HttpRequest.APP_INTERFACE_WEB_URL+RequestURL.OTHER_LOGIN, loginMap, new HttpRequest.HttpResponseCallBank() {
                    @Override
                    public void onSuccess(String response) {
                        loadingAlertDialog.dismiss();
                        Gson gson = new Gson();
                        LoginBean loginBean = gson.fromJson(response, LoginBean.class);

                        SPUtils.writeCurrentLoginUserInfo(mContext, loginBean);
                        Account.sUserId = loginBean.userId;
                        Account.sUserToken = loginBean.credential;
                        if (loginBean.mobile == null) {
                                Bundle bundle = new Bundle();
                                bundle.putString("uid", map.get("uid"));
                                bundle.putString("partnerTypeId", partnerTypeId);
                                 Intent intent = new Intent(LoginActivity.this, BoundMobileActivity.class);
                                 intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                                return;
                        }
                        loginSuccessBack();
                    }

                    @Override
                    public void onFailure(String t) {
                        loadingAlertDialog.dismiss();
                    }
                });
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                loadingAlertDialog.dismiss();
                ToastUtils.show(LoginActivity.this, share_media + " 未安装 或 授权失败");
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {
                loadingAlertDialog.dismiss();
                ToastUtils.show(LoginActivity.this, share_media + "   授权取消");
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(mComeFrom == MainActivity.SHOW_MY_FRAGMENT && keyCode == KeyEvent.KEYCODE_BACK){
            MainActivity.open(this, MainActivity.SHOW_HOME_FRAGMENT);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void disableVerifyCodeButton() {
        btn_login_code.setEnabled(false);
        btn_login_code.setTextColor(Color.parseColor(TEXT_COLOR_GRAY));
    }


    private void disableLoginButton() {
        btn_login_login.setEnabled(false);
        btn_login_login.setTextColor(Color.parseColor(TEXT_COLOR_GRAY));
    }

    private void enableLoginButton() {
        btn_login_login.setEnabled(true);
        btn_login_login.setTextColor(Color.WHITE);
    }

    private void enableVerifyCodeButton() {
        btn_login_code.setEnabled(true);
        btn_login_code.setTextColor(Color.WHITE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myCountDownTimer != null){
            myCountDownTimer.cancel();
        }
    }

    public static void open(Context context, int comeFrom) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(KEY_OF_COME_FROM, comeFrom);
        context.startActivity(intent);
    }
}
