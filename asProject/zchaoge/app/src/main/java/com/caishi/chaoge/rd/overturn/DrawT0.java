package com.caishi.chaoge.rd.overturn;

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import com.caishi.chaoge.base.BaseApplication;
import com.caishi.chaoge.bean.AEModuleBean.AETextLayerInfo;
import com.caishi.chaoge.bean.LrcBean;
import com.caishi.chaoge.bean.MyLrcBean;
import com.caishi.chaoge.utils.DisplayMetricsUtil;
import com.caishi.chaoge.utils.LogUtil;
import com.rd.vecore.models.CanvasObject;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import java.util.List;

/**
 * 静夜思
 */
public class DrawT0 {
    protected static final float fW = DisplayMetricsUtil.getScreenWidth(BaseApplication.getContext());
    protected static final float fH = DisplayMetricsUtil.getScreenHeight(BaseApplication.getContext());
    public static float mouthX = fW/2, mouthY = fH/2;



    public static void drawText0(int index, CanvasObject canvas, float currentProgress, String fontFilePath, List<LrcBean> lrcBeanList) {
        float x = mouthX;
        float y = mouthY;
        int deltaX = lrcBeanList.get(index).getDeltaX();
        int deltaY = lrcBeanList.get(index).getDeltaY();
        int angle = lrcBeanList.get(index).getAngle();

        float scale = 1f;
        float deltaScale = 0.25f;

        RectF rectF ;
            RectF dst ;
            TextLayer.IFrame iFrame = null;
            for(int i =0;i<lrcBeanList.size();i++){
                        if(lrcBeanList.get(i).getStart()<=currentProgress && currentProgress<lrcBeanList.get(i).getEnd()){
                            LogUtil.printLog("i="+i+"--index="+index);

                            TextLayer mTextLayer;
                            if(i==index) {
                                mTextLayer = createTextLayer(index, lrcBeanList, fontFilePath);
                                scale = 0f;
                                //竖着缩小 1  //左下角顶点 （水平状态下，矩形的左下角的顶点坐标）
                                rectF = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                                scale = deltaScale;
                                //宽高缩小为0.68f
                                x = mouthX-deltaX;
                                y = mouthY+deltaY;
                                dst = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                                iFrame = new TextLayer.IFrame(lrcBeanList.get(i).getStart(), lrcBeanList.get(i).getEnd(), new RectF(rectF),
                                        new RectF(dst)).setAngle(0, angle, new PointF(0.5f, 0.5f));
                                mTextLayer.setFrame(iFrame);
                                mTextLayer.onDraw(canvas, currentProgress);
                            }else if(i==index+1) {
                                mTextLayer = createTextLayer(index, lrcBeanList, fontFilePath);
                                x = mouthX-deltaX;
                                y = mouthY+deltaY;
                                scale = deltaScale;
                                //竖着缩小 1  //左下角顶点 （水平状态下，矩形的左下角的顶点坐标）
                                rectF = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                                scale = deltaScale*2;
                                x = mouthX-deltaX*2;
                                y = mouthY+deltaY*2;
                                dst = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                                iFrame = new TextLayer.IFrame(lrcBeanList.get(i).getStart(), lrcBeanList.get(i).getEnd(), new RectF(rectF),
                                        new RectF(dst)).setAngle(angle, 0, new PointF(0.5f, 0.5f));
                                mTextLayer.setFrame(iFrame);
                                mTextLayer.onDraw(canvas, currentProgress);
                            }else if(i==index+2) {
                                mTextLayer = createTextLayer(index, lrcBeanList, fontFilePath);
                                x = mouthX-deltaX*2;
                                y = mouthY+deltaY*2;
                                scale = deltaScale*2;
                                rectF = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                                x = mouthX-deltaX*3;
                                y = mouthY+deltaY*3;
                                scale = deltaScale*3;
                                dst = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                                iFrame = new TextLayer.IFrame(lrcBeanList.get(i).getStart(), lrcBeanList.get(i).getEnd(), new RectF(rectF),
                                        new RectF(dst)).setAngle(angle, 0, new PointF(0.5f, 0.5f));
                                mTextLayer.setFrame(iFrame);
                                mTextLayer.onDraw(canvas, currentProgress);
                            }
                        }
            }
    }

    @NonNull
    private static TextLayer createTextLayer(int index, List<LrcBean> lrcBeanList, String fontFilePath) {
        TextLayer mTextLayer;
        if (lrcBeanList.get(index).getLrc().length()==1){
            mTextLayer = new TextLayer(DensityUtil.dp2px(400), DensityUtil.dp2px(100), new Rect(6, 6, 6, 6), lrcBeanList.get(index).getLrc(), Color.rgb(255, 255, 255), fontFilePath, AETextLayerInfo.Alignment.left);
        }else if (lrcBeanList.get(index).getLrc().length()==2){
            mTextLayer = new TextLayer(DensityUtil.dp2px(370), DensityUtil.dp2px(70), new Rect(6, 6, 6, 6), lrcBeanList.get(index).getLrc(), Color.rgb(255, 255, 255), fontFilePath, AETextLayerInfo.Alignment.left);
        }else {
            mTextLayer = new TextLayer(DensityUtil.dp2px(357), DensityUtil.dp2px(60), new Rect(6, 6, 6, 6), lrcBeanList.get(index).getLrc(), Color.rgb(255, 255, 255), fontFilePath, AETextLayerInfo.Alignment.left);
        }
        return mTextLayer;
    }

    public static void drawText1(int index, CanvasObject canvas, float currentProgress, String fontFilePath, List<LrcBean> lrcBeanList) {
        float x = mouthX+DensityUtil.dp2px(20);
        float y = mouthY;
        int deltaY = lrcBeanList.get(index).getDeltaY();

        float scale = 1f;
        float deltaScale = 0.25f;
        RectF rectF ;
        RectF dst ;
        TextLayer.IFrame iFrame = null;
        for(int i =0;i<lrcBeanList.size();i++){
            if(lrcBeanList.get(i).getStart()<currentProgress && currentProgress<lrcBeanList.get(i).getEnd()){
                TextLayer mTextLayer;
                if(i==index) {
                    mTextLayer = createTextLayer(index, lrcBeanList, fontFilePath);
                        scale = 0f;
                        rectF = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                        scale = deltaScale;
                        y = mouthY+deltaY;
                        dst = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                        iFrame = new TextLayer.IFrame(lrcBeanList.get(i).getStart(), lrcBeanList.get(i).getEnd(), new RectF(rectF),new RectF(dst));
                        mTextLayer.setFrame(iFrame);
                    mTextLayer.onDraw(canvas, currentProgress);
                }else if(i==index+1) {
                    mTextLayer = createTextLayer(index, lrcBeanList, fontFilePath);
                    y = mouthY+deltaY;
                    scale = deltaScale;
                    rectF = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    scale = deltaScale*2;
                    y = mouthY+deltaY*2;
                    dst = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    iFrame = new TextLayer.IFrame(lrcBeanList.get(i).getStart(), lrcBeanList.get(i).getEnd(), new RectF(rectF),new RectF(dst));
                    mTextLayer.setFrame(iFrame);
                    mTextLayer.onDraw(canvas, currentProgress);
                }else if(i==index+2) {
                    mTextLayer = createTextLayer(index, lrcBeanList, fontFilePath);
                    y = mouthY+deltaY*2;
                    scale = deltaScale*2;
                    rectF = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    y = mouthY+deltaY*3;
                    scale = deltaScale*3;
                    dst = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    iFrame = new TextLayer.IFrame(lrcBeanList.get(i).getStart(), lrcBeanList.get(i).getEnd(), new RectF(rectF),new RectF(dst));
                    mTextLayer.setFrame(iFrame);
                    mTextLayer.onDraw(canvas, currentProgress);
                }
            }
        }
    }
    public static void drawText2(int index, CanvasObject canvas, float currentProgress, String fontFilePath, List<LrcBean> lrcBeanList) {
        float x=mouthX;
        float y=mouthY;

        int deltaX = lrcBeanList.get(index).getDeltaX();
        int deltaY = lrcBeanList.get(index).getDeltaY();
        int angle = lrcBeanList.get(index).getAngle();

        float scale = 1f;
        float deltaScale = 0.25f;
        RectF rectF ;
        RectF dst ;
        TextLayer.IFrame iFrame = null;
        for(int i =0;i<lrcBeanList.size();i++){
            if(lrcBeanList.get(i).getStart()<currentProgress && currentProgress<lrcBeanList.get(i).getEnd()){
                TextLayer mTextLayer;
                if(i==index){
                    mTextLayer = createTextLayer(index, lrcBeanList, fontFilePath);
                        scale = 0f;
                        rectF = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                        scale = deltaScale;
                        x = mouthX+deltaX;
                        y = mouthY+deltaY;
                        dst = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                        iFrame = new TextLayer.IFrame(lrcBeanList.get(i).getStart(), lrcBeanList.get(i).getEnd(), new RectF(rectF),
                                            new RectF(dst)).setAngle(0, -angle, new PointF(0.5f, 0.5f));
                        mTextLayer.setFrame(iFrame);
                    mTextLayer.onDraw(canvas, currentProgress);
                }else if(i==index+1) {
                    mTextLayer = createTextLayer(index, lrcBeanList, fontFilePath);
                    scale = deltaScale;
                    x = mouthX+deltaX;
                    y = mouthY+deltaY;
                    rectF = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    scale = deltaScale*2;
                    x = mouthX+deltaX*2;
                    y = mouthY+deltaY*2;
                    dst = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    iFrame = new TextLayer.IFrame(lrcBeanList.get(i).getStart(), lrcBeanList.get(i).getEnd(), new RectF(rectF),
                            new RectF(dst)).setAngle(-angle, 0, new PointF(0.5f, 0.5f));
                    mTextLayer.setFrame(iFrame);
                    mTextLayer.onDraw(canvas, currentProgress);
                }else if(i==index+2) {
                    mTextLayer = createTextLayer(index, lrcBeanList, fontFilePath);
                    x = mouthX+deltaX*2;
                    y = mouthY+deltaY*2;
                    scale = deltaScale*2;
                    rectF = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    x = mouthX+deltaX*3;
                    y = mouthY+deltaY*3;
                    scale = deltaScale*3;
                    dst = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    iFrame = new TextLayer.IFrame(lrcBeanList.get(i).getStart(), lrcBeanList.get(i).getEnd(), new RectF(rectF),
                            new RectF(dst)).setAngle(-angle, 0, new PointF(0.5f, 0.5f));
                    mTextLayer.setFrame(iFrame);
                    mTextLayer.onDraw(canvas, currentProgress);
                }
            }
        }
    }

    public static void drawImg0(int index, CanvasObject canvas, float currentProgress, TextLayer mTextLayer, List<LrcBean> lrcBeanList) {
        float x = mouthX;
        float y = mouthY;

        int deltaX = lrcBeanList.get(index).getDeltaX()-DensityUtil.dp2px(45);
        int deltaY = lrcBeanList.get(index).getDeltaY()-DensityUtil.dp2px(15);
        int angle = lrcBeanList.get(index).getAngle();


        float scale = 1f;
        float deltaScale = 0.2f;
            RectF rectF ;
            RectF dst ;
            TextLayer.IFrame iFrame = null;
            for(int i =0;i<lrcBeanList.size();i++){
                        if(lrcBeanList.get(i).getStart()<=currentProgress && currentProgress<lrcBeanList.get(i).getEnd()){
                            if(i==index) {
                                scale = 0f;
                                rectF = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                                scale = deltaScale;
                                x = mouthX-deltaX;
                                y = mouthY+deltaY;
                                dst = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                                iFrame = new TextLayer.IFrame(lrcBeanList.get(i).getStart(), lrcBeanList.get(i).getEnd(), new RectF(rectF),
                                            new RectF(dst)).setAngle(0, angle, new PointF(0.5f, 0.5f));
                                mTextLayer.setFrame(iFrame);
                            }else if(i==index+1) {
                                x = mouthX-deltaX;
                                y = mouthY+deltaY;
                                scale = deltaScale;
                                //竖着缩小 1  //左下角顶点 （水平状态下，矩形的左下角的顶点坐标）
                                rectF = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                                scale = deltaScale*2;
                                x = mouthX-deltaX*2;
                                y = mouthY+deltaY*2;
                                dst = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                                iFrame = new TextLayer.IFrame(lrcBeanList.get(i).getStart(), lrcBeanList.get(i).getEnd(), new RectF(rectF),
                                            new RectF(dst)).setAngle(angle, 0, new PointF(0.5f, 0.5f));
                                mTextLayer.setFrame(iFrame);
                            }else if(i==index+2) {
                                x = mouthX-deltaX*2;
                                y = mouthY+deltaY*2;
                                scale = deltaScale*2;
                                //竖着缩小 1  //左下角顶点 （水平状态下，矩形的左下角的顶点坐标）
                                rectF = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                                x = mouthX-deltaX*3;
                                y = mouthY+deltaY*3;
                                scale = deltaScale*3;
                                //宽高缩小为0.68f
                                dst = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                                iFrame = new TextLayer.IFrame(lrcBeanList.get(i).getStart(), lrcBeanList.get(i).getEnd(), new RectF(rectF),
                                            new RectF(dst)).setAngle(angle, 0, new PointF(0.5f, 0.5f));
                                mTextLayer.setFrame(iFrame);
                            }else if(i==index+3) {
                                x = mouthX-deltaX*3;
                                y = mouthY+deltaY*3;
                                scale = deltaScale*3;
                                //竖着缩小 1  //左下角顶点 （水平状态下，矩形的左下角的顶点坐标）
                                rectF = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                                x = mouthX-deltaX*4;
                                y = mouthY+deltaY*4;
                                scale = deltaScale*4;
                                //宽高缩小为0.68f
                                dst = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                                iFrame = new TextLayer.IFrame(lrcBeanList.get(i).getStart(), lrcBeanList.get(i).getEnd(), new RectF(rectF),
                                            new RectF(dst)).setAngle(angle, 0, new PointF(0.5f, 0.5f));
                                mTextLayer.setFrame(iFrame);
                            }
                            mTextLayer.onDraw(canvas, currentProgress);
                        }
            }
    }
    public static void drawImg1(int index, CanvasObject canvas, float currentProgress, TextLayer mTextLayer, List<LrcBean> lrcBeanList) {
        float x = mouthX+lrcBeanList.get(index).getDeltaX()-DensityUtil.dp2px(55);
        float y = mouthY;
        int deltaY = lrcBeanList.get(index).getDeltaY()-DensityUtil.dp2px(10);

        float scale = 1f;
        float deltaScale = 0.2f;
        RectF rectF ;
        RectF dst ;
        TextLayer.IFrame iFrame = null;
        for(int i =0;i<lrcBeanList.size();i++){
            if(lrcBeanList.get(i).getStart()<currentProgress && currentProgress<lrcBeanList.get(i).getEnd()){
                if(i==index) {
                    scale = 0f;
                    rectF = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    scale = deltaScale;
                    y = mouthY+deltaY;
                    dst = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    iFrame = new TextLayer.IFrame(lrcBeanList.get(i).getStart(), lrcBeanList.get(i).getEnd(), new RectF(rectF),new RectF(dst));
                    mTextLayer.setFrame(iFrame);
                }else if(i==index+1) {
                    y = mouthY+deltaY;
                    scale = deltaScale;
                    rectF = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    scale = deltaScale*2;
                    y = mouthY+deltaY*2;
                    dst = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    iFrame = new TextLayer.IFrame(lrcBeanList.get(i).getStart(), lrcBeanList.get(i).getEnd(), new RectF(rectF),new RectF(dst));
                    mTextLayer.setFrame(iFrame);
                }else if(i==index+2) {
                    y = mouthY+deltaY*2;
                    scale = deltaScale*2;
                    rectF = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    y = mouthY+deltaY*3;
                    scale = deltaScale*3;
                    dst = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    iFrame = new TextLayer.IFrame(lrcBeanList.get(i).getStart(), lrcBeanList.get(i).getEnd(), new RectF(rectF),new RectF(dst));
                    mTextLayer.setFrame(iFrame);
                }else if(i==index+3) {
                    y = mouthY+deltaY*3;
                    scale = deltaScale*3;
                    rectF = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    y = mouthY+deltaY*4;
                    scale = deltaScale*4;
                    dst = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    iFrame = new TextLayer.IFrame(lrcBeanList.get(i).getStart(), lrcBeanList.get(i).getEnd(), new RectF(rectF),new RectF(dst));
                    mTextLayer.setFrame(iFrame);
                }

                mTextLayer.onDraw(canvas, currentProgress);
            }
        }
    }
    public static void drawImg2(int index, CanvasObject canvas, float currentProgress, TextLayer mTextLayer, List<LrcBean> lrcBeanList) {
        float x=mouthX;
        float y=mouthY;

        int deltaX = lrcBeanList.get(index).getDeltaX()-DensityUtil.dp2px(35);
        int deltaY = lrcBeanList.get(index).getDeltaY()+DensityUtil.dp2px(10);
        int angle = lrcBeanList.get(index).getAngle();

        float scale = 1f;
        float deltaScale = 0.2f;

        RectF rectF ;
        RectF dst ;
        TextLayer.IFrame iFrame = null;
        for(int i =0;i<lrcBeanList.size();i++){
            if(lrcBeanList.get(i).getStart()<currentProgress && currentProgress<lrcBeanList.get(i).getEnd()){
                if(i==index){
                    scale = 0f;
                    rectF = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    scale = deltaScale;
                    x = mouthX+deltaX;
                    y = mouthY+deltaY;
                    dst = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    iFrame = new TextLayer.IFrame(lrcBeanList.get(i).getStart(), lrcBeanList.get(i).getEnd(), new RectF(rectF),
                                            new RectF(dst)).setAngle(0, -angle, new PointF(0.5f, 0.5f));
                    mTextLayer.setFrame(iFrame);
                }else if(i==index+1) {
                    scale = deltaScale;
                    x = mouthX + deltaX;
                    y = mouthY + deltaY;
                    rectF = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    scale = deltaScale*2;
                    x = mouthX + deltaX*2;
                    y = mouthY + deltaY*2;
                    dst = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    iFrame = new TextLayer.IFrame(lrcBeanList.get(i).getStart(), lrcBeanList.get(i).getEnd(), new RectF(rectF),
                            new RectF(dst)).setAngle(-angle, 0, new PointF(0.5f, 0.5f));
                    mTextLayer.setFrame(iFrame);
                }else if(i==index+2) {
                    x = mouthX + deltaX*2;
                    y = mouthY + deltaY*2;
                    scale = deltaScale*2;
                    rectF = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    x = mouthX + deltaX*3;
                    y = mouthY + deltaY*3;
                    scale = deltaScale*3;
                    dst = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    iFrame = new TextLayer.IFrame(lrcBeanList.get(i).getStart(), lrcBeanList.get(i).getEnd(), new RectF(rectF),
                            new RectF(dst)).setAngle(-angle, 0, new PointF(0.5f, 0.5f));
                    mTextLayer.setFrame(iFrame);
                }
                mTextLayer.onDraw(canvas, currentProgress);
            }
        }
    }
    public static void drawImg3(int index, CanvasObject canvas, float currentProgress, TextLayer mTextLayer, List<LrcBean> lrcBeanList) {
        float x = mouthX;
        float y = mouthY;
        int deltaX = lrcBeanList.get(index).getDeltaX()+DensityUtil.dp2px(30);
        int deltaY = lrcBeanList.get(index).getDeltaY()-DensityUtil.dp2px(10);
        int angle = lrcBeanList.get(index).getAngle();

        float scale = 1f;
        float deltaScale = 0.2f;

        RectF rectF ;
        RectF dst ;
        TextLayer.IFrame iFrame = null;
        for(int i =0;i<lrcBeanList.size();i++){
            if(lrcBeanList.get(i).getStart()<=currentProgress && currentProgress<lrcBeanList.get(i).getEnd()){
                if(i==index) {
                    scale = 0f;
                    //竖着缩小 1  //左下角顶点 （水平状态下，矩形的左下角的顶点坐标）
                    rectF = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    scale = deltaScale;
                    //宽高缩小为0.68f
                    x = mouthX-deltaX;
                    y = mouthY+deltaY;
                    dst = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    iFrame = new TextLayer.IFrame(lrcBeanList.get(i).getStart(), lrcBeanList.get(i).getEnd(), new RectF(rectF),
                            new RectF(dst)).setAngle(0, angle, new PointF(0.5f, 0.5f));
                    mTextLayer.setFrame(iFrame);
                }else if(i==index+1) {
                    x = mouthX-deltaX;
                    y = mouthY+deltaY;
                    scale = deltaScale;
                    //竖着缩小 1  //左下角顶点 （水平状态下，矩形的左下角的顶点坐标）
                    rectF = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    scale = deltaScale*2;
                    x = mouthX-deltaX*2;
                    y = mouthY+deltaY*2;
                    dst = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    iFrame = new TextLayer.IFrame(lrcBeanList.get(i).getStart(), lrcBeanList.get(i).getEnd(), new RectF(rectF),
                            new RectF(dst)).setAngle(angle, 0, new PointF(0.5f, 0.5f));
                    mTextLayer.setFrame(iFrame);
                }else if(i==index+2) {
                    x = mouthX-deltaX*2;
                    y = mouthY+deltaY*2;
                    scale = deltaScale*2;
                    rectF = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    x = mouthX-deltaX*3;
                    y = mouthY+deltaY*3;
                    scale = deltaScale*3;
                    dst = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    iFrame = new TextLayer.IFrame(lrcBeanList.get(i).getStart(), lrcBeanList.get(i).getEnd(), new RectF(rectF),
                            new RectF(dst)).setAngle(angle, 0, new PointF(0.5f, 0.5f));
                    mTextLayer.setFrame(iFrame);
                }else if(i==index+3) {
                    x = mouthX-deltaX*3;
                    y = mouthY+deltaY*3;
                    scale = deltaScale*3;
                    rectF = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    x = mouthX-deltaX*4;
                    y = mouthY+deltaY*4;
                    scale = deltaScale*4;
                    dst = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    iFrame = new TextLayer.IFrame(lrcBeanList.get(i).getStart(), lrcBeanList.get(i).getEnd(), new RectF(rectF),
                            new RectF(dst)).setAngle(angle, 0, new PointF(0.5f, 0.5f));
                    mTextLayer.setFrame(iFrame);
                }
                mTextLayer.onDraw(canvas, currentProgress);
            }
        }
    }
    public static void drawImg4(int index, CanvasObject canvas, float currentProgress, TextLayer mTextLayer, List<LrcBean> lrcBeanList) {
        float x=mouthX;
        float y=mouthY;
        int deltaX = lrcBeanList.get(index).getDeltaX()+DensityUtil.dp2px(20);
        int deltaY = lrcBeanList.get(index).getDeltaY()-DensityUtil.dp2px(10);
        int angle = lrcBeanList.get(index).getAngle();

        float scale = 1f;
        float deltaScale = 0.2f;

        RectF rectF ;
        RectF dst ;
        TextLayer.IFrame iFrame = null;
        for(int i =0;i<lrcBeanList.size();i++){
            if(lrcBeanList.get(i).getStart()<currentProgress && currentProgress<lrcBeanList.get(i).getEnd()){
                if(i==index){
                    scale = 0f;
                    rectF = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    scale = deltaScale;
                    x = mouthX+deltaX;
                    y = mouthY+deltaY;
                    dst = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    iFrame = new TextLayer.IFrame(lrcBeanList.get(i).getStart(), lrcBeanList.get(i).getEnd(), new RectF(rectF),
                                            new RectF(dst)).setAngle(0, -angle, new PointF(0.5f, 0.5f));
                    mTextLayer.setFrame(iFrame);
                }else if(i==index+1) {
                    scale = deltaScale;
                    x = mouthX + deltaX;
                    y = mouthY + deltaY;
                    rectF = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    scale = deltaScale*2;
                    x = mouthX + deltaX*2;
                    y = mouthY + deltaY*2;
                    dst = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    iFrame = new TextLayer.IFrame(lrcBeanList.get(i).getStart(), lrcBeanList.get(i).getEnd(), new RectF(rectF),
                            new RectF(dst)).setAngle(-angle, 0, new PointF(0.5f, 0.5f));
                    mTextLayer.setFrame(iFrame);
                }else if(i==index+2) {
                    x = mouthX + deltaX*2;
                    y = mouthY + deltaY*2;
                    scale = deltaScale*2;
                    rectF = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    x = mouthX + deltaX*3;
                    y = mouthY + deltaY*3;
                    scale = deltaScale*3;
                    dst = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    iFrame = new TextLayer.IFrame(lrcBeanList.get(i).getStart(), lrcBeanList.get(i).getEnd(), new RectF(rectF),
                            new RectF(dst)).setAngle(-angle, 0, new PointF(0.5f, 0.5f));
                    mTextLayer.setFrame(iFrame);
                }else if(i==index+3) {
                    x = mouthX + deltaX*3;
                    y = mouthY + deltaY*3;
                    scale = deltaScale*3;
                    rectF = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    x = mouthX + deltaX*4;
                    y = mouthY + deltaY*4;
                    scale = deltaScale*4;
                    dst = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

                    iFrame = new TextLayer.IFrame(lrcBeanList.get(i).getStart(), lrcBeanList.get(i).getEnd(), new RectF(rectF),
                            new RectF(dst)).setAngle(-angle, 0, new PointF(0.5f, 0.5f));
                    mTextLayer.setFrame(iFrame);
                }
                mTextLayer.onDraw(canvas, currentProgress);
            }
        }
    }

}
