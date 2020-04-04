package com.caishi.chaoge.rd.overturn.drawtext;

  import com.caishi.chaoge.rd.overturn.TextLayer;

/**
 */
public class CustomDrawTextLayer29 extends CustomDrawTextLayer {
    public CustomDrawTextLayer29(TextLayer layer) {
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
    public void init29Anim(float timelineFrom, float timelineTo) {
        super.init29Anim(timelineFrom, timelineTo);
        showTextLayerAnim(timelineFrom,timelineTo);
    }

    @Override
    public void init29Show(float timelineFrom, float timelineTo) {
        super.init29Show(timelineFrom, timelineTo);
        showTextLayer(timelineFrom,timelineTo);
    }

    @Override
    public void init30Anim(float timelineFrom, float timelineTo) {
        super.init30Anim(timelineFrom, timelineTo);
        showTextLayer(timelineFrom,timelineTo,randomNumList.get(showPosition));
        initScreenLayer(timelineFrom,timelineTo);
    }

    @Override
    public void init30Show(float timelineFrom, float timelineTo) {
        super.init30Show(timelineFrom, timelineTo);
        showTextLayer(timelineFrom,timelineTo,randomNumList.get(showPosition));
        initScreenLayer2(timelineFrom,timelineTo);
    }
    @Override
    public void init31Anim(float timelineFrom, float timelineTo) {
        super.init11Anim(timelineFrom, timelineTo);
        showVerticalRightShrinkOnceTextLayerAnim(timelineFrom, timelineTo,randomNumList.get(showPosition));
        initScreenLayer2(timelineFrom, timelineTo);
    }

    @Override
    public void init31Show(float timelineFrom, float timelineTo) {
        super.init11Show(timelineFrom, timelineTo);
        showVerticalRightShrinkOnceTextLayerShow(timelineFrom, timelineTo,randomNumList.get(showPosition));
        initScreenLayer2(timelineFrom, timelineTo);
    }

    @Override
    public void init32Anim(float timelineFrom, float timelineTo) {
        super.init11Anim(timelineFrom, timelineTo);
        showVerticalRightShrinkTwiceTextLayerAnim(timelineFrom, timelineTo,randomNumList.get(showPosition-1),randomNumList.get(showPosition));
        initScreenLayer2(timelineFrom, timelineTo);
    }

    @Override
    public void init32Show(float timelineFrom, float timelineTo) {
        super.init11Show(timelineFrom, timelineTo);
        showVerticalRightShrinkTwiceTextLayerShow(timelineFrom, timelineTo,randomNumList.get(showPosition));
        initScreenLayer2(timelineFrom, timelineTo);
    }

    @Override
    void initOther(float timelineFrom, float timelineTo) {


    }
}
