package com.test.xcamera.phonealbum.usecase;

import com.test.xcamera.phonealbum.bean.VideoParamCaptionData;
import com.test.xcamera.phonealbum.widget.subtitle.TimelineVideo;
import com.test.xcamera.phonealbum.widget.subtitle.bean.Mark;
import com.test.xcamera.phonealbum.widget.subtitle.bean.TitleSubtitleMark;

public class VideoSubtitleEdit {
    public static VideoSubtitleEdit getInstance() {
        return new VideoSubtitleEdit();
    }

    public TitleSubtitleMark createMark(VideoParamCaptionData data) {
        TitleSubtitleMark mark;
        mark = new TitleSubtitleMark();
        mark.setStart(data.getStart()*10);
        mark.setEnd(data.getEnd()*10);
        mark.setStartPositionInClip(data.getStart()*10);
        mark.setEndPositionInClip(data.getEnd()*10);
        mark.setHeadLength(mark.getDuration());
        mark.setDraw(true);
        mark.setType(Mark.MARK_TYPE_BLUE);
        mark.setId(TimelineVideo.generateId());
        mark.setCreateTime(System.currentTimeMillis());
        mark.setTitle(data.getText());
        mark.setText(data.getText());

        return mark;
    }
}
