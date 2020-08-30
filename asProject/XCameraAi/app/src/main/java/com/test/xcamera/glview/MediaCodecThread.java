package com.test.xcamera.glview;

import android.graphics.SurfaceTexture;
import android.opengl.EGLContext;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.FloatBuffer;

/**
 * Created by 周 on 2019/11/7.
 * <p>
 * 该线程就是用来编码的线程
 */
public class MediaCodecThread implements Runnable {
    private static final String TAG = "MediaCodecThread";
    private EGLContext eglContext;
    private FloatBuffer m_vertexBuffer;
    private FloatBuffer floatBuffer;
    private Object mReadyFence = new Object();
    private boolean mReady;
    private boolean mRunning;
    private static final int MSG_START_RECORDING = 0;
    private static final int MSG_STOP_RECORDING = 1;
    private static final int MSG_FRAME_AVAILABLE = 2;
    private static final int MSG_SET_TEXTURE_ID = 3;
    private static final int MSG_UPDATE_SHARED_CONTEXT = 4;
    private static final int MSG_QUIT = 5;
    private EncoderHandler mHandler;
    private FrameListener frameListener;


    public void setEglContext(EGLContext eglContext) {
        this.eglContext = eglContext;
    }

    public void setCBu(FloatBuffer m_vertexBuffer) {
        this.m_vertexBuffer = m_vertexBuffer;
    }

    public void setBu(FloatBuffer floatBuffer) {
        this.floatBuffer = floatBuffer;
    }

    @Override
    public void run() {
        Looper.prepare();
        synchronized (mReadyFence) {
            mHandler = new EncoderHandler(this);
            mReady = true;
            mReadyFence.notify();
        }
        Looper.loop();
        synchronized (mReadyFence) {
            mReady = mRunning = false;
            mHandler = null;
        }
    }

    public void start(VideoInfo videoInfo) {
        synchronized (mReadyFence) {
            if (mRunning) {
                return;
            }
            mRunning = true;
            new Thread(this, "TextureMovieEncoder").start();
            while (!mReady) {
                try {
                    mReadyFence.wait();
                } catch (InterruptedException ie) {
                    // ignore
                }
            }
        }

        mHandler.sendMessage(mHandler.obtainMessage(MSG_START_RECORDING, videoInfo));
    }

    private static class EncoderHandler extends Handler {
        private WeakReference<MediaCodecThread> mWeakEncoder;

        public EncoderHandler(MediaCodecThread encoder) {
            mWeakEncoder = new WeakReference<MediaCodecThread>(encoder);
        }

        @Override  // runs on encoder thread
        public void handleMessage(Message inputMessage) {
            int what = inputMessage.what;
            Object obj = inputMessage.obj;
            MediaCodecThread encoder = mWeakEncoder.get();
            if (encoder == null) {
                return;
            }
            switch (what) {
                case MSG_START_RECORDING:
                    encoder.handleStartRecording((VideoInfo) obj);
                    break;
                case MSG_STOP_RECORDING:
                    encoder.handleStopRecording();
                    break;
                case MSG_FRAME_AVAILABLE:
                    long timestamp = (((long) inputMessage.arg1) << 32) |
                            (((long) inputMessage.arg2) & 0xffffffffL);
                    encoder.handleFrameAvailable((float[]) obj, timestamp);
                    break;
                case MSG_SET_TEXTURE_ID:
//                    encoder.handleSetTexture(inputMessage.arg1);
                    break;
                case MSG_UPDATE_SHARED_CONTEXT:
                    encoder.handleUpdateSharedContext((EGLContext) inputMessage.obj);
                    break;
                case MSG_QUIT:
                    Looper.myLooper().quit();
                    break;
                default:
                    throw new RuntimeException("Unhandled msg what=" + what);
            }
        }
    }

    /**
     * Handles a request to stop encoding.
     */
    private void handleStopRecording() {
        mVideoEncoder.drainEncoder(true);
        releaseEncoder();
    }

    private void releaseEncoder() {
        mVideoEncoder.release();
        if (mInputWindowSurface != null) {
            mInputWindowSurface.release();
            mInputWindowSurface = null;
        }

        if (mEglCore != null) {
            mEglCore.release();
            mEglCore = null;
        }
    }


    private VideoEncoderCore mVideoEncoder;
    private EglCore mEglCore;
    private WindowSurface mInputWindowSurface;

    /**
     * Starts recording.
     *
     * @param videoInfo
     */
    private void handleStartRecording(VideoInfo videoInfo) {
        try {
            mVideoEncoder = new VideoEncoderCore(videoInfo.width,  videoInfo.height,
                    videoInfo.width * videoInfo.height * 30, videoInfo.file);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

        mEglCore = new EglCore(eglContext, EglCore.FLAG_RECORDABLE);
        mInputWindowSurface = new WindowSurface(mEglCore, mVideoEncoder.getInputSurface(), true);
        mInputWindowSurface.makeCurrent();
    }

    public interface FrameListener {
        void listener();
    }

    public void setFrameListener(FrameListener frameListener) {
        this.frameListener = frameListener;
    }

    /**
     * Tells the video recorder to refresh its EGL surface.  (Call from non-encoder thread.)
     */
    public void updateSharedContext(EGLContext sharedContext) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_SHARED_CONTEXT, sharedContext));
    }

    private void handleUpdateSharedContext(EGLContext newSharedContext) {
        Log.d(TAG, "handleUpdatedSharedContext " + newSharedContext);

        // Release the EGLSurface and EGLContext.
        mInputWindowSurface.releaseEglSurface();
        mEglCore.release();

        // Create a new EGLContext and recreate the window surface.
        mEglCore = new EglCore(newSharedContext, EglCore.FLAG_RECORDABLE);
        mInputWindowSurface.recreate(mEglCore);
        mInputWindowSurface.makeCurrent();
    }

    /**
     * Handles notification of an available frame.
     * <p>
     * The texture is rendered onto the encoder's input surface, along with a moving
     * box (just because we can).
     * <p>
     *
     * @param transform      The texture transform, from SurfaceTexture.
     * @param timestampNanos The frame's timestamp, from SurfaceTexture.
     */
    private void handleFrameAvailable(float[] transform, long timestampNanos) {
        mVideoEncoder.drainEncoder(false);

        if (frameListener != null) {
            frameListener.listener();
        }

        mInputWindowSurface.setPresentationTime(timestampNanos);
        mInputWindowSurface.swapBuffers();
    }

    public void stopRecording() {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_STOP_RECORDING));
        mHandler.sendMessage(mHandler.obtainMessage(MSG_QUIT));
        // We don't know when these will actually finish (or even start).  We don't want to
        // delay the UI thread though, so we return immediately.
    }

    /**
     * Tells the video recorder what texture name to use.  This is the external texture that
     * we're receiving camera previews in.  (Call from non-encoder thread.)
     * <p>
     * TODO: do something less clumsy
     */
    public void setTextureId(int id) {
        synchronized (mReadyFence) {
            if (!mReady) {
                return;
            }
        }
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TEXTURE_ID, id, 0, null));
    }

    public void frameAvailable(SurfaceTexture st) {
        synchronized (mReadyFence) {
            if (!mReady) {
                return;
            }
        }

        float[] transform = new float[16];
        st.getTransformMatrix(transform);
        long timestamp = System.nanoTime();
        Log.i(TAG, "MediaCodecThread frameAvailable ");
        mHandler.sendMessage(mHandler.obtainMessage(MSG_FRAME_AVAILABLE,
                (int) (timestamp >> 32), (int) timestamp, transform));
    }


}
