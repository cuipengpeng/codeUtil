
package com.pepe.aplayer.opengl.filter;

import android.content.res.Resources;

/**
 * Description:
 */
public class NoFilter extends AFilter {

    public NoFilter(Resources res) {
        super(res);
    }

    @Override
    protected void onCreate() {
        createProgramByAssetsFile("base_vertex.sh",
            "base_fragment.sh");
    }

    @Override
    protected void onSizeChanged(int width, int height) {

    }
}
