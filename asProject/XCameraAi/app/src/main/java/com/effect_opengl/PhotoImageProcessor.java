package com.effect_opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.opengl.GLES20;
import android.util.Log;

import com.meicam.effect.sdk.NvsEffectSdkContext;
import com.meicam.sdk.NvsRational;
import com.meicam.sdk.NvsVideoFrameInfo;
import com.meicam.sdk.NvsVideoResolution;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import static android.opengl.GLES20.glGenTextures;
import static com.meicam.effect.sdk.NvsEffectRenderCore.NV_EFFECT_CORE_FLAGS_SUPPORT_8K;

/**
 * Created by meicam-dx on 2018/3/19.
 */

public class PhotoImageProcessor {
    private static final String TAG = "PhotoImageProcessor";
    private EffectRenderCore mRenderCore;
    private Context mActivity;
    private int mOutRenderTex = -1;
    private int mTextureID = -1;
    private int[] mFrameBuffers = new int[1];

    public PhotoImageProcessor(Context activity) {
        mActivity = activity;
        NvsEffectSdkContext effectSdkContext = NvsEffectSdkContext.getInstance();
        mRenderCore = new EffectRenderCore(effectSdkContext);
        //mRenderCore = renderCore;
    }

    /**
     * 创建Texture
     *
     * @param width
     * @param height
     * @return
     */
    private int createGLTexture(int width, int height) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        int[] tex = new int[1];
        glGenTextures(1, tex, 0);
        EGLHelper.checkGlError("Texture generate");

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex[0]);
        if (mFrameBuffers == null) {
            mFrameBuffers = new int[1];
            GLES20.glGenFramebuffers(1, mFrameBuffers, 0);
        }
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        return tex[0];
    }

    public void processEffect(String path, byte[] data, int width, int height, long timestamp,
                              int cameraOrientation, int oritation, boolean flipHorizontal, int deviceOrientation,
                              ArrayList<EffectRenderCore.EffectRenderItem> renderList, BeautyListener beautyListener) {
        mRenderCore.mEffectRenderCore.initialize(NV_EFFECT_CORE_FLAGS_SUPPORT_8K);
        mRenderCore.setRenderArray(renderList);

        if (oritation == 90 || oritation == 270) {
            int tmp = width;
            width = height;
            height = tmp;
        }
        mTextureID = createGLTexture(width, height);

        NvsVideoFrameInfo info = new NvsVideoFrameInfo();
        info.frameWidth = width;
        info.frameHeight = height;
        info.pixelFormat = NvsVideoFrameInfo.VIDEO_FRAME_PIXEL_FROMAT_NV21;
        info.displayRotation = oritation;
        info.flipHorizontally = flipHorizontal;
        info.frameTimestamp = timestamp;
        int[] fboBuffer = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_FRAMEBUFFER_BINDING, fboBuffer, 0);
        mRenderCore.mEffectRenderCore.uploadVideoFrameToTexture(data, info, mTextureID);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboBuffer[0]);

        mOutRenderTex = mRenderCore.renderVideoEffect(mTextureID, data, false, width, height, timestamp,
                cameraOrientation, oritation, flipHorizontal, deviceOrientation, true);

        NvsVideoResolution resolution = new NvsVideoResolution();
        resolution.imageWidth = width;
        resolution.imageHeight = height;
        resolution.imagePAR = new NvsRational(1, 1);
        fboBuffer = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_FRAMEBUFFER_BINDING, fboBuffer, 0);
        ByteBuffer renderData = mRenderCore.mEffectRenderCore.downloadFromTexture(mOutRenderTex, resolution, NvsVideoFrameInfo.VIDEO_FRAME_PIXEL_FROMAT_RGBA, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboBuffer[0]);
        if (renderData == null) {
            Log.i("DOWN_CAMERS_FILE_LOG", "美颜数据为  null  !");
            return;
        }
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmp.copyPixelsFromBuffer(renderData);

        if (cameraOrientation == 90 || cameraOrientation == 270) {
            Matrix matrix = new Matrix();
            matrix.setRotate(flipHorizontal ? -cameraOrientation : cameraOrientation);
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
        }

        File file = new File(path);
        if (file.exists())
            file.delete();

        saveJPGFile(bmp, file.getAbsolutePath(), beautyListener);
    }

    /**
     * 保存图片
     *
     * @param bitmap
     * @param name
     * @param beautyListener
     */
    public void saveJPGFile(Bitmap bitmap, String name, BeautyListener beautyListener) {
        File file = new File(name);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
                bitmap.recycle();
            }
            if (beautyListener != null) {
                beautyListener.beautyOk();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void destroyFrameBuffers() {
        if (mFrameBuffers != null) {
            GLES20.glDeleteFramebuffers(1, mFrameBuffers, 0);
            mFrameBuffers = null;
        }
        if (mTextureID != -1) {
            int[] tex = new int[1];
            tex[0] = mTextureID;
            GLES20.glDeleteTextures(1, tex, 0);
        }
    }

    public interface BeautyListener {
        void beautyOk();
    }

    public void destroy() {
        destroyFrameBuffers();
    }
}