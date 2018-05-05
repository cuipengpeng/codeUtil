package com.test.bank.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.test.bank.R;

/**
 * Created by 55 on 2017/12/8.
 */

public class ImageUtils {
    public static void displayImage(Object context, String path, ImageView imageView) {
        Log.d("ImageUtils", "displayImage: " + path);
        if (context instanceof Fragment) {
            Glide.with((Fragment) context)
                    .load(path)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .error(R.drawable.icon_placehold)
                    .crossFade()
                    .into(imageView);
        } else if (context instanceof Activity) {
            Glide.with((Activity) context)
                    .load(path)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .error(R.drawable.icon_placehold)
                    .crossFade()
                    .into(imageView);
        } else if (context instanceof Context) {
            Glide.with((Context) context)
                    .load(path)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .error(R.drawable.icon_placehold)
                    .crossFade()
                    .into(imageView);
        }
    }

    public static void displayImage(final Context context, String path, final TextView textView) {
        Glide.with(context).load(path).asBitmap().fitCenter().into(new SimpleTarget<Bitmap>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                textView.setBackground(new BitmapDrawable(context.getResources(), resource));
            }
        });
    }

    public static void displayGifImage(Context context, int resource, ImageView imageView, final OnGifPlayListener onGifPlayListener) {
        displayGifImage(context, resource, imageView, onGifPlayListener, 1);
    }

    public static void displayGifImage(Context context, int resource, ImageView imageView, final OnGifPlayListener onGifPlayListener, int playTime) {
        Glide.with(context).load(resource).diskCacheStrategy(DiskCacheStrategy.SOURCE).listener(new RequestListener<Integer, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, Integer model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, Integer model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                // 计算动画时长
                int duration = 0;
                GifDrawable drawable = (GifDrawable) resource;
                GifDecoder decoder = drawable.getDecoder();
                for (int i = 0; i < drawable.getFrameCount(); i++) {
                    duration += decoder.getDelay(i);
                }
                LogUtils.e("gif duration " + duration);
                //发送延时消息，通知动画结束
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (onGifPlayListener != null) {
                            onGifPlayListener.onGifPlayFinish();
                        }
                    }
                }, duration + 800);
                return false;
            }
        }).into(new GlideDrawableImageViewTarget(imageView, playTime));
    }

    public interface OnGifPlayListener {
        void onGifPlayFinish();
    }

}
