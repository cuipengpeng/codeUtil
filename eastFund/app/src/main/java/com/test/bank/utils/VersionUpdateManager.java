package com.test.bank.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;

import com.test.bank.base.BaseApplication;
import com.test.bank.base.BaseBean;
import com.test.bank.base.BaseBusiness;
import com.test.bank.base.CommonActivity;
import com.test.bank.bean.VersionUpdateBean;
import com.test.bank.http.NetService;
import com.test.bank.http.OkHttpClientUtils;
import com.test.bank.http.ParamMap;
import com.test.bank.http.ProgressResponseBody;
import com.test.bank.inter.OnResponseListener;
import com.test.bank.weight.dialog.VersionUpdateDialogFragment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Observable;
import okhttp3.Request;

/**
 * Created by 55 on 2018/3/15.
 */

public class VersionUpdateManager {

    private static final String TAG = "VersionUpdateManager";

    private VersionUpdateManager() {
    }

    public static VersionUpdateManager versionUpdateManager;

    public static VersionUpdateManager getInstance() {
        if (versionUpdateManager == null) {
            synchronized (VersionUpdateManager.class) {
                if (versionUpdateManager == null) {
                    versionUpdateManager = new VersionUpdateManager();
                }
            }
        }
        return versionUpdateManager;
    }

    private CommonActivity activity;
    private VersionUpdateBean versionUpdateBean;
    private boolean isDownloading = false;

    public VersionUpdateManager checkVersionUpdate(CommonActivity activity) {
        return checkVersionUpdate(activity, 0);
    }

    public VersionUpdateManager checkVersionUpdate(final CommonActivity activity, long delayMillis) {
        this.activity = activity;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BaseBusiness.postRequest(new OnResponseListener<VersionUpdateBean>() {
                    @Override
                    public Observable<BaseBean<VersionUpdateBean>> createObservalbe() {
                        ParamMap paramMap = new ParamMap();
                        paramMap.putLast("appVersion", PackageUtils.getVersion());
                        return NetService.getNetService().checkoutVersionUpdate(paramMap);
                    }

                    @Override
                    public void onResponse(final BaseBean<VersionUpdateBean> result) {
                        if (result.isSuccess() && result.getData() != null) {
                            versionUpdateBean = result.getData();
                            showVersionUpdateDialog();
                        }
                    }

                    @Override
                    public void onError(String errorMsg) {

                    }
                });
            }
        }, delayMillis);
        return versionUpdateManager;
    }

    private VersionUpdateDialogFragment versionUpdateDialogFragment;

    public void showVersionUpdateDialog() {
        if (versionUpdateBean == null) {
            return;
        }
        FragmentTransaction mFragTransaction = activity.getSupportFragmentManager().beginTransaction();
        versionUpdateDialogFragment = (VersionUpdateDialogFragment) activity.getSupportFragmentManager().findFragmentByTag("VersionUpdateDialogFragment");
        if (versionUpdateDialogFragment == null) {
            versionUpdateDialogFragment = new VersionUpdateDialogFragment();
            versionUpdateDialogFragment.setUpdateContent(versionUpdateBean.getContent());
//            versionUpdateDialogFragment.setUpdateContent("1、首页全新改版，资产事项一目了然；\n2、修复了一些bug，优化了一些秩序；\n3、解决了提出问题的人；\n4、更新了版本号");
            versionUpdateDialogFragment.setForceUpdate(versionUpdateBean.isForceUpdate());
            versionUpdateDialogFragment.setCancelable(false);
        }
        if (!versionUpdateDialogFragment.isAdded()) {
            mFragTransaction.add(versionUpdateDialogFragment, "VersionUpdateDialogFragment");
            mFragTransaction.commitAllowingStateLoss();
        }
    }

    public void dismissUpdateDialog() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (versionUpdateDialogFragment != null) {
                    versionUpdateDialogFragment.dismiss();
                }
            }
        });
    }

    public void downloadApk() {
        if (versionUpdateBean == null) {
            return;
        }
        if (isDownloading) {
            ToastUtils.showShort("正在拼命下载，请稍候...");
            return;
        }
        if (TextUtils.isEmpty(versionUpdateBean.getUrl()) || !versionUpdateBean.getUrl().endsWith(".apk")) {
            ToastUtils.showShort("安装包下载地址不正确");
            return;
        }
        String apkUrl = versionUpdateBean.getUrl();
        LogUtils.e("apkUrl: " + apkUrl);
//        apkUrl = "http://js.downcc.com/anzhuo/jflcs_downcc.apk";
        String apkFileName = getFormattedApkName(versionUpdateBean.getVersion());
        final File apkFile = new File(FileUtils.getDownloadAppDir().getAbsolutePath(), apkFileName);
        downloadedApkPath = apkFile.getAbsolutePath();
        if (apkFile.exists()) {
            LogUtils.e("安装包已下载完毕，立即安装");
            if (versionUpdateDialogFragment != null) {
                versionUpdateDialogFragment.onApkExists(apkFile);
            }
            return;
        }
        isDownloading = true;
        Request request = new Request.Builder().url(apkUrl).build();
        OkHttpClientUtils.getOkHttpClient(generateProgressListener()).newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.e(TAG, "download failed: " + e.toString());
                dismissUpdateDialog();
                if (progressListener != null)
                    progressListener.onProgress(-1, -1, true);
                isDownloading = false;
                ToastUtils.showShort("安装包下载失败，请稍后重试");
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                Log.e(TAG, "onResponse: finishCurrState apk url: " + response.toString());
                if (response == null || response.body() == null || response.code() != 200) {
                    dismissUpdateDialog();
                    ToastUtils.showShort("安装包下载失败，请稍后重试");
                    FileUtils.deleteInstallPackage();
                    if (progressListener != null)
                        progressListener.onProgress(-1, -1, true);
                    isDownloading = false;
                    return;
                }
                InputStream is = response.body().byteStream();
                writeStreamToFile(is, apkFile);
            }
        });
    }

    private void writeStreamToFile(InputStream is, File apkFile) {
        FileUtils.save(is, apkFile, new FileUtils.FileWriteListener() {
            @Override
            public void onWriteOver(File apkFile) {
                isDownloading = false;
                PackageUtils.install(apkFile);
            }

            @Override
            public void onWriteFailed(String errorMsg) {
                LogUtils.e("onWriteFailed: " + errorMsg);
            }
        });
    }


    /**
     * 生成下载进度监听
     *
     * @return
     */
    private ProgressResponseBody.ProgressListener generateProgressListener() {
        return new ProgressResponseBody.ProgressListener() {
            @Override
            public void onProgress(long progress, long total, boolean done) {
                if (versionUpdateBean != null && versionUpdateBean.getApkSize() == -1) {
                    versionUpdateBean.setApkSize(total);        //记录安装包大小
                }
                if (null != progressListener && total > 0) {
                    progressListener.onProgress(progress, total, done);
                }
            }
        };
    }

    private String getFormattedApkName(String lastestVersion) {
        return "JLFund" + "_" + lastestVersion.replaceAll("\\.", "_") + ".apk";
    }

    /**
     * 应用退出的时候释放资源并去掉通知栏通知
     */
    public void onActivityDestoryed() {
        Log.e(TAG, "onActivityDestroyed: activity has destroyed........................dismiss and release dialog.");
        isDownloading = false;
        if (versionUpdateDialogFragment != null && versionUpdateDialogFragment.isAdded()) {
            versionUpdateDialogFragment.dismiss();
            versionUpdateDialogFragment = null;
        }
        NotificationManager manager = (NotificationManager) BaseApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(VersionUpdateDialogFragment.NOTIFICATION_ID);
        VersionUpdateDialogFragment.DOWNLOAD_STATUS = VersionUpdateDialogFragment.DOWNLOAD_IDLE;
    }

    public ProgressResponseBody.ProgressListener progressListener;

    public void setProgressListener(ProgressResponseBody.ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public static String downloadedApkPath;
}
