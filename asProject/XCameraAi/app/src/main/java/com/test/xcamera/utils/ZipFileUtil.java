package com.test.xcamera.utils;

import android.annotation.SuppressLint;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipFileUtil {
  /**
   * 解压到指定目录
   */
  public static void unZipFiles(String zipPath,String descDir) {
    unZipFiles(new File(zipPath), descDir);
  }
  /**
   * 解压文件到指定目录
   */
  @SuppressLint("NewApi")
  public static void unZipFiles(File zipFile,String descDir) {
    File pathFile = new File(descDir);
    if(!pathFile.exists())
    {
      pathFile.mkdirs();
    }
    try {
      //解决zip文件中有中文目录或者中文文件
      String fileEncode = EncodeUtil.getEncode(zipFile.getAbsolutePath(),true);
       ZipFile zip = new ZipFile(zipFile, Charset.forName(fileEncode));
      for(Enumeration entries = zip.entries(); entries.hasMoreElements();) {
        ZipEntry entry = (ZipEntry)entries.nextElement();
        String zipEntryName = entry.getName();
        InputStream in = zip.getInputStream(entry);
        String outPath = (descDir+"/"+zipEntryName).replaceAll("\\*", "/");
          //判断路径是否存在,不存在则创建文件路径
        File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
        if(!file.exists())
        {
          file.mkdirs();
        }
        //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
        if(new File(outPath).isDirectory())
        {
          continue;
        }
        //输出文件路径信息
        System.out.println(outPath);
        OutputStream out = new FileOutputStream(outPath);
        byte[] buf1 = new byte[1024];
        int len;
        while((len=in.read(buf1))>0)
        {
          out.write(buf1,0,len);
        }
        in.close();
        out.close();
      }

    }catch (Exception e){
      e.printStackTrace();
    }
    System.out.println("******************解压完毕********************");
  }
//  public static void main(String[] args) throws IOException {
//    /**
//     * 解压文件
//     */
//    File zipFile = new File("d:/资料.zip");
//    String path = "d:/zipfile/";
//    unZipFiles(zipFile, path);
//  }
}