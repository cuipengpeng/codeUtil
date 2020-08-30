package com.test.xcamera.mointerface;

import com.test.xcamera.bean.MoImage;

/**
 * Created by zll on 2019/11/28.
 */

public interface MoAlbumCountCallback {
    void onSuccess(int count, MoImage thumbnail);
    void onFailed();
}
