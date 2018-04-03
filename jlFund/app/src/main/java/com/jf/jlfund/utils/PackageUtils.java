package com.jf.jlfund.utils;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import com.jf.jlfund.base.BaseApplication;

import java.io.File;

/**
 * Created by 55 on 2017/2/21.
 */

public class PackageUtils {
    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion() {
        String versionName = "1.0.0";
        try {
            // ---get the package info---
            PackageManager pm = BaseApplication.getContext().getPackageManager();
            PackageInfo info = pm.getPackageInfo(BaseApplication.getContext().getPackageName(), 0);
            versionName = info.versionName;
            //versioncode = pi.versionCode;
        } catch (Exception e) {
            versionName = "1.0.0";
        }
        return versionName;
    }

    public static String getPackageName() {
        String packageName = "com.jf.jlfund";
        try {
            PackageManager pm = BaseApplication.getContext().getPackageManager();
            PackageInfo info = pm.getPackageInfo(BaseApplication.getContext().getPackageName(), 0);
            packageName = info.packageName;
        } catch (Exception e) {
            LogUtils.e("PackageUtils.getPackageName: " + e.getMessage());
        }
        LogUtils.e("getPackageName ==> " + packageName);
        return packageName;
    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName() {
        String appName = "玖理财富";
        try {
            PackageManager pm = BaseApplication.getContext().getPackageManager();
            PackageInfo info = pm.getPackageInfo(BaseApplication.getContext().getPackageName(), 0);
            int labelRes = info.applicationInfo.labelRes;
            appName = BaseApplication.getContext().getResources().getString(labelRes);
        } catch (Exception e) {
            LogUtils.e(e.toString());
        }
        LogUtils.e("getAppName ==> " + appName);
        return appName;
    }

    public static boolean install(File apkFile) {
        // 没有root权限，利用意图进行安装
        if (!apkFile.exists())
            return false;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 24) { //判读版本是否在7.0以上
            Uri packageUri = FileProvider.getUriForFile(ActivityManager.getInstance().currentActivity(), getPackageName() + ".fileprovider", apkFile);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(packageUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile),
                    "application/vnd.android.package-archive");
        }
        if (ActivityManager.getInstance().currentActivity() != null)
            ActivityManager.getInstance().currentActivity().startActivity(intent);
        return true;
    }
}
