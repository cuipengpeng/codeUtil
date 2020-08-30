package com.test.xcamera.splash;

import android.Manifest;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.framwork.base.view.MOBaseActivity;
import com.google.gson.Gson;
import com.test.xcamera.R;
import com.test.xcamera.api.ApiImpl;
import com.test.xcamera.api.CallBack;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.SplashScreenBean;
import com.test.xcamera.bean.User;
import com.test.xcamera.h5_page.AdvertisingActivity;
import com.test.xcamera.h5_page.H5BasePageActivity;
import com.test.xcamera.home.HomeActivity;
import com.test.xcamera.permission.RxPermissions;
import com.test.xcamera.statistic.StatisticOneKeyMakeVideo;
import com.test.xcamera.utils.DlgUtils;
import com.test.xcamera.utils.GlideUtils;
import com.test.xcamera.utils.SPUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;


/**
 * Created by smz on 2019/5/17.
 */

public class SplashActivity extends MOBaseActivity implements SplashInterface, View.OnClickListener {
    private static final String[] PERMISSIONS = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE
            , Manifest.permission.RECORD_AUDIO};
    ImageView ivImage;
    private String token;
    @BindView(R.id.advertisingImage)
    ImageView advertisingImage;
    @BindView(R.id.advertisingTime)
    RelativeLayout advertisingTime;
    @BindView(R.id.timeTextView)
    TextView timeTextView;
    private CountDownTimer countDownTimer;
    private boolean isFirstHomeActivity = false;
    private SplashScreenBean splashScreenBean;
    private final long ADVERTISING_TIME = 3 * 1000;

    @Override
    public int initView() {
        return R.layout.activity_splash;
    }

    @Override
    public void initClick() {

    }

    @Override
    public void initData() {
        setStatusBarImag();
        startSplashAnimation();
        if (!TextUtils.isEmpty(SPUtils.getToken(mContext))) {
            ExtendToken();
        }
        advertisingImage.setOnClickListener(this);
        advertisingTime.setOnClickListener(this);
    }


    public void startSplashAnimation() {
        ivImage = findViewById(R.id.iv_image);
        Animation animation = new AlphaAnimation(1.0f, 1.0f);
        animation.setDuration(500);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                requestMoreermissions();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        ivImage.startAnimation(animation);
    }

    private void requestMoreermissions() {
        new RxPermissions(SplashActivity.this)
                .requestEach(PERMISSIONS)
                .subscribe(permission -> {
                    if (permission.granted) {
                        getAdvertising();
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        DlgUtils.toast(SplashActivity.this, getResources().getString(R.string.plase_setting_permission));
                    } else {
                        DlgUtils.toast(SplashActivity.this, getResources().getString(R.string.plase_setting_permission));
                    }
                });
    }

    private void getAdvertising() {
        ApiImpl.getInstance().getSplashScreeen(new CallBack<SplashScreenBean>() {
            @Override
            public void onSuccess(SplashScreenBean splashScreenBean) {
                if (splashScreenBean != null && splashScreenBean.data.size() > 0 &&
                        !TextUtils.isEmpty(splashScreenBean.data.get(0).link)) {
                    SplashActivity.this.splashScreenBean = splashScreenBean;
                    if (ivImage != null) {
                        ivImage.setVisibility(View.GONE);
                    }
                    if (advertisingImage != null) {
                        advertisingImage.setEnabled(true);
                    }
                    startSplashAdvertising();
                } else {
                    if (advertisingImage != null) {
                        advertisingImage.setEnabled(false);
                    }
                    openHomeActivity();
                }
            }

            @Override
            public void onFailure(Throwable e) {
                if (advertisingImage != null) {
                    advertisingImage.setEnabled(false);
                }
                openHomeActivity();
            }

            @Override
            public void onCompleted() {

            }
        });
    }

    //是否登陆过
    public boolean isLogin() {
        token = (String) SPUtils.get(mContext, "token", "");
        return !TextUtils.isEmpty(token);
    }

    public void ExtendToken() {
        ApiImpl.getInstance().ExtendToken(new CallBack<User>() {
            @Override
            public void onSuccess(User user) {
                if (user.isSucess()) {
                    User.UserDetail userDetail = user.getData();
                    SPUtils.put(mContext, "token", userDetail.getToken());
                    Object object = SPUtils.readObject(SplashActivity.this, SPUtils.KEY_OF_USER_INFO, new User.UserDetail());
                    if (object instanceof User.UserDetail) {
                        AiCameraApplication.userDetail = (User.UserDetail) object;
                        AiCameraApplication.userDetail.setToken(userDetail.getToken());
                        SPUtils.writeObject(SplashActivity.this, SPUtils.KEY_OF_USER_INFO, AiCameraApplication.userDetail);
                    }
                    Gson gson = new Gson();
                    String detail = gson.toJson(userDetail);
                    SPUtils.put(mContext, "user", detail);
                } else {
                    SPUtils.unLogin(mContext);
                }
            }

            @Override
            public void onFailure(Throwable e) {

            }

            @Override
            public void onCompleted() {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (countDownTimer == null)
            countDownTimer = new CountDownTimer(ADVERTISING_TIME, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    int time = (int) ((millisUntilFinished / 1000) + 1);
                    timeTextView.setText(time + " ");
                }

                @Override
                public void onFinish() {
                    openHomeActivity();
                }
            };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer.onFinish();
            countDownTimer = null;
        }
    }

    /**
     * 开始开屏广告
     */
    private void startSplashAdvertising() {
        if (advertisingImage != null) {
            //加载图片
            GlideUtils.GlideLoaderAdvertising(getApplicationContext(), splashScreenBean.data.get(0).imageUrl, advertisingImage);
            //3秒的倒计时.
            countDownTimer.start();
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.advertisingImage:
                if (splashScreenBean == null) {
                    return;
                }
                StatisticOneKeyMakeVideo.getInstance().setOnEvent("SplashScreen_Ad_Open", getValue());
                countDownTimer.cancel();
                isFirstHomeActivity = true;
                H5BasePageActivity.openActivity(this, splashScreenBean.data.get(0).link, AdvertisingActivity.class);
                finish();
                break;
            case R.id.advertisingTime:
                countDownTimer.cancel();
                openHomeActivity();
                break;
        }
    }

    private String getValue() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title", splashScreenBean.data.get(0).title);
            jsonObject.put("link", splashScreenBean.data.get(0).link);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    private void openHomeActivity() {
        if (isFirstHomeActivity) {
            return;
        }
        isFirstHomeActivity = true;
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @Override
    public void connectedUSB() {
        if (!isFirstHomeActivity) {
            isFirstHomeActivity = true;
            countDownTimer.cancel();
            finish();
        }
        super.connectedUSB();
    }
}
