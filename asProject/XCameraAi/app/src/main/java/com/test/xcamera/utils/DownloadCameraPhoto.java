package com.test.xcamera.utils;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.test.xcamera.application.AppContext;
import com.test.xcamera.bean.MoRange;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.mointerface.MoDownloadCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 下载相机图片
 */
public class DownloadCameraPhoto {
    private final String TAG = "DownloadCameraPhoto";
    private static final long PACKAGE_SIZE = 83 * 1024;

    private Handler handler;
    public String type;
    private String fileName;
    private String filePath;
    //    private byte[] mData;
    private Download download;
    private long offset = 0;
    private long size;
    private long totalSize;

    public DownloadCameraPhoto() {

    }

    public DownloadCameraPhoto(Handler handler, String filePath, String fileName) {
        this.handler = handler;
        this.filePath = filePath;
        this.fileName = fileName;
        getOutputStream(filePath, fileName);
    }

    /**
     * 执行下载
     *
     * @param uri
     * @param size
     * @return
     */
    public void request(Uri uri, long size, Download download) {
        totalSize = size;
        this.download = download;
//        mData = new byte[size];
        dividePackages(size, uri);
    }


    /**
     * 开始分包
     * 之前是一下发了好多个包过去,硬件处理不了,现在改成一个包回完在发下一个包
     *
     * @param size
     * @param uri
     */
    private void dividePackages(long size, Uri uri) {
        this.size = size;
        long len = Math.min(size, PACKAGE_SIZE);
        sendRequest(uri, offset, (int) len);
    }

    private int downCount = 0;

    /**
     * 发送下载的包
     *
     * @param uri
     * @param offset
     * @param len
     */
    private void sendRequest(final Uri uri, final long offset, final int len) {
        if (DownloadConfig.getInstance().getIsCancelDown()) {
            Log.i(TAG, " cancel download file photo");
            return;
        }
        MoRange range = new MoRange();
        range.setmOffset(offset);
        range.setmLength(len);
        ConnectionManager.getInstance().downloadFile(uri.toString(), range, new MoDownloadCallback() {
            @Override
            public void callback(byte[] data) {

                if (handler != null) {
                    Message msg = Message.obtain();
                    msg.what = DownLoadRequest.DOWN_LOADFILE;
                    msg.obj = data.length;
                    handler.sendMessage(msg);
                }
//                Log.i("DOWN_CAMERS_FILE_LOG", "file total size is (" + mData.length + ") down load camera photo size is ( " + data.length + " )  downCount " + downCount);
                try {
//                    System.arraycopy(data, 0, mData, offset, data.length);
                    if (data != null && data.length > 0) {
                        out.write(data);
                        out.flush();
                        if (file.length() == totalSize) {
                            LoggerUtils.i(TAG, " down load camera photo is ok ！！！！！！！！");
                            out.close();
                            if (download != null) {
                                download.downLoadOk(file.getAbsolutePath());
                            }

                            if (handler != null) {
                                Message msg = Message.obtain();
                                msg.what = DownLoadRequest.DOWN_LOADFILE_OK;
                                Bundle bundle = new Bundle();
                                bundle.putString("path", file.getAbsolutePath());
                                msg.setData(bundle);
                                handler.sendMessage(msg);
                            }
                            FileUtil.sendScanBroadcast(AppContext.getInstance(), file.getAbsolutePath());
                            downCount = 0;
                            return;
                        }
                        if (DownloadCameraPhoto.this.size > 0) {
                            DownloadCameraPhoto.this.offset += len;
                            DownloadCameraPhoto.this.size -= len;
                            dividePackages(DownloadCameraPhoto.this.size, uri);
                        }
//                        sumDataLength = sumDataLength + data.length;
//                        Log.i("DOWN_FILE_TEST", totalSize + " = totalSize  : sumDataLength = "
//                                + sumDataLength + " mData length = " + data.length);
                        downCount += 1;
                    }
                } catch (Exception e) {
                    Log.i(TAG, " down load file fail " + e.toString());
                }
            }
        });
    }

    private File file = null;
    private FileOutputStream out = null;

    public void getOutputStream(String folderPath, String fileName) {
        File fileDir = new File(folderPath);
        if (!fileDir.exists()) {
            if (!fileDir.mkdirs()) {
                return;
            }
        }
        file = new File(folderPath + "/" + fileName);


        try {
            out = new FileOutputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface Download {
        void downLoadOk(String path);
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
