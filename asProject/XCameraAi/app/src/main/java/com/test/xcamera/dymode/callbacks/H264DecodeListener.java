package com.test.xcamera.dymode.callbacks;

import com.test.xcamera.player.AVFrame;

/**
 * Created by zll on 2020/7/20.
 */

public interface H264DecodeListener {
    void decodeResult(AVFrame avFrame);

    void onSizeChanged(int width, int height);
}
