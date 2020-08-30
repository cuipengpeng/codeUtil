package com.test.xcamera.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.test.xcamera.R;
import com.test.xcamera.application.AiCameraApplication;

/**
 * 加载不同的
 */
public class GlideUtils {

    public static RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.mipmap.bank_thumbnail_local)//图片加载出来前，显示的图片
            .fallback(R.mipmap.bank_thumbnail_local) //url为空的时候,显示的图片
            .error(R.mipmap.bank_thumbnail_local);//图片加载失败后，显示的图片;
    private static RequestOptions optionsCircleCrop = RequestOptions.bitmapTransform(new CircleCrop())
            .signature(new ObjectKey(System.currentTimeMillis()))
            .placeholder(R.mipmap.icon_header)//图片加载出来前，显示的图片
            .fallback(R.mipmap.icon_header) //url为空的时候,显示的图片
            .error(R.mipmap.icon_header);//图片加载失败后，显示的图片;
    private static RequestOptions optionsCircleCropSkip = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE).skipMemoryCacheOf(true).bitmapTransform(new CircleCrop())
            .signature(new ObjectKey(System.currentTimeMillis()))
            .placeholder(R.mipmap.icon_header)//图片加载出来前，显示的图片
            .fallback(R.mipmap.icon_header) //url为空的时候,显示的图片
            .error(R.mipmap.icon_header);//图片加载失败后，显示的图片;
    public static RequestOptions DY_PROP_OPTIONS = new RequestOptions()
            .placeholder(R.mipmap.dy_props_default_bg)//图片加载出来前，显示的图片
            .fallback(R.mipmap.dy_props_default_bg) //url为空的时候,显示的图片
            .error(R.mipmap.dy_props_default_bg);//图片加载失败后，显示的图片;
    public static RequestOptions DY_FILTER_OPTIONS = new RequestOptions()
            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
            .placeholder(R.mipmap.dy_filter_default_bg)//图片加载出来前，显示的图片
            .fallback(R.mipmap.dy_filter_default_bg) //url为空的时候,显示的图片
            .error(R.mipmap.dy_filter_default_bg);//图片加载失败后，显示的图片;
    public static RequestOptions RoundedCorners_OPTIONS = new RequestOptions()
            .apply(RequestOptions.bitmapTransform(new RoundedCorners(DensityUtils.dp2px(AiCameraApplication.getContext(), 4))))
            .placeholder(R.mipmap.dy_filter_default_bg)//图片加载出来前，显示的图片
            .fallback(R.mipmap.dy_filter_default_bg) //url为空的时候,显示的图片
            .error(R.mipmap.dy_filter_default_bg);//图片加载失败后，显示的图片;

    public static void GlideLoader(Context mcontext, String url, ImageView imageView) {
        Glide.with(mcontext).load(url).apply(options).into(imageView);
    }

    public static void GlideLoaderHeader(Context mcontext, String url, ImageView imageView) {
        Glide.with(mcontext)
                .load(url)
                .apply(optionsCircleCrop).into(imageView);
    }

    public static void GlideLoaderHeaderSkip(Context mcontext, String url, ImageView imageView) {
        Glide.with(mcontext)
                .load(url)
                .apply(optionsCircleCropSkip).into(imageView);
    }

    public static void GlideRoundedCornerHeader(Context mcontext, String url, ImageView imageView) {
        Glide.with(mcontext)
                .load(url)
                .apply(RoundedCorners_OPTIONS).into(imageView);
    }

    public static void GlideCircle(Context mcontext, String url, ImageView imageView) {
        Glide.with(mcontext)
                .load(url)
                .apply(optionsCircleCrop).into(imageView);
    }

    public static void GlideCircle(Context mcontext, int url, ImageView imageView) {
        Glide.with(mcontext)
                .load(url)
                .apply(optionsCircleCrop).into(imageView);
    }

    public static RequestOptions optionsAdvertising = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.mipmap.splash_bc1)//图片加载出来前，显示的图片
            .fallback(R.mipmap.splash_bc1)//url为空的时候,显示的图片
            .error(R.mipmap.splash_bc1);//图片加载失败后，显示的图片;

    public static void GlideLoaderAdvertising(Context mcontext, String url, ImageView imageView) {
        Glide.with(mcontext).load(url).apply(optionsAdvertising).into(imageView);
    }

}
