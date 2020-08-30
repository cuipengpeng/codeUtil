package com.editvideo;

import android.graphics.PointF;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.Log;

import com.editvideo.dataInfo.CompoundCaptionInfo;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.phonealbum.widget.VideoEditManger;
import com.test.xcamera.utils.LoggerUtils;
import com.meicam.sdk.NvsAudioClip;
import com.meicam.sdk.NvsAudioResolution;
import com.meicam.sdk.NvsAudioTrack;
import com.meicam.sdk.NvsColor;
import com.meicam.sdk.NvsLiveWindow;
import com.meicam.sdk.NvsRational;
import com.meicam.sdk.NvsSize;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineAnimatedSticker;
import com.meicam.sdk.NvsTimelineCaption;
import com.meicam.sdk.NvsTimelineCompoundCaption;
import com.meicam.sdk.NvsTimelineVideoFx;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meicam.sdk.NvsVideoResolution;
import com.meicam.sdk.NvsVideoTrack;
import com.editvideo.dataInfo.CaptionInfo;
import com.editvideo.dataInfo.ClipInfo;
import com.editvideo.dataInfo.MusicInfo;
import com.editvideo.dataInfo.RecordAudioInfo;
import com.editvideo.dataInfo.StickerInfo;
import com.editvideo.dataInfo.TimelineData;
import com.editvideo.dataInfo.TransitionInfo;
import com.editvideo.dataInfo.WaterMarkData;

import java.util.ArrayList;
import java.util.List;

import static com.editvideo.WaterMarkConstant.WATERMARK_DYNAMICS_FXNAME;

/**
 * Created by admin on 2018/5/29.
 */

public class TimelineUtil {
    private static String TAG = "TimelineUtil";
    public final static long TIME_BASE = 1000000;



    /**
     * 初始化美摄Context
     */
    public static void initStreamingContext() {
        String licensePath = "assets:/meetvr_arcamera.lic";
//        String licensePath = "assets:/meishesdk.lic";
        NvsStreamingContext.init(AiCameraApplication.mApplication, licensePath, NvsStreamingContext.STREAMING_CONTEXT_FLAG_SUPPORT_4K_EDIT);
//        NvsStreamingContext.init(AiCameraApplication.mApplication, licensePath, NvsStreamingContext.STREAMING_CONTEXT_FLAG_NO_HARDWARE_VIDEO_READER);
        NvAssetManager.init(AiCameraApplication.mApplication);
    }

    //主编辑页面时间线API
    public static NvsTimeline createTimeline(){
        NvsTimeline timeline = newTimeline(TimelineData.instance().getVideoResolution());
        if(timeline == null) {
            Log.e(TAG, "failed to create timeline");
            return null;
        }
        if(!buildVideoTrack(timeline)) {
            return timeline;
        }
        //audioTrack 一般是两个轨道
        timeline.appendAudioTrack(); // 音乐轨道
        timeline.appendAudioTrack(); // 录音轨道

        setTimelineData(timeline);
        VideoEditManger.addSubTitleAll(timeline);
        VideoEditManger.addStickerAll(timeline);
        return timeline;
    }

    //主编辑页面时间线API
    public static NvsTimeline createTimeline(NvsVideoResolution videoResolution, NvsRational videoFps){
        NvsStreamingContext context = NvsStreamingContext.getInstance();
        if(context == null) {
            Log.e(TAG, "failed to get streamingContext");
            return null;
        }

        NvsVideoResolution videoEditRes = videoResolution;
        videoEditRes.imagePAR = new NvsRational(1, 1);

        NvsAudioResolution audioEditRes = new NvsAudioResolution();
        audioEditRes.sampleRate = 44100;
        audioEditRes.channelCount = 2;

        NvsTimeline timeline = context.createTimeline(videoEditRes, videoFps, audioEditRes);

        if(timeline == null) {
            Log.e(TAG, "failed to create timeline");
            return null;
        }
        if(!buildVideoTrack(timeline)) {
            return timeline;
        }
        //audioTrack 一般是两个轨道
        timeline.appendAudioTrack(); // 音乐轨道
        timeline.appendAudioTrack(); // 录音轨道

        setTimelineData(timeline);
        VideoEditManger.addSubTitleAll(timeline);
        VideoEditManger.addStickerAll(timeline);
        return timeline;
    }
    //片段编辑页面时间线API
    public static NvsTimeline createSingleClipTimeline(ClipInfo clipInfo, boolean isTrimClip){
        NvsTimeline timeline = newTimeline(TimelineData.instance().getVideoResolution());
        if(timeline == null) {
            Log.e(TAG, "failed to create timeline");
            return null;
        }
        buildSingleClipVideoTrack(timeline,clipInfo,isTrimClip);
        return timeline;
    }
    //片段编辑页面时间线扩展API
    public static NvsTimeline createSingleClipTimelineExt(NvsVideoResolution videoEditRes,String filePath){
        NvsTimeline timeline = newTimeline(videoEditRes);
        if(timeline == null) {
            Log.e(TAG, "failed to create timeline");
            return null;
        }
        buildSingleClipVideoTrackExt(timeline,filePath);
        return timeline;
    }

     public static boolean buildSingleClipVideoTrack(NvsTimeline timeline,ClipInfo clipInfo,boolean isTrimClip) {
         if(timeline == null || clipInfo == null) {
             return false;
         }

         NvsVideoTrack videoTrack = timeline.appendVideoTrack();
         if(videoTrack == null){
             Log.e(TAG, "failed to append video track");
             return false;
         }
         addVideoClip(videoTrack,clipInfo,isTrimClip);
         return true;
     }
    public static boolean buildSingleClipVideoTrackExt(NvsTimeline timeline,String filePath) {
        if(timeline == null || filePath == null) {
            return false;
        }

        NvsVideoTrack videoTrack = timeline.appendVideoTrack();
        if(videoTrack == null){
            Log.e(TAG, "failed to append video track");
            return false;
        }
        NvsVideoClip videoClip = videoTrack.appendClip(filePath);
        if (videoClip == null){
            Log.e(TAG, "failed to append video clip");
            return false;
        }
        videoClip.changeTrimOutPoint(8000000,true);
        return true;
    }
    public static void setTimelineData(NvsTimeline timeline) {
        if(timeline == null)
            return;
        // 此处注意是clone一份音乐数据，因为添加主题的接口会把音乐数据删掉
        List<MusicInfo> musicInfoClone = TimelineData.instance().cloneMusicData();
        String themeId = TimelineData.instance().getThemeData();
        applyTheme(timeline,themeId);

        if(musicInfoClone != null) {
            TimelineData.instance().setMusicList(musicInfoClone);
            buildTimelineMusic(timeline, musicInfoClone);
        }

        VideoClipFxInfo videoClipFxData = TimelineData.instance().getVideoClipFxData();
        buildTimelineFilter(timeline, videoClipFxData);
        setTransition(timeline);
        ArrayList<StickerInfo> stickerArray = TimelineData.instance().getStickerData();
        setSticker(timeline, stickerArray);

        ArrayList<CaptionInfo> captionArray = TimelineData.instance().getCaptionData();
        setCaption(timeline, captionArray);
        //compound caption
        ArrayList<CompoundCaptionInfo> compoundCaptionArray = TimelineData.instance( ).getCompoundCaptionArray( );
        setCompoundCaption(timeline, compoundCaptionArray);

        ArrayList<RecordAudioInfo> recordArray = TimelineData.instance().getRecordAudioData();
        buildTimelineRecordAudio(timeline, recordArray);

        WaterMarkData waterMarkData = TimelineData.instance().getWaterMarkData();
        WaterMarkUtil.setWaterMark(timeline, waterMarkData);
    }

    public static boolean removeTimeline(NvsTimeline timeline){
        if(timeline == null)
            return false;

        NvsStreamingContext context = NvsStreamingContext.getInstance();
        if(context == null)
            return false;

        return context.removeTimeline(timeline);
    }
    //add compound caption
    public static boolean setCompoundCaption(NvsTimeline timeline, ArrayList<CompoundCaptionInfo> captionArray) {
        if (timeline == null||captionArray==null)
            return false;

        NvsTimelineCompoundCaption deleteCaption = timeline.getFirstCompoundCaption( );
        while (deleteCaption != null) {
            deleteCaption = timeline.removeCompoundCaption(deleteCaption);
        }

        for (CompoundCaptionInfo caption : captionArray) {
            long duration = caption.getOutPoint( ) - caption.getInPoint( );
            NvsTimelineCompoundCaption newCaption = timeline.addCompoundCaption(caption.getInPoint( ),
                    duration, caption.getCaptionStyleUuid( ));
            updateCompoundCaptionAttribute(newCaption, caption);
        }
        return true;
    }
    //update compound caption attribute
    private static void updateCompoundCaptionAttribute(NvsTimelineCompoundCaption newCaption, CompoundCaptionInfo caption) {
        if (newCaption == null || caption == null)
            return;
        newCaption.setZValue(caption.getCaptionZVal());
        ArrayList<CompoundCaptionInfo.CompoundCaptionAttr> captionAttrList = caption.getCaptionAttributeList( );
        if(captionAttrList==null){
            return;
        }
        int captionCount = newCaption.getCaptionCount( );
        for (int index = 0; index < captionCount; ++index) {
            if(captionAttrList.size()<=index){
                break;
            }
            CompoundCaptionInfo.CompoundCaptionAttr captionAttr = captionAttrList.get(index);
            if (captionAttr == null) {
                continue;
            }
            NvsColor textColor = ColorUtil.colorStringtoNvsColor(captionAttr.getCaptionColor( ));
            if (textColor != null) {
                newCaption.setTextColor(index, textColor);
            }

            String fontName = captionAttr.getCaptionFontName( );
            if (!TextUtils.isEmpty(fontName)) {
                newCaption.setFontFamily(index, fontName);
            }
            String captionText = captionAttr.getCaptionText( );
            if (!TextUtils.isEmpty(captionText)) {
                newCaption.setText(index, captionText);
            }
        }

        // 放缩字幕
        float scaleFactorX = caption.getScaleFactorX( );
        float scaleFactorY = caption.getScaleFactorY( );
        newCaption.setScaleX(scaleFactorX);
        newCaption.setScaleY(scaleFactorY);
        float rotation = caption.getRotation( );
        // 旋转字幕
        newCaption.setRotationZ(rotation);
        newCaption.setZValue(caption.getCaptionZVal( ));
        PointF translation = caption.getTranslation( );
        if (translation != null) {
            newCaption.setCaptionTranslation(translation);
        }
    }
    public static boolean buildVideoTrack(NvsTimeline timeline) {
        if(timeline == null) {
            return false;
        }

        NvsVideoTrack videoTrack = timeline.appendVideoTrack();
        if(videoTrack == null){
            Log.e(TAG, "failed to append video track");
            return false;
        }

        ArrayList<ClipInfo> videoClipArray = TimelineData.instance().getClipInfoData();
        for (int i = 0;i < videoClipArray.size();i++) {
            ClipInfo clipInfo = videoClipArray.get(i);
            addVideoClip(videoTrack,clipInfo,true);
        }
        float videoVolume = TimelineData.instance().getOriginVideoVolume();
        videoTrack.setVolumeGain(videoVolume,videoVolume);

        return true;
    }

    public static boolean reBuildVideoTrack(NvsTimeline timeline) {
        if(timeline == null) {
            return false;
        }
        int videoTrackCount = timeline.videoTrackCount();
        NvsVideoTrack videoTrack = videoTrackCount == 0 ? timeline.appendVideoTrack() : timeline.getVideoTrackByIndex(0);
        if(videoTrack == null){
            Log.e(TAG, "failed to append video track");
            return false;
        }
        videoTrack.removeAllClips();
        ArrayList<ClipInfo> videoClipArray = TimelineData.instance().getClipInfoData();
        for (int i = 0;i < videoClipArray.size();i++) {
            ClipInfo clipInfo = videoClipArray.get(i);
            addVideoClip(videoTrack,clipInfo,true);
        }
        setTimelineData(timeline);
        float videoVolume = TimelineData.instance().getOriginVideoVolume();
        videoTrack.setVolumeGain(videoVolume,videoVolume);
        /**
         * 添加字幕
         */
        VideoEditManger.addSubTitleAll(timeline);
        VideoEditManger.addStickerAll(timeline);

        return true;
    }

    private static void addVideoClip(NvsVideoTrack videoTrack,ClipInfo clipInfo,boolean isTrimClip){
        if(videoTrack == null || clipInfo == null)
            return;
        String filePath = clipInfo.getFilePath();
        NvsVideoClip videoClip = videoTrack.appendClip(filePath);
        if (videoClip == null) {
            Log.e(TAG, "failed to append video clip");
            return;
        }

        NvsVideoFx mopiVideoFx = videoClip.appendBuiltinFx(Constants.AR_SCENE);
        //美型功能需要这些设置
//        mopiVideoFx.setBooleanVal("Beauty Effect", true);
//        mopiVideoFx.setBooleanVal("Beauty Shape", true);
//        mopiVideoFx.setBooleanVal("Single Buffer Mode", false);

        mopiVideoFx.setFloatVal(Constants.FX_BEAUTY_RED, clipInfo.getRed());
        mopiVideoFx.setFloatVal(Constants.FX_BEAUTY_MOPI, clipInfo.getStrength());
        mopiVideoFx.setFloatVal(Constants.FX_BEAUTY_WHITE, clipInfo.getWhite());
        mopiVideoFx.setFloatVal(Constants.FX_BEAUTY_THIN_FACE, clipInfo.getThinface());
        mopiVideoFx.setFloatVal(Constants.FX_BEAUTY_BIG_EYE, clipInfo.getBigeye());
        mopiVideoFx.setFloatVal(Constants.FX_BEAUTY_XIABA, clipInfo.getXiaba());
        mopiVideoFx.setFloatVal(Constants.FX_BEAUTY_NOSE, clipInfo.getNose());
        mopiVideoFx.setFloatVal(Constants.FX_BEAUTY_HEAD, clipInfo.getHead());
        mopiVideoFx.setFloatVal(Constants.FX_BEAUTY_MOUTH_SIZE, clipInfo.getMouthSize());
        mopiVideoFx.setFloatVal(Constants.FX_BEAUTY_MOUTH_CORNER, clipInfo.getMouthCorner());
        mopiVideoFx.setFloatVal(Constants.FX_BEAUTY_SMALL_FACE, clipInfo.getSmallFace());
        mopiVideoFx.setFloatVal(Constants.FX_BEAUTY_EYE_CORNER, clipInfo.getEyeCorner());
        mopiVideoFx.setFloatVal(Constants.FX_BEAUTY_FACE_WIDTH, clipInfo.getFaceWidth());
        mopiVideoFx.setFloatVal(Constants.FX_BEAUTY_NOSE_LENGTH, clipInfo.getNoseLength());

        boolean blurFlag = ParameterSettingValues.instance().isUseBackgroudBlur();
        if(blurFlag) {
            videoClip.setSourceBackgroundMode(NvsVideoClip.ClIP_BACKGROUNDMODE_BLUR);
        }

        float brightVal = clipInfo.getBrightnessVal();
        float contrastVal = clipInfo.getContrastVal();
        float saturationVal = clipInfo.getSaturationVal();
        float vignette = clipInfo.getVignetteVal();
        float sharpen = clipInfo.getSharpenVal();
        if(brightVal >= 0 || contrastVal >= 0 || saturationVal >= 0){
            NvsVideoFx videoFxColor = videoClip.appendBuiltinFx(Constants.FX_COLOR_PROPERTY);
            if(videoFxColor != null){
                if(brightVal >= 0)
                    videoFxColor.setFloatVal(Constants.FX_COLOR_PROPERTY_BRIGHTNESS,brightVal);
                if(contrastVal >= 0)
                    videoFxColor.setFloatVal(Constants.FX_COLOR_PROPERTY_CONTRAST,contrastVal);
                if(saturationVal >= 0)
                    videoFxColor.setFloatVal(Constants.FX_COLOR_PROPERTY_SATURATION,saturationVal);
            }
        }
        if(vignette >= 0) {
            NvsVideoFx vignetteVideoFx = videoClip.appendBuiltinFx(Constants.FX_VIGNETTE);
            vignetteVideoFx.setFloatVal(Constants.FX_VIGNETTE_DEGREE, vignette);
        }
        if(sharpen >= 0) {
            NvsVideoFx sharpenVideoFx = videoClip.appendBuiltinFx(Constants.FX_SHARPEN);
            sharpenVideoFx.setFloatVal(Constants.FX_SHARPEN_AMOUNT, sharpen);
        }
        int videoType = videoClip.getVideoType();
        if(videoType == NvsVideoClip.VIDEO_CLIP_TYPE_IMAGE){//当前片段是图片
//            long trimIn = videoClip.getTrimIn();
//            long trimOut = clipInfo.getTrimOut();
//            if(trimOut > 0 && trimOut > trimIn ) {
//                videoClip.changeTrimOutPoint(trimOut, true);
//            }
            long trimIn = clipInfo.getTrimIn();
            long trimOut = clipInfo.getTrimOut();
            if(trimIn > 0) {
                videoClip.changeTrimInPoint(trimIn, true);
            }
            if(trimOut > 0 && trimOut > trimIn) {
                videoClip.changeTrimOutPoint(trimOut, true);
            }

            int imgDisplayMode = clipInfo.getImgDispalyMode();
            if(imgDisplayMode == Constants.EDIT_MODE_PHOTO_AREA_DISPLAY){//区域显示
                videoClip.setImageMotionMode(NvsVideoClip.IMAGE_CLIP_MOTIONMMODE_ROI);
                RectF normalStartRectF = clipInfo.getNormalStartROI();
                RectF normalEndRectF = clipInfo.getNormalEndROI();
                if(normalStartRectF != null && normalEndRectF != null){
                    videoClip.setImageMotionROI(normalStartRectF,normalEndRectF);
                }
            }else {//全图显示
                videoClip.setImageMotionMode(NvsVideoClip.CLIP_MOTIONMODE_LETTERBOX_ZOOMIN);
            }

            float speed = clipInfo.getSpeed();
            if(speed > 0){
                videoClip.changeSpeed(speed);
            }
            boolean isOpenMove = clipInfo.isOpenPhotoMove();
            videoClip.setImageMotionAnimationEnabled(isOpenMove);
        }else{//当前片段是视频
            float volumeGain = clipInfo.getVolume();
            videoClip.setVolumeGain(volumeGain,volumeGain);
            float pan = clipInfo.getPan();
            float scan = clipInfo.getScan();
            videoClip.setPanAndScan(pan,scan);
            float speed = clipInfo.getSpeed();
            if(speed > 0){
                videoClip.changeSpeed(speed);
            }
            videoClip.setExtraVideoRotation(clipInfo.getRotateAngle());
            int scaleX = clipInfo.getScaleX();
            int scaleY = clipInfo.getScaleY();
            if(scaleX >= -1 || scaleY >= -1){
                NvsVideoFx videoFxTransform = videoClip.appendBuiltinFx(Constants.FX_TRANSFORM_2D);
                if(videoFxTransform != null){
                    if(scaleX >= -1)
                        videoFxTransform.setFloatVal(Constants.FX_TRANSFORM_2D_SCALE_X,scaleX);
                    if(scaleY >= -1)
                        videoFxTransform.setFloatVal(Constants.FX_TRANSFORM_2D_SCALE_Y,scaleY);
                }
            }

            if(!isTrimClip)//如果当前是裁剪页面，不裁剪片段
                return;
            long trimIn = clipInfo.getTrimIn();
            long trimOut = clipInfo.getTrimOut();
            if(trimIn > 0) {
                videoClip.changeTrimInPoint(trimIn, true);
            }
            if(trimOut > 0 && trimOut > trimIn) {
                videoClip.changeTrimOutPoint(trimOut, true);
            }
        }
    }

    public static boolean buildTimelineFilter(NvsTimeline timeline, VideoClipFxInfo videoClipFxData) {
        if(timeline == null) {
            return false;
        }

        NvsVideoTrack videoTrack = timeline.getVideoTrackByIndex(0);
        if(videoTrack == null) {
            return false;
        }

        if(videoClipFxData == null)
            return false;

        ArrayList<ClipInfo> clipInfos = TimelineData.instance().getClipInfoData();

        int videoClipCount = videoTrack.getClipCount();
        for(int i = 0;i<videoClipCount;i++) {
            NvsVideoClip clip = videoTrack.getClipByIndex(i);
            if(clip == null)
                continue;

            removeAllVideoFx(clip);
            String clipFilPath = clip.getFilePath();
            boolean isSrcVideoAsset = false;
            for(ClipInfo clipInfo :clipInfos) {//过滤掉主题中自带片头或者片尾的视频
                String videoFilePath = clipInfo.getFilePath();
                if(clipFilPath.equals(videoFilePath)){
                    isSrcVideoAsset = true;
                    break;
                }
            }

            if(!isSrcVideoAsset)
                continue;

            String name = videoClipFxData.getFxId();
            if(TextUtils.isEmpty(name)) {
                continue;
            }
            int mode = videoClipFxData.getFxMode();
            float fxIntensity = videoClipFxData.getFxIntensity();
            if(mode == FilterItem.FILTERMODE_BUILTIN){//内建特效
                NvsVideoFx builtInFx;
                if(videoClipFxData.getIsCartoon()){
                    builtInFx = clip.appendBuiltinFx("Cartoon");
                    if(builtInFx != null){
                        builtInFx.setBooleanVal("Stroke Only", videoClipFxData.getStrokenOnly());
                        builtInFx.setBooleanVal("Grayscale", videoClipFxData.getGrayScale());
                    }else {
                    }
                }else {
                    builtInFx = clip.appendBuiltinFx(name);
                }
                if(builtInFx != null) {
                    builtInFx.setFilterIntensity(fxIntensity);
                }else {
                }
            }else {////添加包裹特效
                NvsVideoFx packagedFx = clip.appendPackagedFx(name);
                if(packagedFx != null){
                    packagedFx.setFilterIntensity(fxIntensity);
                }else {
                }
            }
        }

        return true;
    }

    public static boolean applyTheme(NvsTimeline timeline, String themeId) {
        if(timeline == null)
            return false;

        timeline.removeCurrentTheme();
        if (themeId == null || themeId.isEmpty())
            return false;

        //设置主题片头和片尾
        String themeCaptionTitle = TimelineData.instance().getThemeCptionTitle();
        if(!themeCaptionTitle.isEmpty()){
            timeline.setThemeTitleCaptionText(themeCaptionTitle);
        }
        String themeCaptionTrailer = TimelineData.instance().getThemeCptionTrailer();
        if(!themeCaptionTrailer.isEmpty()){
            timeline.setThemeTrailerCaptionText(themeCaptionTrailer);
        }

        if(!timeline.applyTheme(themeId)) {
            Log.e(TAG, "failed to apply theme");
            return false;
        }

        timeline.setThemeMusicVolumeGain(1.0f, 1.0f);

        // 应用主题之后，要把已经应用的背景音乐去掉
        TimelineData.instance().setMusicList(null);
        TimelineUtil.buildTimelineMusic(timeline, null);
        return true;
    }

    private static boolean removeAllVideoFx(NvsVideoClip videoClip) {
        if(videoClip == null)
            return false;

        int fxCount = videoClip.getFxCount();
        for(int i=0;i<fxCount;i++) {
            NvsVideoFx fx = videoClip.getFxByIndex(i);
            if(fx == null)
                continue;

            String name = fx.getBuiltinVideoFxName();
            Log.e("===>", "fx name: " + name);
            if(name.equals(Constants.FX_COLOR_PROPERTY) || name.equals(Constants.FX_VIGNETTE) ||
                    name.equals(Constants.FX_SHARPEN)|| name.equals(Constants.FX_TRANSFORM_2D) || name.equals(Constants.AR_SCENE)) {
                continue;
            }
            videoClip.removeFx(i);
            i--;
        }
        return true;
    }

    public static boolean setTransition(NvsTimeline timeline) {
        if(timeline == null) {
            return false;
        }

        NvsVideoTrack videoTrack = timeline.getVideoTrackByIndex(0);
        if(videoTrack == null) {
            return false;
        }


        int videoClipCount = videoTrack.getClipCount();
        if(videoClipCount <= 1)
            return false;
       TransitionInfo transitionInfo;
        for(int i = 0;i<videoClipCount;i++) {
            transitionInfo= TimelineData.instance().getClipInfoData().get(i).getTransitionInfo();
            if(transitionInfo==null){
                continue;
            }else {
                Log.i("club","club:getTransitionId:"+transitionInfo.getTransitionId()+" i:"+i);
                if(transitionInfo.getTransitionMode() == TransitionInfo.TRANSITIONMODE_BUILTIN) {
                    //i-1 代表前绑定后一个视频片段
                    videoTrack.setBuiltinTransition(i-1, transitionInfo.getTransitionId());
                } else {
                    videoTrack.setPackagedTransition(i-1, transitionInfo.getTransitionId());
                }
            }

        }

        return true;
    }

    public static boolean buildTimelineMusic(NvsTimeline timeline, List<MusicInfo> musicInfos) {
        if(timeline == null) {
            return false;
        }
        NvsAudioTrack audioTrack =timeline.getAudioTrackByIndex(0);
        long videoDuration=timeline.getVideoTrackByIndex(0).getDuration();
        if(audioTrack == null) {
            return false;
        }
        if(musicInfos == null || musicInfos.isEmpty()) {
                audioTrack.removeAllClips();

            // 去掉音乐之后，要把已经应用的主题中的音乐还原
            String pre_theme_id = TimelineData.instance().getThemeData();
            if (pre_theme_id != null && !pre_theme_id.isEmpty()) {
                timeline.setThemeMusicVolumeGain(1.0f, 1.0f);
            }
            return false;
        }
            audioTrack.removeAllClips();
        for(MusicInfo oneMusic: musicInfos) {
            oneMusic.setTrimOut(videoDuration);
            if(oneMusic == null) {
                continue;
            }
            NvsAudioClip audioClip = audioTrack.addClip(oneMusic.getFilePath(), oneMusic.getInPoint(), oneMusic.getTrimIn(), oneMusic.getTrimOut());
            if(audioClip != null) {
                audioClip.setFadeInDuration(oneMusic.getFadeDuration());
                if(oneMusic.getExtraMusic() <= 0 && oneMusic.getExtraMusicLeft() <= 0) {
                    audioClip.setFadeOutDuration(oneMusic.getFadeDuration());
                }
            }
            if(oneMusic.getExtraMusic() > 0) {
                for(int i = 0; i < oneMusic.getExtraMusic(); ++i) {
                    NvsAudioClip extra_clip = audioTrack.addClip(oneMusic.getFilePath(),
                            oneMusic.getOriginalOutPoint() + i * (oneMusic.getOriginalOutPoint() - oneMusic.getOriginalInPoint()),
                            oneMusic.getOriginalTrimIn(), oneMusic.getOriginalTrimOut());
                    if(extra_clip != null) {
                        extra_clip.setAttachment(Constants.MUSIC_EXTRA_AUDIOCLIP, oneMusic.getInPoint());
                        if(i == oneMusic.getExtraMusic() - 1 && oneMusic.getExtraMusicLeft() <= 0) {
                            extra_clip.setAttachment(Constants.MUSIC_EXTRA_LAST_AUDIOCLIP, oneMusic.getInPoint());
                            extra_clip.setFadeOutDuration(oneMusic.getFadeDuration());
                        }
                    }
                }
            }
            if(oneMusic.getExtraMusicLeft() > 0) {
                NvsAudioClip extra_clip = audioTrack.addClip(oneMusic.getFilePath(),
                        oneMusic.getOriginalOutPoint() + oneMusic.getExtraMusic() * (oneMusic.getOriginalOutPoint() - oneMusic.getOriginalInPoint()),
                        oneMusic.getOriginalTrimIn(),
                        oneMusic.getOriginalTrimIn() + oneMusic.getExtraMusicLeft());
                if(extra_clip != null) {
                    extra_clip.setAttachment(Constants.MUSIC_EXTRA_AUDIOCLIP, oneMusic.getInPoint());
                    extra_clip.setAttachment(Constants.MUSIC_EXTRA_LAST_AUDIOCLIP, oneMusic.getInPoint());
                    extra_clip.setFadeOutDuration(oneMusic.getFadeDuration());
                }
            }
        }
        float audioVolume = TimelineData.instance().getMusicVolume();
        audioTrack.setVolumeGain(audioVolume,audioVolume);

        // 应用音乐之后，要把已经应用的主题中的音乐去掉
        String pre_theme_id = TimelineData.instance().getThemeData();
        if (pre_theme_id != null && !pre_theme_id.isEmpty()) {
            timeline.setThemeMusicVolumeGain(0 , 0);
        }
        return true;
    }

    public static void buildTimelineRecordAudio(NvsTimeline timeline, ArrayList<RecordAudioInfo> recordAudioInfos) {
        if(timeline == null) {
            return;
        }
        NvsAudioTrack audioTrack =timeline.getAudioTrackByIndex(1);
        if(audioTrack != null) {
            audioTrack.removeAllClips();
            if(recordAudioInfos != null) {
                for (int i = 0; i < recordAudioInfos.size(); ++i) {
                    RecordAudioInfo recordAudioInfo = recordAudioInfos.get(i);
                    if (recordAudioInfo == null) {
                        continue;
                    }
                    NvsAudioClip audioClip = audioTrack.addClip(recordAudioInfo.getPath(), recordAudioInfo.getInPoint(), recordAudioInfo.getTrimIn(),
                            recordAudioInfo.getOutPoint() - recordAudioInfo.getInPoint() + recordAudioInfo.getTrimIn());
                    if(audioClip != null) {
                        audioClip.setVolumeGain(recordAudioInfo.getVolume(), recordAudioInfo.getVolume());
                        if(recordAudioInfo.getFxID() != null && !recordAudioInfo.getFxID().equals(Constants.NO_FX)) {
                            audioClip.appendFx(recordAudioInfo.getFxID());
                        }
                    }
                }
            }
            float audioVolume = TimelineData.instance().getRecordVolume();
            audioTrack.setVolumeGain(audioVolume,audioVolume);
        }
    }

    public static boolean setSticker(NvsTimeline timeline, ArrayList<StickerInfo> stickerArray) {
        if(timeline == null)
            return false;

        NvsTimelineAnimatedSticker deleteSticker = timeline.getFirstAnimatedSticker();
        while (deleteSticker != null) {
            deleteSticker = timeline.removeAnimatedSticker(deleteSticker);
        }

        for(StickerInfo sticker : stickerArray) {
            long duration = sticker.getOutPoint() - sticker.getInPoint();
            boolean isCutsomSticker = sticker.isCustomSticker();
            NvsTimelineAnimatedSticker newSticker = isCutsomSticker ?
                    timeline.addCustomAnimatedSticker(sticker.getInPoint(),duration,sticker.getId(),sticker.getCustomImagePath())
                    : timeline.addAnimatedSticker(sticker.getInPoint(), duration, sticker.getId());
            if(newSticker == null)
                continue;
            newSticker.setZValue(sticker.getAnimateStickerZVal());
            newSticker.setHorizontalFlip(sticker.isHorizFlip());
            PointF translation = sticker.getTranslation();
            float scaleFactor = sticker.getScaleFactor();
            float rotation = sticker.getRotation();
            newSticker.setScale(scaleFactor);
            newSticker.setRotationZ(rotation);
            newSticker.setTranslation(translation);
            float volumeGain = sticker.getVolumeGain();
            newSticker.setVolumeGain(volumeGain,volumeGain);
        }
        return true;
    }

    public static boolean setCaption(NvsTimeline timeline,  ArrayList<CaptionInfo> captionArray) {
        if(timeline == null)
            return false;

        NvsTimelineCaption deleteCaption = timeline.getFirstCaption();
        while (deleteCaption != null) {
            int capCategory = deleteCaption.getCategory();
            int roleTheme = deleteCaption.getRoleInTheme();
            if(capCategory == NvsTimelineCaption.THEME_CATEGORY
                        && roleTheme != NvsTimelineCaption.ROLE_IN_THEME_GENERAL){//主题字幕不作删除
                deleteCaption = timeline.getNextCaption(deleteCaption);
            }else {
                deleteCaption = timeline.removeCaption(deleteCaption);
            }
        }

        for(CaptionInfo caption : captionArray) {
            long duration = caption.getOutPoint() - caption.getInPoint();
            NvsTimelineCaption newCaption = timeline.addCaption(caption.getText(), caption.getInPoint(),
                    duration,null);
            updateCaptionAttribute(newCaption,caption);
        }
        return true;
    }

    private static void updateCaptionAttribute(NvsTimelineCaption newCaption,CaptionInfo caption){
        if(newCaption == null || caption == null)
            return;

        //字幕StyleUuid需要首先设置，后面设置的字幕属性才会生效，
        // 因为字幕样式里面可能自带偏移，缩放，旋转等属性，最后设置会覆盖前面的设置的。
        String styleUuid = caption.getCaptionStyleUuid();
        newCaption.applyCaptionStyle(styleUuid);

        int alignVal = caption.getAlignVal();
        if(alignVal >= 0)
            newCaption.setTextAlignment(alignVal);
        NvsColor textColor = ColorUtil.colorStringtoNvsColor(caption.getCaptionColor());
        if(textColor != null){
            textColor.a = caption.getCaptionColorAlpha() / 100.0f;
            newCaption.setTextColor(textColor);
        }

        // 放缩字幕
        float scaleFactorX = caption.getScaleFactorX();
        float scaleFactorY = caption.getScaleFactorY();
        newCaption.setScaleX(scaleFactorX);
        newCaption.setScaleY(scaleFactorY);

        float rotation = caption.getRotation();
        // 旋转字幕
        newCaption.setRotationZ(rotation);
        newCaption.setZValue(caption.getCaptionZVal());
        boolean hasOutline = caption.isHasOutline();
        newCaption.setDrawOutline(hasOutline);
        if(hasOutline){
            NvsColor outlineColor = ColorUtil.colorStringtoNvsColor(caption.getOutlineColor());
            if(outlineColor != null){
                outlineColor.a = caption.getOutlineColorAlpha() / 100.0f;
                newCaption.setOutlineColor(outlineColor);
                newCaption.setOutlineWidth(caption.getOutlineWidth());
            }
        }
        String fontPath = caption.getCaptionFont();
        if(!fontPath.isEmpty())
            newCaption.setFontByFilePath(fontPath);
        boolean isBold = caption.isBold();

        newCaption.setBold(isBold);
        boolean isItalic = caption.isItalic();
        newCaption.setItalic(isItalic);
        boolean isShadow = caption.isShadow();
        newCaption.setDrawShadow(isShadow);
        if(isShadow) {
            PointF offset = new PointF(7, -7);
            NvsColor shadowColor = new NvsColor(0, 0, 0, 0.5f);
            newCaption.setShadowOffset(offset);  //字幕阴影偏移量
            newCaption.setShadowColor(shadowColor); // 字幕阴影颜色
        }
        float fontSize = caption.getCaptionSize();
        if(fontSize >= 0)
            newCaption.setFontSize(fontSize);
        PointF translation = caption.getTranslation();
        if(translation != null)
            newCaption.setCaptionTranslation(translation);
    }

    public static NvsTimeline newTimeline(NvsVideoResolution videoResolution){
        NvsStreamingContext context = NvsStreamingContext.getInstance();
        if(context == null) {
            Log.e(TAG, "failed to get streamingContext");
            return null;
        }
        if(videoResolution==null){
            String path="";
            if(TimelineData.instance().getClipInfoData().size()>0){
                path=TimelineData.instance().getClipInfoData().get(0).getFilePath();
            }
            videoResolution=Util.getVideoEditResolution(NvAsset.AspectRatio_9v16,path);
        }
        NvsVideoResolution videoEditRes = videoResolution;
        videoEditRes.imagePAR = new NvsRational(1, 1);
        NvsRational videoFps = new NvsRational(30, 1);

        NvsAudioResolution audioEditRes = new NvsAudioResolution();
        audioEditRes.sampleRate = 44100;
        audioEditRes.channelCount = 2;

        NvsTimeline timeline = context.createTimeline(videoEditRes, videoFps, audioEditRes);
        return timeline;
    }

    public static NvsSize getTimelineSize(NvsTimeline timeline) {
        NvsSize size = new NvsSize(0, 0);
        if(timeline != null) {
            NvsVideoResolution resolution = timeline.getVideoRes();
            size.width = resolution.imageWidth;
            size.height = resolution.imageHeight;
            return size;
        }
        return null;
    }

    public static void checkAndDeleteExitFX(NvsTimeline mTimeline) {
        NvsTimelineVideoFx nvsTimelineVideoFx = mTimeline.getFirstTimelineVideoFx();
        while (nvsTimelineVideoFx != null) {
            String name = nvsTimelineVideoFx.getBuiltinTimelineVideoFxName();
            if (name.equals(WATERMARK_DYNAMICS_FXNAME)){
                mTimeline.removeTimelineVideoFx(nvsTimelineVideoFx);
                break;
            }else {
                nvsTimelineVideoFx = mTimeline.getNextTimelineVideoFx(nvsTimelineVideoFx);
            }
        }
    }


    /**
     * 添加贴纸
     * @param trimIn  显示时间  秒
     * @param duration 持续时间   秒
     * @param uuid   美摄的贴纸uuid
     * @param localImgPath 本地图片地址
     * @return
     */
    public static NvsTimelineAnimatedSticker addSticker(NvsTimeline timeline, int trimIn, int duration, String uuid, String localImgPath){
        NvAssetManager mAssetManager = NvAssetManager.sharedInstance();
        mAssetManager.searchLocalAssets(NvAsset.ASSET_ANIMATED_STICKER);
        String bundlePath = "sticker";
        mAssetManager.searchReservedAssets(NvAsset.ASSET_ANIMATED_STICKER, bundlePath);

        NvsTimelineAnimatedSticker timelineAnimatedSticker = timeline.addCustomAnimatedSticker(trimIn * Constants.NS_TIME_BASE, duration* Constants.NS_TIME_BASE,
                uuid, localImgPath);
        if(timelineAnimatedSticker !=null){
            timelineAnimatedSticker.setZValue(getCurAnimateStickerZVal(timeline));
        }
        return timelineAnimatedSticker;
    }

    /**
     *TimeLine添加字幕
     * @param text
     * @param trimIn  显示时间  秒
     * @param duration 持续时间   秒
     * @param position x:0.0--1.0     y:0.0--1.0
     */
    public static NvsTimelineCaption addCaption(NvsTimeline timeline, NvsLiveWindow liveWindow, String text, float trimIn, float duration, PointF position, int fontSize) {
        NvsTimelineCaption mTimelineCaption = timeline.addCaption(text,(long)trimIn * Constants.NS_TIME_BASE, (long)duration * Constants.NS_TIME_BASE,null);
        if(mTimelineCaption==null){
            return null;
        }
        mTimelineCaption.setFontSize(fontSize);
        float fontHeightHalf = fontSize*1.0F/2;
        float fontLengthHalf = fontSize*1.0F*text.length()/2+10;
        //上下左右边界计算
        if(position.x*liveWindow.getWidth()<fontLengthHalf){
            position.x = fontLengthHalf/liveWindow.getWidth();
        }
        if(position.x*liveWindow.getWidth()+fontLengthHalf > liveWindow.getWidth()){
            position.x = (liveWindow.getWidth()-fontLengthHalf)/liveWindow.getWidth();
        }
        if(position.y*liveWindow.getHeight()<fontHeightHalf){
            position.y = fontHeightHalf/liveWindow.getHeight();
        }
        if(position.y*liveWindow.getHeight()+fontHeightHalf>liveWindow.getHeight()){
            position.y = (liveWindow.getHeight()-fontHeightHalf)/liveWindow.getHeight();
        }
        //坐标转换
        PointF pre = liveWindow.mapViewToCanonical(new PointF(liveWindow.getWidth()/2f, liveWindow.getHeight()/2f));
//        PointF p = liveWindow.mapViewToCanonical(new PointF(position.x*liveWindow.getWidth()-(int)mTimelineCaption.getFontSize()*text.length()/2, position.y*liveWindow.getHeight()+(int)mTimelineCaption.getFontSize()/2));
        PointF p = liveWindow.mapViewToCanonical(new PointF(position.x*liveWindow.getWidth(), position.y*liveWindow.getHeight()));
        PointF timeLinePointF = new PointF(p.x - pre.x, p.y - pre.y);
        LoggerUtils.printLog(pre.toString()+"---"+p.toString()+"---"+timeLinePointF.toString());
//        mTimelineCaption.setCaptionTranslation(timeLinePointF);
        mTimelineCaption.translateCaption(timeLinePointF);
        mTimelineCaption.setZValue(getCurAnimateStickerZVal(timeline));
        return mTimelineCaption;
    }

    /**
     * 获取timeline 的z轴最大值
     * @param timeline
     * @return
     */
    public  static float getCurAnimateStickerZVal(NvsTimeline timeline) {
        float zVal = 0.0f;
        NvsTimelineAnimatedSticker animatedSticker = timeline.getFirstAnimatedSticker();
        while (animatedSticker != null) {
            float tmpZVal = animatedSticker.getZValue();
            if (tmpZVal > zVal)
                zVal = tmpZVal;
            animatedSticker = timeline.getNextAnimatedSticker(animatedSticker);
        }
        zVal += 1.0;
        return zVal;
    }

}
