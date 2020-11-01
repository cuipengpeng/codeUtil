package com.pepe.aplayer.opengl;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.pepe.aplayer.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;



public class BeautyRenderer implements GLSurfaceView.Renderer{

    public static final String VERTEX_SHADER = "" +
            "attribute vec4 vPosition;\n" +
            "uniform mat4 vMatrix;\n"+
            "attribute vec2 vCoordinate;\n" +
            " \n" +
            "varying vec2 textureCoordinate;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position =vMatrix* vPosition;\n" +
            "    textureCoordinate = vCoordinate;\n" +
            "}";

    public static final String BILATERAL_FRAGMENT_SHADER = "" +
            "precision highp float;\n"+
            "   varying highp vec2 textureCoordinate;\n" +
            "\n" +
            "    uniform sampler2D vTexture;\n" +
            "\n" +
            "    uniform highp vec2 singleStepOffset;\n" +
            "    uniform highp vec4 params;\n" +
            "    uniform highp float brightness;\n" +
            "    uniform float texelWidthOffset;\n"+
            "    uniform float texelHeightOffset;\n"+
            "\n" +
            "    const highp vec3 W = vec3(0.299, 0.587, 0.114);\n" +
            "    const highp mat3 saturateMatrix = mat3(\n" +
            "        1.1102, -0.0598, -0.061,\n" +
            "        -0.0774, 1.0826, -0.1186,\n" +
            "        -0.0228, -0.0228, 1.1772);\n" +
            "    highp vec2 blurCoordinates[24];\n" +
            "\n" +
            "    highp float hardLight(highp float color) {\n" +
            "    if (color <= 0.5)\n" +
            "        color = color * color * 2.0;\n" +
            "    else\n" +
            "        color = 1.0 - ((1.0 - color)*(1.0 - color) * 2.0);\n" +
            "    return color;\n" +
            "}\n" +
            "\n" +
            "    void main(){\n" +
            "    highp vec3 centralColor = texture2D(vTexture, textureCoordinate).rgb;\n" +
            "    vec2 singleStepOffset=vec2(texelWidthOffset,texelHeightOffset);\n"+
            "    blurCoordinates[0] = textureCoordinate.xy + singleStepOffset * vec2(0.0, -10.0);\n" +
            "    blurCoordinates[1] = textureCoordinate.xy + singleStepOffset * vec2(0.0, 10.0);\n" +
            "    blurCoordinates[2] = textureCoordinate.xy + singleStepOffset * vec2(-10.0, 0.0);\n" +
            "    blurCoordinates[3] = textureCoordinate.xy + singleStepOffset * vec2(10.0, 0.0);\n" +
            "    blurCoordinates[4] = textureCoordinate.xy + singleStepOffset * vec2(5.0, -8.0);\n" +
            "    blurCoordinates[5] = textureCoordinate.xy + singleStepOffset * vec2(5.0, 8.0);\n" +
            "    blurCoordinates[6] = textureCoordinate.xy + singleStepOffset * vec2(-5.0, 8.0);\n" +
            "    blurCoordinates[7] = textureCoordinate.xy + singleStepOffset * vec2(-5.0, -8.0);\n" +
            "    blurCoordinates[8] = textureCoordinate.xy + singleStepOffset * vec2(8.0, -5.0);\n" +
            "    blurCoordinates[9] = textureCoordinate.xy + singleStepOffset * vec2(8.0, 5.0);\n" +
            "    blurCoordinates[10] = textureCoordinate.xy + singleStepOffset * vec2(-8.0, 5.0);\n" +
            "    blurCoordinates[11] = textureCoordinate.xy + singleStepOffset * vec2(-8.0, -5.0);\n" +
            "    blurCoordinates[12] = textureCoordinate.xy + singleStepOffset * vec2(0.0, -6.0);\n" +
            "    blurCoordinates[13] = textureCoordinate.xy + singleStepOffset * vec2(0.0, 6.0);\n" +
            "    blurCoordinates[14] = textureCoordinate.xy + singleStepOffset * vec2(6.0, 0.0);\n" +
            "    blurCoordinates[15] = textureCoordinate.xy + singleStepOffset * vec2(-6.0, 0.0);\n" +
            "    blurCoordinates[16] = textureCoordinate.xy + singleStepOffset * vec2(-4.0, -4.0);\n" +
            "    blurCoordinates[17] = textureCoordinate.xy + singleStepOffset * vec2(-4.0, 4.0);\n" +
            "    blurCoordinates[18] = textureCoordinate.xy + singleStepOffset * vec2(4.0, -4.0);\n" +
            "    blurCoordinates[19] = textureCoordinate.xy + singleStepOffset * vec2(4.0, 4.0);\n" +
            "    blurCoordinates[20] = textureCoordinate.xy + singleStepOffset * vec2(-2.0, -2.0);\n" +
            "    blurCoordinates[21] = textureCoordinate.xy + singleStepOffset * vec2(-2.0, 2.0);\n" +
            "    blurCoordinates[22] = textureCoordinate.xy + singleStepOffset * vec2(2.0, -2.0);\n" +
            "    blurCoordinates[23] = textureCoordinate.xy + singleStepOffset * vec2(2.0, 2.0);\n" +
            "\n" +
            "    highp float sampleColor = centralColor.g * 22.0;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[0]).g;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[1]).g;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[2]).g;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[3]).g;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[4]).g;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[5]).g;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[6]).g;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[7]).g;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[8]).g;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[9]).g;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[10]).g;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[11]).g;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[12]).g * 2.0;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[13]).g * 2.0;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[14]).g * 2.0;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[15]).g * 2.0;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[16]).g * 2.0;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[17]).g * 2.0;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[18]).g * 2.0;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[19]).g * 2.0;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[20]).g * 3.0;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[21]).g * 3.0;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[22]).g * 3.0;\n" +
            "    sampleColor += texture2D(vTexture, blurCoordinates[23]).g * 3.0;\n" +
            "\n" +
            "    sampleColor = sampleColor / 62.0;\n" +
            "\n" +
            "    highp float highPass = centralColor.g - sampleColor + 0.5;\n" +
            "\n" +
            "    for (int i = 0; i < 5; i++) {\n" +
            "        highPass = hardLight(highPass);\n" +
            "    }\n" +
            "    highp float lumance = dot(centralColor, W);\n" +
            "\n" +
            "    highp float alpha = pow(lumance, params.r);\n" +
            "\n" +
            "    highp vec3 smoothColor = centralColor + (centralColor-vec3(highPass))*alpha*0.1;\n" +
            "\n" +
            "    smoothColor.r = clamp(pow(smoothColor.r, params.g), 0.0, 1.0);\n" +
            "    smoothColor.g = clamp(pow(smoothColor.g, params.g), 0.0, 1.0);\n" +
            "    smoothColor.b = clamp(pow(smoothColor.b, params.g), 0.0, 1.0);\n" +
            "\n" +
            "    highp vec3 lvse = vec3(1.0)-(vec3(1.0)-smoothColor)*(vec3(1.0)-centralColor);\n" +
            "    highp vec3 bianliang = max(smoothColor, centralColor);\n" +
            "    highp vec3 rouguang = 2.0*centralColor*smoothColor + centralColor*centralColor - 2.0*centralColor*centralColor*smoothColor;\n" +
            "\n" +
            "    gl_FragColor = vec4(mix(centralColor, lvse, alpha), 1.0);\n" +
            "    gl_FragColor.rgb = mix(gl_FragColor.rgb, bianliang, alpha);\n" +
            "    gl_FragColor.rgb = mix(gl_FragColor.rgb, rouguang, params.b);\n" +
            "\n" +
            "    highp vec3 satcolor = gl_FragColor.rgb * saturateMatrix;\n" +
            "    gl_FragColor.rgb = mix(gl_FragColor.rgb, satcolor, params.a);\n" +
            "    gl_FragColor.rgb = vec3(gl_FragColor.rgb + vec3(brightness));\n" +
            "}";

    private  Context context;
//    private TextureShaderProgram textureProgram;
    private int textureProgram;
    private int texture;
    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    private Picture picture;

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

        picture = new Picture();
        textureProgram = ShaderUtils.createProgram(VERTEX_SHADER,BILATERAL_FRAGMENT_SHADER);
//        texture = TextureHelper.loadTexture(context, R.mipmap.liu);
        texture = createTexture(R.mipmap.liu);


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
