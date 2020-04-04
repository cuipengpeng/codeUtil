package com.caishi.chaoge.rd.overturn.drawtext;

  import com.caishi.chaoge.rd.overturn.TextLayer;

/**
 */
public class CustomDrawTextLayer37 extends CustomDrawTextLayer {
    public CustomDrawTextLayer37(TextLayer layer) {
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
    public void init37Anim(float timelineFrom, float timelineTo) {
        super.init37Anim(timelineFrom, timelineTo);
        showTextLayerAnim(timelineFrom, timelineTo);
    }

    @Override
    public void init37Show(float timelineFrom, float timelineTo) {
        super.init37Show(timelineFrom, timelineTo);

        showTextLayer(timelineFrom, timelineTo);
    }

    @Override
    public void init38Anim(float timelineFrom, float timelineTo) {
        super.init38Anim(timelineFrom, timelineTo);
        showTextLayer(timelineFrom, timelineTo,randomNumList.get(showPosition));
        initScreenLayerLeft(timelineFrom, timelineTo);
    }

    @Override
    public void init38Show(float timelineFrom, float timelineTo) {
        super.init38Show(timelineFrom, timelineTo);
        showLeftOverturnTextLayerShow(timelineFrom, timelineTo,randomNumList.get(showPosition));
    }

    @Override
    public void init39Anim(float timelineFrom, float timelineTo) {
        super.init39Anim(timelineFrom, timelineTo);
        showVerticalShrinkOnceTextLayerAnim(timelineFrom,timelineTo,randomNumList.get(showPosition));
    }

    @Override
    public void init39Show(float timelineFrom, float timelineTo) {
        super.init39Show(timelineFrom, timelineTo);
        showVerticalShrinkOnceTextLayerShow(timelineFrom,timelineTo,randomNumList.get(showPosition));
    }

    @Override
    public void init40Anim(float timelineFrom, float timelineTo) {
        super.init40Anim(timelineFrom, timelineTo);
        showVerticalShrinkOnceTextLayerShow(timelineFrom,timelineTo,randomNumList.get(showPosition));
        initScreenLayer(timelineFrom,timelineTo);
    }

    @Override
    void initOther(float timelineFrom, float timelineTo) {


    }
}
