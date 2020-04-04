package com.caishi.chaoge.rd.overturn.drawtext;

  import com.caishi.chaoge.rd.overturn.TextLayer;

/**
 */
public class CustomDrawTextLayer17 extends CustomDrawTextLayer {
    public CustomDrawTextLayer17(TextLayer layer) {
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
    public void init11Anim(float timelineFrom, float timelineTo) {
        super.init11Anim(timelineFrom, timelineTo);
        mTextLayer.setFrame(null);
    }

    @Override
    public void init11Show(float timelineFrom, float timelineTo) {
        super.init11Show(timelineFrom, timelineTo);
        mTextLayer.setFrame(null);
    }

    @Override
    public void init12Anim(float timelineFrom, float timelineTo) {
        super.init12Anim(timelineFrom, timelineTo);
        mTextLayer.setFrame(null);
    }

    @Override
    public void init12Show(float timelineFrom, float timelineTo) {
        super.init12Show(timelineFrom, timelineTo);
        mTextLayer.setFrame(null);
    }

    @Override
    public void init13Anim(float timelineFrom, float timelineTo) {
        super.init13Anim(timelineFrom, timelineTo);
        mTextLayer.setFrame(null);
    }

    @Override
    public void init13Show(float timelineFrom, float timelineTo) {
        super.init13Show(timelineFrom, timelineTo);
        mTextLayer.setFrame(null);
    }

    @Override
    public void init14Anim(float timelineFrom, float timelineTo) {
        super.init14Anim(timelineFrom, timelineTo);
        mTextLayer.setFrame(null);
    }

    @Override
    public void init14Show(float timelineFrom, float timelineTo) {
        super.init14Show(timelineFrom, timelineTo);
        mTextLayer.setFrame(null);
    }

    @Override
    public void init15Anim(float timelineFrom, float timelineTo) {
        super.init15Anim(timelineFrom, timelineTo);
        mTextLayer.setFrame(null);
    }

    @Override
    public void init15Show(float timelineFrom, float timelineTo) {
        super.init15Show(timelineFrom, timelineTo);
        mTextLayer.setFrame(null);
    }

    @Override
    public void init16Anim(float timelineFrom, float timelineTo) {
        super.init16Anim(timelineFrom, timelineTo);
        mTextLayer.setFrame(null);
    }

    @Override
    public void init16Show(float timelineFrom, float timelineTo) {
        super.init16Show(timelineFrom, timelineTo);
        mTextLayer.setFrame(null);
    }

    @Override
    public void init17Anim(float timelineFrom, float timelineTo) {
        super.init17Anim(timelineFrom, timelineTo);
        showTextLayerAnim(timelineFrom,timelineTo);
    }

    @Override
    public void init17Show(float timelineFrom, float timelineTo) {
        super.init17Show(timelineFrom, timelineTo);
        showTextLayer(timelineFrom,timelineTo);
    }

    @Override
    public void init18Anim(float timelineFrom, float timelineTo) {
        super.init18Anim(timelineFrom, timelineTo);
        showTopTranslationOnceTextLayerShow(timelineFrom,timelineTo,showPosition-1,randomNumList.get(showPosition));
        initScreenLayer(timelineFrom,timelineTo);
    }

    @Override
    public void init18Show(float timelineFrom, float timelineTo) {
        super.init18Show(timelineFrom, timelineTo);
        showTextLayer(timelineFrom,timelineTo,randomNumList.get(showPosition));
        initScreenLayer2(timelineFrom,timelineTo);
    }
    @Override
    public void init19Anim(float timelineFrom, float timelineTo) {
        super.init11Anim(timelineFrom, timelineTo);
        showVerticalRightShrinkOnceTextLayerAnim(timelineFrom, timelineTo,randomNumList.get(showPosition));
        initScreenLayer2(timelineFrom, timelineTo);
    }

    @Override
    public void init19Show(float timelineFrom, float timelineTo) {
        super.init11Show(timelineFrom, timelineTo);
        showVerticalRightShrinkOnceTextLayerShow(timelineFrom, timelineTo,randomNumList.get(showPosition));
        initScreenLayer2(timelineFrom, timelineTo);
    }


    @Override
    public void init20Anim(float timelineFrom, float timelineTo) {
        super.init20Anim(timelineFrom, timelineTo);
        showVerticalRightShrinkTwiceTextLayerAnim(timelineFrom, timelineTo,randomNumList.get(showPosition-1),randomNumList.get(showPosition));
        initScreenLayer2(timelineFrom, timelineTo);
    }

    @Override
    public void init20Show(float timelineFrom, float timelineTo) {
        super.init20Show(timelineFrom, timelineTo);
        showVerticalRightShrinkTwiceTextLayerShow(timelineFrom, timelineTo,randomNumList.get(showPosition));
        initScreenLayer2(timelineFrom, timelineTo);
    }

    @Override
    void initOther(float timelineFrom, float timelineTo) {


    }
}
