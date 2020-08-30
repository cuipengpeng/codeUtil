package com.test.xcamera.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.test.xcamera.R;
import com.test.xcamera.utils.glide.OWLImageFid;

import java.security.MessageDigest;

/**
 * Created by zhouxuecheng on
 * Create Time 2020/4/11
 * e-mail zhouxuecheng1991@163.com
 */

public class CustomGlideUtils {
    public static RequestOptions options = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
//            .skipMemoryCache(true)
            .placeholder(R.mipmap.bank_thumbnail_local)//图片加载出来前，显示的图片
            .fallback(R.mipmap.bank_thumbnail_local) //url为空的时候,显示的图片
            .error(R.mipmap.bank_thumbnail_local);//图片加载失败后，显示的图片;;

    public static void loadAlbumPhotoThumbnail(Context context, String uri, ImageView imageView, final long frameTime) {
        OWLImageFid imageFid = new OWLImageFid();
        imageFid.setTime(frameTime);
        imageFid.setUrl(uri);
        Glide.with(context).load(imageFid).apply(options).into(imageView);
    }

    public static void loadAlbumPhotoThumbnailRotate(Context context, String uri, ImageView imageView, final long frameTime, float rotateRotationAngle, int defaultImg) {
        RequestOptions optionsRotate = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(RequestOptions.bitmapTransform(new RotateTransformation(rotateRotationAngle)))
                .placeholder(defaultImg)//图片加载出来前，显示的图片
                .fallback(defaultImg) //url为空的时候,显示的图片
                .error(defaultImg);//图片加载失败后，显示的图片;
        OWLImageFid imageFid = new OWLImageFid();
        imageFid.setTime(frameTime);
        imageFid.setUrl(uri);
        Glide.with(context).load(imageFid).apply(optionsRotate).into(imageView);
    }

    public static void loadAlbumPhotoThumbnailRotate(Context context, String uri, ImageView imageView, final long frameTime, float rotateRotationAngle) {
        RequestOptions optionsRotate = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(RequestOptions.bitmapTransform(new RotateTransformation(rotateRotationAngle)))
                .placeholder(R.mipmap.bank_thumbnail_local)//图片加载出来前，显示的图片
                .fallback(R.mipmap.bank_thumbnail_local) //url为空的时候,显示的图片
                .error(R.mipmap.bank_thumbnail_local);//图片加载失败后，显示的图片;;
        OWLImageFid imageFid = new OWLImageFid();
        imageFid.setTime(frameTime);
        imageFid.setUrl(uri);
        Glide.with(context).load(imageFid).apply(optionsRotate).into(imageView);
    }

    public static void loadLocalPhotoThumbnail(Context mcontext, String url, ImageView imageView) {
        Glide.with(mcontext).load(url).apply(options).into(imageView);
    }


    /**
     * 展示原图信息,先展示缩略图,在展示原图
     *
     * @param context
     * @param thPath
     * @param uri
     * @param imageView
     * @param frameTime
     */
    public static void loadAlbumPhoto(Context context, String thPath, String uri, ImageView imageView, final long frameTime) {
        OWLImageFid imageFidTh = new OWLImageFid();
        imageFidTh.setTime(frameTime);
        imageFidTh.setUrl(thPath);
        RequestBuilder<Drawable> g = Glide.with(context).load(imageFidTh).thumbnail(0.1f);


        OWLImageFid imageFid = new OWLImageFid();
        imageFid.setTime(frameTime);
        imageFid.setUrl(uri);
        Glide.with(context).load(imageFid).apply(options).thumbnail(g).into(imageView);
    }

    /**
     * 展示原图信息,先展示缩略图,在展示原图
     *
     * @param context
     * @param thPath
     * @param uri
     * @param imageView
     * @param frameTime
     */
    public static void loadAlbumPhotoAngle(Context context, String thPath, String uri, ImageView imageView, final long frameTime, float rotateAngle) {
        RequestOptions optionsRotate = new RequestOptions().apply(RequestOptions.bitmapTransform(new RotateTransformation(rotateAngle)))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.bank_thumbnail_local)//图片加载出来前，显示的图片
                .fallback(R.mipmap.bank_thumbnail_local) //url为空的时候,显示的图片
                .error(R.mipmap.bank_thumbnail_local);//图片加载失败后，显示的图片;;
        OWLImageFid imageFidTh = new OWLImageFid();
        imageFidTh.setTime(frameTime);
        imageFidTh.setUrl(thPath);
        RequestBuilder<Drawable> g = Glide.with(context).load(imageFidTh).apply(optionsRotate).thumbnail(0.1f);


        OWLImageFid imageFid = new OWLImageFid();
        imageFid.setTime(frameTime);
        imageFid.setUrl(uri);
        Glide.with(context).load(imageFid).apply(optionsRotate).thumbnail(g).into(imageView);
    }


    public static void loadAlbumPhotoRotateAngle(Context context, String thPath, String uri, ImageView imageView, final long frameTime, float rotateAngle) {

        RequestOptions optionsRotate = new RequestOptions().apply(RequestOptions.bitmapTransform(new RotateTransformation(rotateAngle)))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.bank_thumbnail_local)//图片加载出来前，显示的图片
                .fallback(R.mipmap.bank_thumbnail_local) //url为空的时候,显示的图片
                .error(R.mipmap.bank_thumbnail_local);//图片加载失败后，显示的图片;;

        OWLImageFid imageFidTh = new OWLImageFid();
        imageFidTh.setTime(frameTime);
        imageFidTh.setUrl(thPath);
        RequestBuilder<Drawable> g = Glide.with(context).load(imageFidTh).apply(optionsRotate).thumbnail(0.1f);


        OWLImageFid imageFid = new OWLImageFid();
        imageFid.setTime(frameTime);
        imageFid.setUrl(uri);
        Glide.with(context).load(imageFid).apply(optionsRotate).thumbnail(g).into(imageView);
    }


    /**
     * 为旋转缩略图做的一个辅助类
     */
    public static class RotateTransformation extends BitmapTransformation {

        private final float rotateRotationAngle;

        public RotateTransformation(float rotateRotationAngle) {
            this.rotateRotationAngle = rotateRotationAngle;
        }

        @Override
        protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotateRotationAngle);
            return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight(), matrix, true);
        }

        @Override
        public void updateDiskCacheKey(MessageDigest messageDigest) {

        }
    }
}
