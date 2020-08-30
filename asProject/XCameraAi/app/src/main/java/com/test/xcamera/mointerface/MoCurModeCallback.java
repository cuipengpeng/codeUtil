package com.test.xcamera.mointerface;

/**
 * Created by zll on 2019/7/4.
 */

public interface MoCurModeCallback {
    void success(int mode);

    default void onFailed() {
    }
}

