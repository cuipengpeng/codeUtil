package com.caishi.chaoge.rd.overturn;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;

import com.caishi.chaoge.R;
import com.caishi.chaoge.bean.AEModuleBean.AETextLayerInfo;
import com.caishi.chaoge.bean.LrcBean;
import com.rd.vecore.models.CanvasObject;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 自绘实现自说功能
 */
public class MyDrawTextHandler {
    private List<TextLayer> mImgLayerList = new ArrayList<>();

    public List<LrcBean> mLrcBeanList = new ArrayList<>();
    private List<TextLayer> mTextLayerList = new ArrayList<>();
    private Random random = new Random();
    private List<Integer> mImgIndexList = new ArrayList<>();

    private List<String> mColorList = new ArrayList<>();//文字颜色
    private String mFontFilePath;

    public MyDrawTextHandler(Context context, List<LrcBean> lrcBeanList, String fontFilePath) {
        mFontFilePath = fontFilePath;
        mImgLayerList.clear();
        mImgLayerList.add(new TextLayer(BitmapFactory.decodeResource(context.getResources(), R.mipmap.tuzi_jing)));
        mImgLayerList.add(new TextLayer(BitmapFactory.decodeResource(context.getResources(), R.mipmap.tuzi_n)));
        mImgLayerList.add(new TextLayer(BitmapFactory.decodeResource(context.getResources(), R.mipmap.tuzi_o)));
        mImgLayerList.add(new TextLayer(BitmapFactory.decodeResource(context.getResources(), R.mipmap.tuzi_wenhao)));
        mImgLayerList.add(new TextLayer(BitmapFactory.decodeResource(context.getResources(), R.mipmap.tuzi_x)));
        mImgLayerList.add(new TextLayer(BitmapFactory.decodeResource(context.getResources(), R.mipmap.tuzi_xing)));
        mImgLayerList.add(new TextLayer(BitmapFactory.decodeResource(context.getResources(), R.mipmap.tuzi_y)));
        mImgLayerList.add(new TextLayer(BitmapFactory.decodeResource(context.getResources(), R.mipmap.tuzi_yue)));

        setLrcBeanList(lrcBeanList,fontFilePath);
    }


    public void setLrcBeanList(List<LrcBean> lrcBeanList, String fontFilePath) {
        mFontFilePath = fontFilePath;
        mLrcBeanList.clear();
        mLrcBeanList.addAll(lrcBeanList);
        mTextLayerList.clear();
        mImgIndexList.clear();
        for (int i=0;i<lrcBeanList.size();i++){
            int imgIndex = random.nextInt(8);
            mImgIndexList.add(imgIndex);
        }
    }

    public void setColorList(List<String> colorList) {
        mColorList.clear();
        this.mColorList = colorList;
    }

    /**
     * 自绘
     *
     * @param canvas
     * @param currentProgress 单位：秒
     */
    public void drawText(CanvasObject canvas, float currentProgress) {
        for (int i=0;i<mLrcBeanList.size();i++){
                if(i%5==0){
                    DrawT0.drawImg2(i,canvas,currentProgress, mImgLayerList.get(mImgIndexList.get(i)), mLrcBeanList);
                }else if(i%5==1){
                    DrawT0.drawImg0(i,canvas,currentProgress, mImgLayerList.get(mImgIndexList.get(i)), mLrcBeanList);
                }else if(i%5==2){
                    DrawT0.drawImg1(i,canvas,currentProgress, mImgLayerList.get(mImgIndexList.get(i)), mLrcBeanList);
                }else if(i%5==3){
                    DrawT0.drawImg4(i,canvas,currentProgress, mImgLayerList.get(mImgIndexList.get(i)), mLrcBeanList);
                }else if(i%5==4){
                    DrawT0.drawImg3(i,canvas,currentProgress, mImgLayerList.get(mImgIndexList.get(i)), mLrcBeanList);
                }

                if(i%3==0){
                    DrawT0.drawText0(i,canvas,currentProgress, mFontFilePath, mLrcBeanList);
                }else if(i%3==1){
                    DrawT0.drawText1(i,canvas,currentProgress, mFontFilePath, mLrcBeanList);
                }else if(i%3==2){
                    DrawT0.drawText2(i,canvas,currentProgress, mFontFilePath, mLrcBeanList);
                }
        }
    }
}
