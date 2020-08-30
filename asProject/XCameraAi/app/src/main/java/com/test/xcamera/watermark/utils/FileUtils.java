package com.test.xcamera.watermark.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FileUtils {

    public final static String ROOT_DIR = Environment.getExternalStorageDirectory().getPath() + "/RecordDemo/";
    public final static String RESOURCE_DIR = ROOT_DIR + "resource/";
    public final static String STICKER_RESOURCE_DIR = RESOURCE_DIR + "stickers/";
    public final static String MODELS_DIR = ROOT_DIR + "models/";
    public final static String VIDEO_TMP = ROOT_DIR + "tmp/";
    public final static String CONCAT_VIDEO_DIR = ROOT_DIR + "video/";
    public final static String CACHE = ROOT_DIR + "/cache/";
    public final static String MUSIC = ROOT_DIR + "music/";
    public final static String DOC = ROOT_DIR + "doc/";
//    public final static String AR_DIR = STICKER_RESOURCE_DIR + "ARFace/";

    public final static String MUSIC_EFFECT_DIR = RESOURCE_DIR;
    public final static String FILTER_DIR = RESOURCE_DIR + "/filter/";
    public final static String FILTER = FILTER_DIR + "Filter_02";
    public final static String BEAUTY_12_DIR = RESOURCE_DIR + "/Beauty_12/";
    public final static String FACE_RESHAP_DIR = RESOURCE_DIR + "/FaceReshape_V2/";
    public final static String BEAUTY_12_FILENAME = "Beauty_12.zip";

    public final static String FACE_RESHAPE = "FaceReshape_V2.zip";

    public final static String RESHAPE_DIR_NAME = RESOURCE_DIR;

    public final static String SUFFIX = ".zip";

    public static List<String> ResourcesList = new ArrayList<>();

    public final static String WATERMARK_ASSETT_FILENAME = "watermark.zip";

    public final static String WATERMARK_DIR = RESOURCE_DIR + "watermark/";

    public static String WatermarkBgMask = "watermark_bg.png";

    public final static String PHONEPARAM = "phoneParams.txt";

    static {
        ResourcesList.add("2D_angel");
        ResourcesList.add("2D_bubble");
        ResourcesList.add("E12_3D_Glass_Crystal_a02");
        ResourcesList.add("E12_3D_hat_fj_a01_155");
        ResourcesList.add("E12_D_Glass_Diamond_a01");
        ResourcesList.add("E12_D_glasses_cobain_a02");
        ResourcesList.add("05311139_wuyun");
        ResourcesList.add("05311201_夜景星空");
        ResourcesList.add("05311202_粉色小猫咪");
        ResourcesList.add("ARFace");
    }


    public final static List<String> musicList = new ArrayList<>();
    static {
        musicList.add("music00001.mp3");
        musicList.add("music00002.mp3");
        musicList.add("music00003.mp3");
        musicList.add("music00004.mp3");
    }

    public static List<String> WatermarkList = new ArrayList<>();

    static {
        for (int i = 1; i <= 5; i++) {
            WatermarkList.add("watermark" + i+".png");
        }
        for (int i = 1; i <= 5; i++) {
            WatermarkList.add("watermark2" + i+".png");
        }
    }


    public static void copyFile(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }



    public static boolean copyFileIfNeed(Context context, String dir, String fileName) {
       return copyFileIfNeed(context, dir, fileName, fileName);
    }

    public static boolean copyFileIfNeed(Context context, String dir, String assetPath, String fileName) {
        File dirF = new File(dir);
        if(!dirF.exists()){
            dirF.mkdirs();
        }
        String path = dir + fileName;

        if (!path.isEmpty()) {
            File file = new File(path);
            if (!file.exists()) {
                try {

                    file.createNewFile();
                    InputStream in = context.getApplicationContext().getAssets().open(assetPath);
                    if(in == null)
                    {
                        return false;
                    }
                    OutputStream out = new FileOutputStream(file);
                    byte[] buffer = new byte[4096];
                    int n;
                    while ((n = in.read(buffer)) > 0) {
                        out.write(buffer, 0, n);
                    }
                    in.close();
                    out.close();
                } catch (IOException e) {
                    file.delete();
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean copyAssetsDirectory(Context context, String pathOfAssets, String destDir) {
        File dir = new File(destDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        AssetManager assets = context.getAssets();
        try {
            if (pathOfAssets.endsWith("/")) {
                pathOfAssets = pathOfAssets.substring(0, pathOfAssets.length() - 1);
            }
            String[] list = assets.list(pathOfAssets);
            for (String s : list) {
                String fileName = pathOfAssets + File.separator + s;
                String destDir1 = destDir + File.separator + s;
                try {
                    InputStream open = assets.open(fileName);
                    copyFileIfNeed(context, destDir + File.separator, fileName, s);
                    open.close();
                } catch (FileNotFoundException ex) {
                    // 不是文件，就是文件夹
                    copyAssetsDirectory(context, fileName, destDir1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e("FileUtil", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINESE).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    public static boolean makeDir(String dirPath) {
        if (dirPath.isEmpty()) {
            return false;
        }
        File dir = new File(dirPath);
        if(!dir.exists()){
            return dir.mkdirs();
        }
        return false;
    }

    public static boolean delDir(String dirPath) {
        if (dirPath.isEmpty()) {
            return false;
        }
        File dir = new File(dirPath);
        return !dir.isFile() && dir.exists() && deleteDir(dir);
    }


    /**
     * 删除空目录
     * @param dir 将要删除的目录路径
     */
    private static void doDeleteEmptyDir(String dir) {
        boolean success = (new File(dir)).delete();
        if (success) {
            System.out.println("Successfully deleted empty directory: " + dir);
        } else {
            System.out.println("Failed to delete empty directory: " + dir);
        }
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     *                 If a deletion fails, the method stops attempting to
     *                 delete and returns "false".
     */
    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    private static boolean hasParentDir(InputStream inputStream) throws IOException {
        java.util.zip.ZipInputStream inZip = new java.util.zip.ZipInputStream(inputStream);
        java.util.zip.ZipEntry zipEntry;
        String szName;
        boolean hasParentDir = true;
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if(szName.equals("config.json"))
                hasParentDir=false;
        }
        inZip.close();
        return hasParentDir;
    }

    private static void UnZipFolder(InputStream inputStream, String outDirName) throws Exception {

        java.util.zip.ZipEntry zipEntry;
        String szName = "";


        java.util.zip.ZipInputStream inZip = new java.util.zip.ZipInputStream(inputStream);

        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();

            if (zipEntry.isDirectory()) {

                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outDirName + File.separator + szName);
                if(folder.exists()){
                    continue;
                }
                folder.mkdirs();
            } else {
                File file = new File(outDirName + File.separator + szName);
                if(file.exists()){
                    break;
                }
                file.createNewFile();
                // get the output stream of the file
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                // read (len) bytes into buffer
                while ((len = inZip.read(buffer)) != -1) {
                    // write (len) byte from buffer at the position 0
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }//end of while

        inZip.close();
    }

    public static void UnZipAssetFolder(Context context, String assetFileName, String outDirName) throws Exception {
        InputStream in = context.getAssets().open(assetFileName);
        File dirFile = new File(outDirName);
        if (dirFile.exists()) {
            if (dirFile.isFile()) {
                dirFile.delete();
                dirFile.mkdirs();
            }
        } else {
            dirFile.mkdirs();
        }
        if(true){
            File folder = new File(outDirName + File.separator + GetFileName(assetFileName));
            if(folder.exists()){
                deleteDir(folder);
            }

            folder.mkdirs();
            outDirName +=  File.separator + GetFileName(assetFileName);
        }
        in.close();
        in = context.getAssets().open(assetFileName);

        UnZipFolder(in, outDirName);
    }

    public static int UnZipFileFolder(String filePath, String outDirName) throws Exception {
        InputStream in = new FileInputStream(new File(filePath));
        File dirFile = new File(outDirName);
        if (dirFile.exists()) {
            if (dirFile.isFile()) {
                dirFile.delete();
                dirFile.mkdirs();
            }
        } else {
            dirFile.mkdirs();
        }
        if(true){
            File folder = new File(outDirName + File.separator + GetFileName(filePath));
            if(folder.exists()){
                deleteDir(folder);
            }

            folder.mkdirs();
            outDirName += File.separator + GetFileName(filePath);
        }
        in.close();
        in =  new FileInputStream(new File(filePath));
        UnZipFolder(in, outDirName);
        return 0;
    }


    public static String GetFileName(String pathandname){

        int start=pathandname.lastIndexOf("/");
        int end=pathandname.lastIndexOf(".");

        if (end != -1) {
            return pathandname.substring(start + 1, end);
        } else{
            return null;
        }

    }

    public static synchronized String createtFileName(String prefix, String suffix) {
        Date dt = new Date(System.currentTimeMillis());
        SimpleDateFormat fmt = new SimpleDateFormat("MMddHHmmssSSS");
        String fileName= fmt.format(dt);
        fileName = prefix + fileName + '.'+suffix;
        return fileName;
    }

    public static void makeSureNoMedia(String dir) {
        try {
            new File(dir, ".nomedia").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
