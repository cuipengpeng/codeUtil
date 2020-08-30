package com.test.xcamera.phonealbum.presenter;

import android.content.Context;

import com.editvideo.VideoClipFxInfo;
import com.test.xcamera.phonealbum.bean.MusicBean;
import com.meicam.sdk.NvsAudioTrack;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineCompoundCaption;

import java.util.List;

public class VideoEditContract {
    public interface View{
        /**
         * 显示背景音乐bean
         */
        void showMusicBGBeanList(List<MusicBean> list);
        /**
         * 显示视频滤镜
         */
        void showFilerBeanList(List<MusicBean> list);

        /**
         * 音乐淡入
         * @param isOpen
         */
        void updateMusicFadeInStatus(boolean isOpen);

        /**
         * 音乐淡出
         * @param isOpen
         */
        void updateMusicFadeOutStatus(boolean isOpen);

        /**
         * 视频合并结构返回状态
         * @param isStatus
         */
        void videoCompileResult(boolean isStatus);
    }
    public interface Presenter{
        /**
         * 设置view宽高
         * @param view
         * @param w
         * @param h
         */
        void setLayoutParams(android.view.View view, int w, int h);

        /**
         * 获取背景音乐bean
         */
        void getMusicBGBeanList();

        /**
         * 获取视频滤镜
         */
        void getVideoFilerBeanList();

        /**
         * 选择当前选中的组合字幕
         */
        NvsTimelineCompoundCaption getCurCompoundCaption(NvsTimeline timeline,long curPos);

        /**
         * 设置滤镜参数
         * @param timeline
         */
        void setFilterIntensity(NvsTimeline timeline, VideoClipFxInfo videoClipFxInfo);

        /**
         * 使用音乐
         * @param timeline
         * @param audioTrack
         * @param pos
         * @param musicPath
         */
        void playPositionItemMusic(NvsTimeline timeline, NvsAudioTrack audioTrack,int pos,String musicPath);
        /**
         * 设置音乐淡入
         * @param timeline
         * @param audioTrack
         */
        void  updateMusicFadeInStatus(NvsTimeline timeline, NvsAudioTrack audioTrack);

        /**
         * 设置音乐淡出
         * @param timeline
         * @param audioTrack
         */
        void  updateMusicFadeOutStatus(NvsTimeline timeline, NvsAudioTrack audioTrack);

        /**
         * 设置usb连接错误
         */
        void setConnectedUSBError();

        /**
         * 设置USb连接数据同步
         */
        void setUsbDispose();

        /**
         * 获取组合字幕INdex
         * @param curZValue
         * @return
         */
        int getCompoundCaptionIndex(int curZValue);

        /**
         * 视频合并
         * @param context
         * @param streamingContext
         * @param timeline
         * @param compilePath
         * @param startTime
         * @param endTime
         */
        void compileVideo(Context context, NvsStreamingContext streamingContext,NvsTimeline timeline, String compilePath, long startTime, long endTime) ;

        /**
         * 数据销毁
         */
        void destroy();
        }
}
