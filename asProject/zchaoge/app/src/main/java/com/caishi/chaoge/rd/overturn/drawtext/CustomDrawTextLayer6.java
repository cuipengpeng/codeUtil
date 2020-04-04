package com.caishi.chaoge.rd.overturn.drawtext;

  import com.caishi.chaoge.rd.overturn.TextLayer;

/**
 */
public class CustomDrawTextLayer6 extends CustomDrawTextLayer {
    public CustomDrawTextLayer6(TextLayer layer) {
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
    void init5Anim(float timelineFrom, float timelineTo) { mTextLayer.setFrame(null); }

    @Override
    void init5Show(float timelineFrom, float timelineTo) { mTextLayer.setFrame(null); }

    @Override
    void init6Anim(float timelineFrom, float timelineTo) {
       showTextLayerAnim(timelineFrom,timelineTo);
    }

    @Override
    void init6Show(float timelineFrom, float timelineTo) {
       showTextLayer(timelineFrom,timelineTo);
    }

    @Override
    void init7Anim(float timelineFrom, float timelineTo) {
        showTopTranslationOnceTextLayerAnim(timelineFrom,timelineTo,randomNumList.get(showPosition));
    }

    @Override
    void init7Show(float timelineFrom, float timelineTo) {
       showTopTranslationOnceTextLayerShow(timelineFrom,timelineTo,randomNumList.get(showPosition));
    }

    @Override
    void init8Anim(float timelineFrom, float timelineTo) {
        showTopTranslationOnceTextLayerShow(timelineFrom,timelineTo,showPosition-1,randomNumList.get(showPosition));
        //旋转
        initScreenLayerLeft(timelineFrom, timelineTo);
    }

    @Override
    void init8Show(float timelineFrom, float timelineTo) {
//        int l = 73;
//        int b = 454;
//
//        float scale = 0.9f;
//        RectF rect = new RectF(l, b - mTextLayer.getHeight() * scale, l + mTextLayer.getWidth() * scale, b);
//
//        b = 404;
//        scale = 0.7f;
//        RectF dst = new RectF(rect.left / fW, (b - mTextLayer.getHeight() * scale) / fH, (rect.left + mTextLayer.getWidth() * scale) / fW, b / fH);
//        TextLayer.IFrame iFrame = new TextLayer.IFrame(timelineFrom, timelineTo, new RectF(dst), new RectF(dst)).
//                setAngle(270, 0, new PointF(dst.left, dst.bottom));
//        mTextLayer.setFrame(iFrame);
    }

    @Override
    void init9Anim(float timelineFrom, float timelineTo) {

    }

    @Override
    void init9Show(float timelineFrom, float timelineTo) {

    }

    @Override
    void init10Anim(float timelineFrom, float timelineTo) {

    }

    @Override
    void init10Show(float timelineFrom, float timelineTo) {

    }

    @Override
    void initOther(float timelineFrom, float timelineTo) {


    }
}
