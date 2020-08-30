package com.test.xcamera.glview;

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
import android.widget.RelativeLayout;

import com.test.xcamera.R;

import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.constants.LogcatConstants;
import com.test.xcamera.player.AVFrame;
import com.test.xcamera.utils.DlgUtils;
import com.test.xcamera.view.AutoTrackingRectViewFix;
import com.test.xcamera.view.OnDecodeListener;
import com.moxiang.common.logging.Logcat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by zll on 2019/7/10.
 */
public class PreviewGLSurfaceView2 extends TextureView {
    //设置解码分辨率
    public static int VIDEO_WIDTH = 1920;//2592
    public static int VIDEO_HEIGHT = 1080;//1520

    //解码帧率 1s解码30帧
    private final int FRAME_RATE = 30;
    private static final int CODE_TIME_OUT = 30 * 1000;

    //支持格式
    private final String VIDEOFORMAT_H264 = "video/avc";

    //默认格式
    private String mMimeType = VIDEOFORMAT_H264;

    //接收的视频帧队列
    private LinkedBlockingQueue<AVFrame> mFrmList = new LinkedBlockingQueue<>();

    private MediaCodec mMediaCodec;
    private DecodeThread mDecodeThread;
    private Surface mSurface;
    private SurfaceTexture mSurfaceTexture;
    private AutoTrackingRectViewFix mTrackingRectView;

    //view的宽高
    private int mHeight, mWidth;
    private float mRatio;
    private int mMediaFormatWidth = 0, mMediaFormatHeight = 0;
    //视频在view中的宽高
    private volatile int videoWidth, videoHeight, videoWidthFix = 0, videoHeightFix = 0;
    private volatile boolean mMirror = false;
    private OnDecodeListener mOnDecodeListener;

    public PreviewGLSurfaceView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.e("=====", "==>PreviewGLSurfaceView");
        setSurfaceTextureListener(textureListener);
    }

    private SurfaceTextureListener textureListener = new SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            Log.e("=====", "==>onSurfaceTextureAvailable");

            init();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
            mHeight = ((RelativeLayout) getParent()).getHeight();
            mWidth = ((RelativeLayout) getParent()).getWidth();
            mRatio = mWidth / (float) mHeight;
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            if (mDecodeThread != null) {
                mDecodeThread.stopThread();
                mDecodeThread.interrupt();
                mDecodeThread = null;
            }
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };

    public void init() {
        if (mDecodeThread == null) {
            mDecodeThread = new PreviewGLSurfaceView2.DecodeThread();
            mDecodeThread.start();
        } else if (!mDecodeThread.isAlive())
            mDecodeThread.start();
    }

    public void releaseCodec() {
        releaseThread();
        releaseMediaCodec();
        if (mFrmList != null) {
            mFrmList.clear();
        }
    }

    public void release() {
        releaseCodec();

        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        if (mSurface != null) {
            mSurface.release();
            mSurface = null;
        }
        if (mFrmList != null) {
            mFrmList.clear();
        }
    }

    public void releaseMediaCodec() {
        try {
            if (mMediaCodec != null) {
                mMediaCodec.stop();
                mMediaCodec.release();
                mMediaCodec = null;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void releaseThread() {
        if (mDecodeThread != null) {
            mDecodeThread.stopThread();
            mDecodeThread = null;
        }
    }

    private Surface getSurface() {
        if (mSurface == null) {
            mSurface = new Surface(this.getSurfaceTexture());
        }
        return mSurface;
    }

    /**
     * 清除视频帧缓存
     */
    private void clearVideoCache() {
        if (mFrmList != null) {
            Log.e("=====", "==>clearVideoCache");
            mFrmList.clear();
            if (mMediaCodec != null) {
                mMediaCodec.flush();
            }
        }
    }

    public void onReceived(AVFrame avFrame) {
        if (mFrmList.size() > 60) {
            clearVideoCache();
        }
        Log.e("=====", "offer  ==>" + toHexString(avFrame.frmData, 10));
        mFrmList.offer(avFrame);
    }

    private class DecodeThread extends Thread {
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
                    mMediaCodec.configure(mediaformat, getSurface(), null, 0);
                    mMediaCodec.start();
                } catch (Exception e) {
                    Logcat.v().tag(LogcatConstants.FPV_PREVIEW).msg("initMediaCodec err:" + e.toString()).out();

                    Log.e("=====", "==>" + e.toString());
                    AiCameraApplication.mApplication.mHandler.post(() -> {
                        DlgUtils.toast(AiCameraApplication.getContext(), getResources().getString(R.string.err_mediacodec_create));
                    });
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
                Log.e("=====", "take  ==>" + toHexString(avFrame.frmData, 10));

                //1 准备填充器
                int inIndex = -1;

//                Log.e("=====", String.format("run  mMediaFormatWidth:%d  %d  %d  %d", mMediaFormatWidth, mMediaFormatHeight,
//                        avFrame.getVideoWidth(), avFrame.getVideoHeight()));

                if (mMediaFormatWidth != avFrame.getVideoWidth() || mMediaFormatHeight != avFrame.getVideoHeight()
                        || videoHeightFix == 0 || videoWidthFix == 0) {
                    mMediaFormatWidth = avFrame.getVideoWidth();
                    mMediaFormatHeight = avFrame.getVideoHeight();

                    if (videoHeightFix != 0)
                        mMediaCodec.flush();

                    post(() -> {
                        changeSize(mMediaFormatWidth, mMediaFormatHeight);
                    });
                }

                mMirror = avFrame.getMirror() == 0;

                try {
                    inIndex = mMediaCodec.dequeueInputBuffer(CODE_TIME_OUT);
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

                    Log.e("=====", "byteBuffer.put  ==>" + toHexString(avFrame.frmData, 10));

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
                Log.e("=====", "outIndex==>" + outIndex);
                if (outIndex >= 0) {
                    boolean doRender = (info.size != 0);
                    Log.e("=====", "dequeueOutputBuffer doRender==>" + doRender);
                    try {
                        mMediaCodec.releaseOutputBuffer(outIndex, doRender);
                    } catch (Exception e) {
                        Log.e("=====", "releaseOutputBuffer err==>" + e.toString());
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
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } finally {
                mMediaCodec = null;
                mMediaFormatWidth = 0;
                mMediaFormatHeight = 0;
            }
        }
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//
////                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
//
//    }
    /***
     * 设置View的宽高 与预览视频同步
     *
     * @param width 视频的宽度
     * @param height 视频的高度
     */
    public void changeSize(int width, int height) {
        if (videoHeight != height || videoWidth != width
                || videoHeightFix == 0 || videoWidthFix == 0) {
            videoWidth = width;
            videoHeight = height;

            //如果视频的长宽比比view的长宽比大 则视频宽度填满view宽度。以view的宽度为准
            float videoRatio = (float) width / height;
            if (videoRatio > mRatio) {
                videoHeightFix = (int) ((float) mWidth / width * height);
                videoWidthFix = mWidth;
            } else {
                videoHeightFix = mHeight;
                videoWidthFix = (int) ((float) mHeight / height * width);
            }

            Logcat.v().tag(LogcatConstants.FPV_PREVIEW).msg(String.format("changeSize  videoWidthFix:%d   videoHeightFix:%d", videoWidthFix, videoHeightFix)).out();
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) this.getLayoutParams();
            lp.width = videoWidthFix;
            lp.height = videoHeightFix;

            this.setLayoutParams(lp);
        }
    }

    public void setDecodeListener(OnDecodeListener onDecodeListener) {
        mOnDecodeListener = onDecodeListener;
    }

    public void setTrackingRectView(AutoTrackingRectViewFix trackingRectView) {
        this.mTrackingRectView = trackingRectView;
    }

    /**
     * 方法四：byte[]-->hexString
     *
     * @param bytes
     * @return
     */
    public String toHexString(byte[] bytes, int len) {
        int length = len == -1 ? bytes.length : len;
        StringBuilder sb = new StringBuilder(length * 2);
        // 使用String的format方法进行转换
        for (int i = 0; i < length; i++) {
            sb.append(String.format("%02x ", new Integer(bytes[i] & 0xff)));
        }

        return sb.toString();
    }

}