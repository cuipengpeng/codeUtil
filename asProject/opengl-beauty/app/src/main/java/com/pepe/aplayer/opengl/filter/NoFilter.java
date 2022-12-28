
package com.pepe.aplayer.opengl.filter;

import android.content.res.Resources;

/**
 * Description:
 */
public class NoFilter extends AFilter {

    public NoFilter(Resources res) {
        super(res);
        vertexFileName = "base_vertex.sh";
        fragmentFileName = "base_fragment.sh";
    }

    @Override
    protected void initPropertyLocation() {

    }

    @Override
    protected void onCreate() {
    }

    @Override
    protected void onSizeChanged(int width, int height) {

    }
}
