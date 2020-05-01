package com.jfbank.qualitymarket.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.qiyukf.unicorn.api.ImageLoaderListener;
import com.qiyukf.unicorn.api.UnicornImageLoader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

/**
 * 功能：PicassoImageLoaderHelper七鱼图片加载<br>
 * 作者：赵海<br>
 * 时间： 2017/4/1 0001<br>.
 * 版本：1.2.0
 */

public class PicassoImageLoaderHelper implements UnicornImageLoader {
    private Context context;
    private Handler handler;

    public PicassoImageLoaderHelper(Context context) {
        this.context = context.getApplicationContext();
        handler = new Handler(context.getMainLooper());
    }

    @Nullable
    @Override
    public Bitmap loadImageSync(String uri, int width, int height) {
        return null;
    }

    @Override
    public void loadImage(final String uri, final int width, final int height, final ImageLoaderListener listener) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                RequestCreator requestCreator = Picasso
                        .with(context)
                        .load(uri)
                        .config(Bitmap.Config.RGB_565);
                if (width > 0 && height > 0) {
                    requestCreator = requestCreator.resize(width, height);
                }
                requestCreator.into(new Target() {
                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }

                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        if (listener != null) {
                            listener.onLoadComplete(bitmap);
                        }
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        if (listener != null) {
                            listener.onLoadFailed(null);
                        }
                    }
                });
            }
        });
    }
}