package com.downloader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
        for (Map.Entry<String, String> e : entrySet) {
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
            byte[] byteArr = mdInst.digest();
            
            // 把密文转换成十六进制的字符串形式
            int j = byteArr.length;
            char[] charArr = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = byteArr[i];
                charArr[k++] = hexDigits[byte0 >>> 4 & 0xf];
                charArr[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(charArr).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
	
	/**
     * 把密文转换成十六进制的字符串形式
     * @param btArray
     * @return
     */
    private static String bytes2Hex(byte[] btArray) {
    	StringBuilder hexString = new StringBuilder();
        for (byte b: btArray) {
//			//test
//			int tmp1 = b;
//			int tmp2 = 0xff & b;
//			System.out.println("tmp1 = "+tmp1);
//			System.out.println("tmp2 = "+tmp2);
        	
        	String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
            	hex = "0"+hex;
            }
            hexString.append(hex);
        }
        return hexString.toString().toLowerCase();
    }
	
	  /**
     * <p>
     * 文件转换为二进制数组
     * </p>
     * 
     * @param filePath 文件路径
     * @return
     * @throws Exception
     */
    public static byte[] fileToByte(String filePath) throws Exception {
        byte[] data = new byte[0];
        File file = new File(filePath);
        if (file.exists()) {
            FileInputStream in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
            byte[] cache = new byte[1024];
            int nRead = 0;
            while ((nRead = in.read(cache)) != -1) {
                out.write(cache, 0, nRead);
                out.flush();
            }
            out.close();
            in.close();
            data = out.toByteArray();
         }
        return data;
    }
    
    /**
     * <p>
     * 二进制数据写文件
     * </p>
     * 
     * @param bytes 二进制数据
     * @param filePath 文件生成目录
     */
    public static void byteToFile(byte[] bytes, String filePath) throws Exception {
        InputStream in = new ByteArrayInputStream(bytes);   
        File destFile = new File(filePath);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        destFile.createNewFile();
        OutputStream out = new FileOutputStream(destFile);
        byte[] cache = new byte[1024];
        int nRead = 0;
        while ((nRead = in.read(cache)) != -1) {   
            out.write(cache, 0, nRead);
            out.flush();
        }
        out.close();
        in.close();
    }
}