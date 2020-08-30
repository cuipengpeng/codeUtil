package com.effect_opengl;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;

import com.meicam.effect.sdk.NvsEffect;
import com.meicam.effect.sdk.NvsEffectRenderCore;
import com.meicam.effect.sdk.NvsEffectSdkContext;
import com.meicam.effect.sdk.NvsVideoEffect;
import com.meicam.sdk.NvsARSceneManipulate;
import com.meicam.sdk.NvsRational;
import com.meicam.sdk.NvsVideoFrameInfo;
import com.meicam.sdk.NvsVideoResolution;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import static android.opengl.GLES20.glGenTextures;
import static com.meicam.effect.sdk.NvsEffectRenderCore.NV_EFFECT_CORE_FLAGS_SUPPORT_8K;

/**
 * Created by meicam-dx on 2017/10/26.
 */

public class EffectRenderCore {

    private static final String TAG = "EffectRenderCore";

    private final static String POSITION_COORDINATE = "position";
    private final static String TEXTURE_UNIFORM = "inputImageTexture";
    private final static String TEXTURE_COORDINATE = "inputTextureCoordinate";

    //背景颜色为 #101010
    private final static float CLEAR_COLOR = 16 / 256f;

    private boolean mEffectRenderInit = false;

    private int[] mFrameBuffers = null;
    private int m_convertProgramID = -1;
    private FloatBuffer mTextureBuffer;
    private FloatBuffer mGLCubeBuffer;


    private ArrayList<EffectRenderItem> mRenderArray;
    private ArrayList<EffectRenderItem> mRemoveArray;
    private Object mArraySyncObject = new Object();

    private int mRenderWidth = -1;
    private int mRenderHeight = -1;
    public NvsEffectRenderCore mEffectRenderCore;
    boolean mEffectRenderInitPicture = false;
    private int m_glRenderTexturePicture = 0;
    private int m_glRenderTexturePicture1 = 0;

    private int m_glRenderTexture = 0;
    private int m_glRenderTexture1 = 0;
    private int mPreProcessTextures = -1;

    private int mRenderTextureWidth = -1;
    private int mRenderTextureHeigth = -1;
    private int mRenderTextureWidth1 = -1;
    private int mRenderTextureHeigth1 = -1;
    private int mPreProcessTexturesWidth = -1;
    private int mPreProcessTexturesHeigth = -1;

    private NvsVideoResolution mCurrentVideoResolution;

    //preview data
    private byte[] mPreviewImageData;
    private Object mPreviewImageDataLock = new Object();

    private int m_drawTextureProgramID = -1;
    private FloatBuffer mDrawTextureBuffer;
    private FloatBuffer mDrawGLCubeBuffer;

    public void takePhotoClose() {
        mEffectRenderInitPicture = false;
        m_glRenderTexturePicture = 0;
        m_glRenderTexturePicture1 = 0;
    }


    public static class EffectRenderItem {
        NvsEffect effect;
        long startTimeStamp;
    }

    public EffectRenderCore(NvsEffectSdkContext context) {
        mEffectRenderCore = context.createEffectRenderCore();

        mCurrentVideoResolution = new NvsVideoResolution();
        mCurrentVideoResolution.imagePAR = new NvsRational(1, 1);

        mRenderArray = new ArrayList<>();
        mRemoveArray = new ArrayList<>();
    }

    public void addNewRenderEffect(NvsEffect effect) {
        if (effect == null)
            return;

        EffectRenderItem item = new EffectRenderItem();
        item.effect = effect;
        item.startTimeStamp = -1;
        synchronized (mArraySyncObject) {
            mRenderArray.add(item);
        }
    }

    public void removeRenderEffect(String effectId) {
        synchronized (mArraySyncObject) {
            for (EffectRenderItem ef : mRenderArray) {
                NvsVideoEffect videoef = (NvsVideoEffect) ef.effect;
                String strPackageId = videoef.getVideoFxPackageId();
                String strBuildInName = videoef.getBuiltinVideoFxName();
                if (strPackageId.equalsIgnoreCase(effectId) || strBuildInName.equalsIgnoreCase(effectId)) {
                    mRemoveArray.add(ef);
                    mRenderArray.remove(ef);
                    break;
                }
            }
        }
    }

    private byte[] mPreviewRGBAImageData = null;

    public void sendRGBABuffer(byte[] data, int width, int height) {
        mDataType = NvsVideoFrameInfo.VIDEO_FRAME_PIXEL_FROMAT_RGBA;
        if (mPreviewRGBAImageData == null || mPreviewRGBAImageData.length != data.length) {
            mPreviewRGBAImageData = new byte[data.length];
        }
        synchronized (mPreviewImageDataLock) {
            System.arraycopy(data, 0, mPreviewRGBAImageData, 0, data.length);
        }
    }


    private int mDataType = NvsVideoFrameInfo.VIDEO_FRAME_PIXEL_FROMAT_NV21;

    /**
     * 渲染所有添加的滤镜，必须在opengl环境中运行
     */
    public int renderVideoEffect(int inputTex, byte[] picData, boolean isOESTexture, int width, int height, long currentTimeStamp,
                                 int cameraOrientation, int displayOrientation, boolean flipHorizontal, int deviceOrientation,
                                 boolean useImageMode) {
        int dataType = NvsVideoFrameInfo.VIDEO_FRAME_PIXEL_FROMAT_NV21;//useImageMode ? NvsVideoFrameInfo.VIDEO_FRAME_PIXEL_FROMAT_RGBA : ;
        //清除要删除特效中的GL资源，然后删除不使用的特效。
        synchronized (mArraySyncObject) {
            if (mEffectRenderCore != null) {
                if (mRemoveArray != null) {
                    for (EffectRenderItem effect : mRemoveArray) {
                        mEffectRenderCore.clearEffectResources(effect.effect);
                    }
                    mRemoveArray.clear();
                }
            }
        }

        if (mEffectRenderCore == null) {
            return inputTex;
        }

        //得到当前需要渲染的特效列表
        ArrayList<EffectRenderItem> effects = new ArrayList<>();
        synchronized (mArraySyncObject) {
            if (!mRenderArray.isEmpty()) {
                effects.addAll(mRenderArray);
            }
        }

        if (!mEffectRenderInit) {
            mEffectRenderInit = mEffectRenderCore.initialize(NV_EFFECT_CORE_FLAGS_SUPPORT_8K);
        }
        //处理输入的纹理，如果纹理是OES的纹理转换为标准的GL_TEXTURE_2D格式
        int inRenderTex = inputTex;
        if (isOESTexture) {
            inRenderTex = preProcess(inputTex, width, height, cameraOrientation, flipHorizontal);
        }
        EGLHelper.checkGlError("preProcess");

        //创建输出的纹理
        //创建输出的纹理
        /**
         * preview scale kernel
         *
         * width height change should reset createTexture
         */
        if (m_glRenderTexture <= 0) {
            m_glRenderTexture = createGLTexture(width, height);
            mRenderTextureWidth = width;
            mRenderTextureHeigth = height;
        } else {
            if (mRenderTextureHeigth != height || mRenderTextureWidth != width) {
                destoryGLTexture(m_glRenderTexture);
                m_glRenderTexture = createGLTexture(width, height);
                mRenderTextureWidth = width;
                mRenderTextureHeigth = height;
            }
        }
        EGLHelper.checkGlError("createGLTexture");

        int outRenderTex = m_glRenderTexture;

        if (m_glRenderTexture1 <= 0) {
            m_glRenderTexture1 = createGLTexture(width, height);
            mRenderTextureWidth1 = width;
            mRenderTextureHeigth1 = height;
        } else {
            if (mRenderTextureWidth1 != width || mRenderTextureHeigth1 != height) {
                destoryGLTexture(m_glRenderTexture1);
                m_glRenderTexture1 = createGLTexture(width, height);
                mRenderTextureWidth1 = width;
                mRenderTextureHeigth1 = height;
            }
        }

        //获取当前绑定的FrameBuffer，因为在EffectSDKCore中会有创建一个新的FrameBuffer，所有要保留当前的FrameBuffer
        int[] fboBuffer = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_FRAMEBUFFER_BINDING, fboBuffer, 0);
        EGLHelper.checkGlError("glGetIntegerv");

        int nRenderedCount = 0;
        for (EffectRenderItem effect : effects) {
            boolean bRet = true;
            NvsVideoEffect videoEffect = (NvsVideoEffect) effect.effect;
            String strBuildInName = videoEffect.getBuiltinVideoFxName();
            if (strBuildInName.equalsIgnoreCase("AR Scene")) {
                mCurrentVideoResolution.imageWidth = width;
                mCurrentVideoResolution.imageHeight = height;

                if (effect.startTimeStamp < 0) {
                    effect.startTimeStamp = currentTimeStamp;
                }

                long currentRenderTime = currentTimeStamp - effect.startTimeStamp;
                NvsVideoFrameInfo info = new NvsVideoFrameInfo();
                info.displayRotation = flipHorizontal ? 360 - cameraOrientation : cameraOrientation;
                info.flipHorizontally = flipHorizontal;
                info.isRec601 = true;
                info.isFullRangeYuv = true;
                info.pixelFormat = dataType;
                info.frameWidth = width;
                info.frameHeight = height;
                if (cameraOrientation == 90 || cameraOrientation == 270) {
                    info.frameWidth = height;
                    info.frameHeight = width;
                }
                int physicalOrientation = calcPreviewBufferPhysicalOrientation(cameraOrientation, flipHorizontal, deviceOrientation);
                if (useImageMode) {
                    NvsARSceneManipulate man = effect.effect.getARSceneManipulate();
                    man.setDetectionMode(NvsEffectSdkContext.HUMAN_DETECTION_FEATURE_IMAGE_MODE);
                    info.displayRotation = displayOrientation;
                    info.frameWidth = width;
                    info.frameHeight = height;
                }

                if (useImageMode)
                    mEffectRenderCore.renderEffect(effect.effect, inRenderTex, picData,
                            info, cameraOrientation, mCurrentVideoResolution,
                            outRenderTex, (currentRenderTime) * 1000,
                            0);
                else
                    mEffectRenderCore.renderEffect(effect.effect, inRenderTex, null,
                            info, cameraOrientation, mCurrentVideoResolution,
                            outRenderTex, (currentRenderTime) * 1000,
                            0);

                if (useImageMode) {
                    NvsARSceneManipulate man = effect.effect.getARSceneManipulate();
                    man.setDetectionMode(NvsEffectSdkContext.HUMAN_DETECTION_FEATURE_VIDEO_MODE);
                }
            } else {
                bRet = processSingleFilter(effect.effect, inRenderTex, width, height, outRenderTex, currentTimeStamp * 1000);
            }
            nRenderedCount++;

            if (!bRet) {
                continue;
            }

            if (nRenderedCount == effects.size()) {
                break;
            }

            inRenderTex = outRenderTex;
            if (outRenderTex == m_glRenderTexture) {
                outRenderTex = m_glRenderTexture1;
            } else {
                outRenderTex = m_glRenderTexture;
            }
        }

        EGLHelper.checkGlError("ProcessSingleFilter");

        //恢复之前保存的FrameBufer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboBuffer[0]);

        if (effects.size() == 0) {
            outRenderTex = inRenderTex;
        }
        return outRenderTex;
    }

    /**
     * 一般滤镜处理
     *
     * @param effect
     * @param inputTex
     * @param width
     * @param height
     * @param outputTex
     * @param timeStamp
     * @return
     */
    private boolean processSingleFilter(NvsEffect effect, int inputTex, int width, int height, int outputTex, long timeStamp) {
        if (mEffectRenderCore == null) {
            return false;
        }
        mCurrentVideoResolution.imageWidth = width;
        mCurrentVideoResolution.imageHeight = height;
        int nRet = mEffectRenderCore.renderEffect(effect, inputTex, mCurrentVideoResolution, outputTex, timeStamp, 0);
        return nRet == NvsEffectRenderCore.NV_EFFECT_CORE_NO_ERROR;
    }

    /**
     * 渲染所有添加的滤镜，必须在opengl环境中运行
     */
//    public int renderVideoEffect(int inputTex, boolean isOESTexture, int width, int height, long currentTimeStamp,
//                                 int cameraOrientation, int displayOrientation, boolean flipHorizontal, int deviceOrientation,
//                                 boolean useImageMode) {
//
//        int dataType = mDataType;
//
//        //清除要删除特效中的GL资源，然后删除不使用的特效。
//        synchronized (mArraySyncObject) {
//            if (mEffectRenderCore != null) {
//                if (mRemoveArray != null) {
//                    for (EffectRenderItem effect : mRemoveArray) {
//                        mEffectRenderCore.clearEffectResources(effect.effect);
//                    }
//                    mRemoveArray.clear();
//                }
//            }
//        }
//
//        if (mEffectRenderCore == null)
//            return inputTex;
//
//        //得到当前需要渲染的特效列表
//        ArrayList<EffectRenderItem> effects = new ArrayList<>();
//        synchronized (mArraySyncObject) {
//            if (mRenderArray.size() < 1)
//                return inputTex;
//
//            effects.addAll(mRenderArray);
//        }
//
//        if (!mEffectRenderInit) {
//            mEffectRenderInit = mEffectRenderCore.initialize();
//        }
//        //处理输入的纹理，如果纹理是OES的纹理转换为标准的GL_TEXTURE_2D格式
//        int inRenderTex = inputTex;
//        if (isOESTexture) {
//            inRenderTex = preProcess(inputTex, width, height, cameraOrientation, flipHorizontal);
//        }
//        EGLHelper.checkGlError("preProcess");
//
//        //创建输出的纹理
//        /**
//         * preview scale kernel
//         *
//         * width height change should reset createTexture
//         */
//        if (m_glRenderTexture <= 0) {
//            m_glRenderTexture = createGLTexture(width, height);
//            mRenderTextureWidth = width;
//            mRenderTextureHeigth = height;
//        } else {
//            if (mRenderTextureHeigth != height || mRenderTextureWidth != width) {
//                destoryGLTexture(m_glRenderTexture);
//                m_glRenderTexture = createGLTexture(width, height);
//                mRenderTextureWidth = width;
//                mRenderTextureHeigth = height;
//            }
//        }
//        EGLHelper.checkGlError("createGLTexture");
//
//        int outRenderTex = m_glRenderTexture;
//        int nEffectCount = effects.size();
//
//        if (nEffectCount > 1) {
//            if (m_glRenderTexture1 <= 0) {
//                m_glRenderTexture1 = createGLTexture(width, height);
//                mRenderTextureWidth1 = width;
//                mRenderTextureHeigth1 = height;
//            } else {
//                if (mRenderTextureWidth1 != width || mRenderTextureHeigth1 != height) {
//                    destoryGLTexture(m_glRenderTexture1);
//                    m_glRenderTexture1 = createGLTexture(width, height);
//                    mRenderTextureWidth1 = width;
//                    mRenderTextureHeigth1 = height;
//                }
//            }
//        }
//
//        //获取当前绑定的FrameBuffer，因为在EffectSDKCore中会有创建一个新的FrameBuffer，所有要保留当前的FrameBuffer
//        int[] fboBuffer = new int[1];
//        GLES20.glGetIntegerv(GLES20.GL_FRAMEBUFFER_BINDING, fboBuffer, 0);
//        EGLHelper.checkGlError("glGetIntegerv");
//
//        int nRenderedCount = 0;
//        for (EffectRenderItem effect : effects) {
//            boolean bRet = true;
//            NvsVideoEffect videoEffect = (NvsVideoEffect) effect.effect;
//            String strBuildInName = videoEffect.getBuiltinVideoFxName();
//            if (strBuildInName.equalsIgnoreCase("AR Scene")) {
//                mCurrentVideoResolution.imageWidth = width;
//                mCurrentVideoResolution.imageHeight = height;
//
//                if (effect.startTimeStamp < 0)
//                    effect.startTimeStamp = currentTimeStamp;
//                long currentRenderTime = currentTimeStamp - effect.startTimeStamp;
//
//                int physicalOrientation = calcPreviewBufferPhysicalOrientation(cameraOrientation, flipHorizontal, deviceOrientation);
//
//                if (useImageMode) {
//                    NvsARSceneManipulate man = effect.effect.getARSceneManipulate();
//                    man.setDetectionMode(NvsEffectSdkContext.HUMAN_DETECTION_FEATURE_IMAGE_MODE);
//                }
//
//                if (useImageMode) {
//                    int nRet = mEffectRenderCore.renderEffect(effect.effect, inRenderTex, null, null, 0,
//                            mCurrentVideoResolution, outRenderTex, (currentRenderTime) * 1000, 0);
//
//                    mDataType = NvsVideoFrameInfo.VIDEO_FRAME_PIXEL_FROMAT_NV21;
//                } else {
//                    int nRet = mEffectRenderCore.renderEffect(effect.effect, inRenderTex, null,
//                            null, physicalOrientation, mCurrentVideoResolution, outRenderTex, (currentRenderTime) * 1000, 0);
//                }
//
//                if (useImageMode) {
//                    NvsARSceneManipulate man = effect.effect.getARSceneManipulate();
//                    man.setDetectionMode(NvsEffectSdkContext.HUMAN_DETECTION_FEATURE_VIDEO_MODE);
//                }
//
//            } else {
//                bRet = ProcessSingleFilter(effect.effect, inRenderTex, width, height, outRenderTex, currentTimeStamp * 1000);
//            }
//            nRenderedCount++;
//            if (!bRet)
//                continue;
//
//            if (nRenderedCount == effects.size())
//                break;
//
////            inRenderTex = outRenderTex; //TODO 这个地方就是下载照片,不能美颜的问题
////            if (outRenderTex == m_glRenderTexture)
////                outRenderTex = m_glRenderTexture1;
////            else {
////                outRenderTex = m_glRenderTexture;
////            }
//        }
//
//        EGLHelper.checkGlError("ProcessSingleFilter");
//
//        //恢复之前保存的FrameBufer
//        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboBuffer[0]);
//
//        if (effects.size() == 0)
//            outRenderTex = inRenderTex;
//
//        return outRenderTex;
//    }
    private int calcPreviewBufferPhysicalOrientation(int cameraOrientation, boolean mirror, int deviceOrientation) {
        if (deviceOrientation <= 45 || deviceOrientation >= 315)
            deviceOrientation = 0;
        else if (deviceOrientation < 135)
            deviceOrientation = 90;
        else if (deviceOrientation <= 225)
            deviceOrientation = 180;
        else
            deviceOrientation = 270;

        int orientationAngle = 0;
        final boolean frontCamera = mirror;
        if (!frontCamera)
            orientationAngle = (cameraOrientation + deviceOrientation) % 360;
        else
            orientationAngle = (cameraOrientation - deviceOrientation + 360) % 360;

        return orientationAngle;
    }

    /**
     * 输入结果到GLSurfaceView，必须在opengl环境中运行
     */
    public void drawTexture(int displayTex,
                            int texWidth, int texHeight, int displayWidth, int displayHeight) {
        // Draw texture

        if (m_drawTextureProgramID < 0) {
            m_drawTextureProgramID = EGLHelper.loadProgramForTexture();

            mDrawGLCubeBuffer = ByteBuffer.allocateDirect(EGLHelper.CUBE.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            mDrawGLCubeBuffer.put(EGLHelper.CUBE).position(0);

            mDrawTextureBuffer = ByteBuffer.allocateDirect(EGLHelper.TEXTURE_NO_ROTATION.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            mDrawTextureBuffer.clear();
            mDrawTextureBuffer.put(EGLHelper.TEXTURE_NO_ROTATION).position(0);
        }

        GLES20.glClearColor(CLEAR_COLOR, CLEAR_COLOR, CLEAR_COLOR, 1f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearDepthf(1f);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);

        float ar = (float) texWidth / texHeight;
        float disar = (float) displayWidth / displayHeight;
        float cropWidth = 1f;
        float cropHeight = 1f;
        if (disar > ar) {
            cropHeight = 1.0f;
            cropWidth = (float) texWidth / displayWidth;
        } else {
            cropWidth = 1.0f;
            cropHeight = (float) texHeight / displayHeight;
        }

//        Log.i("AR_TESTTT", "drawTexture: ar " + ar + "  dissar " + disar);
        mDrawGLCubeBuffer.put(0, -cropWidth);
        mDrawGLCubeBuffer.put(1, cropHeight);
        mDrawGLCubeBuffer.put(2, cropWidth);
        mDrawGLCubeBuffer.put(3, cropHeight);
        mDrawGLCubeBuffer.put(4, -cropWidth);
        mDrawGLCubeBuffer.put(5, -cropHeight);
        mDrawGLCubeBuffer.put(6, cropWidth);
        mDrawGLCubeBuffer.put(7, -cropHeight);
        mDrawGLCubeBuffer.position(0);

        GLES20.glUseProgram(m_drawTextureProgramID);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, displayTex);

        mDrawGLCubeBuffer.position(0);
        int glAttribPosition = GLES20.glGetAttribLocation(m_drawTextureProgramID, POSITION_COORDINATE);
        GLES20.glVertexAttribPointer(glAttribPosition, 2, GLES20.GL_FLOAT, false, 0, mDrawGLCubeBuffer);
        GLES20.glEnableVertexAttribArray(glAttribPosition);

        mDrawTextureBuffer.position(0);
        int glAttribTextureCoordinate = GLES20.glGetAttribLocation(m_drawTextureProgramID, TEXTURE_COORDINATE);
        GLES20.glVertexAttribPointer(glAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0,
                mDrawTextureBuffer);
        GLES20.glEnableVertexAttribArray(glAttribTextureCoordinate);

        int textUniform = GLES20.glGetUniformLocation(m_drawTextureProgramID, TEXTURE_UNIFORM);
        GLES20.glUniform1i(textUniform, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);


        GLES20.glDisableVertexAttribArray(glAttribPosition);
        GLES20.glDisableVertexAttribArray(glAttribTextureCoordinate);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glUseProgram(0);
    }

    /**
     * 释放opengl资源，必须在opengl环境中运行
     */
    public void destoryGLResource() {

        destoryGLTexture(m_glRenderTexture);
        m_glRenderTexture = 0;
        destoryGLTexture(m_glRenderTexture1);
        m_glRenderTexture1 = 0;
        destoryGLTexture(mPreProcessTextures);
        mPreProcessTextures = 0;

        if (mFrameBuffers != null) {
            GLES20.glDeleteFramebuffers(1, mFrameBuffers, 0);
            mFrameBuffers = null;
        }

        synchronized (mArraySyncObject) {
            if (mEffectRenderCore != null) {
                if (mRemoveArray != null) {
                    for (EffectRenderItem effect : mRemoveArray) {
                        mEffectRenderCore.clearEffectResources(effect.effect);
                    }
                    mRemoveArray.clear();
                }

                if (mRenderArray != null) {
                    for (EffectRenderItem effect : mRenderArray) {
                        mEffectRenderCore.clearEffectResources(effect.effect);
                    }
                    mRemoveArray.clear();
                }
            }
        }

        mEffectRenderCore.clearCacheResources();
        mEffectRenderCore.cleanUp();

        if (m_drawTextureProgramID > 0) {
            GLES20.glDeleteProgram(m_drawTextureProgramID);
        }
        m_drawTextureProgramID = -1;

        if (m_convertProgramID > 0) {
            GLES20.glDeleteProgram(m_convertProgramID);
        }
        m_convertProgramID = -1;
    }


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

        EGLHelper.bindFrameBuffer(tex[0], mFrameBuffers[0], width, height);

        return tex[0];
    }

    /**
     * 1. 将OES的纹理转换为标准的GL_TEXTURE_2D格式
     * 2. 将纹理宽高对换，即将wxh的纹理转换为了hxw的纹理，并且如果是前置摄像头，则需要有水平的翻转
     *
     * @param textureId 输入的OES的纹理id
     * @return 转换后的GL_TEXTURE_2D的纹理id
     */
    private int preProcess(int textureId, int width, int height, int cameraOrientation, boolean flipHorizontal) {
        if (m_convertProgramID <= 0) {
            m_convertProgramID = EGLHelper.loadProgramForSurfaceTexture();

            mGLCubeBuffer = ByteBuffer.allocateDirect(EGLHelper.CUBE.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            mGLCubeBuffer.put(EGLHelper.CUBE).position(0);

            mTextureBuffer = ByteBuffer.allocateDirect(EGLHelper.TEXTURE_NO_ROTATION.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            mTextureBuffer.clear();
            mTextureBuffer.put(EGLHelper.TEXTURE_NO_ROTATION).position(0);
        }


        if (m_convertProgramID < 0)
            return -1;

        float[] textureCords = EGLHelper.getRotation(cameraOrientation, flipHorizontal, false);
        mTextureBuffer.clear();
        mTextureBuffer.put(textureCords).position(0);

        EGLHelper.checkGlError("preProcess");

        GLES20.glUseProgram(m_convertProgramID);
        EGLHelper.checkGlError("glUseProgram");

//        if (mPreProcessTextures <= 0) {
//            mPreProcessTextures = createGLTexture(width, height);
//        }

        if (mPreProcessTextures <= 0) {
            mPreProcessTextures = createGLTexture(width, height);
            mPreProcessTexturesWidth = width;
            mPreProcessTexturesHeigth = height;
        } else {
            if (mPreProcessTexturesWidth != width || mPreProcessTexturesHeigth != height) {
                destoryGLTexture(mPreProcessTextures);
                mPreProcessTextures = createGLTexture(width, height);
                mPreProcessTexturesWidth = width;
                mPreProcessTexturesHeigth = height;
            }
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mPreProcessTextures);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, mPreProcessTextures, 0);

        mGLCubeBuffer.position(0);
        int glAttribPosition = GLES20.glGetAttribLocation(m_convertProgramID, POSITION_COORDINATE);
        GLES20.glVertexAttribPointer(glAttribPosition, 2, GLES20.GL_FLOAT, false, 0, mGLCubeBuffer);
        GLES20.glEnableVertexAttribArray(glAttribPosition);
        EGLHelper.checkGlError("glEnableVertexAttribArray");

        mTextureBuffer.clear();
        int glAttribTextureCoordinate = GLES20.glGetAttribLocation(m_convertProgramID, TEXTURE_COORDINATE);
        GLES20.glVertexAttribPointer(glAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
        GLES20.glEnableVertexAttribArray(glAttribTextureCoordinate);
        EGLHelper.checkGlError("glEnableVertexAttribArray");

        if (textureId != -1) {
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
            int textUniform = GLES20.glGetUniformLocation(m_convertProgramID, TEXTURE_UNIFORM);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glUniform1i(textUniform, 0);
            EGLHelper.checkGlError("glBindTexture");
        }

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        EGLHelper.checkGlError("GL_TEXTURE0");
        GLES20.glViewport(0, 0, width, height);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        EGLHelper.checkGlError("GL_TRIANGLE_STRIP");

        GLES20.glDisableVertexAttribArray(glAttribPosition);
        GLES20.glDisableVertexAttribArray(glAttribTextureCoordinate);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        EGLHelper.checkGlError("GL_TEXTURE_2D");

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        EGLHelper.checkGlError("GL_FRAMEBUFFER");
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        EGLHelper.checkGlError("GL_TEXTURE_EXTERNAL_OES");
        GLES20.glUseProgram(0);

        return mPreProcessTextures;
    }

    //一般滤镜处理
    private boolean ProcessSingleFilter(NvsEffect effect, int inputTex, int width, int height, int outputTex, long timeStamp) {
        if (mEffectRenderCore == null)
            return false;

        mCurrentVideoResolution.imageWidth = width;
        mCurrentVideoResolution.imageHeight = height;
        int nRet = mEffectRenderCore.renderEffect(effect, inputTex, mCurrentVideoResolution, outputTex, timeStamp, 0);
        return nRet == NvsEffectRenderCore.NV_EFFECT_CORE_NO_ERROR;
    }

    private int resetTexture(int textureId, int width, int height) {
        if (textureId > 0) {
            if (mRenderWidth != width || mRenderHeight != height) {
                destoryGLTexture(textureId);
                textureId = createGLTexture(width, height);
                mRenderWidth = width;
                mRenderHeight = height;
            }
        } else {
            textureId = createGLTexture(width, height);
            mRenderWidth = width;
            mRenderHeight = height;
        }

        return textureId;
    }

    public void clearCacheResources() {
        if (null != mEffectRenderCore) {
            mEffectRenderCore.clearCacheResources();
        }
    }

    public void setRenderArray(ArrayList<EffectRenderItem> renderArray) {
        mRenderArray = renderArray;
    }

    /**
     * 获取所添加的特效
     *
     * @return
     */
    public ArrayList<EffectRenderCore.EffectRenderItem> getRenderArray() {
        return mRenderArray;
    }

    private void destoryGLTexture(int texId) {
        if (texId <= 0)
            return;
        int[] tex = new int[1];
        tex[0] = texId;
        GLES20.glDeleteTextures(1, tex, 0);
    }
}
