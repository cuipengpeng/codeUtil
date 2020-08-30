package com.test.xcamera.utils;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.test.xcamera.application.AppContext;
import com.test.xcamera.bean.MoRange;
import com.test.xcamera.managers.CommandIDManager;
import com.test.xcamera.managers.ConnectionManager;
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

public class DownloadCameraVideo {
    private static final String TAG = "DownloadCameraVideo";
    private static final long PACKAGE_SIZE = 1024 * 1024;
    private Handler handler;
    private String filePath = "";
    private String fileName = "";
    private long total_size = 0;
    private Download download;
    private File file;
    private FileOutputStream out = null;
    private BufferedOutputStream outputStream = null;

    public DownloadCameraVideo(Handler handler, String filePath, String fileName) {
        this.handler = handler;
        this.filePath = filePath;
        this.fileName = fileName;
        getOutputStream(filePath, fileName);
    }

    public void request(Uri uri, long size, Download download) {
        this.total_size = size;
        this.download = download;
        dividePackages(size, uri);
        LoggerUtils.d(TAG, "request: size = " + size + ", uri = " + uri.toString());
//        String s = "request: size = " + size + ", uri = " + uri.toString();
//        FileUtil.writeFileToSDCard(testPath, s.getBytes(), testName, true, true, false);
    }


    private long offset = 0;
    private long size;

    /**
     * 对数据进行分包处理
     * 之前是一下发了好多个包过去,硬件处理不了,现在改成一个包回完在发下一个包
     *
     * @param size
     * @param uri
     */
    private void dividePackages(long size, Uri uri) {
        LoggerUtils.d(TAG, "dividePackages: size = " + size + ", count = " + count + ", uri = " + uri.toString());
        this.size = size;
        long len = Math.min(size, PACKAGE_SIZE);
        sendRequest(uri, offset, len);
//        String s = "dividePackages: size = " + size + ", count = " + count + ", uri = " + uri.toString();
//        FileUtil.writeFileToSDCard(testPath, s.getBytes(), testName, true, true, false);
    }

//    static String testPath = Environment.getExternalStorageDirectory().getPath() + "/MoTest";
//    static String testName = "download.txt";
//    static String testName1 = "exc2.txt";

    private int count = 0;

    /**
     * 发送下载指令
     *
     * @param uri
     * @param offset
     * @param len
     */
    private void sendRequest(final Uri uri, final long offset, final long len) {
        if (DownloadConfig.getInstance().getIsCancelDown()) {
            LoggerUtils.d(TAG, "cancel download file video");
            return;
        }
        MoRange range = new MoRange();
        range.setmOffset(offset);
        range.setmLength((int) len);
        LoggerUtils.d(TAG, "sendRequest: " + count + ", offset = " + offset + ", length = " + len);
//        String s = "sendRequest: " + count + ", offset = " + offset + ", length = " + len;
//        FileUtil.writeFileToSDCard(testPath, s.getBytes(), testName, true, true, false);
        int msgID = CommandIDManager.getInstance().getMsgID();
        ConnectionManager.getInstance().downloadFile(msgID, uri.toString(), range, new MoDownloadCallback() {

            @Override
            public void callback(byte[] data) {
//                LoggerUtils.d(TAG,  "download video callback: data length：" + data.length + ", count = " + count);
//                String s = "download video callback: data length：" + data.length + ", count = " + count;
//                FileUtil.writeFileToSDCard(testPath, s.getBytes(), testName, true, true, false);

                if (handler != null) {
                    Message msg = Message.obtain();
                    msg.what = DownLoadRequest.DOWN_LOADFILE;
                    msg.obj = data.length;
                    handler.sendMessage(msg);
                    LoggerUtils.d(TAG, "download video callback: handler send data length：" + data.length + ", count = " + count + ", msg id：" + msgID);
//                    String s1 = "download video callback: handler send data length：" + data.length + ", count = " + count + ", msg id：" + msgID;
//                    FileUtil.writeFileToSDCard(testPath, s1.getBytes(), testName, true, true, false);
                }

                if (data != null) {
                    LoggerUtils.d(TAG, "download video callback: " + data.length + ", count = " + count + ", msg id：" + msgID);
                    count = count + 1;
//                    datasize += data.length;
                    try {
                        outputStream.write(data);
                        outputStream.flush();
                        if (file.length() == total_size) {
//                        if (datasize == total_size) {
                            outputStream.close();
                            out.close();
                            if (download != null) {
//                                String s1 = "download success";
//                                FileUtil.writeFileToSDCard(testPath, s1.getBytes(), testName, true, true, false);
                                LoggerUtils.d(TAG, "callback: download success");
                                download.downLoadOk();
                                if (handler != null) {
                                    Message msg = Message.obtain();
                                    msg.what = DownLoadRequest.DOWN_LOADFILE_OK;
                                    Bundle bundle = new Bundle();
                                    bundle.putString("path", file.getAbsolutePath());
                                    msg.setData(bundle);
                                    handler.sendMessage(msg);
                                }
                                count = 0;
                            }
                            FileUtil.sendScanBroadcast(AppContext.getInstance(), file.getAbsolutePath());
                            return;
                        }

                        if (DownloadCameraVideo.this.size > 0) {
                            DownloadCameraVideo.this.offset += len;
                            DownloadCameraVideo.this.size -= len;
                            dividePackages(DownloadCameraVideo.this.size, uri);
                        }
                    } catch (IOException e) {
                        LoggerUtils.d(TAG, "IOException: " + e.toString());
//                        FileUtil.writeFileToSDCard(testPath, e.getMessage().getBytes(), testName1, true, true, false);
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private long datasize = 0;

    public void getOutputStream(String folderPath, String fileName) {
        this.filePath = folderPath;
        this.fileName = fileName;
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
        void downLoadOk();
    }
}
