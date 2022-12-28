package com.pepe.aplayer.view.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Size;

import com.pepe.aplayer.opengl.filter.AFilter;
import com.pepe.aplayer.opengl.filter.CamerPreviewFilter;
import com.pepe.aplayer.opengl.filter.Frame;
import com.pepe.aplayer.opengl.filter.RenderScreenFilter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGlSurfaceView_smooth extends GLSurfaceView implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

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


    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
    public volatile boolean mCapturing = false;

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

    public MyGlSurfaceView_smooth(Context context) {
        this(context,null);
    }

    public MyGlSurfaceView_smooth(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
//        setEGLConfigChooser(8,8,8,8,16,8);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        surfaceTexture = new SurfaceTexture(false);
        surfaceTexture.setOnFrameAvailableListener(this);
        previewFilter = new CamerPreviewFilter(getContext());
        screenFilter = new RenderScreenFilter(getContext());
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//        whiteLevel = 50;
//        smoothLevel = 50;
//        updateFilter();
//        previewFilter = new CamerPreviewFilter(getContext());
//        screenFilter = new RenderScreenFilter(getContext());
        surfaceTexture.attachToGLContext(previewFilter.getOesTextureId());

//        vertFloatBuffer = (FloatBuffer) ByteBuffer.allocateDirect(vertPositionArray.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(vertPositionArray).position(0);
//        fragFloatBuffer = (FloatBuffer) ByteBuffer.allocateDirect(fragPositionArray.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(fragPositionArray).position(0);
//        mOrderShortBuffer = (ShortBuffer) ByteBuffer.allocateDirect(ORDERS.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer().put(ORDERS).position(0);
//
//        mProgram = ShaderUtils.createProgram(getResources(),"vetext_sharder.glsl","fragment_sharder.glsl");
//        //获取shader脚本中的属性信息
////        参数一：program指定要查询的程序对象。
////        参数二：shader脚本中属性变量的名称
//        aVertPosition = GLES20.glGetAttribLocation(mProgram, "aVertPosition");
//        aTexPosition = GLES20.glGetAttribLocation(mProgram, "aTexPosition");
//        uMVPMatrix = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
//        uTexMatrix = GLES20.glGetUniformLocation(mProgram, "uTexMatrix");
//        mColorMatrixId = GLES20.glGetUniformLocation(mProgram, "uColorMatrix");
//
//        mTextureId = TextureUtil.initTextureOES();
//        surfaceTexture.attachToGLContext(mTextureId);
//        LogUtil.printLog("1111111");
//        surface = new Surface(surfaceTexture);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //gl_Position = aVertPosition;
////        gl_Position = uMVPMatrix * aVertPosition;
//        onSurfacePreparedCallBack.onSurfacePrepared();
////        告诉OpenGL应把渲染之后的图形绘制在窗体的哪个区域。
////        参数X，Y指定了视见区域的左下角在窗口中的位置，一般情况下为（0，0），Width和Height指定了视见区域的宽度和高度。
//        GLES20.glViewport(0, 0, width,height);
//
//        //设置模型矩阵
//        Matrix.setIdentityM(modelMatrix,0);
////        Matrix.translateM(modelMatrix,0,0f,0f,-1.5f);
////        Matrix.rotateM(modelMatrix,0,-45f,0f,0f,1f);
////        Matrix.scaleM(modelMatrix,0,1.1f,2.1f,0f);
//
//        //计算宽高比
//        float ratio=(float)width/height;
//        LogUtil.printLog("width="+width+"--height="+height+"--ratio="+ratio);
//        //设置透视投影
//        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 6f, 1000);
////        Matrix.frustumM(projectionMatrix, 0, -1, 1, -1, 1, 6f, 1000);
////        MatrixHelper.perspectiveM(projectionMatrix,0,25 ,(float)width/(float)height,1f,10f);
//        //设置相机位置
//        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 6.0001f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
//
//        //计算变换矩阵
//        final float[] tempMatrix = new float[16];
//        Matrix.multiplyMM(tempMatrix,0,viewMatrix,0,modelMatrix,0);
//        System.arraycopy(tempMatrix,0,viewMatrix,0,tempMatrix.length);
//        Matrix.multiplyMM(tempMatrix,0, projectionMatrix,0,viewMatrix,0);
//        System.arraycopy(tempMatrix,0,projectionMatrix,0,tempMatrix.length);
    }

    public CamerPreviewFilter previewFilter;
    RenderScreenFilter screenFilter;
    Frame oesFrame;
    private AFilter mCurrentFilter;
    private AFilter mOldFilter;

    public Size mCapturePreviewSize;


    public void setmCurrentFilter(final AFilter filter) {
         mOldFilter = mCurrentFilter;
        this.mCurrentFilter = filter;
        requestRender();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //GL_COLOR_BUFFER_BIT表示颜色缓冲区、GL_DEPTH_BUFFER_BIT表示深度缓冲区，GL_STENCIL_BUFFER_BIT表示模板缓冲区
        //二维渲染只需清空颜色缓冲COLOR_BUFFER，三维渲染则需清空颜色缓冲COLOR_BUFFER和深度缓冲DEPTH_BUFFER
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_STENCIL_BUFFER_BIT);
        GLES20.glClearColor(1,0,0,0.5f);
//        GLES20.glViewport(0, 0, width,height);

//        final IFilter oldFilter = mCurrentFilter;
//        AGLRenderer.this.filter = mCurrentFilter;
        if (mOldFilter != null) {
            mOldFilter.destroy();
        }

        if(oesFrame==null){
            oesFrame = new Frame(0, previewFilter.getOesTextureId(), mCapturePreviewSize.getWidth(), mCapturePreviewSize.getHeight());
        }

        if(frameAvalible){
            synchronized (this){
                frameAvalible = false;
                surfaceTexture.updateTexImage();
                surfaceTexture.getTransformMatrix(textureMatrix);
            }
        }

        Frame frame = previewFilter.draw(oesFrame);

//        if (filter != null && !disable) {
        if (mCurrentFilter != null) {
            frame = mCurrentFilter.draw(frame);
        }

//        if (needCapture && !isCapturing) {
//            isCapturing = true;
//            capture(frame);
//        }

//        screenFilter.setVerticesCoordination(Transform.adjustVetices(frame.getTextureWidth(), frame.getTextureHeight(), outWidth, outHeight));
        screenFilter.draw(frame);

//        GLES20.glUseProgram(mProgram);
////        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
////        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
////        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture);
//
//        //启用顶点属性数组
//        GLES20.glEnableVertexAttribArray(aVertPosition);
//        GLES20.glEnableVertexAttribArray(aTexPosition);
////        参数一：顶点着色器位置属性（即"position"）
////        参数二：每一个顶点信息由几个值组成，定点的数据siae，这个值必须位1，2，3或4
////        参数三：顶点信息的数据类型
////        参数四：GL_FALSE表示不要将数据类型标准化
////        参数五：数组中每个元素之间的间隔步长
////        参数六：数组的首地址
////        shader属性赋值时，只有attribute属性需要告诉opengl其解析结构及如何解析，因为attribute vec4传递可少于4个float的数组， 但必须告诉顶点属性指针如何解析。其他类型属性如Uniform等直接赋值即可。
////        attribute属性赋值时Stride步长参数: 要么为0，要么=vertexPerSizeCount*4Float
////                uniform修饰的和mat4类型的数据必须传递指定相等长度的数组
//        //为顶点属性赋值
//        GLES20.glVertexAttribPointer(aVertPosition,2,GLES20.GL_FLOAT, false, 0, vertFloatBuffer);
//        GLES20.glVertexAttribPointer(aTexPosition,2,GLES20.GL_FLOAT, false, 0, fragFloatBuffer);
//
////        参数一location : uniform的位置。
////        参数二count : 需要加载数据的数组元素的数量或者需要修改的矩阵的数量。
////        参数三transpose : 指明矩阵是列优先(column major)矩阵（GL_FALSE）还是行优先(row major)矩阵（GL_TRUE）。
////        参数四value : 指向由count个元素的数组的指针。
//        // 将最终MVP变换矩阵传入shader程序
//        GLES20.glUniformMatrix4fv(uMVPMatrix,1,false, projectionMatrix, 0);
//        // 将最终纹理矩阵传入shader程序
//        GLES20.glUniformMatrix4fv(uTexMatrix,1,false, textureMatrix, 0);
//        GLES20.glUniformMatrix4fv(mColorMatrixId, 1, true, COLOR_MATRIX, 0);
//
////        参数一：GL_TRIANGLES表示三角形
////        参数二：0表示数组第一个值的位置
////        参数三：表示数组长度
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
////        GLES20.glDrawElements(GLES20.GL_TRIANGLES, ORDERS.length, GLES20.GL_UNSIGNED_SHORT, mOrderShortBuffer);
//
////        渲染完毕，关闭顶点属性数组
////        GLES20.glDisableVertexAttribArray(aVertPosition);
////        GLES20.glDisableVertexAttribArray(aTexPosition);
//
//        if(mCapturing){
//            mCapturing = false;
//            capture();
//        }
    }

    private void capture() {
        ByteBuffer captureBuffer = ByteBuffer.allocate(1920 * 1080 * 4);
        captureBuffer.order(ByteOrder.nativeOrder());
        captureBuffer.rewind();
//        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frame.getFrameBuffer());
        GLES20.glReadPixels(0, 0, 1080, 1920, GLES20.GL_RGBA, GL10.GL_UNSIGNED_BYTE, captureBuffer);
        final Bitmap bmp = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888);
        bmp.copyPixelsFromBuffer(captureBuffer);
        android.graphics.Matrix matrix = new android.graphics.Matrix();
        //镜像竖直翻转
        matrix.postScale(1, -1);
        final Bitmap bitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String imgName = System.currentTimeMillis() + ".jpg";
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/" + imgName;
//                    File f = new File(path);

                    File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), dateFormat.format(new Date())+"-"+System.currentTimeMillis() + ".jpg");
                    if (f.exists()) {
                        boolean exist = f.delete();
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(f);
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    bitmap.recycle();
//                    ContentValues currentVideoValues = new ContentValues();
//                    currentVideoValues.put(MediaStore.Images.Media.TITLE, title);
//                    currentVideoValues.put(MediaStore.Images.Media.DISPLAY_NAME, title);
//                    currentVideoValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
//                    currentVideoValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
//                    currentVideoValues.put(MediaStore.Images.Media.DATA, path);
//                    currentVideoValues.put(MediaStore.Images.Media.DESCRIPTION, "com.smzh.aglframework");
//                    currentVideoValues.put(MediaStore.Images.Media.SIZE, f.length());
//                    getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, currentVideoValues);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
