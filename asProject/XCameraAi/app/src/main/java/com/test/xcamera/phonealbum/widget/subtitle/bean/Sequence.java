package com.test.xcamera.phonealbum.widget.subtitle.bean;

import android.util.Log;

import com.test.xcamera.phonealbum.widget.VideoEditManger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Sequence {

    private static final String TAG = Sequence.class.getSimpleName();

    public static final float MIN_SPACE = 5f;

    private List<Clip> mClipList = new ArrayList<>();
    private TreeSet<Mark> mMarkSet = new TreeSet<>();
    private List<Mark> mHookedList = new ArrayList<>();

    public Clip getClip(int index) {
        if (index < 0 || index >= mClipList.size()) {
            return null;
        }
        return mClipList.get(index);
    }

    public void addClip(Clip clip) {
        mClipList.add(clip);
        updateMarkList();
    }

    public void addClip(Clip clip, int index) {
        if (index < 0 || index > mClipList.size()) {
            return;
        }
        mClipList.add(index, clip);
        updateMarkList();
    }

    public void deleteClip(int index) {
        if (index < 0 || index >= mClipList.size()) {
            return;
        }
        Clip remove = mClipList.remove(index);
        Iterator<Mark> it = remove.getMarkList().iterator();
        while (it.hasNext()) {
            Mark next = it.next();
            if (next != null) {
                mMarkSet.remove(next);
            }
        }
        updateMarkList();
        for (int i = 0; i < index; i++) {
            Clip clip = mClipList.get(i);
            Set<Mark> markSet = clip.getMarkList();
            for (Mark m : markSet) {
                if (m.getDuration() < MIN_SPACE) {
                    deleteMark(m);
                }
            }
        }
    }

    public void deleteClip2(int index) {
        if (index < 0 || index >= mClipList.size()) {
            return;
        }
        Clip remove = mClipList.get(index);
        remove.setDuration(0);
        updateMarkList();
        mClipList.remove(index);
        for (int i = 0; i < index; i++) {
            Clip clip = mClipList.get(i);
            Set<Mark> markSet = clip.getMarkList();
            for (Mark m : markSet) {
                if (m.getDuration() < MIN_SPACE) {
                    deleteMark(m);
                }
            }
        }
    }

    public void copyClip(Clip clip) {
        int index = mClipList.indexOf(clip);
        if (index == -1) {
            return;
        }
        Clip copy = clip.copy();
        mClipList.add(index + 1, copy);
        updateMarkList();
    }

    public void copyClip2(Clip clip) {
        int index = mClipList.indexOf(clip);
        if (index == -1) {
            return;
        }
        Clip copy = clip.copy();
        mClipList.add(index + 1, copy);
        // 不需要调用updateMarkList, 背景音乐独立时间轴
    }

    public void switchClip(int origin, int destination) {
        if (origin < 0 ||
                origin >= mClipList.size() ||
                destination < 0 ||
                destination >= mClipList.size() ||
                origin == destination) {
            return;
        }
        Collections.swap(mClipList, origin, destination);
        updateMarkList();
        // 交换clip后mark set排序打乱了，需要重新排序
        List<Mark> markList = new ArrayList<>(mMarkSet);
        Collections.sort(markList);
        mMarkSet = new TreeSet<>(markList);
    }

    public void moveClip(int origin, int destination) {
        if (origin < 0 ||
                origin >= mClipList.size() ||
                destination < 0 ||
                destination >= mClipList.size() ||
                origin == destination) {
            return;
        }
        Clip originValue = mClipList.get(origin);
        int delta = origin < destination ? 1 : -1;
        for (int i = origin; i != destination; i+= delta) {
            mClipList.set(i, mClipList.get(i + delta));
        }
        mClipList.set(destination, originValue);
        updateMarkList();
        // 交换clip后mark set排序打乱了，需要重新排序
        List<Mark> markList = new ArrayList<>(mMarkSet);
        Collections.sort(markList);
        mMarkSet = new TreeSet<>(markList);
        invertedUpdateMarkList();
//        updateMarkList();
//        TreeSet<Mark> originMarkList = originValue.getMarkList();
//        Mark first = null;
//        Mark last = null;
//        if (originMarkList != null && originMarkList.size() >= 1) {
//            first = originMarkList.first();
//            if (originMarkList.size() > 1) {
//                last = originMarkList.last();
//            } else {
//                last = null;
//            }
//        }
//        if (first != null) {
//            decisionMark(first);
//        }
//        if (last != null) {
//            decisionMark(last);
//        }
    }

    private void decisionMark(Mark mark) {
        if (mark == null) {
            return;
        }
        deleteMark(mark);
        if ((ArithUtil.sub(mark.getEnd(), mark.getStart())) < MIN_SPACE) {
            deleteMark(mark);
        } else {
            addMark(mark);
        }
    }

    public void moveClipAndCutOverlaidMark(int origin, int destination) {
        if (origin < 0 ||
                origin >= mClipList.size() ||
                destination < 0 ||
                destination >= mClipList.size() ||
                origin == destination) {
            return;
        }
        Clip originClip = getClip(origin);
        TreeSet<Mark> originMarkList = originClip.getMarkList();
        Mark first = null;
        Mark last = null;
        if (originMarkList != null && originMarkList.size() >= 1) {
            first = originMarkList.first();
            if (originMarkList.size() > 1) {
                last = originMarkList.last();
            } else {
                last = null;
            }
        }
        moveClip(origin, destination);
        if (first != null) {
            clipMark(first);
        }
        if (last != null) {
            clipMark(last);
        }
    }

    public void trimClip2(int clipIndex, float trimIn, float trimOut) {
        if (clipIndex < 0 || clipIndex >= mClipList.size()) {
            return;
        }
        boolean trimEnd = (clipIndex == mClipList.size() - 1);
        Clip clip = mClipList.get(clipIndex);
        float lastTrimIn = clip.getClipTrimIn();
        float lastTrimOut = clip.getClipTrimOut();
        float dif = ArithUtil.sub(clip.getClipTrimIn(), trimIn);
        for (Mark m : clip.getMarkList()) {
            m.setStart(ArithUtil.add(m.getStart(), dif));
            m.setEnd(ArithUtil.add(m.getEnd(), dif));
            m.setStartPositionInClip(ArithUtil.add(m.getStartPositionInClip(), dif));
            m.setEndPositionInClip(ArithUtil.add(m.getEndPositionInClip(), dif));
        }
        List<Mark> trimmed = clip.trimClip(trimIn, trimOut, trimEnd);
        for (int i = 0, len = trimmed.size(); i < len; i++) {
            mMarkSet.remove(trimmed.get(i));
        }
        updateMarkList();
    }

    public void trimClip(int clipIndex, float trimIn, float trimOut) {
        if (clipIndex < 0 || clipIndex >= mClipList.size()) {
            return;
        }
        boolean trimEnd = (clipIndex == mClipList.size() - 1);
        Clip clip = mClipList.get(clipIndex);
        float lastTrimIn = clip.getClipTrimIn();
        float lastTrimOut = clip.getClipTrimOut();
        float dif = ArithUtil.sub(clip.getClipTrimIn(), trimIn);
        for (Mark m : clip.getMarkList()) {
            m.setStart(ArithUtil.add(m.getStart(), dif));
            m.setEnd(ArithUtil.add(m.getEnd(), dif));
            m.setStartPositionInClip(ArithUtil.add(m.getStartPositionInClip(), dif));
            m.setEndPositionInClip(ArithUtil.add(m.getEndPositionInClip(), dif));
        }
        List<Mark> trimmed = clip.trimClip(trimIn, trimOut, trimEnd);
        for (int i = 0, len = trimmed.size(); i < len; i++) {
            mMarkSet.remove(trimmed.get(i));
        }
        invertedUpdateMarkList();
    }

    public void invertedUpdateMarkList() {
        float start = getDuration();
        List<Mark> trimmedList = new ArrayList<>();
//        Mark last = null;
        for (int i = mClipList.size() - 1; i >= 0; i--) {
            Clip clip = mClipList.get(i);
            start = ArithUtil.sub(start, clip.getDuration());
            TreeSet<Mark> mTree = clip.getMarkList();
            Iterator<Mark> it = mTree.descendingIterator();
            while (it.hasNext()) {
                Mark mark = it.next();
                if (mark.getStartPositionInClip() >= clip.getDuration()) {
                    it.remove();
                    trimmedList.add(mark);
                    continue;
                }
                mark.setStart(ArithUtil.add(start, mark.getStartPositionInClip()));
                float space = Math.min(mark.getDuration(), ArithUtil.sub(getDuration(), mark.getStart()));
                mark.setEnd(ArithUtil.add(mark.getStart(), space));
                mark.setEndPositionInClip(ArithUtil.add(mark.getStartPositionInClip(), space));
                if (mark.getDuration() < MIN_SPACE) {
                    it.remove();
                    trimmedList.add(mark);
                    continue;
                }
//                if (last != null) {
//                    mark.setStart(ArithUtil.add(start, mark.getStartPositionInClip()));
//                    float space = Math.min(mark.getDuration(), ArithUtil.sub(last.getStart(), mark.getStart()));
//                    mark.setEnd(ArithUtil.add(mark.getStart(), space));
//                    mark.setEndPositionInClip(ArithUtil.add(mark.getStartPositionInClip(), space));
//                    if (mark.getDuration() < MIN_SPACE) {
//                        it.remove();
//                        trimmedList.add(mark);
//                        continue;
//                    }
//                } else {
//                    mark.setStart(ArithUtil.add(start, mark.getStartPositionInClip()));
//                    float space = Math.min(mark.getDuration(), ArithUtil.sub(getDuration(), mark.getStart()));
//                    mark.setEnd(ArithUtil.add(mark.getStart(), space));
//                    mark.setEndPositionInClip(ArithUtil.add(mark.getStartPositionInClip(), space));
//                    if (mark.getDuration() < MIN_SPACE) {
//                        it.remove();
//                        trimmedList.add(mark);
//                        continue;
//                    }
//                }
//                last = mark;
            }
        }
        for (Mark m: trimmedList) {
            mMarkSet.remove(m);
        }
        Mark next;
        for (Iterator<Mark> it = mMarkSet.iterator(); it.hasNext();) {
            Mark current = it.next();
            next = mMarkSet.higher(current);
            if (next != null) {
                if (next.getStart() > current.getEnd()) {
                    current.setHeadLength(current.getDuration());
                } else {
                    current.setHeadLength(ArithUtil.sub(next.getStart(), current.getStart()));
                }
            } else {
                current.setHeadLength(current.getDuration());
            }
        }
    }

    public void speedClip(int clipIndex, float speed, float oldSpeed, boolean isCutOverlay) {
        if (speed <= 0) {
            return;
        }
        Clip clip = mClipList.get(clipIndex);
        clip.speedClip(speed, oldSpeed);
        clip.resetTrimInOut(speed);
        if (isCutOverlay) {
            TreeSet<Mark> markList = clip.getMarkList();
            Iterator<Mark> markIterator = markList.iterator();
            List<Mark> tobeDeleted = new ArrayList<>();
            while (markIterator.hasNext()) {
                Mark left = markIterator.next();
                Mark right = markList.higher(left);
                if (left != null && right != null) {
                    if (left.getEndPositionInClip() > right.getStartPositionInClip()) {
                        float duration = ArithUtil.sub(left.getDuration(), ArithUtil.sub(left.getEndPositionInClip(), right.getStartPositionInClip()));
                        left.setEndPositionInClip(ArithUtil.add(left.getStartPositionInClip(), duration));
                        if (duration < MIN_SPACE) {
                            tobeDeleted.add(left);
                        }
                    }
                }
            }
            for (Mark m : tobeDeleted) {
                deleteMark(m);
            }
        }
        updateMarkList();
    }

    /**
     * 添加mark, mark标记的是相对于整个时间轴的片段
     * @param mark
     */
    public void addMark(Mark mark) {
        if (mark == null) {
            return;
        }
        float dur = ArithUtil.sub(mark.getEnd(), mark.getStart());
        mark.setEnd(Math.min(mark.getEnd(), getDuration()));
        mark.setStart(Math.max(0, ArithUtil.sub(mark.getEnd(), dur)));
        float[] a = new float[mClipList.size()];
        float duration = 0;
        for (int i = 0, len = mClipList.size(); i < len; i++) {
            Clip clip = mClipList.get(i);
            duration = ArithUtil.add(duration, clip.getDuration());
            a[i] = duration;
        }
        for (int i = 0, len = a.length; i < len; i++) {
            if (mark.getStart() < a[i]) {
                Clip selectedClip = mClipList.get(i);
                float offset = i > 0 ? a[i - 1] : 0;
                float space = ArithUtil.sub(mark.getEnd(), mark.getStart());
                mark.setStartPositionInClip(ArithUtil.sub(mark.getStart(), offset));
                mark.setEndPositionInClip(ArithUtil.add(mark.getStartPositionInClip(), space));
                selectedClip.addMark(mark);
                break;
            }
        }
        mMarkSet.add(mark);
        updateMarkList();
    }

    public void insertMark(Mark mark) {
        if (mark == null) {
            return;
        }
        clipMark(mark);
    }

    public void deleteMark(Mark mark) {
        if (mark == null) {
            return;
        }
        mMarkSet.remove(mark);
        float[] a = new float[mClipList.size()];
        float duration = 0;
        for (int i = 0, len = mClipList.size(); i < len; i++) {
            Clip clip = mClipList.get(i);
            duration = ArithUtil.add(duration, clip.getDuration());
            a[i] = duration;
        }
        for (int i = 0, len = a.length; i < len; i++) {
            if (mark.getStart() < a[i]) {
                Clip selectedClip = mClipList.get(i);
                Iterator<Mark> it = selectedClip.getMarkList().iterator();
                while (it.hasNext()) {
                    Mark m = it.next();
                    if (m == null) {
                        return;
                    }
                    if (m.getId().equals(mark.getId())) {
                        it.remove();
                        updateMarkList();
                        return;
                    }
                }
            }
        }
    }

    public void deleteMark(String markId) {
        Mark mark = getMark(markId);
        if (mark == null) {
            return;
        }

        deleteMark(mark);
    }

    public Mark getMark(String id) {
        for (Mark m : mMarkSet) {
            if (m.getId().equals(id)) {
                return m;
            }
        }
        return null;
    }



    /**
     * left sibling     p     right sibling
     * ---------------  |  ----------------
     *
     * @param position
     * @return
     */
    public Mark getLeftSiblingMark(float position) {
        if (mMarkSet.size() == 0) {
            return null;
        }
        float closest = 0f;
        Mark left = null;
        for (Mark m : mMarkSet) {
            if (m.getEnd() < position) {
                if (closest <= m.getEnd()) {
                    left = m;
                }
                closest = Math.max(closest, m.getEnd());
            }
        }
        return left;
    }

    /**
     * left sibling     p     right sibling
     * ---------------  |  ----------------
     *
     * @param start
     * @return
     */
    public Mark getRightSiblingMark(float start) {
        if (mMarkSet.size() == 0) {
            return null;
        }
        for (Mark m : mMarkSet) {
            if (m.getStart() > start) {
                return m;
            }
        }
        return null;
    }



    public int getMarkIndex(Mark mark) {
        if (mark == null) {
            return -1;
        }
        if (mMarkSet == null) {
            return -1;
        }
        int i = 0;
        for (Mark m : mMarkSet) {
            if (m.equals(mark)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public void updateMarkList() {
        float start = 0;
        for (Clip clip : mClipList) {
            Set<Mark> markList = clip.getMarkList();
            for (Mark mark : markList) {
                // 重新设置start, end
                mark.setStart(ArithUtil.add(start, mark.getStartPositionInClip()));
                float space = Math.min(mark.getDuration(), ArithUtil.sub(getDuration(), mark.getStart()));
                mark.setEnd(ArithUtil.add(mark.getStart(), space));
                mark.setEndPositionInClip(ArithUtil.add(mark.getStartPositionInClip(), space));
            }
            start = ArithUtil.add(start, clip.getDuration());
        }
        Mark next;
        for (Iterator<Mark> it = mMarkSet.iterator(); it.hasNext();) {
            Mark current = it.next();
            next = mMarkSet.higher(current);
            if (next != null) {
                if (next.getStart() > current.getEnd()) {
                    current.setHeadLength(current.getDuration());
                } else {
                    current.setHeadLength(ArithUtil.sub(next.getStart(), current.getStart()));
                }
            } else {
                current.setHeadLength(current.getDuration());
            }
        }
    }

    public List<Mark> getMarkList() {
        return new ArrayList<>(mMarkSet);
    }

    public List<Mark> getHookedMarkList(float position) {
        mHookedList.clear();
        if (mMarkSet == null || mMarkSet.size() == 0) {
            return mHookedList;
        }
        for (Mark mark : mMarkSet) {
            if ((position >= mark.getStart() || mark.getStart() - position <= 0.2f)
                    && (position <= mark.getEnd() || position - mark.getEnd() <= 0.2f)) {
                mHookedList.add(mark);
            }
        }

        return mHookedList;
    }
    public List<Mark> getMonologueMarkList(float position) {
        mHookedList.clear();
        if (mMarkSet == null || mMarkSet.size() == 0) {
            return mHookedList;
        }
        for (Mark mark : mMarkSet) {
            if (position >= mark.getStart() && position < mark.getEnd()) {

                mHookedList.add(mark);
            }
        }

        return mHookedList;
    }

    public List<Mark> getTrimmedMarkListInClip(int index, float start, float end) {
        List<Mark> result = new ArrayList<>();
        if (mClipList == null ||  index < 0 || index >= mClipList.size()) {
            return result;
        }
        Clip clip = mClipList.get(index);
        return clip.getTrimmedMarkList(start, end);
    }

    public float getDuration() {
        return VideoEditManger.getVideoDuration() * 10f;
    }

    public List<Clip> getClipList() {
        return mClipList;
    }

    public void clear() {
        for (Clip clip : mClipList) {
            clip.clear();
        }
        mClipList.clear();
        mMarkSet.clear();
    }

    public void reset() {
        for (Clip clip : mClipList) {
            clip.clear();
        }
        mMarkSet.clear();
    }

    public void restore(List<? extends Mark> markList) {
        if (markList == null) {
            return;
        }
        reset();
        for (Mark m : markList) {
            addMark(m);
        }
    }

    public boolean isRightInMarkArea(float position) {
        for (Mark mark : mMarkSet) {
            if (position >= mark.getStart() && position <= mark.getEnd()) {
                return true;
            }
        }
        float totalSpace = getMaxSpace(position);
        Log.d(TAG, "isRightInMarkArea, totalSpace: " + totalSpace);
        return totalSpace < MIN_SPACE;
    }

    public boolean isRightInDurationFoot(float position) {
        float totalSpace = getDuration() - position;
        return totalSpace < MIN_SPACE;
    }

    public boolean canInsert(float startPosition, float endPosition) {
        if (mMarkSet.size() == 0) {
            return true;
        }
        for (Mark m : mMarkSet) {
            if (m.getStart() >= endPosition) {
                return true;
            }
            if (m.getStart() >= startPosition && m.getStart() < endPosition) {
                return false;
            } else if (m.getEnd() > startPosition && m.getEnd() <= endPosition) {
                return false;
            }
        }
        return true;
    }

    public float getMaxSpace(float startPosition) {
        for (Mark m : mMarkSet) {
            if (startPosition < m.getStart()) {
                return m.getStart() - startPosition;
            }
        }
        return getDuration() - startPosition;
    }

    public TreeSet<Mark> getRangeMarkSet(float start, float end) {
        TreeSet<Mark> result = new TreeSet<>();
        for (Mark m : mMarkSet) {
            if (start >= m.getStart() && start < m.getEnd()) {
                result.add(m);
            } else if (end <= m.getEnd() && end > m.getStart()) {
                result.add(m);
            } else if (start <= m.getStart() && end >= m.getEnd()) {
                result.add(m);
            } else if (end <= m.getStart()) {
                break;
            }
        }
        return result;
    }

    public Set<Mark> clipMark(Mark mark) {
        if (mark == null) {
            return null;
        }
        deleteMark(mark);
        float start = mark.getStart();
        float end = mark.getEnd();
        TreeSet<Mark> list = getRangeMarkSet(start, end);
        if (list.size() >= 2) {
            Mark left = list.first();
            Mark right = list.higher(left);
            if (start > left.getStart()) {
                left.setEnd(start);
                mark.setEnd(right.getStart());
            } else if (start < left.getStart()) {
                mark.setEnd(left.getStart());
            } else {
                // 起始点重合特殊，替换
                left.setEnd(start);
                mark.setEnd(right.getStart());
            }
            if ((ArithUtil.sub(left.getEnd(), left.getStart())) < MIN_SPACE) {
                deleteMark(left);
            } else {
                deleteMark(left);
                addMark(left);
            }
            if ((ArithUtil.sub(mark.getEnd(), mark.getStart())) < MIN_SPACE) {
                deleteMark(mark);
            } else {
                addMark(mark);
            }
        } else if (list.size() == 1) {
            Mark left = list.first();
            if (start > left.getStart()) {
                left.setEnd(start);
            } else if (start < left.getStart()) {
                mark.setEnd(left.getStart());
            } else {
                // 起始点重合特殊，替换
                left.setEnd(start);
            }
            if (ArithUtil.sub(left.getEnd(), left.getStart()) < MIN_SPACE) {
                deleteMark(left);
            } else {
                deleteMark(left);
                addMark(left);
            }
            if (ArithUtil.sub(mark.getEnd(), mark.getStart()) < MIN_SPACE) {
                deleteMark(mark);
            } else {
                addMark(mark);
            }
        } else {
            if (ArithUtil.sub(mark.getEnd(), mark.getStart()) < MIN_SPACE) {
                deleteMark(mark);
            } else {
                addMark(mark);
            }
        }
        return list;
    }
}
