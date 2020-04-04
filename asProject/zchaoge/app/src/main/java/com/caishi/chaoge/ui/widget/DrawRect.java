package com.caishi.chaoge.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.caishi.chaoge.R;

import java.util.List;

import static com.caishi.chaoge.utils.ConstantUtils.HANDCLICK_DURATION;
import static com.caishi.chaoge.utils.ConstantUtils.HANDMOVE_DISTANCE;

/**
 * Created by Administrator on 2018/6/20.
 */

public class DrawRect extends View {
    private final static String TAG = "DrawRect";
    private OnTouchListener mListener;
    private onDrawRectClickListener mDrawRectClickListener;
    private PointF prePointF = new PointF(0, 0);
    private RectF rotationRectF = new RectF();
    private List<PointF> mListPointF;
    private Path rectPath = new Path();
    private boolean isInnerDrawRect = false;
    private Bitmap rotationImgBtn = BitmapFactory.decodeResource(getResources(), R.drawable.ic_size_right);
    private long mPrevMillionSecond = 0;
    private double mClickMoveDistance = 0.0D;
    private Paint mRectPaint = new Paint();
    private boolean mMoveOutScreen = false;
    //在方框内绘制的图片路径
    float tempTargetX = 0;
    float tempTargetY = 0;
    float lastOffset = 1;
    float lastCenterPointFX = 0;
    float lastCenterPointFY = 0;


    public DrawRect(Context context) {
        this(context, null);
    }

    public DrawRect(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initRectPaint();
    }

    private void initRectPaint() {
        mRectPaint = new Paint();
        // 设置颜色
        mRectPaint.setColor(Color.parseColor("#000000"));
        // 设置抗锯齿
        mRectPaint.setAntiAlias(true);
        // 设置线宽
        mRectPaint.setStrokeWidth(3);
        // 设置非填充
        mRectPaint.setStyle(Paint.Style.STROKE);
    }

    public void setDrawRect(List<PointF> list) {
        mListPointF = list;
        invalidate();
    }

    public List<PointF> getDrawRect() {
        return mListPointF;
    }

    public void setOnTouchListener(OnTouchListener listener) {
        mListener = listener;
    }

    public void setDrawRectClickListener(onDrawRectClickListener drawRectClickListener) {
        this.mDrawRectClickListener = drawRectClickListener;
    }


    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mListPointF == null || mListPointF.size() < 4) {
            return;
        }
        rectPath.reset();
        rectPath.moveTo(mListPointF.get(0).x, mListPointF.get(0).y);
        rectPath.lineTo(mListPointF.get(1).x, mListPointF.get(1).y);
        rectPath.lineTo(mListPointF.get(2).x, mListPointF.get(2).y);
        rectPath.lineTo(mListPointF.get(3).x, mListPointF.get(3).y);
        rectPath.close();
        canvas.drawPath(rectPath, mRectPaint);
//        if (viewMode == ConstantUtils.EDIT_MODE_CAPTION) {//绘制字幕对其按钮
//        canvas.drawBitmap(alignImgArray, mListPointF.get(0).x - alignImgArray.getHeight() / 2, mListPointF.get(0).y - alignImgArray.getWidth() / 2, mRectPaint);
//        alignRectF.set(mListPointF.get(0).x - alignImgArray.getWidth() / 2, mListPointF.get(0).y - alignImgArray.getHeight() / 2, mListPointF.get(0).x + alignImgArray.getWidth() / 2, mListPointF.get(0).y + alignImgArray.getWidth() / 2);
//        } else if (viewMode == ConstantUtils.EDIT_MODE_STICKER) {//绘制水平翻转按钮
////            canvas.drawBitmap(horizontalFlipImgBtn, mListPointF.get(0).x - horizontalFlipImgBtn.getHeight() / 2, mListPointF.get(0).y - horizontalFlipImgBtn.getWidth() / 2, mRectPaint);
////            horizFlipRectF.set(mListPointF.get(0).x - horizontalFlipImgBtn.getWidth() / 2, mListPointF.get(0).y - horizontalFlipImgBtn.getHeight() / 2, mListPointF.get(0).x + horizontalFlipImgBtn.getWidth() / 2, mListPointF.get(0).y + horizontalFlipImgBtn.getHeight() / 2);
//            if (mHasAudio) {
////                canvas.drawBitmap(muteImgArray[mStickerMuteIndex], mListPointF.get(1).x - muteImgArray[mStickerMuteIndex].getHeight() / 2, mListPointF.get(1).y - muteImgArray[mStickerMuteIndex].getWidth() / 2, mRectPaint);
////                muteRectF.set(mListPointF.get(1).x - muteImgArray[mStickerMuteIndex].getWidth() / 2, mListPointF.get(1).y - muteImgArray[mStickerMuteIndex].getHeight() / 2, mListPointF.get(1).x + muteImgArray[mStickerMuteIndex].getWidth() / 2, mListPointF.get(1).y + muteImgArray[mStickerMuteIndex].getHeight() / 2);
//            } else {
//                muteRectF.set(0, 0, 0, 0);
//            }
//        } else if (viewMode == ConstantUtils.EDIT_MODE_WATERMARK) {
//            if (waterMarkBitmap != null) {
//                canvas.drawBitmap(waterMarkBitmap, new Rect(0, 0, waterMarkBitmap.getWidth(), waterMarkBitmap.getHeight()), new RectF(mListPointF.get(0).x, mListPointF.get(0).y, mListPointF.get(2).x, mListPointF.get(2).y), null);
//            }
//        }


        // 绘制旋转放缩按钮
        canvas.drawBitmap(rotationImgBtn, mListPointF.get(2).x - rotationImgBtn.getHeight() / 2, mListPointF.get(2).y - rotationImgBtn.getWidth() / 2, mRectPaint);
        rotationRectF.set(mListPointF.get(2).x - rotationImgBtn.getWidth() / 2, mListPointF.get(2).y - rotationImgBtn.getHeight() / 2, mListPointF.get(2).x + rotationImgBtn.getWidth() / 2, mListPointF.get(2).y + rotationImgBtn.getHeight() / 2);
        //绘制删除按钮
//        canvas.drawBitmap(deleteImgBtn, mListPointF.get(3).x - deleteImgBtn.getWidth() / 2, mListPointF.get(3).y - deleteImgBtn.getHeight() / 2, mRectPaint);
//        deleteRectF.set(mListPointF.get(3).x - deleteImgBtn.getWidth() / 2, mListPointF.get(3).y - deleteImgBtn.getHeight() / 2, mListPointF.get(3).x + deleteImgBtn.getWidth() / 2, mListPointF.get(3).y + deleteImgBtn.getHeight() / 2);
    }


    public boolean curPointIsInnerDrawRect(int xPos, int yPos) {
        // 判断手指是否在字幕框内
        RectF r = new RectF();
        Path path = new Path();
        path.moveTo(mListPointF.get(0).x, mListPointF.get(0).y);
        path.lineTo(mListPointF.get(1).x, mListPointF.get(1).y);
        path.lineTo(mListPointF.get(2).x, mListPointF.get(2).y);
        path.lineTo(mListPointF.get(3).x, mListPointF.get(3).y);
        path.close();
        path.computeBounds(r, true);
        Region region = new Region();
        region.setPath(path, new Region((int) r.left, (int) r.top, (int) r.right, (int) r.bottom));
        return region.contains(xPos, yPos);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float targetX = event.getX();
        float targetY = event.getY();
        tempTargetX += targetX;
        tempTargetY += targetY;
        if (mListPointF != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    tempTargetX = targetX;
                    tempTargetY = targetY;
                    mPrevMillionSecond = System.currentTimeMillis();
                    if (mListener != null) {
                        mListener.onTouchDown(new PointF(targetX, targetY));
                    }

                    if (mListPointF != null && mListPointF.size() == 4) {
                        // 判断手指是否在字幕框内
                        isInnerDrawRect = curPointIsInnerDrawRect((int) targetX, (int) targetY);
                    }
                    prePointF.set(targetX, targetY);
                    break;


                case MotionEvent.ACTION_UP:
                    long moveTime_up = System.currentTimeMillis() - mPrevMillionSecond;
                    if (mClickMoveDistance < HANDMOVE_DISTANCE && moveTime_up <= HANDCLICK_DURATION) {
                        if (isInnerDrawRect) {
                            if (mDrawRectClickListener != null)
                                mDrawRectClickListener.onDrawRectClick();

                        }
                        isInnerDrawRect = false;
                        mClickMoveDistance = 0.0D;
                        break;
                    }

                case MotionEvent.ACTION_MOVE:
                    mClickMoveDistance = Math.sqrt(Math.pow(targetX - prePointF.x, 2) + Math.pow(targetY - prePointF.y, 2));
//                    Logger.e(TAG,"mClickMoveDistanceDrawRect = " + mClickMoveDistance);
                    // 防止移出屏幕
                    if (targetX <= 100 || targetX >= getWidth() || targetY >= getHeight() || targetY <= 20) {
                        mMoveOutScreen = true;
                        break;
                    }
                    if (mMoveOutScreen) {
                        mMoveOutScreen = false;
                        break;
                    }

                    // 计算字幕框中心点
                    PointF centerPointF = new PointF();
                    if (mListPointF != null && mListPointF.size() == 4) {
                        centerPointF.x = (mListPointF.get(0).x + mListPointF.get(2).x) / 2;
                        centerPointF.y = (mListPointF.get(0).y + mListPointF.get(2).y) / 2;
                        lastCenterPointFX += centerPointF.x;
                        lastCenterPointFY += centerPointF.y;
                    }


                    if (mListener != null) {
                        isInnerDrawRect = false;
                        // 计算手指在屏幕上滑动的距离比例
                        double temp = Math.pow(prePointF.x - centerPointF.x, 2) + Math.pow(prePointF.y - centerPointF.y, 2);
                        double preLength = Math.sqrt(temp);
                        double temp2 = Math.pow(targetX - centerPointF.x, 2) + Math.pow(targetY - centerPointF.y, 2);
                        double length = Math.sqrt(temp2);
                        float offset = (float) (length / preLength);
                        lastOffset *= offset;
                        // 计算手指滑动的角度
                        float radian = (float) (Math.atan2(targetY - centerPointF.y, targetX - centerPointF.x)
                                - Math.atan2(prePointF.y - centerPointF.y, prePointF.x - centerPointF.x));
                        // 弧度转换为角度
                        float angle = (float) (radian * 180 / Math.PI);
                        mListener.onScaleAndRotate(offset, new PointF(centerPointF.x, centerPointF.y), -angle);
                    }

                    if (mListener != null && isInnerDrawRect) {
                        mListener.onDrag(prePointF, new PointF(targetX, targetY), new PointF(centerPointF.x, centerPointF.y));
                    }
                    prePointF.set(targetX, targetY);

                    break;
            }
        }

        return true;

    }

    public interface OnTouchListener {
        void onDrag(PointF prePointF, PointF nowPointF, PointF anchor);

        void onScaleAndRotate(float scaleFactor, PointF anchor, float rotation);

        void onTouchDown(PointF curPoint);

    }

    public interface onDrawRectClickListener {
        void onDrawRectClick();//矩形框点击,只是用于字幕修改文本
    }


}
