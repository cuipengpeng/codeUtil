package com.playppt.ppt.http.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;
import java.io.InputStream;

public class OkHttpsGlideModule implements GlideModule {


    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
    }
    @Override
    public void registerComponents(Context context, Glide glide) {
        glide.register(GlideUrl.class, InputStream.class, new OkHttpsUrlLoader.Factory());
    }
}