package com.effect_opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import com.editvideo.Constants;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.utils.ImageUtils;
import com.meicam.effect.sdk.NvsEffectSdkContext;
import com.meicam.effect.sdk.NvsVideoEffect;
import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsRational;

import java.io.IOException;

/**
 * Created by zhouxuecheng on
 * Create Time 2020/2/19
 * e-mail zhouxuecheng1991@163.com
 */

public class EffectManager {
    private static EffectManager EFFECTMANAGER = new EffectManager();
    private EffectRenderCore mEffectRenderCore;
    private NvsEffectSdkContext mEffectSdkContext;

    public NvsVideoEffect getmArSceneFaceEffect() {
        return mArSceneFaceEffect;
    }

    private NvsVideoEffect mArSceneFaceEffect;
    private PhotoImageProcessor mPhotoImageProcessor;
    private String historyCommonFilterId = "";
    private Handler handler = new Handler();
    private NvsVideoEffect commonEffect;

    private EffectManager() {
    }

    public static EffectManager instance() {
        return EFFECTMANAGER;
    }

    public void initEffect(Context context) {
        if (mEffectSdkContext == null)
            mEffectSdkContext = NvsEffectSdkContext.init(context, "assets:/meetvr_20200311.lic", 0);
//        if (mEffectRenderCore == null)
        mEffectRenderCore = new EffectRenderCore(mEffectSdkContext);

//        if (mPhotoImageProcessor == null)
        mPhotoImageProcessor = new PhotoImageProcessor(AiCameraApplication.getContext());

        initFilterPackage(context);
        initFace(context);
        createEffect();
    }

    /**
     * rendercore add beauty and beautyshape
     */
    private void createEffect() {
        if (mArSceneFaceEffect != null) {
            return;
        }

        NvsRational aspectRatio = new NvsRational(9, 16);
        mArSceneFaceEffect = mEffectSdkContext.createVideoEffect(Constants.AR_SCENE, aspectRatio);
        if (mArSceneFaceEffect == null)
            return;
        mEffectRenderCore.removeRenderEffect(mArSceneFaceEffect.getVideoFxPackageId());
//        mArSceneFaceEffect.setBooleanVal("Single Buffer Mode", true);
//        mArSceneFaceEffect.setBooleanVal("Beauty Effect", true);
//        mArSceneFaceEffect.setBooleanVal("Beauty Shape", true);
//        mArSceneFaceEffect.setStringVal("Scene Id", "");

//        if (mEffectRenderCore != null)
//            mEffectRenderCore.addNewRenderEffect(mArSceneFaceEffect);
    }


    private void initFilterPackage(Context context) {
        try {
            String fileNames[] = context.getAssets().list("filter");
            if (fileNames == null || fileNames.length == 0) {
                return;
            }
            for (String fileName : fileNames) {
                if (fileName.endsWith("videofx")) {
                    NvsAssetPackageManager assetPackageManager = mEffectSdkContext.getAssetPackageManager();
                    String logoTemplatePath = "assets:/filter/" + fileName;
                    StringBuilder packageId = new StringBuilder();
                    assetPackageManager.installAssetPackage(logoTemplatePath, null,
                            NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX, true, packageId);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * init face++ recognize
     *
     * @param context
     */
    private void initFace(Context context) {
        String model = "assets:/facemodel_st/tt_face_v2.0_20200227.model";

        boolean suc = NvsEffectSdkContext.initHumanDetection(context, model,
                "assets:/facemodel_st/meishe_mocam_face_20200227.lic",
                NvsEffectSdkContext.HUMAN_DETECTION_FEATURE_FACE_LANDMARK |
                        NvsEffectSdkContext.HUMAN_DETECTION_FEATURE_FACE_ACTION |
                        NvsEffectSdkContext.HUMAN_DETECTION_FEATURE_VIDEO_MODE |
                        NvsEffectSdkContext.HUMAN_DETECTION_FEATURE_IMAGE_MODE);
    }

    /**
     * add common filter
     */
    public void addCommonFilter(String commonFilterId) {
        if (mEffectRenderCore != null)
            mEffectRenderCore.removeRenderEffect(historyCommonFilterId);

        historyCommonFilterId = commonFilterId;
        NvsRational aspectRatio = new NvsRational(1, 1);
        commonEffect = mEffectSdkContext.createVideoEffect(commonFilterId, aspectRatio);
        if (mEffectRenderCore != null) {
            mEffectRenderCore.addNewRenderEffect(commonEffect);
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (commonEffect != null)
                    commonEffect.setFilterIntensity(1.0f);
            }
        }, 100);
    }

    /**
     * increase common filter Intensity
     *
     * @param value
     */
    public void commonFilterIntensity(float value) {
        if (commonEffect != null)
            commonEffect.setFilterIntensity(value);
    }

    /**
     * add beauty
     */
    public void addBeautyArScene() {
        if (mEffectRenderCore != null && mArSceneFaceEffect != null) {

            mArSceneFaceEffect.setBooleanVal("Single Buffer Mode", true);
            mArSceneFaceEffect.setStringVal("Scene Id", "");
            mArSceneFaceEffect.setBooleanVal("Beauty Effect", true);
            mArSceneFaceEffect.setBooleanVal("Beauty Shape", true);

            mEffectRenderCore.removeRenderEffect(mArSceneFaceEffect.getBuiltinVideoFxName());
            mEffectRenderCore.addNewRenderEffect(mArSceneFaceEffect);
        }
    }

    /**
     * set beauty Intensity
     *
     * @param name
     * @param value
     */
    public void addBeautyIntensity(String name, float value) {
        if (mArSceneFaceEffect == null)
            return;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mArSceneFaceEffect != null) {
                    mArSceneFaceEffect.setFloatVal(name, value);
                    Log.i("ARC_LOG", "run: 当前设置 " + name);
                }
            }
        }, 100);
    }


    /**
     * beauty shape param
     * =======
     * /**
     * beauty intensity
     *
     * @param value 美颜的数值
     */
    public void beautyIntensity(float value) {
        if (mArSceneFaceEffect == null)
            return;
        mArSceneFaceEffect.setFilterIntensity(value);
    }


    /**
     * beautyshape param
     *
     * @param name
     * @param value
     */
    public void beautyShape(String name, float value) {
        if (mArSceneFaceEffect == null)
            return;
        mArSceneFaceEffect.setFloatVal(name, value);
    }


    /**
     * destory rendercore and arscene
     * =======
     * /**
     * destory
     */
    public void destory() {
        if (mArSceneFaceEffect != null) {
            mArSceneFaceEffect.release();
        }
        mArSceneFaceEffect = null;
        mEffectSdkContext = null;
        NvsEffectSdkContext.closeHumanDetection();
        NvsEffectSdkContext.close();
    }

    /**
     * 美颜拍照
     *
     * @param path
     * @param beautyListener
     */
    public void takePhoto(String path, PhotoImageProcessor.BeautyListener beautyListener) {
        final Bitmap bitmap = BitmapFactory.decodeFile(path);
        final byte[] yuvByBitmap = ImageUtils.getYUVByBitmap(bitmap);
        if (bitmap == null) {
            return;
        }
        mPhotoImageProcessor.processEffect(path, yuvByBitmap, bitmap.getWidth(), bitmap.getHeight(),
                System.currentTimeMillis(),
                0,
                0,
                false,
                0,
                mEffectRenderCore.getRenderArray(), beautyListener);
        mEffectRenderCore.clearCacheResources();
    }

    public EffectRenderCore getmRenderCore() {
        return mEffectRenderCore;
    }

    public void clearFilter() {
        if (mEffectSdkContext == null || mEffectRenderCore == null) {
            return;
        }


        mEffectRenderCore.removeRenderEffect(historyCommonFilterId);
        NvsRational aspectRatio = new NvsRational(1, 1);
        commonEffect = mEffectSdkContext.createVideoEffect("", aspectRatio);
        if (mEffectRenderCore != null) {
            mEffectRenderCore.addNewRenderEffect(commonEffect);
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (commonEffect != null)
                    commonEffect.setFilterIntensity(1.0f);
            }
        }, 100);
    }
}
