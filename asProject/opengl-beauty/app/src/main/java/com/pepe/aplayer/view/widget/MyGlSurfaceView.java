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

        mProgram = ShaderUtils.createProgram(getResources(),"vetext_sharder.glsl","fragment_sharder.glsl");
        //获取shader脚本中的属性信息
//        参数一：program指定要查询的程序对象。
//        参数二：shader脚本中属性变量的名称
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
        //GL_COLOR_BUFFER_BIT表示颜色缓冲区、GL_DEPTH_BUFFER_BIT表示深度缓冲区，GL_STENCIL_BUFFER_BIT表示模板缓冲区
        //二维渲染只需清空颜色缓冲COLOR_BUFFER，三维渲染则需清空颜色缓冲COLOR_BUFFER和深度缓冲DEPTH_BUFFER
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_STENCIL_BUFFER_BIT);
        GLES20.glClearColor(1,0,0,0.5f);
//        GLES20.glViewport(0, 0, width,height);

        if(frameAvalible){
            synchronized (this){
                frameAvalible = false;
                surfaceTexture.updateTexImage();
                surfaceTexture.getTransformMatrix(textureMatrix);
            }
        }

        GLES20.glUseProgram(mProgram);
//        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
//        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture);

        //启用顶点属性数组
        GLES20.glEnableVertexAttribArray(aVertPosition);
        GLES20.glEnableVertexAttribArray(aTexPosition);
//        参数一：顶点着色器位置属性（即"position"）
//        参数二：每一个顶点信息由几个值组成，这个值必须位1，2，3或4
//        参数三：顶点信息的数据类型
//        参数四：GL_FALSE表示不要将数据类型标准化
//        参数五：数组中每个元素之间的间隔步长
//        参数六：数组的首地址
        //为顶点属性赋值
        GLES20.glVertexAttribPointer(aVertPosition,2,GLES20.GL_FLOAT, false, 0, vertFloatBuffer);
        GLES20.glVertexAttribPointer(aTexPosition,2,GLES20.GL_FLOAT, false, 0, fragFloatBuffer);

        GLES20.glUniformMatrix4fv(uMVPMatrix,1,false, projectionMatrix, 0);
        GLES20.glUniformMatrix4fv(uTexMatrix,1,false, textureMatrix, 0);
        GLES20.glUniformMatrix4fv(mColorMatrixId, 1, true, COLOR_MATRIX, 0);

//        参数一：GL_TRIANGLES表示三角形
//        参数二：0表示数组第一个值的位置
//        参数三：表示数组长度
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
//        GLES20.glDrawElements(GLES20.GL_TRIANGLES, ORDERS.length, GLES20.GL_UNSIGNED_SHORT, mOrderShortBuffer);

//        渲染完毕，关闭顶点属性数组
//        GLES20.glDisableVertexAttribArray(aVertPosition);
//        GLES20.glDisableVertexAttribArray(aTexPosition);
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
