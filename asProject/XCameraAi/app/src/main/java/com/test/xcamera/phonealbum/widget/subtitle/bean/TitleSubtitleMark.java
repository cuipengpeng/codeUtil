package com.test.xcamera.phonealbum.widget.subtitle.bean;

import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Layout;


/**
 * zjh
 */

public class TitleSubtitleMark extends Mark implements Parcelable {
    /** 文字 */
    private String mText;
    /** 字体颜色 */
    private String mFontColor;
    /** 字体颜色亮度 */
    private float mColorLightness;
    /** 字体id */
    private String mFontId;
    /** 文字对齐方式 */
    private Layout.Alignment mAlignment;
    /** 字体大小 */
    private float mTextSize;
    /** 多行字幕行宽 */
    private float mLineLength;
    /** 多行字幕行距 */
    private float mLineSpace;


    private Rect mTextRect;

    private float mTextX;
    private float mTextY;

    private PointF mTextPoint;
    private float zValue;

    public float getZValue() {
        return zValue;
    }

    public void setZValue(float zValue) {
        this.zValue = zValue;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;

    }

    public void setTextPoint(PointF point) {
        mTextPoint = point;
    }

    public PointF getTextPoint() {
        return mTextPoint;
    }

    public void setTextX(float x) {
        mTextX = x;
    }

    public void setTextY(float y) {
        mTextY = y;
    }

    public float getTextX() {
        return mTextX;
    }

    public float getTextY() {
        return mTextY;
    }

    public String getFontColor() {
        return mFontColor;
    }

    public void setFontColor(String fontColor) {
        mFontColor = fontColor;
    }

    public float getColorLightness() {
        return mColorLightness;
    }

    public void setColorLightness(float lightness) {
        mColorLightness = lightness;
    }

    public String getFontId() {
        return mFontId;
    }

    public void setFontId(String fontId) {
        mFontId = fontId;
    }

    public Layout.Alignment getAlignment() {
        return mAlignment;
    }

    public void setAlignment(Layout.Alignment alignment) {
        mAlignment = alignment;
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float textSize) {
        mTextSize = textSize;
    }

    public float getLineLength() {
        return mLineLength;
    }

    public void setLineLength(float lineLength) {
        mLineLength = lineLength;
    }

    public float getLineSpace() {
        return mLineSpace;
    }

    public void setLineSpace(float lineSpace) {
        mLineSpace = lineSpace;
    }


    public void setTextRect(Rect rect) {
        mTextRect = rect;
    }

    public Rect getTextRect() {
        return mTextRect;
    }

    public Mark copy() {
        TitleSubtitleMark p = new TitleSubtitleMark();
        p.mAlignment = mAlignment;
        p.mFontColor = mFontColor;
        p.mColorLightness = mColorLightness;
        p.mFontId = mFontId;
        p.mText = mText;

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
        p.mTextSize = mTextSize;
        p.mLineLength = mLineLength;
        p.mLineSpace = mLineSpace;

        p.mTextRect = mTextRect;
        p.mTextX = mTextX;
        p.mTextY = mTextY;
        p.zValue = zValue;
        return p;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TitleSubtitleMark)) return false;
        if (!super.equals(o)) return false;
        TitleSubtitleMark that = (TitleSubtitleMark) o;
        return Float.compare(that.mColorLightness, mColorLightness) == 0 &&
                Float.compare(that.mTextSize, mTextSize) == 0 &&
                Float.compare(that.mLineLength, mLineLength) == 0 &&
                Float.compare(that.mLineSpace, mLineSpace) == 0 &&
                Float.compare(that.mTextX, mTextX) == 0 &&
                Float.compare(that.mTextY, mTextY) == 0 &&

                mAlignment == that.mAlignment ;

    }



    @Override
    public String toString() {
        return super.toString() + "\nTitleSubtitleMark{" +
                "\nmText='" + mText + '\'' +
                "\n, mFontColor='" + mFontColor + '\'' +
                "\n, mColorLightness=" + mColorLightness +
                "\n, mFontId='" + mFontId + '\'' +
                "\n, mAlignment=" + mAlignment +
                "\n, mTextSize=" + mTextSize +
                "\n, mLineLength=" + mLineLength +
                "\n, mLineSpace=" + mLineSpace +
                "\n, mTextRect=" + mTextRect +
                "\n, mTextX=" + mTextX +
                "\n, mTextY=" + mTextY +
                "\n, mTextPoint=" + mTextPoint +
                "\n, zValue=" + zValue +
                '}';
    }

    public TitleSubtitleMark() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.mText);
        dest.writeString(this.mFontColor);
        dest.writeFloat(this.mColorLightness);
        dest.writeString(this.mFontId);
        dest.writeInt(this.mAlignment == null ? -1 : this.mAlignment.ordinal());
        dest.writeFloat(this.mTextSize);
        dest.writeFloat(this.mLineLength);
        dest.writeFloat(this.mLineSpace);
        dest.writeParcelable(this.mTextRect, flags);
        dest.writeFloat(this.mTextX);
        dest.writeFloat(this.mTextY);
        dest.writeParcelable(this.mTextPoint, flags);
        dest.writeFloat(this.zValue);
    }

    protected TitleSubtitleMark(Parcel in) {
        super(in);
        this.mText = in.readString();
        this.mFontColor = in.readString();
        this.mColorLightness = in.readFloat();
        this.mFontId = in.readString();
        int tmpMAlignment = in.readInt();
        this.mAlignment = tmpMAlignment == -1 ? null : Layout.Alignment.values()[tmpMAlignment];
        this.mTextSize = in.readFloat();
        this.mLineLength = in.readFloat();
        this.mLineSpace = in.readFloat();
        this.mTextRect = in.readParcelable(Rect.class.getClassLoader());
        this.mTextX = in.readFloat();
        this.mTextY = in.readFloat();
        this.mTextPoint = in.readParcelable(PointF.class.getClassLoader());
        this.zValue = in.readFloat();
    }

    public static final Creator<TitleSubtitleMark> CREATOR = new Creator<TitleSubtitleMark>() {
        @Override
        public TitleSubtitleMark createFromParcel(Parcel source) {
            return new TitleSubtitleMark(source);
        }

        @Override
        public TitleSubtitleMark[] newArray(int size) {
            return new TitleSubtitleMark[size];
        }
    };
}
