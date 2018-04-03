package com.jf.jlfund.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jf.jlfund.R;
import com.jf.jlfund.base.BaseActivity;
import com.jf.jlfund.base.BaseBean;
import com.jf.jlfund.bean.SmsCheckCodeBean;
import com.jf.jlfund.bean.UserInfo;
import com.jf.jlfund.http.NetService;
import com.jf.jlfund.http.ParamMap;
import com.jf.jlfund.inter.OnResponseListener;
import com.jf.jlfund.utils.Aes;
import com.jf.jlfund.utils.LogUtils;
import com.jf.jlfund.utils.MD5;
import com.jf.jlfund.utils.MyCountDownTimer;
import com.jf.jlfund.utils.SPUtil;
import com.jf.jlfund.utils.StringUtil;
import com.jf.jlfund.utils.ToastUtils;
import com.jf.jlfund.utils.UIUtils;
import com.jf.jlfund.weight.dialog.CommonDialogFragment;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.et_register_phone)
    EditText etUserName;
    @BindView(R.id.et_register_checkCode)
    EditText etCheckCode;
    @BindView(R.id.et_register_pwd)
    EditText etPwd;
    @BindView(R.id.iv_icon_register_clear_pwd)
    ImageView ivPwdClear;
    @BindView(R.id.iv_icon_register_eyes)
    ImageView ivEyes;
    @BindView(R.id.tv_register_getCheckCode)
    TextView tvGetCheckCode;
    @BindView(R.id.btn_register)
    TextView tvRegister;

    MyCountDownTimer countDownTimer;

    String phoneNumFromLogin;

    @Override
    protected void init() {
        if (getIntent() != null) {
            phoneNumFromLogin = getIntent().getStringExtra(PARAM_PHONE_NUM_TO_AUTO_SHOW);
        }
        UIUtils.setText(etUserName, phoneNumFromLogin);
        etUserName.setSelection(etUserName.getText().toString().length());
//        long lastTimeStamp = SPUtil.getInstance().getLong(SPUtil.KEY_SMS_CODE_TIMESTAMP_FORGET_REGISTER);
//        long dt = System.currentTimeMillis() - lastTimeStamp;
//        if (lastTimeStamp != -1 && dt < 60 * 1000) {        //距离上次发送不到60s,继续倒计时
//            countDownTimer = new MyCountDownTimer(60 * 1000 - dt, 1000, tvGetCheckCode);
//            countDownTimer.start();
//        } else {
//            countDownTimer = new MyCountDownTimer(tvGetCheckCode);
//        }
        countDownTimer = new MyCountDownTimer(tvGetCheckCode);
        initListener();
    }

    private void initListener() {
//        UIUtils.addSpaceCharFilterAndSpecialCharFilter(etPwd);  //过滤空格和特殊字符
//        UIUtils.addButtonEnabledOnTextWatcher(etUserName, tvRegister);
        etPwd.setKeyListener(UIUtils.getNumberKeyListener(false));
        UIUtils.setViewsVisiiblityOnTextWatcher(etPwd, ivPwdClear, ivEyes);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void doBusiness() {

    }

    @OnClick({R.id.btn_register, R.id.iv_icon_register_eyes, R.id.iv_icon_register_clear_pwd, R.id.tv_register_getCheckCode})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.tv_register_getCheckCode:
                getCheckCode();
                break;
            case R.id.iv_icon_register_clear_pwd:
                etPwd.setText("");
                break;
            case R.id.iv_icon_register_eyes:
                UIUtils.onClickEyesListener(etPwd, ivEyes, false);
                break;
            case R.id.btn_register:
                checkoutRules();
                break;
        }
    }

    private String smsCheckCode = "-1&";

    private void getCheckCode() {
        UIUtils.getSmsCheckCode(etUserName.getText().toString().trim(), "1", new UIUtils.OnGetSmsCheckCodeListener() {
            @Override
            public void onGetSmsCheckCode(BaseBean<SmsCheckCodeBean> result) {
                if (result.isSuccess() && result.getData() != null && !TextUtils.isEmpty(result.getData().getCode())) {
                    ToastUtils.showShort("验证码已发送");
                    if (!TextUtils.isEmpty(result.getData().getCode())) {
                        smsCheckCode = Aes.decryptAES(result.getData().getCode());
                    }
                    LogUtils.e("smsCheckCode: " + smsCheckCode);
                    if (countDownTimer != null) {
//                        SPUtil.getInstance().putLong(SPUtil.KEY_SMS_CODE_TIMESTAMP_FORGET_REGISTER, System.currentTimeMillis());
                        countDownTimer.start();
                    }
                } else {
                    if (result.getResCode().equals(BaseBean.CODE_REGISTER_USER_EXISTS)) {
                        showCommonDialog(result.getResMsg(), "取消", "去登录", null, new CommonDialogFragment.OnRightClickListener() {
                            @Override
                            public void onClickRight() {
                                Intent intent = new Intent();
                                intent.putExtra("phoneNum", etUserName.getText().toString().trim());
                                setResult(RESULT_CANCELED, intent);
                                finish();
                            }
                        });
                    }
                }
            }
        });
    }

    private void checkoutRules() {
        if (TextUtils.isEmpty(etUserName.getText().toString().trim())) {
            ToastUtils.showShort("手机号码不可为空");
            return;
        }
        if (!StringUtil.isPhoneNum(etUserName.getText().toString().trim())) {
            ToastUtils.showShort("请输入正确手机号码");
            return;
        }
        if (TextUtils.isEmpty(etCheckCode.getText().toString().trim())) {
            ToastUtils.showShort("请输入6位验证码");
            return;
        }
        if (!smsCheckCode.equals("-1&") && !TextUtils.equals(etCheckCode.getText().toString(), smsCheckCode)) {
            ToastUtils.showShort("验证码错误");
            return;
        }
        if (TextUtils.isEmpty(etPwd.getText().toString().trim())) {
            ToastUtils.showShort("登录密码不可为空");
            return;
        }
        if (etPwd.getText().toString().trim().length() < 6 || etPwd.getText().toString().trim().length() > 16) {
            ToastUtils.showShort("请输入6-16位数字或字母组合密码");
            return;
        }
        if (!StringUtil.isPasswordValid(etPwd.getText().toString().trim())) {
            ToastUtils.showShort("请输入6-16位数字或字母密码");
            return;
        }
        register();
    }

    private void register() {
        postRequest(new OnResponseListener<UserInfo>() {
            @Override
            public Observable<BaseBean<UserInfo>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                paramMap.put("mobile", Aes.encryptAES(etUserName.getText().toString().trim()));
                paramMap.put("password", MD5.md5(etPwd.getText().toString()));
                paramMap.putLast("verCode", etCheckCode.getText().toString().trim());
                return NetService.getNetService().register(paramMap);
            }

            @Override
            public void onResponse(BaseBean<UserInfo> result) {
                LogUtils.e("result: " + result);
                SPUtil.getInstance().putUserInfo(result.getData());
                if (result.getResCode().equals("0000")) {
                    showCommonDialog("是否前往设置手势密码？", "取消", "去设置", new CommonDialogFragment.OnLeftClickListener() {
                        @Override
                        public void onClickLeft() {
                            setResult(RESULT_OK);
                            finish();
                        }
                    }, new CommonDialogFragment.OnRightClickListener() {
                        @Override
                        public void onClickRight() {
                            GestureVerifyActivity.open(RegisterActivity.this, GestureVerifyActivity.DRAW_GESTURE_PASSWORD);
                        }
                    });
                } else if (result.getResCode().equals(BaseBean.CODE_REGISTER_USER_EXISTS)) {
                    showCommonDialog(result.getResMsg(), "取消", "去登陆", null, new CommonDialogFragment.OnRightClickListener() {
                        @Override
                        public void onClickRight() {
//                            Intent intent = new Intent();
//                            intent.putExtra("phoneNum", etUserName.getText().toString().trim());
//                            setResult(RESULT_CANCELED, intent);
//                            setResult(RESULT_CANCELED);
                            finish();
                        }
                    });
                }
            }

            @Override
            public void onError(String errorMsg) {

            }
        }, true, false);
    }

    public static String PARAM_PHONE_NUM_TO_AUTO_SHOW = "phone_number";

    public static void open(Activity activity, String numberFromLogin) {
        Intent intent = new Intent(activity, RegisterActivity.class);
        intent.putExtra(PARAM_PHONE_NUM_TO_AUTO_SHOW, numberFromLogin);
        activity.startActivityForResult(intent, LoginActivity.REQUEST_CODE_REGISTER);
    }
}
