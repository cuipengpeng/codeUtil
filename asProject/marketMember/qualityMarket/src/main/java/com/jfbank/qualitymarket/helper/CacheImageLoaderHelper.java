package com.jfbank.qualitymarket.helper;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.jfbank.qualitymarket.AppContext;
import com.jfbank.qualitymarket.util.CommonUtils;
import com.qiyukf.unicorn.api.ImageLoaderListener;
import com.squareup.picasso.Cache;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * 功能：<br>
 * 作者：赵海<br>
 * 时间： 2017/8/9 0009<br>.
 * 版本：1.2.0
 */

public abstract class CacheImageLoaderHelper implements Target {
   public String mUri;
    volatile boolean isCache = false;
    public CacheImageLoaderHelper(String uri) {
        this.mUri = uri;
    }

    public abstract void onLoadComplete(@NonNull Bitmap bitmap);


    public abstract void onloadDefault();


    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        onloadDefault();
        postBitmap();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
        isCache = true;
        this.onLoadComplete(bitmap);
        if (mUri != null && from == Picasso.LoadedFrom.NETWORK) {
            if (!DiskLruCacheHelper.isContainKey(mUri)) {
                DiskLruCacheHelper.put(mUri, bitmap);//设置缓存
            }
        }
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    /**
     * bitmap修改。
     */
    public void postBitmap() {
        try {
            DiskLruCacheHelper.getCacheThread().submit(new Runnable() {
                @Override
                public void run() {
                    if (!isCache) {
                        final Bitmap bitmap = DiskLruCacheHelper.getAsBitmap(mUri);//设置缓存
                        if (bitmap != null) {
                            onLoadComplete(bitmap);
                        }
                    }
                }
            });
        } catch (Exception e) {

        }
    }
}
