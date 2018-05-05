package com.test.bank.view.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.base.BaseActivity;
import com.test.bank.base.BaseBean;
import com.test.bank.bean.ResetLoginPwdBean;
import com.test.bank.bean.SmsCheckCodeBean;
import com.test.bank.http.NetService;
import com.test.bank.http.ParamMap;
import com.test.bank.inter.OnResponseListener;
import com.test.bank.utils.Aes;
import com.test.bank.utils.LogUtils;
import com.test.bank.utils.MD5;
import com.test.bank.utils.MyCountDownTimer;
import com.test.bank.utils.SPUtil;
import com.test.bank.utils.StringUtil;
import com.test.bank.utils.ToastUtils;
import com.test.bank.utils.UIUtils;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;

public class ResetLoginPwdActivity extends BaseActivity {

    public static String smsCode = "";  //静态变量存储最新获取的验证码，为了解决用户获取之后再次进入无法通过本地校验

    @BindView(R.id.et_resetLoginPwd_checkCode)
    EditText etCheckCode;
    @BindView(R.id.tv_resetLoginPwd_getCheckCode)
    TextView tvGetCheckCode;
    @BindView(R.id.et_resetLoginPwd_pwd)
    EditText etPwd;

    @BindView(R.id.iv_icon_resetLoginPwd_clearCheckCode)
    ImageView ivClearCheckCode;
    @BindView(R.id.iv_reset_login_clear_pwd)
    ImageView ivPwdClear;
    @BindView(R.id.iv_reset_login_eyes)
    ImageView ivEyes;
    @BindView(R.id.tv_resetLoginPwd_top_hint)
    TextView tvTopHint;

    MyCountDownTimer countDownTimer;

    String mobile;

    @Override
    protected void init() {
        mobile = SPUtil.getInstance().getMobile();
        tvTopHint.setText("已发送短信验证码至" + StringUtil.hidePhoneNumber(Aes.decryptAES(mobile)) + "，请在框内填写验证码，若未收到请点击重新发送");
        long lastTimeStamp = SPUtil.getInstance().getLong(SPUtil.KEY_SMS_CODE_TIMESTAMP_FORGET_RESET_LOGIN_PWD);
        long dt = System.currentTimeMillis() - lastTimeStamp;
        if (lastTimeStamp != -1 && dt < 60 * 1000) {        //距离上次点击获取验证码不足60s，即倒计时为结束，则继续上次剩余倒计时，并且不再发送验证码
            countDownTimer = new MyCountDownTimer(60 * 1000 - dt, 1000, tvGetCheckCode);
            countDownTimer.start();
        } else {        //初次进入或者距上次点击获取验证码倒计时60s已跑完，则重新倒计时 并发送验证码
            countDownTimer = new MyCountDownTimer(tvGetCheckCode);
            getCheckCode();
        }

        initListener();
    }


    private void initListener() {
        UIUtils.setViewsVisiiblityOnTextWatcher(etPwd, ivPwdClear, ivEyes);
        UIUtils.setViewsVisiiblityOnTextWatcher(etCheckCode, ivClearCheckCode);
        etPwd.setKeyListener(UIUtils.getNumberKeyListener(false));
    }

    private void getCheckCode() {
        UIUtils.getSmsCheckCode(Aes.decryptAES(mobile), "2", new UIUtils.OnGetSmsCheckCodeListener() {
            @Override
            public void onGetSmsCheckCode(BaseBean<SmsCheckCodeBean> result) {
                if (result.isSuccess() && result.getData() != null && !TextUtils.isEmpty(result.getData().getCode())) {
                    smsCode = Aes.decryptAES(result.getData().getCode());   //静态变量记住当前最新验证码
                    ToastUtils.showShort("验证码已发送");
                    LogUtils.e("smsCheckCode: " + smsCode);
                    if (countDownTimer != null) {
                        SPUtil.getInstance().putLong(SPUtil.KEY_SMS_CODE_TIMESTAMP_FORGET_RESET_LOGIN_PWD, System.currentTimeMillis());
                        countDownTimer.start();
                    }
                } else {
                    if (result.getResCode().equals(BaseBean.CODE_REGISTER_USER_EXISTS)) {

                    }
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_reset_login_pwd;
    }

    @Override
    protected void doBusiness() {

    }

    @OnClick({R.id.btn_resetLoginPwd, R.id.iv_reset_login_eyes, R.id.iv_reset_login_clear_pwd, R.id.tv_resetLoginPwd_getCheckCode, R.id.iv_icon_resetLoginPwd_clearCheckCode})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.tv_resetLoginPwd_getCheckCode:
                countDownTimer = new MyCountDownTimer(tvGetCheckCode);  //点击获取验证码则重新初始化倒计时类，因为如果是倒计时未结束退出再进入，countDownTimer再次开始倒计时就可能不是从60开始
                getCheckCode();
                break;
            case R.id.iv_reset_login_clear_pwd:
                etPwd.setText("");
                break;
            case R.id.iv_icon_resetLoginPwd_clearCheckCode:
                etCheckCode.setText("");
                break;
            case R.id.iv_reset_login_eyes:
                UIUtils.onClickEyesListener(etPwd, ivEyes, false);
                break;
            case R.id.btn_resetLoginPwd:
                checkoutRules();
                break;
        }
    }

    private void checkoutRules() {

        if (TextUtils.isEmpty(etCheckCode.getText().toString())) {
            ToastUtils.showShort("请输入6位验证码");
            return;
        }
        if (!TextUtils.equals(etCheckCode.getText().toString().trim(), smsCode)) {
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
                paramMap.put("mobile", mobile);
                paramMap.put("password", MD5.md5(etPwd.getText().toString()));
                paramMap.put("verCode", etCheckCode.getText().toString().trim());
                paramMap.putLast("token", SPUtil.getInstance().getToken());
                return NetService.getNetService().resetLoginPwd(paramMap);
            }

            @Override
            public void onResponse(BaseBean<ResetLoginPwdBean> result) {
                LogUtils.e("result: " + result);        //{"resCode":"0000","resMsg":"获取成功","data":{"state":true},"msgStatus":false}
                if (result.getResCode().equals("0000")) {
                    SPUtil.getInstance().putLong(SPUtil.KEY_SMS_CODE_TIMESTAMP_FORGET_RESET_LOGIN_PWD, -1);     //重置倒计时时间戳
                    ToastUtils.showShort("操作成功");
                    finish();
                } else {
//                    countDownTimer.onFinish();
                }
            }

            @Override
            public void onError(String errorMsg) {
                ToastUtils.showShort(errorMsg);
            }
        }, true, false);
    }


    public static void open(Context context) {
        context.startActivity(new Intent(context, ResetLoginPwdActivity.class));
    }
}
