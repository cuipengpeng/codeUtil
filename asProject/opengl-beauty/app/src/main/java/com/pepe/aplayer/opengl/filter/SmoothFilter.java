package com.pepe.aplayer.opengl.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.pepe.aplayer.R;


public class SmoothFilter extends AFilter {

    private int glUniformsample;
    private int glUniformLevel;
    private float smoothLevel;

    public SmoothFilter(Context context) {
        super(context, R.raw.smooth_f);
    }

    @Override
    protected void initPropertyLocation() {
        glUniformLevel = GLES20.glGetUniformLocation(programId, "intensity");
        glUniformsample = GLES20.glGetUniformLocation(programId, "singleStepOffset");
    }


    @Override
    protected void onDrawArraysPre(Frame frame) {
        GLES20.glUniform1f(glUniformLevel, smoothLevel);
        GLES20.glUniform2fv(glUniformsample, 1, new float[]{1.0f / frame.getPreviewWidth(), 1.0f / frame.getPreviewHeight()}, 0);
    }


    public void setSmoothLevel(float level) {
        this.smoothLevel = level;
    }
}
