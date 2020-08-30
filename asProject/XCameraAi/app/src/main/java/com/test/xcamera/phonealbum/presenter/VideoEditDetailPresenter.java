package com.test.xcamera.phonealbum.presenter;

import android.content.Context;

import com.editvideo.Constants;
import com.test.xcamera.R;
import com.test.xcamera.application.AiCameraApplication;
import com.test.xcamera.phonealbum.bean.MusicBean;
import com.test.xcamera.phonealbum.widget.subtitle.TimelineVideo;
import com.test.xcamera.phonealbum.widget.subtitle.bean.Mark;
import com.test.xcamera.phonealbum.widget.subtitle.bean.MovieSubtitleTimeline;
import com.test.xcamera.phonealbum.widget.subtitle.bean.TitleSubtitleMark;
import com.meicam.sdk.NvsStreamingContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VideoEditDetailPresenter implements VideoEditDetailContract.Presenter {
    private VideoEditDetailContract.View mView;
    private Context mContext;

    public static VideoEditDetailPresenter getInstance(VideoEditDetailContract.View view, Context context) {
        return new VideoEditDetailPresenter(view, context);
    }

    private VideoEditDetailPresenter(VideoEditDetailContract.View view, Context context) {
        this.mView = view;
        this.mContext = context;
    }

    @Override
    public void initEditDetail() {
        String model = "assets:/facemodel_st/NvFace2Data.model";
        NvsStreamingContext.initHumanDetection(AiCameraApplication.getContext(), model, "assets:/facemodel_st/meetvr_face.lic",
                NvsStreamingContext.HUMAN_DETECTION_FEATURE_FACE_LANDMARK | NvsStreamingContext.HUMAN_DETECTION_FEATURE_FACE_ACTION);
    }

    @Override
    public List<MusicBean> getEditSetMenu() {
        List<MusicBean> itemBeanList = new ArrayList<>();
        itemBeanList.add(new MusicBean(R.mipmap.video_edit_detail_volumn, R.mipmap.video_edit_detail_volumn, AiCameraApplication.getContext().getString(R.string.video_edit_vlomue)));
        itemBeanList.add(new MusicBean(R.mipmap.video_edit_detail_adjust, R.mipmap.video_edit_detail_adjust, AiCameraApplication.getContext().getString(R.string.video_edit_detail_adjust)));
        itemBeanList.add(new MusicBean(R.mipmap.video_edit_detail_split, R.mipmap.video_edit_detail_split, AiCameraApplication.getContext().getString(R.string.video_edit_detail_split)));
        itemBeanList.add(new MusicBean(R.mipmap.video_edit_detail_copy, R.mipmap.video_edit_detail_copy, AiCameraApplication.getContext().getString(R.string.video_edit_detail_copy)));
        itemBeanList.add(new MusicBean(R.mipmap.video_edit_detail_change_speed, R.mipmap.video_edit_detail_change_speed, AiCameraApplication.getContext().getString(R.string.video_edit_detail_change_speed)));
        itemBeanList.add(new MusicBean(R.mipmap.video_edit_detail_reverse_play, R.mipmap.video_edit_detail_reverse_play, AiCameraApplication.getContext().getString(R.string.video_edit_reverse_play)));
        return itemBeanList;
    }

    @Override
    public List<MusicBean> getEditSetAdjust() {
        List<MusicBean> adjustBeanList = new ArrayList<>();
        adjustBeanList.add(new MusicBean(R.mipmap.video_edit_detail_adjust_anjiao, R.mipmap.video_edit_detail_adjust_anjiao_select, AiCameraApplication.getContext().getString(R.string.video_edit_detail_an_jiao)));
        adjustBeanList.add(new MusicBean(R.mipmap.video_edit_detail_adjust_baoguang, R.mipmap.video_edit_detail_adjust_baoguang_select, AiCameraApplication.getContext().getString(R.string.video_edit_detail_baoguang)));
        adjustBeanList.add(new MusicBean(R.mipmap.video_edit_detail_adjust_baohedu, R.mipmap.video_edit_detail_adjust_baohedu_select, AiCameraApplication.getContext().getString(R.string.video_edit_detail_an_baohedu)));
        adjustBeanList.add(new MusicBean(R.mipmap.video_edit_detail_adjust_duibidu, R.mipmap.video_edit_detail_adjust_duibidu_select, AiCameraApplication.getContext().getString(R.string.video_edit_detail_an_duibidu)));
        adjustBeanList.add(new MusicBean(R.mipmap.video_edit_detail_adjust_ruidu, R.mipmap.video_edit_detail_adjust_ruidu_select, AiCameraApplication.getContext().getString(R.string.video_edit_detail_an_ruidu)));
        return adjustBeanList;
    }

    @Override
    public List<MusicBean> getEditSetBeauty() {
        List<MusicBean> beautyBeanList = new ArrayList<>();

        beautyBeanList.add(new MusicBean(R.mipmap.beauty_red, R.mipmap.beauty_red_select, AiCameraApplication.getContext().getString(R.string.video_edit_detail_beauty_red), 0f, Constants.FX_BEAUTY_RED));
        beautyBeanList.add(new MusicBean(R.mipmap.beauty_mopi, R.mipmap.beauty_mopi_select, AiCameraApplication.getContext().getString(R.string.video_edit_detail_beauty_mopi), 0f, Constants.FX_BEAUTY_MOPI));
        beautyBeanList.add(new MusicBean(R.mipmap.beauty_white, R.mipmap.beauty_white_select, AiCameraApplication.getContext().getString(R.string.video_edit_detail_beauty_white), 0f, Constants.FX_BEAUTY_WHITE));
       /* beautyBeanList.add(new MusicBean(R.mipmap.beauty_thinface, R.mipmap.beauty_thinface, "瘦脸", 0f, Constants.FX_BEAUTY_THIN_FACE));
        beautyBeanList.add(new MusicBean(R.mipmap.beauty_bigeye, R.mipmap.beauty_bigeye, "大眼", 0f, Constants.FX_BEAUTY_BIG_EYE));
        beautyBeanList.add(new MusicBean(R.mipmap.beauty_xiaba, R.mipmap.beauty_xiaba, "下巴", 0f, Constants.FX_BEAUTY_XIABA));
        beautyBeanList.add(new MusicBean(R.mipmap.beauty_nose, R.mipmap.beauty_nose, "鼻子", 0f, Constants.FX_BEAUTY_NOSE));
        beautyBeanList.add(new MusicBean(R.mipmap.beauty_head, R.mipmap.beauty_head, "额头", 0f, Constants.FX_BEAUTY_HEAD));
        beautyBeanList.add(new MusicBean(R.mipmap.beauty_mouth_corner, R.mipmap.beauty_mouth_size, "嘴部", 0f, Constants.FX_BEAUTY_MOUTH_SIZE));
        beautyBeanList.add(new MusicBean(R.mipmap.beauty_small_face, R.mipmap.beauty_small_face, "小脸", 0f, Constants.FX_BEAUTY_SMALL_FACE));
        beautyBeanList.add(new MusicBean(R.mipmap.beauty_eye_corner, R.mipmap.beauty_eye_corner, "眼角", 0f, Constants.FX_BEAUTY_EYE_CORNER));
        beautyBeanList.add(new MusicBean(R.mipmap.beauty_face_width, R.mipmap.beauty_face_width, "窄脸", 0f, Constants.FX_BEAUTY_FACE_WIDTH));
        beautyBeanList.add(new MusicBean(R.mipmap.beauty_nose_length, R.mipmap.beauty_nose_length, "长鼻", 0f, Constants.FX_BEAUTY_NOSE_LENGTH));
        beautyBeanList.add(new MusicBean(R.mipmap.beauty_mouth_corner, R.mipmap.beauty_mouth_corner, "嘴角", 0f, Constants.FX_BEAUTY_MOUTH_CORNER));
*/
        return beautyBeanList;
    }

    @Override
    public TitleSubtitleMark createMark(float time) {
        TitleSubtitleMark mark;
        mark = new TitleSubtitleMark();
        mark.setStart(time * 10);
        mark.setEnd(time * 10);
        mark.setStartPositionInClip(time * 10);
        mark.setEndPositionInClip(time * 10);
        mark.setHeadLength(mark.getDuration());
        mark.setDraw(true);
        mark.setType(Mark.MARK_TYPE_BLUE);
        mark.setId(TimelineVideo.generateId());
        mark.setCreateTime(System.currentTimeMillis());
        mark.setTitle("");
        mark.setText("");

        return mark;
    }

    @Override
    public TitleSubtitleMark updateMarkTime(TitleSubtitleMark mark, float startTime, float endTime) {
        if (mark == null) {
            return null;
        }
        mark.setStart(startTime * 10);
        mark.setEnd(endTime * 10);
        mark.setStartPositionInClip(startTime * 10);
        mark.setEndPositionInClip(endTime * 10);
        mark.setHeadLength(mark.getDuration());
        MovieSubtitleTimeline.replaceMark(mark);
        return mark;
    }

    @Override
    public TitleSubtitleMark updateMarkText(TitleSubtitleMark mark, String text) {
        if (mark == null) {
            return null;
        }
        mark.setText(text);
        MovieSubtitleTimeline.replaceMark(mark);
        return mark;
    }

    public synchronized double getApproximateMin(double start, float mMinValue) {
        List<TitleSubtitleMark> marks = (List<TitleSubtitleMark>) MovieSubtitleTimeline.getMarkList();
        if (marks == null && marks.size() == 0) {
            return mMinValue;
        }
        if (marks.size() == 1) {
            return mMinValue;
        }
        List<Double> list = new ArrayList();
        for (int i = 0; i < marks.size(); i++) {
            list.add(new Double(marks.get(i).getEnd()));
        }
        Collections.sort(list);
        Collections.reverse(list);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).doubleValue() <= start) {
                return list.get(i).doubleValue();
            }
        }
        return mMinValue;
    }

    /**
     * 获取接近最大值
     *
     * @param end
     * @return
     */
    public synchronized double getApproximateMax(double end, float mMaxValue) {
        List<TitleSubtitleMark> marks = (List<TitleSubtitleMark>) MovieSubtitleTimeline.getMarkList();

        if (marks == null && marks.size() == 0) {
            return mMaxValue;
        }
        if (marks.size() == 1) {
            return mMaxValue;
        }
        List<Double> list = new ArrayList();
        for (int i = 0; i < marks.size(); i++) {
            list.add(new Double(marks.get(i).getStart()));
        }
        Collections.sort(list);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).doubleValue() >= end) {
                return list.get(i).doubleValue();
            }
        }
        return mMaxValue;
    }
}
