package com.pepe.aplayer.opengl;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

public class TextureUtil {

    public static int createFrameBuffer(int previewWidth, int previewHeight){
        int textureId = createTexture2D(previewWidth, previewHeight);
        int frameBuffer = createFrameBuffer(textureId, previewWidth, previewHeight);
       return frameBuffer;
    }

    public static int createFrameBuffer(int textureId, int previewWidth, int previewHeight){
        if(textureId==-1){
            textureId = createTexture2D(previewWidth, previewHeight);
        }

        int[] frameBuffer = new int[1];
        GLES20.glGenFramebuffers(1, frameBuffer, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textureId, 0);
        return frameBuffer[0];
    }

    public static int createOesTextureId(){
        int textureId = createTextureId(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,0, 0);
        return textureId;
    }

    public static int createTexture2D(int previewWidth, int previewHeight){
        int textureId = createTextureId(GLES20.GL_TEXTURE_2D,previewWidth, previewHeight);
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
//        GLES20.glTexParameterf(textureType, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
//        GLES20.glTexParameterf(textureType, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        GLES20.glBindTexture(textureType, 0);
        return tex[0];
    }

    public static int createBitmapTextureId(Bitmap bitmap){
        if(bitmap!=null&&!bitmap.isRecycled()){
            int[] texture=new int[1];
            //生成纹理
            GLES20.glGenTextures(1,texture,0);
            //绑定纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
//          GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
//          GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle();
            return texture[0];
        }
        return 0;
    }

    public static void changeTexture(int textureId, Bitmap bitmap) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
    }

    public static Bitmap makeTextBitmap(String text, int color, int width, int height) {
        if (null == text) {
            throw new IllegalArgumentException();
        }
        TextPaint mTextPaint = new TextPaint();
        String familyName = "";
        mTextPaint.setAntiAlias(true);
        Typeface font = Typeface.create(familyName, Typeface.BOLD);
        mTextPaint.setTypeface(font);
        mTextPaint.setAlpha(255);
        mTextPaint.setTextSize(24);
        mTextPaint.setColor(color);
        Bitmap mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(mBitmap);
        mCanvas.drawBitmap(mBitmap, 0, 0, mTextPaint);
        StaticLayout sl = new StaticLayout(text, mTextPaint, mBitmap.getWidth() - 3, Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        mCanvas.translate(0, 0);
        sl.draw(mCanvas);
        return mBitmap;
    }

    public static Bitmap makeBGTextBitmap(String text, int color, int width, int height, Context mcontext, int resourceid, boolean isright) {
        if (null == text) {
            throw new IllegalArgumentException();
        }
        TextPaint mTextPaint = new TextPaint();
        String familyName = "";
        mTextPaint.setAntiAlias(true);
        Typeface font = Typeface.create(familyName, Typeface.BOLD);
        mTextPaint.setTypeface(font);
        mTextPaint.setAlpha(255);
        mTextPaint.setTextSize(34);
        mTextPaint.setColor(color);
        Resources res = mcontext.getResources();

        Bitmap bmp = BitmapFactory.decodeResource(res, resourceid);
        Bitmap mBitmap = ThumbnailUtils.extractThumbnail(bmp, width + 300, height + 30);
        Canvas mCanvas = new Canvas(mBitmap);
        mCanvas.drawBitmap(mBitmap, 0, 0, mTextPaint);

        StaticLayout sl = new StaticLayout(text, mTextPaint, mBitmap.getWidth() - 10, Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        if (isright) {
            mCanvas.translate(50, 0);
        } else {
            mCanvas.translate(0, 0);
        }
        sl.draw(mCanvas);
        if (!bmp.isRecycled()) {
            bmp.recycle();
        }
        return mBitmap;
    }

    public static Bitmap getTextBitmap(String text, int color, int width, int height) {

        if (null == text) {
            throw new IllegalArgumentException();
        }

        TextPaint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        Typeface font = Typeface.defaultFromStyle(Typeface.NORMAL);
        textPaint.setTypeface(font);
        textPaint.setAlpha(255);
        textPaint.setTextSize(24);
        textPaint.setColor(color);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bitmap, 0, 0, textPaint);

        StaticLayout sl = new StaticLayout(text, textPaint, bitmap.getWidth() - 3, Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        canvas.translate(0, 0);
        sl.draw(canvas);

        return bitmap;
    }

    public static Bitmap makeTextBitmap(String text, int textSize, int color, int bgColor, int width, int height) {

        if (null == text) {
            throw new IllegalArgumentException();
        }

        TextPaint mTextPaint = new TextPaint();
        String familyName = "";
        mTextPaint.setAntiAlias(true);
        Typeface font = Typeface.create(familyName, Typeface.BOLD);
        mTextPaint.setTypeface(font);
        mTextPaint.setAlpha(255);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(color);

        Bitmap mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(bgColor);
        mCanvas.drawBitmap(mBitmap, 0, 0, mTextPaint);

        StaticLayout sl = new StaticLayout(text, mTextPaint, mBitmap.getWidth() - 3, Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        mCanvas.translate(0, 0);
        sl.draw(mCanvas);
        return mBitmap;
    }

    public static int createTexture3D() {
        final int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        if (textures[0] == 0) {
            return 0;
        }
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);

        return textures[0];
    }


    public static final int NO_TEXTURE = -1;

    public static int loadTexture(final Bitmap img, final int usedTexId) {
        return loadTexture(img, usedTexId, true);
    }

    public static int loadTexture(final Bitmap img, final int usedTexId, final boolean recycle) {
        int textures[] = new int[1];
        if (usedTexId == NO_TEXTURE) {
            GLES20.glGenTextures(1, textures, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            //TODO 将图片变成一个纹理id
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, img, 0);
        } else {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, usedTexId);
            GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, img);
            textures[0] = usedTexId;
        }
        if (recycle) {
            img.recycle();
        }
        return textures[0];
    }

}
