package com.caishi.chaoge.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.caishi.chaoge.R;
import com.caishi.chaoge.base.BaseApplication;
import com.caishi.chaoge.base.BaseUILocalDataActivity;
import com.caishi.chaoge.bean.MineDataBean;
import com.caishi.chaoge.http.Account;
import com.caishi.chaoge.http.HttpRequest;
import com.caishi.chaoge.http.RequestURL;
import com.caishi.chaoge.utils.SPUtils;
import com.caishi.chaoge.utils.ToastUtils;
import com.caishi.chaoge.utils.Utils;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class BoundMobileActivity extends BaseUILocalDataActivity {

    @BindView(R.id.tv_login_back)
    TextView tv_login_back;
    @BindView(R.id.et_boundMobile_number)
    EditText et_boundMobile_number;
    @BindView(R.id.et_boundMobile_code)
    EditText et_boundMobile_code;
    @BindView(R.id.btn_boundMobile_code)
    Button btn_boundMobile_code;
    @BindView(R.id.btn_boundMobile_login)
    Button btn_boundMobile_login;

    private String partnerTypeId;
    private String partnerUId;
    private boolean countDownNow = false;
    private CountDownTimer mCountDownTimer;

    @OnClick({R.id.btn_boundMobile_code, R.id.btn_boundMobile_login, R.id.tv_login_back})
    public void OnClickView(View v) {
        switch (v.getId()) {
            case R.id.tv_login_back:
                finish();
                break;
            case R.id.btn_boundMobile_login:
                String userCode = et_boundMobile_code.getText().toString().trim();
                String userNumber1 = et_boundMobile_number.getText().toString().trim();
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

                bindMobile(userCode, userNumber1);
                break;
            case R.id.btn_boundMobile_code:
                Utils.hideSoftInput(mContext);
                String userNumber = et_boundMobile_number.getText().toString().trim();
                if (TextUtils.isEmpty(userNumber)) {
                    ToastUtils.show(this, "请输入手机号");
                    return;
                }
                if (userNumber.length() != 11) {
                    ToastUtils.show(this, "请输入正确的手机号");
                    return;
                }

                mCountDownTimer = new CountDownTimer(60000, 1000) {
                    @Override
                    public void onFinish() {
                        countDownNow = false;
                        btn_boundMobile_code.setEnabled(true);
                        btn_boundMobile_code.setText("获取验证码");
                        btn_boundMobile_code.setTextColor(Color.WHITE);

                        cancel();
                    }

                    @Override
                    public void onTick(long millisUntilFinished) {
                        countDownNow = true;
                        String str = millisUntilFinished / 1000 + "S后重试";
                        btn_boundMobile_code.setText(str);
                        btn_boundMobile_code.setEnabled(false);
                        btn_boundMobile_code.setTextColor(Color.parseColor(LoginActivity.TEXT_COLOR_GRAY));
                    }
                }.start();
                LoginActivity.getMobileVerifyCode(userNumber);
                break;
        }
    }

    @Override
    protected String getPageTitle() {
        return null;
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_bound_mobile;
    }

    @Override
    protected void initPageData() {
        showBaseUITitle = false;
        partnerTypeId = (String) getIntent().getExtras().get("partnerTypeId");
        partnerUId = (String) getIntent().getExtras().get("uid");

        et_boundMobile_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 4) {
                    enableBindButton();
                } else {
                    disableBindButton();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        disableBindButton();
    }


    public void bindMobile(String userCode, String userNumber1) {
        disableBindButton();

        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("code",userCode);
        paramsMap.put("mobile",userNumber1);
        paramsMap.put("partnerUId",partnerUId);
        paramsMap.put("partnerTypeId",partnerTypeId);

        HttpRequest.post(false, HttpRequest.APP_INTERFACE_WEB_URL+RequestURL.BOUND_MOBILD, paramsMap,BoundMobileActivity.this, true, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onSuccess(String response) {
                //避免表单重复提交，只有响应回来或者超时，才启动登陆按钮
                enableBindButton();
                if(LoginActivity.KEY_OF_ENABLE_LOGIN_BUTTON.equals(response)){
                    return;
                }

                Gson gson = new Gson();
                MineDataBean mineDataBean = gson.fromJson(response, MineDataBean.class);
                BaseApplication.loginBean.userId = mineDataBean.userId;
                BaseApplication.loginBean.mobile = mineDataBean.mobile;
                BaseApplication.loginBean.credential = mineDataBean.credential;
                BaseApplication.loginBean.nickname = mineDataBean.nickname;
                BaseApplication.loginBean.avatar = mineDataBean.avatar;
                SPUtils.writeCurrentLoginUserInfo(mContext,BaseApplication.loginBean);
                SPUtils.writeThirdAccountBind(mContext, mineDataBean);
                Account.sUserId = mineDataBean.userId;
                Account.sUserToken = mineDataBean.credential;
                MobclickAgent.onProfileSignIn(partnerUId, mineDataBean.userId);//第三方登录用户统计

                finish();
            }

            @Override
            public void onFailure(String t) {
                enableBindButton();
            }
        });
    }


    private void disableBindButton() {
        btn_boundMobile_login.setEnabled(false);
        btn_boundMobile_login.setTextColor(Color.parseColor(LoginActivity.TEXT_COLOR_GRAY));
    }

    private void enableBindButton() {
        btn_boundMobile_login.setEnabled(true);
        btn_boundMobile_login.setTextColor(Color.WHITE);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null){
            mCountDownTimer.cancel();
        }
    }
}
