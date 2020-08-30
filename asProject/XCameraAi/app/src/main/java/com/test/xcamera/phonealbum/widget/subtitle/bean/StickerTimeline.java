package com.test.xcamera.phonealbum.widget.subtitle.bean;


import com.test.xcamera.phonealbum.widget.subtitle.TimelineVideo;

import java.util.List;


public class StickerTimeline extends TimelineVideo {
    
    private static Sequence sSequence = new Sequence();

    public static void addMark(Mark mark) {
        sSequence.addMark(mark);
    }

    public static void insertMark(Mark mark) {
        sSequence.insertMark(mark);
    }

    public static void deleteMark(Mark mark) {
        sSequence.deleteMark(mark);
    }

    public static void deleteMark(String markId) {
        sSequence.deleteMark(markId);
    }

    public static void replaceMark(Mark mark) {
        if (mark == null) {
            return;
        }
        sSequence.deleteMark(mark.getId());
        sSequence.addMark(mark);
    }

    public static Mark getMark(String id) {
        return sSequence.getMark(id);
    }

    public static void clear() {
        sSequence.clear();
    }

    public static void reset() {
        sSequence.reset();
    }

    public static float getDuration() {
        return sSequence.getDuration();
    }

    public static List<? extends Mark> getMarkList() {
        return sSequence.getMarkList();
    }

    public static List<? extends Mark> getHookedMarkList(float position) {
        return sSequence.getHookedMarkList(position);
    }


    public static void restore(List<? extends Mark> markList) {
        sSequence.restore(markList);
    }
    public static boolean isRightInMarkArea(float position) {
        return sSequence.isRightInMarkArea(position);
    }
    public static boolean isRightInDurationFoot(float position) {
        return sSequence.isRightInDurationFoot(position);
    }


    public static void clipMark(Mark mark) {
        sSequence.clipMark(mark);
    }

    public static void speedClip(int clipIndex, float speed, float oldSpeed) {
        sSequence.speedClip(clipIndex, speed, oldSpeed, false);
    }
}
