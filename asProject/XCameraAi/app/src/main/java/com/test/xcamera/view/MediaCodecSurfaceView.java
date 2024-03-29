package com.test.xcamera.view;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.test.xcamera.player.AVFrame;
import com.test.xcamera.utils.MediaCodecUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class MediaCodecSurfaceView extends SurfaceView {

    private final String TAG = MediaCodecSurfaceView.class.getSimpleName();

    //设置解码分辨率
    private final int VIDEO_WIDTH = 1920;//2592
    private final int VIDEO_HEIGHT = 1080;//1520

    //解码帧率 1s解码30帧
    private final int FRAME_RATE = 30;

    //支持格式
    private final String VIDEOFORMAT_H264 = "video/avc";
    private final String VIDEOFORMAT_MPEG4 = "video/mp4v-es";
    private final String VIDEOFORMAT_HEVC = "video/hevc";

    //默认格式
    private String mMimeType = VIDEOFORMAT_H264;

    //接收的视频帧队列
    private volatile ArrayList<AVFrame> mFrmList = new ArrayList<>();

    //解码支持监听器
    private OnSupportListener mSupportListener;
    //解码结果监听
    private OnDecodeListener mOnDecodeListener;

    private MediaCodec mMediaCodec;
    private DecodeThread mDecodeThread;
    private Surface mSurface;


    private int mVideoWidth;
    private int mVideoHeight;

    private double aspectRatio;


    public MediaCodecSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(mCallback);
    }

    private SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {

            Log.i(TAG, "surfaceCreated");
            mSurface = holder.getSurface();
            init();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.i(TAG, "surfaceChanged width = " + width + " height = " + height);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.i(TAG, "surfaceDestroyed");
            unInit();
        }
    };


    public void init() {

        Log.i(TAG, "init");

        if (mDecodeThread != null) {
            mDecodeThread.stopThread();
            try {
                mDecodeThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mDecodeThread = null;
        }

        if (mMediaCodec != null) {
            mMediaCodec.stop();
            mMediaCodec.release();
            mMediaCodec = null;
        }

        try {
            //通过多媒体格式名创建一个可用的解码器
            mMediaCodec = MediaCodec.createDecoderByType(mMimeType);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Init Exception " + e.getMessage());
        }
        //初始化解码器格式
        MediaFormat mediaformat = MediaFormat.createVideoFormat(mMimeType, VIDEO_WIDTH, VIDEO_HEIGHT);
        //设置帧率
        mediaformat.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        //crypto:数据加密 flags:编码器/编码器
        mMediaCodec.configure(mediaformat, mSurface, null, 0);
        mMediaCodec.start();

        mMediaCodec.setVideoScalingMode(MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);

        mDecodeThread = new DecodeThread();
        mDecodeThread.start();
    }

    public void unInit() {
        if (mDecodeThread != null) {
            mDecodeThread.stopThread();
            try {
                mDecodeThread.join(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mDecodeThread = null;
        }
        try {
            if (mMediaCodec != null) {
                mMediaCodec.stop();
                mMediaCodec.release();
                mMediaCodec = null;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        mFrmList.clear();
    }

    public void bindSurface() {

    }


    public int getVideoWidth() {
        return mVideoWidth;
    }

    public int getVideoHeight() {
        return mVideoHeight;
    }


    public void setOnSupportListener(OnSupportListener listener) {
        mSupportListener = listener;
    }

    public void setOnDecodeListener(OnDecodeListener listener) {
        mOnDecodeListener = listener;
    }

    private class DecodeThread extends Thread {

        private boolean isRunning = true;

        public synchronized void stopThread() {
            isRunning = false;
        }

        public boolean isRunning() {
            return isRunning;
        }

        @Override
        public void run() {

            Log.i(TAG, "===start DecodeThread===");

            //存放目标文件的数据
            ByteBuffer byteBuffer = null;
            //解码后的数据，包含每一个buffer的元数据信息，例如偏差，在相关解码器中有效的数据大小
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            long startMs = System.currentTimeMillis();
            AVFrame avFrame = null;
            while (isRunning) {

                if (mFrmList.isEmpty()) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                avFrame = mFrmList.remove(0);

                long startDecodeTime = System.currentTimeMillis();

                //1 准备填充器
                int inIndex = -1;

                try {
                    if (avFrame != null) {
                        inIndex = mMediaCodec.dequeueInputBuffer(avFrame.getTimeStamp());
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    Log.e(TAG, "IllegalStateException dequeueInputBuffer ");
                    if (mSupportListener != null) {
                        mSupportListener.UnSupport();
                    }
                }

                if (inIndex >= 0) {
                    //2 准备填充数据
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        byteBuffer = mMediaCodec.getInputBuffers()[inIndex];
                        byteBuffer.clear();
                    } else {
                        byteBuffer = mMediaCodec.getInputBuffer(inIndex);
                    }

                    if (byteBuffer == null) {
                        continue;
                    }

                    byteBuffer.put(avFrame.frmData, 0, avFrame.frmData.length);
                    //3 把数据传给解码器
                    mMediaCodec.queueInputBuffer(inIndex, 0, avFrame.frmData.length, 0, 0);

                } else {
                    SystemClock.sleep(50);
                    continue;
                }

                //这里可以根据实际情况调整解码速度
//				long sleep = 50;
//
//				if (mFrmList.size() > 20) {
//					sleep = 0;
//				}
//
//				SystemClock.sleep(sleep);


                int outIndex = MediaCodec.INFO_TRY_AGAIN_LATER;

                //4 开始解码
                try {
                    outIndex = mMediaCodec.dequeueOutputBuffer(info, 0);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    Log.e(TAG, "IllegalStateException dequeueOutputBuffer " + e.getMessage());
                }

                if (outIndex >= 0) {

                    //帧控制
//					while (info.presentationTimeUs / 1000 > System.currentTimeMillis() - startMs) {
//						try {
////							Thread.sleep(100);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//					}
                    boolean doRender = (info.size != 0);

                    //对outputbuffer的处理完后，调用这个函数把buffer重新返回给codec类。
                    //调用这个api之后，SurfaceView才有图像
                    mMediaCodec.releaseOutputBuffer(outIndex, doRender);

                    if (mOnDecodeListener != null) {
                        mOnDecodeListener.decodeResult(avFrame);
                    }

//                    Log.i(TAG, "DecodeThread delay = " + (System.currentTimeMillis() - avFrame.getTimeStamp()) + " spent = " + (System.currentTimeMillis() - startDecodeTime) + " size = " + mFrmList.size());
//                    System.gc();

                } else {
                    switch (outIndex) {
                        case MediaCodec.INFO_TRY_AGAIN_LATER: {

                        }
                        break;
                        case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED: {
                            MediaFormat newFormat = mMediaCodec.getOutputFormat();
                            mVideoWidth = newFormat.getInteger("width");
                            mVideoHeight = newFormat.getInteger("height");

                            post(new Runnable() {
                                @Override
                                public void run() {
                                    setAspect((float) mVideoWidth / mVideoHeight);
                                }
                            });

                            //是否支持当前分辨率
                            String support = MediaCodecUtils.getSupportMax(mMimeType);
                            if (support != null) {
                                String width = support.substring(0, support.indexOf("x"));
                                String height = support.substring(support.indexOf("x") + 1, support.length());
                                Log.i(TAG, " current " + mVideoWidth + "x" + mVideoHeight + " mMimeType " + mMimeType);
                                Log.i(TAG, " Max " + width + "x" + height + " mMimeType " + mMimeType);
                                if (Integer.parseInt(width) < mVideoWidth || Integer.parseInt(height) < mVideoHeight) {
                                    if (mSupportListener != null) {
                                        mSupportListener.UnSupport();
                                    }
                                }
                            }
                        }
                        break;
                        case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED: {

                        }
                        break;
                        default: {

                        }
                    }
                }
            }
            Log.i(TAG, "===stop DecodeThread===");
        }
    }

    public void onReceived(AVFrame avFrame) {
        mFrmList.add(avFrame);
    }

    public void setAspect(double aspect) {
        if (aspect > 0) {
            this.aspectRatio = aspect;
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (aspectRatio > 0) {
            int initialWidth = MeasureSpec.getSize(widthMeasureSpec);
            int initialHeight = MeasureSpec.getSize(heightMeasureSpec);

            final int horizPadding = getPaddingLeft() + getPaddingRight();
            final int vertPadding = getPaddingTop() + getPaddingBottom();
            initialWidth -= horizPadding;
            initialHeight -= vertPadding;

            final double viewAspectRatio = (double) initialWidth / initialHeight;
            final double aspectDiff = aspectRatio / viewAspectRatio - 1;

            if (Math.abs(aspectDiff) > 0.01) {
                if (aspectDiff > 0) {
                    initialHeight = (int) (initialWidth / aspectRatio);
                } else {
                    initialWidth = (int) (initialHeight * aspectRatio);
                }
                initialWidth += horizPadding;
                initialHeight += vertPadding;
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(initialWidth, MeasureSpec.EXACTLY);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(initialHeight, MeasureSpec.EXACTLY);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
