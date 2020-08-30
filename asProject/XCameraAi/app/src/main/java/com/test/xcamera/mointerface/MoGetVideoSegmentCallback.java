package com.test.xcamera.mointerface;

import com.test.xcamera.bean.MoVideoSegment;

import java.util.ArrayList;

/**
 * Created by zll on 2019/10/25.
 */

public interface MoGetVideoSegmentCallback {
    void onSuccess(ArrayList<MoVideoSegment> videoSegments);
    void onFailed();
}
