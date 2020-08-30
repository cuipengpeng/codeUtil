package com.test.xcamera.watermark.controller;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.bean.FristMarkBean;
import com.test.xcamera.bean.SecondMarkBean;
import com.test.xcamera.utils.Constants;
import com.test.xcamera.utils.MoStreamingContext;
import com.test.xcamera.utils.WaterMarkJson;
import com.test.xcamera.watermark.WaterMarker;
import com.test.xcamera.watermark.info.LocalVideoInfo;
import com.test.xcamera.watermark.utils.CommonUtil;
import com.test.xcamera.watermark.utils.ContextRef;
import com.test.xcamera.watermark.utils.FileUtils;
import com.test.xcamera.watermark.utils.LoadingUtil;
import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsRational;
import com.meicam.sdk.NvsStreamingContext;
import com.moxiang.common.share.ShareManager;

import com.ss.android.vesdk.VEEditor;
import com.ss.android.vesdk.VEListener;
import com.ss.android.vesdk.VEResult;
import com.ss.android.vesdk.VEVideoEncodeSettings;
import com.ss.android.vesdk.VEWatermarkParam;

import java.io.File;
import java.util.ArrayList;

import static com.ss.android.vesdk.VEVideoEncodeSettings.COMPILE_TYPE.COMPILE_TYPE_MP4;
import static com.ss.android.vesdk.VEVideoEncodeSettings.USAGE_COMPILE;

public class MarkerController {

    private final Gson gson;

    private static class SingletonClassInstance {
        private static final MarkerController instance = new MarkerController();
    }

    public static MarkerController getInstance() {
        return SingletonClassInstance.instance;
    }

    private MarkerController() {
        gson = new Gson();
        handler = new MarkerHandler();
    }

    private MarkerHandler handler;
    private WaterMarker defaultMark;
    private final String LANDSCAPE_VIDEO = "landscape_video.mp4";
    private final String PORTRAIT_VIDEO = "portrait_video.mp4";
    private final String SEP = File.separator;
    private String tailDirPath = "";
    private VEEditor mEditor;
    private String finalOutputPath = "";
    private LoadingUtil loadingUtil;
    private boolean isDone = false;

    public void onAppCreate(final Application application) {
        tailDirPath = Constants.markPngAndVideo + "video" + SEP;
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                ContextRef.getInstance().setContext(application, activity);
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                ContextRef.getInstance().clearContext();
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });
        setShareListener();
    }

    private void setShareListener() {
        ShareManager.getInstance().setBeforeShareListener(new ShareManager.OnBeforeShareListener() {
            @Override
            public void onBeforeShare(ShareManager.ShareEntity entity, Activity activity, ShareManager.ShareChooser shareChooser) {
                startMarker(entity, activity, shareChooser);
            }
        });
    }

    private void startMarker(ShareManager.ShareEntity entity, Activity activity, ShareManager.ShareChooser shareChooser) {
        WaterMarker.startWithoutLoading(entity.getThumbUrl(), new WaterMarker.ResultListener() {
            @Override
            public void onSuccess(String path) {
                entity.setThumbUrl(path);
//                ShareManager.getInstance().finalShare(activity, entity, shareChooser, null);
            }

            @Override
            public void onFailure(String err) {

            }
        });
    }

    private void onFailurePost(WaterMarker waterMarker, String error) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (waterMarker.getResultListener() != null) {
                    waterMarker.getResultListener().onFailure(error);
                }
            }
        });
    }

    private void initEditor(String videoPath, String tailPath) {
        if (mEditor == null)
            mEditor = new VEEditor(FileUtils.ROOT_DIR);
        int mBackgroundMusicTrimIn = 0;
        int mBackgroundMusicTrimOut = 15000;
//        String[] audioPaths = new String[]{audioPath};
        String[] videoPaths = null;

        if (!TextUtils.isEmpty(tailPath)) {
            videoPaths = new String[]{videoPath, tailPath};
        } else {
            videoPaths = new String[]{videoPath};
        }
//        String[] transitions = new String[]{TEDefine.TETransition.FADE};

        int ret = mEditor.init(videoPaths, null, null, VEEditor.VIDEO_RATIO.VIDEO_OUT_RATIO_ORIGINAL);
        if (ret != VEResult.TER_OK) {
            if (mEditor != null) {
                mEditor.destroy();
                mEditor = null;
            }
            return;
        }

        mEditor.setLoopPlay(true);
        mEditor.setScaleMode(VEEditor.SCALE_MODE.SCALE_MODE_CENTER_CROP);

        mEditor.prepare();

        mEditor.addAudioTrack(null, mBackgroundMusicTrimIn, mBackgroundMusicTrimOut, true);
        mEditor.seek(0, VEEditor.SEEK_MODE.EDITOR_SEEK_FLAG_LastSeek);
    }

    private void start(WaterMarker waterMarker, boolean isAppend, boolean isInnerMark) {
        String path = waterMarker.getPath();
        if (waterMarker.getShowLoading()) {
            if (loadingUtil == null) {
                loadingUtil = new LoadingUtil(handler);
                loadingUtil.showLoading("正在处理...");
            }
        }

        String tailPath = "";
        LocalVideoInfo info = CommonUtil.getLocalVideoInfo(path);
        //如果没有传入的拼接视频路径
        int localWidth = info.getWidth();
        int localHeight = info.getHeight();
        int bitrate = info.getBitrate();

        String markVideo = Constants.markPngAndVideo + "video" + SEP + "w_" + localWidth + "_h_" + localHeight + ".mp4";
        if (isAppend) {
            if (localWidth > localHeight) {
                tailPath = tailDirPath + LANDSCAPE_VIDEO;
            } else {
                tailPath = tailDirPath + PORTRAIT_VIDEO;
            }
            String fileName = CommonUtil.getMD5(
                    ContextRef.get().getPackageName() + CommonUtil.getFileMD5(new File(path))) + ".mp4";
            finalOutputPath = getFinalOutput(waterMarker) + SEP + fileName;
        } else {
            if (isInnerMark) {
                finalOutputPath = markVideo;
            } else {
                finalOutputPath = Constants.markPngAndVideo + "video" + SEP + "tmp.mp4";
            }
        }

        initEditor(path, tailPath);

        VEWatermarkParam param = new VEWatermarkParam();
//        String markPath = ContextRef.get().getFilesDir().getAbsolutePath() + SEP + "mark" + SEP + "water_mark.png";
//        String secondMarkPath = ContextRef.get().getFilesDir().getAbsolutePath() + SEP + "mark" + SEP + "water_mark_second.png";
//        File markFile = new File(markPath);
        if (isInnerMark) {
//            File secondMarkFile = new File(secondMarkPath);
//            if (secondMarkFile.exists()) {

            //第一段水印
            param.images = getFristMarkPath(WaterMarkJson.getFristMarkPath());

            //第二段水印
            param.secondHalfImages = getSecondMarkPath(WaterMarkJson.getSecondMarkPath());
//            }

            //水印蒙层 1588832061383_23
            VEWatermarkParam.VEWatermarkMask mask = new VEWatermarkParam.VEWatermarkMask();
            if (localWidth > localHeight) {
                mask.maskImage = Constants.markPngAndVideo + "mark" + SEP + "bg_landscape_mask.png";
            } else {
                mask.maskImage = Constants.markPngAndVideo + "mark" + SEP + "bg_portart_mask.png";
            }

            mask.xOffset = 0;
            mask.yOffset = 0;
            mask.width = localWidth;
            mask.height = localHeight;
            param.mask = mask;

            param.xOffset = waterMarker.getX();
            param.yOffset = waterMarker.getY();

            param.width = waterMarker.getWidth();
            param.height = waterMarker.getHeight();
            //是否双路输出
            param.needExtFile = waterMarker.isNeedExtFile();
            //水印间隔，视频每n帧更换一次水印帧
            param.interval = 1;
        }

        int fps = getLocalVideoFps(path);
        VEVideoEncodeSettings setting = new VEVideoEncodeSettings.Builder(USAGE_COMPILE)
                .setCompileType(COMPILE_TYPE_MP4)
                .setVideoRes(getOutputVideoWidth(waterMarker, localWidth),
                        getOutputVideoHeight(waterMarker, localHeight))
                .setHwEnc(true)
                .setGopSize(fps + 30)
//                .setVideoBitrate(VEVideoEncodeSettings.ENCODE_BITRATE_MODE.ENCODE_BITRATE_ABR, getLocalVideoBitrate(path))
                .setFps(fps)
                .setWatermarkParam(param)
                .build();
        isDone = false;
        mEditor.compile(finalOutputPath, null, setting, new VEListener.VEEditorCompileListener() {

            @Override
            public void onCompileError(int i, int i1, float v, String s) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onFailurePost(waterMarker, s);
                        if (waterMarker.getShowLoading()) {
                            loadingUtil.endLoading();
                        }
                    }
                });
            }

            @Override
            public void onCompileDone() {
                if (isAppend) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (waterMarker.getShowLoading()) {
                                loadingUtil.endLoading();
                                loadingUtil = null;
                            }
                            File file = new File(markVideo);
                            if (file.exists()) {
                                file.delete();
                            }
                            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            Uri uri = Uri.fromFile(new File(finalOutputPath));
                            intent.setData(uri);
                            ContextRef.get().sendBroadcast(intent);

                            Intent intent2 = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            Uri uri2 = Uri.fromFile(new File(getFinalOutput(WaterMarker.builder().build()) + SEP + "fps_60.mp4"));
                            intent.setData(uri2);
                            ContextRef.get().sendBroadcast(intent2);

                            if (waterMarker.getResultListener() != null) {
                                waterMarker.getResultListener().onSuccess(finalOutputPath);
                            }

                            if (mEditor != null) {
                                mEditor.destroy();
                                mEditor = null;
                            }
                        }
                    });
                } else {
                    if (mEditor != null) {
                        mEditor.destroy();
                        mEditor = null;
                    }
                    if (isInnerMark) {
                        WaterMarker tmpWaterMarker = WaterMarker.builder()
                                .setPath(finalOutputPath)
                                .setWidth(waterMarker.getWidth())
                                .setHeight(waterMarker.getHeight())
                                .setX(waterMarker.getX())
                                .setY(waterMarker.getY())
                                .setShowLoading(waterMarker.getShowLoading())
                                .setResultListener(waterMarker.getResultListener())
                                .build();
                        start(tmpWaterMarker, true, false);
                    }
                }
            }

            @Override
            public void onCompileProgress(float v) {
                int progress = Math.round(v * 100);
                if (isInnerMark) {
                    if (waterMarker.getResultListener() != null) {
                        waterMarker.getResultListener().onProgress(progress, 1);
                    }
                } else if(isAppend){
                    if (waterMarker.getResultListener() != null) {
                        waterMarker.getResultListener().onProgress(progress, 2);
                    }
                }
                if (waterMarker.getShowLoading()) {
                    if (isInnerMark) {
                        if (isAppend) {
                            setProgress((50 + Math.round(v * 100 / 2)));
                        } else {
                            setProgress(Math.round(v * 100 / 2));
                        }
                    } else {
                        if (isAppend) {
                            if (!isDone) {
                                setProgress(progress);
                            }
                            if (progress == 100) {
                                isDone = true;
                            }
                        }
                    }
                }
            }
        });
    }

    private String[] getFristMarkPath(String fristMarkPath) {
        FristMarkBean secondMarkBean = gson.fromJson(fristMarkPath, FristMarkBean.class);
        int size = secondMarkBean.list.size();
        ArrayList<FristMarkBean.MarkBean> list = secondMarkBean.list;
        String[] strings = new String[size];
        for (int i = 0; i < list.size(); i++) {
            strings[i] = list.get(i).path;
        }
        return strings;
    }

    private String[] getSecondMarkPath(String secondMarkPath) {
        SecondMarkBean secondMarkBean = gson.fromJson(secondMarkPath, SecondMarkBean.class);
        int size = secondMarkBean.list.size();
        ArrayList<SecondMarkBean.MarkBean> list = secondMarkBean.list;
        String[] strings = new String[size];
        for (int i = 0; i < list.size(); i++) {
            strings[i] = list.get(i).path;
        }
        return strings;
    }

    /**
     * 获取本地视频fps
     *
     * @param path
     * @return
     */
    private int getLocalVideoFps(String path) {
        NvsStreamingContext streamingContext = MoStreamingContext.getInstance().getNvsStreamingContext();
        if (streamingContext == null || streamingContext.getAVFileInfo(path) == null) {
            return 30;
        }
        NvsAVFileInfo fileInfo = streamingContext.getAVFileInfo(path);
        NvsRational fps = fileInfo.getVideoStreamFrameRate(0);
        if (fps.den <= 0 || fps.num <= 0) {
            return 30;
        } else {
            return fps.num / fps.den;
        }
    }


    /**
     * 获取本地视频 bitrate
     *
     * @param path
     * @return
     */
    private int getLocalVideoBitrate(String path) {
        NvsAVFileInfo info = null;
        long bitrate = 0;
        if (NvsStreamingContext.getInstance() != null) {
            info = NvsStreamingContext.getInstance().getAVFileInfo(path);
        }
        if (info != null) {
            bitrate = info.getDataRate();
        }
        if (bitrate == 0) {
            return 4 * 1024 * 1024;
        } else {
            return Integer.parseInt(bitrate + "");
        }
    }

    private void setProgress(int value) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (loadingUtil != null) {
                    loadingUtil.setTips("处理中" + value + "%");
                } else {
                    loadingUtil = new LoadingUtil(handler);
                    loadingUtil.showLoading("处理中" + value + "%");
                }
            }
        });
    }

    public void start(WaterMarker waterMarker) {
        boolean hasMarkFile = getMarkFile().exists();
        start(waterMarker, !hasMarkFile, hasMarkFile);
    }

    /**
     * 获取最终输出路径
     *
     * @param waterMarker
     * @return
     */
    public String getFinalOutput(WaterMarker waterMarker) {
        String waterOut = waterMarker.getOutputPath();
        if (TextUtils.isEmpty(waterOut)) {
            return Constants.myGalleryLocalPath;
//            return Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera";
        }
        return waterOut;
    }

    /**
     * 获取输出视频的宽
     *
     * @param waterMarker
     * @param localWidth
     * @return
     */
    private int getOutputVideoWidth(WaterMarker waterMarker, int localWidth) {
        if (waterMarker.getOutputVideoWidth() == 0) {
            return localWidth;
        } else {
            return waterMarker.getOutputVideoWidth();
        }
    }

    /**
     * 获取输出视频的高
     *
     * @param waterMarker
     * @param localHeight
     * @return
     */
    private int getOutputVideoHeight(WaterMarker waterMarker, int localHeight) {
        if (waterMarker.getOutputVideoHeight() == 0) {
            return localHeight;
        } else {
            return waterMarker.getOutputVideoHeight();
        }
    }

    /**
     * 复制结尾视频到本地
     */
    public void copyTailVideo() {
        File fileLan = new File(tailDirPath + LANDSCAPE_VIDEO);
        File filePor = new File(tailDirPath + PORTRAIT_VIDEO);
        if (!fileLan.exists() || !filePor.exists()) {
            CommonUtil.copyAssets(AiCameraApplication.mApplication, "video");
        }
        if (!getMarkFile().exists()) {
            CommonUtil.copyAssets(AiCameraApplication.mApplication, "mark");
        }
    }

    private File getMarkFile() {
        return new File(Constants.markPngAndVideo + "mark");
    }


    public void initDefault(WaterMarker defaultMark) {
        this.defaultMark = defaultMark;
    }

    public WaterMarker getDefaultMark() {
        return this.defaultMark;
    }
}
