package com.test.xcamera.glview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import com.test.xcamera.R;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.AudioFrameInfo;
import com.test.xcamera.constants.LogcatConstants;
import com.test.xcamera.player.AVFrame;
import com.test.xcamera.test.video.CameraTest;
import com.test.xcamera.utils.DlgUtils;
import com.test.xcamera.view.AutoTrackingRectViewFix;
import com.test.xcamera.view.OnDecodeListener;
import com.moxiang.common.logging.Logcat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by smz on 2020/5/27.
 * <p>
 * <p>
 * 手机相机编码  PreviewGLSurfaceView渲染   250ms
 * 外设相机编码  FPVTextureView渲染         300ms
 * 手机相机编码  FPVTextureView渲染         300ms
 * mediaencodeanddecodedemo  编码、解码     150ms
 * <p>
 * 解码流程可以优化
 */

public class FPVTextureView extends TextureView implements TextureView.SurfaceTextureListener {
    private DecodeThread mDecodeThread = new DecodeThread();
    private PlayerThread mPlayerThread = new PlayerThread();
    private LinkedBlockingQueue<AVFrame> mFrmList = new LinkedBlockingQueue<>();
    private final static int FRAME_RATE = 30;
    private int VIDEO_WIDTH = 1920;//2592
    private int VIDEO_HEIGHT = 1080;//1520

    public CameraTest cameraTest = new CameraTest();

    public void onReceived(AVFrame avFrame) {
        this.mFrmList.add(avFrame);
    }

    public FPVTextureView(Context context) {
        super(context);
        init();
    }

    public FPVTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FPVTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.e("=====", "==>onSurfaceTextureAvailable");
        if (PreviewGLSurfaceView.CODEC_DEBUG) {
            mPlayerThread.init(new Surface(surface));
            mPlayerThread.startThread();
        } else {
            mDecodeThread.init(new Surface(surface));
            mDecodeThread.startThread();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (PreviewGLSurfaceView.CODEC_DEBUG) {
            mPlayerThread.stopThread();
        } else {
            mDecodeThread.stopThread();
        }
        Log.e("=====", "==>onSurfaceTextureDestroyed");
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public void setTrackingRectView(AutoTrackingRectViewFix trackingRectView) {
    }

    public void setDecodeListener(OnDecodeListener decodeListener) {
    }

    public void onReceivedAudio(AudioFrameInfo audioFrameInfo) {
    }

    public void destoryOpengl() {
    }

    public void release() {
    }

    public void reset() {
    }

    class DecodeThread extends Thread {
        private final static int CODE_TIME_OUT = 100 * 1000;
        private final static String VIDEOFORMAT_H264 = "video/avc";

        private MediaCodec mMediaCodec;
        private Surface surface;
        private boolean isRunning = true;

        public void startThread() {
            this.start();
        }

        public void stopThread() {
            this.interrupt();
        }

        public void init(Surface surface) {
            this.surface = surface;
        }

        /**
         * 初始化解码器
         */
        private void initMediacodecDecode(int width, int height) {
            if (mMediaCodec == null) {
                try {
                    //通过多媒体格式名创建一个可用的解码器
                    mMediaCodec = MediaCodec.createDecoderByType(VIDEOFORMAT_H264);
                } catch (IOException e) {
                    Log.e("=====", "createDecoderByType==>" + e.toString());
                    e.printStackTrace();
                }

                MediaFormat mediaformat = MediaFormat.createVideoFormat(VIDEOFORMAT_H264, width, height);
                //设置帧率
                mediaformat.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
                //crypto:数据加密 flags:编码器/编码器
                try {
                    mMediaCodec.configure(mediaformat, surface, null, 0);
                    mMediaCodec.start();
                } catch (Exception e) {
                    Logcat.v().tag(LogcatConstants.FPV_PREVIEW).msg("initMediaCodec err:" + e.toString()).out();

                    Log.e("=====", "==>" + e.toString());
                    AiCameraApplication.mApplication.mHandler.post(() -> {
                        DlgUtils.toast(AiCameraApplication.getContext(), AiCameraApplication.getContext().getResources().getString(R.string.err_mediacodec_create));
                    });
                }

                //初始化解码器格式
                mMediaCodec.setVideoScalingMode(MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            }
        }

        @Override
        public void run() {
            ByteBuffer byteBuffer = null;
//            AVFrame avFrame = null;
            CameraTest.Frame frame;
            initMediacodecDecode(VIDEO_WIDTH, VIDEO_HEIGHT);
            try {
                Log.e("=====", "==>run");
                while (!this.isInterrupted()) {
                    //需要原子操作
//                    if (mFrmList != null)
//                        avFrame = mFrmList.take();
//
//                    if (avFrame == null)
//                        continue;
                    frame = cameraTest.mFrmList.take();
                    if (frame == null) continue;


                    //1 准备填充器
                    int inIndex = -1;

                    try {
                        inIndex = mMediaCodec.dequeueInputBuffer(CODE_TIME_OUT * 2);
                    } catch (Exception e) {
                        Log.e("=====", "dequeueInputBuffer==>" + e.toString());
                    }
                    Log.e("=====", "inIndex==>" + inIndex);

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
                            byteBuffer.put(frame.data, 0, frame.len);
                            mMediaCodec.queueInputBuffer(inIndex, 0, frame.len, 0, 0);
                        } catch (Exception e) {
//                        ex.printStackTrace();
                            Log.e("=====", "queueInputBuffer err==>" + e.toString());
                            continue;
                        }
                    } else {
                        SystemClock.sleep(10);
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
                    Log.e("=====", "outIndex==>" + outIndex);
                    if (outIndex >= 0) {
                        boolean doRender = (info.size != 0);
                        try {
                            mMediaCodec.releaseOutputBuffer(outIndex, doRender);
                        } catch (Exception e) {
                            Log.e("=====", "releaseOutputBuffer err==>" + e.toString());
                        }
//                    if (mOnDecodeListener != null) {
//                        mOnDecodeListener.decodeResult(avFrame);
//                    }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.e("=====", "111==>" + e.getMessage());
            }
            try {
//                Log.e("=====", "==>mMediaCodec.release");
                if (mMediaCodec != null) {
                    mMediaCodec.stop();
                    mMediaCodec.release();
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
                Log.e("=====", "222==>" + e.getMessage());
            } finally {
                mMediaCodec = null;
                Log.e("=====", "==>mMediaCodec.release");
//            mMediaFormatWidth = 0;
//            mMediaFormatHeight = 0;
            }
        }
    }

    private class PlayerThread extends Thread {
        private MediaCodec decoder = null;
        private boolean isRun = true;
        private Surface surface;

        public PlayerThread() {
        }

        public void startThread() {
            isRun = true;
            this.start();
        }

        public void stopThread() {
            this.interrupt();
            this.isRun = false;
        }

        public void init(Surface surface) {
            this.surface = surface;
        }

        @SuppressLint("NewApi")
        @Override
        public void run() {
            MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", VIDEO_WIDTH, VIDEO_HEIGHT);
//            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 2500000);
            mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 1);
            try {
                decoder = MediaCodec.createDecoderByType("video/avc");
            } catch (IOException e) {
                Log.d("Fuck", "Fail to create MediaCodec: " + e.toString());
            }
            decoder.configure(mediaFormat, surface, null, 0);
            //decoder.configure(mediaFormat, null, null, 0);
            decoder.start();

            ByteBuffer[] inputBuffers = decoder.getInputBuffers();
            ByteBuffer[] outputBuffers = decoder.getOutputBuffers();
            if (null == inputBuffers) {
                Log.d("Fuck", "null == inputBuffers");
            }
            if (null == outputBuffers) {
                Log.d("Fuck", "null == outbputBuffers 111");
            }

            int mCount = 0;
            AVFrame avFrame = null;

            while (isRun) {
                try {
                    avFrame = mFrmList.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (avFrame == null)
                    continue;

                int inputBufferIndex = decoder.dequeueInputBuffer(-1);
                if (inputBufferIndex >= 0) {
                    ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                    inputBuffer.clear();

                    inputBuffer.put(avFrame.frmData, 0, avFrame.frmData.length);
                    // long sample_time = ;
                    decoder.queueInputBuffer(inputBufferIndex, 0, avFrame.frmData.length, mCount * 1000000 / 20, 0);
                    ++mCount;
                } else {
                    Log.d("Fuck", "dequeueInputBuffer error");
                }

                MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                int outputBufferIndex = decoder.dequeueOutputBuffer(bufferInfo, 0);
                while (outputBufferIndex >= 0) {
                    decoder.releaseOutputBuffer(outputBufferIndex, true);
                    outputBufferIndex = decoder.dequeueOutputBuffer(bufferInfo, 0);
                }

                if (outputBufferIndex >= 0) {
                    decoder.releaseOutputBuffer(outputBufferIndex, false);
                } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    Log.d("Fuck", "outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED");
                } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    // Subsequent data will conform to new format.
                    Log.d("Fuck", "outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED");
                }
            }// end of for
        }// end of run
    }
}
