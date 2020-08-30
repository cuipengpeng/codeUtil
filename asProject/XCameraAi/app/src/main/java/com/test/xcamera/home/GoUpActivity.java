package com.test.xcamera.home;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import com.framwork.base.adapter.BaseRecyclerAdapter;
import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;
import com.test.xcamera.accrssory.AccessoryManager;
import com.test.xcamera.accrssory.UsbDispose;
import com.test.xcamera.activity.DyFPVActivity;
import com.test.xcamera.activity.MoFPVActivity;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.AppVersion;
import com.test.xcamera.bean.CommunityBean;
import com.test.xcamera.bean.MoErrorData;
import com.test.xcamera.bean.SideKeyBean;
import com.test.xcamera.bean.VideoTemplete;
import com.test.xcamera.cameraclip.CameraClipEditActivity;
import com.test.xcamera.cameraclip.CameraSideClipActivity;
import com.test.xcamera.cameraclip.DownloadVideoTempleteDataUtil;
import com.test.xcamera.cameraclip.GuideConnectActivity;
import com.test.xcamera.cameraclip.NetVideoTempleteHelper;
import com.test.xcamera.cameraclip.NewerGuideActivity;
import com.test.xcamera.cameraclip.OneKeyMakeVideoHelper;
import com.test.xcamera.cameraclip.OneKeyMakeVideoXmlParser;
import com.test.xcamera.cameraclip.TodayVideoListActivity;
import com.test.xcamera.cameraclip.bean.VideoFile;
import com.test.xcamera.constants.LogcatConstants;
import com.test.xcamera.dymode.managers.EffectPlatform;
import com.test.xcamera.dymode.utils.NewCameraDisplay;
import com.test.xcamera.go_up.GoUpInterface;
import com.test.xcamera.go_up.GoUpPresenter;
import com.test.xcamera.h5_page.CommunityActivity;
import com.test.xcamera.h5_page.H5BasePageActivity;
import com.test.xcamera.home.adapter.GalleryPageAdapter;
import com.test.xcamera.home.adapter.ZoomOutPageTransformer;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.moalbum.activity.MyAlbumList;
import com.test.xcamera.mointerface.MoErrorCallback;
import com.test.xcamera.mointerface.MoGetSideKeyCallback;
import com.test.xcamera.ota.UploadHardWareActivity;
import com.test.xcamera.permission.RxPermissions;
import com.test.xcamera.personal.PersonAgreeActivity;
import com.test.xcamera.phonealbum.AlbumActivity;
import com.test.xcamera.statistic.StatisticFloatLayer;
import com.test.xcamera.statistic.StatisticOneKeyMakeVideo;
import com.test.xcamera.utils.AppExecutors;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.VersionCheck;
import com.test.xcamera.utils.Constants;
import com.test.xcamera.utils.DensityUtils;
import com.test.xcamera.utils.DlgUtils;
import com.test.xcamera.utils.DownLoadRequest;
import com.test.xcamera.utils.DownloadSingleCameraFile;
import com.test.xcamera.utils.DownloadWebFileUtil;
import com.test.xcamera.utils.LogAccessory;
import com.test.xcamera.utils.LoggerUtils;
import com.test.xcamera.utils.MoUtil;
import com.test.xcamera.utils.NetworkUtil;
import com.test.xcamera.utils.SPUtils;
import com.test.xcamera.utils.StandardLoadingDlg;
import com.test.xcamera.utils.StringUtil;
import com.test.xcamera.view.basedialog.dialog.CommonDownloadDialog;
import com.test.xcamera.view.basedialog.dialog.DialogVersionUpdataTips;
import com.test.xcamera.view.basedialog.dialog.UserPrivacyDialog;
import com.test.xcamera.widget.ActivityContainerHome;
import com.test.xcamera.widget.LogDataLayout;
import com.test.xcamera.widget.UploadDialog;
import com.moxiang.common.logging.Logcat;

import java.io.File;
import java.io.FileInputStream;
import java.lang.ref.SoftReference;
import java.net.URLDecoder;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by smz on 2019/11/15.
 */

public class GoUpActivity extends MOBaseActivity implements HomeViewInterface, UserPrivacyDialog.PrivacyClickListaner, GoUpInterface.PresenterToView {
    @BindView(R.id.rl_clip_video)
    RelativeLayout rlClipVideo;
    @BindView(R.id.rl_camera)
    RelativeLayout rlCamera;
    @BindView(R.id.rl_drafts)
    RelativeLayout rlDrafts;
    @BindView(R.id.tv_phone_album)
    TextView tvPhoneAlbum;
    @BindView(R.id.tv_carmera)
    TextView captureTextView;
    @BindView(R.id.rl_phone_album)
    RelativeLayout rlPhoneAlbum;
    @BindView(R.id.rl_material_import)
    RelativeLayout rlMaterialImport;
    @BindView(R.id.lin_1)
    LinearLayout lin1;
    @BindView(R.id.v_goUpActivity_colorBorder)
    View colorBorderView;
    @BindView(R.id.iv_go_up_back)
    ImageView ivGoUpBack;
    @BindView(R.id.rl_goUpActivity_placeHolder)
    RelativeLayout placeHolderRelativeLayout;
    @BindView(R.id.iv_goUpActivity_noDataBottom)
    ImageView noDataBottomImageView;
    @BindView(R.id.iv_goUpActivity_noDataTop)
    ImageView noDataTopImageView;
    @BindView(R.id.recy)
    ViewPager galleryViewPager;
    @BindView(R.id.progress1)
    ProgressBar progressBar;
    @BindView(R.id.tv_goupActivvity_loadingContent)
    TextView loadingContentTextView;
    @BindView(R.id.hardwareInfoTips)
    RelativeLayout hardwareInfoTips;
    @BindView(R.id.tv_feedFragment_hardwareInfo)
    TextView tv_feedFragment_hardwareInfo;
    @BindView(R.id.iv_feedFragment_deleteHardwareUpdate)
    ImageView iv_feedFragment_deleteHardwareUpdate;

    @BindView(R.id.usbFrame)
    public FrameLayout mUSBFrameTest;
    @BindView(R.id.communityHelp)
    LinearLayout communityHelp;


    public CommonDownloadDialog progressRelativeLayout;
    private volatile boolean initFlag = false;
    public final int ALBUM_PERMISSIONS_CODE = 3000;
    public static final String JUMP_FLAG = "jumpFlag";
    private boolean mJumpFlag;
    private DownloadVideoTempleteDataUtil downloadVideoTempleteDataUtil;
    private OneKeyMakeVideoHelper oneKeyMakeVideoHelper;
    private GalleryPageAdapter galleryPageAdapter;
    private long createdTime;
    String[] PERMISSIONS = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.WRITE_EXTERNAL_STORAGE};
    String[] DY_PERMISSIONS = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
    private final int PLACE_HOLDER_MODE_NO_DATA = 301;
    private final int PLACE_HOLDER_MODE_DISCONNECT = 302;
    private int placeHolderMode = PLACE_HOLDER_MODE_NO_DATA;
    private static NetVideoTempleteHelper netVideoTempleteHelper;
    private Handler mHandler = new Handler();
    private StandardLoadingDlg mStandardLoadingDlg = null;
    private volatile boolean mInitFinishFlag = false;
    private UserPrivacyDialog userPrivacyDialog;
    private VersionCheck checkVersionUtils;

    private boolean mResuming = false;
    private UsbDispose mUsbDispose = new UsbDispose();
    private Runnable mSyncTimeout = () -> {
        if (mResuming) {
            DlgUtils.toast(GoUpActivity.this, StringUtil.getStr(R.string.sync_param_err));
        }
    };
    private DialogVersionUpdataTips dialogVersionUpdataTips;
    private String hardwareVersion;
    private AppVersion.AppVersionDetail detail;
    private GoUpPresenter goUpPresenter;
    private CommunityBean communityBean;

    public static void startGoUpActivity(Context context) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, GoUpActivity.class);
        context.startActivity(intent);
    }

    @OnClick({R.id.rl_clip_video, R.id.rl_camera, R.id.rl_drafts, R.id.rl_phone_album, R.id.rl_material_import, R.id.iv_go_up_back,
            R.id.rl_goUpActivity_placeHolder, R.id.douyin, R.id.rl_progress, R.id.tv_feedFragment_hardwareInfo, R.id.iv_feedFragment_deleteHardwareUpdate
            , R.id.tv_feedFragment_hardwareUpdate, R.id.communityHelp})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_goUpActivity_placeHolder:
                if (placeHolderMode == PLACE_HOLDER_MODE_DISCONNECT) {
                    startAct(GuideConnectActivity.class);
                } else {
                    Toast.makeText(this, getResources().getString(R.string.startCaptionVideo), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.rl_clip_video:
                StatisticFloatLayer.getInstance().setOnEvent(StatisticFloatLayer.FloatLayer_TodayBrilliant);
                if (AccessoryManager.getInstance().mIsRunning) {
                    if (!syncParams())
                        break;
                    startAct(TodayVideoListActivity.class);
                } else {
                    startAct(GuideConnectActivity.class);
                }
                break;
            case R.id.rl_camera:
                StatisticFloatLayer.getInstance().setOnEvent(StatisticFloatLayer.FloatLayer_Capture);
                if (!AccessoryManager.getInstance().mIsRunning) {
                    startAct(GuideConnectActivity.class);
                    return;
                }

                if (!syncParams())
                    break;

                //TODO 新UI
                if (System.currentTimeMillis() - createdTime < 1000) {  //如果进入当前页面即开始fpv页面 会造成连接尚未完成而黑屏
                    if (AccessoryManager.getInstance().mIsRunning) {
                        Intent intent = new Intent(GoUpActivity.this, MoFPVActivity.class);
                        startActivity(intent);
                        this.finish();
                        break;
                    } else
                        DlgUtils.toast(GoUpActivity.this, getResources().getString(R.string.makeConnecting));
                } else {
                    Intent intent = new Intent(GoUpActivity.this, MoFPVActivity.class);
                    startActivity(intent);
                    this.finish();
                }
                break;
            case R.id.rl_drafts:
                break;
            case R.id.rl_phone_album:
                StatisticFloatLayer.getInstance().setOnEvent(StatisticFloatLayer.FloatLayer_MyAlbum);
                MyAlbumList.openThis(this, "go_up");
                break;
            case R.id.rl_material_import:
                StatisticFloatLayer.getInstance().setOnEvent(StatisticFloatLayer.FloatLayer_MaterialEdit);
                new RxPermissions(GoUpActivity.this)
                        .requestEachCombined(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .subscribe(permission -> {
                            if (permission.granted) {
                                startAct(AlbumActivity.class);
                            } else if (permission.shouldShowRequestPermissionRationale) {
                                DlgUtils.toast(GoUpActivity.this, getResources().getString(R.string.grantStoragePermission));
                            } else {
                                DlgUtils.toast(GoUpActivity.this, getResources().getString(R.string.grantStoragePermission));
                            }
                        });
                break;
            case R.id.iv_go_up_back:
                onBackPressed();
                break;
            case R.id.douyin:
                StatisticFloatLayer.getInstance().setOnEvent(StatisticFloatLayer.FloatLayer_DYCapture);
                if (NewCameraDisplay.mSurfaceDestoryed) {
                    requestDyFPVPermissions();
                }
                break;
            case R.id.tv_feedFragment_hardwareInfo:
            case R.id.tv_feedFragment_hardwareUpdate:
                StatisticOneKeyMakeVideo.getInstance().setOnEvent("Click_GoUpActivity_Tips_Check_Button", getPointValue(hardwareVersion, detail));
                startAct(UploadHardWareActivity.class);
                break;
            case R.id.iv_feedFragment_deleteHardwareUpdate:
//                if (checkVersionUtils != null && checkVersionUtils.getDetail() != null) {
//                    SPUtils.put(AiCameraApplication.getContext(), VersionCheck.CLOSE_KEY, checkVersionUtils.getDetail().getVersion());
//                }
                goneLayout();
                AiCameraApplication.isShowHardwareUpdateTips = true;
                break;
            case R.id.communityHelp:
                H5BasePageActivity.openActivity(this, communityBean.getData().getTargetUrl(), CommunityActivity.class);
                break;
        }
    }

    //同步相机初始状态
    private boolean syncParams() {
        if (!mInitFinishFlag) {
            DlgUtils.toast(this, getResources().getString(R.string.syncingCameraData));
            return false;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mResuming = false;

        ConnectionManager.getInstance().setErrorI(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mStandardLoadingDlg != null)
            mStandardLoadingDlg.clear();
        mStandardLoadingDlg = null;

        initFlag = false;
        mInitFinishFlag = false;

        if (userPrivacyDialog != null) {
            userPrivacyDialog.dismissDialog();
            userPrivacyDialog = null;
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Logcat.e().tag(LogcatConstants.FPV_PREVIEW).msg("==>GoUpActivity onCreate").out();
        HomeActivity.copyAssertFiles(this);
        dealIntent();
        super.onCreate(savedInstanceState);
        ActivityContainerHome.getInstance().finishAllActivity();
        ActivityContainerHome.getInstance().addActivity(this);
        new Handler().postDelayed(() -> {
            delTodayWonderfulCache();

            clearOldAct();
        }, 800);

        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                EffectPlatform.getInstance().initEffectList();
            }
        });
    }

    private void test() {
        ConnectionManager.getInstance().appFpvMode(1, null);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    private int re[] = {2, 3, 4};

                    @Override
                    public void run() {
                        ConnectionManager.getInstance().switchMode("video", null);

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        ConnectionManager.getInstance().startPreview(null);
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        int count = 0;
                        while (true) {
                            try {
                                if (count == 3)
                                    count = 0;

                                Log.e("=====", "==>send suc");
                                Thread.sleep(1500);
                                ConnectionManager.getInstance().stopPreview(null);
                                Thread.sleep(1000);
                                ConnectionManager.getInstance().takeVideoSetting(re[count++], 2, null);

                                Thread.sleep(1000);
                                ConnectionManager.getInstance().startPreview(null);

                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        }, 500);


    }

    private void initMsg() {
        mHandler.postDelayed(mSyncTimeout, 5000);
        mUsbDispose.setSyncStatus(new UsbDispose.SyncStatus() {
            @Override
            public void onSyncSucc() {
                getVideoSideKey();
            }

            @Override
            public void onSyncFailed() {
                getVideoSideKey();
            }

            @Override
            public void onSyncStart() {
                if (mResuming) {
                    runOnUiThread(() -> {
                        if (mStandardLoadingDlg != null)
                            mStandardLoadingDlg.show(GoUpActivity.this, 0);
                    });
                }
            }

            @Override
            public void onProgress() {
                if (mHandler != null)
                    mHandler.removeCallbacks(mSyncTimeout);
            }
        });

        mUsbDispose.dispose();
    }

    //usb连接会弹出新Act 需要移除之前的 保证逻辑正确性
    private void clearOldAct() {
        for (SoftReference<Activity> act : AiCameraApplication.mApplication.mGoUpAct) {
            if (act != null && act.get() != null && act.get() != this)
                act.get().finish();
        }
        AiCameraApplication.mApplication.mGoUpAct.clear();

        AiCameraApplication.mApplication.mGoUpAct.add(new SoftReference<Activity>(GoUpActivity.this));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Logcat.e().tag(LogcatConstants.FPV_PREVIEW).msg("==>GoUpActivity onNewIntent").out();
        OneKeyMakeVideoHelper.setMakeVideoType(OneKeyMakeVideoHelper.MakeVideoType.SIDE_KEY);
        dealIntent();
//        getVideoSideKey();
        super.onNewIntent(intent);
    }

    private void dealIntent() {
        File file = new File(Constants.mFileDirector);
        if (!file.exists()) {
            file.mkdirs();
        }
        HomeActivity.makeStorageDirs();
        overridePendingTransition(R.anim.push_bottom_in, R.anim.push_slient);
        setTitleStatus(true);
        noStatusBar();

        if (getIntent() != null) {
            mJumpFlag = getIntent().getBooleanExtra(JUMP_FLAG, false);
        }
    }

    @Override
    public int initView() {
        return R.layout.activity_go_up;
    }

    @Override
    public void initData() {
        newerGuideGoUp();

        /*测试相机连接专用*/
        if (LogAccessory.getInstance().isIsShowViewLog()) {
            LogDataLayout logDataLayout = new LogDataLayout(this);
            logDataLayout.setCallBack(new LogDataLayout.LogViewCallBack() {
                @Override
                public void onCallBack() {
                    mUSBFrameTest.removeAllViews();
                    mUSBFrameTest.setVisibility(View.GONE);
                }
            });
            mUSBFrameTest.removeAllViews();
            mUSBFrameTest.addView(logDataLayout);
            mUSBFrameTest.setVisibility(View.VISIBLE);
        } else {
            mUSBFrameTest.setVisibility(View.GONE);
        }

        progressRelativeLayout = new CommonDownloadDialog(this);
        progressRelativeLayout.setBackKeyListener(new CommonDownloadDialog.BackKeyListener() {
            @Override
            public void onBack(Dialog mDialog) {
                onBackPressed();
            }
        });
        HomeActivity.copyAssertFiles(this);
        netVideoTempleteHelper = new NetVideoTempleteHelper();
        oneKeyMakeVideoHelper = new OneKeyMakeVideoHelper(this, OneKeyMakeVideoHelper.MakeVideoType.SIDE_KEY);
        downloadVideoTempleteDataUtil = new DownloadVideoTempleteDataUtil();

        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.color_ff7700), PorterDuff.Mode.SRC_IN);
        AccessoryManager.getInstance();
        galleryPageAdapter = new GalleryPageAdapter(mContext);
        galleryViewPager.setAdapter(galleryPageAdapter);
        galleryViewPager.setPageMargin(DensityUtils.dp2px(this, 10));
        galleryViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        galleryPageAdapter.setOnRecyclerItemClickListener(new BaseRecyclerAdapter.OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                StatisticFloatLayer.getInstance().setOnEvent(StatisticFloatLayer.FloatLayer_SideKey_OneClickVideo);
                if (position < (GalleryPageAdapter.MAX_ITEM_COUNT - 1)) {
                    SideKeyBean sideKeyBean = galleryPageAdapter.mDataList.get(position);
                    downloadSideKeyItem(mContext, sideKeyBean, downloadVideoTempleteDataUtil, oneKeyMakeVideoHelper, progressRelativeLayout, loadingContentTextView);
                } else {
                    Intent intent = new Intent(mContext, CameraSideClipActivity.class);
                    mContext.startActivity(intent);
                }
            }
        });
        DownLoadRequest.removeCurDownLoadError(mContext);

        goUpPresenter = new GoUpPresenter(this);
    }

    /**
     * 新手引导浮层页面
     */
    public void newerGuideGoUp() {
        boolean hasGuideGoup = getSharedPreferences(SPUtils.FILE_NAME, Context.MODE_PRIVATE).getBoolean(NewerGuideActivity.KEY_OF_GOUP, false);
        if (!hasGuideGoup && AccessoryManager.getInstance().mIsRunning) {
            NewerGuideActivity.open(this, NewerGuideActivity.NEWER_GUIDE_GOUP);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mStandardLoadingDlg == null)
            mStandardLoadingDlg = new StandardLoadingDlg();
//        initFlag = false;
        mInitFinishFlag = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        goUpPresenter.getCommunityIsHide();

        ConnectionManager.getInstance().setErrorI(new MoErrorCallback() {
            @Override
            public void onError(MoErrorData data) {
                if (data.event == MoErrorCallback.SD_EVENT) {
                    switch (data.status) {
                        case MoErrorCallback.SD_OUT:
                            getVideoSideKey();
                            DlgUtils.toast(GoUpActivity.this, getResources().getString(R.string.sdcard_out));
                            break;
                        case MoErrorCallback.SD_IN:
                            getVideoSideKey();
                            break;
                    }
                }
                mUsbDispose.syncLE(data.event);
            }
        });

        if (!AccessoryManager.getInstance().mIsRunning) {
            showDisconnectView();
        }

        mResuming = true;

        Logcat.e().tag(LogcatConstants.FPV_PREVIEW).msg("GoUpActivity  onResume==>initMsg() flag:" + initFlag + "  running:" + AccessoryManager.getInstance().mIsRunning).out();
        if (!initFlag && AccessoryManager.getInstance().mIsRunning) {
            initFlag = true;
            initMsg();
        }
      /*if (getIntent() != null && !AccessoryManager.getInstance().mIsRunning) {
            UsbAccessory accessory = getIntent().getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
            Debug.debugUsb("goup acc==>" + getIntent().getAction());
            if (accessory != null&&!AccessoryManager.getInstance().mIsRunning) {
                AccessoryManager.getInstance().mCommunicator.onReceive(this, getIntent());
            }
        }*/
        OneKeyMakeVideoHelper.setMakeVideoType(OneKeyMakeVideoHelper.MakeVideoType.SIDE_KEY);
        createdTime = System.currentTimeMillis();
//        getVideoSideKey();
        if (checkVersionUtils == null)
            checkVersionUtils = new VersionCheck(this, getSupportFragmentManager(), mHandler);

        showPrivacyDialog();
    }

    /**
     * 显示权限弹框,和升级弹框,权限弹框的优先级高于,升级弹框
     */
    private void showPrivacyDialog() {
        String privacy_flag = (String) SPUtils.get(getBaseContext(), "privacy_flag", "");
        if (TextUtils.isEmpty(privacy_flag)) {
            privacyDialog();
        } else {
            //TODO 权限框已经弹过了,直接弹出估计升级框就行
            if (checkVersionUtils != null)
                checkVersionUtils.getDevicedInfo();
        }
    }

    private void privacyDialog() {
        if (userPrivacyDialog == null)
            userPrivacyDialog = new UserPrivacyDialog(this, this);
        userPrivacyDialog.show();
    }

    public static void downloadSideKeyItem(Context context, SideKeyBean sideKeyBean, DownloadVideoTempleteDataUtil downloadVideoTempleteDataUtil, OneKeyMakeVideoHelper oneKeyMakeVideoHelper, CommonDownloadDialog progressRelativeLayout, TextView loadingContent) {
        LoggerUtils.printLog("click sideKey xml url = " + sideKeyBean.getXml_uri());
        String xmlUri = null;
        long size = 0;
        try {
            xmlUri = sideKeyBean.getXml_uri();
            size = MoUtil.getXmlSize(xmlUri);

            String decodeUri = URLDecoder.decode(xmlUri);
            xmlUri = decodeUri.replaceAll("\\\\", "");
        } catch (Exception e) {

        }
        LoggerUtils.printLog("click sideKey xml url---new = " + xmlUri + "---size=" + size);
        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("camera json data xml uri  " + xmlUri + " data size " + size).out();
        DownloadVideoTempleteDataUtil.selectedDateForCache = sideKeyBean.getDate();
        new DownloadSingleCameraFile(DownloadSingleCameraFile.FileType.XML).startDownload(Uri.parse(xmlUri), size, new DownloadSingleCameraFile.DownloadCallback() {
            @Override
            public void onFinishDownLoad(String path) {
                try {
                    //该设置必须放在解析xml之前
                    downloadVideoTempleteDataUtil.setOnFinishDownloadVideoCallback(new DownloadVideoTempleteDataUtil.OnFinishDownloadVideoCallback() {
                        @Override
                        public void onStart() {
                            if (!progressRelativeLayout.isShowing()) {
                                progressRelativeLayout.showDialog(false, true);
                            }
                        }

                        @Override
                        public void onProgress(int progress) {
                            String format = String.format(AiCameraApplication.getContext().getResources().getString(R.string.download_save_progress), progress) + "%";
                            progressRelativeLayout.setProgress(format);
                            if (progress == 0) {
                                progressRelativeLayout.setProgress("");
                            }
//                            loadingContent.setText(context.getResources().getString(R.string.loadingData) + " " + progress + "%");
                        }

                        @Override
                        public void onFinish(VideoTemplete videoTemplete) {
                            progressRelativeLayout.dismissDialog();
                            CameraClipEditActivity.open(context, videoTemplete, netVideoTempleteHelper.mNetVideoTempleteList);
                        }

                        @Override
                        public void onFail(String errorInfo) {
                            progressRelativeLayout.dismissDialog();
                        }
                    });

                    new OneKeyMakeVideoXmlParser().parserXml(new FileInputStream(new File(path)), new OneKeyMakeVideoXmlParser.OnParseXmlCallback() {
                        @Override
                        public void onStart() {
                            //侧面键只有4段，xml解析简单
//                            progressRelativeLayout.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onFinish(List<VideoFile> videoFileList) {
                            //所有视频片段打分
                            oneKeyMakeVideoHelper.newScoreVideoSegmentForTodayVideoFiles(videoFileList);

                            if (NetworkUtil.isNetworkAvailable(context)) {
                                //                    if (false) {
                                progressRelativeLayout.showDialog(false, false);
                                netVideoTempleteHelper.getNetVideoTemplete(context, oneKeyMakeVideoHelper, new NetVideoTempleteHelper.NetTempleteCallback() {
                                    @Override
                                    public void onProgress(int progress) {
                                        String format = String.format(context.getResources().getString(R.string.download_save_progress), progress) + "%";
                                        progressRelativeLayout.setProgress(format);
//                                        loadingContent.setText(context.getResources().getString(R.string.loadingData) + " " + progress + "%");

                                    }

                                    @Override
                                    public void onSuccess(VideoTemplete videoTemplete) {
                                        OneKeyMakeVideoHelper.newFillVideoTempleteData(videoTemplete, oneKeyMakeVideoHelper.mAllSortedVideoSegmentList);
                                        if (checkVideoSegmentCount(context, videoTemplete)) {
                                            progressRelativeLayout.dismissDialog();
                                            return;
                                        }
                                        downloadVideoTempleteDataUtil.startPlayDownload(true, videoTemplete, false);
                                    }

                                    @Override
                                    public void onFail(String error) {
                                        String msg = context.getResources().getString(R.string.netTempleteDownloadException);
                                        if (StringUtil.notEmpty(error)) {
                                            msg = error;
                                        }
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                        progressRelativeLayout.dismissDialog();
                                    }
                                });

                            } else {
                                VideoTemplete filledDatavideoTemplete = oneKeyMakeVideoHelper.fillVideoTempleteForOneKeyMakeVideo();
                                if (checkVideoSegmentCount(context, filledDatavideoTemplete)) {
                                    return;
                                }
                                downloadVideoTempleteDataUtil.startPlayDownload(true, filledDatavideoTemplete, false);
                            }
                        }

                        @Override
                        public void onFail(String errorInfo) {
//                            progressRelativeLayout.setVisibility(View.GONE);
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static boolean checkVideoSegmentCount(Context context, VideoTemplete filledDatavideoTemplete) {
        if (filledDatavideoTemplete.getVideoSegmentList().size() < filledDatavideoTemplete.getVideo_segment().size()) {
            Toast.makeText(context, context.getResources().getString(R.string.noEnoughWonderfulData), Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public void getVideoSideKey() {
        mInitFinishFlag = true;
        Logcat.e().tag(LogcatConstants.FPV_PREVIEW).msg("GoUpActivity==>getVideoSideKey()").out();

        runOnUiThread(() -> {
            if (mStandardLoadingDlg != null)
                mStandardLoadingDlg.clear();
        });
        mHandler.removeCallbacks(mSyncTimeout);
        if (!AccessoryManager.getInstance().mIsRunning) {
            showDisconnectView();
            return;
        }
        ConnectionManager.getInstance().getVideoSideKey(0, new MoGetSideKeyCallback() {
            @Override
            public void onSuccess(final List<SideKeyBean> videoByDates) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        CameraToastUtil.show("获取侧面键精彩推荐数据成功", mContext);
                        LoggerUtils.printLog("获取侧面键精彩推荐数据" + videoByDates + "");
                        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("camera json data side key  " + videoByDates + " data size " + videoByDates.size()).out();
                        if (isDestroyed()) {
                            return;
                        }

                        if (videoByDates != null && videoByDates.size() > 0 && galleryViewPager != null) {
                            if (galleryPageAdapter.mDataList.size() > 0 && videoByDates.size() < galleryPageAdapter.mDataList.size()) {
                                galleryViewPager.setCurrentItem(0);
                            }
                            galleryViewPager.setVisibility(View.VISIBLE);
                            colorBorderView.setVisibility(View.VISIBLE);
                            placeHolderRelativeLayout.setVisibility(View.GONE);
                            galleryPageAdapter.updateData(true, videoByDates);
                        } else {
                            //空数据是页面的显示问题
                            showNoDataView();
                        }
                    }
                });
            }

            @Override
            public void onFailed() {
                showNoDataView();
            }
        });

    }

    public void showNoDataView() {
        runOnUiThread(() -> {
            placeHolderMode = PLACE_HOLDER_MODE_NO_DATA;
            if (isDestroyed() || isFinishing()) {
                return;
            }
            galleryViewPager.setVisibility(View.GONE);
            noDataBottomImageView.setImageResource(R.mipmap.no_data_in_sidekey);
            noDataTopImageView.setVisibility(View.GONE);
            colorBorderView.setVisibility(View.VISIBLE);
            placeHolderRelativeLayout.setVisibility(View.VISIBLE);
        });
    }


    public void showDisconnectView() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                placeHolderMode = PLACE_HOLDER_MODE_DISCONNECT;
                if (isDestroyed()) {
                    return;
                }
                noDataBottomImageView.setImageResource(R.mipmap.disconnect_bottom_in_sidekey);
                noDataTopImageView.setVisibility(View.VISIBLE);
                placeHolderRelativeLayout.setVisibility(View.VISIBLE);
                colorBorderView.setVisibility(View.GONE);
                galleryViewPager.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (progressRelativeLayout.isShowing()) {
//            ConnectionManager.getInstance().stopPlay(null);
            Toast.makeText(this, getResources().getString(R.string.cannotCancelWhenDownload), Toast.LENGTH_SHORT).show();
            return;
        }

        if (mJumpFlag) {
            finish();
        } else {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
        overridePendingTransition(R.anim.push_slient, R.anim.push_bottom_out);
    }

    @Override
    public void connectedUSB() {
        super.connectedUSB();
        Logcat.e().tag(LogcatConstants.FPV_PREVIEW).msg("GoUpActivity  connectedUSB==>initMsg() flag:" + initFlag + "  running:" + AccessoryManager.getInstance().mIsRunning).out();
        if (!initFlag && AccessoryManager.getInstance().mIsRunning) {
            initFlag = true;
            initMsg();
        }
        if (checkVersionUtils != null) {
            checkVersionUtils.getDevicedInfo();
        }
        newerGuideGoUp();
    }

    @Override
    public void disconnectedUSB() {
        super.disconnectedUSB();
        initFlag = false;
        mInitFinishFlag = false;
        goneLayout();
        if (dialogVersionUpdataTips != null) {
            dialogVersionUpdataTips.dismissDialog();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mStandardLoadingDlg != null)
                    mStandardLoadingDlg.clear();

                if (progressRelativeLayout != null) {
                    progressRelativeLayout.dismissDialog();
                }
            }
        });
        showDisconnectView();
    }

    public static void delDir(String cachePath) {
        File file = new File(cachePath);
        if (file.isDirectory() && !file.getName().equalsIgnoreCase(Constants.sampleVideoDir)) {
            File zFiles[] = file.listFiles();
            if(zFiles != null){
                for (File file2 : zFiles) {
                    delDir(file2.getAbsolutePath());
                }
                String absolutePath = file.getAbsolutePath();
                if (!Constants.cacheDir.equalsIgnoreCase(absolutePath.substring(absolutePath.lastIndexOf("/") + 1, absolutePath.length()))) {
                    file.delete();
                }
            }
        } else {
            file.delete();
        }
    }

    public void delTodayWonderfulCache() {
        if (ivGoUpBack == null) {
            return;
        }
        File cacheDir = new File(Constants.cacheLocalPath);
        new RxPermissions(GoUpActivity.this)
                .requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(permission -> {
                    if (permission.granted) {
                        if (cacheDir.exists()) {
                            DownloadWebFileUtil.getInstance().execute(new Runnable() {
                                @Override
                                public void run() {
                                    delDir(Constants.cacheLocalPath);
                                }
                            });
                        } else {
                            cacheDir.mkdirs();
                        }
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        DlgUtils.toast(GoUpActivity.this, getResources().getString(R.string.grantStoragePermission));
                    } else {
                        DlgUtils.toast(GoUpActivity.this, getResources().getString(R.string.grantStoragePermission));
                    }
                });
    }

    @Override
    public void setProgresss(int progress) {

    }

    @Override
    public void setTitleAndContent(String title, String Content) {

    }

    @Override
    public void setVisable(int Visable1, int Visable2) {

    }

    @Override
    public UploadDialog getDialog() {
        return null;
    }

    @Override
    public void setContent(int size, long time, String description, String version) {

    }

    @Override
    public void StartAct(Class clazz) {

    }

    @Override
    public void isClickable() {

    }

    @Override
    protected void onDestroy() {
        if (progressRelativeLayout != null) {
            progressRelativeLayout.destroy();
            progressRelativeLayout = null;
        }
        super.onDestroy();
        DownloadWebFileUtil.getInstance().shutdownNow();
        if (dialogVersionUpdataTips != null) {
            dialogVersionUpdataTips.destroyDialog();
            dialogVersionUpdataTips = null;
        }
    }

    public void requestDyFPVPermissions() {
        new RxPermissions(GoUpActivity.this)
                .requestEachCombined(DY_PERMISSIONS)
                .subscribe(permission -> {
                    if (permission.granted) {
                        startDyFPV();
                        Log.d("test 1111", "requestDyFPVPermissions: ");
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        Log.d("test 2222", "requestDyFPVPermissions: ");
                        DlgUtils.toast(GoUpActivity.this, "请到设置页面开启相关权限");
                    } else {
                        Log.d("test 3333", "requestDyFPVPermissions: ");
                        DlgUtils.toast(GoUpActivity.this, "请到设置页面开启相关权限");
                    }
                });
    }

    private void startDyFPV() {
        if (!AccessoryManager.getInstance().mIsRunning) {
            startAct(GuideConnectActivity.class);
            return;
        }

        if (!syncParams())
            return;

        if (System.currentTimeMillis() - createdTime < 1000) {  //如果进入当前页面即开始fpv页面 会造成连接尚未完成而黑屏
//                    if (AccessoryManager.getInstance().mIsRunning) {
//                        Intent intent2 = new Intent(GoUpActivity.this, DyFPVActivity.class);
//                        startActivity(intent2);
//                        break;
//                    } else
//                        DlgUtils.toast(GoUpActivity.this, "连接创建中。。。");
        } else {
            boolean hasGuideGoup = getSharedPreferences(SPUtils.FILE_NAME, Context.MODE_PRIVATE).getBoolean(NewerGuideActivity.KEY_OF_DOU_YIN, false);
            if (!hasGuideGoup && AccessoryManager.getInstance().mIsRunning) {
                NewerGuideActivity.open(this, NewerGuideActivity.NEWER_GUIDE_DOU_YIN);
            } else {
                Intent intentDouYin = new Intent(this, DyFPVActivity.class);
                startActivity(intentDouYin);
            }
        }
    }

    @Override
    public void clickView(int flag) {
        Intent intent = new Intent(this, PersonAgreeActivity.class);
        intent.putExtra("param", flag);
        startActivityForResult(intent, 100);
    }

    @Override
    public void consent() {
        if (checkVersionUtils != null)
            checkVersionUtils.getDevicedInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == 100) {
            showPrivacyDialog();
        }
    }

    /**
     * 用来显示顶部的升级提示框
     */
    @Override
    public void IsVisbilityUploadHardWare() {
        if (hardwareInfoTips != null && !AiCameraApplication.isShowHardwareUpdateTips) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hardwareInfoTips.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    /**
     * 用来影藏,顶部升级提示框的
     */
    public void goneLayout() {
        if (hardwareInfoTips != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hardwareInfoTips.setVisibility(View.GONE);
                }
            });
        }
    }

    public void showVersionDialog(String hardwareVersionParameter, AppVersion.AppVersionDetail detailParameter) {
        String privacy_flag = (String) SPUtils.get(getBaseContext(), "privacy_flag", "");
        if (TextUtils.isEmpty(privacy_flag)) {
            return;
        }
        if (detailParameter != null) {
            if (dialogVersionUpdataTips == null)
                dialogVersionUpdataTips = new DialogVersionUpdataTips(this);
            dialogVersionUpdataTips.getUpdate_dialog_info().setText(detailParameter.getFeature());
            dialogVersionUpdataTips.setDialogTouchOutside(detailParameter.getPkgType() == 2 ? true : false);
            dialogVersionUpdataTips.getVersionCode().setText("V" + detailParameter.getVersion());
            dialogVersionUpdataTips.showDialog(new DialogVersionUpdataTips.CheckDetailListener() {
                @Override
                public void isCheckDetail(boolean isCheckDetail) {
                    //先做保存数据
                    SPUtils.put(AiCameraApplication.getContext(), VersionCheck.VERSION_KEY, detailParameter.getVersion());
                    if (isCheckDetail) {
                        StatisticOneKeyMakeVideo.getInstance().setOnEvent("Click_Updata_Dialog_Sure_Button", getPointValue(hardwareVersionParameter, detailParameter));
                        //跳转到升级详情页面.
                        startAct(UploadHardWareActivity.class);
                    } else {
                        StatisticOneKeyMakeVideo.getInstance().setOnEvent("Click_Updata_Dialog_Cancel_Button", getPointValue(hardwareVersionParameter, detailParameter));
                    }
                }
            });
        }
    }

    private String getPointValue(String hardwareVersion, AppVersion.AppVersionDetail detail) {
        if (detail == null) {
            return "";
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("local_hardwareVersion", hardwareVersion);
            jsonObject.put("cloud_hardwareVersion", detail.getVersion());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public void setCloudData(String hardwareVersion, AppVersion.AppVersionDetail detail) {
        this.hardwareVersion = hardwareVersion;
        this.detail = detail;
    }

    @Override
    public void communityIconIsHide(boolean isHide, CommunityBean communityBean) {
        this.communityBean = communityBean;
        if (communityHelp != null) {
            communityHelp.setVisibility(isHide ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void showErrorView(String errorInfo) {
        CameraToastUtil.show(errorInfo, getApplicationContext());
    }
}
