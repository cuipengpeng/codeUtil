package com.caishi.chaoge.rd.overturn.drawtext;

  import com.caishi.chaoge.rd.overturn.TextLayer;

/**
 */
public class CustomDrawTextLayer25 extends CustomDrawTextLayer {
    public CustomDrawTextLayer25(TextLayer layer) {
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
    public void init25Anim(float timelineFrom, float timelineTo) {
        super.init25Anim(timelineFrom, timelineTo);
//        showTextLayerAnim(timelineFrom,timelineTo);
        showRightOverturnTextLayerAnim(timelineFrom,timelineTo);
    }

    @Override
    public void init25Show(float timelineFrom, float timelineTo) {
        super.init25Show(timelineFrom, timelineTo);
//        showTextLayer(timelineFrom,timelineTo);
        showRightOverturnTextLayerShow(timelineFrom,timelineTo);
    }

    @Override
    public void init26Anim(float timelineFrom, float timelineTo) {
        super.init26Anim(timelineFrom, timelineTo);
        showTopTranslationOnceTextLayerAnim(timelineFrom,timelineTo,randomNumList.get(showPosition));
    }

    @Override
    public void init26Show(float timelineFrom, float timelineTo) {
        super.init26Show(timelineFrom, timelineTo);
        showTopTranslationOnceTextLayerShow(timelineFrom,timelineTo,randomNumList.get(showPosition));
    }

    @Override
    public void init27Anim(float timelineFrom, float timelineTo) {
        super.init27Anim(timelineFrom, timelineTo);
        showTopTranslationTwiceTextLayerAnim(timelineFrom,timelineTo,randomNumList.get(showPosition));
    }

    @Override
    public void init27Show(float timelineFrom, float timelineTo) {
        super.init27Show(timelineFrom, timelineTo);
        showTopTranslationTwiceTextLayerShow(timelineFrom,timelineTo,randomNumList.get(showPosition));
    }

    @Override
    public void init28Anim(float timelineFrom, float timelineTo) {
        super.init28Anim(timelineFrom, timelineTo);
        showTopTranslationTwiceTextLayerShow(timelineFrom,timelineTo,randomNumList.get(showPosition));
        initScreenLayerLeft(timelineFrom,timelineTo);
    }

    @Override
    void initOther(float timelineFrom, float timelineTo) {


    }
}
