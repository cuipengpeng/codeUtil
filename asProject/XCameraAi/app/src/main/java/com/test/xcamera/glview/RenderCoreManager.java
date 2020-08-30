//package com.meetvr.aicamera.glview;
//
//import android.content.Context;
//import android.os.Handler;
//import android.util.Log;
//
//import com.editvideo.Constants;
//import com.effect_opengl.EffectRenderCore;
//import com.meetvr.aicamera.ai_opengl.PhotoImageProcessor;
//import com.meetvr.aicamera.application.AiCameraApplication;
//import com.meicam.effect.sdk.NvsEffect;
//import com.meicam.effect.sdk.NvsEffectSdkContext;
//import com.meicam.effect.sdk.NvsVideoEffect;
//import com.meicam.sdk.NvsAssetPackageManager;
//import com.meicam.sdk.NvsRational;
//
//import java.io.IOException;
//
//import static com.meetvr.aicamera.glview.RenderCoreManager.FilterEnum.CARTOON_1;
//import static com.meetvr.aicamera.glview.RenderCoreManager.FilterEnum.CARTOON_2;
//import static com.meetvr.aicamera.glview.RenderCoreManager.FilterEnum.CARTOON_3;
//
///**
// * Created by 周 on 2019/11/14.
// * <p>
// */
//public class RenderCoreManager {
//    private static final String TAG = RenderCoreManager.class.getSimpleName();
//
//    private static final String FX_CARTOON = "Cartoon";
//    private Context context;
//
//    public NvsEffect getmEffect() {
//        return cartoonEffect;
//    }
//
//    private Handler handler = new Handler();
//    private NvsEffect cartoonEffect;
//    private NvsEffect commonEffect;
//    private NvsVideoEffect mArSceneFaceEffect;
//
//    public enum FilterEnum {
//        CARTOON_1,
//        CARTOON_2,
//        CARTOON_3,
//        DELETE
//    }
//
//    public EffectRenderCore getmRenderCore() {
//        return mRenderCore;
//    }
//
//    public NvsEffectSdkContext getmEffectSdkContext() {
//        return mEffectSdkContext;
//    }
//
//    private NvsEffectSdkContext mEffectSdkContext;
//    private EffectRenderCore mRenderCore;
//    private String historyCommonFilterId = "";
//
//    public NvsVideoEffect getmArSceneFaceEffect() {
//        return mArSceneFaceEffect;
//    }
//
//    private RenderCoreManager() {
//    }
//
//    public static RenderCoreManager ARSCENE = new RenderCoreManager();
//
//    public static RenderCoreManager instance() {
//        return ARSCENE;
//    }
//
//    public void initARScene(Context context) {
//        this.context = context;
//        mEffectSdkContext = NvsEffectSdkContext.init(context, "assets:/5332-191-4152e841d9e3f77c222ae4128b439da6.lic", 0);
//        if (mRenderCore == null)
//            mRenderCore = new EffectRenderCore(mEffectSdkContext);
//
//        initFilterPackage();
//
//        initShangT(context);
//        createEffect();
//        initPhotoProcessor();
//    }
//
//    private void initFilterPackage() {
//        try {
//            String fileNames[] = context.getAssets().list("filter");
//            if (fileNames == null || fileNames.length == 0) {
//                return;
//            }
//            for (String fileName : fileNames) {
//                if (fileName.endsWith("videofx")) {
//                    NvsAssetPackageManager assetPackageManager = mEffectSdkContext.getAssetPackageManager();
//                    String logoTemplatePath = "assets:/filter/" + fileName;
//                    StringBuilder packageId = new StringBuilder();
//                    assetPackageManager.installAssetPackage(logoTemplatePath, null,
//                            NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX, true, packageId);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private PhotoImageProcessor m_photoImageProcessor;
//
//    public PhotoImageProcessor getM_photoImageProcessor() {
//        return m_photoImageProcessor;
//    }
//
//    /**
//     * rendercore add beauty and beautyshape
//     */
//    public void createEffect() {
//        if (mArSceneFaceEffect != null) {
//            return;
//        }
//
//        NvsRational aspectRatio = new NvsRational(9, 16);
//        mArSceneFaceEffect = mEffectSdkContext.createVideoEffect(Constants.AR_SCENE, aspectRatio);
//
//        if (mArSceneFaceEffect == null)
//            return;
//
//        mArSceneFaceEffect.setBooleanVal("Single Buffer Mode", true);
//        mArSceneFaceEffect.setBooleanVal("Beauty Effect", true);
//        mArSceneFaceEffect.setBooleanVal("Beauty Shape", true);
//        mArSceneFaceEffect.setStringVal("Scene Id", "");
//        if (mRenderCore != null)
//            mRenderCore.addNewRenderEffect(mArSceneFaceEffect);
//    }
//
//    /**
//     * beauty takephoto
//     */
//    private void initPhotoProcessor() {
//        m_photoImageProcessor = new PhotoImageProcessor(AiCameraApplication.getContext());
//    }
//
//    public void removeEffect() {
//        mRenderCore.removeRenderEffect(Constants.AR_SCENE);
//        mArSceneFaceEffect = null;
//    }
//
//    /**
//     * init face++ recognize
//     *
//     * @param context
//     */
//    private void initShangT(Context context) {
//        String model = "assets:/facemodel_st/tt_face_v2.0.model";
//
//        boolean suc = NvsEffectSdkContext.initHumanDetection(context, model,
//                "assets:/facemodel_st/meishe_mocam_shangtang.lic",
//                NvsEffectSdkContext.HUMAN_DETECTION_FEATURE_FACE_LANDMARK |
//                        NvsEffectSdkContext.HUMAN_DETECTION_FEATURE_FACE_ACTION |
//                        NvsEffectSdkContext.HUMAN_DETECTION_FEATURE_VIDEO_MODE |
//                        NvsEffectSdkContext.HUMAN_DETECTION_FEATURE_IMAGE_MODE);
//    }
//
//
//    /**
//     * add common filter
//     */
//    public void addCommonFilter(String commonFilterId) {
//        if (mRenderCore != null)
//            mRenderCore.removeRenderEffect(historyCommonFilterId);
//
//        historyCommonFilterId = commonFilterId;
//        NvsRational aspectRatio = new NvsRational(1, 1);
//        commonEffect = mEffectSdkContext.createVideoEffect(commonFilterId, aspectRatio);
//        if (mRenderCore != null) {
//            mRenderCore.addNewRenderEffect(commonEffect);
//        }
//
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (commonEffect != null)
//                    commonEffect.setFilterIntensity(1.0f);
//            }
//        }, 100);
//    }
//
//    /**
//     * add cartoon filter
//     *
//     * @param filterEnum
//     */
//    public void addCartoonFilter(FilterEnum filterEnum) {
//        if (mRenderCore != null) {
//            mRenderCore.removeRenderEffect(FX_CARTOON);
//        }
//        NvsRational aspectRatio = new NvsRational(1, 1);
//        cartoonEffect = mEffectSdkContext.createVideoEffect(FX_CARTOON, aspectRatio);
//        if (filterEnum == CARTOON_1) {
//            filter(false, false);
//        } else if (filterEnum == CARTOON_2) {
//            filter(true, false);
//        } else if (filterEnum == CARTOON_3) {
//            filter(false, true);
//        } else {
//            mRenderCore.removeRenderEffect(FX_CARTOON);
//        }
//    }
//
//    private void filter(boolean b, boolean b2) {
//        cartoonEffect.setBooleanVal("Stroke Only", b);
//        cartoonEffect.setBooleanVal("Grayscale", b2);
//        if (mRenderCore != null) {
//            mRenderCore.addNewRenderEffect(cartoonEffect);
//        }
//    }
//
//    /**
//     * increase filter Intensity
//     *
//     * @param value
//     */
//    public void cartoonFilterIntensity(float value) {
//        if (cartoonEffect != null)
//            cartoonEffect.setFilterIntensity(value);
//    }
//
//    /**
//     * increase common filter Intensity
//     *
//     * @param value
//     */
//    public void commonFilterIntensity(float value) {
//        if (commonEffect != null)
//            commonEffect.setFilterIntensity(value);
//    }
//
//
//    /**
//     * beauty param
//     *
//     * @param name
//     * @param value
//     */
//    public void addBeauty(String name, float value) {
//        if (mArSceneFaceEffect == null)
//            return;
//        mArSceneFaceEffect.setFloatVal(name, value);
//        Log.i(TAG, "addBeauty: name = " + name + "  value =  " + value);
//    }
//
//
//    /**
//     * beauty shape param
//     * =======
//     * /**
//     * beauty intensity
//     *
//     * @param value 美颜的数值
//     */
//    public void beautyIntensity(float value) {
//        if (mArSceneFaceEffect == null)
//            return;
//        mArSceneFaceEffect.setFilterIntensity(value);
//        Log.i(TAG, "addBeauty:  value = " + value);
//    }
//
//
//    /**
//     * beautyshape param
//     *
//     * @param name
//     * @param value
//     */
//    public void beautyShape(String name, float value) {
//        if (mArSceneFaceEffect == null)
//            return;
//        mArSceneFaceEffect.setFloatVal(name, value);
//    }
//
//
//    /**
//     * destory rendercore and arscene
//     * =======
//     * /**
//     * destory
//     */
//    public void destory() {
//        if (mArSceneFaceEffect != null) {
//            mArSceneFaceEffect.release();
//        }
//        mArSceneFaceEffect = null;
//        mEffectSdkContext = null;
//        NvsEffectSdkContext.closeHumanDetection();
//        NvsEffectSdkContext.close();
//    }
//}
