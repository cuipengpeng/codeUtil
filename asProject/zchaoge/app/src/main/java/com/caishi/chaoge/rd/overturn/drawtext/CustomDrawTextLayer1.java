package com.caishi.chaoge.rd.overturn.drawtext;

import android.graphics.PointF;
import android.graphics.RectF;

import com.caishi.chaoge.rd.overturn.TextLayer;

/**
 */
public class CustomDrawTextLayer1 extends CustomDrawTextLayer {


    public CustomDrawTextLayer1(TextLayer layer) {
        super(layer);
    }

    @Override
    void init1Show(float timelineFrom, float timelineTo) {
        {
            //左下角顶点 （水平状态下，矩形的左下角的顶点坐标）
            x = 69;
            y = 455;
            float scale = 1f;
            RectF rectF = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);
            TextLayer.IFrame iFrame = new TextLayer.IFrame(timelineFrom, timelineTo, new RectF(rectF), new RectF(rectF));
            mTextLayer.setFrame(iFrame);
        }
    }

    @Override
    void init2Anim(float timelineFrom, float timelineTo) {
        {  //旋转90
                x = 69;
                y = 455;
                float scale = randomNumList.get(showPosition);
                RectF rectF = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);
                RectF dst = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);
                TextLayer.IFrame iFrame = new TextLayer.IFrame(timelineFrom, timelineTo, new RectF(rectF), new RectF(dst));
                PointF rotatePointF = new PointF(rectF.left, rectF.bottom);
                iFrame.setAngle(0, -90, rotatePointF);
                mTextLayer.setFrame(iFrame);
            }

    }

    @Override
    void init2Show(float timelineFrom, float timelineTo) {
        {
            //竖着静止
            x = 69;
            y = 455;
            float scale = randomNumList.get(showPosition);
            RectF rectF = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);
            RectF dst = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);
            PointF rotatePointF = new PointF(rectF.left, rectF.bottom);
            TextLayer.IFrame iFrame = new TextLayer.IFrame(timelineFrom, timelineTo, new RectF(dst),
                    new RectF(dst)).setAngle(270, 0, new PointF(rotatePointF.x, rotatePointF.y));
            mTextLayer.setFrame(iFrame);
        }
    }

    @Override
    void init3Anim(float timelineFrom, float timelineTo) {
        {
            showVerticalShrinkOnceTextLayerAnim(timelineFrom, timelineTo,randomNumList.get(showPosition));
        }
    }

    @Override
    void init3Show(float timelineFrom, float timelineTo) {
        {
            showVerticalShrinkOnceTextLayerShow(timelineFrom, timelineTo,randomNumList.get(showPosition));
        }
    }

    @Override
    void init4Anim(float timelineFrom, float timelineTo) {
        {
            //竖着缩小2
            x = 69;
            y = 455;
            float scale = 1f;
            RectF src0 = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);


            scale = randomNumList.get(showPosition-1);
            x = x + textHeightList.get(showPosition - 1);
            y = 455;
            RectF dst2 = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);


            //缩小为0.42f
            x = x + textHeightList.get(showPosition);
            y = 455;
            scale = randomNumList.get(showPosition);
            RectF dst3 = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);

            TextLayer.IFrame iFrame = new TextLayer.IFrame(timelineFrom, timelineTo, new RectF(dst2),
                    new RectF(dst3)).setAngle(270, 0, new PointF(src0.left, src0.bottom));
            mTextLayer.setFrame(iFrame);
        }

    }

    @Override
    void init4Show(float timelineFrom, float timelineTo) {
        {
            //竖着静止2
            x = 69;
            y = 455;
            float scale = 1f;
            RectF src0 = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);


            //缩小为0.42f
            x = x + textHeightList.get(showPosition - 1) + textHeightList.get(showPosition);
            y = 455;
            scale = randomNumList.get(showPosition);

            RectF rectF = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);
            TextLayer.IFrame iFrame = new TextLayer.IFrame(timelineFrom, timelineTo, new RectF(rectF),
                    new RectF(rectF)).setAngle(270, 0, new PointF(src0.left, src0.bottom));
            mTextLayer.setFrame(iFrame);
        }

    }

    @Override
    void init5Anim(float timelineFrom, float timelineTo) {

        {
            float scale = 1f;
            x = 69;
            y = 455;
            RectF src0 = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);


            //缩小为0.42f
            x = x + textHeightList.get(showPosition - 1) + textHeightList.get(showPosition);
            y = 455;
            scale = randomNumList.get(showPosition);
            RectF rectF = new RectF(x / fW, (y - mTextLayer.getHeight() * scale) / fH, (x + mTextLayer.getWidth() * scale) / fW, y / fH);
            // 一直停留在此处，整体旋转
            TextLayer.IFrame iFrame = new TextLayer.IFrame(timelineFrom, timelineTo, new RectF(rectF),
                    new RectF(rectF)).setAngle(270, 0, new PointF(src0.left, src0.bottom));
            mTextLayer.setFrame(iFrame);


            //旋转
            initScreenLayer(timelineFrom, timelineTo);

        }
    }

    @Override
    void init5Show(float timelineFrom, float timelineTo) {
        mTextLayer.setFrame(null);
    }

    @Override
    void init6Anim(float timelineFrom, float timelineTo) {
        mTextLayer.setFrame(null);
    }

    @Override
    void init6Show(float timelineFrom, float timelineTo) {
        mTextLayer.setFrame(null);
    }

    @Override
    void init7Anim(float timelineFrom, float timelineTo) {
        mTextLayer.setFrame(null);
    }

    @Override
    void init7Show(float timelineFrom, float timelineTo) {
        mTextLayer.setFrame(null);
    }

    @Override
    void init8Anim(float timelineFrom, float timelineTo) {
        mTextLayer.setFrame(null);
    }

    @Override
    void init8Show(float timelineFrom, float timelineTo) {
        mTextLayer.setFrame(null);
    }

    @Override
    void init9Anim(float timelineFrom, float timelineTo) {
        mTextLayer.setFrame(null);
    }

    @Override
    void init9Show(float timelineFrom, float timelineTo) {
        mTextLayer.setFrame(null);
    }

    @Override
    void init10Anim(float timelineFrom, float timelineTo) {
        mTextLayer.setFrame(null);
    }

    @Override
    void init10Show(float timelineFrom, float timelineTo) {
        mTextLayer.setFrame(null);
    }

    @Override
    void initOther(float timelineFrom, float timelineTo) {
        //屏幕外的效果，就不用绘制
        mTextLayer.setFrame(null);

    }
}
