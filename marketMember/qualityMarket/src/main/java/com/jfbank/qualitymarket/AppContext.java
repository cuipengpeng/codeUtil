package com.jfbank.qualitymarket;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.multidex.MultiDex;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import com.growingio.android.sdk.collection.Configuration;
import com.growingio.android.sdk.collection.GrowingIO;
import com.jfbank.qualitymarket.activity.WelcomeActivity;
import com.jfbank.qualitymarket.dao.StoreService;
import com.jfbank.qualitymarket.helper.DiskLruCacheHelper;
import com.jfbank.qualitymarket.helper.ImageLoaderHelper;
import com.jfbank.qualitymarket.helper.PicassoImageLoaderHelper;
import com.jfbank.qualitymarket.model.AddressInfoBean;
import com.jfbank.qualitymarket.model.Location;
import com.jfbank.qualitymarket.model.User;
import com.jfbank.qualitymarket.net.HttpRequest;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.jfbank.qualitymarket.util.ConstantsUtil;
import com.jfbank.qualitymarket.util.JumpUtil;
import com.jfbank.qualitymarket.util.LogUtil;
import com.jfbank.qualitymarket.util.StringUtil;
import com.jfbank.qualitymarket.util.UnicornUtils;
import com.qiyukf.unicorn.api.Unicorn;
import com.sptalkingdata.sdk.SPAgent;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;

public class AppContext extends Application {

    private static final String TAG = "AppContext";
    public static Context mContext;
    public static boolean isLogin = false;
    public static User user;
    public static Location location = new Location();
    public static AddressInfoBean addressInfo;
    private UncaughtExceptionHandler uncaught = new UncaughtExceptionHandler() {

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            ex.printStackTrace();

            //MainActivity.java 采用单利模式，启动MainActivity后会直接关闭应用。故不能启动MainActivity来重启应用。
            Intent intent = new Intent(mContext, WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            clearWebViewCache(mContext);
            new StoreService(mContext).saveUserInfo(AppContext.user);
            android.os.Process.killProcess(android.os.Process.myPid()); // 结束进程之前可以程序的注销或者退出代码放在这段代码之前
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        HttpRequest.initEnvironment();

        GrowingIO.startWithConfiguration(this, new Configuration()
                .useID()
                .trackAllFragments()
                .setDebugMode(true)); //打开调试Log;

        mContext = this;
        JumpUtil.init(mContext);
        DiskLruCacheHelper.init(mContext);//緩存初始化
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.setDebugMode(true);
        MobclickAgent.enableEncrypt(true);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        saveAppIconFile(bitmap);

        StoreService storeService = new StoreService(mContext);
        //极光推送初始化
        JPushInterface.init(this);
        JPushInterface.setDebugMode(false);            //debug用于打印log
        user = storeService.getUserInfo();
        HashSet set = new HashSet<String>();
        set.add("android");
        if (user != null && StringUtil.notEmpty(user.getToken())) {
            isLogin = true;
        } else {
            JPushInterface.setAliasAndTags(this, "", set, mAliasCallback);
        }
        Thread.setDefaultUncaughtExceptionHandler(uncaught);
        getAppVersionName(mContext);
        getMacAddress(mContext);
        Unicorn.init(this, "e475f83a88aa09a579b466d44385fdcc", UnicornUtils.options(), new PicassoImageLoaderHelper(this));
        SPAgent.LOG_ON = true;
        // App ID: 在TalkingData创建应用后，进入数据报表页中，在“系统设置”-“编辑应用”页面里查看App ID。
        // 如果已经在AndroidManifest.xml配置了App ID和渠道ID，调用TCAgent.init(this)即可；或与AndroidManifest.xml中的对应参数保持一致。
        SPAgent.init(this.getApplicationContext(), "F00839450C00C9DB2000152A3006069A", CommonUtils.getAppChannelId(mContext) + "");
        SPAgent.setReportUncaughtExceptions(false);
        OkHttpClient client = new OkHttpClient.Builder()
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .build();
        Picasso.setSingletonInstance(new Picasso.Builder(mContext).
                downloader(new ImageLoaderHelper(client))
                .build());
    }

    private static final int MSG_SET_ALIAS = 1001;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d(TAG, "Set alias in handler.");
                    // 调用 JPush 接口来设置别名。
                    HashSet set = new HashSet<String>();
                    set.add("android");
                    if (!AppContext.isLogin) {
                        JPushInterface.setAliasAndTags(getApplicationContext(),
                                "",
                                set,
                                mAliasCallback);
                    }
                    break;
                default:
                    Log.i(TAG, "Unhandled msg - " + msg.what);
            }
        }
    };
    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.e(TAG, logs);
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.e(TAG, logs);
                    // 延迟 60 秒来调用 Handler 设置别名
                    Message message = Message.obtain();
                    Bundle data = new Bundle();
                    data.putString("alias", alias);
                    data.putString("tag", "android");
                    message.setData(data);
                    message.what = MSG_SET_ALIAS;
                    mHandler.sendMessageDelayed(message, 1000 * 2);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }
        }
    };

    /**
     * 获取APP版本号
     *
     * @param context
     * @return
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
            if (TextUtils.isEmpty(versionName)) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 获取设备IMEI号
     *
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        String IMEI = tm.getDeviceId();
        if (TextUtils.isEmpty(IMEI)) {//设备号为空，则实用ANDROID_ID
            IMEI = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        return IMEI;
    }

    /**
     * 获取本机mac地址
     *
     * @param context
     * @return
     */
    public static String getMacAddress(Context context) {
        // 在wifi未开启状态下，仍然可以获取MAC地址，但是IP地址必须在已连接状态下否则为0
        String macAddress = null, ip = null;
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
        if (null != info) {
            macAddress = info.getMacAddress();
        }
        LogUtil.printLog("MAC地址：" + macAddress);
        return macAddress;
    }

    public static void clearWebViewCache(Context context) {
        new WebView(context).clearCache(true);
    }

    public static AddressInfoBean getAddressInfo() {
        return addressInfo;
    }

    public static void setAddressInfo(AddressInfoBean addressInfo) {
        AppContext.addressInfo = addressInfo;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public File saveAppIconFile(Bitmap bitmap) {
        // sdcard下创建目录
//		String saveDir = Environment.getExternalStorageDirectory() + ConstantsUtil.SDCARD_STORAGE_DIR;
        String saveDir = mContext.getFilesDir().getAbsolutePath();
        File appIconFile = new File(saveDir, ConstantsUtil.APP_ICON_FILE_NAME);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            File dir = new File(saveDir);
            if (!dir.exists()) {
                dir.mkdir();
            }
            appIconFile.delete();
            if (!appIconFile.exists()) {
                try {
                    appIconFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            getSharedPreferences(ConstantsUtil.GLOBAL_CONFIG_FILE_NAME, MODE_PRIVATE).edit()
                    .putString(ConstantsUtil.APP_ICON_LOCAL_STORE_KEY, appIconFile.getAbsolutePath()).commit();

            FileOutputStream fileOutputStream = null;
            BufferedOutputStream bos = null;
            // 将要保存图片的路径
            try {
                fileOutputStream = new FileOutputStream(appIconFile);
                bos = new BufferedOutputStream(fileOutputStream);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                bos.flush();
                fileOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    bos.close();
                    fileOutputStream.close();

                    bos = null;
                    fileOutputStream = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return appIconFile;
    }


    public static String getChannel(Context context) {
        ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        String ret = "";
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.startsWith("channel")) {
                    ret = entryName;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String[] split = ret.split("_");
        if (split != null && split.length >= 2) {
            return ret.substring(split[0].length() + 1);

        } else {
            return "";
        }
    }
}
