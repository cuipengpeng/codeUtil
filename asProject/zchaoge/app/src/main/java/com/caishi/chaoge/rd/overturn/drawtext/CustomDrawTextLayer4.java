package com.caishi.chaoge.rd.overturn.drawtext;

  import com.caishi.chaoge.rd.overturn.TextLayer;

/**
 */
public class CustomDrawTextLayer4 extends CustomDrawTextLayer {
    public CustomDrawTextLayer4(TextLayer layer) {
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

        showTextLayerAnim(timelineFrom, timelineTo);

    }

    @Override
    void init4Show(float timelineFrom, float timelineTo) {
        showTextLayer(timelineFrom, timelineTo);
    }

    @Override
    void init5Anim(float timelineFrom, float timelineTo) {
        showTextLayer(timelineFrom, timelineTo);
        initScreenLayer(timelineFrom, timelineTo);
    }

    @Override
    void init5Show(float timelineFrom, float timelineTo) {
        showTextLayer(timelineFrom, timelineTo);
        initScreenLayer2(timelineFrom, timelineTo);
    }

    @Override
    void init6Anim(float timelineFrom, float timelineTo) {
        showVerticalRightShrinkOnceTextLayerAnim(timelineFrom, timelineTo,randomNumList.get(showPosition));
        initScreenLayer2(timelineFrom, timelineTo);
    }

    @Override
    void init6Show(float timelineFrom, float timelineTo) {
        showVerticalRightShrinkOnceTextLayerShow(timelineFrom, timelineTo,randomNumList.get(showPosition));
        initScreenLayer2(timelineFrom, timelineTo);
    }

    @Override
    void init7Anim(float timelineFrom, float timelineTo) {
        showVerticalRightShrinkTwiceTextLayerAnim(timelineFrom, timelineTo,randomNumList.get(showPosition-1),randomNumList.get(showPosition));
        initScreenLayer2(timelineFrom, timelineTo);
    }

    @Override
    void init7Show(float timelineFrom, float timelineTo) {
        showVerticalRightShrinkTwiceTextLayerShow(timelineFrom, timelineTo,randomNumList.get(showPosition));
        initScreenLayer2(timelineFrom, timelineTo);
    }

    @Override
    void init8Anim(float timelineFrom, float timelineTo) {
    }

    @Override
    void init8Show(float timelineFrom, float timelineTo) {

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
