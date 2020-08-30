package com.test.xcamera.phonealbum.bean;



import com.editvideo.dataInfo.TransitionInfo;

import java.io.Serializable;

/**
 * 视频片段
 */
public class VideoParamClipData implements Serializable {
    /*视频地址*/
    private String filePath;
    /*转场信息*/
    private TransitionInfo transitionInfo;
    /*视频时长单位秒*/
    private float duration;
    /*速度*/
    private float speed=1;

    //校色数据

    private float m_brightnessVal;//曝光 0-10
    private float m_contrastVal;//对比度 0-10
    private float m_saturationVal;//饱和度 0-10
    private float m_vignetteVal; // 暗角 0-1
    private float m_sharpenVal;  // 锐度 0-5

    public VideoParamClipData(){
        m_brightnessVal = -1.0f;
        m_contrastVal = -1.0f;
        m_saturationVal = -1.0f;
        m_sharpenVal = 0;
        m_vignetteVal = 0;
    }
    public float getM_brightnessVal() {
        return m_brightnessVal;
    }

    public void setM_brightnessVal(float m_brightnessVal) {
        this.m_brightnessVal = m_brightnessVal;
    }

    public float getM_contrastVal() {
        return m_contrastVal;
    }

    public void setM_contrastVal(float m_contrastVal) {
        this.m_contrastVal = m_contrastVal;
    }

    public float getM_saturationVal() {
        return m_saturationVal;
    }

    public void setM_saturationVal(float m_saturationVal) {
        this.m_saturationVal = m_saturationVal;
    }

    public float getM_vignetteVal() {
        return m_vignetteVal;
    }

    public void setM_vignetteVal(float m_vignetteVal) {
        this.m_vignetteVal = m_vignetteVal;
    }

    public float getM_sharpenVal() {
        return m_sharpenVal;
    }

    public void setM_sharpenVal(float m_sharpenVal) {
        this.m_sharpenVal = m_sharpenVal;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public TransitionInfo getTransitionInfo() {
        return transitionInfo;
    }

    public void setTransitionInfo(TransitionInfo transitionInfo) {
        this.transitionInfo = transitionInfo;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

}
