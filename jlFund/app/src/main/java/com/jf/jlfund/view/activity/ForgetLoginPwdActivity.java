package com.jf.jlfund.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jf.jlfund.R;
import com.jf.jlfund.base.BaseActivity;
import com.jf.jlfund.base.BaseBean;
import com.jf.jlfund.bean.ResetLoginPwdBean;
import com.jf.jlfund.bean.SmsCheckCodeBean;
import com.jf.jlfund.http.NetService;
import com.jf.jlfund.http.ParamMap;
import com.jf.jlfund.inter.OnResponseListener;
import com.jf.jlfund.utils.Aes;
import com.jf.jlfund.utils.LogUtils;
import com.jf.jlfund.utils.MD5;
import com.jf.jlfund.utils.MyCountDownTimer;
import com.jf.jlfund.utils.StringUtil;
import com.jf.jlfund.utils.ToastUtils;
import com.jf.jlfund.utils.UIUtils;
import com.jf.jlfund.weight.dialog.CommonDialogFragment;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;

public class ForgetLoginPwdActivity extends BaseActivity {

    @BindView(R.id.et_forget_login_pwd_phone)
    EditText etUserName;
    @BindView(R.id.et_forget_login_pwd_checkCode)
    EditText etCheckCode;
    @BindView(R.id.et_forget_login_pwd)
    EditText etPwd;
    @BindView(R.id.iv_forget_login_clear_pwd)
    ImageView ivPwdClear;
    @BindView(R.id.iv_forget_login_eyes)
    ImageView ivEyes;
    @BindView(R.id.tv_forget_login_pwd_getCheckCode)
    TextView tvGetCheckCode;
    @BindView(R.id.ll_clear_eyes)
    LinearLayout llClearEyes;

    MyCountDownTimer countDownTimer;

    String phoneNum;

    @Override
    protected void init() {
        if (getIntent() != null) {
            phoneNum = getIntent().getStringExtra(PARAM_PHONE_NUM);
        }
        if (!TextUtils.isEmpty(phoneNum)) {
            etUserName.setText(phoneNum);
            etUserName.setSelection(etUserName.getText().length());
        }
//        long lastTimeStamp = SPUtil.getInstance().getLong(SPUtil.KEY_SMS_CODE_TIMESTAMP_FORGET_LOGIN_PWD);
//        long dt = System.currentTimeMillis() - lastTimeStamp;
//        if (lastTimeStamp != -1 && dt < 60 * 1000) {
//            countDownTimer = new MyCountDownTimer(60 * 1000 - dt, 1000, tvGetCheckCode);
//            countDownTimer.start();
//        } else {
//            countDownTimer = new MyCountDownTimer(tvGetCheckCode);
//        }
        countDownTimer = new MyCountDownTimer(tvGetCheckCode);
        initListener();
    }

    private void initListener() {
        UIUtils.setViewsVisiiblityOnTextWatcher(etPwd, llClearEyes);
        etPwd.setKeyListener(UIUtils.getNumberKeyListener(false));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_forget_login_pwd;
    }

    @Override
    protected void doBusiness() {

    }

    @OnClick({R.id.btn_get_login_pwd, R.id.iv_forget_login_eyes, R.id.iv_forget_login_clear_pwd, R.id.tv_forget_login_pwd_getCheckCode})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.tv_forget_login_pwd_getCheckCode:
                getCheckCode();
                break;
            case R.id.iv_forget_login_clear_pwd:
                etPwd.setText("");
                break;
            case R.id.iv_forget_login_eyes:
                UIUtils.onClickEyesListener(etPwd, ivEyes, false);
                break;
            case R.id.btn_get_login_pwd:
                checkoutRules();
                break;
        }
    }

    private String smsCheckCode = "-1&";

    private void getCheckCode() {
        UIUtils.getSmsCheckCode(etUserName.getText().toString().trim(), "2", new UIUtils.OnGetSmsCheckCodeListener() {
            @Override
            public void onGetSmsCheckCode(BaseBean<SmsCheckCodeBean> result) {
                if (result.isSuccess() && result.getData() != null && !TextUtils.isEmpty(result.getData().getCode())) {
                    ToastUtils.showShort("验证码已发送");
                    smsCheckCode = Aes.decryptAES(result.getData().getCode());
                    LogUtils.e("smsCheckCode: " + smsCheckCode);
                    if (countDownTimer != null) {
//                        SPUtil.getInstance().putLong(SPUtil.KEY_SMS_CODE_TIMESTAMP_FORGET_LOGIN_PWD, System.currentTimeMillis());
                        countDownTimer.start();
                    }
                } else {
                }
            }
        });
    }

    private void checkoutRules() {
        if (TextUtils.isEmpty(etUserName.getText().toString())) {
            ToastUtils.showShort("手机号码不可为空");
            return;
        }
        if (!StringUtil.isPhoneNum(etUserName.getText().toString().trim())) {
            ToastUtils.showShort("请输入正确的手机号");
            return;
        }
        if (TextUtils.isEmpty(etCheckCode.getText().toString())) {
            ToastUtils.showShort("请输入6位验证码");
            return;
        }
        if (!smsCheckCode.equals("-1&") && !TextUtils.equals(etCheckCode.getText().toString(), smsCheckCode)) { //退出页面再次进入验证码只要在有效期内依然有效
            ToastUtils.showShort("验证码错误");
            return;
        }
        if (TextUtils.isEmpty(etPwd.getText().toString())) {
            ToastUtils.showShort("登录密码不可为空");
            return;
        }
        if (etPwd.getText().toString().trim().length() < 6 || etPwd.getText().toString().trim().length() > 16) {
            ToastUtils.showShort("请输入6-16位数字或字母组合密码");
            return;
        }
        if (!StringUtil.isPasswordValid(etPwd.getText().toString().trim())) {
            ToastUtils.showShort("请输入6-16位数字或字母组合密码");
            return;
        }
        resetLoginPwd();
    }

    private void resetLoginPwd() {
        postRequest(new OnResponseListener<ResetLoginPwdBean>() {
            @Override
            public Observable<BaseBean<ResetLoginPwdBean>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                paramMap.put("mobile", Aes.encryptAES(etUserName.getText().toString().trim()));
                paramMap.put("password", MD5.md5(etPwd.getText().toString()));
                paramMap.putLast("verCode", etCheckCode.getText().toString().trim());
                return NetService.getNetService().forgetLoginPwd(paramMap);
            }

            @Override
            public void onResponse(BaseBean<ResetLoginPwdBean> result) {
                LogUtils.e("result: " + result);        //{"resCode":"0000","resMsg":"获取成功","data":{"state":true},"msgStatus":false}
                if (result.getResCode().equals("0000")) {
                    showOneBtnDialog("密码重置成功，请重新登录", "确定", new CommonDialogFragment.OnLeftClickListener() {
                        @Override
                        public void onClickLeft() {
//                            ToastUtils.showShort("密码已重置，请重新登录");
                            setResult(RESULT_OK);   //忘记密码成功之后需要清除登录页面的旧密码
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

    public static String PARAM_PHONE_NUM = "phone_number";

    public static void open(Context context, String phoneNum) {
        Intent intent = new Intent(context, ForgetLoginPwdActivity.class);
        intent.putExtra(PARAM_PHONE_NUM, phoneNum);
        ((Activity) context).startActivityForResult(intent, LoginActivity.REQUEST_CODE_FORGET_PWD);
    }
}
