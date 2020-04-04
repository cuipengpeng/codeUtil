package com.caishi.chaoge.ui.widget;

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

public class CaptionDrawRect extends View {

    private final String TAG = "CaptionDrawRect";
    private Bitmap rotationImgBtn = null;
    private OnTouchListener mListener;
    private PointF prePointF = new PointF(0, 0);
    private RectF rotationRectF = new RectF();
    private RectF delRectF = new RectF();
    private RectF alignRectF = new RectF();
    private List<PointF> mListPointF;
    private boolean canScalOrRotate = false;
    private boolean canMove = false;
    private boolean isOutScreen = false;
    private boolean isMoveIng = false;
    private OnUIClickListener mClickListener;
    private onCheckedListener mOnCheckedListener;
    private int textAlign = 1;

    public CaptionDrawRect(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 控制器的显示位置
     *
     * @param list
     */
    public void SetDrawRect(List<PointF> list) {
        mListPointF = list;
//        Log.e(TAG, "SetDrawRect: " + mListPointF);
        invalidate();
    }

    /**
     * 控制器的坐标
     *
     * @return
     */
    public List<PointF> getList() {
        return mListPointF;
    }


    public void SetOnTouchListener(OnTouchListener listener) {
        mListener = listener;
    }

    public void SetOnAlignClickListener(OnUIClickListener alignClickListener) {
        mClickListener = alignClickListener;
    }


    public void setOnCheckedListener(onCheckedListener listener) {
        mOnCheckedListener = listener;
    }

    private Paint m_paint = new Paint();


    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (null != mListPointF) {
            // 设置颜色
            m_paint.setColor(Color.WHITE);
            // 设置抗锯齿
            m_paint.setAntiAlias(true);
            // 设置线宽
            m_paint.setStrokeWidth(3);
            // 设置非填充
            m_paint.setStyle(Paint.Style.STROKE);

            canvas.drawLine(mListPointF.get(0).x, mListPointF.get(0).y, mListPointF.get(1).x, mListPointF.get(1).y, m_paint);
            canvas.drawLine(mListPointF.get(1).x, mListPointF.get(1).y, mListPointF.get(2).x, mListPointF.get(2).y, m_paint);
            canvas.drawLine(mListPointF.get(2).x, mListPointF.get(2).y, mListPointF.get(3).x, mListPointF.get(3).y, m_paint);
            canvas.drawLine(mListPointF.get(3).x, mListPointF.get(3).y, mListPointF.get(0).x, mListPointF.get(0).y, m_paint);
            if (null != rotationImgBtn) {
                //控制按钮的位置
                PointF control = mListPointF.get(2);
                canvas.drawBitmap(rotationImgBtn, control.x - rotationImgBtn.getHeight() / 2, control.y - rotationImgBtn.getWidth() / 2, m_paint);
                rotationRectF.set(control.x - rotationImgBtn.getWidth() / 2, control.y - rotationImgBtn.getHeight() / 2, control.x - rotationImgBtn.getWidth() / 2 + rotationImgBtn.getWidth(), control.y - rotationImgBtn.getWidth() / 2 + rotationImgBtn.getHeight());
            }
        }
    }

    private PointF centerPointF = new PointF();
    private boolean isDeleteClick = false;
    private boolean isAlignClick = false;

    private float downTargetX;
    private float downTargetY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.e(TAG, "onTouchEvent: " + event.getAction() + ">>" + mListPointF);
        float targetX = event.getX();
        float targetY = event.getY();
        if (mListPointF != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    downTargetX = event.getX();
                    downTargetY = event.getY();
                    isMoveIng = true;
                    isDeleteClick = delRectF.contains(targetX, targetY);
                    isAlignClick = alignRectF.contains(targetX, targetY);
                    if (!isDeleteClick && !isAlignClick) {

                        canScalOrRotate = rotationRectF.contains(targetX, targetY);

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
                        canMove = region.contains((int) targetX, (int) targetY);

                        prePointF.set(targetX, targetY);
                    }
                }
                break;


                case MotionEvent.ACTION_MOVE: {
                    isMoveIng = true;
                    if (!isDeleteClick) {
                        // 防止移出屏幕
//                            if (targetX <= 100 || targetX >= getWidth() - 100 || targetY >= getHeight() - 10 || targetY <= 10) {
//                                isOutScreen = true;
//                                break;
//                            }
//
//                            if (isOutScreen) {
//                                isOutScreen = false;
//                                break;
//                            }
                        centerPointF.set((mListPointF.get(0).x + mListPointF.get(2).x) / 2
                                , (mListPointF.get(0).y + mListPointF.get(2).y) / 2);
                        if (canMove) {
                            if (null != mListener) {
                                mListener.onDrag(prePointF, new PointF(targetX, targetY));
                            }
                        } else if (canScalOrRotate) {

                            float offsetScale = 1;
                            float angle = 0;

                            // 计算手指在屏幕上滑动的距离比例
                            double temp = Math.pow(prePointF.x - centerPointF.x, 2) + Math.pow(prePointF.y - centerPointF.y, 2);
                            //上一次的点到中心点的距离
                            double preLength = Math.sqrt(temp);

                            double temp2 = Math.pow(targetX - centerPointF.x, 2) + Math.pow(targetY - centerPointF.y, 2);
                            //新的点到中心点的距离
                            double length = Math.sqrt(temp2);

                            //每次缩放变化的比
                            offsetScale = (float) ((length - preLength) / preLength);


                            // 计算手指滑动的角度
                            float radian = (float) (Math.atan2(targetY - centerPointF.y, targetX - centerPointF.x)
                                    - Math.atan2(prePointF.y - centerPointF.y, prePointF.x - centerPointF.x));
                            // 弧度转换为角度
                            angle = (float) (radian * 180 / Math.PI);
                            if (null != mListener) {
                                mListener.onScaleAndRotate(offsetScale, -angle);
                            }
                        }
                        prePointF.set(targetX, targetY);
                    }
                }
                break;

                case MotionEvent.ACTION_UP: {
                    isMoveIng = false;
                    canScalOrRotate = false;
                    canMove = false;
                    if (isDeleteClick) {
                        if (null != mClickListener) {
                            mClickListener.onDeleteClick();
                        }
                        isDeleteClick = false;
                    }
                    if (isAlignClick) {
                        if (null != mClickListener) {
                            textAlign++;
                            if (textAlign > 2) {
                                textAlign = 0;
                            }
                            invalidate();
                            mClickListener.onAlignClick(textAlign);
                        }
                        isAlignClick = false;
                    }
                    if (null != mOnCheckedListener) {
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
                        if ((Math.abs(event.getX() - downTargetX) < 50 ||
                                Math.abs(event.getY() - downTargetY) < 50) && !region.contains((int) targetX, (int) targetY))
                            mOnCheckedListener.onCheckItem(targetX, targetY);
                    }


                }
                break;
            }
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }


    /**
     * 释放,退出字幕界面时
     */
    public void recycle() {
        this.setVisibility(View.GONE);
        if (null != rotationImgBtn) {
            rotationImgBtn.recycle();
            rotationImgBtn = null;
        }
    }

    /**
     * 进入显示之前，初始化bmp
     */
    public void initbmp() {
        rotationImgBtn = BitmapFactory.decodeResource(getResources(), R.drawable.ic_size_right);
        this.setVisibility(View.VISIBLE);
    }

    public interface OnUIClickListener {
        /**
         * 响应删除按钮
         */
        void onDeleteClick();

        void onAlignClick(int align);
    }

    public interface onCheckedListener {
        /**
         * 点击控制器的任意一个区域
         *
         * @param x
         * @param y
         */
        void onCheckItem(float x, float y);
    }


    public interface OnTouchListener {
        void onDrag(PointF prePointF, PointF nowPointF);

        /**
         * @param offsetScale     缩放参数：相比上一次的比例变化
         * @param offfsetRotation 旋转角度，每次的偏移量
         */
        void onScaleAndRotate(float offsetScale, float offfsetRotation);


    }

}
