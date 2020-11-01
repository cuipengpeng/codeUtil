package com.pepe.aplayer.test;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TestGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
    public TestGLSurfaceView(Context context) {
        super(context);
    }

    public TestGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    private float[] vertPositionArray = {
            -1,1,
            1,1,
            -1,-1,
            1,-1
    };
    private float[] texPositionArray = {
            0,1,
            1,1,

    };


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {

    }
}
