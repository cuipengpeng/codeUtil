package com.test.xcamera.download;

public interface OnDownloadCallback {
    void onDownLoadLength(long length);
    void onFail(String msg);
}
