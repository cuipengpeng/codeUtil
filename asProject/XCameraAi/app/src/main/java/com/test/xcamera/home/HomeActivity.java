package com.test.xcamera.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.BuildConfig;
import com.test.xcamera.R;
import com.test.xcamera.accrssory.AccessoryManager;
import com.test.xcamera.activity.MoFPVActivity;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.base.StatisticHelper;
import com.test.xcamera.bean.User;
import com.test.xcamera.bean.VideoTemplete;
import com.test.xcamera.cameraclip.OneKeyMakeVideoHelper;
import com.test.xcamera.home.fragment.FeedFragment;
import com.test.xcamera.personal.MyFragment;
import com.test.xcamera.personal.PersonAgreeActivity;
import com.test.xcamera.phonealbum.AlbumActivity;
import com.test.xcamera.statistic.StatisticFloatLayer;
import com.test.xcamera.utils.AsyncCopyAssertRunnable;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.Constants;
import com.test.xcamera.utils.DownloadWebFileUtil;
import com.test.xcamera.utils.PermissionUtils;
import com.test.xcamera.utils.SPUtils;
import com.test.xcamera.view.basedialog.dialog.UserPrivacyDialog;
import com.test.xcamera.watermark.controller.MarkerController;
import com.moxiang.common.crash.LogcatHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by mz on 2019/7/2.
 */
public class HomeActivity extends MOBaseActivity implements UserPrivacyDialog.PrivacyClickListaner {
    @BindView(R.id.tv_home_page)
    ImageView tvHomePage;
    @BindView(R.id.tv_add)
    ImageView tvAdd;
    @BindView(R.id.tv_personal)
    ImageView tvPersonal;
    @BindView(R.id.frame_layout)
    FrameLayout frameLayout;

    private MyFragment personFragment;
    private FeedFragment feedFragment;
    private FragmentManager fragmentManager;
    public static final int MY_FRAGMENT = 1001;
    public static final int FEED_FRAGMENT = 1002;
    String[] PERMISSIONS = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public final int ALBUM_PERMISSIONS_CODE = 3000;
    private ActivationHelper activationHelper;
    private Handler mHandler = new Handler();
    //上次点击返回键的时间
    private long lastBackPressed;
    //两次点击的间隔时间
    private static final int QUIT_INTERVAL = 2000;
    private UserPrivacyDialog userPrivacyDialog;
    private boolean isShowAppUpdataDialog = false;

    @OnClick({R.id.tv_home_page, R.id.tv_add, R.id.tv_personal, R.id.rl_homeActivity_bottom})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_iv_title1:
                startAct(MoFPVActivity.class);
                break;
            case R.id.rl_homeActivity_bottom:
                break;
            case R.id.tv_left_title:
                break;
            case R.id.right_tv_titlee:
                break;
            case R.id.tv_home_page:
                showFragment(FEED_FRAGMENT);
                break;
            case R.id.tv_add:
                Intent intent = new Intent(this, GoUpActivity.class);
                intent.putExtra(GoUpActivity.JUMP_FLAG, true);
                startActivity(intent);
                break;
            case R.id.tv_personal:
                showFragment(MY_FRAGMENT);
                break;
        }
    }

    @Override
    public int initView() {
        return R.layout.activity_home;
    }

    @Override
    public void initData() {
        makeStorageDirs();
        copyAssertFiles(this);
        Object object = SPUtils.readObject(this, SPUtils.KEY_OF_USER_INFO, new User.UserDetail());
        if (object instanceof User.UserDetail) {
            AiCameraApplication.userDetail = (User.UserDetail) object;
        }

        initFragment();
        activationHelper = new ActivationHelper(this);
    }

    private void initFragment() {
        fragmentManager = getSupportFragmentManager();
        feedFragment = new FeedFragment();
        personFragment = new MyFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frame_layout, feedFragment);
        fragmentTransaction.add(R.id.frame_layout, personFragment);
        fragmentTransaction.commit();

        showFragment(FEED_FRAGMENT);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (userPrivacyDialog != null) {
            userPrivacyDialog.dismissDialog();
            userPrivacyDialog = null;
        }
        isShowAppUpdataDialog = false;
    }

    private void showPrivacyDialog() {
        String privacy_flag = (String) SPUtils.get(getBaseContext(), "privacy_flag", "");
        if (TextUtils.isEmpty(privacy_flag)) {
            privacyDialog();
        }
    }

    private void privacyDialog() {
        if (userPrivacyDialog == null)
            userPrivacyDialog = new UserPrivacyDialog(this, this);
        userPrivacyDialog.show();
    }

    public static void copyAssertFiles(Context context) {
        List<VideoTemplete> videoTempleteList = new ArrayList<>();
        OneKeyMakeVideoHelper.setMakeVideoType(OneKeyMakeVideoHelper.MakeVideoType.TODAY_WONDERFUL);
        videoTempleteList.addAll(OneKeyMakeVideoHelper.convertTempleteJsonToBean(context));
        OneKeyMakeVideoHelper.setMakeVideoType(OneKeyMakeVideoHelper.MakeVideoType.SIDE_KEY);
        videoTempleteList.addAll(OneKeyMakeVideoHelper.convertTempleteJsonToBean(context));
        for (VideoTemplete videoTemplete : videoTempleteList) {
            DownloadWebFileUtil.getInstance().execute(new AsyncCopyAssertRunnable(context, Constants.template_path + videoTemplete.getExample_video()));
        }
    }

    private void checkAppVersion() {
        activationHelper.setHandler(mHandler);
        activationHelper.checkAppVersion(BuildConfig.VERSION_NAME);
        //umeng
        if (Build.VERSION.SDK_INT >= 23) {
            String[] mPermissionList = new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_EXTERNAL_STORAGE};
            this.requestPermissions(mPermissionList, 123);
        }
    }

    public void showFragment(int id) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(feedFragment);
        fragmentTransaction.hide(personFragment);
        switch (id) {
            case FEED_FRAGMENT:
                fragmentTransaction.show(feedFragment);
                tvHomePage.setAlpha(1.0f);
                tvPersonal.setAlpha(0.3f);
                break;
            case MY_FRAGMENT:
                StatisticFloatLayer.getInstance().setOnEvent(StatisticFloatLayer.Home_PersonalCenter);
                fragmentTransaction.show(personFragment);
                tvHomePage.setAlpha(0.3f);
                tvPersonal.setAlpha(1.0f);
                break;
        }
        fragmentTransaction.commit();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }


    @Override
    protected void onResume() {
        super.onResume();
        StatisticHelper.onResume(this);

        String privacy_flag = (String) SPUtils.get(getBaseContext(), "privacy_flag", "");
        if (!TextUtils.isEmpty(privacy_flag) && !isShowAppUpdataDialog) {
            Log.i("UP_TEST_LOG", "onResume: 升级提示框");
            isShowAppUpdataDialog = true;
            checkAppVersion();
        }
        AccessoryManager.getInstance().setConnectStateListener(this, "HomeActivity");

        showPrivacyDialog();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (activationHelper != null)
            activationHelper.hideDlg();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ALBUM_PERMISSIONS_CODE:
                PermissionUtils.onRequestMorePermissionsResult(mContext, PERMISSIONS, new PermissionUtils.PermissionCheckCallBack() {
                    @Override
                    public void onHasPermission() {
                        startAct(AlbumActivity.class);
                    }

                    @Override
                    public void onUserHasAlreadyTurnedDown(String... permission) {
                        CameraToastUtil.show("需要" + Arrays.toString(permission) + "权限", mContext);
                    }

                    @Override
                    public void onUserHasAlreadyTurnedDownAndDontAsk(String... permission) {
                        CameraToastUtil.show("需要" + Arrays.toString(permission) + "权限", mContext);
                    }
                });
        }
    }

    public static void makeStorageDirs() {
        new Thread() {
            @Override
            public void run() {
                File cacheDir = new File(Constants.sampleVideoLocalPath);
                File myGalleryDir = new File(Constants.myGalleryLocalPath);
                File appDir = new File(Constants.appLocalPath);
                File waterDir = new File(Constants.dyGalleryLocalPath);

                File markPngAndVideo = new File(Constants.markPngAndVideo);

                if (!cacheDir.exists()) {
                    cacheDir.mkdirs();
                }
                if (!myGalleryDir.exists()) {
                    myGalleryDir.mkdirs();
                }
                if (!appDir.exists()) {
                    appDir.mkdirs();
                }
                if (!waterDir.exists()) {
                    waterDir.mkdirs();
                }
                if (!markPngAndVideo.exists()) {
                    markPngAndVideo.mkdirs();
                }

                //创建好文件夹,把assets文件里面的内容拷贝到sd卡中
                MarkerController.getInstance().copyTailVideo();
            }
        }.start();
    }

    /**
     * 双击返回
     */
    private void onBack() {
        long backPressed = System.currentTimeMillis();
        if (backPressed - lastBackPressed > QUIT_INTERVAL) {
            lastBackPressed = backPressed;
            CameraToastUtil.show(getResources().getString(R.string.exit_app), mContext);
        } else {
            super.onBackPressed();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            onBack();
        return false;
    }


    @Override
    public void connectedUSB() {
        super.connectedUSB();
        int pkgType = (int) SPUtils.get(this, ActivationHelper.PKG_TYPE, 0);
        if (pkgType != 3) {
            GoUpActivity.startGoUpActivity(this);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogcatHelper.getInstance(this).stop();
    }

    @Override
    public void clickView(int flag) {
        Intent intent = new Intent(this, PersonAgreeActivity.class);
        intent.putExtra("param", flag);
        startActivityForResult(intent, 100);
    }

    @Override
    public void consent() {
        //点击同意
        checkAppVersion();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == 100) {
            showPrivacyDialog();
        }
    }
}
