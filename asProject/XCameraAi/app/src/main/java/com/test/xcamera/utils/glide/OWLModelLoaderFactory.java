package com.test.xcamera.utils.glide;

import android.graphics.Bitmap;

import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

public class OWLModelLoaderFactory implements ModelLoaderFactory<OWLImageFid, Bitmap> {

    @Override
    public ModelLoader<OWLImageFid, Bitmap> build(MultiModelLoaderFactory multiFactory) {
        return new OWLBitmapLoader();
    }

    @Override
    public void teardown() {
        // Do nothing.
    }
}
