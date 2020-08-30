package com.test.xcamera.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Set;

/**
 * Author: mz
 * Time:  2019/10/11
 */
public class Md5Util {
    /**
     * md5加密
     */
    public static String getMD5(String info) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes("UTF-8"));
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

    /**
     * 文件的MD5值
     *
     * @param path
     * @return
     */
    public static String getFileMD5(String path) {
        BigInteger bi = null;
        try {
            byte[] buffer = new byte[8192];
            int len = 0;
            MessageDigest md = MessageDigest.getInstance("MD5");
            File f = new File(path);
            FileInputStream fis = new FileInputStream(f);
            while ((len = fis.read(buffer)) != -1) {
                md.update(buffer, 0, len);
            }
            fis.close();
            byte[] b = md.digest();
            bi = new BigInteger(1, b);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bi.toString(16);
    }



    public static String sign(Map<String, String> param) {
        return md5(sortData(param));
    }

    private static String sortData(Map<String, String> data) {
        Set<Map.Entry<String, String>> entrySet = data.entrySet();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry e : entrySet) {
            String key = (String) e.getKey();
            String value = (String) e.getValue();
            if (value == null) continue;
            if (value.length() == 0) {
                continue;
            }
            sb.append("&").append(key).append("=").append(value);
        }

        sb.deleteCharAt(0);
        String sortData = sb.toString();
        return sortData;
    }

    public final static String md5(String data) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = data.getBytes("UTF-8");
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("md5");//MD5 ，SHA-1,SHA-256，SHA-384，SHA-512
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String sign(String str, String type){
        String s=Encrypt(str,type);
        return s;
    }
    public static String Encrypt(String strSrc, String encName) {
        MessageDigest md = null;
        String strDes = null;
        byte[] bt = strSrc.getBytes();
        try {
            md = MessageDigest.getInstance(encName);//MD5 ，SHA-1,SHA-256，SHA-384，SHA-512
            md.update(bt);
            strDes = bytes2Hex(md.digest()); // to HexString
        } catch (NoSuchAlgorithmException e) {
            System.out.println("签名失败！");
            return null;
        }
        return strDes;
    }
    public static String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des;
    }

    /**
     *
     * @param data
     * @param messageDigestName  MD5, SHA-1,SHA-256, SHA-384, SHA-512
     * @return
     */
    public static String messageDigest(String data, String messageDigestName){
        try {
            // Create MD5 Hash
            if(messageDigestName==null || "".equals(messageDigestName)){
                messageDigestName="MD5";
            }
            MessageDigest digest = MessageDigest.getInstance(messageDigestName);
            digest.update(data.getBytes());
            byte[] byteArr = digest.digest();
            return bytes2Hex(byteArr);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
