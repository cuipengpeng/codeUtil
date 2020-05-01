package com.jfbank.qualitymarket.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * MD5 密码加密
 * 
 * @author 贵平
 * 
 */
public class Md5 {
	private static final String TAG = Md5.class.getName();

	public static String md5(String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(s.getBytes());
			byte bytes[] = digest.digest();

			// Create Hex String
			StringBuilder hexString = new StringBuilder();
			for (byte b : bytes) {
				String hex = Integer.toHexString(0xFF & b);
				if (hex.length() == 1)
					hex = "0" + hex;
				hexString.append(hex);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static String sha1(String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("SHA-1");
			digest.update(s.getBytes());
			byte bytes[] = digest.digest();
			
			// Create Hex String
			StringBuilder hexString = new StringBuilder();
			for (byte b : bytes) {
				String hex = Integer.toHexString(0xFF & b);
				if (hex.length() == 1)
					hex = "0" + hex;
				hexString.append(hex);
			}
			return hexString.toString();
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static byte[] aesDecode(byte[] raw, byte[] encrypted)
			throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		// Cipher cipher = Cipher.getParamsInstance("AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] decrypted = cipher.doFinal(encrypted);
		return decrypted;
	}
}
