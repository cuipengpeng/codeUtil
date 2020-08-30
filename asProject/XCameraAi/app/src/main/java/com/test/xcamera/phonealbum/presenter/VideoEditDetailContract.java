package com.test.xcamera.phonealbum.presenter;

import com.test.xcamera.phonealbum.bean.MusicBean;
import com.test.xcamera.phonealbum.widget.subtitle.bean.TitleSubtitleMark;

import java.util.List;

public class VideoEditDetailContract {
    public interface View{
    }
    public interface Presenter{
        void initEditDetail();
        /**
         * 获取视频编辑菜单
         * @return
         */
        List<MusicBean> getEditSetMenu();

        /**
         * 获取调节参数
         * @return
         */
        List<MusicBean> getEditSetAdjust();
        /**
         * 获取美颜
         * @return
         */
        List<MusicBean> getEditSetBeauty();
        /**
         * 字幕创建
         * @param time
         * @return
         */
        TitleSubtitleMark createMark(float time);

        /**
         * 修改字幕时间点
         * mark
         * @param startTime
         * @param endTime
         * @return
         */
        TitleSubtitleMark updateMarkTime(TitleSubtitleMark mark ,float startTime,float endTime);

        /**
         * 修改字幕文本
         * @param mark
         * @param text
         * @return
         */
        TitleSubtitleMark updateMarkText(TitleSubtitleMark mark ,String text);

        /**
         * 获取最小移动区间值
         * @param start
         * @param mMinValue
         * @return
         */
        double getApproximateMin(double start,float mMinValue );

        /**
         * 最大移动区间值
         * @param end
         * @param mMaxValue
         * @return
         */
        double getApproximateMax(double end,float mMaxValue);
    }
}
