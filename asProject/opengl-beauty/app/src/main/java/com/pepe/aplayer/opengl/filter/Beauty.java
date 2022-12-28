/*
 *
 * Beauty.java
 * 
 * Created by Wuwang on 2016/11/18
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.pepe.aplayer.opengl.filter;

import android.content.res.Resources;
import android.opengl.GLES20;

/**
 * Description:
 */
public class Beauty extends AFilter {

    private int gHaaCoef;
    private int gHmixCoef;
    private int gHiternum;
    private int gHWidth;
    private int gHHeight;

    private float aaCoef;
    private float mixCoef;
    private int iternum;

    private int mWidth=720;
    private int mHeight=1280;


    public Beauty(Resources res) {
        super(res);
        setFlag(0);
        vertexFileName = "beauty.vert";
        fragmentFileName = "beauty.frag";
    }

    @Override
    protected void initPropertyLocation() {
        gHaaCoef= GLES20.glGetUniformLocation(programId,"aaCoef");
        gHmixCoef= GLES20.glGetUniformLocation(programId,"mixCoef");
        gHiternum= GLES20.glGetUniformLocation(programId,"iternum");
        gHWidth= GLES20.glGetUniformLocation(programId,"mWidth");
        gHHeight= GLES20.glGetUniformLocation(programId,"mHeight");
    }

    @Override
    protected void onCreate() {

    }

    @Override
    public void setFlag(int flag) {
        super.setFlag(flag);
        switch (flag){
            case 1:
                a(1,0.19f,0.54f);
                break;
            case 2:
                a(2,0.29f,0.54f);
                break;
            case 3:
                a(3,0.17f,0.39f);
                break;
            case 4:
                a(3,0.25f,0.54f);
                break;
            case 5:
                a(4,0.13f,0.54f);
                break;
            case 6:
                a(4,0.19f,0.69f);
                break;
            default:
                a(0,0f,0f);
                break;
        }
    }

    private void a(int a,float b,float c){
        this.iternum=a;
        this.aaCoef=b;
        this.mixCoef=c;
    }

    @Override
    protected void onSizeChanged(int width, int height) {
        this.mWidth=width;
        this.mHeight=height;
    }

    @Override
    protected void onSetExpandData() {
        super.onSetExpandData();
        GLES20.glUniform1i(gHWidth,mWidth);
        GLES20.glUniform1i(gHHeight,mHeight);
        GLES20.glUniform1f(gHaaCoef,aaCoef);
        GLES20.glUniform1f(gHmixCoef,mixCoef);
        GLES20.glUniform1i(gHiternum,iternum);
    }
}
