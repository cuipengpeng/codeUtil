package com.test.xcamera.utils;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.test.xcamera.application.AppContext;
import com.test.xcamera.managers.CommandManager;
import com.test.xcamera.managers.MoDownloadHandler;
import com.test.xcamera.mointerface.MoDownloadCallback;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by wangchunhui on 2019/7/31.
 * <p>
 * 下载相机视频
 */

public class DownloadCameraVideoNew {
    private static final String TAG = "DownloadCameraVideo";
    private Handler handler;
    private String filePath = "";
    private File file;
    private FileOutputStream out = null;
    private BufferedOutputStream outputStream = null;

    public DownloadCameraVideoNew(Handler handler, String filePath, String fileName) {
        this.handler = handler;
        this.filePath = filePath;
        getOutputStream(filePath, fileName);
    }

    public void request(Uri uri, long total_size, Download download) {
        Log.i(TAG, "request: 11111111111111111111111111");
        sendTimeOutMassage();
        CommandManager.getInstance().downloadSingleFile(uri.toString(), total_size, new MoDownloadCallback() {
            @Override
            public void callback(byte[] data) {
                Log.i(TAG, "request: 22222222222222222222222");
                if (handler != null) {
                    handler.removeMessages(DownLoadRequest.DOWN_LOAD_TIME_OUT);
                    handler.removeMessages(DownLoadRequest.DOWN_LOAD_ERROR);
                    Message msg = Message.obtain();
                    msg.what = DownLoadRequest.DOWN_LOADFILE;
                    msg.obj = data.length;
                    handler.sendMessage(msg);
                    sendTimeOutMassage();
                }
                Log.i(TAG, "request: 33333333333333333333333");
                if (data != null) {
//                    datasize += data.length;
                    try {
                        outputStream.write(data);
                        outputStream.flush();
                        Log.i(TAG, "request: 444444444444444444444");
                        if (file.length() == total_size) {
//                        if (datasize == total_size) {
                            outputStream.close();
                            out.close();
                            MoDownloadHandler.getInstance().removeCallback(uri.toString());
                            if (download != null) {
                                Log.d(TAG, "callback: download success");
                                download.downLoadOk(file.getAbsolutePath());
                                if (handler != null) {
                                    handler.removeMessages(DownLoadRequest.DOWN_LOAD_TIME_OUT);
                                    handler.removeMessages(DownLoadRequest.DOWN_LOAD_ERROR);
                                    Message msg = Message.obtain();
                                    msg.what = DownLoadRequest.DOWN_LOADFILE_OK;
                                    Bundle bundle = new Bundle();
                                    bundle.putString("path", file.getAbsolutePath());
                                    msg.setData(bundle);
                                    handler.sendMessage(msg);
                                }
                            }
                            FileUtil.sendScanBroadcast(AppContext.getInstance(), file.getAbsolutePath());
                            Log.i(TAG, "request: 555555555555555555");
                            return;
                        }
                    } catch (IOException e) {
                        Log.i(TAG, "request: 66666666666666666666");
                        Log.d(TAG, "IOException: " + e.toString());
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void sendTimeOutMassage() {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(DownLoadRequest.DOWN_LOAD_TIME_OUT, 1000);
            handler.sendEmptyMessageDelayed(DownLoadRequest.DOWN_LOAD_ERROR, 30*1000);
        }
    }

    public void getOutputStream(String folderPath, String fileName) {
        this.filePath = folderPath;
        File fileDir = new File(folderPath);
        if (!fileDir.exists()) {
            if (!fileDir.mkdirs()) {
                return;
            }
        }
        file = new File(filePath + "/" + fileName);

        try {
            out = new FileOutputStream(file);
            outputStream = new BufferedOutputStream(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface Download {
        void downLoadOk(String path);
    }
}
