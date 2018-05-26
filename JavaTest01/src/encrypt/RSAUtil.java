package encrypt;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PKCS12Attribute;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;


public class RSAUtil {
	private static final String transformation = "RSA/ECB/PKCS1Padding";
	//RSA加密明文大小(117)
	public static final int MAX_ENCRYPT_BLOCK = 117;
	//获取公钥的key
	public static final String PUBLIC_KEY = "RSAPublicKey";
	// 获取私钥的key
	public static final String PRIVATE_KEY = "RSAPrivateKey";
	//签名算法
	public static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
	//缓存密钥
	private static final Map<String, Object> keyMap = new HashMap<String, Object>();

	
	public static void main(String[] args) {
		String strModel = "123333333333334534254";
		
//		PublicKey publicKey = RSAUtil.getPublicKey("MIIB7TCCAVagAwIBAgIQO9/ktLH3p61McDiYKTrLdjANBgkqhkiG9w0BAQQFADARMQ8wDQYDVQQDEwY5ZmJhbmswHhcNMTExMjMxMTYwMDAwWhcNMTkxMjMxMTYwMDAwWjARMQ8wDQYDVQQDEwY5ZmJhbmswgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBANuqa7U2mm5/afsgi9HkDJw44HSr0A/jaZnWLoNZaBveQ95conmlYBTHB+8o+7u+FDwZOI0TwIgPJ5k4iU63/nKvqqc83H2zh7ATA3FzIMQlaXT1vxlQGenbECFuZedF5FYbXiU4n32XAUAb0KZOzANXSZuKgiDFp9NxcE7El9DVAgMBAAGjRjBEMEIGA1UdAQQ7MDmAENTa02T4R/m7ylslbZdY80GhEzARMQ8wDQYDVQQDEwY5ZmJhbmuCEDvf5LSx96etTHA4mCk6y3YwDQYJKoZIhvcNAQEEBQADgYEAoBsrm8Zal5+JGP7iGPt0SGK9ul/WKgl5cfOOJjBYyANV/VhSCAJNkwBFI0xx3PJzMFh844T9CUYZIiW57oy9FWtehmYqhELy4oNarhn+RbFlcY7TMxFSOt1QkWfuDYU2v9LBpYjYQ07WpZ8Wi27z5U3EgJ0K8E385iI53vSTQWE=");
//		PublicKey publicKey = RSAUtil.getPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuuagL40kzjTviFy4OTMDsoxpOj0QjMjqZ/fy9dOJppvUP/m2u2Ftu4v6fmMCOyjZGZ52ohNS3pw684fL4E4+XYgGK1M6PNtIMDUmXspDQeJjxsrSsb6KxAvmTtTn+09WXnE2VuG1C6fqAZgBbVCl5pHn3aaKEz2AiBJYuH1jBQ0n0WucYoOz8Lk4XGV+DTbJiQI0Sa92q3Z/qrjPOMHbwzVG1HyKopUFMAz5PVogBi5+BHR+teA27K4A/fv2hL7S4TBqP05c6ySGUvqlnfbKCvCJ9nfbZjMxWCI6HmiCcOCLzG2dTUJViQJARXMR96JzjlO+7vcXLuZ8ANtrT57epwIDAQAB");
		PublicKey publicKey = RSAUtil.loadPublicKey("d:\\cipher\\cert.crt");
		
//		PublicKey publicKey = RSAUtil.loadPublicKey("d:\\t_dwf_public.crt");
//		PublicKey publicKey = RSAUtil.loadPublicKey("d:\\t_dwf_public_encrypted");
//		PublicKey publicKey = RSAUtil.loadPublicKey("d:\\certificate.der");
//		PublicKey publicKey = RSAUtil.loadPublicKey("d:\\t_dwf_public_decrypted");
		//Zef6tQ8fY 	YgDc2nm7i 	FtmyJzEo8v
//		PrivateKey privateKey = RSAUtil.getPrivateKey("MIICXAIBAAKBgQDbqmu1Nppuf2n7IIvR5AycOOB0q9AP42mZ1i6DWWgb3kPeXKJ5pWAUxwfvKPu7vhQ8GTiNE8CIDyeZOIlOt/5yr6qnPNx9s4ewEwNxcyDEJWl09b8ZUBnp2xAhbmXnReRWG14lOJ99lwFAG9CmTswDV0mbioIgxafTcXBOxJfQ1QIDAQABAoGAGNjFCCFeBIOrBDnhNuOZvY39bZUFmtZ5WmpMppTVXtmcSammwyW+oXsF3bqABvNwLIAZ0nNagslcDEvF1D6hrDej3gUJg2pHr6fTxvLePr7D7wOo0yZQsXpqO8DtkYqe58ebNq868sSLNtty0nYPENUsBo6muTdPb1uusMnZsL0CQQDyNbZ5WxQHmrJVoKlA2WPLUsgP7RkVYf/f2O+90qkaq3LJO0sZnodcNqUuTnQ7OR1nzrn/yZaTySt/ZhNAXlh3AkEA6CwexCSsbswVItTjzR9LvBthLctpWIbQWnhakoT/p+im3mnGUV3tXzmv3fHhpquPJLx4ZbDX5RzlyqtrrDLAEwJAcsHmOetgOxYdhpp/dr1JHg2Two/4wXvUKu/y7wI/ouY2lYdlVrcYtSUi8CS1x1WRTBzTXZDaqhy3Yj4vwuCwkwJBAKwq45nCNFhpYJcW1eLrfUb96r+B4xnxxxn/u/KBS/Bg1zWBrL3z8C24YpEsaJAnEpxEYufODxtopYL4QX1NmR8CQDgePnGZt/jtoCOw7QHrOkPiXW6USE9nASP8k9b1Yvc2nQvjNWNWXTVdfngTw1/uMV7eMDM4SX6Ra6rhC3VXnaI=");
		
//		PrivateKey privateKey = RSAUtil.loadPrivateKey("d:\\dwf_private.pfx", "Zef6tQ8fY");
//		PrivateKey privateKey = RSAUtil.loadPrivateKey("d:\\Cert.p12", "111222");
		
		PrivateKey privateKey = RSAUtil.getPrivateKey("MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC65qAvjSTONO+IXLg5MwOyjGk6PRCMyOpn9/L104mmm9Q/+ba7YW27i/p+YwI7KNkZnnaiE1LenDrzh8vgTj5diAYrUzo820gwNSZeykNB4mPGytKxvorEC+ZO1Of7T1ZecTZW4bULp+oBmAFtUKXmkefdpooTPYCIEli4fWMFDSfRa5xig7PwuThcZX4NNsmJAjRJr3ardn+quM84wdvDNUbUfIqilQUwDPk9WiAGLn4EdH614DbsrgD9+/aEvtLhMGo/TlzrJIZS+qWd9soK8In2d9tmMzFYIjoeaIJw4IvMbZ1NQlWJAkBFcxH3onOOU77u9xcu5nwA22tPnt6nAgMBAAECggEAQ8yxw04yUdOGKTNpaOaGqtigu423WTrxPbIGowKP5q6Uunh9v0/CnMxUnFCSWJ+i0dZYNsdHwpVuFTXn0YDQwuE7S7AvajAp6cL+HljFOTtE8rJU6EG02RVwAJzfYp0POcdb7PyHzqilDNr1eZBN/mxjblahtAUVgUcWhCaIKyZQne42754sYr/qpnW1obULgGeSDXP7O4nnEBvmz6j4zFrpkLEcmyXrkBm2w0TiXUc53829eVrOn0GFSwNiLVeuYouhqTLHHYwpuhvkcO9vifLoJPNG7YiQeDOkCpQ6kQH6O3UzvpJZ8H+qfR+oppMGMI97TugIUnsjumga+rKjgQKBgQD3+0t6GAPkkKMeLyeRSZD9Cam9/cA+LrcjAfMFQyidOyUlDANUv6J19XzeCSoPMy8VWPKfwKh6VphJ5UDu3D2DbBh6OkExLtXd9W+mn++vH8bTMkJg1NVImeQNpeHvnCMzDgEwkLTsbzj6o6vJ95uXZknQw9aQcsNo5qtnRQ/cYQKBgQDA8bnT90WOBleOmNUhCemq7bTpavI08GJiTTZOJyJRGlbsDgGgvvn7VwUaT29MvGkjjKZycnKCTw6fuWICHZk9beD0H8tq60kLaDqeBxiF1KGgSMUgR5bqTpkuy1nJoI0+PSt1dgrEUZGtNlVFelWAcEDDQ6nvZR2EG3XBov7YBwKBgQDMpDlWKWlU/rNN3nYVpJMc7hOA8V3rMtv47MvjJNfoYP23Q+Gf14E3PP3tsbxezMtnH38hrHicdMe5+5GJTIEK6nKMs3RHJTJit/+KjuPaLiwC76/tftX/0tdrF+CKvtwhbsY2kMRtdR+dXuIOiuKAn2389RAlJj/yWqFKWKjNIQKBgGc7rpqr7cosuIIyYa54uE5dOvO85fTPPUStXUX5iFHujt1kLRjNtcD18WP/ZmSfVn3Pzmrjs0Lne8KYFMle/tOwS7EypcsCMqXo0Zwl3cgVWZkB9n7r7uJUxX8VDGhl/8xyPCNv0hjyL8iGjp9K8h5zcSNIu867wkwhySKezE3XAoGBAOXOqaeHumqM5C4rCkPu0Ffv2ubUVcjbKSkcfVoCMKN4uwHsDzWKS6bovDyeVdfj+60I+tVWoZPOyLCFi7bI7lVAJZrdAdA1s1/dKs+WTqFJuaNjJ/yewJ88n1erUBk1pVGK8KqIfjEVQNFFWMhwIwkCtZ3ftpVCwFlYJkX4SwOF");
//		PrivateKey privateKey = RSAUtil.loadPrivateKey("d:\\cipher\\Cert.p12", "111222");
		
		
		// 公钥加密
		String currData = RSAUtil.encrypt(publicKey, strModel);
		System.out.println(currData);
		// 私钥解密
		System.out.println(RSAUtil.decrypt(privateKey, currData));

		//私钥签名
		String currSign = RSAUtil.generateSignature(currData, privateKey);
		System.out.println(currSign);
		 //公钥验证签名
		 System.out.println(RSAUtil.verifySignature(currData, publicKey, currSign));
	}

	/**
	 * 加载指定路径证书文件，获取公�?
	 * 
	 * @param keyPath
	 *            证书文件路径
	 * @return 公钥对象
	 * @throws RuntimeException
	 */
	public static PublicKey loadPublicKey(String keyPath)
			throws RuntimeException {
		PublicKey publicKey = null;
		publicKey = (PublicKey) keyMap.get(PUBLIC_KEY);
		
		if(publicKey == null){
			InputStream input = null;
			X509Certificate x509certificate;
			try {
				input = new BufferedInputStream(new FileInputStream(keyPath));
				CertificateFactory certificatefactory = CertificateFactory
						.getInstance("X.509");
				x509certificate = (X509Certificate) certificatefactory
						.generateCertificate(input);
			} catch (Exception e) {
				throw new SecurityException("读取证书失败", e);
			} finally {
				if (null != input) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			publicKey = x509certificate.getPublicKey();
			
			keyMap.put(PUBLIC_KEY, publicKey);
		}
		
		return publicKey;
	}

	/**
	 * 获取私钥
	 * 
	 * @param filePath
	 * @param passwd
	 * @return
	 * @throws SecurityException
	 */
	public static PrivateKey loadPrivateKey(String filePath, String password)
			throws SecurityException {
		PrivateKey privateKey = null;
		privateKey = (PrivateKey) keyMap.get(PRIVATE_KEY);
		
		if(privateKey == null){
			try {
				KeyStore ks = KeyStore.getInstance("PKCS12");
				FileInputStream fis = new FileInputStream(filePath);
				// If the keystore password is empty(""), then we have to set
				// to null, otherwise it won't work!!!
				char[] nPassword = null;
				if ((password == null) || password.trim().equals("")) {
					nPassword = null;
				} else {
					nPassword = password.toCharArray();
				}
				ks.load(fis, nPassword);
				fis.close();
				//System.out.println("keystore type=" + ks.getType());
				// Now we loop all the aliases, we need the alias to get keys.
				// It seems that this value is the "Friendly name" field in the
				// detals tab <-- Certificate window <-- view <-- Certificate
				// Button <-- Content tab <-- Internet Options <-- Tools menu
				// In MS IE 6.
				Enumeration enumas = ks.aliases();
				String keyAlias = null;
				if (enumas.hasMoreElements())// we are readin just one certificate.
				{
					keyAlias = (String) enumas.nextElement();
					//System.out.println("alias=[" + keyAlias + "]");
				}
				// Now once we know the alias, we could get the keys.
				//System.out.println("is key entry=" + ks.isKeyEntry(keyAlias));
				privateKey = (PrivateKey) ks.getKey(keyAlias, nPassword);
				Certificate cert = ks.getCertificate(keyAlias);
				PublicKey pubkey = cert.getPublicKey();
				/*System.out.println("cert class = " + cert.getClass().getName());
				System.out.println("cert = " + cert);
				System.out.println("public key = " + pubkey);
				System.out.println("private key = " + prikey);*/
			} catch (Exception e) {
				e.printStackTrace();
			}
			keyMap.put(PRIVATE_KEY, privateKey);
		}
		
		return privateKey;
	}


	/**	
	 * 加密
	 * 
	 * @param key
	 *            密钥
	 * @param data
	 *            源数据bytes
	 * @return
	 * @throws RuntimeException
	 */
	public static String encrypt(Key key, String dataStr)
			throws RuntimeException {
		try {
			byte[] data = dataStr.getBytes("UTF-8");
			Cipher cipher = Cipher.getInstance(transformation);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			
			int outputSize = cipher.getOutputSize(data.length);// 获得加密块加密后块大�?
			int leavedSize = data.length % MAX_ENCRYPT_BLOCK;
			int blocksSize = leavedSize != 0 ? data.length / MAX_ENCRYPT_BLOCK
					+ 1 : data.length / MAX_ENCRYPT_BLOCK;
			byte[] raw = new byte[outputSize * blocksSize];
			int i = 0;
			while (data.length - i * MAX_ENCRYPT_BLOCK > 0) {
				if (data.length - i * MAX_ENCRYPT_BLOCK > MAX_ENCRYPT_BLOCK)
					cipher.doFinal(data, i * MAX_ENCRYPT_BLOCK,
							MAX_ENCRYPT_BLOCK, raw, i * outputSize);
				else
					cipher.doFinal(data, i * MAX_ENCRYPT_BLOCK, data.length - i
							* MAX_ENCRYPT_BLOCK, raw, i * outputSize);
				i++;
			}
			return Base64.encode(raw);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**	
	 * 加密
	 * 
	 * @param key
	 *            密钥
	 * @param data
	 *            源数据bytes
	 * @return
	 * @throws RuntimeException
	 */
	public static String decrypt(Key key, String decryptData)
			throws RuntimeException {
		try {
			byte[] base64Bytes = decryptData.getBytes("UTF-8");
			
//			byte[] data = Base64.decode(base64Bytes);
			byte[] data = Base64.decode(decryptData);
			
			Cipher cipher = Cipher.getInstance(transformation);
			cipher.init(Cipher.DECRYPT_MODE, key);
			return new String(cipher.doFinal(data),"UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 生成签名
	 * 
	 * @param data
	 *            源数据bytes
	 * @param prikey
	 *            私钥
	 * @return 签名bytes
	 * @throws RuntimeException
	 */
	public static String generateSignature(String data, PrivateKey prikey)
			throws RuntimeException {
		try {
			Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
			sig.initSign(prikey);
			sig.update(Base64.decode(data));
			return Base64.encode(sig.sign());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	
	/**
	 * 验签
	 * 
	 * @param data
	 *            源数据bytes
	 * @param pubKey
	 *            公钥
	 * @param signature
	 *            签名bytes
	 * @return 验签结果
	 * @throws RuntimeException
	 */
	public static boolean verifySignature(String data, PublicKey pubKey,
			String signature) throws RuntimeException {
		try {
			Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
			sig.initVerify(pubKey);
			sig.update(Base64.decode(data));
			return sig.verify(Base64.decode(signature));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (InvalidKeyException e) {
			throw new RuntimeException(e);
		} catch (SignatureException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 鍔犵
	 * 
	 * @param data
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws RuntimeException
	 */
	public static String generateMessageDigestSignature(Key key, String encryptData) {
		
		String signature = "";
		if (!"".equals(encryptData) && encryptData != null) {//PRIVATE_KEY_PATH
		
			String messageDigest = MD5.sha1(encryptData);
			signature = encrypt(key, messageDigest);
		}
		
		return signature;
	}
	
	
	/**
	 * 1、字符串生成公钥对象 PublicKey 实例化公钥
	 */
	public static PublicKey getPublicKey(String pubKeyStr) {
//		pubKeyStr = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCVRiDkEKXy/KBTe+UmkA+feq1zGWIgBxkgbz7aBJGb5+eMKKoiDRoEHzlGndwFKm4mQWNftuMOfNcogzYpGKSEfC7sqfBPDHsGPZixMWzL3J10zkMTWo6MDIXKKqMG1Pgeq1wENfJjcYSU/enYSZkg3rFTOaBSFId+rrPjPo7Y4wIDAQAB";
		
		PublicKey publicKey = null;
		try {
			// 自己的公钥(测试)
			X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(Base64.decode(pubKeyStr));
			// RSA对称加密算法
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			// 取公钥匙对象
			publicKey = keyFactory.generatePublic(bobPubKeySpec);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("publicKey--"+publicKey.toString());
		return publicKey;
	}

	/**
	 * 
	 * 2、字符串生成私钥对象  实例化私钥
	 */
	public static PrivateKey getPrivateKey(String priKeyStr) {
//		priKeyStr = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJVGIOQQpfL8oFN75SaQD596rXMZYiAHGSBvPtoEkZvn54woqiINGgQfOUad3AUqbiZBY1+24w581yiDNikYpIR8Luyp8E8MewY9mLExbMvcnXTOQxNajowMhcoqowbU+B6rXAQ18mNxhJT96dhJmSDesVM5oFIUh36us+M+jtjjAgMBAAECgYABtnxKIabF0wBD9Pf8KUsEmXPEDlaB55LyPFSMS+Ef2NlfUlgha+UQhwsxND6CEKqS5c0uG/se/2+4l0jXz+CTYBEh+USYB3gxcMKEo5XDFOGaM2Ncbc7FAKJIkYYN2DHmr4voSM5YkVibw5Lerw0kKdYyr0Xd0kmqTok3JLiLgQJBAOGZ1ao9oqWUzCKnpuTmXre8pZLmpWPhm6S1FU0vHjI0pZh/jusc8UXSRPnx1gLsgXq0ux30j968x/DmkESwxX8CQQCpY1+2p1aX2EzYO3UoTbBUTg7lCsopVNVf41xriek7XF1YyXOwEOSokp2SDQcRoKJ2PyPc2FJ/f54pigdsW0adAkAM8JTnydc9ZhZ7WmBhOrFuGnzoux/7ZaJWxSguoCg8OvbQk2hwJd3U4mWgbHWY/1XB4wHkivWBkhRpxd+6gOUjAkBH9qscS52zZzbGiwQsOk1Wk88qKdpXku4QDeUe3vmSuZwC85tNyu+KWrfM6/H74DYFbK/MzK7H8iz80uJye5jVAkAEqEB/LwlpXljFAxTID/SLZBb+bCIoV/kvg+2145F+CSSUjEWRhG/+OH0cQfqomfg36WrvHl0g/Xw06fg31HgK";
		
		PrivateKey privateKey = null;
		PKCS8EncodedKeySpec priPKCS8;
//		PKCS12Attribute
		try {
			priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(priKeyStr));
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			privateKey = keyFactory.generatePrivate(priPKCS8);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		System.out.println("privateKey--"+privateKey.toString());
		return privateKey;
	}
}
