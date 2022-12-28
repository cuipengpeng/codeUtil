package com.pepe.aplayer.opengl.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.pepe.aplayer.R;


public class WhiteFilter extends AFilter {


    private int glUniformLevel;

    private float level;


    public WhiteFilter(Context context) {
        super(context, R.raw.light_f);
    }

    @Override
    protected void initPropertyLocation() {
        glUniformLevel = GLES20.glGetUniformLocation(programId, "level");
    }

    @Override
    protected void onDrawArraysPre(Frame frame) {
        GLES20.glUniform1f(glUniformLevel, level);
    }

    public void setWhiteLevel(float level) {
        this.level = level;
    }
}
