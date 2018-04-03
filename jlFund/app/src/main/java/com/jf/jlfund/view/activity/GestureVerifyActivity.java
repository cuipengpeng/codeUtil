package com.jf.jlfund.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jf.jlfund.R;
import com.jf.jlfund.base.BaseApplication;
import com.jf.jlfund.base.BaseLocalDataActivity;
import com.jf.jlfund.bean.GestureVerify;
import com.jf.jlfund.bean.UserInfo;
import com.jf.jlfund.http.HttpRequest;
import com.jf.jlfund.utils.Aes;
import com.jf.jlfund.utils.ConstantsUtil;
import com.jf.jlfund.utils.DeviceUtil;
import com.jf.jlfund.utils.GestureUtils;
import com.jf.jlfund.utils.LogUtils;
import com.jf.jlfund.utils.MD5;
import com.jf.jlfund.utils.SPUtil;
import com.jf.jlfund.utils.StringUtil;
import com.jf.jlfund.weight.dialog.MyPopupDialog;
import com.jf.jlfund.weight.gesture_lock.GestureContentView;
import com.jf.jlfund.weight.gesture_lock.GestureDrawline;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

import static com.jf.jlfund.view.activity.PutInActivity.popupDialog;


/**
 * 手势绘制/校验界面
 */
public class GestureVerifyActivity extends BaseLocalDataActivity {
    @BindView(R.id.ll_gestureVerityActivity_gestureThumb)
    LinearLayout gestureThumbLinearLayout;
    @BindView(R.id.iv_gestureVerityActivity_userImage)
    ImageView userLogo;
    @BindView(R.id.tv_gestureVerityActivity_phoneNumber)
    TextView textPhoneNumber;
    @BindView(R.id.tv_gestureVerityActivity_errorTip)
    TextView errorTipTextView;

    @BindView(R.id.iv_gestureVerityActivity_thumb1)
    ImageView thumb1ImageView;
    @BindView(R.id.iv_gestureVerityActivity_thumb2)
    ImageView thumb2ImageView;
    @BindView(R.id.iv_gestureVerityActivity_thumb3)
    ImageView thumb3ImageView;
    @BindView(R.id.iv_gestureVerityActivity_thumb4)
    ImageView thumb4ImageView;
    @BindView(R.id.iv_gestureVerityActivity_thumb5)
    ImageView thumb5ImageView;
    @BindView(R.id.iv_gestureVerityActivity_thumb6)
    ImageView thumb6ImageView;
    @BindView(R.id.iv_gestureVerityActivity_thumb7)
    ImageView thumb7ImageView;
    @BindView(R.id.iv_gestureVerityActivity_thumb8)
    ImageView thumb8ImageView;
    @BindView(R.id.iv_gestureVerityActivity_thumb9)
    ImageView thumb9ImageView;


    @BindView(R.id.ll_gesturePasswordActivity_fingerPassword)
    LinearLayout fingerPasswordLinearLayout;
    @BindView(R.id.fl_gestureVerityActivity_gestureContainer)
    FrameLayout gesturePasswordContainerFrameLayout;
    @BindView(R.id.tv_gestureVerityActivity_reDrawAndVerifyLoginPassword)
    TextView reDrawAndVerifyLoginPasswordTextView;

    @BindView(R.id.tv_gesturePasswordActivity_forgetGesture)
    TextView forgetGestureTextView;

    private GestureContentView mGestureContentView;
    GestureVerify gestureVerify;
    private String mParamPhoneNumber;
    private long mExitTime = 0;
    private int lastTime = 4;
    public static boolean isShow = false;
    private LinearLayout mllForgetGesture;

    public static final String KEY_OF_FINGERPRINT_API = "fingerprintApiKey";
    public static final String KEY_OF_LOCK_SCREEN_TIME = "screenTimeKey";
    public static final String KEY_OF_BACKGROUND_TIME = "backgroundTimeKey";


    public static final int MAX_FINGERPRINT_PASSWORD_COUNT = 4;
    public static final int MAX_GESTURE_PASSWORD_COUNT = 5;
    private int fingerprintVerifyCount = 0;
    private int gestureVerifyCount = 0;
    private CancellationSignal mCancellationSignal = new CancellationSignal();

    private boolean verifyGesturePassword = false;
    private int drawGesturePasswordCount = 0;
    private String firstDrawPasswordStr = "";

    public static final String KEY_OF_PASSWORD_TYPE = "passwordTypeKey";
    private int currentPasswordType;
    public static final int DRAW_GESTURE_PASSWORD = 300;
    public static final int VERITY_GESTURE_PASSWORD = 301;
    public static final int FINGERPRINT_PASSWORD = 302;
    public static final int VERITY_GESTURE_PASSWORD_AND_VERIFY_LOGIN_PASSWORD = 303;
    public static final String KEY_OF_5MINUTE_SECRITY_CHECK = "fiveMinuteSectiryCheckKey";
    public static boolean fiveMinuteSectiryCheck = false;
    public static final String KEY_OF_BACK_TAB_INDEX = "backTabIndexKey";
    private int backToTabIndex = -1;

    public static final String KEY_OF_CLOSE_GESTURE_PASSWORD = "closeGesturePsswordKey";
    private boolean closeGesturePassword = false;

    private boolean canBack = true;
    public static boolean initAccountFragment = true;

    private Dialog firstTipDialog;
    private Dialog secondTipDialog;

    @Override
    protected String getPageTitle() {
        return null;
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_gesture_verify;
    }

    @Override
    protected void initPageData() {
        Intent intent = getIntent();
        if (intent != null) {
            if (supportFingerprint(this)) {
                currentPasswordType = getIntent().getIntExtra(KEY_OF_PASSWORD_TYPE, FINGERPRINT_PASSWORD);
            } else if (StringUtil.notEmpty(SPUtil.getInstance().getUserInfo().getGesturePassword())) {
                currentPasswordType = getIntent().getIntExtra(KEY_OF_PASSWORD_TYPE, VERITY_GESTURE_PASSWORD);
            } else {
                currentPasswordType = getIntent().getIntExtra(KEY_OF_PASSWORD_TYPE, DRAW_GESTURE_PASSWORD);
            }
            backToTabIndex = getIntent().getIntExtra(KEY_OF_BACK_TAB_INDEX, -1);
            fiveMinuteSectiryCheck = getIntent().getBooleanExtra(KEY_OF_5MINUTE_SECRITY_CHECK, false);
        }

        closeGesturePassword = getIntent().getBooleanExtra(KEY_OF_CLOSE_GESTURE_PASSWORD, false);

        showCorrespondingView();

        secondTipDialog = new Dialog(this);
        isShow = true;
        gestureVerify = new GestureVerify();
        gestureVerify.setPsd(SPUtil.getInstance().getUserInfo().getGesturePassword());
        LogUtils.printLog("currentPasswordType=" + currentPasswordType + "--closeGesturePassword=" + closeGesturePassword);
        setUpViews();
    }

    private void showCorrespondingView() {
        switch (currentPasswordType) {
            case DRAW_GESTURE_PASSWORD:
                baseTitleTextView.setText("设置手势密码");
                gestureThumbLinearLayout.setVisibility(View.VISIBLE);
                userLogo.setVisibility(View.GONE);
                textPhoneNumber.setVisibility(View.VISIBLE);
                textPhoneNumber.setTextColor(GestureVerifyActivity.this.getResources().getColor(R.color.appTitleColor));
                textPhoneNumber.setText("绘制解锁图案");
                reDrawAndVerifyLoginPasswordTextView.setVisibility(View.INVISIBLE);
                gesturePasswordContainerFrameLayout.setVisibility(View.VISIBLE);
                fingerPasswordLinearLayout.setVisibility(View.GONE);
                break;
            case VERITY_GESTURE_PASSWORD:
                baseTitleTextView.setText("验证手势密码");
                verifyGesturePassword = true;
                userLogo.setVisibility(View.VISIBLE);
                gestureThumbLinearLayout.setVisibility(View.GONE);
                showGesturePasswordView();
                //      Glide.with(this).load(SPUtil.getInstance().getUserInfo().ge).into(userLogo);
                textPhoneNumber.setVisibility(View.VISIBLE);
                textPhoneNumber.setTextColor(GestureVerifyActivity.this.getResources().getColor(R.color.appTitleColor));
                textPhoneNumber.setText("hi，"+StringUtil.hidePhoneNumber(Aes.decryptAES(SPUtil.getInstance().getUserInfo().getMobile())));
                break;
            case VERITY_GESTURE_PASSWORD_AND_VERIFY_LOGIN_PASSWORD:
                baseTitleTextView.setText("验证手势密码");
                verifyGesturePassword = true;
                userLogo.setVisibility(View.INVISIBLE);
                gestureThumbLinearLayout.setVisibility(View.GONE);
                showGesturePasswordViewAndVerifyLoginPassword();
                //      Glide.with(this).load(SPUtil.getInstance().getUserInfo().ge).into(userLogo);
                textPhoneNumber.setVisibility(View.VISIBLE);
                textPhoneNumber.setTextColor(GestureVerifyActivity.this.getResources().getColor(R.color.appTitleColor));
                textPhoneNumber.setText("hi，"+StringUtil.hidePhoneNumber(Aes.decryptAES(SPUtil.getInstance().getUserInfo().getMobile())));
                textPhoneNumber.setText("绘制解锁图案");
                break;
            case FINGERPRINT_PASSWORD:
                verifyGesturePassword = true;
                userLogo.setVisibility(View.VISIBLE);
                gestureThumbLinearLayout.setVisibility(View.GONE);
                //      Glide.with(this).load(SPUtil.getInstance().getUserInfo().ge).into(userLogo);
                textPhoneNumber.setText("hi，"+StringUtil.hidePhoneNumber(Aes.decryptAES(SPUtil.getInstance().getUserInfo().getMobile())));
                forgetGestureTextView.setText("手势密码解锁");
                reDrawAndVerifyLoginPasswordTextView.setVisibility(View.GONE);
                gesturePasswordContainerFrameLayout.setVisibility(View.GONE);
                fingerPasswordLinearLayout.setVisibility(View.VISIBLE);

                firstTipDialog = popupDialog(this, "通过手机已有指纹解锁", ConstantsUtil.Dialog.FINGERPRINT_VERIFY_FIRST_TIP, null, false);
                validFingerprintPassword();
                break;
        }
        errorTipTextView.setVisibility(View.GONE);
    }


    public void showGesturePasswordView() {
        mCancellationSignal.cancel();
//        mCancellationSignal = null;
        forgetGestureTextView.setVisibility(View.VISIBLE);
        forgetGestureTextView.setText("忘记手势密码");
        reDrawAndVerifyLoginPasswordTextView.setVisibility(View.INVISIBLE);
        reDrawAndVerifyLoginPasswordTextView.setText("验证登录密码");
        gesturePasswordContainerFrameLayout.setVisibility(View.VISIBLE);
        fingerPasswordLinearLayout.setVisibility(View.GONE);
        currentPasswordType = VERITY_GESTURE_PASSWORD;
    }

    public void showGesturePasswordViewAndVerifyLoginPassword() {
        mCancellationSignal.cancel();
//        mCancellationSignal = null;
        forgetGestureTextView.setText("忘记手势密码");
        forgetGestureTextView.setVisibility(View.INVISIBLE);
        reDrawAndVerifyLoginPasswordTextView.setVisibility(View.VISIBLE);
        reDrawAndVerifyLoginPasswordTextView.setText("验证登录密码");
        gesturePasswordContainerFrameLayout.setVisibility(View.VISIBLE);
        fingerPasswordLinearLayout.setVisibility(View.GONE);
    }

    @Override
    protected synchronized void onDestroy() {
        isShow = false;
        super.onDestroy();
    }

    @Override
    public void finish() {
        isShow = false;
        super.finish();
    }

    private void setUpViews() {
        // 初始化一个显示各个点的viewGroup
        LogUtils.printLog("verifyGesturePassword=" + verifyGesturePassword);
        mGestureContentView = new GestureContentView(this, gesturePasswordContainerFrameLayout, verifyGesturePassword, gestureVerify.getPsd(),
                new GestureDrawline.GestureCallBack() {

                    @Override
                    public void onGestureCodeInput(String inputCode) {
                        drawGesturePasswordCount++;
                        if (drawGesturePasswordCount == 1) {
                            if (inputCode.length() > 3) {
                                firstDrawPasswordStr = inputCode;
                                textPhoneNumber.setTextColor(GestureVerifyActivity.this.getResources().getColor(R.color.appTitleColor));
                                textPhoneNumber.setText("确认解锁图案");
                                reDrawAndVerifyLoginPasswordTextView.setVisibility(View.VISIBLE);
                                reDrawAndVerifyLoginPasswordTextView.setText("重新绘制");
                                drawThumbGesture(inputCode);
                            } else {
                                drawGesturePasswordCount = 0;
                                textPhoneNumber.setTextColor(GestureVerifyActivity.this.getResources().getColor(R.color.appRedColor));
                                textPhoneNumber.setText("至少连接4个点，请再次绘制");
                            }
                        } else if (drawGesturePasswordCount >= 2) {
                            if (StringUtil.notEmpty(firstDrawPasswordStr) && firstDrawPasswordStr.equals(inputCode)) {
                                Toast.makeText(GestureVerifyActivity.this, "成功开启", Toast.LENGTH_SHORT).show();
                                UserInfo userInfo = SPUtil.getInstance().getUserInfo();
                                userInfo.setGesturePassword(inputCode);
                                SPUtil.writeUserConfigInfo(GestureVerifyActivity.this, userInfo);
                                GestureVerifyActivity.this.finish();
                            } else {
                                textPhoneNumber.setTextColor(GestureVerifyActivity.this.getResources().getColor(R.color.appRedColor));
                                textPhoneNumber.setText("与上一次手势密码不一致，请再次绘制");
                            }
                        }
                    }

                    @Override
                    public void checkedSuccess() {
                        long timeScape = System.currentTimeMillis() - gestureVerify.getValidTime();
//                        if ((timeScape < 60 * 60 * 1000 && gestureVerify.getValidTime() != 0) || lastTime <= 0) {
//                            updateVerify();
//                        } else {

                        LogUtils.printLog("currentPasswordType=" + currentPasswordType + "--closeGesturePassword=" + closeGesturePassword);
                        switch (currentPasswordType) {
                            case VERITY_GESTURE_PASSWORD:
                                if (fiveMinuteSectiryCheck) {
                                    removeLockScreenAndBackgroundKey();
                                }
                                BaseApplication.goIntoAccountSecurity = true;

                                mGestureContentView.gestureDrawline.clearDrawlineState(1000L, true);
                                Toast.makeText(GestureVerifyActivity.this, "验证成功", Toast.LENGTH_SHORT).show();
                                GestureVerifyActivity.this.finish();
                                break;
                            case VERITY_GESTURE_PASSWORD_AND_VERIFY_LOGIN_PASSWORD:
                                showVerifyLoginPassword();
                                break;
                        }
//                        }
                    }

                    @Override
                    public void checkedFail() {
                        updateVerify();
                    }
                });
    }

    private void showVerifyLoginPassword() {
        if (closeGesturePassword) {
            UserInfo userInfo = SPUtil.getInstance().getUserInfo();
            userInfo.setHasFinderprintPassword(false);
            userInfo.setGesturePassword("");
            SPUtil.writeUserConfigInfo(GestureVerifyActivity.this, userInfo);
            GestureVerifyActivity.this.finish();
        } else {
            currentPasswordType = DRAW_GESTURE_PASSWORD;
            mGestureContentView.gestureDrawline.clearDrawlineState(0L, true);
            showCorrespondingView();
            mGestureContentView.gestureDrawline.isVerify = false;
        }
    }

    /**
     * 绘制缩略图手势
     *
     * @param inputCode
     */
    public void drawThumbGesture(String inputCode) {
        for (int i = 0; i < inputCode.length(); i++) {
            LogUtils.printLog(Integer.valueOf(inputCode.charAt(i) + "") + "--");
            switch (Integer.valueOf(inputCode.charAt(i) + "")) {
                case 1:
                    thumb1ImageView.setBackgroundResource(R.drawable.circle_blue_radius2);
                    break;
                case 2:
                    thumb2ImageView.setBackgroundResource(R.drawable.circle_blue_radius2);
                    break;
                case 3:
                    thumb3ImageView.setBackgroundResource(R.drawable.circle_blue_radius2);
                    break;
                case 4:
                    thumb4ImageView.setBackgroundResource(R.drawable.circle_blue_radius2);
                    break;
                case 5:
                    thumb5ImageView.setBackgroundResource(R.drawable.circle_blue_radius2);
                    break;
                case 6:
                    thumb6ImageView.setBackgroundResource(R.drawable.circle_blue_radius2);
                    break;
                case 7:
                    thumb7ImageView.setBackgroundResource(R.drawable.circle_blue_radius2);
                    break;
                case 8:
                    thumb8ImageView.setBackgroundResource(R.drawable.circle_blue_radius2);
                    break;
                case 9:
                    thumb9ImageView.setBackgroundResource(R.drawable.circle_blue_radius2);
                    break;
            }
        }
    }


    /**
     * 重新设置手势
     */
    private void updateVerify() {
        if (lastTime > 0) {
            mGestureContentView.gestureDrawline.clearDrawlineState(1000L, false);
        } else {
            mGestureContentView.gestureDrawline.clearDrawlineState(0L, false);
        }

        textPhoneNumber.setVisibility(View.GONE);
        long timeScape = System.currentTimeMillis() - gestureVerify.getValidTime();
        if ((timeScape < 60 * 60 * 1000 && gestureVerify.getValidTime() != 0) || lastTime <= 0) {
            errorTipTextView.setVisibility(View.INVISIBLE);
            canBack = false;
            popupDialog(this, "手势密码失效，请重新登录", ConstantsUtil.Dialog.GESTURE_VERIFY_FAIL, null, false);

            //用户锁定60分钟内还在绘制，则更新最后一次绘制的时间
            if (lastTime == 0) {
                gestureVerify.setValidTime(System.currentTimeMillis());
                GestureUtils.setGesture(GestureVerifyActivity.this, gestureVerify);
            }

            //更新剩余时间
//            long tempTime = 60 - (timeScape / (60 * 1000));
//            tempTime = (tempTime > 60 || tempTime < 0) ? 60 : tempTime;
//            errorTipTextView.setText(Html
//                    .fromHtml("<font color='#FE664F'>手势验证登录被锁定,请" + tempTime + "分钟后重试</font>"));
        } else {
            errorTipTextView.setVisibility(View.VISIBLE);
            errorTipTextView.setText(Html
                    .fromHtml("<font color='#FE664F'>密码错误，还可以再绘制" + lastTime + "次</font>"));
            lastTime = lastTime - 1;
        }
        // 左右移动动画
        Animation shakeAnimation = AnimationUtils.loadAnimation(GestureVerifyActivity.this, R.anim.shake);
        errorTipTextView.startAnimation(shakeAnimation);
    }


    public static void removeLockScreenAndBackgroundKey() {
        SharedPreferences.Editor editor = BaseApplication.applicationContext.getSharedPreferences(ConstantsUtil.CONFIG_FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.remove(KEY_OF_LOCK_SCREEN_TIME);
        editor.remove(KEY_OF_BACKGROUND_TIME).apply();
    }

    public static long getLockScreenStartTime() {
        long lockScreenStartTime = System.currentTimeMillis();
        SharedPreferences sp = BaseApplication.applicationContext.getSharedPreferences(ConstantsUtil.CONFIG_FILE_NAME, Context.MODE_PRIVATE);
        if (sp.contains(KEY_OF_LOCK_SCREEN_TIME)) {
            lockScreenStartTime = sp.getLong(KEY_OF_LOCK_SCREEN_TIME, System.currentTimeMillis());
        }
        return lockScreenStartTime;
    }

    public static void putLockScreenStartTime(long time) {
        SharedPreferences sp = BaseApplication.applicationContext.getSharedPreferences(ConstantsUtil.CONFIG_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putLong(KEY_OF_LOCK_SCREEN_TIME, time).apply();
//        sp.edit().putLong(KEY_OF_LOCK_SCREEN_TIME, 0l).apply();
    }

    public static void putBackgroundStartTime(long time) {
        SharedPreferences sp = BaseApplication.applicationContext.getSharedPreferences(ConstantsUtil.CONFIG_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putLong(KEY_OF_BACKGROUND_TIME, time).apply();
//        sp.edit().putLong(KEY_OF_BACKGROUND_TIME, 0l).apply();
    }

    public static long getBackgroundStartTime() {
        long lockScreenStartTime = System.currentTimeMillis();
        SharedPreferences sp = BaseApplication.applicationContext.getSharedPreferences(ConstantsUtil.CONFIG_FILE_NAME, Context.MODE_PRIVATE);
        if (sp.contains(KEY_OF_BACKGROUND_TIME)) {
            lockScreenStartTime = sp.getLong(KEY_OF_BACKGROUND_TIME, System.currentTimeMillis());
        }

        return lockScreenStartTime;
    }

    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
//            showDenyPermissionDialog(this, requestCode, grantResults);
        }
    }

    @OnClick({R.id.tv_gesturePasswordActivity_forgetGesture, R.id.ll_gesturePasswordActivity_fingerPassword, R.id.tv_gestureVerityActivity_reDrawAndVerifyLoginPassword})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_gesturePasswordActivity_fingerPassword:
                break;
            case R.id.tv_gestureVerityActivity_reDrawAndVerifyLoginPassword:
                if (currentPasswordType == VERITY_GESTURE_PASSWORD_AND_VERIFY_LOGIN_PASSWORD) {
                    final MyPopupDialog myAlertDialog = new MyPopupDialog(this, "", "   身份验证\n\n\n " + StringUtil.hidePhoneNumber(Aes.decryptAES(SPUtil.getInstance().getUserInfo().getMobile())), "取消", "确定", "确定", true, true);
                    myAlertDialog.mPop_dialog_tv_left_content.setTextColor(getResources().getColor(R.color.appViewFullTextColor));
                    myAlertDialog.mPop_dialog_tv_right_content.setTextColor(getResources().getColor(R.color.appViewFullTextColor));
                    myAlertDialog.mPop_dialog_tv_title.setVisibility(View.GONE);
                    myAlertDialog.setOnClickListen(new MyPopupDialog.OnClickListen() {

                        @Override
                        public void leftClick() {
                            myAlertDialog.dismiss();
                        }

                        @Override
                        public void rightClick() {
                            loginToVerifyPassword(myAlertDialog.mEdittext.getText().toString().trim(), myAlertDialog);
                        }
                    });
                    myAlertDialog.show();
                } else if (currentPasswordType == DRAW_GESTURE_PASSWORD) {
                    drawGesturePasswordCount = 0;
                    textPhoneNumber.setTextColor(GestureVerifyActivity.this.getResources().getColor(R.color.appTitleColor));
                    textPhoneNumber.setText("绘制解锁图案");
                    reDrawAndVerifyLoginPasswordTextView.setVisibility(View.INVISIBLE);
                    thumb1ImageView.setBackgroundResource(R.drawable.circle_dp6_cccccc);
                    thumb2ImageView.setBackgroundResource(R.drawable.circle_dp6_cccccc);
                    thumb3ImageView.setBackgroundResource(R.drawable.circle_dp6_cccccc);
                    thumb4ImageView.setBackgroundResource(R.drawable.circle_dp6_cccccc);
                    thumb5ImageView.setBackgroundResource(R.drawable.circle_dp6_cccccc);
                    thumb6ImageView.setBackgroundResource(R.drawable.circle_dp6_cccccc);
                    thumb7ImageView.setBackgroundResource(R.drawable.circle_dp6_cccccc);
                    thumb8ImageView.setBackgroundResource(R.drawable.circle_dp6_cccccc);
                    thumb9ImageView.setBackgroundResource(R.drawable.circle_dp6_cccccc);
                }
                break;
            case R.id.tv_gesturePasswordActivity_forgetGesture:
                if (currentPasswordType == VERITY_GESTURE_PASSWORD) {
//                    clearUserInfoAndStartLoginActivity(this);
                    SPUtil.getInstance().clearUserInfo();
                    LoginActivity.open(this, LoginActivity.KEY_TOKEN_INVALID_GO_MAIN);
                    finish();
                    if (DeviceUtil.isPhoneHasLock()) {
//                    Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
//                    startActivity(intent);

//                    Intent intent = new Intent("/");
//                    ComponentName cm = new ComponentName("com.android.settings","com.android.settings.ChooseLockGeneric");
//                    intent.setComponent(cm);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivityForResult(intent, 0);
                    }
                } else if (currentPasswordType == FINGERPRINT_PASSWORD) {
                    showGesturePasswordView();
                }
                break;
        }
    }

    //关闭手势密码时如果验证失败5次重新登录后手势密码依旧处于开启状态【关闭失败】
    public static void clearUserInfoAndStartLoginActivity(Activity activity) {
        UserInfo userInfo = SPUtil.getInstance().getUserInfo();
        userInfo.setGesturePassword("");
        userInfo.setHasFinderprintPassword(false);
        SPUtil.writeUserConfigInfo(activity, userInfo);

        SPUtil.getInstance().clearUserInfo();
        LoginActivity.open(activity, LoginActivity.KEY_TOKEN_INVALID_GO_MAIN);
        activity.finish();
    }

    public void validFingerprintPassword() {
        if (supportFingerprint(this)) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED) {
                    if (mCancellationSignal.isCanceled()) {
                        mCancellationSignal = new CancellationSignal();
                    }
                    FingerprintManagerCompat.from(this).authenticate(null, 0, mCancellationSignal, new MyFingerPrintCallBack(this), null);
                } else {
                    Toast.makeText(this, "请在设置中开启本应用的指纹密码权限", Toast.LENGTH_SHORT).show();
                }

//                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//                        if( ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED){
//                            if (mCancellationSignal.isCanceled()) {
//                                mCancellationSignal = new CancellationSignal();
//                            }
//                            fingerprintManager.authenticate(null,0, mCancellationSignal, new MyFingerPrintCallBack(), null);
//                        }else {
//                            Toast.makeText(this, "请允许使用设备的指纹密码功能", Toast.LENGTH_SHORT).show();
//                        }
//                    }else {
//                        fingerprintManager.authenticate(null,0, mCancellationSignal, new MyFingerPrintCallBack(), null);
//                    }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            LogUtils.printLog("mei you fingerprint hardware");
        }
    }

    public static boolean supportFingerprint(Activity activity) {
        boolean supportFingerprint = false;
        if (hasFingerPrintHardWare(activity) && enrollFingerprint(activity) && SPUtil.getInstance().getUserInfo().isHasFinderprintPassword()) {
            supportFingerprint = true;
        }

        return supportFingerprint;
    }

    public static boolean supportAndEnrollFingerprint(Activity activity) {
        boolean supportFingerprint = false;
        if (hasFingerPrintHardWare(activity) && enrollFingerprint(activity)) {
            supportFingerprint = true;
        }

        return supportFingerprint;
    }

    public static boolean enrollFingerprint(Context context) {
        boolean enrollFingerprint;
        FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(context);
        if (fingerprintManager.hasEnrolledFingerprints()) {
            enrollFingerprint = true;
        } else {
            enrollFingerprint = false;
            LogUtils.printLog("您还没有录入指纹, 请在设置界面录入至少一个指纹");
//            Toast.makeText(context, "您还没有录入指纹, 请在设置界面录入至少一个指纹",
//                    Toast.LENGTH_LONG).show();
        }
        return enrollFingerprint;
    }


    public static boolean hasFingerPrintHardWare(Activity activity) {
        boolean supportFingerPrintHardWare = false;
        try {
            if (hasFingerPrintApi(activity)) {
                FingerprintManager manager = (FingerprintManager) BaseApplication.applicationContext.getSystemService(activity.FINGERPRINT_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && ActivityCompat.checkSelfPermission(activity, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.USE_FINGERPRINT}, 0);
                }
                supportFingerPrintHardWare = manager.isHardwareDetected();
            } else {
                supportFingerPrintHardWare = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return supportFingerPrintHardWare;
    }

    private static boolean hasFingerPrintApi(Context context) {
        SharedPreferences sp = context.getSharedPreferences(ConstantsUtil.CONFIG_FILE_NAME, Context.MODE_PRIVATE);
        if (sp.contains(KEY_OF_FINGERPRINT_API)) { // 检查是否存在该值，不必每次都通过反射来检查
            return sp.getBoolean(KEY_OF_FINGERPRINT_API, false);
        }
        SharedPreferences.Editor editor = sp.edit();
        try {
            Class.forName("android.hardware.fingerprint.FingerprintManager"); // 通过反射判断是否存在该类
            editor.putBoolean(KEY_OF_FINGERPRINT_API, true);
        } catch (ClassNotFoundException e) {
            editor.putBoolean(KEY_OF_FINGERPRINT_API, false);
            e.printStackTrace();
        }
        editor.apply();
        return sp.getBoolean(KEY_OF_FINGERPRINT_API, false);
    }

    public class MyFingerPrintCallBack extends FingerprintManagerCompat.AuthenticationCallback {
        private Activity mActivity;

        public MyFingerPrintCallBack(Activity activity) {
            this.mActivity = activity;
        }

        // 当出现错误的时候回调此函数，比如多次尝试都失败了的时候，errString是错误信息
        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            LogUtils.printLog("onAuthenticationError: " + errString);
        }

        // 当指纹验证失败的时候会回调此函数，失败之后允许多次尝试，失败次数过多会停止响应一段时间然后再停止sensor的工作
        @Override
        public void onAuthenticationFailed() {
            LogUtils.printLog("onAuthenticationFailed: " + "验证失败");
            fingerprintVerifyCount++;
            if (fingerprintVerifyCount < 3 && !secondTipDialog.isShowing()) {
                firstTipDialog.dismiss();
                secondTipDialog = PutInActivity.popupDialog(mActivity, "再试一次\n通过手机已有指纹解锁", ConstantsUtil.Dialog.FINGERPRINT_VERIFY_FIRST_FAIL, null, true);
            } else if (fingerprintVerifyCount == 3) {
                Toast.makeText(mActivity, "指纹解锁失败", Toast.LENGTH_SHORT).show();
            } else if (fingerprintVerifyCount == MAX_FINGERPRINT_PASSWORD_COUNT) {
                secondTipDialog.dismiss();
                showGesturePasswordView();
            }
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            LogUtils.printLog("onAuthenticationHelp: " + helpString);
        }

        // 当验证的指纹成功时会回调此函数，然后不再监听指纹sensor
        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult
                                                      result) {
            LogUtils.printLog("onAuthenticationSucceeded: " + "验证成功");
            if (fiveMinuteSectiryCheck) {
                removeLockScreenAndBackgroundKey();
            }
            BaseApplication.goIntoAccountSecurity = true;
            mActivity.finish();
        }
    }


    protected void setBackListener() {
        baseBackRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fiveMinuteSectiryCheck) {
                    MainActivity.open(GestureVerifyActivity.this, 1);
                } else if (backToTabIndex != -1) {
                    MainActivity.open(GestureVerifyActivity.this, backToTabIndex);
                } else if (!canBack) {
                    // do nothing
                } else {
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (fiveMinuteSectiryCheck) {
            MainActivity.open(this, 1);
            return true;
        } else if (backToTabIndex != -1) {
            MainActivity.open(this, backToTabIndex);
            return true;
        } else if (!canBack) {
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     *
     */
    private void loginToVerifyPassword(String password, final Dialog dialog) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile", SPUtil.getInstance().getUserInfo().getMobile().trim());
        params.put("password", MD5.md5(password));
        HttpRequest.post(HttpRequest.JL_FUND_TEST_ENV_WEB_URL + HttpRequest.FUND_LOGIN, params, this, new HttpRequest.HttpResponseCallBank() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
//                mFundTrendBean = JSON.parseObject(response.body(), FundTrendBean.class);
                UserInfo userInfo = SPUtil.getInstance().getUserInfo();
                userInfo.setHasFinderprintPassword(false);
                userInfo.setGesturePassword("");
                SPUtil.writeUserConfigInfo(GestureVerifyActivity.this, userInfo);
                
                showVerifyLoginPassword();
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    public static void open(Context context, int passwordType) {
        Intent intent = new Intent(context, GestureVerifyActivity.class);
        intent.putExtra(GestureVerifyActivity.KEY_OF_PASSWORD_TYPE, passwordType);
        context.startActivity(intent);
    }

    public static void open(Context context, int passwordType, int backToTabIndex, boolean fiveMinuteSectiryCheck) {
        Intent intent = new Intent(context, GestureVerifyActivity.class);
        intent.putExtra(GestureVerifyActivity.KEY_OF_PASSWORD_TYPE, passwordType);
        intent.putExtra(KEY_OF_BACK_TAB_INDEX, backToTabIndex);
        intent.putExtra(KEY_OF_5MINUTE_SECRITY_CHECK, fiveMinuteSectiryCheck);
        context.startActivity(intent);
    }

    public static void open(Context context, int passwordType, boolean closeGesturePassword) {
        Intent intent = new Intent(context, GestureVerifyActivity.class);
        intent.putExtra(GestureVerifyActivity.KEY_OF_PASSWORD_TYPE, passwordType);
        intent.putExtra(KEY_OF_CLOSE_GESTURE_PASSWORD, closeGesturePassword);
        context.startActivity(intent);
    }
}
