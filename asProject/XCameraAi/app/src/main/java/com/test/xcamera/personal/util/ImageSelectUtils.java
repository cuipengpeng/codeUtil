package com.test.xcamera.personal.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;

import java.io.File;

public class ImageSelectUtils {
    public static File getPhotoSelect(Context context,String name){
         return new File(getPicPath(context) + File.separator + name + ".jpg");
    }
    public static String getPicPath(Context context) {
        return context.getExternalCacheDir().getAbsolutePath();
    }
    /**
     * 选择图片（从相册或相机）
     * @param uri 相机存储uri
     * @return
     */


    public static Intent getPhotoSelectIntent(Uri uri){
        Intent take = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        take.addCategory(Intent.CATEGORY_DEFAULT);
        take.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        Intent pics = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent chose= Intent.createChooser(pics,"选择图片");
        chose.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{take});
        return chose;
    }
    /**
     * 图片裁剪
     * @param inputUri 需要裁剪的图片
     * @param outputUri 裁剪后存储位置
     * @param width 裁剪宽度
     * @param height 裁剪高度
     * @return
     */
    public static Intent getImageCropIntent(Uri inputUri, Uri outputUri, int width, int height) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(inputUri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true); // 去黑边
        intent.putExtra("scaleUpIfNeeded", true); // 去黑边
        // aspectX aspectY 裁剪框宽高比例
        intent.putExtra("aspectX", width); // 输出是X方向的比例
        intent.putExtra("aspectY", height);
        // outputX outputY 输出图片宽高，切忌不要再改动下列数字，会卡死
        intent.putExtra("outputX", width); // 输出X方向的像素
        intent.putExtra("outputY", height);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("return-data", false); // 设置为不返回数据
        return intent;
    }
    public static Uri getContentUri(Context context, File file) {
        String filePath = file.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            cursor.close();
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (file.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
    public String uploadImage(Uri inputUri){
        String url="";
        return url;
    }
}
