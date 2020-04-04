package com.caishi.chaoge.ui.activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.caishi.chaoge.R;
import com.caishi.chaoge.bean.EventBusBean.IssueBean;
import com.caishi.chaoge.bean.UpAppBean;
import com.caishi.chaoge.http.RequestURL;
import com.caishi.chaoge.ui.fragment.HomeFragment;
import com.caishi.chaoge.ui.fragment.MyFragment;
import com.caishi.chaoge.ui.widget.MyCountDownTimer;
import com.caishi.chaoge.ui.widget.dialog.BaseDialog;
import com.caishi.chaoge.ui.widget.dialog.IDialog;
import com.caishi.chaoge.ui.widget.dialog.SYDialog;
import com.caishi.chaoge.utils.ConstantUtils;
import com.caishi.chaoge.utils.DisplayMetricsUtil;
import com.caishi.chaoge.utils.EasyBlur;
import com.caishi.chaoge.utils.LogUtil;
import com.caishi.chaoge.utils.SPUtils;
import com.caishi.chaoge.utils.UpdateAppHttpUtil;
import com.caishi.chaoge.utils.Utils;
import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.UpdateCallback;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.caishi.chaoge.utils.ConstantUtils.ISFIRSTLAUNCH;
import static com.caishi.chaoge.utils.ConstantUtils.IS_NEW_VERSION;

public class MainActivity extends RxAppCompatActivity {
    @BindView(R.id.fl_mainActivity_content)
    FrameLayout flMainActivityLayout;
    @BindView(R.id.tv_mainActivity_home)
    TextView homeTextView;
    @BindView(R.id.rl_mainActivity_home)
    RelativeLayout rlMainActivityHome;
    @BindView(R.id.btn_mainActivity_issue)
    Button btnMainActivityIssue;
    @BindView(R.id.tv_mainActivity_mine)
    TextView mineTextView;
    @BindView(R.id.rl_mainActivity_mine)
    RelativeLayout rlMainActivityMine;
    @BindView(R.id.ll_mainActivity_bottomMenu)
    LinearLayout bottomMenuLinearLayout;

    private FragmentManager mFragmentManager;
    private HomeFragment homeFragment;
    private MyFragment myFragment;
    public static final int SHOW_HOME_FRAGMENT = 101;
    public static final int SHOW_MY_FRAGMENT = 102;
    private int mCurrentFragment = -1;
    public static final String KEY_OF_FRAGMENT = "fragmentKey";
    public static final String KEY_OF_UPLOAD = "uploadKey";
    private boolean mUpload = false;
    public static final String KEY_OF_UPLOAD_DATA = "uploadDataKey";
    private long mPressedTime = 0;
    private IssueBean mIssueBean;
    private SYDialog wayDialog;
    private ImageView img_dialogWay_read;
    private ImageView img_dialogWay_dub;

    @OnClick({R.id.rl_mainActivity_home, R.id.btn_mainActivity_issue, R.id.rl_mainActivity_mine})
    public void OnClickView(View v) {
        switch (v.getId()) {
            case R.id.rl_mainActivity_home:
                if (mCurrentFragment != SHOW_HOME_FRAGMENT) {
                    showFragment(SHOW_HOME_FRAGMENT);
                }
                break;
            case R.id.btn_mainActivity_issue:
//                wayDialog = new SYDialog.Builder(MainActivity.this)
//                        .setDialogView(R.layout.dialog_way)
//                        .setScreenWidthP(1f)
//                        .setScreenHeightP(1f)
////                        .setHeight(DisplayMetricsUtil.getScreenHeight(MainActivity.this) - ImmersionBar.getStatusBarHeight(this))
//                        .setGravity(Gravity.BOTTOM)
//                        .setCancelable(true)
//                        .setWindowBackgroundP(0.8f)
//                        .setCancelableOutSide(false)
//                        .setBuildChildListener(new IDialog.OnBuildListener() {
//                            @Override
//                            public void onBuildChildView(final IDialog dialog, View view, int layoutRes, final FragmentManager fragmentManager) {
//                                Utils.umengStatistics(MainActivity.this, "lz0001");//单击录制
//                                ImmersionBar.with(wayDialog).transparentStatusBar().init();
//                                homeFragment.pauseVideo();
//                                ImageView img_dialogWay_bg = view.findViewById(R.id.img_dialogWay_bg);
//                                img_dialogWay_read = view.findViewById(R.id.img_dialogWay_read);
//                                img_dialogWay_dub = view.findViewById(R.id.img_dialogWay_dub);
//                                ImageView img_dialogWay_close = view.findViewById(R.id.img_dialogWay_close);
//                                Bitmap finalBitmap = EasyBlur.with(MainActivity.this)
//                                        .bitmap(BitmapFactory.decodeResource(getResources(), R.drawable.im_bg_dim)) //要模糊的图片
//                                        .radius(10)//模糊半径
//                                        .blur();
//                                img_dialogWay_bg.setImageBitmap(finalBitmap);
//                                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img_dialogWay_bg, "alpha", 0f, 0.92f);
//                                objectAnimator.setDuration(300);
//                                objectAnimator.start();
//                                new MyCountDownTimer(100, 100) {
//                                    @Override
//                                    public void onFinish() {
//                                        img_dialogWay_read.setVisibility(View.VISIBLE);
//                                        img_dialogWay_dub.setVisibility(View.VISIBLE);
//                                        startAnimator(img_dialogWay_read, img_dialogWay_dub);
//                                    }
//                                }.start();
//                                img_dialogWay_close.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        closeAnimator(img_dialogWay_read, img_dialogWay_dub);
//                                    }
//                                });
//                                img_dialogWay_dub.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        new MyCountDownTimer(100, 100) {
//                                            @Override
//                                            public void onFinish() {
//                                                Utils.umengStatistics(MainActivity.this, "lz0003");//视频配音
//                                                startActivity(new Intent(MainActivity.this, TemplateActivity.class));
//                                            }
//                                        }.start();
//                                        closeAnimator(img_dialogWay_read, img_dialogWay_dub);
//                                    }
//                                });
//                                img_dialogWay_read.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        new MyCountDownTimer(100, 100) {
//                                            @Override
//                                            public void onFinish() {
                                                ISFIRSTLAUNCH = true;
                                                Utils.umengStatistics(MainActivity.this, "lz0002"); //文字朗读
                                                startActivity(new Intent(MainActivity.this, RecordActivity.class));
//                                            }
//                                        }.start();
//                                        closeAnimator(img_dialogWay_read, img_dialogWay_dub);
//                                    }
//                                });
//
//                            }
//                        }).show();
//
//                wayDialog.setOnBackListener(new BaseDialog.OnBackListener() {//dialog时返回按钮监听
//                    @Override
//                    public void onBack(boolean isBack) {
//                        if (isBack) {
//                            if (null != wayDialog) {
//                                if (null != img_dialogWay_read && null != img_dialogWay_dub)
//                                    closeAnimator(img_dialogWay_read, img_dialogWay_dub);
//                                else
//                                    wayDialog.dismiss();
//                            }
//                        }
//                    }
//                });
                break;
            case R.id.rl_mainActivity_mine:
                if (!SPUtils.isLogin(this)) {
                    LoginActivity.open(this, MainActivity.SHOW_MY_FRAGMENT);
                    return;
                }

                if (mCurrentFragment != SHOW_MY_FRAGMENT) {
                    showFragment(SHOW_MY_FRAGMENT);
                }
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ImmersionBar.with(this).transparentStatusBar().init();
        SPUtils spUtils = new SPUtils(this, "FirstLaunch");
        boolean isFirstLaunch = (boolean) spUtils.get("isFirstLaunch", true);
        if (isFirstLaunch)
            spUtils.put("isFirstLaunch", false);
        IS_NEW_VERSION = isFirstLaunch;
        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        homeFragment = new HomeFragment();
        myFragment = new MyFragment();
        fragmentTransaction.add(R.id.fl_mainActivity_content, homeFragment);
        fragmentTransaction.add(R.id.fl_mainActivity_content, myFragment);
        fragmentTransaction.commit();
        int fragment = getIntent().getIntExtra(KEY_OF_FRAGMENT, SHOW_HOME_FRAGMENT);
        showFragment(fragment);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int fragment = intent.getIntExtra(KEY_OF_FRAGMENT, SHOW_HOME_FRAGMENT);
        mUpload = intent.getBooleanExtra(KEY_OF_UPLOAD, false);
        mIssueBean = (IssueBean) intent.getSerializableExtra(KEY_OF_UPLOAD_DATA);
        showFragment(fragment);
    }

    private void showFragment(int position) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.hide(homeFragment);
        fragmentTransaction.hide(myFragment);
        if (position == SHOW_HOME_FRAGMENT) {
            mCurrentFragment = SHOW_HOME_FRAGMENT;
            fragmentTransaction.show(homeFragment);
            bottomMenuLinearLayout.setBackgroundColor(Color.TRANSPARENT);
            homeTextView.setTextColor(Color.parseColor("#FE5175"));
            mineTextView.setTextColor(Color.parseColor("#ffffff"));
        } else if (position == SHOW_MY_FRAGMENT) {
            mCurrentFragment = SHOW_MY_FRAGMENT;
            myFragment.mUploadVideo = mUpload;
            if (mUpload) {
                myFragment.issueBean = mIssueBean;
            }
            fragmentTransaction.show(myFragment);
            bottomMenuLinearLayout.setBackgroundColor(Color.WHITE);
            mineTextView.setTextColor(Color.parseColor("#FE5175"));
            homeTextView.setTextColor(Color.parseColor("#42000000"));
        }
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        upDataAPP();

        if (!SPUtils.isLogin(this)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SettingActivity.deleteAllFiles(new File(Environment.getExternalStorageDirectory().getPath() + "/" + ConstantUtils.FILE_BASE_PATH));
                }
            }).start();
        }
    }

    private void startAnimator(final ImageView img_dialogWay_read,
                               final ImageView img_dialogWay_dub) {
        final int screenHeight = DisplayMetricsUtil.getScreenHeight(MainActivity.this);
        final int measuredHeight = (int) getResources().getDimension(R.dimen._180dp);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img_dialogWay_read, "translationY",
                screenHeight, screenHeight - measuredHeight);
        objectAnimator.setDuration(200);
        objectAnimator.start();
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(img_dialogWay_dub, "translationY",
                screenHeight, screenHeight - measuredHeight);
        objectAnimator1.setDuration(300);
        objectAnimator1.start();
    }

    private void closeAnimator(final ImageView img_dialogWay_read,
                               final ImageView img_dialogWay_dub) {
        final int screenHeight = DisplayMetricsUtil.getScreenHeight(MainActivity.this);
        final int measuredHeight = (int) getResources().getDimension(R.dimen._180dp);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(img_dialogWay_read, "translationY",
                screenHeight - measuredHeight, screenHeight);
        objectAnimator.setDuration(200);
        objectAnimator.start();
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(img_dialogWay_dub, "translationY",
                screenHeight - measuredHeight, screenHeight);
        objectAnimator1.setDuration(300);
        objectAnimator1.start();
        new MyCountDownTimer(300, 300) {
            @Override
            public void onFinish() {
                if (null != wayDialog)
                    wayDialog.dismiss();
            }
        }.start();
    }

    private void upDataAPP() {
        XXPermissions.with(this)
                //.constantRequest() //可设置被拒绝后继续申请，直到用户授权或者永久拒绝
                //.permission(Permission.SYSTEM_ALERT_WINDOW, Permission.REQUEST_INSTALL_PACKAGES) //支持请求6.0悬浮窗权限8.0请求安装权限
                .permission(Permission.RECORD_AUDIO, Permission.WRITE_EXTERNAL_STORAGE, Permission.ACCESS_FINE_LOCATION)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                    }
                });

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ChaoGe/";
        Map<String, String> params = new HashMap<>();
        params.put("appKey", "ab55ce55Ac4bcP408cPb8c1Aaeac179c5f6f");
        new UpdateAppManager
                .Builder()
                //必须设置，当前Activity
                .setActivity(this)
                //必须设置，实现httpManager接口的对象
                .setHttpManager(new UpdateAppHttpUtil())
                //必须设置，更新地址
                .setUpdateUrl(RequestURL.BASE_URL + "/" + RequestURL.APP_VERSION)
                //添加自定义参数，默认version=1.0.0（app的versionName）；apkKey=唯一表示（在AndroidManifest.xml配置）
                .setParams(params)
                //设置点击升级后，消失对话框，默认点击升级后，对话框显示下载进度
                // .hideDialogOnDownloading()
                //设置头部，不设置显示默认的图片，设置图片后自动识别主色调，然后为按钮，进度条设置颜色
                .setTopPic(R.mipmap.lib_update_app_top_bg)
                //为按钮，进度条设置颜色，默认从顶部图片自动识别。
                //.setThemeColor(ColorUtil.getRandomColor())
                //设置apk下砸路径，默认是在下载到sd卡下/Download/1.0.0/test.apk
                .setTargetPath(path)
                //设置appKey，默认从AndroidManifest.xml获取，如果，使用自定义参数，则此项无效
                //.setAppKey("ab55ce55Ac4bcP408cPb8c1Aaeac179c5f6f")
                //不显示通知栏进度条
                .dismissNotificationProgress()
                //是否忽略版本
                //.showIgnoreVersion()
                .build()
                //检测是否有新版本
                .checkNewApp(new UpdateCallback() {
                    /**
                     * 解析json,自定义协议
                     *
                     * @param json 服务器返回的json
                     * @return UpdateAppBean
                     */
                    @Override
                    protected UpdateAppBean parseJson(String json) {
                        LogUtil.i(json);
                        UpdateAppBean updateAppBean = new UpdateAppBean();
                        Gson gson = new Gson();
                        UpAppBean upAppBean = gson.fromJson(json, UpAppBean.class);
                        if (null == upAppBean.data)
                            return updateAppBean;
                        updateAppBean
                                //（必须）是否更新Yes,No
                                .setUpdate(upAppBean.data.result == null ? "No" : "Yes")
                                //（必须）新版本号，
                                .setNewVersion(upAppBean.data.result.version)
                                //（必须）下载地址
                                .setApkFileUrl(upAppBean.data.result.url)
                                //（必须）更新内容
                                .setUpdateLog(upAppBean.data.result.desc)
                                //大小，不设置不显示大小，可以不设置
//                                    .setTargetSize()
                                //是否强制更新，可以不设置
                                .setConstraint(upAppBean.data.result.force);
                        //设置md5，可以不设置
//                                    .setNewMd5(jsonObject.optString("new_md51"));
                        return updateAppBean;
                    }

                    /**
                     * 网络请求之前
                     */
                    @Override
                    public void onBefore() {
                        // CProgressDialogUtils.showProgressDialog(JavaActivity.this);
                    }

                    /**
                     * 网路请求之后
                     */
                    @Override
                    public void onAfter() {
                        // CProgressDialogUtils.cancelProgressDialog(JavaActivity.this);
                    }

                    @Override
                    protected void noNewApp(String error) {
                        super.noNewApp(error);
                    }
                });

    }

    @Override
    public void onBackPressed() {
        long mNowTime = System.currentTimeMillis();
        if ((mNowTime - mPressedTime) > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mPressedTime = mNowTime;
        } else {
            this.finish();
            System.exit(0);
        }
    }


    /**
     * @param context
     */
    public static void open(Context context, IssueBean issueBean) {
        open(context, MainActivity.SHOW_MY_FRAGMENT, issueBean);
    }

    public static void open(Context context, int fragment) {
        open(context, fragment, null);
    }

    /**
     * @param context
     * @param fragment SHOW_HOME_FRAGMENT  SHOW_MY_FRAGMENT
     */
    public static void open(Context context, int fragment, IssueBean issueBean) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(KEY_OF_FRAGMENT, fragment);
        if (issueBean == null) {
            intent.putExtra(KEY_OF_UPLOAD, false);
        } else {
            intent.putExtra(KEY_OF_UPLOAD, true);
            intent.putExtra(KEY_OF_UPLOAD_DATA, issueBean);
        }
        context.startActivity(intent);
    }
}
