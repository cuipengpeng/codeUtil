package com.test.xcamera.upload;

/**
 * Created by smz on 2019/11/6.
 */

public interface UploadCallbacks {

        void onProgressUpdate(int percentage);
        void onError(Throwable e);
        void onFinish();
}
