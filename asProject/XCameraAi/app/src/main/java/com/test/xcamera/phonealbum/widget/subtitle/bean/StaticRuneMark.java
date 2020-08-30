package com.test.xcamera.phonealbum.widget.subtitle.bean;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

import com.facebook.common.internal.Objects;


/**
 * 静态贴纸
 */

public class StaticRuneMark extends StickerMark {
    /** 贴纸图片路径 */
    private String mStickerPath;

    /** 贴纸缩略图路径 */
    private String mThumbnailPath;

    public String getStickerPath() {
        return mStickerPath;
    }

    public void setStickerPath(String stickerPath) {
        mStickerPath = stickerPath;
    }

    public String getThumbnailPath() {
        return mThumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        mThumbnailPath = thumbnailPath;
    }

    public Mark copy() {
        StaticRuneMark p = new StaticRuneMark();
        p.mAngle = mAngle;
        p.mPoint = mPoint;
        p.mScale = mScale;
        p.mStart = mStart;
        p.mEnd = mEnd;
        p.mStartPositionInClip = mStartPositionInClip;
        p.mEndPositionInClip = mEndPositionInClip;
        p.mType = mType;
        p.isDraw = isDraw;
        p.mTrackId = mTrackId;
        p.mId = mId;
        p.mCreateTime = mCreateTime;
        p.mTitle = mTitle;
        p.mHeadLength = mHeadLength;
        p.mX = mX;
        p.mY = mY;
        p.mCanvasWidth = mCanvasWidth;
        p.mCanvasHeight = mCanvasHeight;
        p.mStickerPath = mStickerPath;
        p.mThumbnailPath = mThumbnailPath;
        return p;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StaticRuneMark)) return false;
        if (!super.equals(o)) return false;
        StaticRuneMark mark = (StaticRuneMark) o;
        return Objects.equal(mStickerPath, mark.mStickerPath) &&
                Objects.equal(mThumbnailPath, mark.mThumbnailPath);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(
                super.hashCode(),
                mStickerPath,
                mThumbnailPath);
    }

    @Override
    public String toString() {
        return super.toString() + ", StaticRuneMark{" +
                ", mStickerPath=" + mStickerPath +
                ", mThumbnailPath=" + mThumbnailPath +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.mScale);
        dest.writeFloat(this.mAngle);
        dest.writeParcelable(this.mPoint, flags);
        dest.writeFloat(this.mStart);
        dest.writeFloat(this.mEnd);
        dest.writeFloat(this.mStartPositionInClip);
        dest.writeFloat(this.mEndPositionInClip);
        dest.writeInt(this.mType);
        dest.writeByte(this.isDraw ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mTrackId);
        dest.writeString(this.mId);
        dest.writeString(this.mTitle);
        dest.writeFloat(this.mX);
        dest.writeFloat(this.mY);
        dest.writeFloat(this.mCanvasWidth);
        dest.writeFloat(this.mCanvasHeight);
        dest.writeString(this.mStickerPath);
        dest.writeString(this.mThumbnailPath);
        dest.writeLong(this.mCreateTime);
    }

    public StaticRuneMark() {
    }

    protected StaticRuneMark(Parcel in) {
        this.mScale = in.readFloat();
        this.mAngle = in.readFloat();
        this.mPoint = in.readParcelable(PointF.class.getClassLoader());
        this.mStart = in.readFloat();
        this.mEnd = in.readFloat();
        this.mStartPositionInClip = in.readFloat();
        this.mEndPositionInClip = in.readFloat();
        this.mType = in.readInt();
        this.isDraw = in.readByte() != 0;
        this.mTrackId = in.readInt();
        this.mId = in.readString();
        this.mTitle = in.readString();
        this.mX = in.readFloat();
        this.mY = in.readFloat();
        this.mCanvasWidth = in.readFloat();
        this.mCanvasHeight = in.readFloat();
        this.mStickerPath = in.readString();
        this.mThumbnailPath = in.readString();
        this.mCreateTime = in.readLong();
    }

    public static final Parcelable.Creator<StaticRuneMark> CREATOR = new Parcelable.Creator<StaticRuneMark>() {
        @Override
        public StaticRuneMark createFromParcel(Parcel source) {
            return new StaticRuneMark(source);
        }

        @Override
        public StaticRuneMark[] newArray(int size) {
            return new StaticRuneMark[size];
        }
    };
}
