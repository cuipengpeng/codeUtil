package com.pepe.aplayer.opengl;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;
import android.view.WindowManager;

import com.pepe.aplayer.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;



public class BeautyRenderer implements GLSurfaceView.Renderer{

    private  Context context;
    private int textureProgram;
    private int texture;
    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];


    public int mWidth,mHeight;
    private float toneLevel;
    private float beautyLevel;
    private float brightLevel;
    private float texelWidthOffset;
    private float texelHeightOffset;

    private int vPosition;
    private int texCoord;
    private int uMVPMatrix;

    private int paramsLocation;
    private int brightnessLocation;
    private int singleStepOffsetLocation;
    private int texelWidthLocation;
    private int texelHeightLocation;
    private boolean isTakePicture;

    private float[] vertexPositionArray = {
      -1f, 1f,
      1f, 1f,
      -1f,-1f,
      1f, -1f
    };

    private float[] fragmentPositionArray = {
      0f, 0f,
      1f, 0f,
      0f,1f,
      1f, 1f
    };

    private FloatBuffer vertexCoordFloatBuffer;
    private FloatBuffer fragCoordFloatBuffer;

    public BeautyRenderer(Context context) {
        this.context = context;
        texelWidthOffset=texelHeightOffset=2;
        toneLevel = -0.5f; 
        beautyLevel =1.2f; 
        brightLevel =0.47f;
        WindowManager wm = ((Activity)context).getWindowManager();
        mWidth = wm.getDefaultDisplay().getWidth();
        mHeight = wm.getDefaultDisplay().getHeight();
    }



    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        vertexCoordFloatBuffer = (FloatBuffer) ByteBuffer.allocateDirect(vertexPositionArray.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(vertexPositionArray).position(0);
        fragCoordFloatBuffer = (FloatBuffer) ByteBuffer.allocateDirect(fragmentPositionArray.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(fragmentPositionArray).position(0);

        texture = createTexture(R.mipmap.liu);
        textureProgram = ShaderUtils.createProgram(ShaderUtils.loadFromAssetsFile("vertex_beauty.glsl", context.getResources()),
                                                    ShaderUtils.loadFromAssetsFile("fragment_beauty.glsl", context.getResources()));

        vPosition = GLES20.glGetAttribLocation(textureProgram, "vPosition");
        texCoord = GLES20.glGetAttribLocation(textureProgram, "vCoordinate");
        uMVPMatrix = GLES20.glGetUniformLocation(textureProgram, "vMatrix");
        paramsLocation = GLES20.glGetUniformLocation(textureProgram, "params");
        brightnessLocation = GLES20.glGetUniformLocation(textureProgram, "brightness");
        singleStepOffsetLocation = GLES20.glGetUniformLocation(textureProgram, "singleStepOffset");
        texelWidthLocation = GLES20.glGetUniformLocation(textureProgram, "texelWidthOffset");
        texelHeightLocation = GLES20.glGetUniformLocation(textureProgram, "texelHeightOffset");

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width,height);

        Log.d("##################", "onSurfaceChanged--1111");
//        MatrixHelper.perspectiveM(projectionMatrix,0,45,(float)width/(float)height,1f,10f);
        MatrixHelper.perspectiveM(projectionMatrix,0,90,(float)width/(float)height,1f,10f);

        Matrix.setIdentityM(modelMatrix,0);
//        Matrix.translateM(modelMatrix,0,0f,0f,-2.5f);
        Matrix.translateM(modelMatrix,0,0f,0f,-1.5f);
//        Matrix.rotateM(modelMatrix,0,60f,1f,0f,0f);
        Matrix.rotateM(modelMatrix,0,-60f,1f,0f,0f);
        final float[] temp = new float[16];
        Matrix.multiplyMM(temp,0,projectionMatrix,0,modelMatrix,0);
        System.arraycopy(temp,0,projectionMatrix,0,temp.length);

        setTexelSize(width, height);
        mWidth = width;
        mHeight = height;

    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_STENCIL_BUFFER_BIT);
        GLES20.glClearColor(0f,0f,0f, 1f);

//        textureProgram.useProgram();
//        textureProgram.setUniforms(projectionMatrix,texture);
        GLES20.glUseProgram(textureProgram);
        GLES20.glUniformMatrix4fv(uMVPMatrix, 1,false, projectionMatrix, 0);
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glEnableVertexAttribArray(texCoord);
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, vertexCoordFloatBuffer);
        GLES20.glVertexAttribPointer(texCoord, 2, GLES20.GL_FLOAT, false, 0, fragCoordFloatBuffer);

        setParams(beautyLevel, toneLevel);
        setBrightLevel(brightLevel);
        setTexelOffset(texelWidthOffset);


//        picture.bindData(textureProgram);
//        picture.draw();
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);


        // 获取GLSurfaceView的图片并保存
        if (isTakePicture) {
           /* Bitmap bmp = GLBitmapUtils.createBitmapFromGLSurface(0, 0, mWidth,
                    mHeight, gl10);*/
            GLBitmapUtil.saveFrame(mWidth, mHeight);
            isTakePicture = false;
        }

    }

    public void setTexelOffset(float texelOffset) {
        texelWidthOffset=texelHeightOffset=texelOffset;
        setFloat(texelWidthLocation, texelOffset/mWidth);
        setFloat(texelHeightLocation, texelOffset/mHeight);
    }

    public void setToneLevel(float toneLeve) {
        this.toneLevel = toneLeve;
        setParams(beautyLevel, toneLevel);
    }

    public void setBeautyLevel(float beautyLeve) {
        this.beautyLevel = beautyLeve;
        setParams(beautyLevel, toneLevel);
    }

    public void setBrightLevel(float brightLevel) {
        this.brightLevel = brightLevel;
        setFloat(brightnessLocation, 0.6f * (-0.5f + brightLevel));
    }

    public void setParams(float beauty, float tone) {
        this.beautyLevel=beauty;
        this.toneLevel = tone;
        float[] vector = new float[4];
        vector[0] = 1.0f - 0.6f * beauty;
        vector[1] = 1.0f - 0.3f * beauty;
        vector[2] = 0.1f + 0.3f * tone;
        vector[3] = 0.1f + 0.3f * tone;
        setFloatVec4(paramsLocation, vector);
    }

    private void setTexelSize(final float w, final float h) {
        setFloatVec2(singleStepOffsetLocation, new float[] {2.0f / w, 2.0f / h});
    }


   protected void setFloat(final int location, final float floatValue) {
       GLES20.glUniform1f(location, floatValue);
   }
    protected void setFloatVec2(final int location, final float[] arrayValue) {
        GLES20.glUniform2fv(location, 1, FloatBuffer.wrap(arrayValue));
    }

    protected void setFloatVec3(final int location, final float[] arrayValue) {
        GLES20.glUniform3fv(location, 1, FloatBuffer.wrap(arrayValue));
    }

    protected void setFloatVec4(final int location, final float[] arrayValue) {
        GLES20.glUniform4fv(location, 1, FloatBuffer.wrap(arrayValue));
    }

    public void saveImage(){
        isTakePicture =true;
    }

    private int createTexture(int resid){
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resid);
        int[] texture=new int[1];
        if(bitmap!=null&&!bitmap.isRecycled()){
            //生成纹理
            GLES20.glGenTextures(1,texture,0);
            //生成纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
            //根据以上指定的参数，生成一个2D纹理
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            return texture[0];
        }
        return 0;
    }
}
