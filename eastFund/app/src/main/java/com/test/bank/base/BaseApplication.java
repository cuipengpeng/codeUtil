package com.test.bank.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.multidex.MultiDex;

import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.flattener.ClassicFlattener;
import com.elvishew.xlog.interceptor.BlacklistTagsFilterInterceptor;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;
import com.test.bank.BuildConfig;
import com.test.bank.R;
import com.test.bank.bean.UserInfo;
import com.test.bank.http.HttpRequest;
import com.test.bank.utils.ConstantsUtil;
import com.test.bank.utils.CrashHandler;
import com.test.bank.utils.LogUtils;
import com.test.bank.utils.SPUtil;
import com.test.bank.utils.StringUtil;
import com.test.bank.view.activity.CurrentPlusActivity;
import com.test.bank.view.activity.FundActivity;
import com.test.bank.view.activity.GestureVerifyActivity;
import com.test.bank.view.activity.MainActivity;
import com.test.bank.view.activity.MessageActivity;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;


public class BaseApplication extends Application {
    //	全局上下文
    public static Context applicationContext;

    public static Printer globalFilePrinter;
    private int foregroundActivityCount = 0;//activity的count数
    public List<String> accountPageList = new ArrayList<>();
    public static boolean goIntoAccountSecurity = false;
    public static UserInfo userInfo = new UserInfo();

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();
        JPushInterface.init(this);

        //初始化组件化基础库, 统计SDK/推送SDK/分享SDK都必须调用此初始化接口
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "5a9cd9a9a40fa33662000026");    //  5aa898ce8f4a9d161a0000cd
        UMConfigure.setLogEnabled(true);
//        UMConfigure.init(this, "5a9cd9a9a40fa33662000026", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "669c30a9584623e70e8cd01b0381dcb4");
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.setDebugMode(true);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_DUM_NORMAL);

        CrashHandler.getInstance().init(this);
        HttpRequest.initEnvironment();
        initXlog();
        LitePal.initialize(this);       //初始化数据库
//        UIUtils.replaceSystemDefaultFont(this, "fonts/my_font.ttf");

        //账户页面安全检查放在MyaccountFragment中进行
        accountPageList.add(MainActivity.class.getName());
        accountPageList.add(CurrentPlusActivity.class.getName());
        accountPageList.add(FundActivity.class.getName());
        accountPageList.add(MessageActivity.class.getName());

        GestureVerifyActivity.removeLockScreenAndBackgroundKey();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                foregroundActivityCount++;
                boolean isAccountPage = containAccountPage(activity);
                LogUtils.printLog("isAccountPage=" + isAccountPage + "--" + "foregroundActivityCount=" + foregroundActivityCount + "--" + activity.getPackageName() + "." + activity.getLocalClassName());

                if (isAccountPage && foregroundActivityCount > 0
                        && ((System.currentTimeMillis() - GestureVerifyActivity.getBackgroundStartTime()) > ConstantsUtil.ACCOUNT_MAX_VALID_TIME
                        || (System.currentTimeMillis() - GestureVerifyActivity.getLockScreenStartTime()) > ConstantsUtil.ACCOUNT_MAX_VALID_TIME
                        || !BaseApplication.goIntoAccountSecurity)
                        && (GestureVerifyActivity.supportFingerprint(activity) || StringUtil.notEmpty(SPUtil.getInstance().getUserInfo().getGesturePassword()))) {
                    //TODO 用户有交易记录，但是没有设置手势和指纹密码，进入账户页面或锁屏5分钟后安全检查如何处理？
                    lockScreenOrBackgroundMinuteSecurityCheck(activity, true, -1);
                }

            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                foregroundActivityCount--;
                LogUtils.printLog("foregroundActivityCount=" + foregroundActivityCount + "--" + activity.getComponentName());
                if (0 == foregroundActivityCount) {
                    GestureVerifyActivity.putBackgroundStartTime(System.currentTimeMillis());
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });


    }

    public void lockScreenOrBackgroundMinuteSecurityCheck(Activity activity, boolean fiveMinuteSecurityCheck, int backToTabIndex) {
        boolean hasGesturePassword = false;

        Intent intent = new Intent(activity, GestureVerifyActivity.class);
        if (GestureVerifyActivity.supportFingerprint(activity)) {
            intent.putExtra(GestureVerifyActivity.KEY_OF_PASSWORD_TYPE, GestureVerifyActivity.FINGERPRINT_PASSWORD);
        } else if (StringUtil.notEmpty(SPUtil.getInstance().getUserInfo().getGesturePassword())) {
            intent.putExtra(GestureVerifyActivity.KEY_OF_PASSWORD_TYPE, GestureVerifyActivity.VERITY_GESTURE_PASSWORD);
        }
        intent.putExtra(GestureVerifyActivity.KEY_OF_5MINUTE_SECRITY_CHECK, fiveMinuteSecurityCheck);
        intent.putExtra(GestureVerifyActivity.KEY_OF_BACK_TAB_INDEX, backToTabIndex);
        activity.startActivity(intent);
    }

    public boolean containAccountPage(Activity activity) {
        boolean isAccountPage = false;

        String activityFullName = activity.getPackageName() + "." + activity.getLocalClassName();
        for (int i = 0; i < accountPageList.size(); i++) {
            if (accountPageList.get(i).equals(activityFullName)) {
                isAccountPage = true;
                break;
            }
        }
        if (MainActivity.class.getName().equals(activityFullName) &&
                (((MainActivity) activity).index != 0 || !SPUtil.getInstance().isLogin())) {
            isAccountPage = false;
        }
        return isAccountPage;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Context getContext() {
        return applicationContext;
    }

    private void initXlog() {
        LogConfiguration config = new LogConfiguration.Builder()
                .logLevel(BuildConfig.DEBUG ? LogLevel.ALL             // Specify log level, logs below this level won't be printed, default: LogLevel.ALL
                        : LogLevel.NONE)
                .tag("tag")                   // Specify TAG, default: "X-LOG"
                 .nt()                                                // Enable thread info, disabled by default
                 .nst()                                              // Enable stack trace info with depth 2, disabled by default
                 .b()                                                // Enable border, disabled by default
                // .jsonFormatter(new MyJsonFormatter())               // Default: DefaultJsonFormatter
                // .xmlFormatter(new MyXmlFormatter())                 // Default: DefaultXmlFormatter
                // .throwableFormatter(new MyThrowableFormatter())     // Default: DefaultThrowableFormatter
                // .threadFormatter(new MyThreadFormatter())           // Default: DefaultThreadFormatter
                // .stackTraceFormatter(new MyStackTraceFormatter())   // Default: DefaultStackTraceFormatter
                // .borderFormatter(new MyBoardFormatter())            // Default: DefaultBorderFormatter
                // .addObjectFormatter(AnyClass.class,                 // Add formatter for specific class of object
                //     new AnyClassObjectFormatter())                  // Use Object.toString() by default
                .addInterceptor(new BlacklistTagsFilterInterceptor(    // Add blacklist tags filter
                        "blacklist1", "blacklist2", "blacklist3"))
                // .addInterceptor(new WhitelistTagsFilterInterceptor( // Add whitelist tags filter
                //     "whitelist1", "whitelist2", "whitelist3"))
                // .addInterceptor(new MyInterceptor())                // Add a log interceptor
                .build();

        Printer androidPrinter = new AndroidPrinter();             // Printer that print the log using android.util.Log
        Printer filePrinter = new FilePrinter                      // Printer that print the log to the file system
                .Builder(new File(Environment.getExternalStorageDirectory(), "001log").getPath())       // Specify the path to save log file
                .fileNameGenerator(new DateFileNameGenerator())        // Default: ChangelessFileNameGenerator("log")
//         .backupStrategy(new MyBackupStrategy())             // Default: FileSizeBackupStrategy(1024 * 1024)
                // .cleanStrategy(new FileLastModifiedCleanStrategy(MAX_TIME))     // Default: NeverCleanStrategy()
                .flattener(new ClassicFlattener())                     // Default: DefaultFlattener
                .build();

        XLog.init(                                                 // Initialize XLog
                config,                                                // Specify the log configuration, if not specified, will use new LogConfiguration.Builder().build()
                androidPrinter,                                        // Specify printers, if no printer is specified, AndroidPrinter(for Android)/ConsolePrinter(for java) will be used.
                filePrinter);

        // For future usage: partial usage in MainActivity.
        globalFilePrinter = filePrinter;
    }
}
