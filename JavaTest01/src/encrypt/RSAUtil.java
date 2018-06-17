package encrypt;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;


public class RSAUtil {
	private static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";
//	private static final String TRANSFORMATION_FILE = "RSA/None/PKCS1Padding";
	private static final String TRANSFORMATION_FILE = "RSA/ECB/NoPadding";
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
	
	private static final int KEY_SIZE = 1024;//秘钥长度(1024，2048，3072 ，4096位)
	
	public static void main(String[] args) {
		String plainString = "123333333333334534254";
		String seed = "111222";
		
		try {
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
	        SecureRandom secureRandom;
	        if (seed == null || "".equals(seed)) {
	            secureRandom = new SecureRandom();
	        } else {
	            secureRandom = new SecureRandom(seed.getBytes());
	        }
			keyPairGen.initialize(KEY_SIZE, secureRandom);//秘钥长度
			KeyPair keyPair = keyPairGen.generateKeyPair();
			// Generate keys
			RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
			RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
			System.out.println("privateKey="+Base64.encode(privateKey.getEncoded()));
			System.out.println("publicKey="+Base64.encode(publicKey.getEncoded()));
			
//			PublicKey publicKey = RSAUtil.getPublicKeyFile("d:\\t_dwf_public.crt");
//			PublicKey publicKey = RSAUtil.getPublicKeyFile("d:\\t_dwf_public_encrypted");
//			PublicKey publicKey = RSAUtil.getPublicKeyFile("d:\\t01_dwf_public_decrypted");
//			Zef6tQ8fY 	YgDc2nm7i 	FtmyJzEo8v
//			PrivateKey privateKey = RSAUtil.getPrivateKeyFile("d:\\dwf_private.pfx", "Zef6tQ8fY");
	
			//4096
//			PublicKey publicKey = RSAUtil.getPublicKeyString("MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA66UuFgI3mmeBXaCSyjUhYdA/yyV+GlHzX+PgNNoCivFt7aVR7klM+khkzlfofHVsVjat8SmBO1qNK9IHC3cV3Km31qlk+EErbAcw0P3mirjZW+cKvtkb9hElE3tciku9MmWkWSx/IBJsF64nnaZlM+iiTi+ULi1ET6SkSUfS0DROTVQWJ8q3FEBrMWD5uEoJz8wfgUjS4KLqdZfgzdMO/DWRJNAsZ9j/XXFBEe9+lCuu17zd3yQjHKB1NZU+vZ/922AOJhNO+ema5bWvANFkOD5d53ea8dTl69LC2l06LJvQOQ2I2dK02mB0uiW4U2+mjLfALi4GRgqNwVp+Ug+z4MJWG+MptIQn9IgSYtJ4whSmCa1SkvJsE2Aw7bQN9Iyg+vUlgepBqpmF2olCv+2/Ye0YoGO0ml38j+qcWOoyc1EUu8UA+3+MxQbUTVJo8s6rHxRt55uQax1gqgF3dpesntD/2/5dqnF4tJOwe+vP6EGVgu7mGIIAO81XF+hYXOO62MjUbo69cJe6fvm5Ix7DEGK/BkK8wR2sqsYEOEZBI//cYfT9xP38Tuu2DzcK2XoNHQ+fJoNe1bXcvbmGmrzk320GrmyRmd/NR+X9feZIgeZi8Yo2Dj5DiPo8ZgOkoCO0ECRFNX0uTde5sXnM8isohQqiRpJXEy3FDmkU3wysVhcCAwEAAQ==");
//			PrivateKey privateKey = RSAUtil.getPrivateKeyString("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");
	
			System.out.println("-----------------------------------");
			// 公钥加密
			String encryptedData = RSAUtil.encryptString(publicKey, plainString);
			System.out.println(encryptedData);
			// 私钥解密
			System.out.println(RSAUtil.decryptString(privateKey, encryptedData));
	
			//私钥签名
			String signString = RSAUtil.generateSignature(encryptedData, privateKey);
			System.out.println(signString);
			 //公钥验证签名
			System.out.println(RSAUtil.verifySignature(encryptedData, publicKey, signString));
			System.out.println("-----------------------------------");
			File file = new File("D:\\t_dwf_public.crt");
			File encryptedFile = new File("D:\\t01_dwf_public_encrypted");
			File decryptedFile1 = new File("D:\\t01_dwf_public_decrypted");
			encryptFile(publicKey, file, encryptedFile);
			decryptFile(privateKey, encryptedFile, decryptedFile1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 加载指定路径证书文件，获取公�?
	 * 
	 * @param keyPath
	 *            证书文件路径
	 * @return 公钥对象
	 * @throws RuntimeException
	 */
	public static PublicKey getPublicKeyFile(String keyPath)
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
	public static PrivateKey getPrivateKeyFile(String filePath, String password)
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
	 * 1、字符串生成公钥对象 PublicKey 实例化公钥
	 */
	public static PublicKey getPublicKeyString(String pubKeyStr) {
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
	public static PrivateKey getPrivateKeyString(String priKeyStr) {
//		priKeyStr = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJVGIOQQpfL8oFN75SaQD596rXMZYiAHGSBvPtoEkZvn54woqiINGgQfOUad3AUqbiZBY1+24w581yiDNikYpIR8Luyp8E8MewY9mLExbMvcnXTOQxNajowMhcoqowbU+B6rXAQ18mNxhJT96dhJmSDesVM5oFIUh36us+M+jtjjAgMBAAECgYABtnxKIabF0wBD9Pf8KUsEmXPEDlaB55LyPFSMS+Ef2NlfUlgha+UQhwsxND6CEKqS5c0uG/se/2+4l0jXz+CTYBEh+USYB3gxcMKEo5XDFOGaM2Ncbc7FAKJIkYYN2DHmr4voSM5YkVibw5Lerw0kKdYyr0Xd0kmqTok3JLiLgQJBAOGZ1ao9oqWUzCKnpuTmXre8pZLmpWPhm6S1FU0vHjI0pZh/jusc8UXSRPnx1gLsgXq0ux30j968x/DmkESwxX8CQQCpY1+2p1aX2EzYO3UoTbBUTg7lCsopVNVf41xriek7XF1YyXOwEOSokp2SDQcRoKJ2PyPc2FJ/f54pigdsW0adAkAM8JTnydc9ZhZ7WmBhOrFuGnzoux/7ZaJWxSguoCg8OvbQk2hwJd3U4mWgbHWY/1XB4wHkivWBkhRpxd+6gOUjAkBH9qscS52zZzbGiwQsOk1Wk88qKdpXku4QDeUe3vmSuZwC85tNyu+KWrfM6/H74DYFbK/MzK7H8iz80uJye5jVAkAEqEB/LwlpXljFAxTID/SLZBb+bCIoV/kvg+2145F+CSSUjEWRhG/+OH0cQfqomfg36WrvHl0g/Xw06fg31HgK";
		
		PrivateKey privateKey = null;
		try {
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(priKeyStr));
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			privateKey = keyFactory.generatePrivate(priPKCS8);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		System.out.println("privateKey--"+privateKey.toString());
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
	public static String encryptString(Key key, String plainString)
			throws RuntimeException {
		try {
			byte[] data = plainString.getBytes("UTF-8");
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			
			int outputSize = cipher.getOutputSize(data.length);// 获得加密块加密后块大�?
			int leavedSize = data.length % MAX_ENCRYPT_BLOCK;
			int blocksSize = leavedSize != 0 ? data.length / MAX_ENCRYPT_BLOCK
					+ 1 : data.length / MAX_ENCRYPT_BLOCK;
			byte[] raw = new byte[outputSize * blocksSize];
			int i = 0;
			while (data.length - i * MAX_ENCRYPT_BLOCK > 0) {
				if (data.length - i * MAX_ENCRYPT_BLOCK > MAX_ENCRYPT_BLOCK){
					cipher.doFinal(data, i * MAX_ENCRYPT_BLOCK,MAX_ENCRYPT_BLOCK, raw, i * outputSize);
				}
				else{
					cipher.doFinal(data, i * MAX_ENCRYPT_BLOCK, data.length - i* MAX_ENCRYPT_BLOCK, raw, i * outputSize);
				}
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
	public static String decryptString(Key key, String encryptedData)
			throws RuntimeException {
		try {
			byte[] data = Base64.decode(encryptedData);
			
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
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
			signature = encryptString(key, messageDigest);
		}
		
		return signature;
	}

	
	public static void encryptFile(Key publicKey, File file, File newFile) {
		try {
			InputStream is = new FileInputStream(file);
			OutputStream os = new FileOutputStream(newFile);

			byte[] bytes = new byte[53];
			while (is.read(bytes) > 0) {
				byte[] e = encrypt(publicKey, bytes);
				bytes = new byte[53];
				os.write(e, 0, e.length);
			}
			os.close();
			is.close();
			System.out.println("encrypt file write success");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void decryptFile(Key privateKey, File file, File newFile) {
		try {
			InputStream is = new FileInputStream(file);
			OutputStream os = new FileOutputStream(newFile);
			byte[] bytes1 = new byte[64];
			while (is.read(bytes1) > 0) {
				byte[] de = decrypt(privateKey, bytes1);
				bytes1 = new byte[64];
				os.write(de, 0, de.length);
			}
			os.close();
			is.close();
			System.out.println("decrypt file write success");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * * Encrypt String. *
	 * @return byte[]
	 */
	private static byte[] encrypt(Key key, byte[] obj) {
		if (key != null) {
			try {
				Cipher cipher = Cipher.getInstance(TRANSFORMATION_FILE);
				cipher.init(Cipher.ENCRYPT_MODE, key);
				return cipher.doFinal(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * * Basic decrypt method *
	 * @return byte[]
	 */
	private static byte[] decrypt(Key key, byte[] obj) {
		if (key != null) {
			try {
				Cipher cipher = Cipher.getInstance(TRANSFORMATION_FILE);
				cipher.init(Cipher.DECRYPT_MODE, key);
				return cipher.doFinal(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
