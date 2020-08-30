package com.test.xcamera.utils.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.editvideo.ToastUtil;
import com.test.xcamera.R;
import com.test.xcamera.picasso.MoCameraRequest;
import com.test.xcamera.utils.AppExecutors;

/**
 * 视屏贞加载
 */
public class VideoFrameUtils {


    public static RequestOptions options = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            //.skipMemoryCache(true)
            .placeholder(R.mipmap.bank_thumbnail_local)//图片加载出来前，显示的图片
            .fallback(R.mipmap.bank_thumbnail_local) //url为空的时候,显示的图片
            .error(R.mipmap.bank_thumbnail_local);//图片加载失败后，显示的图片;;

    public static void loadVideoThumbnail(Context context, String uri, ImageView imageView, final long frameTime) {
        OWLImageFid imageFid = new OWLImageFid();
        imageFid.setTime(frameTime);
        imageFid.setUrl(uri);
        Glide.with(context).load(imageFid).apply(options).into(imageView);
    }
    public static void loadImageThumbnailBitmap(Context context, String uri, ImageView imageView) {
        OWLImageFid imageFid = new OWLImageFid();
        imageFid.setUrl(uri);
        Glide.with(context).asBitmap().load(imageFid).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                ToastUtil.showToast(context,"bit map:resource "+resource);

                if(resource!=null){
                    imageView.setImageBitmap(resource);
                }
            }
        });
    }
    public static void loadImageThumbnailBitmap(final String strUir, ImageView imageView) {

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Uri uri = Uri.parse(strUir);

                    if (uri == null) throw new NullPointerException("uri is null");
                    String strSize = uri.getQueryParameter("size");

                    int size = 0;
                    try {
                        size = Integer.parseInt(strSize);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    byte[] data = new MoCameraRequest().request(uri, size);

                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    AppExecutors.getInstance().mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap);

                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
