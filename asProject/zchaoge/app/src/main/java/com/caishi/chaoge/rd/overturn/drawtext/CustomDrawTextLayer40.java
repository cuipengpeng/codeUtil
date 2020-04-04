package com.caishi.chaoge.rd.overturn.drawtext;

  import com.caishi.chaoge.rd.overturn.TextLayer;

/**
 */
public class CustomDrawTextLayer40 extends CustomDrawTextLayer {
    public CustomDrawTextLayer40(TextLayer layer) {
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
    public void init40Anim(float timelineFrom, float timelineTo) {
        super.init40Anim(timelineFrom, timelineTo);
        showRightOverturnTextLayerAnim(timelineFrom, timelineTo);
    }

    @Override
    public void init40Show(float timelineFrom, float timelineTo) {
        super.init40Show(timelineFrom, timelineTo);
        showRightOverturnTextLayerShow(timelineFrom, timelineTo);
    }

    @Override
    public void init41Anim(float timelineFrom, float timelineTo) {
        super.init41Anim(timelineFrom, timelineTo);
        showTopTranslationOnceTextLayerAnim(timelineFrom,timelineTo,randomNumList.get(showPosition));
    }

    @Override
    public void init41Show(float timelineFrom, float timelineTo) {
        super.init41Show(timelineFrom, timelineTo);
        showTopTranslationOnceTextLayerShow(timelineFrom,timelineTo,randomNumList.get(showPosition));
    }

    @Override
    public void init42Anim(float timelineFrom, float timelineTo) {
        super.init42Anim(timelineFrom, timelineTo);
        showTopTranslationTwiceTextLayerAnim(timelineFrom,timelineTo,randomNumList.get(showPosition));
    }

    @Override
    public void init42Show(float timelineFrom, float timelineTo) {
        super.init42Show(timelineFrom, timelineTo);
        showTopTranslationTwiceTextLayerShow(timelineFrom,timelineTo,randomNumList.get(showPosition));
    }

    @Override
    public void init43Anim(float timelineFrom, float timelineTo) {
        super.init43Anim(timelineFrom, timelineTo);
        showTopTranslationTwiceTextLayerShow(timelineFrom,timelineTo,randomNumList.get(showPosition));
        initScreenLayerLeft(timelineFrom,timelineTo);
    }

    @Override
    void initOther(float timelineFrom, float timelineTo) {


    }
}
