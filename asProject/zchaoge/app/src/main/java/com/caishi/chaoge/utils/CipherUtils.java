package com.caishi.chaoge.utils;

import android.util.Base64;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;

/**
 * Created by victorxie on 4/7/16.
 */
public class CipherUtils {

    public static final String ALGORITHM_MD5 = "MD5";

    public static final String ALGORITHM_SHA256 = "SHA256";

    public static final String ALGORITHM_RSA = "RSA";

    public static char[] toHexDigits(byte[] data) {

        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] result = new char[data.length * 2];
        int k = 0;
        for (int i = 0; i < data.length; i++) {
            byte b = data[i];
            result[k++] = hexDigits[b >> 4 & 0xf];
            result[k++] = hexDigits[b & 0xf];
        }
        return result;
    }

    public static String computeHashValue(byte[] data, String algorithm) {

        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);

            digest.update(data, 0, data.length);
            return new String(toHexDigits(digest.digest()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String computeHashValue(InputStream inputStream, String algorithm) {

        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = inputStream.read(bytes, 0, bytes.length)) > 0) {
                digest.update(bytes, 0, length);
            }
            return new String(toHexDigits(digest.digest()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String hashWithMD5(String data) {

        return computeHashValue(data.getBytes(), ALGORITHM_MD5);
    }

    public static String hashWithMD5(byte[] data) {

        return computeHashValue(data, ALGORITHM_MD5);
    }

    public static String hashWithMD5(InputStream inputStream) {

        return computeHashValue(inputStream, ALGORITHM_MD5);
    }

    public static String hashWithSHA256(String data) {

        return computeHashValue(data.getBytes(), ALGORITHM_SHA256);
    }

    public static String hashWithSHA256(byte[] data) {

        return computeHashValue(data, ALGORITHM_SHA256);
    }

    public static String hashWithSHA256(InputStream inputStream) {

        return computeHashValue(inputStream, ALGORITHM_SHA256);
    }

    public static String decryptWithRSA(byte[] data, InputStream keyStream) {

        try {
            final Key key = (Key) new ObjectInputStream(keyStream).readObject();
            // get an RSA cipher object
            final Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);

            // decrypt the text using the public key
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedText = cipher.doFinal(Base64.decode(data, Base64.DEFAULT));

            return new String(decryptedText);

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String encryptWithRSA(byte[] data, InputStream keyStream) {

        try {

            final Key key = (Key) new ObjectInputStream(keyStream).readObject();
            // get an RSA cipher object
            final Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);

            // encrypt the text using the private key
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedText = cipher.doFinal(data);

            return new String(Base64.encode(encryptedText, Base64.DEFAULT));

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String encryptWithRSA(byte[] data, byte[] key) {

        try {
            PublicKey publicKey = KeyFactory.getInstance(ALGORITHM_RSA).generatePublic(
                    new X509EncodedKeySpec(Base64.decode(key, Base64.DEFAULT)));
            // get an RSA cipher object
            final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

            // encrypt the text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedText = cipher.doFinal(data);

            return new String(Base64.encode(encryptedText, Base64.NO_WRAP | Base64.URL_SAFE));

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
}
