package com.pepe.aplayer.opengl.filter;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

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
        int textureId = createTextureId(GLES20.GL_TEXTURE_2D,previewWidth, previewHeight);

        int[] frameBuffer = new int[1];
        GLES20.glGenFramebuffers(1, frameBuffer, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textureId, 0);
        return new Frame(frameBuffer[0], textureId, previewWidth, previewHeight);
    }

    public static int createOesTextureId(){
        int textureId = createTextureId(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,0, 0);
        return textureId;
    }

    private static int createTextureId(final int textureType, int previewWidth, int previewHeight){
        int[] tex = new int[1];
        GLES20.glGenTextures(1, tex, 0);
        GLES20.glBindTexture(textureType, tex[0]);
        if(textureType==GLES20.GL_TEXTURE_2D){
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, previewWidth, previewHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        }
        GLES20.glTexParameterf(textureType, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        GLES20.glTexParameterf(textureType, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(textureType, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(textureType, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glBindTexture(textureType, 0);
        return tex[0];
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
