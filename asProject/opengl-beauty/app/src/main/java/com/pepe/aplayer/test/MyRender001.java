package com.pepe.aplayer.test;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.pepe.aplayer.opengl.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyRender001 implements GLSurfaceView.Renderer {

    private String vertexShaderStr =
            "arrtibute vec4 aPosition;"+
            "uniform mat4 uMVPMatrix;"+
            "arrtibute vec4 aTexPosition;"+
            "uniform mat4 uTexMatrix;"+
            "varying vec2 vTexPosition;"+
            "void main(){"+
            "gl_Position = uMVPMatrix * aPosition;"+
            "vTexPosition = (uTexMatrix * aTexPosition).xy;"+
                    "}";

    private String fragmentShaderStr =
            "#extension GL_OES_EGL_image_external : require"+
            "precision medium float;"+
            "uniform samplerExternalOES uTexture;"+
            "varying vec2 vTexPosition;"+
                    "void main(){"+
                    "gl_FragColor = texture2D(uTexture,vTexPosition);"+
                    "}";

    private float[] vertexPositionArray = {
            -1f, 1f,
            1f, 1f,
            -1f, -1f,
            1f, -1f,
    };

    private float[] fragmentPositionArray = {
            0f, 0f,
            1f, 0f,
            0f, 1f,
            1f, 1f,
    };

    private FloatBuffer vertexFloatBuffer;
    private FloatBuffer fragmentFloatBuffer;
    private int mProgram;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        vertexFloatBuffer = ByteBuffer.allocateDirect(vertexPositionArray.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexFloatBuffer.put(vertexPositionArray).position(0);
        fragmentFloatBuffer = ByteBuffer.allocateDirect(fragmentPositionArray.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        fragmentFloatBuffer.put(fragmentPositionArray).position(0);

        mProgram = ShaderUtils.createProgram(vertexShaderStr, fragmentShaderStr);


//        private String vertexShaderStr =
//                "arrtibute vec4 aPosition;"+
//                        "uniform mat4 uMVPMatrix;"+
//                        "arrtibute vec4 aTexPosition;"+
//                        "uniform mat4 uTexMatrix;"+
//                        "varying vec2 vTexPosition;"+
//                        "void main(){"+
//                        "gl_Position = uMVPMatrix * aPosition;"+
//                        "vTexPosition = (uTexMatrix * aTexPosition).xy;"+
//                        "}";
//
//        private String fragmentShaderStr =
//                "#extension GL_OES_EGL_image_external : require"+
//                        "precision medium float;"+
//                        "uniform samplerExternalOES uTexture;"+
//                        "varying vec2 vTexPosition;"+
//                        "void main(){"+
//                        "gl_FragColor = texture2D(uTexture,vTexPosition);"+
//                        "}";
        GLES20.glGetUniformLocation(mProgram, "");
        GLES20.glGetUniformLocation(mProgram, "");
        GLES20.glGetAttribLocation(mProgram, "aPosition");
        GLES20.glGetAttribLocation(mProgram, "");

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }
}
