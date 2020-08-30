package com.test.xcamera.download;

import java.io.File;

/**
 * Author: mz
 * Time:  2019/9/27
 */
public interface DownloadListener {
    void onStart();//下载开始

    void onProgress(int progress);//下载进度

    void onFinish(File path);//下载完成

    void onFail(String errorInfo);//下载失败
}
