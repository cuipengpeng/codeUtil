package com.android.player.utils.encrypt;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Aes {


    private final static String secretKey = "Px0D34a7VnT6sqkn";//(128、192、256位即16字节，24字节，32字节)

    private final static String jinniuKey = "z";

    private final static String encoding = "UTF-8";

    private static final String IV_STRING = "16-Bytes--String";

    /**
     * 输出结果:
     * 加密内容： 早上好abcd123456
     * AES加密结果： 0F4Af4e/wAcXlNvQ+oiyrrVLqOlyNy0/BpGUBH2YS4M=      AES解密结果： 早上好abcd123456
     * MD5加密结果： 386e87adb150c94e1ff0f5b2bda87f54
     *
     * @param args
     */
    public static void main(String[] args) {
        String content = "早上好abcd123456";
        String aesResult;
        try {
            aesResult = encryptAES(content);
            System.out.println("加密内容： " + content);
            System.out.println("AES加密结果： " + encryptAES(content) + "      AES解密结果： " + decryptAES(aesResult));
            System.out.println("MD5加密结果： " + MD5.md5(aesResult));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加密
     *
     * @param content
     * @return
     */
    public static String encryptAES(String content) {
        if ("".equals(content)|| content == null) {
            return "";
        }
        try {
            byte[] byteContent = content.getBytes(encoding);
            byte[] enCodeFormat = secretKey.getBytes();
            SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
            byte[] initParam = IV_STRING.getBytes();
            IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encryptedBytes = cipher.doFinal(byteContent);
            String retString = base64Encode(encryptedBytes);
            retString = retString.replaceAll("\r\n", "");
            retString = retString.replaceAll("\r", "");
            retString = retString.replaceAll("\n", "");
            return retString;
        } catch (Exception e) {
        	System.out.println(e.getMessage());
        }
        return "";
    }

    /**
     * 加密
     *
     * @param content
     * @return
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws UnsupportedEncodingException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static String jinNiuEncryptAES(String content) throws InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException,
            UnsupportedEncodingException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException {
        byte[] byteContent = content.getBytes(encoding);
        byte[] enCodeFormat = jinniuKey.getBytes();
        SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
        byte[] initParam = IV_STRING.getBytes();
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encryptedBytes = cipher.doFinal(byteContent);
        String retString = base64Encode(encryptedBytes);
        retString = retString.replaceAll("\r\n", "");
        retString = retString.replaceAll("\r", "");
        retString = retString.replaceAll("\n", "");
        return retString;
    }

    /**
     * 解密
     *
     * @param content
     * @return
     * @throws Exception
     */
    public static String decryptAES(String content) {
        try {
            byte[] encryptedBytes = base64Decode(content);
            byte[] enCodeFormat = secretKey.getBytes();
            SecretKeySpec secretKey = new SecretKeySpec(enCodeFormat, "AES");
            byte[] initParam = IV_STRING.getBytes();
            IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            byte[] result = cipher.doFinal(encryptedBytes);
            return new String(result, encoding);
        } catch (Exception e) {
        	System.out.println(e.getMessage());
        }
        return "";
    }


    /**
     * base 64 encode
     *
     * @param bytes 待编码的byte[]
     * @return 编码后的base 64 code
     */
    public static String base64Encode(byte[] bytes) {
        return Base64.encode(bytes);
    }

    /**
     * base 64 decode
     *
     * @param base64Code 待解码的base 64 code
     * @return 解码后的byte[]
     * @throws Exception
     */
    public static byte[] base64Decode(String base64Code) throws Exception {
        byte[] retByte = base64Code.isEmpty() ? null : Base64.decode(base64Code);
        return retByte;
    }

}