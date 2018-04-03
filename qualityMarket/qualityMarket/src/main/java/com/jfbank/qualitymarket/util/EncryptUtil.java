package com.jfbank.qualitymarket.util;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * @author cuihl
 */
public class EncryptUtil {
    public static String desEncrypt(String source, String key) {
        try {
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            return toHexString(cipher.doFinal(source.getBytes("UTF-8"))).toUpperCase();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * ����
     * @param source Դ
     * @param key  ��Կ
     * @return
     */
    public static String desDecrypt(String source, String key) {
        try {
            source = urlDecode(source, "utf-8");
            byte[] src = fromHexString(source);
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byte[] retByte = cipher.doFinal(src);
            return new String(retByte);
        } catch (Exception e) {
            return null;
        }
    }
    private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    public static String toHexString(byte[] buf) {
        return toHexString(buf, null, Integer.MAX_VALUE);
    }
    public static String toHexString(byte[] buf, String sep, int lineLen) {
        if (buf == null)
            return null;
        if (lineLen <= 0)
            lineLen = Integer.MAX_VALUE;
        StringBuilder res = new StringBuilder(buf.length * 2);
        for (int i = 0; i < buf.length; i++) {
            int b = buf[i];
            res.append(HEX_DIGITS[(b >> 4) & 0xf]);
            res.append(HEX_DIGITS[b & 0xf]);
            if (i > 0 && (i % lineLen) == 0)
                res.append('\n');
            else if (sep != null && i < lineLen - 1)
                res.append(sep);
        }
        return res.toString();
    }

    private static final int charToNibble(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        } else if (c >= 'a' && c <= 'f') {
            return 0xa + (c - 'a');
        } else if (c >= 'A' && c <= 'F') {
            return 0xA + (c - 'A');
        } else {
            return -1;
        }
    }

    public static byte[] fromHexString(String text) {
        text = text.trim();
        if (text.length() % 2 != 0)
            text = "0" + text;
        int resLen = text.length() / 2;
        int loNibble, hiNibble;
        byte[] res = new byte[resLen];
        for (int i = 0; i < resLen; i++) {
            int j = i << 1;
            hiNibble = charToNibble(text.charAt(j));
            loNibble = charToNibble(text.charAt(j + 1));
            if (loNibble == -1 || hiNibble == -1)
                return null;
            res[i] = (byte) (hiNibble << 4 | loNibble);
        }
        return res;
    }


    public static String urlDecode(String content, String charset) {
        try {
            if (content != null) {
                return URLDecoder.decode(content, charset);
            } else {
                return "";
            }
        } catch (UnsupportedEncodingException ex) {
            return content;
        }
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println(EncryptUtil.desEncrypt("VipPost", "12345678"));
        System.out.println(EncryptUtil.desDecrypt("C9982369FF9300B5", "12345678"));
//        System.out.println(EncryptUtil.desDecrypt(EncryptUtil.desEncrypt("VipPost", "12345678"), "12345678"));
    }
}
