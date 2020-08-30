package com.test.xcamera.upgrade;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.test.xcamera.R;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.Constants;
import com.test.xcamera.utils.DownloadCloudFileTask;

import java.io.File;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

/**
 * creat  by mz
 * 2019.10.12
 */
public abstract class BaseUpdateDialogFragment extends DialogFragment implements DownloadCloudFileTask.DownloadFileTaskListener {

    private Activity mActivity;
    public VersionInfo mVersionData;
    private int PERMISSION_REQUEST_WRITE_SD = 1024;
    public static String INTENT_KEY = "result_key";
    //    private DownloadTask mDownloadTask;
    private Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_Design_BottomSheetDialog_NoActionBar);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate = LayoutInflater.from(getActivity()).inflate(getLayoutId(), null);
        mActivity = getActivity();
        return inflate;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mVersionData = (VersionInfo) getArguments().getSerializable(INTENT_KEY);
        initView(view, mVersionData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public abstract int getLayoutId();

    /**
     * 初始化UI
     *
     * @param view
     * @param versionData
     */
    protected abstract void initView(View view, VersionInfo versionData);

    /**
     * 当下载开始前（MainThred）
     */
    protected abstract void onDownLoadStart();

    /**
     * 当下载中（MainThred）
     *
     * @param progress
     */
    protected abstract void onDownLoadUpdate(int progress);

    /**
     * 当下在完成
     *
     * @param file
     */
    protected void onDownLoadFinish(File file) {
        if (file != null && file.exists()) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri contentUri = FileProvider.getUriForFile(mActivity, mActivity.getPackageName() + ".fileprovider", file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            startActivity(intent);
        }
    }

    /**
     * 校验权限
     */
    void checkPermission() {

        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_WRITE_SD);
        } else {
            if (!TextUtils.isEmpty(mVersionData.getVersion())) {
                if (mVersionData.isMustUp()) {
                    openDownloadTask();
                } else if (mVersionData.getViewStyle() == BaseVersion.NOTIFYCATION_STYLE) {
                    mActivity.startService(new Intent(mActivity, NotifyDownloadService.class).putExtra(INTENT_KEY, mVersionData));
                    dismiss();//关掉更新提示dialog
                } else {
                    openDownloadTask();
                }
            } else {
                Toast.makeText(getActivity(), "下载地址不能为空", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 开启下载
     */
    protected void openDownloadTask() {
        DownloadCloudFileTask downloadFileTask = new DownloadCloudFileTask(this);
        downloadFileTask.startDownload(Constants.base_url + "api/app/package/" + mVersionData.getVersion(), mVersionData.getpath(), mVersionData.getVersion());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_WRITE_SD && grantResults[0] == PackageManager.PERMISSION_GRANTED) {//开通权限
            startDownload();
        }
    }

    protected void startDownload() {
        checkPermission();
    }

    /**
     * 强制更新提示
     */
    public void showMustUpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        AlertDialog dialog = builder.setTitle("温馨提示")
                .setMessage("本次更新是我们的一大步，放弃更新将会退出应用哦~!")
                .setNegativeButton("关闭应用", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getActivity().finish();
                    }
                }).setPositiveButton("继续更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        checkPermission();
                    }
                }).show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLUE);
    }

    /**
     * 通知权限
     */
    public void showNotifyPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        AlertDialog dialog = builder.setTitle("温馨提示")
                .setMessage("下载更新需要允许应用通知,是否打开应用通知")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("去打开", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent();
                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.setData(Uri.fromParts("package", getActivity().getPackageName(), null));
                        startActivity(intent);
                    }
                }).show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLUE);
    }

    @Override
    public void startTask() {
        onDownLoadStart();
    }

    @Override
    public void onProgress(int progress) {
        onDownLoadUpdate(progress);
    }

    @Override
    public void onSuccess(File file) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onDownLoadFinish(file);
                if (isVisible())
                    dismissAllowingStateLoss();//关闭下载中弹窗,升级过程中然后home按键等下载完毕后会crash
            }
        });
    }

    @Override
    public void onFailed(String errorInfo) {
        if (errorInfo.contains(DownloadCloudFileTask.ERROR_MESSAGE_3)) {
            CameraToastUtil.show(getResources().getString(R.string.net_down_error_apk), getContext());
        } else if (errorInfo.contains(DownloadCloudFileTask.ERROR_MESSAGE_4)) {
            CameraToastUtil.show(getResources().getString(R.string.net_down_error_apk_timeout), getContext());
        } else if (errorInfo.contains(DownloadCloudFileTask.ERROR_MESSAGE_1)) {
            CameraToastUtil.show(getResources().getString(R.string.net_down_error_apk_cloud_file_error), getContext());
        }
        if (isVisible())
            dismissAllowingStateLoss();
    }

    @Override
    public void onPaused() {

    }

    @Override
    public void onCanceled() {

    }
}
