/**
 * 文件名：FileImagePath.java
 * 全路径：com.jfbank.qualitymarket.util.FileImagePath
 */
package com.test.bank.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 功能：文件工具类<br>
 *
 */
public class FileImagePath {
	/**
	 * 需要知道当前SD卡的目录，Environment.getExternalStorageDierctory()
	 */


	private String SDPATH;


	public String getSDPATH() {
		return SDPATH;
	}


	public FileImagePath() { // 目录名/sdcard
		SDPATH = Environment.getExternalStorageDirectory() + "/com.jf.jlfund/";
	}


	// 在sdcard卡上创建文件
	public File createSDFile(String fileName) throws IOException {
		File file = new File(SDPATH + fileName);
		file.createNewFile();
		return file;
	}


	// 在sd卡上创建目录
	public File createSDDir(String dirName) {
		File dir = new File(SDPATH + dirName);
// mkdir只能创建一级目录 ,mkdirs可以创建多级目录
		dir.mkdir();
		return dir;
	}


	// 判断sd卡上的文件夹是否存在
	public boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		return file.exists();
	}
	public String getFilePath(String fileName) {
		File file = new File(SDPATH + fileName);
		return file.getPath();
	}

	public void deleteFile(String fileName) {
		File file = new File(SDPATH + fileName);
		file.delete();
	}


	/**
	 * 将一个inputstream里面的数据写入SD卡中 第一个参数为目录名 第二个参数为文件名
	 */
	public File write2SDFromInput(String path, InputStream inputstream) {
		File file = null;
		OutputStream output = null;
		try {
			file = createSDFile(path);
			output = new FileOutputStream(file);
// 4k为单位，每4K写一次
			byte buffer[] = new byte[4 * 1024];
			int temp = 0;
			while ((temp = inputstream.read(buffer)) != -1) {
// 获取指定信,防止写入没用的信息
				output.write(buffer, 0, temp);
			}
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	 /**
	 * Method for return file path of Gallery image
	 *
	 * @param context
	 * @param uri
	 * @return path of the selected image file from gallery
	 */
	 @RequiresApi(api = Build.VERSION_CODES.KITKAT)
	 public static String getPath(final Context context, final Uri uri) {

	    // check here to KITKAT or new version
	    final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

	    // DocumentProvider
	    if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

	        // ExternalStorageProvider
	        if (isExternalStorageDocument(uri)) {
	            final String docId = DocumentsContract.getDocumentId(uri);
	            final String[] split = docId.split(":");
	            final String type = split[0];

	            if ("primary".equalsIgnoreCase(type)) {
	                return Environment.getExternalStorageDirectory() + "/"
	                        + split[1];
	            }
	        }
	        // DownloadsProvider
	        else if (isDownloadsDocument(uri)) {

	            final String id = DocumentsContract.getDocumentId(uri);
	            final Uri contentUri = ContentUris.withAppendedId(
	                    Uri.parse("content://downloads/public_downloads"),
	                    Long.valueOf(id));

	            return getDataColumn(context, contentUri, null, null);
	        }
	        // MediaProvider
	        else if (isMediaDocument(uri)) {
	            final String docId = DocumentsContract.getDocumentId(uri);
	            final String[] split = docId.split(":");
	            final String type = split[0];

	            Uri contentUri = null;
	            if ("image".equals(type)) {
	                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	            } else if ("video".equals(type)) {
	                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
	            } else if ("audio".equals(type)) {
	                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	            }

	            final String selection = "_id=?";
	            final String[] selectionArgs = new String[] { split[1] };

	            return getDataColumn(context, contentUri, selection,
	                    selectionArgs);
	        }
	    }
	    // MediaStore (and general)
	    else if ("content".equalsIgnoreCase(uri.getScheme())) {

	        // Return the remote address
	        if (isGooglePhotosUri(uri))
	            return uri.getLastPathSegment();

	        return getDataColumn(context, uri, null, null);
	    }
	    // File
	    else if ("file".equalsIgnoreCase(uri.getScheme())) {
	        return uri.getPath();
	    }

	    return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @param selection
	 *            (Optional) Filter used in the query.
	 * @param selectionArgs
	 *            (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically CircleTransform file path.
	 */
	public static String getDataColumn(Context context, Uri uri,
									   String selection, String[] selectionArgs) {

	    Cursor cursor = null;
	    final String column = "_data";
	    final String[] projection = { column };

	    try {
	        cursor = context.getContentResolver().query(uri, projection,
	                selection, selectionArgs, null);
	        if (cursor != null && cursor.moveToFirst()) {
	            final int index = cursor.getColumnIndexOrThrow(column);
	            return cursor.getString(index);
	        }
	    } finally {
	        if (cursor != null)
	            cursor.close();
	    }
	    return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
	    return "com.android.externalstorage.documents".equals(uri
	            .getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
	    return "com.android.providers.downloads.documents".equals(uri
	            .getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
	    return "com.android.providers.media.documents".equals(uri
	            .getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
	    return "com.google.android.apps.photos.content".equals(uri
	            .getAuthority());
	}
}
