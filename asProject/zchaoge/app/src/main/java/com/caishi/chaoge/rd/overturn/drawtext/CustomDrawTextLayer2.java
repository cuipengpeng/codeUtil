package com.caishi.chaoge.rd.overturn.drawtext;

import android.graphics.PointF;
import android.graphics.RectF;

  import com.caishi.chaoge.rd.overturn.TextLayer;

/**
 */
public class CustomDrawTextLayer2 extends CustomDrawTextLayer {
    public CustomDrawTextLayer2(TextLayer layer) {
        super(layer);
    }


    @Override
    void init1Show(float timelineFrom, float timelineTo) {
        mTextLayer.setFrame(null);
    }

    @Override
    void init2Anim(float timelineFrom, float timelineTo) {
        int l = 73;
        int b = 454;
        float scale = 1f;
        RectF rect = new RectF(l, b - mTextLayer.getHeight() * scale, l + mTextLayer.getWidth() * scale, b);
        RectF rectF = new RectF(rect.left / fW, rect.top / fH, rect.right / fW, rect.bottom / fH);


        TextLayer.IFrame iFrame = new TextLayer.IFrame(timelineFrom, timelineTo, rectF, new RectF(rectF))
                .setAngle(90, -90, new PointF(rectF.left, rectF.bottom)).setScale(0, 0, 1, 1);
        mTextLayer.setFrame(iFrame);
    }

    @Override
    void init2Show(float timelineFrom, float timelineTo) {
        int l = 73;
        int b = 454;

        float scale = 1f;
        RectF rect = new RectF(l, b - mTextLayer.getHeight() * scale, l + mTextLayer.getWidth() * scale, b);
        RectF rectF = new RectF(rect.left / fW, rect.top / fH, rect.right / fW, rect.bottom / fH);


        //不变
        TextLayer.IFrame iFrame = new TextLayer.IFrame(timelineFrom, timelineTo, rectF, new RectF(rectF))
                .setAngle(0, 0, new PointF(rectF.left, rectF.bottom));
        mTextLayer.setFrame(iFrame);
    }

    @Override
    void init3Anim(float timelineFrom, float timelineTo) {

      showTopTranslationOnceTextLayerAnim(timelineFrom,timelineTo,randomNumList.get(showPosition));
    }

    @Override
    void init3Show(float timelineFrom, float timelineTo) {
       showTopTranslationOnceTextLayerShow(timelineFrom,timelineTo,randomNumList.get(showPosition));
    }

    @Override
    void init4Anim(float timelineFrom, float timelineTo) {

      showTopTranslationTwiceTextLayerAnim(timelineFrom,timelineTo,randomNumList.get(showPosition));
    }

    @Override
    void init4Show(float timelineFrom, float timelineTo) {
       showTopTranslationTwiceTextLayerShow(timelineFrom,timelineTo,randomNumList.get(showPosition));
    }

    @Override
    void init5Anim(float timelineFrom, float timelineTo) {
        showTopTranslationOnceTextLayerShow(timelineFrom,timelineTo,showPosition-1,randomNumList.get(showPosition));


        initScreenLayer(timelineFrom, timelineTo);
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

    }
}
