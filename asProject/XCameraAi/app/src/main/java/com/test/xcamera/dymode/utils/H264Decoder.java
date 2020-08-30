package com.test.xcamera.dymode.utils;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.view.Surface;

import com.test.xcamera.constants.LogcatConstants;
import com.test.xcamera.dymode.callbacks.H264DecodeListener;
import com.test.xcamera.player.AVFrame;
import com.moxiang.common.logging.Logcat;
import com.ss.android.vesdk.TERecorder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by zll on 2020/1/7.
 */

public class H264Decoder {
    private static final String TAG = "H264Decoder";

    //设置解码分辨率
    private final int VIDEO_WIDTH = 1920;//2592
    private final int VIDEO_HEIGHT = 1080;//1520

    //解码帧率 1s解码30帧
    private final int FRAME_RATE = 30;

    //支持格式
    private final String VIDEOFORMAT_H264 = "video/avc";

    //默认格式
    private String mMimeType = VIDEOFORMAT_H264;

    //接收的视频帧队列
    private LinkedBlockingQueue<AVFrame> mFrmList = new LinkedBlockingQueue<>();
    //解码结果监听
    private H264DecodeListener mOnDecodeListener;

    private MediaCodec mMediaCodec;
    private DecodeThread2 mDecodeThread;
    private Surface mSurface;

    private TERecorder mRecorder;

    private int mMediaFormatWidth = 0;
    private int mMediaFormatHeight = 0;

    public void init(TERecorder recorder) {
        Log.i(TAG, "init");
        this.mRecorder = recorder;
        if (mDecodeThread != null) {
            mDecodeThread.stopThread();
            mDecodeThread.interrupt();
            mDecodeThread = null;
        }

        if (mMediaCodec != null) {
            mMediaCodec.stop();
            mMediaCodec.release();
            mMediaCodec = null;
        }

        mDecodeThread = new DecodeThread2();
        mDecodeThread.start();
    }

    public void unInit() {
        clearVideoCache();
        if (mDecodeThread != null) {
            mDecodeThread.stopThread();
            mDecodeThread.interrupt();
            mDecodeThread = null;
        }
    }

    public void setSurface(Surface surface) {
        mSurface = surface;
    }

    public void onReceived(AVFrame avFrame) {
//        mFrmList.add(avFrame);
        if (mFrmList.size() > 100) {
            clearVideoCache();
        }
        mFrmList.offer(avFrame);
    }

    public void setOnDecodeListener(H264DecodeListener listener) {
        mOnDecodeListener = listener;
    }

    /**
     * 清除视频帧缓存
     */
    public void clearVideoCache() {
        if (mFrmList != null)
            mFrmList.clear();
    }

    private final int CODE_TIME_OUT = 90 * 1000;

    private class DecodeThread2 extends Thread {
        /**
         * 初始化解码器
         */
        public void initMediacodecDecode(int width, int height) {
            if (mMediaCodec == null) {
                try {
                    //通过多媒体格式名创建一个可用的解码器
                    mMediaCodec = MediaCodec.createDecoderByType(mMimeType);
                } catch (IOException e) {
                    Log.e("=====", "createDecoderByType==>" + e.toString());
                    e.printStackTrace();
                }

                MediaFormat mediaformat = MediaFormat.createVideoFormat(mMimeType, width, height);
                //设置帧率
                mediaformat.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
                //crypto:数据加密 flags:编码器/编码器
                try {
                    mMediaCodec.configure(mediaformat, mSurface, null, 0);
                    mMediaCodec.start();
                } catch (Exception e) {
                    Logcat.v().tag(LogcatConstants.FPV_PREVIEW).msg("initMediaCodec err:" + e.toString()).out();

                    Log.e("=====", "==>" + e.toString());
                }

                //初始化解码器格式
                mMediaCodec.setVideoScalingMode(MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            }
        }

        private boolean isRunning = true;

        public synchronized void stopThread() {
            isRunning = false;
        }

        public synchronized void startDecode() {
            isRunning = true;
        }

        public boolean isRunning() {
            return isRunning;
        }

        @Override
        public void run() {
            ByteBuffer byteBuffer = null;
            AVFrame avFrame = null;
            initMediacodecDecode(VIDEO_WIDTH, VIDEO_HEIGHT);
            while (isRunning) {
                //需要原子操作
                try {
                    avFrame = mFrmList.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (avFrame == null)
                    continue;

                //1 准备填充器
                int inIndex = -1;

                if (mMediaFormatWidth != avFrame.getVideoWidth() || mMediaFormatHeight != avFrame.getVideoHeight()) {

                    if (mOnDecodeListener != null) {
                        mMediaFormatWidth = avFrame.getVideoWidth();
                        mMediaFormatHeight = avFrame.getVideoHeight();

                        mOnDecodeListener.onSizeChanged(mMediaFormatWidth, mMediaFormatHeight);
                    }
                }

                try {
                    inIndex = mMediaCodec.dequeueInputBuffer(CODE_TIME_OUT * 2);
                } catch (Exception e) {
                    Log.e("=====", "dequeueInputBuffer==>" + e.toString());
                }
//                Log.e("=====", "inIndex==>" + inIndex);

                if (inIndex >= 0) {
                    //2 准备填充数据
                    try {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                            byteBuffer = mMediaCodec.getInputBuffers()[inIndex];
                            byteBuffer.clear();
                        } else {
                            byteBuffer = mMediaCodec.getInputBuffer(inIndex);
                        }
                    } catch (Exception e) {
                        Log.e("=====", "==>" + e.toString());
                    }

                    if (byteBuffer == null) {
                        continue;
                    }

                    //                    Log.e("=====", "byteBuffer.put  ==>" + toHexString(avFrame.frmData, 10));

                    //3 把数据传给解码器
                    try {
                        byteBuffer.put(avFrame.frmData, 0, avFrame.frmData.length);
                        mMediaCodec.queueInputBuffer(inIndex, 0, avFrame.frmData.length, 0, 0);
                    } catch (Exception e) {
//                        ex.printStackTrace();
                        Log.e("=====", "queueInputBuffer err==>" + e.toString());
                        continue;
                    }
                } else {
                    SystemClock.sleep(30);
                    continue;
                }

                MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
                int outIndex = MediaCodec.INFO_TRY_AGAIN_LATER;

                //4 开始解码
                try {
                    outIndex = mMediaCodec.dequeueOutputBuffer(info, CODE_TIME_OUT);
                } catch (Exception e) {
                    Log.e("=====", "dequeueOutputBuffer err==>" + e.toString());
                }
//                Log.e("=====", "outIndex==>" + outIndex);
                if (outIndex >= 0) {
                    boolean doRender = (info.size != 0);
                    try {
                        mMediaCodec.releaseOutputBuffer(outIndex, doRender);
                    } catch (Exception e) {
                        Log.e("=====", "releaseOutputBuffer err==>" + e.toString());
                    }
                    if (mRecorder != null) {
                        long decode = System.currentTimeMillis();
                        long camera = avFrame.getmFrameTimeStamp();
//                        long parse = avFrame.getmParseTimeStamp();
                        long latency = decode - camera;
//                        long latency2 = decode - parse;
                        if (latency > 0)
                            mRecorder.setVideoFrameLatency(latency);
//                        String content = "decode: " + String.valueOf(decode) + ", camera: " + String.valueOf(camera)
//                                + ", latency: " + String.valueOf(latency);
//                        FileUtil.writeFileToSDCard(FileUtil.path, content.getBytes(), "framestamp.txt", true, true, false);
                    }
                    if (mOnDecodeListener != null) {
                        mOnDecodeListener.decodeResult(avFrame);
                    }
                }
            }

            try {
//                Log.e("=====", "==>mMediaCodec.release");
                if (mMediaCodec != null) {
                    mMediaCodec.stop();
                    mMediaCodec.release();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mMediaCodec = null;
            }
        }
    }
}
