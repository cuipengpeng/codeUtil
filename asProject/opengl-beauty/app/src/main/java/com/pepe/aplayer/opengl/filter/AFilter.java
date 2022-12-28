/*
 *
 * AFilter.java
 * 
 * Created by Wuwang on 2016/11/19
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.pepe.aplayer.opengl.filter;

import static android.opengl.GLES20.glBindTexture;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;
import android.util.SparseArray;

import com.pepe.aplayer.R;
import com.pepe.aplayer.opengl.MatrixUtil;
import com.pepe.aplayer.util.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;


/**
 * Description:
 */
public abstract class AFilter {

    private static final String TAG="Filter";

    public static final int KEY_OUT=0x101;
    public static final int KEY_IN=0x102;
    public static final int KEY_INDEX=0x201;

    public static boolean DEBUG=true;
    /**
     * 单位矩阵
     */
    public static final float[] OM= MatrixUtil.getOriginalMatrix();

    /**
     * 顶点坐标句柄
     */
    protected int glAttrPosition;
    /**
     * 纹理坐标句柄
     */
    protected int glAttrTextureCoordinate;
    /**
     * 总变换矩阵句柄
     */
    protected int textureMatrix;
    /**
     * 默认纹理贴图句柄
     */
    protected int glUniformTexture;

    protected Resources mRes;
    protected Context context;


    /**
     * 顶点坐标Buffer
     */
    protected FloatBuffer mVerBuffer;

    /**
     * 纹理坐标Buffer
     */
    protected FloatBuffer textureCords;

    /**
     * 索引坐标Buffer
     */
    protected ShortBuffer mindexBuffer;

    protected int mFlag=0;

    private float[] matrix= Arrays.copyOf(OM,16);

    private int textureType=0;      //默认使用Texture2D0
    private int textureId=0;
    //顶点坐标
    private float pos[] = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f
    };

    //纹理坐标
    protected float[] coord={
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f
    };

    private SparseArray<boolean[]> mBools;
    private SparseArray<int[]> mInts;
    private SparseArray<float[]> mFloats;
    private int outputWidth;
    private int outputHeight;

    public AFilter(Resources mRes){
        this.mRes=mRes;
        initBuffer();
    }
    public AFilter(){
    }

    public AFilter(Context context) {
        this(context, R.raw.single_input_v, R.raw.texture_f);
    }

    public AFilter(Context context, int fragmentResId) {
        this(context, R.raw.single_input_v, fragmentResId);
    }

    public AFilter(Context context, int vertexResId, int fragmentResId) {
        this.context = context;
        this.vertexResId = vertexResId;
        this.fragmentResId = fragmentResId;
    }

    public final void create(){
        onCreate();
    }

    public final void setSize(int width,int height){
        onSizeChanged(width,height);
    }




    protected boolean hasInit =false;
    protected int programId;
    protected String vertexFileName;
    protected String fragmentFileName;
    private int vertexResId;
    private int fragmentResId;

    private FloatBuffer cubeBuffer = ByteBuffer.allocateDirect(8 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    private FloatBuffer textureBuffer = ByteBuffer.allocateDirect(8 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    protected boolean isRenderScreen=false;
    protected Frame mframe;

    protected  void initPropertyLocation(){

    };
    protected void onDrawArraysPre(Frame frame){
    };
    protected void onDrawArraysAfter(Frame frame){
    };

    protected void bindTexture(int textureId) {
        glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
    }

    public Frame draw(Frame frame){
        LogUtil.printLog("11111----"+this.getClass().getName()+"--frameBuffer="+frame.getFrameBuffer()+"--frame="+frame.hashCode());

        if(!hasInit){
            LogUtil.printLog("000000---"+this.getClass().getName()+"--hasInit="+hasInit);
            programId = createGlProgram(readGlShader(context,vertexResId),readGlShader(context,fragmentResId));
//            programId = createGlProgram(readResourceString(mRes,vertexFileName),readResourceString(mRes,fragmentFileName));
//            glAttrPosition = GLES20.glGetAttribLocation(programId, "vPosition");
//            glAttrTextureCoordinate = GLES20.glGetAttribLocation(programId,"vCoord");
//            textureMatrix = GLES20.glGetUniformLocation(programId,"vMatrix");
//            glUniformTexture = GLES20.glGetUniformLocation(programId,"vTexture");
            glAttrPosition = GLES20.glGetAttribLocation(programId, "position");
            glAttrTextureCoordinate = GLES20.glGetAttribLocation(programId, "inputTextureCoordinate");
            glUniformTexture = GLES20.glGetUniformLocation(programId, "inputImageTexture");
            initPropertyLocation();
            textureBuffer.clear();
            textureBuffer.put(coord).position(0);
            cubeBuffer.clear();
            cubeBuffer.put(pos).position(0);
            hasInit = true;
        }
        if (programId <= 0) {
            return frame;
        }


//        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);


        if(mframe ==null){
            LogUtil.printLog("33333---"+this.getClass().getName()+"--frameBuffer="+frame.getFrameBuffer()+"--frame="+frame.hashCode());
            mframe = Frame.createOffScreemFrame(1080, 1920);
        }
        Log.d("#####", "onUseProgram()  mProgram="+ programId);
        GLES20.glUseProgram(programId);
        onDrawArraysPre(AFilter.this.mframe);


//        onSetExpandData();

        if (isRenderScreen) {
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        } else {
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mframe.getFrameBuffer());
        }
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0+textureType);
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,getTextureId());
        bindTexture(frame.getTextureId());
        GLES20.glUniform1i(glUniformTexture,textureType);

        GLES20.glEnableVertexAttribArray(glAttrPosition);
        GLES20.glVertexAttribPointer(glAttrPosition,2, GLES20.GL_FLOAT, false, 0,cubeBuffer);
        GLES20.glEnableVertexAttribArray(glAttrTextureCoordinate);
        GLES20.glVertexAttribPointer(glAttrTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
        Log.d("#####", "glDrawArrays 2222  mProgram="+ programId +"--"+this.getClass().getName());

        GLES20.glDisableVertexAttribArray(glAttrPosition);
        GLES20.glDisableVertexAttribArray(glAttrTextureCoordinate);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        if (isRenderScreen) {
            return null;
        } else {
            onDrawArraysAfter(AFilter.this.mframe);
            return AFilter.this.mframe;
        }
    }


    public void destroy() {
        hasInit = false;
        if (mframe != null) {
            int[] frameBufferTextures = {mframe.getTextureId()};
            GLES20.glDeleteTextures(frameBufferTextures.length, frameBufferTextures, 0);
            int[] frameBuffers = {mframe.getFrameBuffer()};
            GLES20.glDeleteFramebuffers(frameBuffers.length, frameBuffers, 0);
        }
        mframe = null;
        outputWidth = 0;
        outputHeight = 0;
    }

    public void setMatrix(float[] matrix){
        this.matrix=matrix;
    }

    public float[] getMatrix(){
        return matrix;
    }

    public final void setTextureType(int type){
        this.textureType=type;
    }

    public final int getTextureType(){
        return textureType;
    }

    public final int getTextureId(){
        return textureId;
    }

    public final void setTextureId(int textureId){
        this.textureId=textureId;
    }

    public void setFlag(int flag){
        this.mFlag=flag;
    }

    public int getFlag(){
        return mFlag;
    }

    public void setFloat(int type,float ... params){
        if(mFloats==null){
            mFloats=new SparseArray<>();
        }
        mFloats.put(type,params);
    }
    public void setInt(int type,int ... params){
        if(mInts==null){
            mInts=new SparseArray<>();
        }
        mInts.put(type,params);
    }
    public void setBool(int type,boolean ... params){
        if(mBools==null){
            mBools=new SparseArray<>();
        }
        mBools.put(type,params);
    }

    public boolean getBool(int type,int index) {
        if (mBools == null) return false;
        boolean[] b = mBools.get(type);
        return !(b == null || b.length <= index) && b[index];
    }

    public int getInt(int type,int index){
        if (mInts == null) return 0;
        int[] b = mInts.get(type);
        if(b == null || b.length <= index){
            return 0;
        }
        return b[index];
    }

    public float getFloat(int type,int index){
        if (mFloats == null) return 0;
        float[] b = mFloats.get(type);
        if(b == null || b.length <= index){
            return 0;
        }
        return b[index];
    }

    public int getOutputTexture(){
        return -1;
    }

    /**
     * 实现此方法，完成程序的创建，可直接调用createProgram来实现
     */
    protected void onCreate(){

    };
    protected void onSizeChanged(int width,int height){

    };



    /**
     * Buffer初始化
     */
    protected void initBuffer(){
        ByteBuffer a= ByteBuffer.allocateDirect(32);
        a.order(ByteOrder.nativeOrder());
        mVerBuffer=a.asFloatBuffer();
        mVerBuffer.put(pos);
        mVerBuffer.position(0);
        ByteBuffer b= ByteBuffer.allocateDirect(32);
        b.order(ByteOrder.nativeOrder());
        textureCords =b.asFloatBuffer();
        textureCords.put(coord);
        textureCords.position(0);
    }






    /**
     * 设置其他扩展数据
     */
    protected void onSetExpandData(){
        GLES20.glUniformMatrix4fv(textureMatrix,1,false,matrix,0);
    }


    public static void glError(int code, Object index){
        if(DEBUG&&code!=0){
            Log.e(TAG,"glError:"+code+"---"+index);
        }
    }





    //通过路径加载Assets中的文本内容
    public static String readResourceString(Resources mRes, String path){
        StringBuilder result=new StringBuilder();
        try{
            InputStream is=mRes.getAssets().open(path);
            int ch;
            byte[] buffer=new byte[1024];
            while (-1!=(ch=is.read(buffer))){
                result.append(new String(buffer,0,ch));
            }
        }catch (Exception e){
            return null;
        }
        return result.toString().replaceAll("\\r\\n","\n");
    }

    static String readGlShader(Context context, int resourceId) {
        StringBuilder builder = new StringBuilder();
        InputStream inputStream = context.getResources().openRawResource(resourceId);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    //创建GL程序
    public static int createGlProgram(String vertexSource, String fragmentSource){
        int vertex=loadShader(GLES20.GL_VERTEX_SHADER,vertexSource);
        if(vertex==0)return 0;
        int fragment=loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentSource);
        if(fragment==0)return 0;
        int program= GLES20.glCreateProgram();
        if(program!=0){
            GLES20.glAttachShader(program,vertex);
            GLES20.glAttachShader(program,fragment);
            GLES20.glLinkProgram(program);
            int[] linkStatus=new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS,linkStatus,0);
            if(linkStatus[0]!= GLES20.GL_TRUE){
                glError(1,"Could not link program:"+ GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program=0;
            }
        }
        return program;
    }

    //加载shader
    public static int loadShader(int shaderType, String source){
        int shader= GLES20.glCreateShader(shaderType);
        if(0!=shader){
            GLES20.glShaderSource(shader,source);
            GLES20.glCompileShader(shader);
            int[] compiled=new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS,compiled,0);
            if(compiled[0]==0){
                glError(1,"Could not compile shader:"+shaderType);
                glError(1,"GLES20 Error:"+ GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader=0;
            }
        }
        return shader;
    }


}
