package com.test.xcamera.phonealbum.widget;

import android.content.Context;
import android.graphics.PointF;
import android.text.TextUtils;
import android.util.Log;


import com.editvideo.Constants;
import com.editvideo.MediaConstant;

import com.editvideo.ParameterSettingValues;
import com.editvideo.TimelineUtil;
import com.editvideo.Util;
import com.editvideo.dataInfo.ClipInfo;
import com.editvideo.dataInfo.CompoundCaptionInfo;
import com.editvideo.dataInfo.MusicInfo;
import com.editvideo.dataInfo.StickerInfo;
import com.editvideo.dataInfo.TimelineData;
import com.editvideo.dataInfo.TransitionInfo;
import com.test.xcamera.application.AppContext;
import com.test.xcamera.phonealbum.bean.MusicBean;
import com.test.xcamera.phonealbum.bean.ThumbnailBean;
import com.test.xcamera.phonealbum.bean.VideoFrameBean;
import com.test.xcamera.phonealbum.bean.VideoTimeLineBean;
import com.test.xcamera.phonealbum.widget.subtitle.bean.Mark;
import com.test.xcamera.phonealbum.widget.subtitle.bean.MovieSubtitleTimeline;
import com.test.xcamera.phonealbum.widget.subtitle.bean.StaticRuneMark;
import com.test.xcamera.phonealbum.widget.subtitle.bean.StickerTimeline;
import com.test.xcamera.phonealbum.widget.subtitle.bean.TitleSubtitleMark;
import com.test.xcamera.utils.AppExecutors;
import com.test.xcamera.utils.FileUtils;
import com.test.xcamera.utils.LoggerUtils;
import com.test.xcamera.utils.PUtil;
import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsColor;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineAnimatedSticker;
import com.meicam.sdk.NvsTimelineCaption;
import com.meicam.sdk.NvsTimelineCompoundCaption;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoTrack;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class VideoEditManger {
    public static long mCurrentTime=0;
    public static  int mVideoWidth = 0;
    public static  int mVideoHeight = 0;
    //视频每帧宽度
    public static final int VIDEO_FRAME_WIDTH = 80;

    public static final int CAPTION_FONT_SIZE_60 = 60;
    public static final int CAPTION_FONT_SIZE_80 = 80;

    public static final int TRIM_DURATION = 100;
    //视频每帧缩略图的代表的时长默认1秒
    public static float VIDEO_FRAME_TIME = 1.0f;
    //视频默认微妙秒
    public static long VIDEO_microsecond_TIME = 1000 * 1000;
    public static NvsVideoTrack mVideoTrack;

    public static float getVideoDuration(){
        if(mVideoTrack!=null){
            long duration=mVideoTrack.getDuration();
            if(duration!=0){
                return (float) mVideoTrack.getDuration()/VIDEO_microsecond_TIME;
            }else {
                return 100000000;
            }
        }
        return 100000000;
    }

    /**
     * 滚动的距离转时间
     *
     * @param context
     * @param width
     * @param videoTrack
     * @return
     */
    public static long tranWidthToTime(Context context, int width, NvsVideoTrack videoTrack) {
        long time = 0;

        float second = (float) width / PUtil.dip2px(context, VIDEO_FRAME_WIDTH);
        time = (long) (second * VIDEO_microsecond_TIME);
        if (time < 0) {
            time = 0;
        } else if (time > videoTrack.getDuration()) {
            time = videoTrack.getDuration();
        }
        return time;
    }
    public static int tranTimeToWidth(Context context, long time, NvsVideoTrack videoTrack) {
        if (time < 0) {
            time = 0;
        } else if (time > videoTrack.getDuration()) {
            time = videoTrack.getDuration();
        }
        float secondTime=(float) time/VIDEO_microsecond_TIME;
        int width=(int)(secondTime*PUtil.dip2px(context, VIDEO_FRAME_WIDTH));
        return width;

    }

    public static List<VideoTimeLineBean> getVideoTimeLineBeanList(Context context, NvsVideoTrack videoTrack) {
        mVideoTrack=videoTrack;
        int width = PUtil.dip2px(context, VIDEO_FRAME_WIDTH);
        int halfScreen = PUtil.getScreenW(context) / 2;
        long totalDuration = videoTrack.getDuration();
        int frameCount = (int) ((float)totalDuration / VIDEO_microsecond_TIME);
        float restValue =  (float)totalDuration % VIDEO_microsecond_TIME;
        boolean isRest = false;
        //如果小于100毫秒
        if (restValue > restValue / 10) {
            frameCount++;
            isRest = true;
        }
        List<VideoTimeLineBean> thumbnailBeans = new ArrayList<>();
        for (int i = 0; i < frameCount; i++) {
            VideoTimeLineBean thumbnailBean = new VideoTimeLineBean();
            if (i == frameCount - 1 && isRest) {
//                int frameWidth = (int) (width * (restValue / VIDEO_microsecond_TIME));
                thumbnailBean.setTime(VIDEO_microsecond_TIME*(i)+restValue);
//                thumbnailBean.setTime(VIDEO_microsecond_TIME);
                thumbnailBean.setViewStartIndex(width);
                thumbnailBean.setViewFrameWidth(width);
            } else {
                thumbnailBean.setTime(VIDEO_microsecond_TIME*i);
                thumbnailBean.setViewStartIndex(width);
                thumbnailBean.setViewFrameWidth(width);
            }
            thumbnailBeans.add(thumbnailBean);

        }
        VideoTimeLineBean thumbnailBeanStart = new VideoTimeLineBean();
        thumbnailBeanStart.setEmptyView(true);
        thumbnailBeanStart.setViewStartIndex(halfScreen);
        thumbnailBeanStart.setViewFrameWidth(halfScreen);
        thumbnailBeans.add(0, thumbnailBeanStart);
        VideoTimeLineBean thumbnailBeanEnd = new VideoTimeLineBean();
        thumbnailBeanEnd.setEmptyView(true);
        thumbnailBeanEnd.setViewStartIndex(halfScreen);
        thumbnailBeanEnd.setViewFrameWidth(halfScreen);
        thumbnailBeans.add(thumbnailBeanEnd);
        return thumbnailBeans;
    }

    /**
     * 抽取指定片段的视频帧
     */
    public static List<ThumbnailBean> getFrameInVideoWithMeisheSDK(Context context, NvsVideoTrack videoTrack,int selectedPosition) {
        mVideoTrack=videoTrack;

        int halfScreen = PUtil.getScreenW(context) / 2;
        int width = PUtil.dip2px(context, VIDEO_FRAME_WIDTH);
        List<ThumbnailBean> beanList = new ArrayList<>();
        List<ClipInfo> clipInfoList = TimelineData.instance().getClipInfoData();

        for (int i = 0; i < videoTrack.getClipCount(); i++) {
            ThumbnailBean thumbnailBean = new ThumbnailBean();
            int clipTotalWidth = 0;
            ClipInfo mediaData = clipInfoList.get(i);
            NvsVideoClip videoClip = videoTrack.getClipByIndex(i);
            long duration;
            if(videoClip!=null){
                duration = ((videoClip.getOutPoint() - videoClip.getInPoint()));
            }else {
                duration=mediaData.getDuration();
            }
            final List<VideoFrameBean> frameBeans = new ArrayList<>();
            final List<Object> imgList = new ArrayList<>();
            int frameCount = (int) (duration / VIDEO_microsecond_TIME);
            float restValue = (float) (duration % VIDEO_microsecond_TIME);
            boolean isRest = false;
            //如果小于100毫秒
            if (restValue > restValue / 10) {
                frameCount++;
                isRest = true;
            }
//            //图片默认4秒
//            if(mediaData.getDuration()>0){
//                frameCount=(int) (4/videoTrack.getClipByIndex(i).getSpeed());
//            }
            float itemWidth=0;
            for (int j = 0; j < frameCount; j++) {
                imgList.add(mediaData.getFilePath());
                VideoFrameBean bean = new VideoFrameBean();
                if (j == frameCount - 1 && isRest) {
                    int frameWidth = (int) (width * (restValue / VIDEO_microsecond_TIME));
                    bean.setFrameWidth(frameWidth);
                } else {
                    bean.setFrameWidth(width);
                }
                clipTotalWidth = clipTotalWidth + bean.getFrameWidth();
                bean.setMediaPath(mediaData.getFilePath());
                bean.setTime(mediaData.getTrimIn()+j * VIDEO_microsecond_TIME);
                bean.setType(mediaData.getDuration() > 0 ? MediaConstant.VIDEO : MediaConstant.IMAGE);
                itemWidth=itemWidth+bean.getFrameWidth();
                frameBeans.add(bean);

            }
            thumbnailBean.setMediaPath(mediaData.getFilePath());
            thumbnailBean.setType(mediaData.getDuration() > 0 ? MediaConstant.VIDEO : MediaConstant.IMAGE);
            thumbnailBean.setItemWidth((int) itemWidth);
            thumbnailBean.setImgList(imgList);
            thumbnailBean.setVideoFrameList(frameBeans);
            thumbnailBean.setViewStartIndex(clipTotalWidth);
            thumbnailBean.setTrimInTime(mediaData.getTrimIn()>0?mediaData.getTrimIn():0);
            if(selectedPosition==i){
                thumbnailBean.setSelected(true);
            }
            beanList.add(thumbnailBean);
        }
        ThumbnailBean thumbnailBeanStart = new ThumbnailBean();
        thumbnailBeanStart.setEmptyView(true);
        thumbnailBeanStart.setViewStartIndex(halfScreen);
        thumbnailBeanStart.setItemWidth(halfScreen);
        beanList.add(0, thumbnailBeanStart);
        ThumbnailBean thumbnailBeanEnd = new ThumbnailBean();
        thumbnailBeanEnd.setEmptyView(true);
        thumbnailBeanEnd.setViewStartIndex(halfScreen);
        thumbnailBeanEnd.setItemWidth(halfScreen);

        beanList.add(thumbnailBeanEnd);
        return beanList;
    }
    /**
     * 抽取指定片段的视频帧
     */
    public static List<ThumbnailBean> getFrameInVideoWithMeisheSDK3(Context context, NvsVideoTrack videoTrack) {
        mVideoTrack=videoTrack;

        int halfScreen = PUtil.getScreenW(context) / 2;
        int width = PUtil.dip2px(context, VIDEO_FRAME_WIDTH);
        List<ThumbnailBean> beanList = new ArrayList<>();
        List<ClipInfo> clipInfoList = TimelineData.instance().getClipInfoData();

        for (int i = 0; i < clipInfoList.size(); i++) {
            ClipInfo mediaData = clipInfoList.get(i);
            NvsVideoClip videoClip = videoTrack.getClipByIndex(i);
            long duration;
            if(videoClip!=null){
                duration = ((videoClip.getOutPoint() - videoClip.getInPoint()));
            }else {
                duration=mediaData.getDuration();
            }
            int frameCount = (int) (duration / VIDEO_microsecond_TIME);
            float restValue = (float) (duration % VIDEO_microsecond_TIME);
            boolean isRest = false;
            //如果小于100毫秒
            if (restValue > restValue / 10) {
                frameCount++;
                isRest = true;
            }

            for (int j = 0; j < frameCount; j++) {
                ThumbnailBean thumbnailBean = new ThumbnailBean();
                thumbnailBean.setClipPosition(i);
                if(j==0){
                    thumbnailBean.setClipStart(true);
                }
                if(j==frameCount-1){
                    thumbnailBean.setClipEnd(true);
                }
                if (i == 0) {
                    thumbnailBean.setSelected(true);
                }
                if (j == frameCount - 1 && isRest) {
                    int frameWidth = (int) (width * (restValue / VIDEO_microsecond_TIME));
                    thumbnailBean.setItemWidth( frameWidth);
                    thumbnailBean.setViewStartIndex(frameWidth);
                    thumbnailBean.setFrameWidth(frameWidth);
                } else {
                    thumbnailBean.setItemWidth(width);
                    thumbnailBean.setViewStartIndex(width);
                    thumbnailBean.setFrameWidth(width);
                }
                thumbnailBean.setMediaPath(mediaData.getFilePath());
                double speed=1;
                if(videoTrack!=null&&videoTrack.getClipByIndex(i)!=null){
                    speed=videoTrack.getClipByIndex(i).getSpeed();
                }
                thumbnailBean.setTime((long)((mediaData.getTrimIn()+j * VIDEO_microsecond_TIME)*speed));
                thumbnailBean.setType(mediaData.getDuration() > 0 ? MediaConstant.VIDEO : MediaConstant.IMAGE);
                beanList.add(thumbnailBean);

            }

        }
        ThumbnailBean thumbnailBeanStart = new ThumbnailBean();
        thumbnailBeanStart.setEmptyView(true);
        thumbnailBeanStart.setViewStartIndex(halfScreen);
        thumbnailBeanStart.setItemWidth(halfScreen);
        beanList.add(0, thumbnailBeanStart);
        ThumbnailBean thumbnailBeanEnd = new ThumbnailBean();
        thumbnailBeanEnd.setEmptyView(true);
        thumbnailBeanEnd.setViewStartIndex(halfScreen);
        thumbnailBeanEnd.setItemWidth(halfScreen);

        beanList.add(thumbnailBeanEnd);
        return beanList;
    }

    /**
     * 倒叙后重新修改trimIn 和trimOut
     * @param clipInfo
     */
    public static void reConvertTrimInAndTrimOut(ClipInfo clipInfo){
        long duration=getVideoDuration(clipInfo);
        long out=duration;
        if(clipInfo.getTrimOut()>0&&clipInfo.getTrimOut()>clipInfo.getTrimIn()){
            out=clipInfo.getTrimOut();
            if(out>duration){
                out=duration;
            }
        }
        long trimIn=duration-out;
        if(trimIn<0){
            trimIn=0;
        }
        long trimOut=trimIn+(out-clipInfo.getTrimIn());
        if(trimOut>duration){
            trimOut=duration;
        }
        LoggerUtils.printLog("club:out:"+out
                        +" trimIn:"+trimIn
                        +" trimOut:"+trimOut
                        +" getDuration:"+getVideoDuration(clipInfo)
                        +" clipInfo.getTrimIn():"+clipInfo.getTrimIn()
                );
        clipInfo.changeTrimIn(trimIn);
        clipInfo.changeTrimOut(trimOut);
    }
    public static long getVideoDuration(ClipInfo clip) {
        NvsAVFileInfo info = null;
        long duration = clip.getDuration();
        if (NvsStreamingContext.getInstance() != null) {
            info = NvsStreamingContext.getInstance().getAVFileInfo(clip.getFilePath());
        }
        if (info != null) {
            duration = info.getDuration();
        }
        return duration;
    }
    /**
     * 视屏分割
     *
     * @param mSelectedItemPosition
     * @param curTime               分割线时间点
     * @param timeline
     */
    public static boolean setVideoSplit(int mSelectedItemPosition, long curTime, NvsTimeline timeline, NvsVideoTrack videoTrack) {
        mVideoTrack=videoTrack;

        NvsVideoClip videoClip = videoTrack.getClipByIndex(mSelectedItemPosition);
        //只有视频做分割，图片不做分割
        ArrayList<ClipInfo> mClipArrayList = TimelineData.instance().getClipInfoData();
        float speed = mClipArrayList.get(mSelectedItemPosition).getSpeed();
        speed = speed <= 0 ? 1.0f : speed;
        ClipInfo clipInfo = mClipArrayList.get(mSelectedItemPosition);

        long newSpiltPoint = (long)(((curTime - videoClip.getInPoint())*speed + clipInfo.getTrimIn()));
        Log.i("club","VideoSplit_curTime:"+(float)curTime/VIDEO_microsecond_TIME
                +" InPoint:"+(float)videoClip.getInPoint()/VIDEO_microsecond_TIME
                +" OutPoint:"+(float)videoClip.getOutPoint()/VIDEO_microsecond_TIME
                +" TrimIn:"+(float)videoClip.getTrimIn()/VIDEO_microsecond_TIME
                +" TrimOut:"+(float)videoClip.getTrimOut()/VIDEO_microsecond_TIME
                +" clipTrimIn:"+ (float)clipInfo.getTrimIn()/VIDEO_microsecond_TIME
                +" clipTrimOut:"+ (float)clipInfo.getTrimOut()/VIDEO_microsecond_TIME
                +" newSpiltPoint:"+ (float)newSpiltPoint/VIDEO_microsecond_TIME
                );
        ClipInfo clipInfoFst = mClipArrayList.get(mSelectedItemPosition).clone();
        clipInfoFst.changeTrimOut(newSpiltPoint);
        ClipInfo clipInfoSec = mClipArrayList.get(mSelectedItemPosition).clone();
        clipInfoSec.changeTrimIn(newSpiltPoint);
        if(clipInfoFst.getTrimOut()-videoClip.getTrimIn()<200000||videoClip.getTrimOut()-clipInfoSec.getTrimIn()<200000){
            return false;
        }
        mClipArrayList.remove(mSelectedItemPosition);
        mClipArrayList.add(mSelectedItemPosition, clipInfoSec);
        mClipArrayList.add(mSelectedItemPosition, clipInfoFst);
        TimelineData.instance().setClipInfoData(mClipArrayList);
        TimelineUtil.reBuildVideoTrack(timeline);
         return true;
    }
    public static boolean setVideoCutX(int mSelectedItemPosition, float in,float out,boolean isUp, NvsTimeline timeline, NvsVideoTrack videoTrack) {
        long trimIn=tranWidthToTime(AppContext.getInstance(),(int)in,videoTrack);
        long trimOut=tranWidthToTime(AppContext.getInstance(),(int)out,videoTrack);
        return setVideoCut(mSelectedItemPosition, trimIn,trimOut, isUp, timeline, videoTrack);
    }
    public static boolean setVideoCut(int mSelectedItemPosition, long trimIn,long trimOut,boolean isUp, NvsTimeline timeline, NvsVideoTrack videoTrack) {
//        NvsVideoClip videoClip = videoTrack.getClipByIndex(mSelectedItemPosition);
//        videoClip.changeTrimInPoint(trimIn,true);
//        videoClip.changeTrimOutPoint(trimOut,true);
        ArrayList<ClipInfo> mClipArrayList = TimelineData.instance().getClipInfoData();
        ClipInfo clipInfo = mClipArrayList.get(mSelectedItemPosition);
        clipInfo.changeTrimIn(trimIn);
        clipInfo.changeTrimOut(trimOut);
        if(!isUp){
        }
        TimelineUtil.reBuildVideoTrack(timeline);

        return true;
    }

    /**
     * 视屏复制
     *
     * @param mSelectedItemPosition
     * @param timeline
     */
    public static void setVideoCopy(int mSelectedItemPosition, NvsTimeline timeline) {
        ArrayList<ClipInfo> videoClipArray = TimelineData.instance().getClipInfoData();
        videoClipArray.add(mSelectedItemPosition, videoClipArray.get(mSelectedItemPosition).clone());
        TimelineData.instance().setClipInfoData(videoClipArray);
        TimelineUtil.reBuildVideoTrack(timeline);
    }

    /**
     * 视屏移动
     *
     * @param fromPosition
     * @param toPosition
     * @param timeline
     */
    public static void setVideoMove(int fromPosition, int toPosition, NvsTimeline timeline) {
        ArrayList<ClipInfo> videoClipArray = TimelineData.instance().getClipInfoData();

        if (fromPosition < 0 || fromPosition >= videoClipArray.size()) {
            return;
        }
        if (toPosition < 0 || toPosition >= videoClipArray.size()) {
            return;
        }
        if (fromPosition == toPosition) {
            return;
        }
//        Collections.swap(videoClipArray, fromPosition, toPosition);
        TimelineData.instance().setClipInfoData(videoClipArray);
        TimelineUtil.reBuildVideoTrack(timeline);
    }

    /**
     * 视屏删除
     *
     * @param mSelectedItemPosition
     * @param timeline
     */
    public static void setVideoDel(int mSelectedItemPosition, NvsTimeline timeline) {

        Log.i("club", "club_setVideoDel:mSelectedItemPosition_" + mSelectedItemPosition);
        ArrayList<ClipInfo> videoClipArray = TimelineData.instance().getClipInfoData();
        if (mSelectedItemPosition < 0 || mSelectedItemPosition >= videoClipArray.size()) {
            return;
        }
        videoClipArray.remove(mSelectedItemPosition);
        TimelineData.instance().setClipInfoData(videoClipArray);
        TimelineUtil.reBuildVideoTrack(timeline);
    }

    /**
     * 视屏转场设置
     * @param mSelectedItemPosition 设置单个片段转场效果、等-1时会设置全部片段的转场
     * @param timeline
     * @param transitionInfo
     */
    public static void setVideoTransition(int mSelectedItemPosition, NvsTimeline timeline, TransitionInfo transitionInfo) {
        ArrayList<ClipInfo> videoClipArray = TimelineData.instance().getClipInfoData();
        if(transitionInfo==null){
            TimelineUtil.setTransition(timeline);
        }else {
            if (mSelectedItemPosition==-1) {
                for (int i = 0; i < videoClipArray.size(); i++) {
                    TransitionInfo transitionNew=new TransitionInfo();
                    transitionNew.setTransitionId(transitionInfo.getTransitionId());
                    transitionNew.setTransitionMode(transitionInfo.getTransitionMode());
                    ClipInfo info = videoClipArray.get(i);
                    info.setTransitionInfo(transitionNew);
                }
                TimelineData.instance().setTransitionData(transitionInfo);
            } else {
                ClipInfo info = videoClipArray.get(mSelectedItemPosition);
                info.setTransitionInfo(transitionInfo);

                TimelineData.instance().setTransitionData(null);
            }
        }
        TimelineUtil.setTransition(timeline);

    }
    public static void addStickerAll(NvsTimeline timeline){
        for (Mark mark: StickerTimeline.getMarkList()){
            StaticRuneMark subtitleMark=(StaticRuneMark) mark;
            addSticker(timeline,subtitleMark);
        }
    }
    public static NvsTimelineAnimatedSticker  addSticker(NvsTimeline timeline, StaticRuneMark staticRuneMark){
        long startTime=(long)(staticRuneMark.getStart()/10 * Constants.NS_TIME_BASE);
        long duration=(long)(staticRuneMark.getDuration()/10 * Constants.NS_TIME_BASE);
        if(duration<TRIM_DURATION){
            duration=TRIM_DURATION;
        }
        NvsTimelineAnimatedSticker timelineAnimatedSticker = timeline.addCustomAnimatedSticker(startTime, duration, staticRuneMark.getId(), staticRuneMark.getStickerPath());
        if(timelineAnimatedSticker !=null){
            timelineAnimatedSticker.setZValue(TimelineUtil.getCurAnimateStickerZVal(timeline));
        }
        return timelineAnimatedSticker;
    }
    /**
     * 添加组合贴纸
     * @param timeline
     * @param captionInfo
     * @return
     */
    public static NvsTimelineAnimatedSticker addComSticker(NvsTimeline timeline, StickerInfo captionInfo){
        NvsTimelineAnimatedSticker  comCaption = timeline.addAnimatedSticker(captionInfo.getInPoint(), captionInfo.getOutPoint()-captionInfo.getInPoint(), captionInfo.getId());
        if(comCaption!=null){
            comCaption.setZValue(captionInfo.getAnimateStickerZVal());
            return comCaption;
        }else {
            return null;
        }

    }
    /**
     * 删除组合贴纸
     * @param timeline
     * @param compoundCaption
     * @return
     */
    public static boolean  delComSticker(NvsTimeline timeline, NvsTimelineAnimatedSticker compoundCaption) {
        if(timeline==null||compoundCaption==null){
            return false;
        }
        timeline.removeAnimatedSticker(compoundCaption);
        return true;
    }


    /**
     * 查找当前贴纸
     * @param timeline
     * @param captionInfo
     * @return
     */
    public static NvsTimelineAnimatedSticker  getNvsNvsTimelineSticker(NvsTimeline timeline, StickerInfo captionInfo) {
        if(timeline==null){
            return null;
        }
        float zVal = captionInfo.getAnimateStickerZVal();
        NvsTimelineAnimatedSticker caption = timeline.getFirstAnimatedSticker();
        while (caption != null) {
            float tmpZVal = caption.getZValue();
            if (tmpZVal ==zVal){
                return caption;
            }
            caption = timeline.getNextAnimatedSticker(caption);
        }
        return null;
    }
    /**
     * 添加组合字幕
     * @param timeline
     * @param captionInfo
     * @return
     */
    public static NvsTimelineCompoundCaption addCompoundCaption(NvsTimeline timeline, CompoundCaptionInfo captionInfo){
        NvsTimelineCompoundCaption  comCaption = timeline.addCompoundCaption(captionInfo.getInPoint(), captionInfo.getOutPoint()-captionInfo.getInPoint(), captionInfo.getCaptionStyleUuid());
        if(comCaption!=null){
            comCaption.setZValue(captionInfo.getCaptionZVal());
            return comCaption;
        }else {
            return null;
        }

    }

    /**
     * 删除组合字幕
     * @param timeline
     * @param compoundCaption
     * @return
     */
    public static boolean  delCompoundCaption(NvsTimeline timeline, NvsTimelineCompoundCaption compoundCaption) {
        if(timeline==null||compoundCaption==null){
            return false;
        }
        timeline.removeCompoundCaption(compoundCaption);
        return true;
    }


        /**
         * 查找当前字幕
         * @param timeline
         * @param captionInfo
         * @return
         */
    public static NvsTimelineCompoundCaption  getNvsNvsTimelineCompoundCaption(NvsTimeline timeline, CompoundCaptionInfo captionInfo) {
        if(timeline==null){
            return null;
        }
        float zVal = captionInfo.getCaptionZVal();
        NvsTimelineCompoundCaption caption = timeline.getFirstCompoundCaption();
        while (caption != null) {
            float tmpZVal = caption.getZValue();
            if (tmpZVal ==zVal){
                return caption;
            }
            caption = timeline.getNextCaption(caption);
        }
        return null;
    }
    /**
     * 添加所有字幕
     * @param timeline
     */
    public static void addSubTitleAll(NvsTimeline timeline){
        for (Mark mark: MovieSubtitleTimeline.getMarkList()){
            TitleSubtitleMark subtitleMark=(TitleSubtitleMark) mark;
            addSubTitle(timeline,subtitleMark);
        }
    }

    /**
     * 添加字幕
     * @param timeline
     * @param subtitleMark
     * @return
     */
    public static NvsTimelineCaption  addSubTitle(NvsTimeline timeline, TitleSubtitleMark subtitleMark){
        long startTime=(long)(subtitleMark.getStart()/10 * Constants.NS_TIME_BASE);
        long duration=(long)(subtitleMark.getDuration()/10 * Constants.NS_TIME_BASE);
        if(duration<TRIM_DURATION){
            duration=TRIM_DURATION;
        }
        NvsTimelineCaption caption=timeline.addCaption(subtitleMark.getText(),startTime, duration,null);
        NvsColor color=new NvsColor(255,255,255,1);
        int fontSize=CAPTION_FONT_SIZE_60;
        int compileRes = ParameterSettingValues.instance().getCompileVideoRes();
        if(compileRes==Util.VIDEO_RES_1440){
            fontSize=CAPTION_FONT_SIZE_80;
        }
        if(caption!=null&&color!=null){
            caption.setTextColor(color);
            caption.setFontSize(fontSize);
            caption.setZValue(subtitleMark.getZValue());
            List<PointF> list = caption.getBoundingRectangleVertices();
            if(list == null || list.size() < 4)
                return null;
            Collections.sort(list, new Util.PointYComparator());
            float y_dis = list.get(3).y - list.get(0).y;

            float yOffset = -(timeline.getVideoRes().imageHeight/2 + list.get(3).y - y_dis-20);
            caption.translateCaption(new PointF(0,yOffset));
        }

        return caption;
    }
    public static float getCurCaptionZVal(NvsTimeline timeline) {
        float zVal = 0.0f;
        NvsTimelineCaption caption = timeline.getFirstCaption();
        while (caption != null) {
            float tmpZVal = caption.getZValue();
            if (tmpZVal > zVal)
                zVal = tmpZVal;
            caption = timeline.getNextCaption(caption);
        }
        zVal += 1.0;
        return zVal;
    }
    public static void   delSubTitle(NvsTimeline timeline, TitleSubtitleMark subtitleMark){
        float startTime=subtitleMark.getStart()+1;
        if(startTime>subtitleMark.getEnd()){
            startTime=subtitleMark.getEnd();
        }
        long time=(long)(startTime/10f * Constants.NS_TIME_BASE);
        NvsTimelineCaption caption=getNvsTimelineCaption(timeline,subtitleMark);
        Log.i("club","clubSubTitle_Del:："+subtitleMark);
        if(caption==null){
            return;
        }
        timeline.removeCaption(caption);
    }
    public static NvsTimelineCaption   updateSubTitle(NvsTimelineCaption timelineCaption, TitleSubtitleMark subtitleMark){
        if(timelineCaption==null){
            return null;
        }
        timelineCaption.setText(subtitleMark.getText());
        long startTime=(long)(subtitleMark.getStart()/10 * Constants.NS_TIME_BASE);
        long endTime=(long)(subtitleMark.getEnd()/10 * Constants.NS_TIME_BASE);
        timelineCaption.changeInPoint(startTime);
        timelineCaption.changeOutPoint(endTime);
        return timelineCaption;
    }

    public static NvsTimelineCaption  getNvsTimelineCaption(NvsTimeline timeline, TitleSubtitleMark subtitleMark) {
        if(timeline==null||subtitleMark==null){
            return null;
        }
        float zVal = subtitleMark.getZValue();
        NvsTimelineCaption caption = timeline.getFirstCaption();
        while (caption != null) {
            float tmpZVal = caption.getZValue();
            if (tmpZVal ==zVal){
                return caption;
            }
            caption = timeline.getNextCaption(caption);
        }
        return null;
    }

        /**
         *检查视频是否丢失
         * @param timeline
         */
    public static boolean checkVideoLose(NvsTimeline timeline) {
        boolean lose=false;
        ArrayList<ClipInfo> videoClipArray = TimelineData.instance().getClipInfoData();
        Iterator item = videoClipArray.iterator();
        while (item.hasNext()) {
            ClipInfo model = (ClipInfo) item.next();
            if (!new File(model.getFilePath()).exists()) {
                item.remove();
                lose=true;
            }
        }
        if(lose){
            TimelineData.instance().setClipInfoData(videoClipArray);
            TimelineUtil.reBuildVideoTrack(timeline);
        }
        return lose;
    }
    /**
     *检查音乐是否丢失
     */
    public static boolean checkMusicLose(List<MusicBean> list,NvsTimeline timeline) {
        boolean lose=false;
        if(list==null){
            return lose;
        }
        Iterator item = list.iterator();
        while (item.hasNext()) {
            MusicBean model = (MusicBean) item.next();
            if (!TextUtils.isEmpty(model.getPath())&& !new File(model.getPath()).exists()) {
                item.remove();
                lose=true;
            }
        }
        if(lose){
            List<MusicInfo> musicBeans=TimelineData.instance().getMusicData();
            if(musicBeans!=null&&musicBeans.size()>0){
                MusicInfo info=musicBeans.get(0);
                if (!TextUtils.isEmpty(info.getFilePath())&& !new File(info.getFilePath()).exists()) {
                    TimelineData.instance().getMusicData().clear();
                    TimelineUtil.reBuildVideoTrack(timeline);
                }
            }
        }

        return lose;
    }
    public static int  selectPosition(int position) {
        if(position<0){
            position=0;
        }else if(position>=TimelineData.instance().getClipCount()){
            position=TimelineData.instance().getClipCount()-1;
        }
        return position;
    }

    /**
     * 释放美摄资源
     * @return
     */
    public static boolean releaseNvsStreamingContext(){
        try {
            NvsStreamingContext.close();
        }catch (Exception e){
          return false;
        }
        return true;
    }
    public static boolean releaseProject(){
        TimelineData.instance().clear();
        MovieSubtitleTimeline.clear();
        StickerTimeline.clear();
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                FileUtils.deleteDirectory(FileUtils.getProjectPath());
            }
        });
        return true;
    }
    }
