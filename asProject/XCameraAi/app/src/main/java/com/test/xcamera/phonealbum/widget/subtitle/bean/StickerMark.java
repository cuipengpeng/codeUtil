package com.test.xcamera.phonealbum.widget.subtitle.bean;

import android.annotation.SuppressLint;
import android.graphics.PointF;

import com.facebook.common.internal.Objects;


@SuppressLint("ParcelCreator")
public class StickerMark extends Mark {
    /** 缩放比率 */
    protected float mScale;
    /** 旋转角度 */
    protected float mAngle;
    /** 旋转中心点 */
    protected PointF mPoint;
    /** 旋转中心点x轴位置相对于屏幕中央位置百分比 */
    protected float mX;
    /** 旋转中心点y轴位置相对于屏幕中央位置百分比 */
    protected float mY;
    /** 画布宽度（视频窗口宽度） */
    protected float mCanvasWidth;
    /** 画布高度（视频窗口高度） */
    protected float mCanvasHeight;

    public float getScale() {
        return mScale;
    }

    public void setScale(float scale) {
        mScale = scale;
    }

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }

    public PointF getPoint() {
        return mPoint;
    }

    public void setPoint(PointF point) {
        mPoint = point;
    }

    public boolean hasCenterPoint() {
        return mPoint != null;
    }

    public float getX() {
        return mX;
    }

    public void setX(float x) {
        mX = x;
    }

    public float getY() {
        return mY;
    }

    public void setY(float y) {
        mY = y;
    }

    public float getCanvasWidth() {
        return mCanvasWidth;
    }

    public void setCanvasWidth(float width) {
        mCanvasWidth = width;
    }

    public float getCanvasHeight() {
        return mCanvasHeight;
    }

    public void setCanvasHeight(float height) {
        mCanvasHeight = height;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StickerMark)) return false;
        if (!super.equals(o)) return false;
        StickerMark property = (StickerMark) o;
        return Float.compare(property.mScale, mScale) == 0 &&
                Float.compare(property.mAngle, mAngle) == 0 &&
                Float.compare(property.mX, mX) == 0 &&
                Float.compare(property.mY, mY) == 0 &&
                Float.compare(property.mCanvasWidth, mCanvasWidth) == 0 &&
                Float.compare(property.mCanvasHeight, mCanvasHeight) == 0 &&
                Objects.equal(mPoint, property.mPoint);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), mScale, mAngle, mPoint, mX, mY, mCanvasWidth, mCanvasHeight);
    }

    @Override
    public String toString() {
        return super.toString() + "\n, StickerProperty{" +
                "\nmScale=" + mScale +
                "\n, mAngle=" + mAngle +
                "\n, mPoint=" + mPoint +
                "\n, mX=" + mX +
                "\n, mY=" + mY +
                "\n, mCanvasWidth=" + mCanvasWidth +
                "\n, mCanvasHeight=" + mCanvasHeight +
                '}';
    }
}
