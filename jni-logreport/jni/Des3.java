package com.jfbank.qualitymarket.util;
import java.security.Key;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
/**
 * Created by cuihongliang on 16/11/1.
 */
public class Des3 {

    // 密钥
    private final static String secretKey = "pinZhiShangcheng.20160932";
    // 向量
    private final static String iv = "01234567";
    // 加解密统一使用的编码方式
    private final static String encoding = "utf-8";

    /**
     * 3DES鍔犲瘑
     *
     * @param plainText 鏅?氭枃鏈?
     * @return
     * @throws Exception
     */
    public static String encode(String plainText) throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);

        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
        byte[] encryptData = cipher.doFinal(plainText.getBytes(encoding));
//        return Base64.encode(encryptData);
        return parseByte2HexStr(encryptData);
    }

    /**
     * 3DES瑙ｅ瘑
     *
     * @param encryptText 鍔犲瘑鏂囨湰
     * @return
     * @throws Exception
     */
    public static String decode(String encryptText) throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, deskey, ips);

//        byte[] decryptData = cipher.doFinal(Base64.decode(encryptText));
        byte[] decryptData = cipher.doFinal(parseHexStr2Byte(encryptText));

        return new String(decryptData, encoding);
    }

    /**将二进制转换成16进制
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**将16进制转换为二进制
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length()/2];
        for (int i = 0;i< hexStr.length()/2; i++) {
            result[i] = Integer.valueOf(hexStr.substring(2*i, 2*i+2), 16).byteValue();
//                    int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);
//                    int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);
//                    result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
    /**
     *  对请求参数进行签名
     *
     *  签名按请求参数的key进行排序，忽略大小写。
     *  空value值不加入签名
     *
     * @param content 待签名的字符串
     * @return 签名后的内容
     */
    public static String signContent(String content){
        List<String> nonNullValueParameterList = new ArrayList<String>();

        String[] parameterArray = content.split("&");
        for(int i=0;i<parameterArray.length; i++){
            //请求参数按=分割
            String[] tmpArray = parameterArray[i].split(ConstantsUtil.SYMBOL_EQUAL);
            //参数中value为空的不加入签名
            if(tmpArray.length>1 && tmpArray[1] != "" && tmpArray[1] != null){
                nonNullValueParameterList.add(parameterArray[i]);
            }
        }

        Collections.sort(nonNullValueParameterList, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.compareTo(rhs);
            }
        });

        StringBuffer stringBuffer = new StringBuffer();
        for(int i=0; i<nonNullValueParameterList.size();i++){
            stringBuffer.append(nonNullValueParameterList.get(i)).append(ConstantsUtil.SIGNED_SEPETATOR);
        }

        String paramString = stringBuffer.toString();
        if(paramString.endsWith(ConstantsUtil.SIGNED_SEPETATOR)){
            paramString = stringBuffer.substring(0, stringBuffer.length()-1);
        }

        LogUtil.printLog("待签名的内容："+paramString);

        return Md5.md5(paramString);
    }
}
