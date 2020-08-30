package com.test.xcamera.ota;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.test.xcamera.R;

import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.constants.LogcatConstants;
import com.test.xcamera.home.HomeViewInterface;
import com.test.xcamera.mointerface.MoCheckUploadCallback;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.mointerface.MoRequestCallback;
import com.test.xcamera.mointerface.MoUploadDataCallback;
import com.test.xcamera.statistic.StatisticOneKeyMakeVideo;
import com.test.xcamera.utils.LoggerUtils;
import com.moxiang.common.logging.Logcat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Author: mz
 * Time:  2019/10/17
 * 上传固件版本
 */
public class UploadCameraFile {
    private final String TAG = "OTA_LOG";
    //    private Handler mhandler;
    private Context mcontext;
    private File file;
    private static int DATASIZE = 512 * 1024;
    private int offset = 0;
    private int START_UPLOAD_FLAG = 10001;
    private int UPLOAD_FILE_FLAG = 10002;
    private FileInputStream inputStream;
    private byte[] buffer;
    private int len;
    private HomeViewInterface homeViewInterface;
    private boolean uploadStart = false;
    private boolean isWriteFile = false;
    private int startUploadCount = 0;

    private String fileName;
    private Handler timerHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == START_UPLOAD_FLAG) {
                if (startUploadCount > 3) {
                    LoggerUtils.i(TAG, "start upload timerHandler  结束作用");
                    timerHandler.removeMessages(START_UPLOAD_FLAG);
                } else {
                    if (!uploadStart) {
                        LoggerUtils.i(TAG, "start upload timerHandler  起作用了");
                        startUpload(fileName);
                    }
                }
            } else if (what == UPLOAD_FILE_FLAG) {
                //TODO 暂时还没有逻辑
                if (!isWriteFile) {
                    uploadFile();
                }
            }
        }
    };


//    public UploadCameraFile(Handler mhandler, Context mcontext, File file) {
//        this.mhandler = mhandler;
//        this.mcontext = mcontext;
//        this.file = file;
//        initInputStream();
//    }

    public UploadCameraFile(Handler mhandler, HomeViewInterface homeViewInterface, File file) {
        // this.mhandler = mhandler;
        this.homeViewInterface = homeViewInterface;
        this.mcontext = (Context) homeViewInterface;
        this.file = file;
        fileName = getFileName(file.getAbsolutePath());
        initInputStream();
    }

    private void initInputStream() {
        try {
            inputStream = new FileInputStream(file);
            buffer = new byte[DATASIZE];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始上传
     */
    public void startUpload(String fileName) {
        String replace = fileName.replace("/", "");
        ConnectionManager.getInstance().uploadCheckcond(replace, new MoCheckUploadCallback() {
            @Override
            public void onSuccess(final int reason) {
                Log.i(TAG, "checkUploadRoomMemoryAndBattery: onSuccess " + reason + " fileName " + replace);

                show(reason);

            }

            @Override
            public void onFailed() {
                uploadFile(replace);
            }
        });
    }

    private void uploadFile(String fileName) {
        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("开始升级固件接口").out();
        timerHandler.sendEmptyMessageDelayed(START_UPLOAD_FLAG, 15 * 1000);
        startUploadCount += 1;
        ConnectionManager.getInstance().startUpload(fileName, new MoRequestCallback() {
            @Override
            public void onSuccess() {
                Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("开始升级固件接口,调用成功").out();
                startUploadCount = 0;
                uploadStart = true;
                timerHandler.removeMessages(START_UPLOAD_FLAG);
                LoggerUtils.i(TAG, "开始上传固件包");
                uploadFile();
            }

            @Override
            public void onFailed() {
                startUploadCount = 0;
                timerHandler.removeMessages(START_UPLOAD_FLAG);
                setPointData(fileName, "upload_start interface call failed");
            }
        });
    }


    /**
     * 上传文件
     */
    public void uploadFile() {

        if (!isWriteFile) {
            timerHandler.sendEmptyMessageDelayed(UPLOAD_FILE_FLAG, 5 * 1000);
        }
        try {
            if ((len = inputStream.read(buffer)) != -1) {
                byte[] bu = new byte[len];
                System.arraycopy(buffer, 0, bu, 0, len);

                LoggerUtils.i(TAG, "上传 file.length()  = " + file.length() + " buffer.length  =  " + buffer.length);
                LoggerUtils.i(TAG, "上传 bu.length  = " + bu.length + " offset = " + offset);

                ConnectionManager.getInstance().uploadData(file.getName(), (int) file.length(), offset, bu, new MoUploadDataCallback() {
                    @Override
                    public void onSuccess(final int progress) {

                        isWriteFile = true;
                        offset += len;
                        int prop = (50 + progress / 2);
                        LoggerUtils.i(TAG, "上传中 progress = " + (50 + progress / 2));
                        homeViewInterface.setProgresss(prop == 100 ? 98 : prop);
                        uploadFile();
                    }

                    @Override
                    public void onFailed() {
                        closeStream();
                        isWriteFile = true;
                        if (homeViewInterface instanceof UploadHardWareActivity) {
                            ((UploadHardWareActivity) homeViewInterface).upDataError();
                        }
                        setPointData(fileName, "uploadData interface call failed");
                        LoggerUtils.i(TAG, "上传失败!!!!!!!");
                    }
                });
            } else {
                closeStream();
                isWriteFile = true;
                LoggerUtils.i(TAG, "uploadFile 完成");
                uploadFinish();
            }
        } catch (Exception e) {
            isWriteFile = true;
        }
    }

    private void closeStream() {
        if (inputStream != null) {
            try {
                inputStream.close();
                inputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        buffer = null;
    }

    /**
     * 上传结束
     */
    public void uploadFinish() {
        ConnectionManager.getInstance().uploadFinish(new MoRequestCallback() {
            @Override
            public void onSuccess() {
                LoggerUtils.i(TAG, "upload_finish 接口调用成功 !!!");
                deleteFile();//固件校验包是正确的可以正常升级.这个时候把本地的固件文件就能删除了
                upgrade();
            }

            @Override
            public void onFailed() {
                deleteFile();//固件校验包失败,说明当前的包已经坏掉了不能正常升级,直接删除掉
                LoggerUtils.i(TAG, "upload_finish 接口调用失败 !!!");
                setPointData(fileName, "upload_finish interface call failed");
            }
        });
    }

    private void deleteFile() {
        if (file != null && file.exists()) {
            file.delete();
            file = null;
        }
        if (timerHandler != null) {
            timerHandler.removeCallbacksAndMessages(null);
            timerHandler = null;
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
        String msg = "";
        switch (reason) {
            case 1:
                msg = AiCameraApplication.getContext().getResources().getString(R.string.sdcard_status_full);
                break;
            case 2:
                msg = AiCameraApplication.getContext().getResources().getString(R.string.bat_low_30);
                break;
            case 3:
                msg = AiCameraApplication.getContext().getResources().getString(R.string.version_no);
                break;
            case 4:
                msg = AiCameraApplication.getContext().getResources().getString(R.string.device_s);
                break;
            case 5:
                msg = AiCameraApplication.getContext().getResources().getString(R.string.sdcard_out);
                break;
        }
        if (homeViewInterface instanceof UploadHardWareActivity) {
            ((UploadHardWareActivity) homeViewInterface).showUpLoadErrorDialog(msg,
                    AiCameraApplication.mApplication.getResources().getString(R.string.update_title));
        }
        setPointData(fileName, msg);
    }


    public void upgrade() {
        ConnectionManager.getInstance().upgrade(new MoRequestCallback() {
            @Override
            public void onSuccess() {
                LoggerUtils.i(TAG, "开始升级接口调用成功 !!!");

//                if (homeViewInterface instanceof UploadHardWareActivity) {
//                    ((UploadHardWareActivity) homeViewInterface).uploadSuccess();
//                }
            }

            @Override
            public void onFailed() {
                LoggerUtils.i(TAG, "开始升级接口调用失败 !!!");
                setPointData(fileName, "upgrade interface call failed");
                if (homeViewInterface instanceof UploadHardWareActivity) {
                    ((UploadHardWareActivity) homeViewInterface).uploadFailed();
                }
                homeViewInterface.setProgresss(98);
            }
        });
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