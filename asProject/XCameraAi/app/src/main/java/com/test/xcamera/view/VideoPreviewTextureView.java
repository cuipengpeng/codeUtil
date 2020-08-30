package com.test.xcamera.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import com.test.xcamera.player.AVFrame;
import com.test.xcamera.enumbean.ShootMode;
import com.test.xcamera.managers.ShotModeManager;
import com.test.xcamera.utils.MediaCodecUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by zll on 2019/7/10.
 */

public class VideoPreviewTextureView extends TextureView {
    private final String TAG = VideoPreviewTextureView.class.getSimpleName();

    //设置解码分辨率
    public static int VIDEO_WIDTH = 1920;//2592
    public static int VIDEO_HEIGHT = 1080;//1520

    //解码帧率 1s解码30帧
    private final int FRAME_RATE = 30;
    private int CODE_TIME_OUT = 30 * 1000;

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
    private SurfaceTexture mSurfaceTexture;
    private TextureView mTextureView;

    private int mVideoWidth;
    private int mVideoHeight;

    private int mRatioWidth = 0;
    private int mRatioHeight = 0;
    private int mRealWidth = 0;
    private int mRealHeight = 0;
    /**
     * 旋转宽高
     */
    private int mWidthRotation = 1920;
    private int mHeightRotation = 1080;
    private int mRotation = 0;
    private double aspectRatio;
    private boolean isRotate;

    public VideoPreviewTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTextureView = this;

    }

    public void setTextureListener(int angle) {
        setmRotation(angle);
        setSurfaceTextureListener(textureListener);
    }

    public void setCodeTimeOut(int codeTimeOut) {
        CODE_TIME_OUT = codeTimeOut;
    }

    private SurfaceTextureListener textureListener = new SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            if (mSurfaceTexture != null) {
                mTextureView.setSurfaceTexture(mSurfaceTexture);
            }
            Log.d("TAG_OOOOO", "onSurfaceTextureAvailable: 1111111 ");
            if (mMediaCodec == null) {
                Log.d("TAG_OOOOO", "onSurfaceTextureAvailable: 222222 ");
                init();
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            Log.d(TAG, "onSurfaceTextureDestroyed: ");
            mSurfaceTexture = surfaceTexture;
            if (mDecodeThread != null) {
                mDecodeThread.stopThread();
            }
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };

    public void setVideoResolution() {
        VIDEO_WIDTH = 1920;
        VIDEO_HEIGHT = 1080;
    }

    public void setPhotoResolution() {
        VIDEO_WIDTH = 1440;
        VIDEO_HEIGHT = 1080;
    }

    public void restartCodec() {
        releaseCodec();
        init();
    }

    public int getWidthRotation() {
        return mWidthRotation;
    }

    public void setWidthRotation(int mWidthRotation) {
        this.mWidthRotation = mWidthRotation;
    }

    public int getHeightRotation() {
        return mHeightRotation;
    }

    public void setHeightRotation(int mHeightRotation) {
        this.mHeightRotation = mHeightRotation;
    }

    public int getmRotation() {
        return mRotation;
    }

    public void setmRotation(int mRotation) {
        this.mRotation = mRotation;
    }

    public void init() {
        try {
            if (ShotModeManager.getInstance().getmShootMode() == ShootMode.VIDEO) {
                setVideoResolution();
            } else if (ShotModeManager.getInstance().getmShootMode() == ShootMode.PHOTO) {
                setPhotoResolution();
            }
            Log.i(TAG, "init...  current mode = " + ShotModeManager.getInstance().getmShootMode());

            if (mMediaCodec == null) {
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
                mediaformat.setInteger(MediaFormat.KEY_ROTATION, mRotation);
                //crypto:数据加密 flags:编码器/编码器
                mMediaCodec.configure(mediaformat, getSurface(), null, 0);
                mMediaCodec.start();


                mMediaCodec.setVideoScalingMode(MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            }
            if (mDecodeThread == null) {
                mDecodeThread = new DecodeThread();
                mDecodeThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            init();
        }

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

    public void restartPlay() {
        releaseThread();
        if (mDecodeThread == null) {
            mDecodeThread = new DecodeThread();
            mDecodeThread.isRunning = true;
            mDecodeThread.start();

        } else if (!mDecodeThread.isRunning) {
            mDecodeThread.isRunning = true;
            mDecodeThread.start();
        }
    }

    private Surface getSurface() {
        if (mSurface == null) {
            mSurface = new Surface(mTextureView.getSurfaceTexture());
        }
        return mSurface;
    }

    private class DecodeThread extends Thread {

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

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
                if (!isRunning) {
                    break;
                }
                avFrame = mFrmList.remove(0);
                long startDecodeTime = System.currentTimeMillis();

                //1 准备填充器
                int inIndex = -1;

                try {
                    if (avFrame != null) {
                        inIndex = mMediaCodec.dequeueInputBuffer(CODE_TIME_OUT);
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
                    try {
                        if (!isRunning) {
                            break;
                        }
                        mMediaCodec.queueInputBuffer(inIndex, 0, avFrame.frmData.length, 0, 0);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

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
//                mMediaCodec.getInputFormat().setInteger(MediaFormat.KEY_ROTATION, 90);
                //4 开始解码
                try {
                    outIndex = mMediaCodec.dequeueOutputBuffer(info, CODE_TIME_OUT);
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
                    if (!isRunning) {
                        break;
                    }
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
//                                    setAspect((float) mVideoWidth / mVideoHeight);
                                    setAspectRatio(mVideoWidth, mVideoHeight);
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

    public void setAspectRatio(final int width, final int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        mRatioWidth = width;
        mRatioHeight = height;
//        new Handler().post(new Runnable() {
//            @Override
//            public void run() {
//                ToastUtil.showToast(AppContext.getInstance(), "width = " + width + "  height = " + height);
//            }
//        });
        requestLayout();
    }

    public void onReceived(AVFrame avFrame) {
        if (mDecodeThread != null && mDecodeThread.isRunning) {
            mFrmList.add(avFrame);
        }
    }

    public void setAspect(double aspect) {
        if (aspect > 0) {
            this.aspectRatio = aspect;
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (isRotate) {
            setMeasuredDimension(mWidthRotation, mHeightRotation);
        } else {
            if (0 == mRatioWidth || 0 == mRatioHeight) {
                Log.i("club:", "club:mRatioWidth:" + mRatioWidth + " mRatioHeight" + mRatioHeight);
                setMeasuredDimension(width, height);
            } else {
                if (width < height * mRatioWidth / mRatioHeight) {
                    mRealWidth = width;
                    mRealHeight = width * mRatioHeight / mRatioWidth;
                    setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
                } else {
                    mRealWidth = height * mRatioWidth / mRatioHeight;
                    mRealHeight = height;
                    setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
                }
            }
        }
    }

    public void setRotate(boolean isRotate) {
        this.isRotate = isRotate;
    }

    public int getViewWidth() {
        return mRealWidth;
    }

    public int getViewHeight() {
        return mRealHeight;
    }

}
