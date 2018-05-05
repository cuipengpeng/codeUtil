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
import com.test.bank.bean.SmsCheckCodeBean;
import com.test.bank.http.NetService;
import com.test.bank.http.ParamMap;
import com.test.bank.inter.OnResponseListener;
import com.test.bank.utils.Aes;
import com.test.bank.utils.LogUtils;
import com.test.bank.utils.MyCountDownTimer;
import com.test.bank.utils.SPUtil;
import com.test.bank.utils.StringUtil;
import com.test.bank.utils.ToastUtils;
import com.test.bank.utils.UIUtils;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;

public class ResetTradePwdActivity extends BaseActivity {

    @BindView(R.id.et_reset_trade_pwd_idcard)
    EditText etIDCard;
    @BindView(R.id.et_reset_trade_pwd_phone)
    EditText etPhoneNum;
    @BindView(R.id.et_reset_trade_pwd_checkCode)
    EditText etCheckCode;
    @BindView(R.id.et_reset_trade_pwd)
    EditText etPwd;
    @BindView(R.id.tv_reset_trade_pwd_getCheckCode)
    TextView tvGetCheckCode;
    @BindView(R.id.btn_reset_trade_pwd)
    TextView tvFinish;


    @BindView(R.id.iv_icon_resetTradePwd_clearIDCard)
    ImageView ivClearIDCard;
    @BindView(R.id.iv_icon_resetTradePwd_clearPhone)
    ImageView ivClearPhone;
    @BindView(R.id.iv_icon_resetTradePwd_clearCheckCode)
    ImageView ivClearCheckCode;
    @BindView(R.id.iv_reset_trade_pwd_clear_pwd)
    ImageView ivClearPwd;
    @BindView(R.id.iv_reset_trade_pwd_eyes)
    ImageView ivEyes;

    MyCountDownTimer countDownTimer;

    @Override
    protected void init() {
//        long lastTimeStamp = SPUtil.getInstance().getLong(SPUtil.KEY_SMS_CODE_TIMESTAMP_FORGET_RESET_TRADE_PWD);
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
        etIDCard.setKeyListener(UIUtils.getNumberKeyListener(false));
        etPwd.setKeyListener(UIUtils.getNumberKeyListener(false, true));
        UIUtils.setViewsVisiiblityOnTextWatcher(etIDCard, ivClearIDCard);
        UIUtils.setViewsVisiiblityOnTextWatcher(etPhoneNum, ivClearPhone);
        UIUtils.setViewsVisiiblityOnTextWatcher(etCheckCode, ivClearCheckCode);
        UIUtils.setViewsVisiiblityOnTextWatcher(etPwd, ivClearPwd, ivEyes);
        tvFinish.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                LogUtils.d("onLongClick.........");
                return true;
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_reset_trade_pwd;
    }

    @Override
    protected void doBusiness() {
    }


    @OnClick({R.id.tv_reset_trade_pwd_getCheckCode, R.id.iv_icon_resetTradePwd_clearIDCard, R.id.iv_icon_resetTradePwd_clearPhone, R.id.iv_icon_resetTradePwd_clearCheckCode, R.id.iv_reset_trade_pwd_clear_pwd, R.id.iv_reset_trade_pwd_eyes, R.id.btn_reset_trade_pwd})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_reset_trade_pwd_getCheckCode:
                getCheckCode();
                break;
            case R.id.btn_reset_trade_pwd:
                checkRules();
                break;
            case R.id.iv_icon_resetTradePwd_clearIDCard:
                etIDCard.setText("");
                break;
            case R.id.iv_icon_resetTradePwd_clearPhone:
                etPhoneNum.setText("");
                break;
            case R.id.iv_icon_resetTradePwd_clearCheckCode:
                etCheckCode.setText("");
                break;
            case R.id.iv_reset_trade_pwd_clear_pwd:
                etPwd.setText("");
                break;
            case R.id.iv_reset_trade_pwd_eyes:
                UIUtils.onClickEyesListener(etPwd, ivEyes, false);
                break;

        }
    }

    private void checkRules() {
        if (TextUtils.isEmpty(etIDCard.getText().toString().trim())) {
            ToastUtils.showShort("身份证号码不能为空");
            return;
        }
        if (!(etIDCard.getText().toString().trim().length() == 15 || etIDCard.getText().toString().trim().length() == 18)) {
            ToastUtils.showShort("身份证号码不匹配");
            return;
        }
        if (TextUtils.isEmpty(etPhoneNum.getText().toString().trim())) {
            ToastUtils.showShort("手机号码不能为空");
            return;
        }
        if (!StringUtil.isPhoneNum(etPhoneNum.getText().toString().trim())) {
            ToastUtils.showShort("请输入正确的手机号码");
            return;
        }
        if (!etPhoneNum.getText().toString().trim().equals(Aes.decryptAES(SPUtil.getInstance().getMobile()))) {
            ToastUtils.showShort("请输入登录手机号码");
            return;
        }
        if (TextUtils.isEmpty(etCheckCode.getText().toString().trim())) {
            ToastUtils.showShort("验证码不能为空");
            return;
        }
        if (TextUtils.isEmpty(smsCheckCode) || !smsCheckCode.equals(etCheckCode.getText().toString().trim())) {
            ToastUtils.showShort("验证码错误");
            return;
        }
        if (TextUtils.isEmpty(etPwd.getText().toString().trim())) {
            ToastUtils.showShort("交易密码不能为空");
            return;
        }
        if (etPwd.getText().toString().trim().length() < 6) {
            ToastUtils.showShort("密码应为6位数字");
            return;
        }
        if (StringUtil.isWeakPassword(etPwd.getText().toString().trim())) {
            showOneBtnDialog("您设置的密码过于简单，请重新输入", "确定");
            etPwd.setText("");
            return;
        }
        resetTradePwd();
    }

    private void resetTradePwd() {
        postRequest(new OnResponseListener<Boolean>() {
            @Override
            public Observable<BaseBean<Boolean>> createObservalbe() {
                ParamMap paramMap = new ParamMap();
                paramMap.put("token", SPUtil.getInstance().getToken());
                paramMap.put("pwd", Aes.encryptAES(etPwd.getText().toString().trim()));
                paramMap.put("certificateno", Aes.encryptAES(etIDCard.getText().toString().trim()));
                paramMap.putLast("verCode", Aes.encryptAES(etCheckCode.getText().toString().trim()));
                return NetService.getNetService().resetTradePwd(paramMap);
            }

            @Override
            public void onResponse(BaseBean<Boolean> result) {
                if (result.isSuccess()) {
//                    ToastUtils.showShort("操作成功");
                    finish();
                }
            }

            @Override
            public void onError(String errorMsg) {
                ToastUtils.showShort(errorMsg);
            }
        });
    }

    String smsCheckCode;

    private void getCheckCode() {
        if (TextUtils.isEmpty(etPhoneNum.getText().toString().trim())) {
            ToastUtils.showShort("手机号码不能为空");
            return;
        }
        if (!StringUtil.isPhoneNum(etPhoneNum.getText().toString().trim())) {
            ToastUtils.showShort("请输入正确的手机号码");
            return;
        }
        if (!etPhoneNum.getText().toString().trim().equals(Aes.decryptAES(SPUtil.getInstance().getMobile()))) {
            ToastUtils.showShort("请输入登录手机号码");
            return;
        }
        UIUtils.getSmsCheckCode(etPhoneNum.getText().toString().trim(), "3", new UIUtils.OnGetSmsCheckCodeListener() {
            @Override
            public void onGetSmsCheckCode(BaseBean<SmsCheckCodeBean> result) {
                if (result.isSuccess() && result.getData() != null && !TextUtils.isEmpty(result.getData().getCode())) {
                    ToastUtils.showShort("验证码已发送");
                    smsCheckCode = Aes.decryptAES(result.getData().getCode());
                    LogUtils.e("smsCheckCode: " + smsCheckCode);
                    if (countDownTimer != null) {
//                        SPUtil.getInstance().putLong(SPUtil.KEY_SMS_CODE_TIMESTAMP_FORGET_RESET_TRADE_PWD, System.currentTimeMillis());
                        countDownTimer.start();
                    }
                } else {
                    ToastUtils.showShort(result.getResMsg());
                }
            }
        });
    }

    public static void open(Context context) {
        context.startActivity(new Intent(context, ResetTradePwdActivity.class));
    }
}
