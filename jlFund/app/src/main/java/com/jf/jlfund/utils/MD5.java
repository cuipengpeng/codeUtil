package com.jf.jlfund.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Set;


/**
 * 加密类
 */
public class MD5 {


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
}