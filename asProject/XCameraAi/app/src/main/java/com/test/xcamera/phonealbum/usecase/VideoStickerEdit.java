package com.test.xcamera.phonealbum.usecase;

import com.test.xcamera.phonealbum.bean.VideoParamStickerData;
import com.test.xcamera.phonealbum.widget.subtitle.TimelineVideo;
import com.test.xcamera.phonealbum.widget.subtitle.bean.Mark;
import com.test.xcamera.phonealbum.widget.subtitle.bean.StaticRuneMark;

public class VideoStickerEdit {
    public static VideoStickerEdit getInstance() {
        return new VideoStickerEdit();
    }
    public StaticRuneMark createMark(VideoParamStickerData data) {
        StaticRuneMark mark;
        mark = new StaticRuneMark();
        mark.setStart(data.getStart()*10);
        mark.setEnd(data.getEnd()*10);
        mark.setStartPositionInClip(data.getStart()*10);
        mark.setEndPositionInClip(data.getEnd()*10);
        mark.setHeadLength(mark.getDuration());
        mark.setDraw(true);
        mark.setType(Mark.MARK_TYPE_BLUE);
        mark.setId(TimelineVideo.generateId());
        mark.setCreateTime(System.currentTimeMillis());
        mark.setTitle("");
        mark.setStickerPath(data.getPackagePath());
        mark.setThumbnailPath(data.getPackagePath());
        return mark;
    }
}
