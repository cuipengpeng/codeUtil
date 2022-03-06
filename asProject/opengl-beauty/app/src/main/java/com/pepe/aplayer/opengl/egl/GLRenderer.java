package com.pepe.aplayer.opengl.egl;

import android.content.Context;
import android.opengl.GLES20;

import com.pepe.aplayer.R;
import com.pepe.aplayer.opengl.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GLRenderer {
    private Context context;

    private FloatBuffer vertexBuffer;
    private FloatBuffer textureVertexBuffer;

    private int programId;


    private int aPositionHandle;
    private int uTextureSamplerHandle;
    private int aTextureCoordHandle;
    private int mColorMatrixId;


        //反相
    private static float[] COLOR_MATRIX_001 = {
        -1.0f, 0.0f, 0.0f, 1.0f,
        0.0f, -1.0f, 0.0f, 1.0f,
        0.0f, 0.0f, -1.0f, 1.0f,
        0.0f, 0.0f, 0.0f, 1.0f
    };

    // 去色
    private static float[] COLOR_MATRIX_002 = {
        0.299f, 0.587f, 0.114f, 0.0f,
        0.299f, 0.587f, 0.114f, 0.0f,
        0.299f, 0.587f, 0.114f, 0.0f,
        0.0f, 0.0f, 0.0f, 1.0f,
    };

    //    // 怀旧
    private static float[] COLOR_MATRIX_003 = {
            0.393f, 0.769f, 0.189f, 0.0f,
            0.349f, 0.686f, 0.168f, 0.0f,
            0.272f, 0.534f, 0.131f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
    };

    //原图
    private static float[] COLOR_MATRIX_004 = {
        1.0f, 0.0f, 0.0f, 0.0f,
        0.0f, 1.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 1.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 1.0f
    };
    private int colorId = -1;
    public void setColorID(int cId){
        colorId= cId;
    }

    public GLRenderer(Context context){
        this.context = context;
        final float[] vertexData = {
                1f, -1f, 0f,
                -1f, -1f, 0f,
                1f, 1f, 0f,
                -1f, 1f, 0f
        };


        final float[] textureVertexData = {
                1f, 0f,
                0f, 0f,
                1f, 1f,
                0f, 1f
        };
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);

        textureVertexBuffer = ByteBuffer.allocateDirect(textureVertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureVertexData);
        textureVertexBuffer.position(0);
    }

    public void initShader(){
        programId = ShaderUtils.createProgram(context.getResources(),"bitmapVertexShader.glsl", "bitmapFragmentShader.glsl");
        aPositionHandle = GLES20.glGetAttribLocation(programId, "aPosition");
        uTextureSamplerHandle = GLES20.glGetUniformLocation(programId, "sTexture");
        aTextureCoordHandle = GLES20.glGetAttribLocation(programId, "aTexCoord");
        mColorMatrixId = GLES20.glGetUniformLocation(programId, "uColorMatrix");

        GLES20.glUseProgram(programId);

        vertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(aPositionHandle);
        GLES20.glVertexAttribPointer(aPositionHandle, 3, GLES20.GL_FLOAT, false,12, vertexBuffer);

        textureVertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(aTextureCoordHandle);
        GLES20.glVertexAttribPointer(aTextureCoordHandle, 2, GLES20.GL_FLOAT, false, 8, textureVertexBuffer);

        if(colorId==1){
            GLES20.glUniformMatrix4fv(mColorMatrixId, 1, true, COLOR_MATRIX_001, 0);
        }else if(colorId==2){
            GLES20.glUniformMatrix4fv(mColorMatrixId, 1, true, COLOR_MATRIX_002, 0);
        }else if(colorId==3){
            GLES20.glUniformMatrix4fv(mColorMatrixId, 1, true, COLOR_MATRIX_003, 0);
        }else {
            GLES20.glUniformMatrix4fv(mColorMatrixId, 1, true, COLOR_MATRIX_004, 0);
        }
    }
    public void drawFrame(){
        GLES20.glUseProgram(programId);
        GLES20.glUniform1i(uTextureSamplerHandle, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }
}
