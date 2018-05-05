package com.test.bank.view.activity;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.base.BaseActivity;
import com.test.bank.base.BaseBean;
import com.test.bank.bean.UserInfo;
import com.test.bank.http.NetService;
import com.test.bank.http.ParamMap;
import com.test.bank.inter.OnResponseListener;
import com.test.bank.utils.Aes;
import com.test.bank.utils.LogUtils;
import com.test.bank.utils.MD5;
import com.test.bank.utils.NetUtil;
import com.test.bank.utils.SPUtil;
import com.test.bank.utils.StringUtil;
import com.test.bank.utils.ToastUtils;
import com.test.bank.utils.UIUtils;
import com.test.bank.weight.CommonTitleBar;
import com.test.bank.weight.dialog.CommonDialogFragment;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;

import static com.test.bank.base.BaseBean.CODE_LOGIN_TRY_TIME_OUT_LOAD;

public class LoginActivity extends BaseActivity {
    @BindView(R.id.commonTitleBar_login)
    CommonTitleBar commonTitleBar;
    @BindView(R.id.et_login_phone)
    EditText etUserName;
    @BindView(R.id.et_login_pwd)
    EditText etPassword;
    @BindView(R.id.iv_login_username_clear)
    ImageView ivUserNameClear;
    @BindView(R.id.iv_icon_login_clear_pwd)
    ImageView ivPwdClear;
    @BindView(R.id.iv_login_face)
    ImageView ivFace;
    @BindView(R.id.tv_login)
    TextView tvLogin;

    private String phoneNum;

    private boolean isGoMainAfterLogin = false;
    public String classToGoAfterLogin = "";
    public boolean isFinishAfterLogin = true;

    @Override
    protected void init() {
        if (getIntent() != null) {
            phoneNum = getIntent().getStringExtra(PARAM_PHONE_NUM);
            classToGoAfterLogin = getIntent().getStringExtra(KEY_ACTIVIYT_TO_GO_AFTER_LOGIN);
            isFinishAfterLogin = getIntent().getBooleanExtra(KEY_IS_FINISH_AFTER_LOGIN, true);
            LogUtils.e("classToGoAfterLogin: " + classToGoAfterLogin + " ,isFinishAfterLogin: " + isFinishAfterLogin);
        }

        if (!TextUtils.isEmpty(phoneNum)) {
            if (KEY_TOKEN_INVALID_GO_MAIN.equals(phoneNum)) {
                isGoMainAfterLogin = true;
            } else if (phoneNum.startsWith("1")) {
                UIUtils.setText(etUserName, phoneNum);
                etPassword.setFocusable(true);
                etPassword.requestFocus();
            }
        }
        initListener();
        refreshBtnStatus();
    }

    private void initListener() {
        commonTitleBar.setOnLeftClickListener(new CommonTitleBar.OnLeftClickListener() {
            @Override
            public void onClickLeft() {
                if (isGoMainAfterLogin) {
                    MainActivity.open(LoginActivity.this, 1);
                } else {
                    finish();
                }
            }
        });
        etUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                refreshBtnStatus();
                if (ivUserNameClear != null) {
                    ivUserNameClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                refreshBtnStatus();
                if (ivPwdClear != null) {
                    ivPwdClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etPassword.setKeyListener(UIUtils.getNumberKeyListener(true));
        etUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ivUserNameClear.setVisibility(hasFocus && !TextUtils.isEmpty(etUserName.getText().toString().trim()) ? View.VISIBLE : View.GONE);
            }
        });
        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ivPwdClear.setVisibility(hasFocus && !TextUtils.isEmpty(etPassword.getText().toString().trim()) ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void refreshBtnStatus() {
        if (!TextUtils.isEmpty(etUserName.getText().toString().trim()) && !TextUtils.isEmpty(etPassword.getText().toString().trim())) {
            tvLogin.setEnabled(true);
        } else {
            tvLogin.setEnabled(false);
        }
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void doBusiness() {


    }


    @OnClick({R.id.tv_login, R.id.tv_login_register, R.id.tv_login_forgetPwd, R.id.iv_login_username_clear, R.id.iv_icon_login_clear_pwd})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_login_username_clear:
                etUserName.setText("");
                break;
            case R.id.iv_icon_login_clear_pwd:
                etPassword.setText("");
                break;
            case R.id.tv_login:
                checkoutRules();
                break;
            case R.id.tv_login_register:
                String num = etUserName.getText().toString().trim();
                if (StringUtil.isPhoneNum(num)) {
                    RegisterActivity.open(this, num);
                } else {
                    RegisterActivity.open(this, "");
                }
                break;
            case R.id.tv_login_forgetPwd:
                String phoneNum = etUserName.getText().toString().trim();
                if (StringUtil.isPhoneNum(phoneNum)) {
                    ForgetLoginPwdActivity.open(this, phoneNum);
                } else {
                    ForgetLoginPwdActivity.open(this, "");
                }
                break;
        }
    }

    private void checkoutRules() {
        if (TextUtils.isEmpty(etUserName.getText().toString().trim())) {
            ToastUtils.showShort("手机号码不可为空");
            return;
        }
        if (!StringUtil.isPhoneNum(etUserName.getText().toString().trim())) {
            ToastUtils.showShort("请正确输入手机号码");
            return;
        }
        String passwd = etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(passwd)) {
            ToastUtils.showShort("登录密码不可为空");
            return;
        }
        if (passwd.length() < 6 || passwd.length() > 16 || !StringUtil.isPasswordValid(passwd)) {
            ToastUtils.showShort("请输入6-16位登录密码");
            return;
        }
        login();
    }


    private void login() {
        postRequest(new OnResponseListener<UserInfo>() {
            @Override
            public Observable<BaseBean<UserInfo>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                paramMap.put("mobile", Aes.encryptAES(etUserName.getText().toString().trim()));
                paramMap.putLast("password", MD5.md5(etPassword.getText().toString()));
                return NetService.getNetService().login(paramMap);
            }

            @Override
            public void onResponse(BaseBean<UserInfo> result) {
                if (result.isSuccess()) {   //
                    if (!TextUtils.isEmpty(result.getData().getToken())) {
                        NetUtil.setJPushAliasAndTags(MD5.md5(result.getData().getToken()).toLowerCase());
                    }

                    UserInfo userInfo = result.getData();
                    userInfo = SPUtil.readUserConfigInfo(LoginActivity.this, userInfo);
                    SPUtil.getInstance().putUserInfo(userInfo);
                    if (isGoMainAfterLogin) {
                        MainActivity.open(LoginActivity.this, 1);
                    } else if (!TextUtils.isEmpty(classToGoAfterLogin)) {
                        try {
                            MobclickAgent.onProfileSignIn(etUserName.getText().toString().trim());
                            Class classToGo = Class.forName(classToGoAfterLogin);
                            Intent intent = new Intent(LoginActivity.this, classToGo);
                            startActivity(intent);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                            LogUtils.e("登录成功跳转异常--> " + e.getMessage());
                            MainActivity.open(LoginActivity.this, 1);
                        }
                        if (isFinishAfterLogin)
                            finish();
                    } else {
                        finish();
                    }
                } else {    //用户名密码错误或者用户不存在弹一个框
                    if (result.getResCode().equals(CODE_LOGIN_TRY_TIME_OUT_LOAD)) {  //错误超过5次
                        showCommonDialog(result.getResMsg(), "取消", "忘记密码", null, new CommonDialogFragment.OnRightClickListener() {
                            @Override
                            public void onClickRight() {
                                ForgetLoginPwdActivity.open(LoginActivity.this, TextUtils.isEmpty(etUserName.getText().toString().trim()) ? "" : etUserName.getText().toString().trim());
                            }
                        });
                    }
                }
            }

            @Override
            public void onError(String errorMsg) {
            }
        }, true, false);
    }


    public static final int REQUEST_CODE_REGISTER = 11;
    public static final int REQUEST_CODE_FORGET_PWD = 12;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.e("requestCode: " + requestCode + " resultCode: " + resultCode);
        if (requestCode == REQUEST_CODE_REGISTER) {
            if (resultCode == RESULT_OK) {
                finish();
            } else if (resultCode == RESULT_CANCELED) {
                if (data != null) {
                    String phoneNum = data.getStringExtra("phoneNum");
                    if (!TextUtils.isEmpty(phoneNum)) {
                        etUserName.setText(phoneNum);
                        etUserName.setSelection(phoneNum.length());
                    }
                }
            }
        } else if (requestCode == REQUEST_CODE_FORGET_PWD) {
            if (resultCode == RESULT_OK) {
                etPassword.setText("");
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (isGoMainAfterLogin) {
                MainActivity.open(this, 1);
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public static void open(Context context) {
        open(context, "");
    }

    public static String PARAM_PHONE_NUM = "PARAM_PHONE_NUM";

    public static String KEY_TOKEN_INVALID_GO_MAIN = "token_invalid_go_main";

    public static void open(Context context, String phoneNum) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(PARAM_PHONE_NUM, phoneNum);
        context.startActivity(intent);
    }

    public static String KEY_ACTIVIYT_TO_GO_AFTER_LOGIN = "activityToGoAfterLogin";
    public static String KEY_IS_FINISH_AFTER_LOGIN = "isFinsishAfterLogin";

    public static void open(Context context, Class classToGoAfterLogin, boolean isFinishAfterLogin) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(KEY_ACTIVIYT_TO_GO_AFTER_LOGIN, classToGoAfterLogin.getCanonicalName());
        intent.putExtra(KEY_IS_FINISH_AFTER_LOGIN, isFinishAfterLogin);
        context.startActivity(intent);
    }

}
