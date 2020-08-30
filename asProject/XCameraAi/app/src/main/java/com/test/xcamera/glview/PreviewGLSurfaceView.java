package com.test.xcamera.glview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.RelativeLayout;

import com.effect_opengl.EGLHelper;
import com.effect_opengl.EffectManager;
import com.effect_opengl.ShaderHelper;
import com.test.xcamera.R;

import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.AudioFrameInfo;
import com.test.xcamera.constants.LogcatConstants;
import com.test.xcamera.player.AVFrame;
import com.test.xcamera.utils.BitmapUtils;
import com.test.xcamera.utils.DlgUtils;
import com.test.xcamera.view.AutoTrackingRectViewFix;
import com.test.xcamera.view.OnDecodeListener;

import com.editvideo.RawResourceReader;
import com.moxiang.common.logging.Logcat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.concurrent.LinkedBlockingQueue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.glGenTextures;

/**
 * Created by zll on 2019/7/10.
 */
@SuppressLint("NewApi")
@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public class PreviewGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
    private static final String TAG = "PreviewGLSurfaceView";
    public static final boolean CODEC_DEBUG = false;
    //设置解码分辨率
    public static int VIDEO_WIDTH = 1920;//2592
    public static int VIDEO_HEIGHT = 1080;//1520

    private int mMediaFormatWidth = 0;
    private int mMediaFormatHeight = 0;

    //解码帧率 1s解码30帧
    private final int FRAME_RATE = 30;
    private volatile boolean mMirror = false;

    /**
     * mediacodec 超时时间 为一帧的时间
     */
    private final int CODE_TIME_OUT = 100 * 1000;

    //支持格式
    private final String VIDEOFORMAT_H264 = "video/avc";


    //默认格式
    private String mMimeType = VIDEOFORMAT_H264;

    //接收的视频帧队列
//    private volatile List<AVFrame> mFrmList = new LinkedList();
    private LinkedBlockingQueue<AVFrame> mFrmList = new LinkedBlockingQueue<>();

    //解码结果监听
    private OnDecodeListener mOnDecodeListener;
    private GetGLBitmap mGetGLBitmap;

    private MediaCodec mMediaCodec;
    private DecodeThread mDecodeThread;
    PlayerThread mPlayerThread;
    private Surface mSurface;
    private SurfaceTexture mSurfaceTexture;
    private AutoTrackingRectViewFix mTrackingRectView;

    //view的宽高
    private int mHeight, mWidth;
    private float mRatio;
    //视频在view中的宽高
    private volatile int videoWidth, videoHeight, videoWidthFix = 0, videoHeightFix = 0;

    private int[] mCameraPreviewtextures = new int[1];
    private float[] m_videoTextureTransform = new float[16];
    private SurfaceTexture surfaceTexture;
    private boolean m_frameAvailable;

    private FloatBuffer textureBuffer;
    private float textureCoords[] = {
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f};
    private int m_shaderProgram;
    private int m_textureParamHandle;
    private int m_textureCoordinateHandle;
    private int m_positionHandle;
    private int m_textureTranformHandle;
    private int m_width;
    private int m_height;

    private static short m_drawOrder[] = {0, 1, 2, 0, 2, 3};
    private FloatBuffer m_vertexBuffer;
    private ShortBuffer m_drawListBuffer;
    private static float squareSize = 1.0f;
    private static float m_squareCoords[] = {
            -squareSize, squareSize,   // top left
            -squareSize, -squareSize,   // bottom left
            squareSize, -squareSize,    // bottom right
            squareSize, squareSize}; // top right

    private int texWidth;
    private int texHeight;
    private int displayTex;
    private MediaCodecThread mediaCodecThread = new MediaCodecThread();

    private Context mContext;


    public PreviewGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setEGLContextClientVersion(2); //设置使用OPENGL ES2.0
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        surfaceTexture = new SurfaceTexture(false);
    }

    public void setTrackingRectView(AutoTrackingRectViewFix trackingRectView) {
        this.mTrackingRectView = trackingRectView;
    }

    /**
     * 清除视频帧缓存
     */
    private void clearVideoCache() {
        if (mFrmList != null) {
            Log.e("=====", "==>clearVideoCache");
            mFrmList.clear();
            if (mMediaCodec != null) {
                try {
                    mMediaCodec.flush();
                } catch (RuntimeException e) {
                    Log.e("=====", "flush exception==>" + e.getMessage());
                }
            }
        }
    }

    public void reset() {
        if (CODEC_DEBUG) {
            if (mPlayerThread == null) {
                mPlayerThread = new PlayerThread();
                mPlayerThread.start();
            } else if (!mPlayerThread.isAlive())
                mPlayerThread.start();
        } else {
            if (mDecodeThread == null) {
                mDecodeThread = new DecodeThread();
                mDecodeThread.start();
            } else if (!mDecodeThread.isAlive())
                mDecodeThread.start();
        }
    }

    public void release() {
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        if (mSurface != null) {
            mSurface.release();
            mSurface = null;
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        initOES();
        if (CODEC_DEBUG) {
            if (mPlayerThread == null) {
                mPlayerThread = new PlayerThread();
                mPlayerThread.startThread();
            }
        } else {
            if (mDecodeThread == null) {
                mDecodeThread = new DecodeThread();
                mDecodeThread.startThread();
            } else if (!mDecodeThread.isAlive())
                mDecodeThread.startThread();
        }

//        if (mAacDecoderUtil == null) {
//            mAacDecoderUtil = new AACDecoderUtil();
//            if (mAacDecoderUtil.prepare()) {
//                mAacDecoderUtil.init();
//            }
//            Log.d(TAG, "init audio decoder");
//        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
//        if (mAacDecoderUtil != null) {
//            mAacDecoderUtil.stop();
//            mAacDecoderUtil = null;
//        }

        if (CODEC_DEBUG) {
            if (mPlayerThread != null) {
                mPlayerThread.stopThread();
                mPlayerThread = null;
            }
        } else {
            if (mDecodeThread != null) {
                mDecodeThread.stopThread();
                mDecodeThread = null;
            }
        }

        release();
    }

    /**
     * 初始化,没有滤镜效果的预览需要的参数
     */
    private void initOES() {
        final String vertexShader = RawResourceReader.readTextFileFromRawResource(getContext(), R.raw.vetext_sharder);
        final String fragmentShader = RawResourceReader.readTextFileFromRawResource(getContext(), R.raw.fragment_sharder);

        final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);
        m_shaderProgram = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                new String[]{"texture", "vPosition", "vTexCoordinate", "textureTransform"});

        GLES20.glUseProgram(m_shaderProgram);
        m_textureParamHandle = GLES20.glGetUniformLocation(m_shaderProgram, "texture");
        m_textureCoordinateHandle = GLES20.glGetAttribLocation(m_shaderProgram, "vTexCoordinate");
        m_positionHandle = GLES20.glGetAttribLocation(m_shaderProgram, "vPosition");
        m_textureTranformHandle = GLES20.glGetUniformLocation(m_shaderProgram, "textureTransform");


        ByteBuffer dlb = ByteBuffer.allocateDirect(m_drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        m_drawListBuffer = dlb.asShortBuffer();
        m_drawListBuffer.put(m_drawOrder);
        m_drawListBuffer.position(0);

        // Initialize the texture holder
        ByteBuffer bb = ByteBuffer.allocateDirect(m_squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());

        m_vertexBuffer = bb.asFloatBuffer();
        m_vertexBuffer.put(m_squareCoords);
        m_vertexBuffer.position(0);


        ByteBuffer texturebb = ByteBuffer.allocateDirect(textureCoords.length * 4);
        texturebb.order(ByteOrder.nativeOrder());

        textureBuffer = texturebb.asFloatBuffer();
        textureBuffer.put(textureCoords);
        textureBuffer.position(0);

        // Generate the actual texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        glGenTextures(1, mCameraPreviewtextures, 0);
        //创建摄像机需要的Preview Texture

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mCameraPreviewtextures[0]);
        surfaceTexture.attachToGLContext(mCameraPreviewtextures[0]);
        surfaceTexture.setOnFrameAvailableListener(this);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.mHeight = ((RelativeLayout) getParent()).getHeight();
        this.mWidth = ((RelativeLayout) getParent()).getWidth();
        this.mRatio = this.mWidth / (float) this.mHeight;

        m_width = width;
        m_height = height;
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        synchronized (this) {
            if (m_frameAvailable) {
                surfaceTexture.updateTexImage();
                surfaceTexture.getTransformMatrix(m_videoTextureTransform);
                m_frameAvailable = false;
            }
        }

        texWidth = videoWidthFix;
        texHeight = videoHeightFix;
        if (EffectManager.instance().getmRenderCore() != null) {
            displayTex = EffectManager.instance().getmRenderCore().renderVideoEffect(mCameraPreviewtextures[0], null, true,
                    getWidth(), getHeight(), System.currentTimeMillis(),
                    180, 180, mMirror, 180, false);
        }
        if (displayTex == mCameraPreviewtextures[0]) {
            this.drawTextureOES(displayTex, texWidth, texHeight);
        } else {
            if (EffectManager.instance().getmRenderCore() != null)
                EffectManager.instance().getmRenderCore().drawTexture(displayTex, texWidth, texHeight,
                        getWidth(), getHeight());
        }

        EGLHelper.checkGlError("drawTexture");

        mediaCodecThread.frameAvailable(surfaceTexture);

        if (mGetGLBitmap != null) {
            Bitmap glBitmap = BitmapUtils.createBitmapFromGLSurface(0, 0,
                    this.getWidth(), this.getHeight(), gl);
//            BitmapUtils.saveImg(bbb, "save_after.jpg");
            mGetGLBitmap.getBitmap(glBitmap);
            mGetGLBitmap = null;
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        synchronized (this) {
            m_frameAvailable = true;
        }
        requestRender();
    }

    private void drawTextureOES(int displayTex, int texWidht, int texHeight) {
        GLES20.glUseProgram(m_shaderProgram);
        float ar = (float) texWidht / texHeight;
        float disar = (float) m_width / m_height;
        float cropWidth = 1.0f;
        float cropHeight = 1.0f;
        if (ar > disar) {
            cropHeight = 1.0f;
            cropWidth = ar / disar;
        } else {
            cropWidth = 1.0f;
            cropHeight = disar / ar;
        }
        m_vertexBuffer.put(0, -cropWidth);
        m_vertexBuffer.put(1, cropHeight);
        m_vertexBuffer.put(2, -cropWidth);
        m_vertexBuffer.put(3, -cropHeight);
        m_vertexBuffer.put(4, cropWidth);
        m_vertexBuffer.put(5, -cropHeight);
        m_vertexBuffer.put(6, cropWidth);
        m_vertexBuffer.put(7, cropHeight);
        m_vertexBuffer.position(0);

        GLES20.glEnableVertexAttribArray(m_positionHandle);
        GLES20.glVertexAttribPointer(m_positionHandle, 2, GLES20.GL_FLOAT, false, 0, m_vertexBuffer);

        EGLHelper.checkGlError("glEnableVertexAttribArray");
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, displayTex);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glUniform1i(m_textureParamHandle, 0);

        GLES20.glEnableVertexAttribArray(m_textureCoordinateHandle);
        GLES20.glVertexAttribPointer(m_textureCoordinateHandle, 4, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glUniformMatrix4fv(m_textureTranformHandle, 1, false, m_videoTextureTransform, 0);

        EGLHelper.checkGlError("glUniformMatrix4fv");

        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, m_drawOrder.length, GLES20.GL_UNSIGNED_SHORT, m_drawListBuffer);
        EGLHelper.checkGlError("glDrawElements");


        GLES20.glDisableVertexAttribArray(m_positionHandle);
        GLES20.glDisableVertexAttribArray(m_textureCoordinateHandle);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        GLES20.glUseProgram(0);
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
            this.interrupt();
        }

        public void startThread() {
            isRunning = true;
            this.start();
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
//                Log.e("=====", "take  ==>" + toHexString(avFrame.frmData, 10));

                //1 准备填充器
                int inIndex = -1;

//                Log.e("=====", String.format("run  mMediaFormatWidth:%d  %d  %d  %d", mMediaFormatWidth, mMediaFormatHeight,
//                        avFrame.getVideoWidth(), avFrame.getVideoHeight()));

                if (mMediaFormatWidth != avFrame.getVideoWidth() || mMediaFormatHeight != avFrame.getVideoHeight()
                        || videoHeightFix == 0 || videoWidthFix == 0) {

                    mMediaFormatWidth = avFrame.getVideoWidth();
                    mMediaFormatHeight = avFrame.getVideoHeight();

                    if (videoHeightFix != 0)
                        try {
                            mMediaCodec.flush();
                        } catch (Exception e) {
                            Log.e("======", "mMediaCodec flush==>" + e.toString());
                        }

                    changeSize(mMediaFormatWidth, mMediaFormatHeight);
                }

                mMirror = avFrame.getMirror() == 0;

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

    public void destoryOpengl() {
        this.queueEvent(new Runnable() {
            @Override
            public void run() {
                EffectManager.instance().getmRenderCore().destoryGLResource();
            }
        });
    }

    private Surface getSurface() {
        if (mSurface == null) {
            mSurface = new Surface(surfaceTexture);
        }
        return mSurface;
    }

    public void onReceived(AVFrame avFrame) {
        if (mFrmList.size() > 60) {
            clearVideoCache();
        }
//        Log.e("=====", "offer  ==>" + toHexString(avFrame.frmData, 10));
        mFrmList.offer(avFrame);
    }

    public void onReceivedAudio(AudioFrameInfo audioFrameInfo) {
//        if (mAacDecoderUtil != null) {
//            mAacDecoderUtil.onReceivedAudioData(audioFrameInfo);
//        }
    }

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
        }
    }

    public void setDecodeListener(OnDecodeListener onDecodeListener) {
        mOnDecodeListener = onDecodeListener;
    }

    /**
     * 用于获取GLSurfaceView中的图片
     * 设置一次 只会获取一次图片
     */
    public void setGetGLBitmap(GetGLBitmap mGetGLBitmap) {
        this.mGetGLBitmap = mGetGLBitmap;
    }

    public interface GetGLBitmap {
        void getBitmap(Bitmap bm);
    }

    private class PlayerThread extends Thread {
        private MediaCodec decoder = null;
        private boolean isRun = true;

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
            decoder.configure(mediaFormat, getSurface(), null, 0);
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

                mMirror = avFrame.getMirror() == 0;
                if (mMediaFormatWidth != avFrame.getVideoWidth() || mMediaFormatHeight != avFrame.getVideoHeight()
                        || videoHeightFix == 0 || videoWidthFix == 0) {
                    mMediaFormatWidth = avFrame.getVideoWidth();
                    mMediaFormatHeight = avFrame.getVideoHeight();
                    if (videoHeightFix != 0)
                        try {
                            mMediaCodec.flush();
                        } catch (Exception e) {
                            Log.e("======", "mMediaCodec flush==>" + e.toString());
                        }
                    changeSize(mMediaFormatWidth, mMediaFormatHeight);
                }

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

