package com.test.xcamera.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.test.xcamera.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoadPictureUtils {

    public static void showUri(Context context, Uri uri, ImageView imageView) {
        imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        if (uri.toString().startsWith("/")) {
            Picasso.with(context).load(Uri.parse("file://" + uri.toString())).resize(288, 162).noFade().into(imageView);
        } else {
            Picasso.with(context).load(uri).noFade().into(imageView);
        }
    }

    public static void showUri_gallery(Context context, Uri uri, ImageView imageView) {
        imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        if (uri.toString().startsWith("/")) {
            Picasso.with(context).load(Uri.parse("file://" + uri.toString())).noFade().into(imageView);
        } else {
            Picasso.with(context).load(uri).noFade().into(imageView);
        }
    }


    public static void showUri_video(Context context, Uri uri, ImageView imageView) {
        imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        if (uri.toString().startsWith("/")) {
            Picasso.with(context).load(Uri.parse("file://" + uri.toString())).noFade().into(imageView);
        } else {
            Picasso.with(context).load(uri).noFade().into(imageView);
        }
    }


    /**
     * 从网络请求的图片
     *
     * @param context
     * @param url
     * @param imageView
     */
    public static void showUrl(Context context, String url, ImageView imageView) {
        imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        Picasso.with(context).load(url).noFade().into(imageView);
    }

    /**
     * 根据URL获取Bitmap
     */
    public static Bitmap getHttpBitmap(String url) {
        Bitmap bitmap = null;
        URL myUrl;

        try {
            myUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) myUrl.openConnection();
            conn.setConnectTimeout(5000);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            //把bitmap转成圆形
            bitmap = toRoundBitmap(bitmap);
            is.close();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //返回圆形bitmap
        return bitmap;
    }

    /**
     * 把bitmap转成圆形
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int r = 0;
        //取最短边做边长
        if (width < height) {
            r = width;
        } else {
            r = height;
        }
        //构建一个bitmap
        Bitmap backgroundBm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //new一个Canvas，在backgroundBmp上画图
        Canvas canvas = new Canvas(backgroundBm);
        Paint p = new Paint();
        //设置边缘光滑，去掉锯齿
        p.setAntiAlias(true);
        RectF rect = new RectF(0, 0, r, r);
        //通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，
//        <span style="white-space:pre">	</span>//且都等于r/2时，画出来的圆角矩形就是圆形
//                canvas.drawRoundRect(rect, r/2, r/2, p);
        //设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //canvas将bitmap画在backgroundBmp上
        canvas.drawBitmap(bitmap, null, rect, p);
        return backgroundBm;
    }


}
