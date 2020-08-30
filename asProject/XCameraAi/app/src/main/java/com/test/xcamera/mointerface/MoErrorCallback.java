package com.test.xcamera.mointerface;

import com.test.xcamera.bean.MoErrorData;

/**
 * Created by ms on 2020/2/10.
 *
 * 上报回调
 */

public interface MoErrorCallback {
    //sd卡
    int SD_EVENT = 1;
    //电池
    int BAT_EVENT = 2;
    //拍摄异常结束
    int PHOTO_ERR_EVENT = 3;
    //云台
    int PTZ_EVENT = 4;
    //跟踪
    int TRACK_EVENT = 5;
    //温度
    int TEMPER_EVENT = 6;
    //视频预结束
    int VIDEO_PRE_FINISH = 9;
    //拍照结束
    int PHOTO_FINISH = 10;

    //sd卡状态
    int SD_OUT = 0;
    int SD_IN = 1;
    int SD_FULL = 2;
    int SD_IN_FAIL = 3;
    int SD_LOW = 10;

    void onError(MoErrorData data);
}
