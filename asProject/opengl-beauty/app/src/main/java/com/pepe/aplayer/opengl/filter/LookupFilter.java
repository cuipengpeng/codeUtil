package com.pepe.aplayer.opengl.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;

import com.pepe.aplayer.R;
import com.pepe.aplayer.opengl.TextureUtil;

public class LookupFilter extends AFilter {

    private int glUniIntensity;
    private int glUniLookupTexture;
    private float intensity = 1f;
    private Bitmap bitmap;
    private volatile boolean isNewFilter;


    public LookupFilter(Context context) {
        super(context, R.raw.lookup_f);
    }

    @Override
    protected void initPropertyLocation() {
        glUniIntensity = GLES20.glGetUniformLocation(programId, "intensity");
        glUniLookupTexture = GLES20.glGetUniformLocation(programId, "lookupTexture");
    }

    @Override
    protected void onDrawArraysPre(Frame frame) {
        GLES20.glUniform1f(glUniIntensity, intensity);
        if (isNewFilter && bitmap != null) {
            int lookupTexture = TextureUtil.loadTexture(bitmap, TextureUtil.NO_TEXTURE);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, lookupTexture);
            GLES20.glUniform1i(glUniLookupTexture, 4);
            isNewFilter = false;
        }
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public void setBitmap(Bitmap bitmap) {
        if (!isNewFilter) {
            this.bitmap = bitmap;
            isNewFilter = true;
        }
    }
}
