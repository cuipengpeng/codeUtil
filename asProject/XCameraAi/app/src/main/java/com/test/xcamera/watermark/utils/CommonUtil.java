package com.test.xcamera.watermark.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.text.TextUtils;

import com.test.xcamera.utils.Constants;
import com.test.xcamera.watermark.info.LocalVideoInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CommonUtil {
    private String dirPath = Environment.getExternalStorageDirectory().getPath();

    //获取VideoPath
    public static String getPath(String path, String fileName) {
        String p = getBaseFolder(ContextRef.getInstance().getContext()) + path;
        File f = new File(p);
        if (!f.exists() && !f.mkdirs()) {
            return getBaseFolder(ContextRef.getInstance().getContext()) + fileName;
        }
        return p + fileName;
    }

    public static String getBaseFolder(Context context) {
        String baseFolder = Environment.getExternalStorageDirectory() + "/DCIM/Camera/";
        File f = new File(baseFolder);
        if (!f.exists()) {
            boolean b = f.mkdirs();
            if (!b) {
                baseFolder = context.getApplicationContext().getExternalFilesDir(null).getAbsolutePath() + "/";
            }
        }
        return baseFolder;
    }

    //通过资源路径加载shader脚本文件
    public static String uRes(String path) {
        Resources resources = ContextRef.getInstance().getContext().getResources();
        StringBuilder result = new StringBuilder();
        try {
            InputStream is = resources.getAssets().open(path);
            int ch;
            byte[] buffer = new byte[1024];
            while (-1 != (ch = is.read(buffer))) {
                result.append(new String(buffer, 0, ch));
            }
        } catch (Exception e) {
            return null;
        }
        return result.toString().replaceAll("\\r\\n", "\n");
    }

    public static LocalVideoInfo getLocalVideoInfo(String videoPath) {
        LocalVideoInfo info = new LocalVideoInfo();
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(videoPath);
            int duration = Integer.parseInt(mmr.extractMetadata
                    (MediaMetadataRetriever.METADATA_KEY_DURATION));
            int width = Integer.parseInt(mmr.extractMetadata
                    (MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            int height = Integer.parseInt(mmr.extractMetadata
                    (MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            int rotation = Integer.parseInt(mmr.extractMetadata
                    (MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
            int bitrate = Integer.parseInt(mmr.extractMetadata
                    (MediaMetadataRetriever.METADATA_KEY_BITRATE));

//            int frameCount = Integer.parseInt(mmr.extractMetadata
//                    (MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT));

            //时长(毫秒)
            //String duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
            //宽
//            String width = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            //高
//            String height = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            info.setDuration(duration);
            info.setWidth(width);
            info.setHeight(height);
            info.setRotation(rotation);
            info.setBitrate(bitrate);
        } catch (Exception e) {
            e.printStackTrace();
            return info;
        }
        return info;
    }

    /**
     * 复制文件到本地
     *
     * @param context
     * @param assetsDir
     */
    public static void copyAssets(Context context, String assetsDir) {
        String outputDir = Constants.markPngAndVideo;
        AssetManager assets = context.getAssets();
        String sep = File.separator;
        try {
            String[] fileNames = assets.list(assetsDir);//只能获取到文件(夹)名,所以还得判断是文件夹还是文件
            if (fileNames != null && fileNames.length > 0) {// is dir
                checkFolderExists(outputDir + sep + assetsDir);
                for (String name : fileNames) {
                    if (!TextUtils.isEmpty(assetsDir)) {
                        name = assetsDir + File.separator + name;//补全assets资源路径
                    }
                    String[] childNames = assets.list(name);//判断是文件还是文件夹
                    if (!TextUtils.isEmpty(name) && childNames != null && childNames.length > 0) {
                        checkFolderExists(outputDir + sep + name);
                        copyAssets(context, name);//递归, 因为资源都是带着全路径,
                        //所以不需要在递归是设置目标文件夹的路径
                    } else {
                        InputStream is = assets.open(name);
                        writeFile(outputDir + sep + name, is);
                    }
                }
            } else {// is file
                InputStream is = assets.open(assetsDir);
                // 写入文件前, 需要提前级联创建好路径, 下面有代码贴出
                writeFile(outputDir + sep + assetsDir, is);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeFile(String fileName, InputStream in) throws IOException {
        try {
            OutputStream os = new FileOutputStream(fileName);
            byte[] buffer = new byte[4112];
            int read;
            while ((read = in.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
            in.close();
            in = null;
            os.flush();
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void checkFolderExists(String path) {
        File file = new File(path);
        if ((file.exists() && !file.isDirectory()) || !file.exists()) {
            file.mkdirs();
        }
    }

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return "";
        }
        int maxLen = 150 * 1024 * 1024;
        MessageDigest digest = null;
        boolean isTooLarge = file.length() > maxLen;
        FileInputStream in = null;
        byte[] buffer = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            int readLength = 0;
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                if (isTooLarge) {
                    if (readLength <= maxLen) {
                        digest.update(buffer, 0, len);
                    } else {
                        break;
                    }
                } else {
                    digest.update(buffer, 0, len);
                }
                readLength += len;
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    public static String getMD5(String str) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(str.getBytes("UTF-8"));
            byte[] encryption = md5.digest();

            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < encryption.length; i++) {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
                    strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
                } else {
                    strBuf.append(Integer.toHexString(0xff & encryption[i]));
                }
            }
            return strBuf.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

}
