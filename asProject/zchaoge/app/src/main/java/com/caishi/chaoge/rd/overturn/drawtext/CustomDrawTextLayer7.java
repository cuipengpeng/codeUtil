package com.caishi.chaoge.rd.overturn.drawtext;

  import com.caishi.chaoge.rd.overturn.TextLayer;

/**
 */
public class CustomDrawTextLayer7 extends CustomDrawTextLayer {
    public CustomDrawTextLayer7(TextLayer layer) {
        super(layer);
    }

    @Override
    void init1Show(float timelineFrom, float timelineTo) {
        mTextLayer.setFrame(null);
    }

    @Override
    void init2Anim(float timelineFrom, float timelineTo) {
        mTextLayer.setFrame(null);
    }

    @Override
    void init2Show(float timelineFrom, float timelineTo) {
        mTextLayer.setFrame(null);
    }

    @Override
    void init3Anim(float timelineFrom, float timelineTo) {
        mTextLayer.setFrame(null);
    }

    @Override
    void init3Show(float timelineFrom, float timelineTo) {
        mTextLayer.setFrame(null);
    }


    @Override
    void init4Anim(float timelineFrom, float timelineTo) {
        mTextLayer.setFrame(null);
    }

    @Override
    void init4Show(float timelineFrom, float timelineTo) {
        mTextLayer.setFrame(null);
    }

    @Override
    void init5Anim(float timelineFrom, float timelineTo) {
        mTextLayer.setFrame(null);
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
        showTextLayerAnim(timelineFrom, timelineTo);
    }

    @Override
    void init7Show(float timelineFrom, float timelineTo) {
        showTextLayer(timelineFrom, timelineTo);

    }

    @Override
    void init8Anim(float timelineFrom, float timelineTo) {

        showTextLayer(timelineFrom, timelineTo,randomNumList.get(showPosition));
        //旋转
        initScreenLayerLeft(timelineFrom, timelineTo);
    }

    @Override
    void init8Show(float timelineFrom, float timelineTo) {
        //翻转之后显示
       showLeftOverturnTextLayerShow(timelineFrom,timelineTo,randomNumList.get(showPosition));

    }

    @Override
    void init9Anim(float timelineFrom, float timelineTo) {
        showVerticalShrinkOnceTextLayerAnim(timelineFrom, timelineTo,randomNumList.get(showPosition));
    }

    @Override
    void init9Show(float timelineFrom, float timelineTo) {
        showVerticalShrinkOnceTextLayerShow(timelineFrom, timelineTo,randomNumList.get(showPosition));
    }

    @Override
    void init10Anim(float timelineFrom, float timelineTo) {
//        //竖着缩小2
//        int l = 70;
//        int b = 462;
//        RectF src0 = new RectF(l / fW, (b - mTextLayer.getHeight() * 1) / fH, (l + mTextLayer.getWidth() * 1) / fW, b / fH);
//
//
//        float scale = 0.58f;
//        l = 70 + (462 - 374);
//        b = 462;
//        RectF dst2 = new RectF(l / fW, (b - mTextLayer.getHeight() * scale) / fH, (l + mTextLayer.getWidth() * scale) / fW, b / fH);
//
//
//        //缩小为0.42f
//        l = 70 + (462 - 374) + (374 - 318);
//        b = 462;
//        scale = 0.32f;
//        RectF dst3 = new RectF(l / fW, (b - mTextLayer.getHeight() * scale) / fH, (l + mTextLayer.getWidth() * scale) / fW, b / fH);
//
//        TextLayer.IFrame iFrame = new TextLayer.IFrame(timelineFrom, timelineTo, new RectF(dst2),
//                new RectF(dst3)).setAngle(270, 0, new PointF(src0.left, src0.bottom));
//        mTextLayer.setFrame(iFrame);
    }

    @Override
    void init10Show(float timelineFrom, float timelineTo) {
//        //竖着静止2
//        int l = 70;
//        int b = 462;
//        float scale = 1f;
//        RectF src0 = new RectF(l / fW, (b - mTextLayer.getHeight() * scale) / fH, (l + mTextLayer.getWidth() * scale) / fW, b / fH);
//
//
//        //缩小为0.42f
//        l = 70 + (462 - 374) + (374 - 318);
//        b = 462;
//        scale = 0.32f;
//
//        RectF rectF = new RectF(l / fW, (b - mTextLayer.getHeight() * scale) / fH, (l + mTextLayer.getWidth() * scale) / fW, b / fH);
//        TextLayer.IFrame iFrame = new TextLayer.IFrame(timelineFrom, timelineTo, new RectF(rectF),
//                new RectF(rectF)).setAngle(270, 0, new PointF(src0.left, src0.bottom));
//        mTextLayer.setFrame(iFrame);
    }

    @Override
    void initOther(float timelineFrom, float timelineTo) {


    }
}
