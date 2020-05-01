package com.jfbank.qualitymarket.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 文 件 名： 功 能： 作 者：赵海 时 间：2016/7/20
 **/
public class PhotoUtils {
    public static String ADD_PIC = "ADD_PIC";
    public static int TAKE_HOTO = 0x23;
    public final static int TAKE_PIC = 0x22;
    public final static String TAG = "PhotoUtils";

    public static String onActivityResultTakePic(Activity activity, int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED)
            return null;
        if (resultCode == Activity.RESULT_OK && requestCode == TAKE_PIC) {
            File imageFile = new File(getPrefString(activity, "tack_pic", ""));
            if (!imageFile.exists()) {
                setPrefString(activity, "tack_pic", "");
                Toast.makeText(activity, "拍照失败", Toast.LENGTH_SHORT).show();
                return null;
            }
            currUri = Uri.fromFile(imageFile);
            getPrefString(activity, "tack_pic", "");
            String path = currUri.getEncodedPath();
            return path;
        }
        if (resultCode == Activity.RESULT_OK && requestCode == TAKE_HOTO) {
            return getRealPathFromURI(activity, data.getData());
        }
        return null;
    }

    public static Uri getImageContentUri(Context context, java.io.File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    private static Uri currUri;

    private static String getRealPathFromURI(Context context, Uri contentURI) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static Bitmap getimage(Bitmap bitmap) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 95, bout);// 可以是CompressFormat.PNG

        // 图片原始数据
        byte[] byteArr = bout.toByteArray();

        // 计算sampleSize
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        // 调用方法后，option已经有图片宽高信息
        BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length, options);

        // 计算最相近缩放比例
        options.inSampleSize = calculateInSampleSize(options, 720, 1280);
        options.inJustDecodeBounds = false;

        // 这个Bitmap out只有宽高
        Bitmap out = BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length, options);

        return bitmap;// 压缩好比例大小后再进行质量压缩
    }

    /**
     * 计算图片的缩放值
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 拍照
     *
     * @param activity
     */
    public static void takePic(Activity activity) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        currUri = getOutputUri(activity);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, currUri);
        setPrefString(activity, "tack_pic", currUri.getPath());
        activity.startActivityForResult(intent, TAKE_PIC);
    }

    public static void takePic(Context context, JumpUtil.JumpInterface jumpInterface) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        currUri = getOutputUri(context);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, currUri);
        setPrefString(context, "tack_pic", currUri.getPath());
        jumpInterface.startActivityForResult(intent, TAKE_PIC);
    }

    public static void choosePic(JumpUtil.JumpInterface jumpInterface) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        jumpInterface.startActivityForResult(intent, TAKE_HOTO);
    }

    /**
     * 拍照
     *
     * @param fragment
     */
    public static void takePic(Fragment fragment) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        currUri = getOutputUri(fragment.getActivity());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, currUri);
        setPrefString(fragment.getActivity(), "tack_pic", currUri.getPath());
        fragment.startActivityForResult(intent, TAKE_PIC);
    }

    /**
     * 返回URi
     *
     * @param context
     * @return
     */
    public static Uri getOutputUri(Context context) {
        File eFile = Environment.getExternalStorageDirectory();
        File mDirectory = new File(eFile.toString() + File.separator + context.getPackageName());
        if (!mDirectory.exists()) {
            mDirectory.mkdirs();
        }
        File imageFile = new File(mDirectory, "photo_" + System.currentTimeMillis() + ".jpg");
        Uri uri = Uri.fromFile(imageFile);
        return uri;
    }

    public static File getFile(Context context) {
        File eFile = Environment.getExternalStorageDirectory();
        File mDirectory = new File(eFile.toString() + File.separator + context.getPackageName());
        if (!mDirectory.exists()) {
            mDirectory.mkdirs();
        }
        File imageFile = new File(mDirectory, "photo_" + System.currentTimeMillis() + ".jpg");
        return imageFile;
    }

    public static File createPic(Context context, final byte[] data) {
        File file = getFile(context);
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            os.write(data);
            os.close();
        } catch (IOException e) {
            Log.w(TAG, "Cannot write to " + file, e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
            if (file != null && file.exists()) {
                return file;
            } else {
                return null;
            }
        }
    }

    public static void getPic(final Activity activity, OnCancelListener onCancelListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle("选择图片");
        builder.setSingleChoiceItems(new String[]{"拍照", "相册"}, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0) {
                    List<String> permission = new ArrayList<String>();
                    if (ContextCompat.checkSelfPermission(activity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        // 请求权限
                        permission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }
                    if (ContextCompat.checkSelfPermission(activity,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        permission.add(Manifest.permission.CAMERA);
                    }
                    if (permission.size() > 0) {
                        // 请求权限
                        ActivityCompat.requestPermissions(activity, permission.toArray(new String[permission.size()]), TAKE_PIC);
                    } else {
                        // Toast.makeText(fragment.getContext(), "CAMERA",
                        // Toast.LENGTH_LONG).show();
                        takePic(activity);
                    }
                } else if (which == 1) {
                    if (ContextCompat.checkSelfPermission(activity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        // 请求权限
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                TAKE_HOTO);
                    } else {
                        choosePic(activity);
                    }

                }
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        if (onCancelListener != null) {
            dialog.setOnCancelListener(onCancelListener);
        }
        dialog.show();
    }

    public static void choosePic(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, TAKE_HOTO);
    }

    public static boolean selfPermissionGranted(Fragment fragment, String permission) {
        // For Android < Android M, self permissions are always granted.
        boolean result = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (getTargetSdkVersion(fragment.getActivity()) >= Build.VERSION_CODES.M) {
                // targetSdkVersion >= Android M, we can
                // use Context#checkSelfPermission
                result = ContextCompat.checkSelfPermission(fragment.getContext(),
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
            } else {
                // targetSdkVersion < Android M, we have to use
                // PermissionChecker
                result = PermissionChecker.checkSelfPermission(fragment.getContext(),
                        permission) == PermissionChecker.PERMISSION_GRANTED;
            }
        } else {
            result = PermissionChecker.checkSelfPermission(fragment.getContext(),
                    permission) == PermissionChecker.PERMISSION_GRANTED;
        }

        return result;
    }

    public static int getTargetSdkVersion(Context context) {
        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 21;
    }

    /**
     * 获取String值
     *
     * @param context
     * @param key          键
     * @param defaultValue 默认值
     * @return
     */
    private static String getPrefString(Context context, String key, final String defaultValue) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString(key, defaultValue);
    }

    /**
     * 保存String值
     *
     * @param context
     * @param key     键
     * @param value   值
     */
    private static void setPrefString(Context context, final String key, final String value) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings.edit().putString(key, value).commit();
    }
}
