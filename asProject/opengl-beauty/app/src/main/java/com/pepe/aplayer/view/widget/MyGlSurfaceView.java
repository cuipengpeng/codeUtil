package com.pepe.aplayer.view.widget;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;

import com.pepe.aplayer.opengl.ShaderUtils;
import com.pepe.aplayer.opengl.TextureUtil;
import com.pepe.aplayer.util.LogUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGlSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private final float[] vertPositionArray = {
            -1,1,
            1,1,
            -1,-1,
            1,-1
    };
    private final float[] fragPositionArray = {
            0,1,
            1,1,
            0,0,
            1,0
    };
    private static final short[] ORDERS = {
            0, 1, 2, // 左上角三角形
            1, 2, 3  // 右下角三角形
    };

//    //反相
//    private static float[] COLOR_MATRIX = {
//        -1.0f, 0.0f, 0.0f, 1.0f,
//        0.0f, -1.0f, 0.0f, 1.0f,
//        0.0f, 0.0f, -1.0f, 1.0f,
//        0.0f, 0.0f, 0.0f, 1.0f
//    };

//    // 去色
//    private static float[] COLOR_MATRIX = {
//        0.299f, 0.587f, 0.114f, 0.0f,
//        0.299f, 0.587f, 0.114f, 0.0f,
//        0.299f, 0.587f, 0.114f, 0.0f,
//        0.0f, 0.0f, 0.0f, 1.0f,
//    };

    //    // 怀旧
    private static float[] COLOR_MATRIX = {
            0.393f, 0.769f, 0.189f, 0.0f,
            0.349f, 0.686f, 0.168f, 0.0f,
            0.272f, 0.534f, 0.131f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
    };

    //原图
//    private static float[] COLOR_MATRIX = {
//        1.0f, 0.0f, 0.0f, 0.0f,
//        0.0f, 1.0f, 0.0f, 0.0f,
//        0.0f, 0.0f, 1.0f, 0.0f,
//        0.0f, 0.0f, 0.0f, 1.0f
//    };





    private ShortBuffer mOrderShortBuffer;
    private int mColorMatrixId;

    private FloatBuffer vertFloatBuffer;
    private FloatBuffer fragFloatBuffer;
    private int aVertPosition;
    private int aTexPosition;
    private int uMVPMatrix;
    private int uTexMatrix;
    private int mTextureId;
    private float[] modelMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private float[] mvpMatrix = new float[16];
    private float[] textureMatrix = new float[16];

    private int mProgram;

    private boolean frameAvalible = false;
    public SurfaceTexture surfaceTexture;
//    public Surface surface;

    public MyGlSurfaceView(Context context) {
        this(context,null);
    }

    public MyGlSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
//        setEGLConfigChooser(8,8,8,8,16,8);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        surfaceTexture = new SurfaceTexture(false);
        surfaceTexture.setOnFrameAvailableListener(this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        vertFloatBuffer = (FloatBuffer) ByteBuffer.allocateDirect(vertPositionArray.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(vertPositionArray).position(0);
        fragFloatBuffer = (FloatBuffer) ByteBuffer.allocateDirect(fragPositionArray.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(fragPositionArray).position(0);
        mOrderShortBuffer = (ShortBuffer) ByteBuffer.allocateDirect(ORDERS.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer().put(ORDERS).position(0);

        mProgram = ShaderUtils.createProgram(ShaderUtils.loadFromAssetsFile("vetext_sharder.glsl", getResources()),
                                            ShaderUtils.loadFromAssetsFile("fragment_sharder.glsl", getResources()));
        aVertPosition = GLES20.glGetAttribLocation(mProgram, "aVertPosition");
        aTexPosition = GLES20.glGetAttribLocation(mProgram, "aTexPosition");
        uMVPMatrix = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        uTexMatrix = GLES20.glGetUniformLocation(mProgram, "uTexMatrix");
        mColorMatrixId = GLES20.glGetUniformLocation(mProgram, "uColorMatrix");

        mTextureId = TextureUtil.initTextureOES();
        surfaceTexture.attachToGLContext(mTextureId);
        LogUtil.printLog("1111111");
//        surface = new Surface(surfaceTexture);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //gl_Position = aVertPosition;
//        gl_Position = uMVPMatrix * aVertPosition;
        onSurfacePreparedCallBack.onSurfacePrepared();
        GLES20.glViewport(0, 0, width,height);

        Matrix.setIdentityM(modelMatrix,0);
//        Matrix.translateM(modelMatrix,0,0f,0f,-1.5f);
//        Matrix.rotateM(modelMatrix,0,-45f,0f,0f,1f);
//        Matrix.scaleM(modelMatrix,0,1.1f,2.1f,0f);

        //计算宽高比
        float ratio=(float)width/height;
        LogUtil.printLog("width="+width+"--height="+height+"--ratio="+ratio);
        //设置透视投影
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 6f, 1000);
//        Matrix.frustumM(projectionMatrix, 0, -1, 1, -1, 1, 6f, 1000);
//        MatrixHelper.perspectiveM(projectionMatrix,0,25 ,(float)width/(float)height,1f,10f);
        //设置相机位置
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 6.0001f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        //计算变换矩阵
        final float[] tempMatrix = new float[16];
        Matrix.multiplyMM(tempMatrix,0,viewMatrix,0,modelMatrix,0);
        System.arraycopy(tempMatrix,0,viewMatrix,0,tempMatrix.length);
        Matrix.multiplyMM(tempMatrix,0, projectionMatrix,0,viewMatrix,0);
        System.arraycopy(tempMatrix,0,projectionMatrix,0,tempMatrix.length);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearColor(1,0,0,0.5f);

        if(frameAvalible){
            synchronized (this){
                frameAvalible = false;
                surfaceTexture.updateTexImage();
                surfaceTexture.getTransformMatrix(textureMatrix);
            }
        }

        GLES20.glUseProgram(mProgram);

        GLES20.glEnableVertexAttribArray(aVertPosition);
        GLES20.glVertexAttribPointer(aVertPosition,2,GLES20.GL_FLOAT, false, 0, vertFloatBuffer);
        GLES20.glEnableVertexAttribArray(aTexPosition);
        GLES20.glVertexAttribPointer(aTexPosition,2,GLES20.GL_FLOAT, false, 0, fragFloatBuffer);

        GLES20.glUniformMatrix4fv(uMVPMatrix,1,false, projectionMatrix, 0);
        GLES20.glUniformMatrix4fv(uTexMatrix,1,false, textureMatrix, 0);
        GLES20.glUniformMatrix4fv(mColorMatrixId, 1, true, COLOR_MATRIX, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
//        GLES20.glDrawElements(GLES20.GL_TRIANGLES, ORDERS.length, GLES20.GL_UNSIGNED_SHORT, mOrderShortBuffer);
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        synchronized (this){
            frameAvalible = true;
        }
        requestRender();
    }

    private OnSurfacePreparedCallBack onSurfacePreparedCallBack;
    public void setOnSurfacePrepared(OnSurfacePreparedCallBack onSurfacePrepared){
        onSurfacePreparedCallBack = onSurfacePrepared;
    }
    public interface OnSurfacePreparedCallBack{
        void onSurfacePrepared();
    }
}
