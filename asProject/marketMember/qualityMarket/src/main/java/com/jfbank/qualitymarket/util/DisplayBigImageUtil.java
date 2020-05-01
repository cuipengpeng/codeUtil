package com.jfbank.qualitymarket.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jfbank.qualitymarket.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2017/4/24<br>
*/
public class DisplayBigImageUtil {


    /**
     * 放大显示一张图片
     * @param activity
     * @param imageUrl
     */
    public static void displayBigImageView(final Activity activity, String imageUrl) {
        if (activity == null || StringUtil.isNull(imageUrl)) {
            return;
        }

        final ImageView imageView = new ImageView(activity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(layoutParams);

        //获取屏幕分辨率
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowMgr = (WindowManager)activity.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowMgr.getDefaultDisplay().getMetrics(dm);
        final int screenWidth = dm.widthPixels;
        final int screenHeight = dm.heightPixels;

        LogUtil.printLog(imageUrl);
        Picasso.with(activity).load(imageUrl).transform(new Transformation() {
            @Override
            public Bitmap transform(Bitmap bitmap) {
                Bitmap newBitmap  = createBigBitmap(bitmap, screenWidth,screenHeight);
                if(newBitmap != null && newBitmap.hashCode()!= bitmap.hashCode()){
                    bitmap.recycle();
                    return newBitmap;
                }else {
                    return bitmap;
                }
            }

            @Override
            public String key() {
                return "123";
            }
        }).into(imageView, new Callback() {

                    @Override
                    public void onSuccess() {
                        //图片加载成功表明本次网络请求成功
                        final Dialog dialog = new Dialog(activity, R.style.bigImageViewDialog);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.setCancelable(true);
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.setContentView(imageView);
                        dialog.setTitle("");
                        dialog.show();
                    }

                    @Override
                    public void onError() {
                    }
                }
        );


    }

    public static Bitmap createBigBitmap(Bitmap b,float targetWidth,float targetHeight)
    {
        int w=b.getWidth();
        int h=b.getHeight();
        float standardScale =1;
        if(targetWidth > targetHeight){
            standardScale = targetHeight/h;
        }else {
            standardScale = targetWidth/w;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(standardScale, standardScale); // 长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(b, 0, 0, w, h, matrix, true);
        return resizeBmp;
    }
}
