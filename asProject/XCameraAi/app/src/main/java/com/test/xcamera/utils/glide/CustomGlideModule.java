package com.test.xcamera.utils.glide;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
@GlideModule
public class CustomGlideModule extends AppGlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        super.applyOptions(context, builder);
    }

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        registry.prepend(OWLImageFid.class,Bitmap.class,new OWLModelLoaderFactory());
    }
}
