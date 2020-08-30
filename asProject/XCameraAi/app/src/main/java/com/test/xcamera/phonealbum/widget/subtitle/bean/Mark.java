package com.test.xcamera.phonealbum.widget.subtitle.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public class Mark implements Comparable<Mark>, Parcelable {

    /**
     * 自由字幕、自定义贴纸和静态贴纸的状态控制
     *
     * 1.可编辑状态
     * 2.不可编辑状态
     * 3.区域预览状态
     * 三种状态互斥 记录状态的成员变量字段不参与序列化
     * 比较棘手的是 可编辑状态和区域预览状态相对于所有笔迹或者说是符文来说 具有唯一性
     * 所以在设置一个笔迹状态之后，需要将时间轴上所有其他符文的状态全部设置为不可编辑
     * 为了避免循环遍历 我们需要记录当前非不可编辑状态的笔迹id
     */
    public static final int EDIT = 0xb0;
    public static final int NORMAL = 0xb1;
    public static final int AREA_PREVIEW = 0xb2;
    @IntDef({EDIT, NORMAL, AREA_PREVIEW})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MarkStatus{}

    public static final int TRACK_TOP = 0;
    public static final int TRACK_MIDDLE = 1;
    public static final int TRACK_BOTTOM = 2;
    public static final int TRACK_DYNAMIC = 3;

    /** 红色笔迹 */
    public static final int MARK_TYPE_RED = 0;
    /** 蓝色笔迹 */
    public static final int MARK_TYPE_BLUE = 1;
    /** 笔迹对应的实际起始位置 */
    protected float mStart;
    /** 笔迹对应的实际结束位置 */
    protected float mEnd;
    /** 在clip中的起始位置 */
    protected float mStartPositionInClip;
    /** 在clip中的结束位置 */
    protected float mEndPositionInClip;
    /** 未重叠的笔迹头部长度 */
    protected transient float mHeadLength;
    /** 笔迹类型 */
    protected int mType = MARK_TYPE_RED;
    /** 是否绘制笔迹 */
    protected boolean isDraw = true;
    /** 时间轴id */
    protected int mTrackId;
    /** 唯一id */
    protected String mId;
    /** 笔迹名称 */
    protected String mTitle = "";
    /** 创建时间，用于排序 */
    protected long mCreateTime;
    /** 笔迹状态，用来控制符文的编辑状态，不参与序列化 */
    protected transient @MarkStatus int mMarkStatus = NORMAL;


    public float getStart() {
        return mStart;
    }
    public void setStart(float start) {
        mStart = start;
    }

    public float getEnd() {
        return mEnd;
    }

    public void setEnd(float end) {
        mEnd = end;
    }

    public float getStartPositionInClip() {
        return mStartPositionInClip;
    }

    public void setStartPositionInClip(float start) {
        mStartPositionInClip = start;
    }

    public float getEndPositionInClip() {
        return mEndPositionInClip;
    }

    public void setEndPositionInClip(float end) {
        mEndPositionInClip = end;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public boolean isDraw() {
        return isDraw;
    }

    public void setDraw(boolean draw) {
        isDraw = draw;
    }

    public int getTrackId() {
        return mTrackId;
    }

    public void setTrackId(int trackId) {
        mTrackId = trackId;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }


    public float getHeadLength() {
        return mHeadLength;
    }

    public void setHeadLength(float headLength) {
        mHeadLength = headLength;
    }

    public long getCreateTime() {
        return mCreateTime;
    }

    public void setCreateTime(long createTime) {
        mCreateTime = createTime;
    }

    public void setMarkStatus(@MarkStatus int markStatus) {
        mMarkStatus = markStatus;
    }

    public @MarkStatus int getMarkStatus() {
        return mMarkStatus;
    }

    public Mark copy() {
        Mark copy = new Mark();
        copy.setStart(mStart);
        copy.setEnd(mEnd);
        copy.setStartPositionInClip(mStartPositionInClip);
        copy.setEndPositionInClip(mEndPositionInClip);
        copy.setType(mType);
        copy.setDraw(isDraw);
        copy.setTrackId(mTrackId);
        copy.setTitle(mTitle);
        copy.setId(mId);
        copy.setCreateTime(mCreateTime);
        copy.setHeadLength(mHeadLength);
        return copy;
    }

    public float getDuration() {
        return mEndPositionInClip - mStartPositionInClip;
    }



    @Override
    public String toString() {
        return "Mark{" +
                "\nmStart=" + mStart +
                "\n, mEnd=" + mEnd +
                "\n, mStartPositionInClip=" + mStartPositionInClip +
                "\n, mEndPositionInClip=" + mEndPositionInClip +
                "\n, mType=" + mType +
                "\n, isDraw=" + isDraw +
                "\n, mTrackId=" + mTrackId +
                "\n, mId=" + mId +
                "\n, mTitle=" + mTitle +
                "\n, mHeadLength=" + mHeadLength +
                "\n, mCreateTime=" + mCreateTime +
                '}';
    }

    @Override
    public int compareTo(Mark another) {
        if (another == null) {
            return 1;
        }
        if (mStart > another.getStart()) {
            return 1;
        } else if (mStart < another.getStart()) {
            return -1;
        } else {
            if (mCreateTime > another.getCreateTime()) {
                return 1;
            } else if (mCreateTime < another.getCreateTime()) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.mStart);
        dest.writeFloat(this.mEnd);
        dest.writeFloat(this.mStartPositionInClip);
        dest.writeFloat(this.mEndPositionInClip);
        dest.writeInt(this.mType);
        dest.writeByte(this.isDraw ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mTrackId);
        dest.writeString(this.mId);
        dest.writeString(this.mTitle);
        dest.writeLong(this.mCreateTime);
    }

    public Mark() {
    }

    protected Mark(Parcel in) {
        this.mStart = in.readFloat();
        this.mEnd = in.readFloat();
        this.mStartPositionInClip = in.readFloat();
        this.mEndPositionInClip = in.readFloat();
        this.mType = in.readInt();
        this.isDraw = in.readByte() != 0;
        this.mTrackId = in.readInt();
        this.mId = in.readString();
        this.mTitle = in.readString();
        this.mCreateTime = in.readLong();
    }

}