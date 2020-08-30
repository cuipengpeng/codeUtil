package com.test.xcamera.mointerface;

import com.test.xcamera.bean.MoVideoByDate;

import java.util.ArrayList;

/**
 * Created by zll on 2019/10/25.
 */

public interface MoGetVideoByDateCallback {
    void onSuccess(ArrayList<MoVideoByDate> videoByDates);
    void onFailed();
}
