package com.test.xcamera.phonealbum.widget.subtitle.bean;



import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;


public class Clip {
    private TreeSet<Mark> mMarkList = new TreeSet<>();
    private float mDuration;
    /** clip被裁剪的起始位置 */
    private float mClipTrimIn;
    /** clip被裁剪的结束位置 */
    private float mClipTrimOut;
    /** 片段倍速 */
    private float mSpeed = 1f;

    public Clip(float duration) {
        mDuration = duration;
        mClipTrimIn = 0f;
        mClipTrimOut = duration;
        mSpeed = 1f;
    }

    public float getDuration() {
        return mDuration;
    }

    public void setDuration(float duration) {
        mDuration = duration;
    }

    public float getClipTrimIn() {
        return mClipTrimIn;
    }

    public void setClipTrimIn(float trimIn) {
        mClipTrimIn = trimIn;
    }

    public float getClipTrimOut() {
        return mClipTrimOut;
    }

    public void setClipTrimOut(float trimOut) {
        mClipTrimOut = trimOut;
    }

    public float getSpeed() {
        return mSpeed;
    }

    public void setSpeed(float speed) {
        mSpeed = speed;
    }

    public void addMark(Mark mark) {
        mMarkList.add(mark);
    }

    /**
     * 以mark入点为标准，删除入点和出点两端片段，保留中间片段
     * @param trimIn 删除片段入点
     * @param trimOut 删除片段出点
     */
    public List<Mark> trimClip(float trimIn, float trimOut, boolean trimEnd) {
        float oldTrimIn = mClipTrimIn;
        mClipTrimIn = trimIn;
        mClipTrimOut = trimOut;
        List<Mark> trimmedOut = new ArrayList<>();
        Iterator<Mark> it = mMarkList.iterator();
        while (it.hasNext()) {
            Mark mark = it.next();
            if ((ArithUtil.add(mark.getStartPositionInClip(), oldTrimIn)) < trimIn
                    || (ArithUtil.add(mark.getStartPositionInClip(), oldTrimIn)) > trimOut) {
                it.remove();
                trimmedOut.add(mark);
            }
        }
        for (Mark m : mMarkList) {
            float duration;
            if (trimEnd) {
                duration = Math.min(m.getDuration(), ArithUtil.sub(trimOut, m.getStartPositionInClip()));
            } else {
                //todo 添加获取总时常
                /*duration = Math.min(m.getDuration(),
                        ArithUtil.sub(ArithUtil.mul(VideoEditManager.getSequenceDuration(), 10f),
                                m.getStartPositionInClip()));*/
            }
       /*     m.setEndPositionInClip(ArithUtil.add(m.getStartPositionInClip(), duration));*/
        }
        // 剪枝后有些片段不足最小长度需要删除
        it = mMarkList.iterator();
        while (it.hasNext()) {
            Mark mark = it.next();
            if (mark.getDuration() < Sequence.MIN_SPACE) {
                it.remove();
                trimmedOut.add(mark);
            }
        }
        mDuration = ArithUtil.sub(trimOut, trimIn);
        return trimmedOut;
    }

    public void speedClip(float speed, float oldSpeed) {
        if (speed <= 0) {
            return;
        }
        for (Mark m : mMarkList) {
            float duration = m.getDuration();
            m.setStartPositionInClip(ArithUtil.div(m.getStartPositionInClip(), ArithUtil.div(speed, oldSpeed)));
            m.setEndPositionInClip(ArithUtil.add(m.getStartPositionInClip(), duration));
        }
        mSpeed = speed;
        mDuration = ArithUtil.div(ArithUtil.sub(mClipTrimOut, mClipTrimIn), ArithUtil.div(speed, oldSpeed));
    }

    public Clip copy() {
        return new Clip(mDuration);
    }

    public TreeSet<Mark> getMarkList() {
        return mMarkList;
    }

    public List<Mark> getTrimmedMarkList(float start, float end) {
        List<Mark> result = new ArrayList<>();
        for (Mark m : mMarkList) {
            if (m.getStart() < start || m.getStart() > end) {
                result.add(m);
            }
        }
        return result;
    }

    public void clear() {
        mMarkList.clear();
    }

    public void resetTrimInOut(float speed) {
        mClipTrimIn = ArithUtil.div(mClipTrimIn, speed);
        mClipTrimOut = ArithUtil.div(mClipTrimOut, speed);
    }

    @Override
    public String toString() {
        return "Clip{" +
                "\nmMarkList=" + mMarkList +
                "\n, mDuration=" + mDuration +
                "\n, mSpeed=" + mSpeed +
                "\n, mClipTrimIn=" + mClipTrimIn +
                "\n, mClipTrimOut=" + mClipTrimOut +
                '}';
    }
}
