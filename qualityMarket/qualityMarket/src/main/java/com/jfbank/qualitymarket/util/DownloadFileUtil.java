package com.jfbank.qualitymarket.util;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 下载文件的工具类
 *
 * @author 彭爱军
 * @date 2016年9月22日
 */
public class DownloadFileUtil {
    private final static String TAG = "DownloadFileUtil";

    /**
     * 使用下载功能
     *
     * @param context  启动下载Activity
     * @param url      下载地址
     * @param title    下载标题
     * @param des      下载文件描述
     * @param mimeType 下载类型
     * @param fileName 下载文件名称
     */
    public static void downloadApk(final Context context, String url, String title, String des, String mimeType, String fileName) {
        try {
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDestinationInExternalPublicDir("download", fileName);
            //设置文件存放目录
//		request.setDestinationInExternalFilesDir(activiy, Environment.DIRECTORY_DOWNLOADS, mydown);
            request.setTitle(title);
            request.setDescription(des);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
//		request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
            if (!TextUtils.isEmpty(mimeType))
                request.setMimeType(mimeType);
            downloadManager.enqueue(request);
        } catch (Exception e) {//系統下載異常，則采用okhttp下載
            ToastUtil.show("请检测下载安装器是否开启，或到应用市场进行更新安装");
        }

    }



    public interface IDownLoaderListener {
        int DOWNLOAD_SUCCESS = 0X01;
        int DOWNLOAD_FAILD = 0X02;

        void onDownReult(int state, String httpUrl, final String fileName);
    }

    /**
     * 下载apk文件。
     *
     * @param httpUrl
     */
    public static void downFile(final String httpUrl, final String fileName, final IDownLoaderListener iDownLoaderListener) {

        new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    URL url = new URL(httpUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    FileOutputStream fileOutputStream = null;
                    InputStream inputStream;
                    if (connection.getResponseCode() == 200) {
                        FileImagePath fileutils = new FileImagePath();
                        inputStream = connection.getInputStream();
                        String tempPath = "download";
                        // 文件地址
                        String filePath = tempPath + "/" + fileName;
                        if (inputStream != null) {

                            // 判断sd卡上的文件夹是否存在
                            if (!fileutils.isFileExist(tempPath)) {
                                fileutils.createSDDir(tempPath);
                            }
                            // 删除已下载的apk
                            if (fileutils.isFileExist(filePath)) {
                                fileutils.deleteFile(filePath);
                            }
                            fileOutputStream = new FileOutputStream(fileutils.getFilePath(filePath));
                            byte[] buffer = new byte[1024];
                            int length = 0;

                            while ((length = inputStream.read(buffer)) != -1) {
                                fileOutputStream.write(buffer, 0, length);
                            }
                            fileOutputStream.close();
                            fileOutputStream.flush();
                        }
                        inputStream.close();
                        if (iDownLoaderListener != null)
                            iDownLoaderListener.onDownReult(IDownLoaderListener.DOWNLOAD_SUCCESS, httpUrl, fileutils.getFilePath(filePath));
                    }
                } catch (IOException e) {
                    if (iDownLoaderListener != null)
                        iDownLoaderListener.onDownReult(IDownLoaderListener.DOWNLOAD_FAILD, httpUrl, null);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 下载apk文件。
     *
     * @param handler
     * @param httpUrl
     */
    public static void downFile(final Handler handler, final String httpUrl, final String fileName) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                Message message = handler.obtainMessage();
                try {
                    URL url = new URL(httpUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    FileOutputStream fileOutputStream = null;
                    InputStream inputStream;
                    if (connection.getResponseCode() == 200) {
                        inputStream = connection.getInputStream();

                        if (inputStream != null) {

                            File file = getFile(httpUrl, fileName);


                            fileOutputStream = new FileOutputStream(file);
                            byte[] buffer = new byte[1024];
                            int length = 0;

                            while ((length = inputStream.read(buffer)) != -1) {
                                fileOutputStream.write(buffer, 0, length);
                            }
                            fileOutputStream.close();
                            fileOutputStream.flush();
                        }
                        inputStream.close();
                    }

                    System.out.println("已经下载完成");

                    message.what = 1;                //1代码成功，2代表失败
                    handler.sendMessage(message);
                } catch (IOException e) {
                    message.what = 2;
                    handler.sendMessage(message);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 根据传过来url创建文件
     */
    public static File getFile(String url, final String fileName) {
        File files = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), getFilePath(url));
        if (StringUtil.notEmpty(fileName)) {
            files = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), fileName);
        }

        return files;
    }

    /**
     * 截取出url后面的apk的文件名
     *
     * @param url
     * @return
     */
    private static String getFilePath(String url) {
        return url.substring(url.lastIndexOf("?"), url.length()) + ".pdf";
    }
}
