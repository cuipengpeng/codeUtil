package com.test.xcamera.phonealbum.subtitle;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by liutao on 24/04/2017.
 */

public class TextStyle implements Parcelable {

    private String mStartColor;
    private String mEndColor;
    private String mFirstStrokeColor;
    private float mFirstStrokeWidth;
    private String mSecondStrokeColor;
    private float mSecondStrokeWidth;
    private String mName;
    private boolean isItalic;
    private transient boolean isSelected;

    public String getStartColor() {
        return mStartColor;
    }

    public void setStartColor(String startColor) {
        mStartColor = startColor;
    }

    public String getEndColor() {
        return mEndColor;
    }

    public void setEndColor(String endColor) {
        mEndColor = endColor;
    }

    public String getFirstStrokeColor() {
        return mFirstStrokeColor;
    }

    public void setFirstStrokeColor(String firstStrokeColor) {
        mFirstStrokeColor = firstStrokeColor;
    }

    public float getFirstStrokeWidth() {
        return mFirstStrokeWidth;
    }

    public void setFirstStrokeWidth(float firstStrokeWidth) {
        mFirstStrokeWidth = firstStrokeWidth;
    }

    public String getSecondStrokeColor() {
        return mSecondStrokeColor;
    }

    public void setSecondStrokeColor(String secondStrokeColor) {
        mSecondStrokeColor = secondStrokeColor;
    }

    public float getSecondStrokeWidth() {
        return mSecondStrokeWidth;
    }

    public void setSecondStrokeWidth(float secondStrokeWidth) {
        mSecondStrokeWidth = secondStrokeWidth;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isItalic() {
        return isItalic;
    }

    public void setItalic(boolean italic) {
        isItalic = italic;
    }

    public TextStyle copy() {
        TextStyle ts = new TextStyle();
        ts.mStartColor = mStartColor;
        ts.mEndColor = mEndColor;
        ts.mFirstStrokeColor = mFirstStrokeColor;
        ts.mFirstStrokeWidth = mFirstStrokeWidth;
        ts.mSecondStrokeColor = mSecondStrokeColor;
        ts.mSecondStrokeWidth = mSecondStrokeWidth;
        ts.mName = mName;
        ts.isSelected = isSelected;
        ts.isItalic = isItalic;
        return ts;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mStartColor);
        dest.writeString(this.mEndColor);
        dest.writeString(this.mFirstStrokeColor);
        dest.writeFloat(this.mFirstStrokeWidth);
        dest.writeString(this.mSecondStrokeColor);
        dest.writeFloat(this.mSecondStrokeWidth);
        dest.writeString(this.mName);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isItalic ? (byte) 1 : (byte) 0);
    }

    public TextStyle() {
    }

    protected TextStyle(Parcel in) {
        this.mStartColor = in.readString();
        this.mEndColor = in.readString();
        this.mFirstStrokeColor = in.readString();
        this.mFirstStrokeWidth = in.readFloat();
        this.mSecondStrokeColor = in.readString();
        this.mSecondStrokeWidth = in.readFloat();
        this.mName = in.readString();
        this.isSelected = in.readByte() != 0;
        this.isItalic = in.readByte() != 0;
    }

    public static final Parcelable.Creator<TextStyle> CREATOR = new Parcelable.Creator<TextStyle>() {
        @Override
        public TextStyle createFromParcel(Parcel source) {
            return new TextStyle(source);
        }

        @Override
        public TextStyle[] newArray(int size) {
            return new TextStyle[size];
        }
    };





    @Override
    public String toString() {
        return "TextStyle{" +
                "\nmStartColor='" + mStartColor + '\'' +
                "\n, mEndColor='" + mEndColor + '\'' +
                "\n, mFirstStrokeColor='" + mFirstStrokeColor + '\'' +
                "\n, mFirstStrokeWidth=" + mFirstStrokeWidth +
                "\n, mSecondStrokeColor='" + mSecondStrokeColor + '\'' +
                "\n, mSecondStrokeWidth=" + mSecondStrokeWidth +
                "\n, mName='" + mName + '\'' +
                "\n, isItalic=" + isItalic +
                "\n, isSelected=" + isSelected +
                '}';
    }
}
