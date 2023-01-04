package com.pepe.aplayer.opengl.filter;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.pepe.aplayer.opengl.TextureUtil;

import javax.microedition.khronos.opengles.GL10;

public class Frame {

    private int frameBuffer;
    private int textureId;
    private int previewWidth;
    private int previewHeight;

    public Frame(int frameBuffer, int textureId, int previewWidth, int previewHeight){
        this.frameBuffer=frameBuffer;
        this.textureId=textureId;
        this.previewWidth=previewWidth;
        this.previewHeight=previewHeight;
    }

    public static Frame createOffScreemFrame(int previewWidth, int previewHeight){
        int textureId = TextureUtil.createTexture2D(previewWidth, previewHeight);
        int frameBuffer = TextureUtil.createFrameBuffer(textureId, previewWidth, previewHeight);
        return new Frame(frameBuffer, textureId, previewWidth, previewHeight);
    }


    public int getFrameBuffer() {
        return frameBuffer;
    }

    public void setFrameBuffer(int frameBuffer) {
        this.frameBuffer = frameBuffer;
    }

    public int getTextureId() {
        return textureId;
    }

    public void setTextureId(int textureId) {
        this.textureId = textureId;
    }

    public int getPreviewWidth() {
        return previewWidth;
    }

    public void setPreviewWidth(int previewWidth) {
        this.previewWidth = previewWidth;
    }

    public int getPreviewHeight() {
        return previewHeight;
    }

    public void setPreviewHeight(int previewHeight) {
        this.previewHeight = previewHeight;
    }
}
