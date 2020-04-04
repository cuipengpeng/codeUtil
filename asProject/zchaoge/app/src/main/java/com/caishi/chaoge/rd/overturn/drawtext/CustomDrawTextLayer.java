package com.caishi.chaoge.rd.overturn.drawtext;

import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import com.caishi.chaoge.bean.LrcBean;
import com.caishi.chaoge.rd.overturn.TextLayer;
import com.caishi.chaoge.utils.LogUtil;
import com.rd.vecore.models.CanvasObject;

import java.util.List;

/**
 * 自定义绘制文本-处理时间线
 */
public abstract class CustomDrawTextLayer {
    //左下角顶点 （水平状态下，矩形的左下角的顶点坐标）
    protected int x = 73;
    protected int y = 462;
    protected int showPosition = -1;
    protected int tow = -1;//第二次平移的位置


    public CustomDrawTextLayer(TextLayer layer) {
        mTextLayer = layer;
    }

    protected TextLayer mTextLayer;
    protected static final float fW = 480.0f, fH = 852.0f;
    protected List<Integer> textHeightList;
    protected List<Float> randomNumList;
    protected float currentProgress;

    /**
     * 配置当前时刻，当前layer的效果并绘制
     *
     * @param canvas
     * @param currentProgress 当前进度：秒
     */
    public void initConfigAndDraw(CanvasObject canvas, float currentProgress, List<LrcBean> lrcList, List<Integer> textHeightList, List<Float> randomNumList) {
        this.textHeightList = textHeightList;
        this.randomNumList = randomNumList;
        this.currentProgress = currentProgress;
        float timelineFrom = 0f;
        float timelineTo;
        for (int i = 0; i < lrcList.size(); i++) {
            if ((lrcList.get(i).getStart() / 1000f) <= currentProgress && currentProgress < (lrcList.get(i).getEnd() / 1000f)) {
                showPosition = i;
            }
        }
        LogUtil.d("showPosition===" + showPosition);
        if (0 <= currentProgress && currentProgress < (timelineTo = lrcList.get(0).getEnd() / 1000f)) {
            init1Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 2 && (timelineFrom = lrcList.get(1).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(1).getStart() / 1000f + 0.2f)) {

            init2Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 2 && (timelineFrom = lrcList.get(1).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(1).getEnd() / 1000f)) {

            init2Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 3 && (timelineFrom = lrcList.get(2).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(2).getStart() / 1000f + 0.2f)) {

            init3Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 3 && (timelineFrom = lrcList.get(2).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(2).getEnd() / 1000f)) {

            init3Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 4 && (timelineFrom = lrcList.get(3).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(3).getStart() / 1000f + 0.2f)) {

            init4Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 4 && (timelineFrom = lrcList.get(3).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(3).getEnd() / 1000f)) {

            init4Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 5 && (timelineFrom = lrcList.get(4).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(4).getStart() / 1000f + 0.2f)) {

            init5Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 5 && (timelineFrom = lrcList.get(4).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(4).getEnd() / 1000f)) {

            init5Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 6 && (timelineFrom = lrcList.get(5).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(5).getStart() / 1000f + 0.2f)) {

            init6Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 6 && (timelineFrom = lrcList.get(5).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(5).getEnd() / 1000f)) {

            init6Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 7 && (timelineFrom = lrcList.get(6).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(6).getStart() / 1000f + 2f)) {

            init7Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 7 && (timelineFrom = lrcList.get(6).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(6).getEnd() / 1000f)) {

            init7Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 8 && (timelineFrom = lrcList.get(7).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(7).getStart() / 1000f + 2f)) {

            init8Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 8 && (timelineFrom = lrcList.get(7).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(7).getEnd() / 1000f)) {

            init8Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 9 && (timelineFrom = lrcList.get(8).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(8).getStart() / 1000f + 0.2f)) {

            init9Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 9 && (timelineFrom = lrcList.get(8).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(8).getEnd() / 1000f)) {

            init9Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 10 && (timelineFrom = lrcList.get(9).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(9).getStart() / 1000f + 0.2f)) {

            init10Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 10 && (timelineFrom = lrcList.get(9).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(9).getEnd() / 1000f)) {

            init10Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 11 && (timelineFrom = lrcList.get(10).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(10).getStart() / 1000f + 0.2f)) {

            init11Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 11 && (timelineFrom = lrcList.get(10).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(10).getEnd() / 1000f)) {

            init11Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 12 && (timelineFrom = lrcList.get(11).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(11).getStart() / 1000f + 0.2f)) {

            init12Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 12 && (timelineFrom = lrcList.get(11).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(11).getEnd() / 1000f)) {

            init12Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 13 && (timelineFrom = lrcList.get(12).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(12).getStart() / 1000f + 0.2f)) {

            init13Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 13 && (timelineFrom = lrcList.get(12).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(12).getEnd() / 1000f)) {

            init13Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 14 && (timelineFrom = lrcList.get(13).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(13).getStart() / 1000f + 0.2f)) {

            init14Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 14 && (timelineFrom = lrcList.get(13).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(13).getEnd() / 1000f)) {

            init14Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 15 && (timelineFrom = lrcList.get(14).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(14).getStart() / 1000f + 0.2f)) {

            init15Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 15 && (timelineFrom = lrcList.get(14).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(14).getEnd() / 1000f)) {

            init15Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 16 && (timelineFrom = lrcList.get(15).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(15).getStart() / 1000f + 0.2f)) {

            init16Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 16 && (timelineFrom = lrcList.get(15).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(15).getEnd() / 1000f)) {

            init16Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 17 && (timelineFrom = lrcList.get(16).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(16).getStart() / 1000f + 0.2f)) {

            init17Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 17 && (timelineFrom = lrcList.get(16).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(16).getEnd() / 1000f)) {

            init17Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 18 && (timelineFrom = lrcList.get(17).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(17).getStart() / 1000f + 0.2f)) {

            init18Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 18 && (timelineFrom = lrcList.get(17).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(17).getEnd() / 1000f)) {

            init18Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 19 && (timelineFrom = lrcList.get(18).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(18).getStart() / 1000f + 0.2f)) {

            init19Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 19 && (timelineFrom = lrcList.get(18).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(18).getEnd() / 1000f)) {

            init19Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 20 && (timelineFrom = lrcList.get(19).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(19).getStart() / 1000f + 0.2f)) {

            init20Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 20 && (timelineFrom = lrcList.get(19).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(19).getEnd() / 1000f)) {

            init20Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 21 && (timelineFrom = lrcList.get(20).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(20).getStart() / 1000f + 0.2f)) {

            init21Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 21 && (timelineFrom = lrcList.get(20).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(20).getEnd() / 1000f)) {

            init21Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 22 && (timelineFrom = lrcList.get(21).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(21).getStart() / 1000f + 0.2f)) {

            init22Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 22 && (timelineFrom = lrcList.get(21).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(21).getEnd() / 1000f)) {

            init22Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 23 && (timelineFrom = lrcList.get(22).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(22).getStart() / 1000f + 0.2f)) {

            init23Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 23 && (timelineFrom = lrcList.get(22).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(22).getEnd() / 1000f)) {

            init23Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 24 && (timelineFrom = lrcList.get(23).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(23).getStart() / 1000f + 0.2f)) {

            init24Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 24 && (timelineFrom = lrcList.get(23).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(23).getEnd() / 1000f)) {

            init24Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 25 && (timelineFrom = lrcList.get(24).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(24).getStart() / 1000f + 0.2f)) {

            init25Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 25 && (timelineFrom = lrcList.get(24).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(24).getEnd() / 1000f)) {

            init25Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 26 && (timelineFrom = lrcList.get(25).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(25).getStart() / 1000f + 0.2f)) {

            init26Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 26 && (timelineFrom = lrcList.get(25).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(25).getEnd() / 1000f)) {

            init26Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 27 && (timelineFrom = lrcList.get(26).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(26).getStart() / 1000f + 0.2f)) {

            init27Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 27 && (timelineFrom = lrcList.get(26).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(26).getEnd() / 1000f)) {

            init27Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 28 && (timelineFrom = lrcList.get(27).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(27).getStart() / 1000f + 0.2f)) {

            init28Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 28 && (timelineFrom = lrcList.get(27).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(27).getEnd() / 1000f)) {

            init28Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 29 && (timelineFrom = lrcList.get(28).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(28).getStart() / 1000f + 0.2f)) {

            init29Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 29 && (timelineFrom = lrcList.get(28).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(28).getEnd() / 1000f)) {

            init29Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 30 && (timelineFrom = lrcList.get(29).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(29).getStart() / 1000f + 0.2f)) {

            init30Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 30 && (timelineFrom = lrcList.get(29).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(29).getEnd() / 1000f)) {

            init30Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 31 && (timelineFrom = lrcList.get(30).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(30).getStart() / 1000f + 0.2f)) {

            init31Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 31 && (timelineFrom = lrcList.get(30).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(30).getEnd() / 1000f)) {

            init31Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 32 && (timelineFrom = lrcList.get(31).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(31).getStart() / 1000f + 0.2f)) {

            init32Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 32 && (timelineFrom = lrcList.get(31).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(31).getEnd() / 1000f)) {

            init32Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 33 && (timelineFrom = lrcList.get(32).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(32).getStart() / 1000f + 0.2f)) {

            init33Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 33 && (timelineFrom = lrcList.get(32).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(32).getEnd() / 1000f)) {

            init33Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 34 && (timelineFrom = lrcList.get(33).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(33).getStart() / 1000f + 0.2f)) {

            init34Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 34 && (timelineFrom = lrcList.get(33).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(33).getEnd() / 1000f)) {

            init34Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 35 && (timelineFrom = lrcList.get(34).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(34).getStart() / 1000f + 0.2f)) {

            init35Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 35 && (timelineFrom = lrcList.get(34).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(34).getEnd() / 1000f)) {

            init35Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 36 && (timelineFrom = lrcList.get(35).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(35).getStart() / 1000f + 0.2f)) {

            init36Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 36 && (timelineFrom = lrcList.get(35).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(35).getEnd() / 1000f)) {

            init36Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 37 && (timelineFrom = lrcList.get(36).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(36).getStart() / 1000f + 0.2f)) {

            init37Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 37 && (timelineFrom = lrcList.get(36).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(36).getEnd() / 1000f)) {

            init37Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 38 && (timelineFrom = lrcList.get(37).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(37).getStart() / 1000f + 0.2f)) {

            init38Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 38 && (timelineFrom = lrcList.get(37).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(37).getEnd() / 1000f)) {

            init38Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 39 && (timelineFrom = lrcList.get(38).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(38).getStart() / 1000f + 0.2f)) {

            init39Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 39 && (timelineFrom = lrcList.get(38).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(38).getEnd() / 1000f)) {

            init39Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 40 && (timelineFrom = lrcList.get(39).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(39).getStart() / 1000f + 0.2f)) {

            init40Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 40 && (timelineFrom = lrcList.get(39).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(39).getEnd() / 1000f)) {

            init40Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 41 && (timelineFrom = lrcList.get(40).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(40).getStart() / 1000f + 0.2f)) {

            init41Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 41 && (timelineFrom = lrcList.get(40).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(40).getEnd() / 1000f)) {

            init41Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 42 && (timelineFrom = lrcList.get(41).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(41).getStart() / 1000f + 0.2f)) {

            init42Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 42 && (timelineFrom = lrcList.get(41).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(41).getEnd() / 1000f)) {

            init42Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 43 && (timelineFrom = lrcList.get(42).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(42).getStart() / 1000f + 0.2f)) {

            init43Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 43 && (timelineFrom = lrcList.get(42).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(42).getEnd() / 1000f)) {

            init43Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 44 && (timelineFrom = lrcList.get(43).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(43).getStart() / 1000f + 0.2f)) {

            init44Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 44 && (timelineFrom = lrcList.get(43).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(43).getEnd() / 1000f)) {

            init44Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 45 && (timelineFrom = lrcList.get(44).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(44).getStart() / 1000f + 0.2f)) {

            init45Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 45 && (timelineFrom = lrcList.get(44).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(44).getEnd() / 1000f)) {

            init45Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 46 && (timelineFrom = lrcList.get(45).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(45).getStart() / 1000f + 0.2f)) {

            init46Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 46 && (timelineFrom = lrcList.get(45).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(45).getEnd() / 1000f)) {

            init46Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 47 && (timelineFrom = lrcList.get(46).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(46).getStart() / 1000f + 0.2f)) {

            init47Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 47 && (timelineFrom = lrcList.get(46).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(46).getEnd() / 1000f)) {

            init47Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 48 && (timelineFrom = lrcList.get(47).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(47).getStart() / 1000f + 0.2f)) {

            init48Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 48 && (timelineFrom = lrcList.get(47).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(47).getEnd() / 1000f)) {

            init48Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 49 && (timelineFrom = lrcList.get(48).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(48).getStart() / 1000f + 0.2f)) {

            init49Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 49 && (timelineFrom = lrcList.get(48).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(48).getEnd() / 1000f)) {

            init49Show(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 50 && (timelineFrom = lrcList.get(49).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(49).getStart() / 1000f + 0.2f)) {

            init50Anim(timelineFrom, timelineTo);
        } else if (lrcList.size() >= 50 && (timelineFrom = lrcList.get(49).getStart() / 1000f) <= currentProgress
                && currentProgress < (timelineTo = lrcList.get(49).getEnd() / 1000f)) {
            init50Show(timelineFrom, timelineTo);
        } else {
            initOther(lrcList.get(lrcList.size() - 1).getStart() / 1000f, 1000f);
        }
        if (null != canvas) {
            drawText(canvas, currentProgress);
        }


    }


    /**
     * 第一条字幕显示
     */
    abstract void init1Show(float timelineFrom, float timelineTo);

    /**
     * 第二条字幕动画时间
     */
    abstract void init2Anim(float timelineFrom, float timelineTo);

    /**
     * 第二条字幕显示
     */
    abstract void init2Show(float timelineFrom, float timelineTo);

    /**
     * 第三条字幕动画时间
     */
    abstract void init3Anim(float timelineFrom, float timelineTo);

    /**
     * 第三条字幕显示
     */
    abstract void init3Show(float timelineFrom, float timelineTo);

    /**
     * 第四条字幕动画时间
     */
    abstract void init4Anim(float timelineFrom, float timelineTo);

    /**
     * 第四条字幕显示
     */
    abstract void init4Show(float timelineFrom, float timelineTo);

    /**
     * 第五条字幕动画时间
     */
    abstract void init5Anim(float timelineFrom, float timelineTo);

    /**
     * 第五条字幕显示
     */
    abstract void init5Show(float timelineFrom, float timelineTo);

    /**
     * 第六条字幕动画时间
     */
    abstract void init6Anim(float timelineFrom, float timelineTo);

    /**
     * 第六条字幕显示
     */
    abstract void init6Show(float timelineFrom, float timelineTo);

    /**
     * 第七条字幕动画时间
     */
    abstract void init7Anim(float timelineFrom, float timelineTo);

    /**
     * 第七条字幕显示
     */
    abstract void init7Show(float timelineFrom, float timelineTo);

    /**
     * 第八条字幕动画时间
     */
    abstract void init8Anim(float timelineFrom, float timelineTo);

    /**
     * 第八条字幕显示
     */
    abstract void init8Show(float timelineFrom, float timelineTo);

    /**
     * 第九条字幕动画时间
     */
    abstract void init9Anim(float timelineFrom, float timelineTo);

    /**
     * 第九条字幕显示
     */
    abstract void init9Show(float timelineFrom, float timelineTo);

    /**
     * 第10条字动画时间
     */
    abstract void init10Anim(float timelineFrom, float timelineTo);

    /**
     * 第10条字幕显示
     */
    abstract void init10Show(float timelineFrom, float timelineTo);


    /**
     * 第11条字动画时间
     */
    public void init11Anim(float timelineFrom, float timelineTo) {

    }


    /**
     * 第11条字幕显示
     */
    public void init11Show(float timelineFrom, float timelineTo) {

    }

    /**
     * 第12条字动画时间
     */
    public void init12Anim(float timelineFrom, float timelineTo) {

    }

    /**
     * 第12条字幕显示
     */
    public void init12Show(float timelineFrom, float timelineTo) {

    }


    /**
     * 第13条字动画时间
     */
    public void init13Anim(float timelineFrom, float timelineTo) {

    }

    /**
     * 第13条字幕显示
     */
    public void init13Show(float timelineFrom, float timelineTo) {

    }

    /**
     * 第14条字动画时间
     */
    public void init14Anim(float timelineFrom, float timelineTo) {

    }

    /**
     * 第14条字幕显示
     */
    public void init14Show(float timelineFrom, float timelineTo) {

    }

    /**
     * 第15条字动画时间
     */
    public void init15Anim(float timelineFrom, float timelineTo) {

    }

    /**
     * 第15条字幕显示
     */
    public void init15Show(float timelineFrom, float timelineTo) {

    }


    /**
     * 第16条字动画时间
     */
    public void init16Anim(float timelineFrom, float timelineTo) {

    }

    /**
     * 第16条字幕显示
     */
    public void init16Show(float timelineFrom, float timelineTo) {

    }


    /**
     * 第17条字动画时间
     */
    public void init17Anim(float timelineFrom, float timelineTo) {

    }

    /**
     * 第17条字幕显示
     */
    public void init17Show(float timelineFrom, float timelineTo) {

    }


    /**
     * 第18条字动画时间
     */
    public void init18Anim(float timelineFrom, float timelineTo) {

    }

    /**
     * 第18条字幕显示
     */
    public void init18Show(float timelineFrom, float timelineTo) {

    }


    /**
     * 第19条字动画时间
     */
    public void init19Anim(float timelineFrom, float timelineTo) {

    }

    /**
     * 第10条字幕显示
     */
    public void init19Show(float timelineFrom, float timelineTo) {

    }


    /**
     * 第20条字动画时间
     */
    public void init20Anim(float timelineFrom, float timelineTo) {

    }

    /**
     * 第20条字幕显示
     */
    public void init20Show(float timelineFrom, float timelineTo) {

    }

    /**
     * 第21条字动画时间
     */
    public void init21Anim(float timelineFrom, float timelineTo) {

    }

    /**
     * 第21条字幕显示
     */
    public void init21Show(float timelineFrom, float timelineTo) {

    }

    /**
     * 第22条字动画时间
     */
    public void init22Anim(float timelineFrom, float timelineTo) {

    }

    /**
     * 第22条字幕显示
     */
    public void init22Show(float timelineFrom, float timelineTo) {

    }

    /**
     * 第23条字动画时间
     */
    public void init23Anim(float timelineFrom, float timelineTo) {

    }

    /**
     * 第23条字幕显示
     */
    public void init23Show(float timelineFrom, float timelineTo) {

    }

    /**
     * 第24条字动画时间
     */
    public void init24Anim(float timelineFrom, float timelineTo) {

    }

    /**
     * 第24条字幕显示
     */
    public void init24Show(float timelineFrom, float timelineTo) {

    }

    /**
     * 第25条字动画时间
     */
    public void init25Anim(float timelineFrom, float timelineTo) {

    }

    /**
     * 第25条字幕显示
     */
    public void init25Show(float timelineFrom, float timelineTo) {

    }


    /**
     * 第26条字动画时间
     */
    public void init26Anim(float timelineFrom, float timelineTo) {

    }

    /**
     * 第26条字幕显示
     */
    public void init26Show(float timelineFrom, float timelineTo) {

    }

    /**
     * 第27条字动画时间
     */
    public void init27Anim(float timelineFrom, float timelineTo) {

    }

    /**
     * 第20条字幕显示
     */
    public void init27Show(float timelineFrom, float timelineTo) {

    }

    /**
     * 第28条字动画时间
     */
    public void init28Anim(float timelineFrom, float timelineTo) {

    }

    /**
     * 第28条字幕显示
     */
    public void init28Show(float timelineFrom, float timelineTo) {

    }

    /**
     * 第29条字动画时间
     */
    public void init29Anim(float timelineFrom, float timelineTo) {

    }

    /**
     * 第29条字幕显示
     */
    public void init29Show(float timelineFrom, float timelineTo) {

    }

    /**
     * 第30条字动画时间
     */
    public void init30Anim(float timelineFrom, float timelineTo) {

    }

    /**
     * 第30条字幕显示
     */
    public void init30Show(float timelineFrom, float timelineTo) {
    }

    /**
     * 第31条字动画时间
     */
    public void init31Anim(float timelineFrom, float timelineTo) {
    }

    /**
     * 第31条字幕显示
     */
    public void init31Show(float timelineFrom, float timelineTo) {
    }


    /**
     * 第32条字动画时间
     */
    public void init32Anim(float timelineFrom, float timelineTo) {
    }

    /**
     * 第32条字幕显示
     */
    public void init32Show(float timelineFrom, float timelineTo) {

    }

    /**
     * 第33条字动画时间
     */
    public void init33Anim(float timelineFrom, float timelineTo) {
    }

    /**
     * 第33条字幕显示
     */
    public void init33Show(float timelineFrom, float timelineTo) {
    }

    /**
     * 第34条字动画时间
     */
    public void init34Anim(float timelineFrom, float timelineTo) {
    }

    /**
     * 第34条字幕显示
     */
    public void init34Show(float timelineFrom, float timelineTo) {
    }

    /**
     * 第35条字动画时间
     */
    public void init35Anim(float timelineFrom, float timelineTo) {
    }

    /**
     * 第35条字幕显示
     */
    public void init35Show(float timelineFrom, float timelineTo) {
    }

    /**
     * 第36条字动画时间
     */
    public void init36Anim(float timelineFrom, float timelineTo) {
    }

    /**
     * 第36条字幕显示
     */
    public void init36Show(float timelineFrom, float timelineTo) {
    }

    /**
     * 第37条字动画时间
     */
    public void init37Anim(float timelineFrom, float timelineTo) {
    }

    /**
     * 第37条字幕显示
     */
    public void init37Show(float timelineFrom, float timelineTo) {
    }

    /**
     * 第38条字动画时间
     */
    public void init38Anim(float timelineFrom, float timelineTo) {
    }

    /**
     * 第38条字幕显示
     */
    public void init38Show(float timelineFrom, float timelineTo) {
    }

    /**
     * 第39条字动画时间
     */
    public void init39Anim(float timelineFrom, float timelineTo) {
    }

    /**
     * 第39条字幕显示
     */
    public void init39Show(float timelineFrom, float timelineTo) {
    }

    /**
     * 第40条字动画时间
     */
    public void init40Anim(float timelineFrom, float timelineTo) {
    }

    /**
     * 第40条字幕显示
     */
    public void init40Show(float timelineFrom, float timelineTo) {
    }


    /**
     * 第41条字动画时间
     */
    public void init41Anim(float timelineFrom, float timelineTo) {

    }

    /**
     * 第41条字幕显示
     */
    public void init41Show(float timelineFrom, float timelineTo) {
    }

    /**
     * 第42条字动画时间
     */
    public void init42Anim(float timelineFrom, float timelineTo) {
    }

    /**
     * 第42条字幕显示
     */
    public void init42Show(float timelineFrom, float timelineTo) {
    }

    /**
     * 第43条字动画时间
     */
    public void init43Anim(float timelineFrom, float timelineTo) {
    }

    /**
     * 第43条字幕显示
     */
    public void init43Show(float timelineFrom, float timelineTo) {
    }

    /**
     * 第44条字动画时间
     */
    public void init44Anim(float timelineFrom, float timelineTo) {
    }

    /**
     * 第44条字幕显示
     */
    public void init44Show(float timelineFrom, float timelineTo) {
    }

    /**
     * 第45条字动画时间
     */
    public void init45Anim(float timelineFrom, float timelineTo) {
    }

    /**
     * 第45条字幕显示
     */
    public void init45Show(float timelineFrom, float timelineTo) {
    }

    /**
     * 第46条字动画时间
     */
    public void init46Anim(float timelineFrom, float timelineTo) {
    }

    /**
     * 第46条字幕显示
     */
    public void init46Show(float timelineFrom, float timelineTo) {
    }

    /**
     * 第47条字动画时间
     */
    public void init47Anim(float timelineFrom, float timelineTo) {
    }

    /**
     * 第47条字幕显示
     */
    public void init47Show(float timelineFrom, float timelineTo) {
    }

    /**
     * 第48条字动画时间
     */
    public void init48Anim(float timelineFrom, float timelineTo) {
    }

    /**
     * 第48条字幕显示
     */
    public void init48Show(float timelineFrom, float timelineTo) {
    }

    /**
     * 第49条字动画时间
     */
    public void init49Anim(float timelineFrom, float timelineTo) {
    }

    /**
     * 第49条字幕显示
     */
    public void init49Show(float timelineFrom, float timelineTo) {
    }

    /**
     * 第50条字动画时间
     */
    public void init50Anim(float timelineFrom, float timelineTo) {

    }

    /**
     * 第50条字幕显示
     */
    public void init50Show(float timelineFrom, float timelineTo) {

    }

    abstract void initOther(float timelineFrom, float timelineTo);

    /**
     * 实时绘制
     *
     * @param canvas
     * @param progress
     */
    private void drawText(CanvasObject canvas, float progress) {
        mTextLayer.onDraw(canvas, progress);
    }


    /**
     * @param rect
     * @param fW
     * @param fH
     * @return
     */
    RectF rect2RectF(Rect rect, float fW, float fH) {
        return new RectF(rect.left / fW, rect.top / fH, rect.right / fW, rect.bottom / fH);
    }

    RectF rect2RectF(RectF rect, float fW, float fH) {
        return new RectF(rect.left / fW, rect.top / fH, rect.right / fW, rect.bottom / fH);
    }

    /**
     * 直接显示
     *
     * @param timelineFrom 开始时间
     * @param timelineTo   结束时间
     */
    protected void showTextLayerAnim(float timelineFrom, float timelineTo) {

        int l = 73;
        int b = 462;


        float scale = 1f;
        RectF rect = new RectF(l, b - (mTextLayer.getHeight() * scale), l + mTextLayer.getWidth() * scale, b);
        RectF rectF = new RectF(rect.left / fW, rect.top / fH, rect.right / fW, rect.bottom / fH);


        RectF src1 = rect2RectF(new RectF(l, b - 2, l + 2, b), fW, fH);
        TextLayer.IFrame iFrame = new TextLayer.IFrame(timelineFrom, timelineTo, src1, new RectF(rectF));
        mTextLayer.setFrame(iFrame);
    }

    /**
     * 直接显示
     *
     * @param timelineFrom 开始时间
     * @param timelineTo   结束时间
     */
    protected void showTextLayer(float timelineFrom, float timelineTo) {

        int l = 73;
        int b = 462;


        float scale = 1f;
        RectF rect = new RectF(l, b - (mTextLayer.getHeight() * scale), l + mTextLayer.getWidth() * scale, b);
        RectF rectF = new RectF(rect.left / fW, rect.top / fH, rect.right / fW, rect.bottom / fH);
        TextLayer.IFrame iFrame = new TextLayer.IFrame(timelineFrom, timelineTo, new RectF(rectF), new RectF(rectF));
        mTextLayer.setFrame(iFrame);
    }

    /**
     * @param timelineFrom 开始时间
     * @param timelineTo   结束时间
     * @param newScale     缩放比
     */
    protected void showTextLayer(float timelineFrom, float timelineTo, float newScale) {

        int l = 73;
        int b = 462;


        RectF rect = new RectF(l , b - mTextLayer.getHeight() * newScale, l + mTextLayer.getWidth()* newScale, b);
        RectF rectF = new RectF(rect.left / fW, rect.top / fH, rect.right / fW, rect.bottom / fH);
        TextLayer.IFrame iFrame = new TextLayer.IFrame(timelineFrom, timelineTo, new RectF(rectF), new RectF(rectF));
        mTextLayer.setFrame(iFrame);
    }


    /**
     * 右竖 缩放第一次 动画
     *
     * @param timelineFrom
     * @param timelineTo
     */
    protected void showVerticalRightShrinkOnceTextLayerAnim(float timelineFrom, float timelineTo, float newScale) {

        int l = 73;
        int b = 462;
        float scale = 1f;
        RectF rect = new RectF(l, b - (mTextLayer.getHeight() * scale), l + mTextLayer.getWidth() * scale, b);
        RectF rectF = new RectF(rect.left / fW, rect.top / fH, rect.right / fW, rect.bottom / fH);

        scale = newScale;
        RectF rectNew = new RectF(l - textHeightList.get(showPosition ) * scale, b - (mTextLayer.getHeight() * scale),
                (l + mTextLayer.getWidth()) - textHeightList.get(showPosition ) * scale, b);
        RectF rectFNew = new RectF(rectNew.left / fW, rectNew.top / fH, rectNew.right / fW, rectNew.bottom / fH);
        TextLayer.IFrame iFrame = new TextLayer.IFrame(timelineFrom, timelineTo, new RectF(rectF), new RectF(rectFNew));
        mTextLayer.setFrame(iFrame);
    }

    /**
     * 右竖 缩放第一次 显示
     *
     * @param timelineFrom
     * @param timelineTo
     */
    protected void showVerticalRightShrinkOnceTextLayerShow(float timelineFrom, float timelineTo, float newScale) {

        int l = 73;
        int b = 462;
        RectF rectNew = new RectF(l - textHeightList.get(showPosition ) * newScale, b - (mTextLayer.getHeight() * newScale),
                (l + mTextLayer.getWidth()) - textHeightList.get(showPosition ) * newScale, b);
        RectF rectFNew = new RectF(rectNew.left / fW, rectNew.top / fH, rectNew.right / fW, rectNew.bottom / fH);
        TextLayer.IFrame iFrame = new TextLayer.IFrame(timelineFrom, timelineTo, new RectF(rectFNew), new RectF(rectFNew));
        mTextLayer.setFrame(iFrame);
    }

    /**
     * 右竖 缩放第二次 动画
     *
     * @param timelineFrom
     * @param timelineTo
     */
    protected void showVerticalRightShrinkTwiceTextLayerAnim(float timelineFrom, float timelineTo, float oneScale, float twoScale) {

        int l = 73;
        int b = 462;
        float scale = 1f;
        RectF rect = new RectF(l - textHeightList.get(showPosition) * scale, b - (mTextLayer.getHeight() * scale),
                (l + mTextLayer.getWidth()) - textHeightList.get(showPosition) * scale, b);
        RectF rectF = new RectF(rect.left / fW, rect.top / fH, rect.right / fW, rect.bottom / fH);
        l = (int) (l - textHeightList.get(showPosition ) * scale);
        scale = twoScale;
        RectF rectNew = new RectF(l - textHeightList.get(showPosition - 1) * scale, b - (mTextLayer.getHeight() * scale),
                (l + mTextLayer.getWidth()) - textHeightList.get(showPosition - 1) * scale, b);
        RectF rectFNew = new RectF(rectNew.left / fW, rectNew.top / fH, rectNew.right / fW, rectNew.bottom / fH);
        TextLayer.IFrame iFrame = new TextLayer.IFrame(timelineFrom, timelineTo, new RectF(rectF), new RectF(rectFNew));
        mTextLayer.setFrame(iFrame);
    }

    /**
     * 右竖 缩放第二次 显示
     *
     * @param timelineFrom
     * @param timelineTo
     */
    protected void showVerticalRightShrinkTwiceTextLayerShow(float timelineFrom, float timelineTo, float twoScale) {

        int l = 73;
        int b = 462;
        l = (int) (l - textHeightList.get(showPosition ) * twoScale);
        RectF rectNew = new RectF(l - textHeightList.get(showPosition - 1) * twoScale, b - (mTextLayer.getHeight() * twoScale),
                (l + mTextLayer.getWidth()) - textHeightList.get(showPosition - 1) * twoScale, b);
        RectF rectFNew = new RectF(rectNew.left / fW, rectNew.top / fH, rectNew.right / fW, rectNew.bottom / fH);
        TextLayer.IFrame iFrame = new TextLayer.IFrame(timelineFrom, timelineTo, new RectF(rectFNew), new RectF(rectFNew));
        mTextLayer.setFrame(iFrame);
    }


    /**
     * 向上平移一次 动画
     *
     * @param timelineFrom 开始时间
     * @param timelineTo   结束时间
     */
    protected void showTopTranslationOnceTextLayerAnim(float timelineFrom, float timelineTo, float newScale) {
        int l = 73;
        int b = 462;

        float scale = 1f;
        RectF rect = new RectF(l, b - mTextLayer.getHeight() * scale, l + mTextLayer.getWidth() * scale, b);
        RectF rectF = new RectF(rect.left / fW, rect.top / fH, rect.right / fW, rect.bottom / fH);


        //上移动-变小
        b = (int) (b - textHeightList.get(showPosition) * scale);
        scale = newScale;
        RectF dst = new RectF(rect.left / fW, (b - mTextLayer.getHeight() * scale) / fH, (rect.left + mTextLayer.getWidth() * scale) / fW, b / fH);
        TextLayer.IFrame iFrame = new TextLayer.IFrame(timelineFrom, timelineTo, rectF, new RectF(dst));
        mTextLayer.setFrame(iFrame);
    }

    /**
     * 向上平移一次 显示
     *
     * @param timelineFrom 开始时间
     * @param timelineTo   结束时间
     */
    protected void showTopTranslationOnceTextLayerShow(float timelineFrom, float timelineTo, float newScale) {
        int l = 73;
        int b = 462;

        float scale = 1f;
        RectF rect = new RectF(l, b - mTextLayer.getHeight() * scale, l + mTextLayer.getWidth() * scale, b);

        b = (int) (b - textHeightList.get(showPosition) * scale);
        scale = newScale;
        RectF dst = new RectF(rect.left / fW, (b - mTextLayer.getHeight() * scale) / fH, (rect.left + mTextLayer.getWidth() * scale) / fW, b / fH);
        TextLayer.IFrame iFrame = new TextLayer.IFrame(timelineFrom, timelineTo, new RectF(dst), new RectF(dst));
        mTextLayer.setFrame(iFrame);
    }

    /**
     * 向上平移一次 显示
     *
     * @param timelineFrom 开始时间
     * @param timelineTo   结束时间
     */
    protected void showTopTranslationOnceTextLayerShow(float timelineFrom, float timelineTo, int showPosition, float newScale) {
        int l = 73;
        int b = 462;

        float scale = 1f;
        RectF rect = new RectF(l, b - mTextLayer.getHeight() * scale, l + mTextLayer.getWidth() * scale, b);

        b = (int) (b - textHeightList.get(showPosition) * scale);
        scale = newScale;
        RectF dst = new RectF(rect.left / fW, (b - mTextLayer.getHeight() * scale) / fH, (rect.left + mTextLayer.getWidth() * scale) / fW, b / fH);
        TextLayer.IFrame iFrame = new TextLayer.IFrame(timelineFrom, timelineTo, new RectF(dst), new RectF(dst));
        mTextLayer.setFrame(iFrame);
    }

    /**
     * 向上平移两次  动画
     *
     * @param timelineFrom 开始时间
     * @param timelineTo   结束时间
     */
    protected void showTopTranslationTwiceTextLayerAnim(float timelineFrom, float timelineTo, float oneScale) {
        int l = 73;
        int b = 462;

        float scale = 1f;
        RectF rect = new RectF(l, b - mTextLayer.getHeight() * scale, l + mTextLayer.getWidth() * scale, b);


        b = (int) (b - textHeightList.get(showPosition) * scale);
        scale = oneScale;
        RectF dst = new RectF(rect.left / fW, (b - mTextLayer.getHeight() * scale) / fH, (rect.left + mTextLayer.getWidth() * scale) / fW, b / fH);


        b = (int) (b - (textHeightList.get(showPosition - 1)) * oneScale);
        tow = b;
        scale = oneScale;
        RectF dst1 = new RectF(rect.left / fW, (b - mTextLayer.getHeight() * scale) / fH, (rect.left + mTextLayer.getWidth() * scale) / fW, b / fH);
        TextLayer.IFrame iFrame = new TextLayer.IFrame(timelineFrom, timelineTo, new RectF(dst), new RectF(dst1));
        mTextLayer.setFrame(iFrame);
    }

    /**
     * 向上平移两次  显示
     *
     * @param timelineFrom 开始时间
     * @param timelineTo   结束时间
     */
    protected void showTopTranslationTwiceTextLayerShow(float timelineFrom, float timelineTo, float twoScale) {
        int l = 73;
        int b = 462;

        float scale = 1f;
        RectF rect = new RectF(l, b - mTextLayer.getHeight() * scale, l + mTextLayer.getWidth() * scale, b);


        b = tow;
        scale = twoScale;
        RectF dst1 = new RectF(rect.left / fW, (b - mTextLayer.getHeight() * scale) / fH, (rect.left + mTextLayer.getWidth() * scale) / fW, b / fH);
        TextLayer.IFrame iFrame = new TextLayer.IFrame(timelineFrom, timelineTo, new RectF(dst1), new RectF(dst1));
        mTextLayer.setFrame(iFrame);
    }

    /**
     * 竖着缩小  动画
     *
     * @param timelineFrom
     * @param timelineTo
     */
    protected void showVerticalShrinkOnceTextLayerAnim(float timelineFrom, float timelineTo, float newScale) {
        //竖着缩小 1
        int l = 73;
        int b = 462;
        float scale = 1f;
        RectF src = new RectF(l / fW, (b - mTextLayer.getHeight() * scale) / fH, (l + mTextLayer.getWidth() * scale) / fW, b / fH);


        scale = newScale;
        //宽高缩小为0.68f
        l = l + textHeightList.get(showPosition);
        b = 462;

        RectF dst2 = new RectF(l / fW, (b - mTextLayer.getHeight() * scale) / fH, (l + mTextLayer.getWidth() * scale) / fW, b / fH);
        TextLayer.IFrame iFrame = new TextLayer.IFrame(timelineFrom, timelineTo, new RectF(src),
                new RectF(dst2)).setAngle(270, 0, new PointF(src.left, src.bottom));
        mTextLayer.setFrame(iFrame);

    }

    /**
     * //竖着静止
     *
     * @param timelineFrom
     * @param timelineTo
     */
    protected void showVerticalShrinkOnceTextLayerShow(float timelineFrom, float timelineTo, float newScale) {
        //竖着静止1
        int l = 73;
        int b = 462;
        float scale = 1f;
        RectF rectF = new RectF(l / fW, (b - mTextLayer.getHeight() * scale) / fH, (l + mTextLayer.getWidth() * scale) / fW, b / fH);


        l = l + textHeightList.get(showPosition);
        b = 462;
        scale = newScale;
        RectF src = new RectF(l / fW, (b - mTextLayer.getHeight() * scale) / fH, (l + mTextLayer.getWidth() * scale) / fW, b / fH);
        TextLayer.IFrame iFrame = new TextLayer.IFrame(timelineFrom, timelineTo, new RectF(src),
                new RectF(src)).setAngle(270, 0, new PointF(rectF.left, rectF.bottom));
        mTextLayer.setFrame(iFrame);

    }

    /**
     * //左转之后 竖显示
     *
     * @param timelineFrom
     * @param timelineTo
     */
    protected void showVerticalTextLayerShow(float timelineFrom, float timelineTo, float newScale) {
        //翻转之后显示
        int l = 73;
        int b = 462;


        RectF rect = new RectF(l, b - (mTextLayer.getHeight() * newScale), l + mTextLayer.getWidth() * newScale, b);
        RectF rectF = new RectF(rect.left / fW, rect.top / fH, rect.right / fW, rect.bottom / fH);

        TextLayer.IFrame iFrame = new TextLayer.IFrame(timelineFrom, timelineTo, new RectF(rectF), new RectF(rectF)).
                setAngle(270, 0, new PointF(rectF.left, rectF.bottom));
        mTextLayer.setFrame(iFrame);

    }

    /**
     * 右翻转之后当前显示行 动画
     *
     * @param timelineFrom
     * @param timelineTo
     */
    protected void showRightOverturnTextLayerAnim(float timelineFrom, float timelineTo) {

        int l = 73;
        int b = 462;


        float scale = 1f;
        RectF rect = new RectF(l, b - (mTextLayer.getHeight() * scale), l + mTextLayer.getWidth() * scale, b);
        RectF rectF = new RectF(rect.left / fW, rect.top / fH, rect.right / fW, rect.bottom / fH);


        TextLayer.IFrame iFrame = new TextLayer.IFrame(timelineFrom, timelineTo, rectF, new RectF(rectF))
                .setAngle(-90, 90, new PointF(rectF.right, rectF.bottom));
        iFrame.setRotateType(0);
        iFrame.setScale(0, 0, 1, 1);
        mTextLayer.setFrame(iFrame);

    }

    /**
     * 右翻转之后当前显示行 显示
     *
     * @param timelineFrom
     * @param timelineTo
     */
    protected void showRightOverturnTextLayerShow(float timelineFrom, float timelineTo) {
        //右下角顶点坐标
        int l = 73;
        int b = 462;


        float scale = 1f;
        RectF rect = new RectF(l, b - (mTextLayer.getHeight() * scale), l + mTextLayer.getWidth() * scale, b);
        RectF rectF = new RectF(rect.left / fW, rect.top / fH, rect.right / fW, rect.bottom / fH);


        TextLayer.IFrame iFrame = new TextLayer.IFrame(timelineFrom, timelineTo, rectF, new RectF(rectF));

        mTextLayer.setFrame(iFrame);

    }

    /**
     * 左翻转之后当前显示行 显示
     *
     * @param timelineFrom
     * @param timelineTo
     */
    protected void showLeftOverturnTextLayerShow(float timelineFrom, float timelineTo, float newScale) {
        int l = 73;
        int b = 462;


        RectF rect = new RectF(l, b - (mTextLayer.getHeight() * newScale), l + mTextLayer.getWidth() * newScale, b);
        RectF rectF = new RectF(rect.left / fW, rect.top / fH, rect.right / fW, rect.bottom / fH);

        TextLayer.IFrame iFrame = new TextLayer.IFrame(timelineFrom, timelineTo, new RectF(rectF), new RectF(rectF)).
                setAngle(270, 0, new PointF(rectF.left, rectF.bottom));
        mTextLayer.setFrame(iFrame);

    }


    /**
     * 旋转
     */
    protected void initScreenLayer(float lineFrom, float lineTo) {
        TextLayer.IFrame iFrame = new TextLayer.IFrame(lineFrom, lineTo, null, null);
        iFrame.setAngle(0, 90, new PointF(452 / fW, 462 / fH));
        mTextLayer.setScreenFrame(iFrame);
    }

    /**
     * 旋转 向左
     */
    protected void initScreenLayerLeft(float lineFrom, float lineTo) {
        TextLayer.IFrame iFrame = new TextLayer.IFrame(lineFrom, lineTo, null, null);
        iFrame.setAngle(0, -90, new PointF(72 / fW, 462 / fH));
        mTextLayer.setScreenFrame(iFrame);
    }

    /**
     * 旋转2
     */
    protected void initScreenLayer2(float lineFrom, float lineTo) {
        TextLayer.IFrame iFrame = new TextLayer.IFrame(lineFrom, lineTo, null, null);
        iFrame.setAngle(90, 0, new PointF(452 / fW, 462 / fH));
        mTextLayer.setScreenFrame(iFrame);
    }
}
