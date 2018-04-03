package com.jf.jlfund.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.jf.jlfund.base.BaseApplication;
import com.jf.jlfund.utils.LogUtils;
import com.jf.jlfund.utils.PackageUtils;
import com.jf.jlfund.utils.ToastUtils;
import com.jf.jlfund.utils.VersionUpdateManager;
import com.jf.jlfund.weight.dialog.VersionUpdateDialogFragment;

import java.io.File;

/**
 * Created by 55 on 2017/7/18.
 */

public class NotificationClickReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationClickReceiv";

    public NotificationClickReceiver() {
    }

    public static final String APK_SIZE = "apkSize"; //在点击安装的时候判断是否已经下载完毕或者安装包是否被手动删除，来决定是否dismiss该通知【默认的是下载完新包不安装就一直显示，除非手动左滑删除】

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        long apkSize = intent.getLongExtra(APK_SIZE, -1L);
        Log.e(TAG, "onReceive: apkSize: " + apkSize);   //2.5M
        //鸡肋，下载完成之后会自动安装，此处更多的是在用户选择了后台下载之后给用户呈现下载进度。另外一个就是用户在现在完成后点击了取消，则也可以通过点击通知再次安装
        if (action.equals("notification_clicked")) {
            LogUtils.e(TAG, "clicked notification .... go to install...");
            try {
                String newApkPath = VersionUpdateManager.downloadedApkPath;
                if (TextUtils.isEmpty(newApkPath)) {
                    return;
                }
                File newApk = new File(newApkPath);
                //如果安装包存在且大小与服务器传回的apkSize大小一直，并且下载状态为已完成，则安装
                if (VersionUpdateDialogFragment.DOWNLOAD_STATUS == VersionUpdateDialogFragment.DOWNLOAD_FINISHED) {
                    if (!TextUtils.isEmpty(newApkPath) && newApk.exists() && newApk.length() == apkSize) {   //下载完成，并且大小与服务器传回大小相同
                        PackageUtils.install(newApk);
                    } else {    //出现不知道的错误，可能文件被替换？？？或者下载状态逻辑错误？此时就dismiss通知栏并重置DOWNLOAD_STATUS
                        LogUtils.e(TAG, "newApkPath is null......");
                        NotificationManager manager = (NotificationManager) BaseApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        manager.cancel(VersionUpdateDialogFragment.NOTIFICATION_ID);
                        VersionUpdateDialogFragment.DOWNLOAD_STATUS = VersionUpdateDialogFragment.DOWNLOAD_IDLE;
                    }
                } else if (VersionUpdateDialogFragment.DOWNLOAD_STATUS == VersionUpdateDialogFragment.DOWNLOADING) {    //正在下载
                    ToastUtils.showShort("正在下载，请稍候......");
                }
            } catch (Exception e) {
                LogUtils.e("NotificationClickReceiver: " + e.getMessage());
            }
        }

        if (action.equals("notification_cancelled")) {
            //处理滑动清除和点击删除事件
            LogUtils.e(TAG, "swap and dismiss the notification...");
        }
    }
}
