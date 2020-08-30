package com.test.xcamera.utils.glide;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.signature.ObjectKey;

public class OWLBitmapLoader implements ModelLoader<OWLImageFid, Bitmap> {
    private static final String DATA_URI_PREFIX = "data:";



    @Nullable
    @Override
    public LoadData<Bitmap> buildLoadData(OWLImageFid imageFid, int width, int height, Options options) {
        return new LoadData<>(new ObjectKey(imageFid), new OWLBitmapFetcher(imageFid));
    }

    @Override
    public boolean handles(OWLImageFid imageFid) {
        return true;
    }
}
