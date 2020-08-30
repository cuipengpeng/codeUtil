package com.test.xcamera.utils;

import android.net.Uri;
import android.os.Looper;
import android.util.Log;

import com.test.xcamera.application.AppContext;
import com.test.xcamera.managers.CommandManager;
import com.test.xcamera.managers.MoDownloadHandler;
import com.test.xcamera.mointerface.MoDownloadCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DownloadSingleCameraFileNew {
    public String type;
    private String fileName;
    private String filePath;
    private FileType mFileType;
    private File file;
    private FileOutputStream out = null;

    public DownloadSingleCameraFileNew(FileType fileType) {
        mFileType = fileType;
    }

    public DownloadSingleCameraFileNew(String filePath, String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
    }

    /**
     * 执行下载
     *
     * @param uri
     * @return
     */
    public void startDownload(Uri uri, long totalSize, DownloadCallback download) {
        getOutputStream(filePath, fileName);
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendRequest(uri, totalSize, download);
            }
        }).start();
    }

    private void sendRequest(final Uri uri, final long totalSize, final  DownloadCallback download) {
        CommandManager.getInstance().downloadSingleFile(uri.toString(), totalSize, new MoDownloadCallback() {
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
                                        MoDownloadHandler.getInstance().removeCallback(uri.toString());
                                    }
                                });
                            }
                            FileUtil.sendScanBroadcast(AppContext.getInstance(), file.getAbsolutePath());
                            return;
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
    }

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
