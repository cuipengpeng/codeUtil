package com.test.xcamera.utils;

import android.net.Uri;
import android.os.Looper;
import android.util.Log;

import com.test.xcamera.application.AppContext;
import com.test.xcamera.bean.MoRange;
import com.test.xcamera.managers.ConnectionManager;
import com.test.xcamera.mointerface.MoDownloadCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DownloadSingleCameraFile {
    private static final long PACKAGE_SIZE = 256 * 1024;

    public String type;
    private String fileName;
    private String filePath;
//    private byte[] mData;
    private DownloadCallback download;
    private long offset = 0;
    private long size;
    private long totalSize;
    private FileType mFileType;

    public DownloadSingleCameraFile(FileType fileType) {
        mFileType = fileType;
    }

    public DownloadSingleCameraFile(String filePath, String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
    }

    /**
     * 执行下载
     *
     * @param uri
     * @param size
     * @return
     */
    public void startDownload(Uri uri, long size, DownloadCallback download) {
        getOutputStream(filePath, fileName);
        this.totalSize = size;
        this.download = download;
//        mData = new byte[size];
        new Thread(new Runnable() {
            @Override
            public void run() {
                dividePackages(size, uri);
            }
        }).start();
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
        sendRequest(uri, offset, len);
    }

    /**
     * 发送下载的包
     *
     * @param uri
     * @param offset
     * @param len
     */
    private void sendRequest(final Uri uri, final long offset, final long len) {
        MoRange range = new MoRange();
        range.setmOffset(offset);
        range.setmLength((int)len);
        ConnectionManager.getInstance().downloadFile(uri.toString(), range, new MoDownloadCallback() {
            @Override
            public void callback(byte[] data) {
                try {
//                    System.arraycopy(data, 0, mData, offset, data.length);
                    if (data != null && data.length > 0) {
                        out.write(data);
                        Log.i("DOWN_FILE_TEST", " = totalSize  : sumDataLength = " + data.length);
                        out.flush();
                        if (file.length() == totalSize) {
                            out.close();
                            if (download != null) {
                               new android.os.Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        download.onFinishDownLoad(file.getAbsolutePath());
                                    }
                                });
                            }
                            FileUtil.sendScanBroadcast(AppContext.getInstance(), file.getAbsolutePath());
                            return;
                        }
                        if (DownloadSingleCameraFile.this.size > 0) {
                            DownloadSingleCameraFile.this.offset += len;
                            DownloadSingleCameraFile.this.size -= len;
                            dividePackages(DownloadSingleCameraFile.this.size, uri);
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    private File file;
    private FileOutputStream out = null;

    private void getOutputStream(String folderPath, String fileName) {
        File cacheDir = new File(Constants.cacheLocalPath);
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        if(StringUtil.notEmpty(folderPath) && StringUtil.notEmpty(fileName)){
            File fileDir = new File(folderPath);
            if (!fileDir.exists()) {
                if (!fileDir.mkdirs()) {
                    return;
                }
            }
            file = new File(folderPath + "/" + fileName);
        }else {
            if(mFileType == FileType.VIDEO){
                file = new File(Constants.cacheLocalPath + "/" + System.currentTimeMillis() + ".mp4");
            }else if(mFileType == FileType.PHOTO){
                file = new File(Constants.cacheLocalPath + "/" + System.currentTimeMillis() + ".jpg");
            }else if(mFileType == FileType.XML){
                file = new File(Constants.cacheLocalPath + "/" + System.currentTimeMillis() + ".xml");
            }
        }

        try {
            out = new FileOutputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface DownloadCallback {
         void onFinishDownLoad(String path);
    }

    public enum FileType{
        VIDEO, PHOTO, XML
    }
}
