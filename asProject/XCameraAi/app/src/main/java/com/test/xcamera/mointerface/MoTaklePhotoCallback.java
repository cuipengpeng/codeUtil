package com.test.xcamera.mointerface;

import com.test.xcamera.bean.MoImage;

/**
 * Created by zll on 2019/7/17.
 */

public interface MoTaklePhotoCallback{
    void onSuccess(MoImage image);
    default void onFailed(int reason){}
}
