package com.test.xcamera.utils;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;

import com.test.xcamera.R;
import com.test.xcamera.accrssory.AccessoryManager;
import com.test.xcamera.api.ApiImpl;
import com.test.xcamera.api.CallBack;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.AppVersion;
import com.test.xcamera.bean.MoDeviceInfo;
import com.test.xcamera.home.GoUpActivity;
import com.test.xcamera.home.HomeViewInterface;
import com.test.xcamera.ota.UploadCameraFile;
import com.test.xcamera.ota.UploadHardWareActivity;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.mointerface.MoCheckUploadCallback;
import com.test.xcamera.mointerface.MoDeviceInfoCallback;
import com.test.xcamera.statistic.StatisticOneKeyMakeVideo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Author: mz
 * Time:  2019/10/11
 * app 升级   固件文件下载
 */
public class VersionCheck implements DownloadCloudFileTask.DownloadFileTaskListener {
    private final String TAG = "OTA_LOG";
    public static final String VERSION_KEY = "ota_version";
    private FragmentManager manager;
    private HomeViewInterface viewInterface;
    private Handler mHandler;

    public AppVersion.AppVersionDetail getDetail() {
        return detail;
    }

    private AppVersion.AppVersionDetail detail;
    private String deviceDid = "";
    private DownloadCloudFileTask downloadFileTask;

    public VersionCheck(HomeViewInterface viewInterface, FragmentManager manager, Handler mHandler) {
        this.viewInterface = viewInterface;
        this.manager = manager;
        this.mHandler = mHandler;
    }

    /**
     * 效验固件版本
     *
     * @param hardwareVersion
     */
    public void checkHardwareVsersion(final String hardwareVersion, final String hardwaredid) {
        Log.i(TAG, "PushHardWareVersion: " + hardwareVersion + " hardwaredid = " + hardwaredid);
        ApiImpl.getInstance().PushHardWareVersion(hardwareVersion, hardwaredid, new CallBack<AppVersion>() {
            @Override
            public void onSuccess(AppVersion appVersion) {
                if (appVersion.isSucess()) {
                    Log.i(TAG, "PushHardWareVersion: onSuccess " + appVersion.toString());
                    if (appVersion.getData().size() == 0) {
                        if (viewInterface instanceof GoUpActivity) {
                            if (viewInterface != null) {
                                ((GoUpActivity) viewInterface).goneLayout();
                            }
                        }
                        return;
                    }
                    detail = appVersion.getData().get(0);
                    if (viewInterface instanceof GoUpActivity) {
                        ((GoUpActivity) viewInterface).setCloudData(hardwareVersion, detail);
                        if (detail.getPkgType() == 2) {
                            String version = (String) SPUtils.get(AiCameraApplication.getContext(), VERSION_KEY, "");
                            if (TextUtils.isEmpty(version) || !TextUtils.isEmpty(version) && !TextUtils.equals(version, detail.getVersion())) {
                                ((GoUpActivity) viewInterface).showVersionDialog(hardwareVersion, detail);
                            }
                            viewInterface.IsVisbilityUploadHardWare();
                        } else if (detail.getPkgType() == 3) {
                            ((GoUpActivity) viewInterface).showVersionDialog(hardwareVersion, detail);
                        }

                        return;
                    }

                    if (viewInterface instanceof UploadHardWareActivity && detail != null) {
                        viewInterface.setContent(detail.getPkgSize(), detail.getReleaseTime(), detail.getDescription(), detail.getVersion());
                        viewInterface.isClickable();
                        ((UploadHardWareActivity) viewInterface).setpkgType(detail.getPkgType());
                    }

                } else {
                    if (viewInterface instanceof UploadHardWareActivity) {
                        ((UploadHardWareActivity) viewInterface).exception();
                    }

                    if (viewInterface instanceof GoUpActivity) {
                        ((GoUpActivity) viewInterface).goneLayout();
                    }
                }
            }

            @Override
            public void onFailure(Throwable e) {
                viewInterface.isClickable();
                if (viewInterface instanceof UploadHardWareActivity) {
                    ((UploadHardWareActivity) viewInterface).exception();
                }

                if (viewInterface instanceof GoUpActivity) {
                    ((GoUpActivity) viewInterface).goneLayout();
                }
            }

            @Override
            public void onCompleted() {
                viewInterface.isClickable();
            }
        });
    }

    /**
     * 检查升级的条件,一个是固件的电量,一个是内存
     */
    public void checkUploadRoomMemoryAndBattery() {
        if (detail == null) {
            return;
        }
        Log.i(TAG, "checkUploadRoomMemoryAndBattery:  ");
        ConnectionManager.getInstance().uploadCheckcond(detail.getVersion(), new MoCheckUploadCallback() {
            @Override
            public void onSuccess(final int reason) {
                Log.i(TAG, "checkUploadRoomMemoryAndBattery: onSuccess " + reason);

                show(reason);

            }

            @Override
            public void onFailed() {
                //满足升级条件可以正常升级
                if (viewInterface instanceof UploadHardWareActivity) {
                    ((UploadHardWareActivity) viewInterface).batteryAndMemorySuccess();
                }
                clickDownload();
            }
        });
    }


    public void clickDownload() {
        if (TextUtils.isEmpty(deviceDid) || detail == null) {
            LoggerUtils.i(TAG, " deviceDid = " + deviceDid);
            return;
        }
        String version = detail.getVersion();
        String hardwarePath = Constants.mFileDirector + version;
        if (FileUtils.isExistFile(hardwarePath)) {
            File file = new File(hardwarePath);
            long length = file.length();
            boolean hardwareFileCheckOk = checkHardwareFile(hardwarePath, detail.getPkgSign());
            if (length == detail.getPkgSize() && hardwareFileCheckOk) {
                //此时可以把app 的文件上传给固件
                Log.i(TAG, "clickDownload: 固件正常");
                viewInterface.setProgresss(50);
                //直接上传
                UploadCameraFile uploadCameraFile = new UploadCameraFile(new Handler(Looper.getMainLooper()), viewInterface, new File(hardwarePath));
                uploadCameraFile.startUpload(version);

            } else if (length == detail.getPkgSize() && !hardwareFileCheckOk) {
                Log.i(TAG, "clickDownload: 固件已经下载完成 MD5 值校验失败,删除包重新下载");
                file.delete();
                downloadHadrWareFile(detail.getVersion(), deviceDid, hardwarePath);
            } else {
                Log.i(TAG, "clickDownload: 固件还没有下载完成,直接下载");
                downloadHadrWareFile(detail.getVersion(), deviceDid, hardwarePath);
            }
        } else {
            //没下载过  直接下载
            downloadHadrWareFile(detail.getVersion(), deviceDid, hardwarePath);
        }
    }


    /**
     * @param reason 0：无原因
     *               1：无sd卡或sd卡容量不足
     *               2：电池电量低
     *               3：版本号不兼容
     *               4：未知
     */
    public void show(int reason) {
        if (viewInterface != null && viewInterface instanceof UploadHardWareActivity) {
            UploadHardWareActivity viewInterface = (UploadHardWareActivity) this.viewInterface;
            String msg = "";
            switch (reason) {
                case 1:
                    msg = viewInterface.getBaseContext().getResources().getString(R.string.sdcard_status_full);
                    break;
                case 2:
                    msg = viewInterface.getBaseContext().getResources().getString(R.string.bat_low_30);
                    break;
                case 3:
                    msg = viewInterface.getBaseContext().getResources().getString(R.string.version_no);
                    break;
                case 4:
                    msg = viewInterface.getBaseContext().getResources().getString(R.string.device_s);
                    break;
                case 5:
                    msg = viewInterface.getBaseContext().getResources().getString(R.string.sdcard_out);
                    break;
            }
            final String finalMsg = msg;
            ((UploadHardWareActivity) this.viewInterface).showUpLoadErrorDialog(finalMsg,
                    AiCameraApplication.mApplication.getResources().getString(R.string.update_title));
            setPointData(detail.getVersion(), msg);
        }
    }


    public void downloadHadrWareFile(String version, final String hardwaredid, String path) {
        //对当前网络环境的判断
        if (viewInterface instanceof UploadHardWareActivity) {
            ((UploadHardWareActivity) viewInterface).netWorkCheck(version, hardwaredid, path);
        }
    }

    public void downLoadFile(String version, final String hardwaredid, String path) {
        if (TextUtils.isEmpty(version) || TextUtils.isEmpty(hardwaredid) || TextUtils.isEmpty(path)) {
            return;
        }
        String resultDid = null;
        try {
            resultDid = URLEncoder.encode(hardwaredid, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        cancelDownload();
        downloadFileTask = new DownloadCloudFileTask(this);
        String url = Constants.base_url + "api/firmware/package/" + version + "?did=" + resultDid;
        if (!downloadFileTask.isCancelled())
            downloadFileTask.execute(url, Constants.mFileDirector, getFileName(path));
    }

    public void cancelDownload() {
        if (downloadFileTask != null) {
            downloadFileTask.cancel(true);
            downloadFileTask = null;
        }
    }

    /**
     * check tips can show
     */
    public void getDevicedInfo() {
        if (AccessoryManager.getInstance().mIsRunning) {
            ConnectionManager.getInstance().getDeviceInfo(new MoDeviceInfoCallback() {
                @Override
                public void onSuccess(MoDeviceInfo deviceInfo) {
                    checkHardwareVsersion(deviceInfo.getmFirmwareVersion(), deviceInfo.getDid());
                    StatisticOneKeyMakeVideo.getInstance().setOnEvent("Connect_Camera_Get_Hardware_Version", deviceInfo.getmFirmwareVersion());
                    Log.i(TAG, "onSuccess:getDevicedInfo " + deviceInfo.getmFirmwareVersion() + " did = " + deviceInfo.getDid());
                    deviceDid = deviceInfo.getDid();
                }

                @Override
                public void onFailed() {
                    Log.i(TAG, "onFailed: getDevicedInfo ");
                    if (viewInterface instanceof UploadHardWareActivity) {
                        ((UploadHardWareActivity) viewInterface).exception();
                    }

                    if (viewInterface instanceof GoUpActivity) {
                        ((GoUpActivity) viewInterface).goneLayout();
                    }
                }
            });
        } else {
            if (viewInterface instanceof GoUpActivity) {
                ((GoUpActivity) viewInterface).goneLayout();
            }
        }
    }

    @Override
    public void startTask() {

    }

    @Override
    public void onProgress(int progress) {
        if (viewInterface instanceof UploadHardWareActivity) {
            viewInterface.setProgresss(progress / 2);
        }
    }

    @Override
    public void onSuccess(File file) {
        Log.i(TAG, "downloadHadrWareFile: onFinish ok path = " + file.getAbsolutePath());
        boolean hardwareFileCheckOk = checkHardwareFile(file.getAbsolutePath(), detail.getPkgSign());
        if (hardwareFileCheckOk) {
            //上传固件到设备
            UploadCameraFile uploadCameraFile = new UploadCameraFile(mHandler, viewInterface, file);
            uploadCameraFile.startUpload(getFileName(file.getAbsolutePath()));
        } else {
            setPointData(detail.getVersion(), "hardware md5 check failed ");
            ((UploadHardWareActivity) viewInterface).showUpLoadErrorDialog(AiCameraApplication.mApplication.getResources().getString(R.string.md5_error),
                    AiCameraApplication.mApplication.getResources().getString(R.string.update_title));
        }
        cancelDownload();
    }

    @Override
    public void onFailed(String errorInfo) {
        setPointData(detail.getVersion(), errorInfo);
        if (errorInfo.contains(DownloadCloudFileTask.HARDWARE_DID)) {
            viewInterface.setProgresss(400);
        } else if (errorInfo.contains(DownloadCloudFileTask.ERROR_MESSAGE_4)) {
            viewInterface.setProgresss(-500);
        } else {
            viewInterface.setProgresss(-100);
        }
        cancelDownload();
        Log.i(TAG, "downloadHadrWareFile: onFail " + errorInfo);
    }

    @Override
    public void onPaused() {

    }

    @Override
    public void onCanceled() {

    }

    /**
     * 校验下载包的完整性,md5 值进行比较,包校验完成之后,才可以上传
     *
     * @param hardwareFilePath
     * @param pkgSign
     * @return
     */
    private boolean checkHardwareFile(String hardwareFilePath, String pkgSign) {
        String fileMd5 = Md5Util.getFileMD5(hardwareFilePath);
        int index = -1;
        char[] chars = pkgSign.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            String c = chars[i] + "";
            if (!TextUtils.equals(c, "0")) {
                index = i;
                break;
            }
        }
        pkgSign = pkgSign.substring(index, pkgSign.length());
        Log.i(TAG, "checkHardwareFile: " + index + " pkgSign == " + pkgSign + "  fileMd5 = " + fileMd5);
        return fileMd5.equals(pkgSign);
    }

    private String getFileName(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return "";
        }
        int lastIndexOf = filePath.lastIndexOf("/");
        return filePath.substring(lastIndexOf, filePath.length());
    }

    private void setPointData(String version, String reason) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("hardware_version", version);
            jsonObject.put("updata_failed_reason", reason);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StatisticOneKeyMakeVideo.getInstance().setOnEvent("Hardware_Updata_Failed_Reason", jsonObject.toString());
    }
}
