package com.test.xcamera.utils;

/**
 * Created by zhouxuecheng on
 * Create Time 2020/3/31
 * e-mail zhouxuecheng1991@163.com
 * 这个类就放一个下载状态,用来取消下载操作的
 */
public class DownloadConfig {
    private boolean isCancelDown;

    public boolean getIsCancelDown() {
        return isCancelDown;
    }

    public void setCancelDown(boolean isCancelDown) {
        this.isCancelDown = isCancelDown;
    }

    private static DownloadConfig instance = new DownloadConfig();

    private DownloadConfig() {
    }

    public static DownloadConfig getInstance() {
        return instance;
    }
}
