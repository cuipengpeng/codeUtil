package com.test.xcamera.phonealbum.usecase;

import android.text.TextUtils;

import com.editvideo.MediaConstant;
import com.editvideo.MediaData;
import com.editvideo.ToastUtil;
import com.editvideo.Util;
import com.editvideo.VideoClipFxInfo;
import com.editvideo.dataInfo.ClipInfo;
import com.editvideo.dataInfo.MusicInfo;
import com.editvideo.dataInfo.TimelineData;
import com.editvideo.dataInfo.TransitionInfo;
import com.test.xcamera.R;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.phonealbum.bean.MusicBean;
import com.test.xcamera.phonealbum.bean.VideoParamCaptionData;
import com.test.xcamera.phonealbum.bean.VideoParamClipData;
import com.test.xcamera.phonealbum.bean.VideoParamData;
import com.test.xcamera.phonealbum.bean.VideoParamMusicData;
import com.test.xcamera.phonealbum.bean.VideoParamStickerData;
import com.test.xcamera.phonealbum.widget.VideoEditManger;
import com.test.xcamera.phonealbum.widget.subtitle.bean.MovieSubtitleTimeline;
import com.test.xcamera.phonealbum.widget.subtitle.bean.StaticRuneMark;
import com.test.xcamera.phonealbum.widget.subtitle.bean.StickerTimeline;
import com.test.xcamera.phonealbum.widget.subtitle.bean.TitleSubtitleMark;
import com.test.xcamera.utils.LoggerUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MediaClipDataTran {
    /**
     * 媒体文件转编辑片段
     *
     * @param mediaDataList
     * @return
     */
    public static ArrayList<ClipInfo> mediaData2ClipInfo(List<MediaData> mediaDataList) {
        ArrayList<ClipInfo> pathList = new ArrayList<>();
        for (MediaData mediaData : mediaDataList) {
            if(check(mediaData)){
                LoggerUtils.printLog("mediaData.getPath()=" + mediaData.getPath());
                ClipInfo clipInfo = new ClipInfo();
                clipInfo.setFilePath(mediaData.getPath());
                clipInfo.setM_fileOldPath(mediaData.getPath());
                clipInfo.setDuration(mediaData.getDuration());
                clipInfo.setRemoteData(mediaData.isRemoteData());
                pathList.add(clipInfo);
            }
        }
        return pathList;
    }
    private static boolean check(MediaData mediaData ) {
        if (!new File(mediaData.getPath()).exists()) {
            return false;
        }else  if (mediaData.getType() == MediaConstant.VIDEO) {
            if (VideoImportCheck.getInstance().getVideoBitRate(mediaData.getPath()) == 0) {
                return false;
            }
        }
        return true;
    }

    public static List<MediaData> clipInfo2MediaData(List<ClipInfo> clipInfoList) {
        ClipInfo clipInfo;
        List<MediaData> mediaDataList = new ArrayList<>();
        for (int i = 0; i < clipInfoList.size(); i++) {
            clipInfo = clipInfoList.get(i);
            MediaData mediaData = new MediaData();
            mediaData.setM_speed(clipInfo.getSpeed());
            mediaData.setPath(clipInfo.getFilePath());
            mediaData.setTrimIn(clipInfo.getTrimIn()>0?clipInfo.getTrimIn():0);
            LoggerUtils.printLog("mediaData.getPath()=" + mediaData.getPath());
            if (clipInfo.getDuration() > 0) {
                mediaData.setType(MediaConstant.VIDEO);
                if (clipInfo.getTrimOut() > 0) {
                    mediaData.setDuration((long)(((clipInfo.getTrimOut() - clipInfo.getTrimIn()) / 1000)/clipInfo.getSpeed()));
                } else {
                    mediaData.setDuration((long)((clipInfo.getDuration() - (clipInfo.getTrimIn() / 1000))/clipInfo.getSpeed()));
                }
            } else {
                mediaData.setType(MediaConstant.IMAGE);
            }
            mediaDataList.add(mediaData);
        }
        return mediaDataList;
    }

    /**
     * 传入文件转换视频编辑文件
     *
     * @param paramData
     * @return
     */
    public static ArrayList<ClipInfo> videoParamClipToClipInfo(VideoParamData paramData) {
        ArrayList<ClipInfo> clipInfos = new ArrayList<>();
        if (paramData == null || paramData.getVideoParamClipData() == null) {
            return clipInfos;
        }
        int index=0;
        for (VideoParamClipData clipData : paramData.getVideoParamClipData()) {
            ClipInfo clipInfo = new ClipInfo();
            clipInfo.setFilePath(clipData.getFilePath());
            clipInfo.setM_fileOldPath(clipData.getFilePath());
            clipInfo.setDuration((long) (clipData.getDuration() * 1000));
            clipInfo.setRemoteData(false);
            if(index>0){
                clipInfo.setTransitionInfo(paramData.getVideoParamClipData().get(index-1).getTransitionInfo());
            }
            clipInfo.setSpeed(clipData.getSpeed());
            clipInfo.setBrightnessVal(clipData.getM_brightnessVal());
            clipInfo.setContrastVal(clipData.getM_contrastVal());
            clipInfo.setVignetteVal(clipData.getM_vignetteVal());
            clipInfo.setSaturationVal(clipData.getM_saturationVal());
            clipInfo.setSharpenVal(clipData.getM_sharpenVal());
            clipInfos.add(clipInfo);
            index++;
        }
        return clipInfos;
    }

    public static List<MusicBean> getMusicInfoListToMusicBean(List<MusicInfo> paramData) {
        List<MusicBean> musicBeanList = new ArrayList<>();
        if(paramData==null){
            return musicBeanList;
        }
        for(MusicInfo musicInfo:paramData){
            MusicBean bean=new MusicBean();
            bean.setSelected(true);
            bean.setPath(musicInfo.getFilePath());
            bean.setCoverFileURL(musicInfo.getImagePath());
            bean.setDuration((int)(musicInfo.getDuration()/1000));
            bean.setName(musicInfo.getTitle());
            musicBeanList.add(bean);
        }
        return musicBeanList;
    }

    public static List<MusicInfo> getMusicInfoList(VideoParamData paramData) {
        List<MusicInfo> musicInfoList = new ArrayList<>();
        VideoParamMusicData data = paramData.getVideoParamMusicData();
        if (data != null) {
            MusicInfo musicInfo = new MusicInfo();
            musicInfo.setFilePath(data.getFilePath());
            musicInfo.setImagePath(data.getImagePath());
            musicInfo.setTitle(data.getName());
            musicInfo.setInPoint((long) (data.getInPoint() * VideoEditManger.VIDEO_microsecond_TIME));
            musicInfo.setOutPoint((long) (data.getOutPoint() * VideoEditManger.VIDEO_microsecond_TIME));
            musicInfo.setTrimIn((long) (data.getTrimIn() * VideoEditManger.VIDEO_microsecond_TIME));
            musicInfo.setTrimOut((long) (data.getTrimOut() * VideoEditManger.VIDEO_microsecond_TIME));
            musicInfo.setDuration((long) (data.getDuration() * VideoEditManger.VIDEO_microsecond_TIME));
            musicInfo.setVolume(data.getVolume());

            musicInfoList.add(musicInfo);
        }
        return musicInfoList;
    }

    public static boolean videoParamToTimelineData(VideoParamData paramData, ArrayList<ClipInfo> clipInfos) {
        if (paramData == null) {
            return false;
        }
        /*滤镜*/
        if (paramData.getM_videoClipFxInfo() != null) {
            TimelineData.instance().setVideoClipFxData(paramData.getM_videoClipFxInfo());
        }
        /*音乐*/
        if (paramData.getVideoParamMusicData() != null) {
            List<MusicInfo> musicInfos = getMusicInfoList(paramData);
            TimelineData.instance().setMusicList(musicInfos);
            TimelineData.instance().setMusicVolume(paramData.getVideoParamMusicData().getVolume());
        }
        /*字幕*/
        if (paramData.getVideoParamCaptionData() != null) {
            MovieSubtitleTimeline.clear();
            for (VideoParamCaptionData captionData : paramData.getVideoParamCaptionData()) {
                TitleSubtitleMark mark = VideoSubtitleEdit.getInstance().createMark(captionData);
                MovieSubtitleTimeline.addMark(mark);
            }
        }
        /*贴纸*/
        if (paramData.getVideoParamStickerData() != null) {
            StickerTimeline.clear();
            for (VideoParamStickerData captionData : paramData.getVideoParamStickerData()) {
                StaticRuneMark mark = VideoStickerEdit.getInstance().createMark(captionData);
                StickerTimeline.addMark(mark);
            }
        }
        /*组合字幕*/
        if(paramData.getCompoundCaptionArray()!=null){
            TimelineData.instance().setCompoundCaptionArray(paramData.getCompoundCaptionArray());
        }

        return videoParamToTimelineData(clipInfos);
    }

    public static boolean videoParamToTimelineData(ArrayList<ClipInfo> clipInfos) {
        if (clipInfos == null || clipInfos.size() <= 0|| TextUtils.isEmpty(clipInfos.get(0).getFilePath())) {
            ToastUtil.showToast(AiCameraApplication.getContext(), AiCameraApplication.getContext().getString(R.string.video_import_error));
            return false;
        }
        int ratio = Util.getVideoRatio(clipInfos.get(0).getFilePath());
        TimelineData.instance().setVideoResolution(Util.getVideoEditResolution(ratio,clipInfos.get(0).getFilePath()));
        TimelineData.instance().setClipInfoData(clipInfos);
        TimelineData.instance().setMakeRatio(ratio);
        return true;
    }
    public static VideoParamData getTestVideoParamData(){
        VideoParamData paramData=new VideoParamData();
        TransitionInfo transitionInfo;
        ArrayList<VideoParamClipData> clipInfos = new ArrayList<>();
        VideoParamClipData clipInfo=new VideoParamClipData();
        clipInfo.setFilePath("/storage/emulated/0/DCIM/Camera/VID_20200313_091913.mp4");
        clipInfo.setDuration(12705/1000f);
        transitionInfo=new TransitionInfo();
        transitionInfo.setTransitionId("FCC6384D-1459-4C44-B05F-496AFC472A83");
        transitionInfo.setTransitionMode(1);
        clipInfo.setTransitionInfo(transitionInfo);
        clipInfos.add(clipInfo);

        clipInfo=new VideoParamClipData();
        clipInfo.setFilePath("/storage/emulated/0/DCIM/Camera/VID_20200313_091740.mp4");
        clipInfo.setDuration(11766/1000f);
        transitionInfo=new TransitionInfo();
        transitionInfo.setTransitionId("FCC6384D-1459-4C44-B05F-496AFC472A83");
        transitionInfo.setTransitionMode(1);
        clipInfo.setTransitionInfo(transitionInfo);
        clipInfos.add(clipInfo);
        paramData.setVideoParamClipData(clipInfos);

        VideoClipFxInfo m_videoClipFxInfo=new VideoClipFxInfo();
        m_videoClipFxInfo.setFxId("9323F172-7B2E-4D8F-BE74-5FE8934E6046");
        m_videoClipFxInfo.setFxMode(1);
        paramData.setM_videoClipFxInfo(m_videoClipFxInfo);

        VideoParamMusicData videoParamMusicData=new VideoParamMusicData();
        videoParamMusicData.setFilePath("/storage/emulated/0/Android/data/com.meetvr.aicamera/cache/music/068ba1e8f56fe59b403999b1bcc9963c");
        videoParamMusicData.setImagePath("https://p3-dy.byteimg.com/aweme/720x720/iesmusic-cn-local/m/5ad70006d1d3f876814c.jpeg");
        videoParamMusicData.setVolume(0.5f);
        videoParamMusicData.setName("baby oh");
        videoParamMusicData.setInPoint(0);
        videoParamMusicData.setOutPoint(10);
        videoParamMusicData.setTrimIn(0);
        videoParamMusicData.setTrimOut(10);
        videoParamMusicData.setDuration(10);
        paramData.setVideoParamMusicData(videoParamMusicData);

        List<VideoParamCaptionData> captionDataList=new ArrayList<>();
        VideoParamCaptionData captionData;
        captionData=new VideoParamCaptionData();
        captionData.setStart(1);
        captionData.setEnd(5);
        captionData.setText("1,第一段测试");
        captionDataList.add(captionData);
        captionData=new VideoParamCaptionData();
        captionData.setStart(7);
        captionData.setEnd(9);
        captionData.setText("2,第二段测试");
        captionDataList.add(captionData);
        paramData.setVideoParamCaptionData(captionDataList);
        return paramData;
    }
}
