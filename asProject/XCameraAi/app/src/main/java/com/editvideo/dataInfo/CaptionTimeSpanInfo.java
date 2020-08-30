package com.editvideo.dataInfo;

import com.editvideo.timelineEditor.NvsTimelineTimeSpan;
import com.meicam.sdk.NvsTimelineCaption;

public class CaptionTimeSpanInfo {
        public NvsTimelineCaption mCaption;
        public NvsTimelineTimeSpan mTimeSpan;

        public CaptionTimeSpanInfo(NvsTimelineCaption caption, NvsTimelineTimeSpan timeSpan) {
            this.mCaption = caption;
            this.mTimeSpan = timeSpan;
        }
    }