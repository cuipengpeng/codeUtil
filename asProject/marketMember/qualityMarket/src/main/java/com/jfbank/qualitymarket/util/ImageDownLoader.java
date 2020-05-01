package com.jfbank.qualitymarket.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;

/*
* 描    述：<br>
* 作    者：崔朋朋<br>
* 时    间：2017/3/17<br>
*/
public class ImageDownLoader {
    private static File savedPublicFileDir = null;

    public static void download(final Activity activity , String url , File savedDir, final String savedFileName) {

        if(StringUtil.isNull(url)){
            return ;
        }

        if(savedDir != null && savedDir.exists()){
            savedPublicFileDir = savedDir;
        }else {
            if (!Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                Toast.makeText(activity,"请插入SD卡", Toast.LENGTH_SHORT).show();
                return;
            }
            savedPublicFileDir = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DCIM);
        }




        Target target = new Target(){
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                String imageName = System.currentTimeMillis() + ".png";
                if(StringUtil.notEmpty(savedFileName)){
                    imageName = savedFileName;
                }

                File downloadFile = new File(savedPublicFileDir, imageName);

                FileOutputStream ostream = null;
                try {
                    ostream = new FileOutputStream(downloadFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                    ostream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                Toast.makeText(activity, "图片下载至:"+downloadApk.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };

        //Picasso下载
        Picasso.with(activity).load(url).into(target);
    }


    public static void saveBitmapToDisk(final Activity activity , Bitmap bitmap, File savedDir, final String savedFileName){
        if(bitmap == null){
            return ;
        }

        File savedPublicFileDir = null;

        if(savedDir != null && savedDir.exists()){
            savedPublicFileDir = savedDir;
        }else {
            if (!Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                Toast.makeText(activity,"请插入SD卡", Toast.LENGTH_SHORT).show();
                return;
            }
            savedPublicFileDir = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DCIM);
        }


        String imageName = System.currentTimeMillis() + ".png";
        if(StringUtil.notEmpty(savedFileName)){
            imageName = savedFileName;
        }

        final File downloadFile = new File(savedPublicFileDir, imageName);

        FileOutputStream ostream = null;
        try {
            ostream = new FileOutputStream(downloadFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
            ostream.flush();
            ostream.close();
            ostream = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(activity, "图片下载至:"+downloadApk.getAbsolutePath(), Toast.LENGTH_SHORT).show();
//            }
//        });


    }


}
