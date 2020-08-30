package com.test.xcamera.ota;

import android.app.Dialog;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.framwork.base.view.MOBaseActivity;
import com.test.xcamera.R;
import com.test.xcamera.accrssory.AccessoryManager;
import com.test.xcamera.home.HomeActivity;
import com.test.xcamera.home.HomeViewInterface;
import com.test.xcamera.managers.DataManager;
import com.test.xcamera.mointerface.OtaCallback;
import com.test.xcamera.statistic.StatisticOneKeyMakeVideo;
import com.test.xcamera.utils.CameraToastUtil;
import com.test.xcamera.utils.VersionCheck;
import com.test.xcamera.utils.DateUtils;
import com.test.xcamera.utils.NetworkUtil;
import com.test.xcamera.view.basedialog.dialog.ClosePageDialog;
import com.test.xcamera.view.basedialog.dialog.Net4GDialog;
import com.test.xcamera.view.basedialog.dialog.UpLoadRoomDialog;
import com.test.xcamera.view.basedialog.dialog.UploadFailedDialog;
import com.test.xcamera.view.basedialog.dialog.UploadSuccessDialog;
import com.test.xcamera.widget.UploadDialog;
import com.moxiang.common.logging.Logcat;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by smz on 2019/11/8.
 */
public class UploadHardWareActivity extends MOBaseActivity implements HomeViewInterface, OtaCallback {
    @BindView(R.id.tv_size)
    TextView tvSize;
    @BindView(R.id.tv_updata_time)
    TextView tvUpdataTime;
    @BindView(R.id.tv_upload)
    TextView tvUpload;
    @BindView(R.id.tv_5)
    TextView tv5;
    @BindView(R.id.tv_upload_process)
    TextView tvUploadProcess;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.buttonLayout)
    LinearLayout buttonLayout;
    @BindView(R.id.progressLayout)
    LinearLayout progressLayout;
    @BindView(R.id.closePageImage)
    ImageView closePageImage;
    @BindView(R.id.noUpLoad)
    TextView noUpLoad;
    @BindView(R.id.version)
    TextView textViewVersion;
    @BindView(R.id.tv_middle_title)
    TextView tv_middle_title;

    private UploadDialog dialog;
    private VersionCheck versionCheck;
    private Handler mHandler = new Handler();
    private String version;
    private String hardwaredid;
    private String path;
    private ClosePageDialog closePageDialog;
    private int pkgType;

    @Override
    public int initView() {
        return R.layout.activity_upload_hard_ware;
    }

    @Override
    public void initData() {
        versionCheck = new VersionCheck(this, getSupportFragmentManager(), mHandler);
        versionCheck.getDevicedInfo();

        initDialog();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        DataManager.getInstance().setOtaCallback(this);
    }

    @Override
    public void onBackPressed() {
        String trim = tvUpload.getText().toString().trim();
        if (trim.equals(getResourceToString(R.string.upload_monitor)) && AccessoryManager.getInstance().mIsRunning) {
            CameraToastUtil.show(getResources().getString(R.string.hardware_updataing), this);
        } else {
            super.onBackPressed();
        }
    }

    public void initDialog() {
        if (dialog == null) {
            dialog = new UploadDialog(mContext, R.layout.dialog_upload, R.style.DialogTheme);
        }

        if (closePageDialog == null)
            closePageDialog = new ClosePageDialog(UploadHardWareActivity.this);

        dialog.setDialogListener(new UploadDialog.DialogClickListener() {
            @Override
            public void onCancleListener(View v) {
                dialog.dismiss();
            }

            @Override
            public void onConfirmListener(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void setVisable(final int Visable1, final int Visable2) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvUpload.setVisibility(Visable1);
                tv5.setVisibility(Visable2);
                tvUploadProcess.setVisibility(Visable2);
                progress.setVisibility(Visable2);
            }
        });

    }

    public void setpkgType(int pkgType) {
        this.pkgType = pkgType;
        if (pkgType == 2) {
            closePageImage.setVisibility(View.VISIBLE);
            noUpLoad.setVisibility(View.VISIBLE);
        } else {
            closePageImage.setVisibility(View.GONE);
            noUpLoad.setVisibility(View.GONE);
        }
    }

    @Override
    public UploadDialog getDialog() {
        if (dialog == null) {
            initDialog();
        }
        return dialog;
    }

    @Override
    public void setContent(final int size, final long time, String description, final String version) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (tvSize == null || tvUpdataTime == null || textViewVersion == null || tv_middle_title == null) {
                    return;
                }
                tvSize.setText(size / (1024 * 1024) + "M");
                tvUpdataTime.setText(DateUtils.timeToDate("yyyy-MM-dd", time));
                textViewVersion.setText("v " + version);
                tv_middle_title.setText(description);
            }
        });
    }

    @Override
    public void StartAct(Class clazz) {
        startAct(HomeActivity.class);
    }

    @Override
    public void isClickable() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvUpload.setClickable(true);
                tvUpload.setBackgroundResource(R.drawable.bt_login_bg_normal);
            }
        });
    }


    @OnClick({R.id.tv_upload, R.id.closePageImage, R.id.noUpLoad})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.noUpLoad:
            case R.id.closePageImage:
                versionCheck.cancelDownload();
                finish();
                break;
            case R.id.tv_upload:
                if (!AccessoryManager.getInstance().mIsRunning) {
                    CameraToastUtil.show(getResourceToString(R.string.connectCamera), this);
                    return;
                }
                tvUpload.setText(getResourceToString(R.string.upload_monitor));
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        versionCheck.checkUploadRoomMemoryAndBattery();
                    }
                }, 2000);
                break;

        }

    }

    @Override
    public void IsVisbilityUploadHardWare() {

    }

    @Override
    public void setProgresss(final int mProgress) {
        Log.i("OTA_LOG", "setProgresss( " + mProgress + " ) ");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgress > 50 && mProgress != -100 && mProgress != 400) {
                    //这个显示的上传固件的进度
                    uploadFileProgress(mProgress);
                } else {
                    if (mProgress == -100) {
                        showNetWorkAvailable(getResources().getString(R.string.net_work_Available));
                        return;
                    } else if (mProgress == 400) {
                        showNetWorkAvailable(getResources().getString(R.string.hardware_did_error));
                        return;
                    } else if (mProgress == -500) {
                        showNetWorkAvailable(getResources().getString(R.string.net_down_timeout));
                        return;
                    }
                    boolean networkAvailable = NetworkUtil.isNetworkAvailable(UploadHardWareActivity.this);
                    if (networkAvailable) {
                        if (!NetworkUtil.isWifiConnected(UploadHardWareActivity.this) &&
                                !NetworkUtil.isMobileConnected(UploadHardWareActivity.this)) {
                            showNetWorkAvailable(getResources().getString(R.string.net_work_Available));
                        } else {
                            //这个显示的是下载固件的进度
                            uploadFileProgress(mProgress);
                        }
                    } else {
                        //网络环境不可用
                        showNetWorkAvailable(getResources().getString(R.string.net_work_Available));
                    }
                }
            }
        });
    }

    private void uploadFileProgress(int mProgress) {
        if (tvUploadProcess != null && progress != null) {
            String mProcess = getString(R.string.eleary_down, mProgress);
            tvUploadProcess.setText(mProcess + " %");
            progress.setProgress(mProgress);
            if (pkgType == 2) {
                if (mProgress > 50) {
                    closePageImage.setVisibility(View.GONE);
                } else {
                    closePageImage.setVisibility(View.VISIBLE);
                }
            }
        } else {
            Log.i("OTA_LOG", "run: tvUploadProcess == null && progress == null " + tvUploadProcess + " progress " + progress);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraUploadOk && progress.getProgress() == 100) {
            showSuccessDialog();
        }
    }

    @Override
    public void setTitleAndContent(String title, String Content) {
        if (dialog != null) {
            dialog.setTitle(title);
            dialog.setContent(Content);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        closePageDialog = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (versionCheck != null) {
            versionCheck.cancelDownload();
        }
    }

    @Override
    public void disconnectedUSB() {
        if (closePageDialog == null || isDestroyed() || isFinishing()) {
            return;
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (cameraUploadOk) {

                } else {
                    showDisconnect(0);
                }
            }
        }, 1000);
    }

    private void showDisconnect(long time) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (closePageDialog == null || isDestroyed() || isFinishing()) {
                    return;
                }
                closePageDialog.setDialogContent(14, getResources().getString(R.string.disconenct_usb));
                closePageDialog.showGoneTitleDialog();
                closePageDialog.setSureClick(new ClosePageDialog.SureClick() {
                    @Override
                    public void onSure(Dialog mDialog) {
                        StartAct(HomeActivity.class);
                        finish();
                    }
                });
            }
        }, time);
    }

    public void showUpLoadErrorDialog(String finalMsg, String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isDestroyed() || isFinishing()) {
                    return;
                }
                UpLoadRoomDialog upLoadRoomDialog = new UpLoadRoomDialog(UploadHardWareActivity.this);
                upLoadRoomDialog.setDialogContent(14, finalMsg);
                upLoadRoomDialog.showVisibilityTitleDialog(16, title);
                upLoadRoomDialog.setSureClick(new UpLoadRoomDialog.SureClick() {
                    @Override
                    public void onSure(Dialog mDialog) {
                        mDialog.dismiss();
                        UploadHardWareActivity.this.finish();
                    }
                });
            }
        });
    }

    /**
     * 升级环境检查成功
     */
    public void batteryAndMemorySuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (buttonLayout == null || progressLayout == null) {
                    return;
                }
                buttonLayout.setVisibility(View.GONE);
                progressLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 检查网络环境
     *
     * @param version
     * @param hardwaredid
     * @param path
     */
    public void netWorkCheck(String version, String hardwaredid, String path) {
        this.version = version;
        this.hardwaredid = hardwaredid;
        this.path = path;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean networkAvailable = NetworkUtil.isNetworkAvailable(UploadHardWareActivity.this);
                if (networkAvailable) {
                    if (NetworkUtil.isWifiConnected(UploadHardWareActivity.this)) {
                        versionCheck.downLoadFile(version, hardwaredid, path);
                    } else if (!NetworkUtil.isWifiConnected(UploadHardWareActivity.this) &&
                            NetworkUtil.isMobileConnected(UploadHardWareActivity.this)) {
                        show4GDialog(version, hardwaredid, path);
                    } else if (!NetworkUtil.isWifiConnected(UploadHardWareActivity.this) && !NetworkUtil.isMobileConnected(UploadHardWareActivity.this)) {
                        showNetWorkAvailable(getResources().getString(R.string.net_work_Available));
                    }
                } else {
                    showNetWorkAvailable(getResources().getString(R.string.net_work_Available));
                }
            }
        });
    }

    private boolean is4GDown = false;

    private void show4GDialog(String version, String hardwaredid, String path) {
        if (is4GDown || isDestroyed() || isFinishing()) {
            return;
        }
        Net4GDialog net4GDialog = new Net4GDialog(this);
        net4GDialog.setDialogContent(14, getResources().getString(R.string.net_4g));
        net4GDialog.showGoneTitleDialog();
        net4GDialog.setSureClick(new Net4GDialog.SureClick() {
            @Override
            public void onSure(Dialog mDialog) {
                is4GDown = true;
                mDialog.dismiss();
                versionCheck.downLoadFile(version, hardwaredid, path);
            }

            @Override
            public void onCancel(Dialog mDialog) {
                mDialog.dismiss();
                finish();
            }
        });
    }

    private void showNetWorkAvailable(String text) {
        if (isDestroyed() || isFinishing()) {
            return;
        }
        ClosePageDialog closePageDialog = new ClosePageDialog(this);
        closePageDialog.setDialogContent(14, text);
        closePageDialog.showGoneTitleDialog();
        closePageDialog.setSureClick(new ClosePageDialog.SureClick() {
            @Override
            public void onSure(Dialog mDialog) {
                mDialog.dismiss();
                finish();
            }
        });
    }

    /**
     * 升级失败
     */
    public void uploadFailed() {
        showFialedDialog();
    }

    public void upDataError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isDestroyed() || isFinishing()) {
                    return;
                }
                UploadFailedDialog uploadFailedDialog = new UploadFailedDialog(UploadHardWareActivity.this);
                uploadFailedDialog.setDialogContent(14, getResources().getString(R.string.updata_failed));
                uploadFailedDialog.showVisibilityTitleDialog(16, getResources().getString(R.string.update_title));
                uploadFailedDialog.setSureButtonContent(getResources().getString(R.string.reset_upload), 0, 0);
                uploadFailedDialog.setSureClick(new UploadFailedDialog.SureClick() {
                    @Override
                    public void onSure(Dialog mDialog) {
                        mDialog.dismiss();
                        if (versionCheck != null) {
                            versionCheck.clickDownload();
                        }
                    }

                    @Override
                    public void onCancel(Dialog mDialog) {
                        mDialog.dismiss();
                        startAct(HomeActivity.class);
                    }
                });
            }
        });
    }

    private boolean cameraUploadOk = false;

    @Override
    public void otaStatus(int status) {
        cameraUploadOk = true;
        Logcat.v().tag("OTA_LOG").msg("升级成功,相机推送回调 " + status).out();
        if (status == 0) {
            StatisticOneKeyMakeVideo.getInstance().setOnEvent("Hardware_Updata_Success", version);
            showSuccessDialog();
        } else {
            StatisticOneKeyMakeVideo.getInstance().setOnEvent("Hardware_Updata_Failed_Reason", getPointValue(status));
            showFialedDialog();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (tvUploadProcess != null && progress != null) {
                    String mProcess = getString(R.string.eleary_down, 100);
                    tvUploadProcess.setText(mProcess + " %");
                    progress.setProgress(100);
                }
            }
        });
    }

    private String getPointValue(int status) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("hardware_version", version);
            jsonObject.put("updata_failed_reason", " hardware reported data is error " + status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private void showFialedDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isDestroyed() || isFinishing()) {
                    return;
                }
                UploadFailedDialog uploadFailedDialog = new UploadFailedDialog(UploadHardWareActivity.this);
                uploadFailedDialog.setDialogContent(14, getResources().getString(R.string.upload_failed));
                uploadFailedDialog.showVisibilityTitleDialog(16, getResources().getString(R.string.update_title));
                uploadFailedDialog.setSureButtonContent(getResources().getString(R.string.reset_upload), 0, 0);
                uploadFailedDialog.setSureClick(new UploadFailedDialog.SureClick() {
                    @Override
                    public void onSure(Dialog mDialog) {
                        mDialog.dismiss();
                        if (versionCheck != null) {
                            versionCheck.clickDownload();
                        }
                    }

                    @Override
                    public void onCancel(Dialog mDialog) {
                        mDialog.dismiss();
                        startAct(HomeActivity.class);
                    }
                });
            }
        });
    }

    private void showSuccessDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isDestroyed() || isFinishing()) {
                    return;
                }
                Logcat.v().tag("OTA_LOG").msg("升级成功,相机推送回调 显示弹框 !!!").out();
                UploadSuccessDialog uploadSuccessDialog = new UploadSuccessDialog(UploadHardWareActivity.this);
                uploadSuccessDialog.setDialogContent(14, getResources().getString(R.string.upload_ok));
                uploadSuccessDialog.showVisibilityTitleDialog(16, getResources().getString(R.string.update_title));
                uploadSuccessDialog.setSureClick(new UploadSuccessDialog.SureClick() {
                    @Override
                    public void onSure(Dialog mDialog) {
                        mDialog.dismiss();
                        finish();
                        startAct(HomeActivity.class);
                    }
                });
            }
        });
    }

    public void exception() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showNetWorkAvailable(getResources().getString(R.string.device_exception));
            }
        });
    }
}
