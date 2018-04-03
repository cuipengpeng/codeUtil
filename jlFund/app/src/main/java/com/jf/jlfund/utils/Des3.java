package com.jf.jlfund.utils;


import java.security.Key;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class Des3 {

    // 密钥
    private final static String secretKey = "secretKeyForMima.11223344"; //(112位或168位即14字节或21字节)
    // 向量
    private final static String iv = "01234567";
    // 加解密统一使用的编码方式
    private final static String encoding = "utf-8";

    /**
     * 3DES加密
     *
     * @param plainText 普通文本
     * @return
     * @throws Exception
     */
    public static String encode(String plainText) throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");//对称加密算法。可选aes，des，desede
        deskey = keyfactory.generateSecret(spec);

        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
        byte[] encryptData = cipher.doFinal(plainText.getBytes(encoding));
        return Base64.encode(encryptData);
    }

    /**
     * 3DES解密
     *
     * @param encryptText 加密文本
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

        byte[] decryptData = cipher.doFinal(Base64.decode(encryptText));

        return new String(decryptData, encoding);
    }

//    public static void main(String[] args)throws Exception{
//        String e=encode("18511730532");
//        System.out.println(e);
//
//        String d=decode(e);
//        System.out.println(d);
//    }


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

        LogUtils.printLog("待签名的内容："+paramString);

        return "";//md5.EncoderByMd5(paramString);
    }
}
