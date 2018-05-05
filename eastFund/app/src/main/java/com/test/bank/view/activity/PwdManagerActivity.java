package com.test.bank.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.test.bank.R;
import com.test.bank.base.BaseActivity;
import com.test.bank.bean.UserInfo;
import com.test.bank.utils.SPUtil;
import com.test.bank.utils.StringUtil;
import com.test.bank.weight.SwitchView;
import com.test.bank.weight.dialog.CommonDialogFragment;
import com.test.bank.weight.dialog.FingerCheckDialog;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.OnClick;

public class PwdManagerActivity extends BaseActivity {
    @BindView(R.id.tv_pwdManager_gensture)
    TextView tvGensture;

    @BindView(R.id.switchView_pwdManager_gesturePwd)
    SwitchView switchViewGesture;
    @BindView(R.id.switchView_pwdManager_fingerPrintPwd)
    SwitchView switchViewFinger;
    @BindView(R.id.ll_pwdManager_bottom)
    LinearLayout llBottom;
    @BindView(R.id.rl_pwdManager_fingerPrint)
    RelativeLayout rlFingerPrint;
    @BindView(R.id.tv_pwdManager_bottomTip)
    TextView tvBottomTip;

    private boolean isOpenGensture;
    private boolean isOpenFingerPrint = false;

    @Override
    protected void init() {

        if (GestureVerifyActivity.supportAndEnrollFingerprint(this)) {
            //硬件设备，并且已录入指纹
            rlFingerPrint.setVisibility(View.VISIBLE);
            tvBottomTip.setVisibility(View.VISIBLE);
        } else {
            rlFingerPrint.setVisibility(View.GONE);
        }

        initListener();
    }

    private void initListener() {
        switchViewGesture.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(SwitchView view) {
                tvBottomTip.setVisibility(View.GONE);
            }

            @Override
            public void toggleToOff(SwitchView view) {
                tvBottomTip.setVisibility(View.VISIBLE);
            }
        });

//        switchViewFinger.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
//            @Override
//            public void toggleToOn(SwitchView view) {
//                UserInfo userInfo = SPUtil.getInstance().getUserInfo();
//                userInfo.setHasFinderprintPassword(true);
//                SPUtil.writeUserConfigInfo(PwdManagerActivity.this, userInfo);
//            }
//
//            @Override
//            public void toggleToOff(SwitchView view) {
//                UserInfo userInfo = SPUtil.getInstance().getUserInfo();
//                userInfo.setHasFinderprintPassword(false);
//                SPUtil.writeUserConfigInfo(PwdManagerActivity.this, userInfo);
//            }
//        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pwd_manager;
    }

    @Override
    protected void doBusiness() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        UserInfo userInfo = SPUtil.getInstance().getUserInfo();
        isOpenGensture = StringUtil.notEmpty(userInfo.getGesturePassword());
        if (isOpenGensture) {
            switchViewGesture.toggleSwitch(true);
            llBottom.setVisibility(View.VISIBLE);
            tvGensture.setText("关闭手势密码");

            if (userInfo.isHasFinderprintPassword()) {
                switchViewFinger.toggleSwitch(true);
            } else {
                switchViewFinger.toggleSwitch(false);
            }
        } else {
            switchViewGesture.toggleSwitch(false);
            tvGensture.setText("开启手势密码");
            llBottom.setVisibility(View.GONE);
            switchViewFinger.toggleSwitch(false);
        }
    }

    private FingerCheckDialog fingerCheckDialog;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @OnClick({R.id.tv_pwdManager_resetLoginPwd, R.id.tv_pwdManager_resetTradePwd, R.id.rl_pwdManager_closeGensturePwd, R.id.switchView_pwdManager_gesturePwd, R.id.tv_pwdManger_modifyGensturePwd, R.id.rl_pwdManager_fingerPrint, R.id.switchView_pwdManager_fingerPrintPwd})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_pwdManager_resetLoginPwd:
                MobclickAgent.onEvent(this, "click_btn_pwdManagerActivity_resetLoginPwd");
                ResetLoginPwdActivity.open(this);
                break;
            case R.id.tv_pwdManager_resetTradePwd:
                MobclickAgent.onEvent(this, "click_btn_pwdManagerActivity_resetTradePwd");
                if (SPUtil.getInstance().getUserInfo().getBindBankCard()) {
                    ResetTradePwdActivity.open(this);
                } else {
                    showCommonDialog("您尚未绑定银行卡，请先绑卡", "取消", "去绑卡", null, new CommonDialogFragment.OnRightClickListener() {
                        @Override
                        public void onClickRight() {
                            RapidBindCardActivity.open(PwdManagerActivity.this);
                        }
                    });
                }
                break;
            case R.id.rl_pwdManager_closeGensturePwd:
            case R.id.switchView_pwdManager_gesturePwd:
                MobclickAgent.onEvent(this,"click_pwdManagerActivity_switchGensturePwd");
                if (StringUtil.notEmpty(SPUtil.getInstance().getUserInfo().getGesturePassword())) {
                    GestureVerifyActivity.open(PwdManagerActivity.this, GestureVerifyActivity.VERITY_GESTURE_PASSWORD_AND_VERIFY_LOGIN_PASSWORD, true);
                } else {
                    showOneBtnDialog("在进入账户页面需要手势验证", "知道了", new CommonDialogFragment.OnLeftClickListener() {
                        @Override
                        public void onClickLeft() {
                            GestureVerifyActivity.open(PwdManagerActivity.this, GestureVerifyActivity.DRAW_GESTURE_PASSWORD);
                        }
                    }, true);
                }
                break;
            case R.id.tv_pwdManger_modifyGensturePwd:
                MobclickAgent.onEvent(this,"click_pwdManagerActivity_modifyGensturePwd");
                GestureVerifyActivity.open(PwdManagerActivity.this, GestureVerifyActivity.VERITY_GESTURE_PASSWORD_AND_VERIFY_LOGIN_PASSWORD, false);
                break;
            case R.id.rl_pwdManager_fingerPrint:
            case R.id.switchView_pwdManager_fingerPrintPwd:
                MobclickAgent.onEvent(this,"click_pwdManagerActivity_switchFingerPrintPwd");
                if (switchViewFinger.isOpened()) {  //关闭指纹的时候不需要弹框提示
                    UserInfo userInfo = SPUtil.getInstance().getUserInfo();
                    userInfo.setHasFinderprintPassword(false);
                    SPUtil.getInstance().putUserInfo(userInfo);
                    switchViewFinger.toggleSwitch();
                    return;
                }
                if (fingerCheckDialog == null) {
                    fingerCheckDialog = new FingerCheckDialog(PwdManagerActivity.this);
                }
                fingerCheckDialog.show();
                FingerprintManagerCompat.from(this).authenticate(null, 0, null, new MyFingerPrintCallBack(PwdManagerActivity.this), null);
                break;
        }
    }

    public class MyFingerPrintCallBack extends FingerprintManagerCompat.AuthenticationCallback {
        private Activity mActivity;

        public MyFingerPrintCallBack(Activity activity) {
            this.mActivity = activity;
        }

        // 当出现错误的时候回调此函数，比如多次尝试都失败了的时候，errString是错误信息
        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
        }

        // 当指纹验证失败的时候会回调此函数，失败之后允许多次尝试，失败次数过多会停止响应一段时间然后再停止sensor的工作
        @Override
        public void onAuthenticationFailed() {
            if (fingerCheckDialog != null && fingerCheckDialog.isShowing()) {
                fingerCheckDialog.onCheckFailed();
            }
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        }

        // 当验证的指纹成功时会回调此函数，然后不再监听指纹sensor
        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult
                                                      result) {

            UserInfo userInfo = SPUtil.getInstance().getUserInfo();
            if (SPUtil.getInstance().getUserInfo().isHasFinderprintPassword()) {
                userInfo.setHasFinderprintPassword(false);
            } else {
                userInfo.setHasFinderprintPassword(true);
            }
            SPUtil.writeUserConfigInfo(PwdManagerActivity.this, userInfo);
            if (switchViewFinger != null) {
                switchViewFinger.toggleSwitch();
            }
            if (fingerCheckDialog != null && fingerCheckDialog.isShowing()) {
                fingerCheckDialog.onCheckSuccess();
                fingerCheckDialog.dismiss();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SPUtil.getInstance().putBoolean(SPUtil.KEY_IS_OPEN_GENSTURE_PWD, isOpenGensture);
        SPUtil.getInstance().putBoolean(SPUtil.KEY_IS_OPEN_FINGER_PWD, isOpenFingerPrint);
    }

    public static void open(Context context) {
        context.startActivity(new Intent(context, PwdManagerActivity.class));
    }
}
