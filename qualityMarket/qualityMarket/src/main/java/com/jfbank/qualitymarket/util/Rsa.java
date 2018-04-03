
package com.jfbank.qualitymarket.util;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA工具类
 * 
 */
public class Rsa {

	public static final String SIGN_ALGORITHMS = "MD5WithRSA";

	public static String sign(String content, String privateKey) {
		String charset = "utf-8";
		try {
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
					Base64.decode(privateKey));
			KeyFactory keyf = KeyFactory.getInstance("RSA");
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);

			java.security.Signature signature = java.security.Signature
					.getInstance(SIGN_ALGORITHMS);

			signature.initSign(priKey);
			signature.update(content.getBytes(charset));

			byte[] signed = signature.sign();

			return Base64.encode(signed);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void testFlow() {
		//获取signature 对象，初始化算法：MD2withRSA, MD5withRSA, or SHA1withRSA
		Signature signature = Signature.getInstance("MD5withRSA");
		//创建私钥（从磁盘上读取）
		PrivateKey privateKey = (PrivateKey)SerializableUtil.readObject("tt.privateKey");
		//使用私钥进行初始化
		signature.initSign(privateKey);
		//传入需要签名的数据
		signature.update(content.getBytes());
		//执行签名
		byte[] sign = signature.sign();

		//创建公钥（从磁盘上读取）
		PublicKey publicKey = (PublicKey) SerializableUtil.readObject("tt.publicKey");
		//使用公钥进行初始化
		signature.initVerify(publicKey);
		//传入需要校验的数据（即上面的原文）
		signature.update(content.getBytes());
		//执行校验
		boolean verify = signature.verify(sign);
	}

	public static boolean doCheck(String content, String sign, String publicKey) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			byte[] encodedKey = Base64.decode(publicKey);
			PublicKey pubKey = keyFactory
					.generatePublic(new X509EncodedKeySpec(encodedKey));

			java.security.Signature signature = java.security.Signature
					.getInstance(SIGN_ALGORITHMS);

			signature.initVerify(pubKey);
			signature.update(content.getBytes("utf-8"));

			boolean bverify = signature.verify(Base64.decode(sign));
			return bverify;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}
