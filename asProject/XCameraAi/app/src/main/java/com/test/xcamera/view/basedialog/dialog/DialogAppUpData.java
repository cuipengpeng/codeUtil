package com.test.xcamera.view.basedialog.dialog;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.test.xcamera.R;
import com.test.xcamera.upgrade.BaseVersion;
import com.test.xcamera.upgrade.NotifyDownloadService;
import com.test.xcamera.upgrade.NumberProgressBar;
import com.test.xcamera.upgrade.VersionInfo;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.Constants;
import com.test.xcamera.utils.DownloadCloudFileTask;
import com.test.xcamera.utils.NetworkUtil;
import com.test.xcamera.utils.SPUtils;
import com.test.xcamera.utils.StorageUtils;

import java.io.File;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static com.test.xcamera.home.ActivationHelper.CLOUD_APP_VERSION_SPKEY;
import static com.test.xcamera.upgrade.BaseUpdateDialogFragment.INTENT_KEY;

/**
 * Created by zhouxuecheng on
 * Create Time 2020/4/14
 * e-mail zhouxuecheng1991@163.com
 */

public class DialogAppUpData implements View.OnClickListener, DownloadCloudFileTask.DownloadFileTaskListener {

    private final Activity activity;
    private final VersionInfo mVersionData;
    private Dialog mDialog;
    private View view;
    private NumberProgressBar mDialogPregressbar;
    private Button mDialogConfirm;
    private final long SDCARD_MIN_SIZE = 300 * 1024 * 1024;
    private int PERMISSION_REQUEST_WRITE_SD = 1024;
    private TextView temporaryNoUpdata;

    public DialogAppUpData(Activity activity, VersionInfo mVersionData) {
        this.mVersionData = mVersionData;
        this.activity = activity;
        initDialogView();
        initView();
    }

    public void showDialog() {
        if (mDialog != null && !mDialog.isShowing() && !activity.isDestroyed() && !activity.isFinishing()) {
            mDialog.show();
        }
    }

    private void initView() {
        TextView dialogTitle = view.findViewById(R.id.update_dialog_title);
        TextView dialogContent = view.findViewById(R.id.update_dialog_info);
        mDialogPregressbar = view.findViewById(R.id.update_dialog_progressbar);
        mDialogConfirm = view.findViewById(R.id.update_dialog_confirm);
        temporaryNoUpdata = view.findViewById(R.id.temporaryNoUpdata);
        dialogTitle.setText("V" + mVersionData.getVersion());
        dialogContent.setText(mVersionData.getContent());
        mDialogConfirm.setOnClickListener(this);
        temporaryNoUpdata.setOnClickListener(this);
    }

    public void settemporaryNoUpdataText(String text) {
        if (temporaryNoUpdata != null)
            temporaryNoUpdata.setText(text);
    }

    private void initDialogView() {
        view = View.inflate(activity, R.layout.dialog_appupdata_layout, null);
        mDialog = new Dialog(activity);
        mDialog.setContentView(view);

        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        mDialog.getWindow().setAttributes(params);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    /**
     * 点击dialog外部是否影藏dialog
     *
     * @param isHide
     */
    public void setDialogTouchOutside(boolean isHide) {
        mDialog.setCanceledOnTouchOutside(isHide);
        mDialog.setCancelable(isHide);
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.update_dialog_confirm) {
            if (!NetworkUtil.isNetworkConnected(activity)) {
                CameraToastUtil.show(activity.getResources().getString(R.string.no_network), activity);
                return;
            }
            boolean b = StorageUtils.externalMemoryAvailable();
            if (!b) {
                CameraToastUtil.show(activity.getResources().getString(R.string.stor_no_external), activity);
                return;
            }
            long availableExternalMemorySize = StorageUtils.getAvailableExternalMemorySize();
            if ((availableExternalMemorySize) < SDCARD_MIN_SIZE) {
                CameraToastUtil.show(activity.getResources().getString(R.string.stor_no), activity);
                return;
            }
            checkPermission();
        } else if (vid == R.id.temporaryNoUpdata) {
            if (temporaryNoUpdata.getText().toString().trim().equals(activity.getResources().getString(R.string.noUpdata))) {
                //关闭dialog记录一个标记,等下次有新版本在提示
                SPUtils.put(activity, CLOUD_APP_VERSION_SPKEY, mVersionData.getVersion());
                dismissDialog();
            } else {
                //退出app
                dismissDialog();
                if (activity != null) {
                    activity.finishAffinity();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }
        }
    }

    private void dismissDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog.cancel();
            mDialog = null;
        }
    }

    /**
     * 校验权限
     */
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_WRITE_SD);
        } else {
            if (!TextUtils.isEmpty(mVersionData.getVersion())) {
                if (mVersionData.isMustUp()) {
                    openDownloadTask();
                } else if (mVersionData.getViewStyle() == BaseVersion.NOTIFYCATION_STYLE) {
                    activity.startService(new Intent(activity, NotifyDownloadService.class).putExtra(INTENT_KEY, mVersionData));
                    if (mDialog != null) {
                        mDialog.dismiss();
                    }
                } else {
                    openDownloadTask();
                }
            } else {
                Toast.makeText(activity, "下载地址不能为空", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 开启下载
     */
    protected void openDownloadTask() {
        DownloadCloudFileTask downloadFileTask = new DownloadCloudFileTask(this);
        downloadFileTask.startDownload(Constants.base_url + "api/app/package/" + mVersionData.getVersion(), mVersionData.getpath(), mVersionData.getVersion() + ".apk");
    }

    @Override
    public void startTask() {
        mDialogConfirm.setVisibility(View.GONE);
        temporaryNoUpdata.setVisibility(View.GONE);
        mDialogPregressbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProgress(int progress) {
        mDialogPregressbar.setProgress(progress);
    }

    @Override
    public void onSuccess(File file) {
        onDownLoadFinish(file);
    }

    private void onDownLoadFinish(File file) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        if (file != null && file.exists()) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri contentUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            activity.startActivity(intent);
        }
    }

    @Override
    public void onFailed(String errorInfo) {
        if (activity.isFinishing() || activity.isDestroyed()) {
            return;
        }
        if (errorInfo.contains(DownloadCloudFileTask.ERROR_MESSAGE_3)) {
            CameraToastUtil.show(activity.getResources().getString(R.string.net_down_error_apk), activity);
        } else if (errorInfo.contains(DownloadCloudFileTask.ERROR_MESSAGE_4)) {
            CameraToastUtil.show(activity.getResources().getString(R.string.net_down_error_apk_timeout), activity);
        } else if (errorInfo.contains(DownloadCloudFileTask.ERROR_MESSAGE_1)) {
            CameraToastUtil.show(activity.getResources().getString(R.string.net_down_error_apk_cloud_file_error), activity);
        }
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    public void onPaused() {

    }

    @Override
    public void onCanceled() {

    }

    public boolean isShow() {
        if (mDialog != null)
            return mDialog.isShowing();
        return false;
    }
}
