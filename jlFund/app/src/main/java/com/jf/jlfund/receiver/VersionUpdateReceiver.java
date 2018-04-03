package com.jf.jlfund.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.jf.jlfund.base.BaseApplication;
import com.jf.jlfund.utils.FileUtils;
import com.jf.jlfund.utils.LogUtils;

import java.io.File;

/**
 * 用于安装或更新成功后删除无用的安装包
 */
public class VersionUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //初次安装广播
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
            String packageName = intent.getDataString();
            if (isAddedOrReplacedMyApp(packageName)) {
                FileUtils.deleteInstallPackage();
            }
        }
        //更新安装广播
        if (intent.getAction().equals("android.intent.action.PACKAGE_REPLACED")) {
            String packageName = intent.getDataString();
            if (isAddedOrReplacedMyApp(packageName)) {
                FileUtils.deleteInstallPackage();
            }
        }
//        //接收卸载广播
//        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
//            String packageName = intent.getDataString();
//            Log.e("zzzzzz", "卸载了:" + packageName + " version: " + VersionUtils.getVersion());
//        }
    }

    //判断是否安装或更新了当前应用【从intent中获取的packageName中的内容为 <package:com.nbeebank.licaishi>】
    private boolean isAddedOrReplacedMyApp(String packageName) {
        if (!packageName.contains(":")) {
            return false;
        }
        if (packageName.split(":")[1].equals(BaseApplication.getContext().getPackageName())) {
            return true;
        }
        return false;
    }


}
