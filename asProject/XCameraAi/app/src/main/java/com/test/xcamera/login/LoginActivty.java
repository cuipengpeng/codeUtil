package com.test.xcamera.login;


import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;
import com.test.xcamera.h5_page.CommunityActivity;
import com.test.xcamera.login.presenter.LoginPresenterImpl;
import com.test.xcamera.login.presenter.LoginPresenterInterface;
import com.test.xcamera.personal.PersonAgreeActivity;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.NetworkUtil;
import com.test.xcamera.utils.proxy.Perform;
import com.test.xcamera.utils.proxy.click.NonDuplicateFactory;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Author: mz
 * Time:  2019/10/8
 */
public class LoginActivty extends MOBaseActivity implements LoginInterface, CountDownTimerListener {
    @BindView(R.id.ed_phone)
    EditText edPhone;
    @BindView(R.id.ed_code)
    EditText edCode;
    @BindView(R.id.ed_get_code)
    TextView edGetCode;
    @BindView(R.id.liner_login)
    LinearLayout linerLogin;
    @BindView(R.id.image_login)
    ImageView imageLogin;
    @BindView(R.id.tv_login)
    TextView tvLogin;
    @BindView(R.id.left_iv_title)
    ImageView leftIvTitle;
    @BindView(R.id.tv_user_agreement)
    TextView tvUserAgreement;
    @BindView(R.id.tv_privacy_policy)
    TextView tvPrivacyPolicy;
    @BindView(R.id.is_check)
    ImageView isCheck;
    @BindView(R.id.tv_3)
    TextView tv3;
    @BindView(R.id.lin2)
    LinearLayout lin2;
    private Animation mRotateAnimation;


    private LoginPresenterInterface loginPresenter;
    private boolean isLoginClickable;
    public long sumTime = 60000;
    public long delayTime = 1000;
    private VerificationCodeCountTimer countTimer;

    private boolean isPhonenNotEmpty, isCodeNotEmpty;
    private String isActivation = "";
    private boolean isTimerFinsh = true;

    private boolean isChecked;
    private String mCurPhoneNumber;
    public static void startLoginActivity(Context context){
        NonDuplicateFactory.proxy(new Perform() {
            @Override
            public void perform() {
                Intent intent=new Intent(context,LoginActivty.class);
                context.startActivity(intent);
            }
        });
    }
    @Override
    public int initView() {
        return R.layout.activity_login;
    }

    @Override
    public void initData() {
        mRotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_anim);

        noStatusBar();
        setStatusBarColor(getResources().getColor(R.color.c050505), 0);
        getCountTimer();
        countTimer.setTimerListener(this);
        loginPresenter = new LoginPresenterImpl(this);

        edCode.addTextChangedListener(new CodeWatcher());
        edPhone.addTextChangedListener(new PhoneWatcher());

        leftIvTitle.setImageResource(R.mipmap.icon_back_login);
        isActivation = getIntent().getStringExtra("isActivation");

        if ("1".equals(isActivation)) {
            leftIvTitle.setVisibility(View.GONE);
        }
    }

    @Override
    public String getPhone() {
        return edPhone.getText().toString().trim();
    }

    @Override
    public String getCode() {
        return edCode.getText().toString().trim();
    }

    @Override
    public void Finish() {
        onBackPressed();
    }


    @Override
    public void setLoginClickable(boolean isClickable) {
        this.isLoginClickable = isClickable;
        linerLogin.setClickable(!isClickable);
        tvLogin.setText(getString(R.string.login_go));
        imageLogin.setVisibility(View.GONE);
        imageLogin.clearAnimation();
    }

    @Override
    public String getisActivation() {
        return isActivation;
    }

    @Override
    public void codeCallBack(boolean isSuccess) {
        if (!isSuccess) {
            if (countTimer != null) {
                countTimer.cancel();
            }
            onFinish();
        } else {
            edCode.requestFocus();
        }
    }

    @OnClick({R.id.ed_get_code, R.id.liner_login, R.id.left_iv_title, R.id.is_check, R.id.tv_user_agreement, R.id.tv_privacy_policy})
    public void onViewClicked(View view) {


        switch (view.getId()) {
            case R.id.ed_get_code:
                if (phoneisEmpty()) {
                    if (!NetworkUtil.isNetworkConnected(mContext)) {
                        CameraToastUtil.show(getResourceToString(R.string.no_network), mContext);
                        return;
                    }
                    if (countTimer == null) {
                        getCountTimer();
                    }
                    isTimerFinsh = false;
                    countTimer.start();
                    edGetCode.setClickable(false);
                    edGetCode.setTextColor(getResources().getColor(R.color.color_bebebe));
                    loginPresenter.getcode();
                    mCurPhoneNumber = getPhone();
                }
                break;
            case R.id.liner_login:
                if (!NetworkUtil.isNetworkConnected(mContext)) {
                    CameraToastUtil.show(getResourceToString(R.string.no_network), mContext);
                    return;
                }
                if (!isLoginClickable && phoneisEmpty() && CodeTextisEmpty()) {
                    isLoginClickable = true;
                    linerLogin.setClickable(false);
                    loginPresenter.login();
                    tvLogin.setText(getString(R.string.login_ing));
                    imageLogin.setVisibility(View.VISIBLE);
                    imageLogin.startAnimation(mRotateAnimation);

                }
                break;
            case R.id.left_iv_title:
                Finish();
                if (CommunityActivity.logingCallback != null) {
                    CommunityActivity.logingCallback.loginCallback(0, "");
                }
                break;
            case R.id.tv_user_agreement:
                PersonAgreeActivity.startPersonAgreeActivity(this, PersonAgreeActivity.mType_User);
                break;
            case R.id.tv_privacy_policy:
                PersonAgreeActivity.startPersonAgreeActivity(this, PersonAgreeActivity.mType_Privacy);
                break;
            case R.id.is_check:
                if (!isChecked) {
                    isCheck.setImageResource(R.mipmap.icon_login_check_press);
                } else {
                    isCheck.setImageResource(R.mipmap.icon_login_check_normal);
                }
                isChecked = !isChecked;
                isLoginBtnColor();
                break;
        }
    }

    @Override
    public void onTimerDoing(long millisUntilFinished) {
        if (edGetCode == null) {
            return;
        }
        edGetCode.setText(getString(R.string.login_resend, (millisUntilFinished) / 1000) + "");
    }

    @Override
    public void onFinish() {
        if (edGetCode == null) {
            return;
        }
        edGetCode.setText(getResourceToString(R.string.getcode));
        edGetCode.setTextColor(getResources().getColor(R.color.white));
        edGetCode.setClickable(true);
        isTimerFinsh = true;
    }

    private boolean changePhoneNumber() {
        if (!TextUtils.isEmpty(getPhone()) && !TextUtils.isEmpty(mCurPhoneNumber)) {
            if (getPhone().length() == 11 && (!mCurPhoneNumber.equals(getPhone()))) {
                return true;
            }
        }
        return false;
    }

    public VerificationCodeCountTimer getCountTimer() {
        if (countTimer == null)
            countTimer = new VerificationCodeCountTimer(sumTime, delayTime);
        return countTimer;
    }

    public boolean phoneisEmpty() {
        if (!TextUtils.isEmpty(getPhone())) {
            return true;
        } else {
            CameraToastUtil.show(getResourceToString(R.string.phone_not_null), mContext);
        }

        return false;
    }

    public boolean CodeTextisEmpty() {
        if (!TextUtils.isEmpty(getCode())) {
            return true;
        } else {
            CameraToastUtil.show(getResourceToString(R.string.code_not_null), mContext);
        }
        return false;
    }

    @Override
    public void onBackPressed() {

        if (countTimer != null) {
            countTimer.cancel();
            countTimer = null;
        }

        if ("1".equals(isActivation)) {
            CameraToastUtil.show(getString(R.string.login_no_out), mContext);
        } else {
            super.onBackPressed();
        }
    }

    public class PhoneWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            isPhonenNotEmpty = s.length() > 0;
            if (s.length() == 11) {
                if (changePhoneNumber()) {
                    if (countTimer != null) {
                        countTimer.cancel();
                    }
                    onFinish();
                }
            }
            isLoginBtnColor();
            isGeTcodeBtn(s.toString());
        }
    }

    public class CodeWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            isCodeNotEmpty = s.length() > 0;
            isLoginBtnColor();
        }
    }

    public boolean isLoginBtnColor() {
        if (isPhonenNotEmpty && isCodeNotEmpty && isChecked) {
            linerLogin.setBackgroundResource(R.drawable.bt_login_bg_normal);
            linerLogin.setClickable(true);
            return true;
        } else {
            linerLogin.setBackgroundResource(R.drawable.bt_login_bg_press);
            linerLogin.setClickable(false);
        }
        return false;
    }

    public boolean isGeTcodeBtn(String s) {
        if (s.length() != 0 && isTimerFinsh) {
            edGetCode.setClickable(true);
            edGetCode.setTextColor(getResources().getColor(R.color.white));
            return true;
        } else {
            edGetCode.setClickable(false);
            edGetCode.setTextColor(getResources().getColor(R.color.color_bebebe));
        }
        return false;
    }

}
