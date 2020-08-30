package com.editvideo;

import android.content.Context;
import android.graphics.PointF;

import com.test.xcamera.constants.LogcatConstants;
import com.test.xcamera.utils.LoggerUtils;
import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineAnimatedSticker;
import com.moxiang.common.logging.Logcat;

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by admin on 2018/11/13.
 */

public class VideoCompileUtil {
    public static String getCompileVideoPath() {
        String compilePath = PathUtils.getVideoCompileDirPath();
        if (compilePath == null)
            return null;
        long currentMilis = System.currentTimeMillis();
        String videoName = "/meicam_" + String.valueOf(currentMilis) + ".mp4";
        compilePath += videoName;
        return compilePath;
    }

    public static void compileVideo(NvsStreamingContext context,
                                    NvsTimeline timeline,
                                    String compileVideoPath,
                                    long startTime,
                                    long endTime) {
        compileVideo(context, timeline, compileVideoPath, startTime, endTime, true);
    }

    public static void compileVideo(NvsStreamingContext context,
                                    NvsTimeline timeline,
                                    String compileVideoPath,
                                    long startTime,
                                    long endTime, boolean beautyMode) {
        Logcat.v().tag(LogcatConstants.INTELLIGENTSYNTHETICVIDEO).msg("compile video  starttime "+startTime+" end time "+endTime).out();
        if (context == null || timeline == null || compileVideoPath.isEmpty()) {
            return;
        }
        context.stop();
        context.setCompileConfigurations(null);//之前配置清空
        Hashtable<String, Object> config = new Hashtable<>();
            double bitrate = ParameterSettingValues.instance().getCompileBitrate();
            if (bitrate != 0) {
                config.put(NvsStreamingContext.COMPILE_BITRATE, bitrate * 1000000);
                context.setCompileConfigurations(config);
            }
        int encoderFlag = 0;
        if (ParameterSettingValues.instance().disableDeviceEncorder()) {
            encoderFlag = NvsStreamingContext.STREAMING_ENGINE_COMPILE_FLAG_DISABLE_HARDWARE_ENCODER;
        }
        int h=timeline.getVideoRes().imageHeight;
        int w=timeline.getVideoRes().imageWidth;
        LoggerUtils.printLog("club_ClipInfo :视频宽高 输出:"+h+" w:"+w);

        context.setCustomCompileVideoHeight(timeline.getVideoRes().imageHeight);

        if(beautyMode){
            context.compileTimeline(timeline, startTime, endTime, compileVideoPath, NvsStreamingContext.COMPILE_VIDEO_RESOLUTION_GRADE_CUSTOM, NvsStreamingContext.COMPILE_BITRATE_GRADE_HIGH, NvsStreamingContext.STREAMING_ENGINE_COMPILE_FLAG_BUDDY_HOST_VIDEO_FRAME);
        }else {
            context.compileTimeline(timeline, startTime, endTime, compileVideoPath, NvsStreamingContext.COMPILE_VIDEO_RESOLUTION_GRADE_CUSTOM, NvsStreamingContext.COMPILE_BITRATE_GRADE_HIGH, 0);
        }

    }

    public static NvsTimelineAnimatedSticker addLogoWaterMark(Context context, NvsStreamingContext mStreamingContext, NvsTimeline mTimeline) {
        NvsTimelineAnimatedSticker mLogoSticker = null;
        String logoTemplatePath = "assets:/E14FEE65-71A0-4717-9D66-3397B6C11223.5.animatedsticker";
        String imagePath = "assets:/logo.png";
        StringBuilder packageId = new StringBuilder();
        int error = mStreamingContext.getAssetPackageManager().installAssetPackage(logoTemplatePath, null, NvsAssetPackageManager.ASSET_PACKAGE_TYPE_ANIMATEDSTICKER, true, packageId);
        if (error == NvsAssetPackageManager.ASSET_PACKAGE_MANAGER_ERROR_NO_ERROR
                || error == NvsAssetPackageManager.ASSET_PACKAGE_MANAGER_ERROR_ALREADY_INSTALLED) {
            mLogoSticker = mTimeline.addCustomAnimatedSticker(0, mTimeline.getDuration(), packageId.toString(), imagePath);
            if (mLogoSticker == null) {
                return null;
            }
            mLogoSticker.setScale(0.3f);
            List<PointF> list = mLogoSticker.getBoundingRectangleVertices();
            if (list == null || list.size() < 4) {
                return null;
            }
            Collections.sort(list, new Util.PointXComparator());
            int offset = ScreenUtils.dip2px(context, 10);
            float xPos = -(mTimeline.getVideoRes().imageWidth / 2 + list.get(0).x - offset);

            Collections.sort(list, new Util.PointYComparator());
            float y_dis = list.get(3).y - list.get(0).y;
            float yPos = mTimeline.getVideoRes().imageHeight / 2 - list.get(0).y - y_dis - offset;

            PointF logoPos = new PointF(xPos, yPos);
            mLogoSticker.translateAnimatedSticker(logoPos);
        }
        return mLogoSticker;
    }
    public static void removeLogoSticker(NvsTimelineAnimatedSticker mLogoSticker, NvsTimeline mTimeline){
        if(mLogoSticker != null){
            mTimeline.removeAnimatedSticker(mLogoSticker);
        }
    }
}
