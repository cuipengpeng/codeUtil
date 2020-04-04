package com.caishi.chaoge.listener;

public interface OnUploadFileListener {
    void success();

    void error(String msg);

    void progress(int progress, long currentSize, long totalSize);

}
