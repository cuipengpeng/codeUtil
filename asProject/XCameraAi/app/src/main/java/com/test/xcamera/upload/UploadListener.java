package com.test.xcamera.upload;

/**
 * Created by smz on 2019/11/7.
 */

public interface UploadListener<T> {
    void onProgress(int progress);
    void onFail(Throwable errorInfo);
    void onSucess(T t);
}
