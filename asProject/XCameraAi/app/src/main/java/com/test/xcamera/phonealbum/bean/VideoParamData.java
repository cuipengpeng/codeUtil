package com.test.xcamera.phonealbum.bean;

import com.editvideo.VideoClipFxInfo;
import com.editvideo.dataInfo.CompoundCaptionInfo;

import java.util.ArrayList;
import java.util.List;

public class VideoParamData {

    /*视频片段*/
    private List<VideoParamClipData> mVideoParamClipData;
    /*滤镜*/
    private VideoClipFxInfo m_videoClipFxInfo;
    /*配置音乐*/
    private VideoParamMusicData mVideoParamMusicData;
    /*字幕*/
    private List<VideoParamCaptionData> mVideoParamCaptionData;
    /*组合字幕*/
    private ArrayList<CompoundCaptionInfo> mCompoundCaptionArray;
    /*贴纸*/
    private List<VideoParamStickerData> mVideoParamStickerData;

    public List<VideoParamClipData> getVideoParamClipData() {
        return mVideoParamClipData;
    }

    public void setVideoParamClipData(List<VideoParamClipData> mVideoParamClipData) {
        this.mVideoParamClipData = mVideoParamClipData;
    }

    public VideoClipFxInfo getM_videoClipFxInfo() {
        return m_videoClipFxInfo;
    }

    public void setM_videoClipFxInfo(VideoClipFxInfo m_videoClipFxInfo) {
        this.m_videoClipFxInfo = m_videoClipFxInfo;
    }

    public VideoParamMusicData getVideoParamMusicData() {
        return mVideoParamMusicData;
    }

    public void setVideoParamMusicData(VideoParamMusicData mVideoParamMusicData) {
        this.mVideoParamMusicData = mVideoParamMusicData;
    }

    public List<VideoParamCaptionData> getVideoParamCaptionData() {
        return mVideoParamCaptionData;
    }

    public void setVideoParamCaptionData(List<VideoParamCaptionData> mVideoParamCaptionData) {
        this.mVideoParamCaptionData = mVideoParamCaptionData;
    }

    public List<VideoParamStickerData> getVideoParamStickerData() {
        return mVideoParamStickerData;
    }

    public void setVideoParamStickerData(List<VideoParamStickerData> mVideoParamStickerData) {
        this.mVideoParamStickerData = mVideoParamStickerData;
    }
    public ArrayList<CompoundCaptionInfo> getCompoundCaptionArray() {
        return mCompoundCaptionArray;
    }

    public void setCompoundCaptionArray(ArrayList<CompoundCaptionInfo> m_compoundCaptionArray) {
        this.mCompoundCaptionArray = m_compoundCaptionArray;
    }
}
