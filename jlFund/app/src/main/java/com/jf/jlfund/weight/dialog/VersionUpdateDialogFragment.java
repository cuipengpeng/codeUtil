package com.jf.jlfund.weight.dialog;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jf.jlfund.R;
import com.jf.jlfund.base.BaseApplication;
import com.jf.jlfund.base.CommonActivity;
import com.jf.jlfund.http.ProgressResponseBody;
import com.jf.jlfund.receiver.NotificationClickReceiver;
import com.jf.jlfund.utils.PackageUtils;
import com.jf.jlfund.utils.VersionUpdateManager;

import java.io.File;


/**
 * Created by 55 on 2017/11/7.
 */

public class VersionUpdateDialogFragment extends DialogFragment implements ProgressResponseBody.ProgressListener {
    private static final String TAG = "VersionUpdateDialogFrag";

    private TextView tvContent;
    private CustomProgressBar customProgressBar;
    private TextView tvUpdateNow;
    private TextView tvUpdateLater;
    private View vSplit;


    private String updateContent = "";
    private boolean isForceUpdate = false;
    private long apkSize = -1;
    public static boolean isDownloadInBackground = false;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.VersionUpdateDialogStyle);
        dialog.setContentView(R.layout.dialog_version_update);
        init(dialog);
        return dialog;
    }

    private void init(Dialog dialog) {
        VersionUpdateManager.getInstance().setProgressListener(this);
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.68), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        tvContent = dialog.findViewById(R.id.tv_dialogVersionUpdate_content);
        customProgressBar = dialog.findViewById(R.id.progressBar_dialogVersionUpdate);
        tvUpdateNow = dialog.findViewById(R.id.tv_dialogVersionUpdate_updateNow);
        tvUpdateLater = dialog.findViewById(R.id.tv_dialogVersionUpdate_updateLater);
        vSplit = dialog.findViewById(R.id.v_split_dialogVersionUpdate);
        initData();
        initListener();
    }

    private void initData() {
        tvContent.setText(updateContent);
    }

    private void initListener() {
        tvUpdateNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvUpdateNow.getText().toString().equals("马上更新") && customProgressBar.getVisibility() != View.VISIBLE) {     //马上更新
                    ((CommonActivity) getActivity()).checkPermissions(new CommonActivity.CheckPermListener() {
                        @Override
                        public void superPermission() {
                            isDownloadInBackground = false;
                            tvUpdateNow.setText("后台下载");
                            tvContent.setVisibility(View.GONE);
                            tvUpdateLater.setVisibility(View.GONE);
                            vSplit.setVisibility(View.GONE);
                            customProgressBar.setVisibility(View.VISIBLE);
                            VersionUpdateManager.getInstance().downloadApk();
                        }
                    }, R.string.permission_tip, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                } else if (tvUpdateNow.getText().toString().equals("后台下载") && tvUpdateLater.getVisibility() != View.VISIBLE) {     //后台下载
                    isDownloadInBackground = true;
                    initNotification();
                    dismiss();
                }
            }
        });

        tvUpdateLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


    public void setUpdateContent(String updateContent) {
        this.updateContent = updateContent;
    }

    public void setForceUpdate(boolean forceUpdate) {
        if (this.isForceUpdate != forceUpdate) {
            this.isForceUpdate = forceUpdate;
        }
    }

    public void onApkExists(File apkFile) {
        dismiss();
        PackageUtils.install(apkFile);
    }


    public final static int DOWNLOADING = 1;
    public final static int DOWNLOAD_FINISHED = 0;
    public final static int DOWNLOAD_FAILED = -2;
    public final static int DOWNLOAD_IDLE = 2;
    public static int DOWNLOAD_STATUS = -1;

    @Override
    public void onProgress(long progress, long total, boolean done) {
        Log.d(TAG, "onProgress >>  " + progress + "/" + total + "   done: " + done);
        if (progress == -1 && total == -1) {        //下载失败
            DOWNLOAD_STATUS = DOWNLOAD_FAILED;
            dismissAll();
            return;
        }
        if (apkSize == -1) {
            apkSize = total;
        }
        DOWNLOAD_STATUS = DOWNLOADING;
        if (!isDownloadInBackground) {
            if (customProgressBar != null) {
                customProgressBar.setPercent((float) (progress * 1.0 / total));
            }
            if (progress == total && done) {
                DOWNLOAD_STATUS = DOWNLOAD_FINISHED;
                dismiss();  //下载完成
            }
        } else {
            if (builder == null || manager == null) {
                initNotification();
            }
            if (progress == total && done) {
                DOWNLOAD_STATUS = DOWNLOAD_FINISHED;
                builder.setContentTitle("下载完成");
                builder.setContentText("点击安装");
                isDownloadInBackground = false;
            } else {
                builder.setProgress((int) total, (int) progress, false);
                //show notification
                builder.setContentText("已下载 " + (int) (progress * 100 / total) + "%");
            }
            manager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    private void dismissAll() {
        if (isAdded()) {
            dismiss();
        }
        if (manager == null || builder == null) {
            initNotification();
        }
        manager.cancel(NOTIFICATION_ID);
    }


    private NotificationCompat.Builder builder;
    private NotificationManager manager;
    public static final int NOTIFICATION_ID = 66;


    /**
     * 初始化通话栏
     */
    private void initNotification() {
        manager = (NotificationManager) BaseApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(BaseApplication.getContext());

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("正在下载，请稍后...")
                .setContentText("正在连接...");

        Intent clickIntent = new Intent(BaseApplication.getContext(), NotificationClickReceiver.class);
        clickIntent.setAction("notification_clicked");
        clickIntent.putExtra(NotificationClickReceiver.APK_SIZE, apkSize);
        PendingIntent clickPendingIntent = PendingIntent.getBroadcast(BaseApplication.getContext(), 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(clickPendingIntent);

        Intent cancelIntent = new Intent(BaseApplication.getContext(), NotificationClickReceiver.class);
        cancelIntent.setAction("notification_cancelled");
        clickIntent.putExtra(NotificationClickReceiver.APK_SIZE, apkSize);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(BaseApplication.getContext(), 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setDeleteIntent(cancelPendingIntent);

        builder.setProgress(100, 0, false);

        manager.notify(NOTIFICATION_ID, builder.build());
    }

}
