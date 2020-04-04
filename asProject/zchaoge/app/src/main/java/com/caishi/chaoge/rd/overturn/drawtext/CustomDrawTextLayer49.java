package com.caishi.chaoge.rd.overturn.drawtext;

  import com.caishi.chaoge.rd.overturn.TextLayer;

/**
 */
public class CustomDrawTextLayer49 extends CustomDrawTextLayer {
    public CustomDrawTextLayer49(TextLayer layer) {
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
    public void init49Anim(float timelineFrom, float timelineTo) {
        super.init40Anim(timelineFrom, timelineTo);
        showTextLayerAnim(timelineFrom, timelineTo);
    }

    @Override
    public void init49Show(float timelineFrom, float timelineTo) {
        super.init40Show(timelineFrom, timelineTo);
        showTextLayer(timelineFrom, timelineTo);
    }

    @Override
    public void init50Anim(float timelineFrom, float timelineTo) {
        super.init50Anim(timelineFrom, timelineTo);
        showTextLayer(timelineFrom, timelineTo,randomNumList.get(showPosition));
        initScreenLayer(timelineFrom,timelineTo);
    }

    @Override
    public void init50Show(float timelineFrom, float timelineTo) {
        super.init50Show(timelineFrom, timelineTo);
        showTextLayer(timelineFrom,timelineTo,randomNumList.get(showPosition));
        initScreenLayer2(timelineFrom,timelineTo);
    }

    @Override
    void initOther(float timelineFrom, float timelineTo) {
        showTextLayer(timelineFrom,timelineTo);
        initScreenLayer2(timelineFrom,timelineTo);

    }
}
