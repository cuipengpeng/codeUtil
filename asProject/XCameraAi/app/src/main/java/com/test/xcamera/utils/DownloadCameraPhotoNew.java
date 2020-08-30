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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 下载相机图片
 */
public class DownloadCameraPhotoNew {
    private static final String TAG = "DownloadCameraCamera";
    private static final long PACKAGE_SIZE = 83 * 1024;

    private Handler handler;
    public String type;
    private File file = null;
    private FileOutputStream outputStream = null;

    public DownloadCameraPhotoNew(Handler handler, String filePath, String fileName) {
        this.handler = handler;
        getOutputStream(filePath, fileName);
    }

    /**
     * 执行下载
     *
     * @param uri
     * @return
     */
    public void request(Uri uri, long total_size, DownloadCameraVideoNew.Download download) {
        sendTimeOutMassage();
        CommandManager.getInstance().downloadSingleFile(uri.toString(), total_size, new MoDownloadCallback() {

            @Override
            public void callback(byte[] data) {
                if (handler != null) {
                    handler.removeMessages(DownLoadRequest.DOWN_LOAD_TIME_OUT);
                    handler.removeMessages(DownLoadRequest.DOWN_LOAD_ERROR);
                    Message msg = Message.obtain();
                    msg.what = DownLoadRequest.DOWN_LOADFILE;
                    msg.obj = data.length;
                    handler.sendMessage(msg);
                    sendTimeOutMassage();
                }
                if (data != null) {
//                    datasize += data.length;
                    try {
                        outputStream.write(data);
                        outputStream.flush();
                        if (file.length() == total_size) {
//                        if (datasize == total_size) {
                            outputStream.close();
                            outputStream.close();
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
                                    bundle.putString("path",file.getAbsolutePath());
                                    msg.setData(bundle);
                                    handler.sendMessage(msg);
                                }
                            }
                            FileUtil.sendScanBroadcast(AppContext.getInstance(), file.getAbsolutePath());
                            return;
                        }
                    } catch (IOException e) {
                        Log.d(TAG, "IOException: " + e.toString());
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    public void sendTimeOutMassage(){
        if (handler != null) {
            handler.sendEmptyMessageDelayed(DownLoadRequest.DOWN_LOAD_TIME_OUT, 1000);
            handler.sendEmptyMessageDelayed(DownLoadRequest.DOWN_LOAD_ERROR,30*1000);
        }
    }
    public void getOutputStream(String folderPath, String fileName) {
        File fileDir = new File(folderPath);
        if (!fileDir.exists()) {
            if (!fileDir.mkdirs()) {
                return;
            }
        }
        file = new File(folderPath + "/" + fileName);


        try {
            outputStream = new FileOutputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 在断开连接,或者是拔出sdcrad的时候判断一下,当前的文件是否已经下载好
     * 如果没有下载好,删除当前文件
     */
    public void deleteFile() {
        if (file != null) {
            if (file.exists()) {
                file.delete();
            }
        }
    }

}
