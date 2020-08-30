package com.test.xcamera.application;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.editvideo.NvAsset;
import com.editvideo.NvAssetManager;
import com.editvideo.TimelineUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.test.xcamera.BuildConfig;
import com.test.xcamera.accrssory.AccessoryCommunicator;
import com.test.xcamera.bean.User;
import com.test.xcamera.cameraclip.bean.VideoFile;
import com.test.xcamera.cameraclip.bean.VideoSegment;
import com.test.xcamera.dymode.utils.FileUtils;
import com.test.xcamera.utils.Constants;
import com.test.xcamera.utils.SPUtils;
import com.test.xcamera.utils.SharedPreferencesUtil;
import com.test.xcamera.utils.StorageUtils;
import com.test.xcamera.utils.StringUtil;
import com.test.xcamera.watermark.WaterMarker;
import com.meicam.sdk.NvsStreamingContext;
import com.moxiang.common.base.BaseApplication;
import com.moxiang.common.crash.CrashHelper;
import com.moxiang.common.crash.LogcatHelper;
import com.moxiang.common.logging.Builder;
import com.moxiang.common.logging.Logcat;
import com.moxiang.common.share.ShareManager;
import com.ss.android.vesdk.VELogUtil;
import com.ss.android.vesdk.VESDK;
import com.ss.android.vesdk.runtime.oauth.TEOAuth;
import com.ss.android.vesdk.runtime.oauth.TEOAuthResult;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by zll on 2019/5/22.
 */

public class AiCameraApplication extends BaseApplication {
    private static final String TAG = "AiCameraApplication";
    public static AiCameraApplication mApplication = null;
    private static final String TEST_LICENSE = "f35bcdd18b968f16a1c0349b230d6bf1c52e704f19bacab831f2c83232981e868bd1cb08f9946d869d587f1bf78a4d0d1f57632b6f4c068512e2d60d8c90b67a";
    private static final String TEST_APPID = "100120";
    private static final String LICENSE = "a9ad77540b1c9ec7fdd4e136673e765a1d0509f7461641795885ec8897d675897875cdcbb6e35bd0b406c3e53d990847c6a84bd514de953bc1ae9efb1cdfcdef";
    private static final String APPID = "276262";
    public static long frameBaseCount = 0;
    public static User.UserDetail userDetail = new User.UserDetail();
    private static Context mContext;
    public Handler mHandler = new Handler();
    public boolean ratesScreen = false;
    public List<SoftReference<Activity>> mGoUpAct = new ArrayList<>();
    public List<VideoSegment> mAllSortedVideoSegmentList = new ArrayList<>();
    public List<VideoFile> mVideoFileList = new ArrayList<>();
    public static boolean isShowHardwareUpdateTips = false;

    @Override
    public void onCreate() {
        super.onCreate();
        /*只有开发模式 才开启内存监控*/
        /*if(com.meetvr.aicamera.BuildConfig.FLAVOR_DEV){
            LeakCanary.install(this);
            AppUseMonitor.getInstance();
        }*/

        mApplication = this;
        AppContext.init(this);
        init();

        if (NvsStreamingContext.getInstance() == null) {
            TimelineUtil.initStreamingContext();
        }

        NvAssetManager mAssetManager = NvAssetManager.sharedInstance();
        mAssetManager.searchLocalAssets(NvAsset.ASSET_FILTER);
        mAssetManager.searchReservedAssets(NvAsset.ASSET_FILTER, "filter");
        mAssetManager.searchLocalAssets(NvAsset.ASSET_ANIMATED_STICKER);
        mAssetManager.searchReservedAssets(NvAsset.ASSET_ANIMATED_STICKER, "sticker");
        mAssetManager.searchLocalAssets(NvAsset.ASSET_VIDEO_TRANSITION);
        mAssetManager.searchReservedAssets(NvAsset.ASSET_VIDEO_TRANSITION, "transition");
        mAssetManager.searchLocalAssets(NvAsset.ASSET_FONT);
        mAssetManager.searchReservedAssets(NvAsset.ASSET_FONT, "font");

        Fresco.initialize(this);
//        String clientkey = "awvcii9svhgkgixl"; // 修改为在开发者应用登记页面申请的clientkey
//        TikTokOpenApiFactory.init(new TikTokOpenConfig(clientkey));

        //init sp utils
        SharedPreferencesUtil.instance().init(this);

        registSreenStatusReceiver();

//        EffectPlatform.getInstance().initEffectManager(this);
//        //初始化,美摄管理类
//        EffectPlatform.instance().initEffect(this);

        initDyResource();
        initializeLogcat();
        initWaterMarker();
        registerReceiver(new AccessoryReceiver(), new IntentFilter(AccessoryCommunicator.ACTION_USB_PERMISSION));

    }

    static {
        System.loadLibrary("mp4v2");
        //TODO 待添加so库
//        System.loadLibrary("templete");
        System.loadLibrary("Mp4RecorderNative");
    }

    private void init() {
        //监听客户端崩溃
        CrashHelper.init(getApplicationContext());

        mContext = getApplicationContext();

        switchEnv();
        baseUrl = Constants.base_url;
        ShareManager.getInstance().initShareManager(AiCameraApplication.getContext());
        initUMPush();
        LogcatHelper.getInstance(this).start();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void switchEnv() {
        if (com.test.xcamera.BuildConfig.DEBUG) {
            SharedPreferences sharedPreferences = getSharedPreferences(SPUtils.FILE_NAME, Context.MODE_PRIVATE);
            if (sharedPreferences.contains(SPUtils.KEY_OF_ONLINE_ENV)) {
                if (sharedPreferences.getBoolean(SPUtils.KEY_OF_ONLINE_ENV, true)) {
                    Constants.base_url = com.test.xcamera.BuildConfig.SWITCH_ENV_ONLINE;
                } else {
                    Constants.base_url = com.test.xcamera.BuildConfig.SWITCH_ENV_TEST;
                }
            }
        }
    }

    public static Context getContext() {
        return mContext;
    }

    //监听息屏
    private void registSreenStatusReceiver() {
        ScreenStatusReceiver screenStatusReceiver = new ScreenStatusReceiver();
        IntentFilter screenStatusIF = new IntentFilter();
        screenStatusIF.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(screenStatusReceiver, screenStatusIF);
    }

    private void initDyResource() {
//        VESDK.init(getApplicationContext(), FileUtils.ROOT_DIR);
        VESDK.init(mContext, FileUtils.getDyResourceDir(mContext));
        VESDK.setAssetManagerEnable(true);
        VESDK.setLogLevel(VELogUtil.LOG_LEVEL_D);
        VESDK.enableGLES3(true);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String activationCode = sp.getString("activationCode", "");
        TEOAuthResult result = TEOAuth.activate(mContext, LICENSE, APPID, activationCode);
        Log.d(TAG, "TEOAuthResult: " + result);
        if (result == TEOAuthResult.OK || result == TEOAuthResult.TBD) {
            activationCode = TEOAuth.getActivationCode();
            sp.edit().putString("activationCode", activationCode).apply();
//            Toast.makeText(this, "Activation Success", Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(this, "Activation Failed", Toast.LENGTH_SHORT).show();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
//                FileUtils.delDir(FileUtils.ROOT_DIR);
                if (VESDK.needUpdateEffectModelFiles()) {
                    VESDK.updateEffectModelFiles();
                }
                FileUtils.makeDir(FileUtils.getDyResourceDir(mContext));
                try {
//                    FileUtils.UnZipAssetFolder(mContext, FileUtils.FACE_MAKE_UP_FILENAME,
//                            FileUtils.RESOURCE_DIR);
//                    FileUtils.UnZipAssetFolder(mContext, FileUtils.BEAUTY_12_FILENAME, FileUtils.RESOURCE_DIR);
//
//                    FileUtils.UnZipAssetFolder(mContext, FileUtils.FACE_RESHAPE, FileUtils.RESOURCE_DIR);

                    FileUtils.UnZipAssetFolder(mContext, FileUtils.FACE_MAKE_UP_FILENAME,
                            FileUtils.getDyResourceDir(mContext));
                    FileUtils.UnZipAssetFolder(mContext, FileUtils.BEAUTY_12_FILENAME,
                            FileUtils.getDyResourceDir(mContext));

                    FileUtils.UnZipAssetFolder(mContext, FileUtils.FACE_RESHAPE,
                            FileUtils.getDyResourceDir(mContext));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    class ScreenStatusReceiver extends BroadcastReceiver {
        String SCREEN_ON = "android.intent.action.SCREEN_ON";
        String SCREEN_OFF = "android.intent.action.SCREEN_OFF";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (SCREEN_ON.equals(intent.getAction())) {
                ratesScreen = true;
            }
        }

    }


    private void initializeLogcat() {
        //简单初始化Logcat
//        Logcat.initialize(this);

        /*推荐配置*/
        //初始化Logcat 配置更多信息
        Builder builder = Logcat.newBuilder();
        //设置Log 保存的文件夹
        builder.logSavePath(StorageUtils.getDiskCacheDir(this, "log"));
//        Log.e("=====", "log path==>" + StorageUtils.getDiskCacheDir(this, "log").getAbsolutePath());
        //设置输出日志等级
        if (BuildConfig.DEBUG) {
            builder.logCatLogLevel(Logcat.SHOW_ALL_LOG);
            //设置输出文件日志等级
            builder.fileLogLevel(Logcat.SHOW_ALL_LOG);
        } else {
            builder.logCatLogLevel(Logcat.SHOW_INFO_LOG | Logcat.SHOW_WARN_LOG | Logcat.SHOW_ERROR_LOG);
            //设置输出文件日志等级
            builder.fileLogLevel(Logcat.SHOW_INFO_LOG | Logcat.SHOW_WARN_LOG | Logcat.SHOW_ERROR_LOG);
        }
        //不显示日志
        //builder.fileLogLevel(Logcat.NOT_SHOW_LOG);

//        builder.topLevelTag(TAG_TOP_1);
        //删除过了几天无用日志条目
        builder.deleteUnusedLogEntriesAfterDays(7);
        //输出到Java控制台服务端
//        if (isMainProcess) {
//            builder.dispatchLog(new JLog("192.168.3.11", 5036));
//        }
        //是否自动保存日志到文件中
        builder.autoSaveLogToFile(true);
        //是否显示打印日志调用堆栈信息
        builder.showStackTraceInfo(true);
        //是否显示文件日志的时间
        builder.showFileTimeInfo(true);
        //是否显示文件日志的进程以及Linux线程
        builder.showFilePidInfo(true);
        //是否显示文件日志级别
        builder.showFileLogLevel(true);
        //是否显示文件日志标签
        builder.showFileLogTag(true);
        //是否显示文件日志调用堆栈信息
        builder.showFileStackTraceInfo(true);
        //添加该标签,日志将被写入文件
//        builder.addTagToFile(TAG_APP_EVENT);
//        builder.addTagToFile(PushService.TAG);
//        builder.addTagToFile(PushService.TAG);
//        builder.addTagToFile(MainActivity.TAG_VIEW_CLICK_EVENT);
//        logBuilder = builder;
        Logcat.initialize(this, builder.build());
//        MyActivityLifecycle mActivityLifecycle = new MyActivityLifecycle();
//        this.registerActivityLifecycleCallbacks(mActivityLifecycle);
    }

    public static boolean isLogin() {
        return StringUtil.notEmpty(userDetail.getToken());
    }

    private void initWaterMarker() {
        WaterMarker.onAppCreate(this);
        WaterMarker.builder()
                .setWidth(252)
                .setHeight(102)
                .setX(30)
                .setY(30)
                .buildDefault();
    }

    private void initUMPush() {
        //获取推送代理，这个代理可以帮我们去执行诸如点击事件，样式不同的通知栏等操作
        PushAgent pushAgent = PushAgent.getInstance(this);
        pushAgent.setResourcePackageName("com.meetvr.aicamera");
        pushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String s) {
                //注册成功
                Log.e("=====", "deviceToken=" + s);
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.e("=====", "deviceToken  onFailure=" + s + "###" + s1);
                //注册失败
            }
        });
    }
}
