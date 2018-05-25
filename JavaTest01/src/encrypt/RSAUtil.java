package encrypt;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
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
		
		// 公钥加密
		PublicKey publicKey = RSAUtil.loadPublicKey("d:\\t_dwf_public.crt");
//		PublicKey publicKey = RSAUtil.loadPublicKey("d:\\t_dwf_public_encrypted");
//		PublicKey publicKey = RSAUtil.loadPublicKey("d:\\certificate.der");
//		PublicKey publicKey = RSAUtil.loadPublicKey("d:\\t_dwf_public_decrypted");
		String currData = RSAUtil.encrypt(publicKey, strModel);
		System.out.println(currData);
		
		//私钥签名
		//Zef6tQ8fY 	YgDc2nm7i 	FtmyJzEo8v
		PrivateKey privateKey = RSAUtil.loadPrivateKey("d:\\dwf_private.pfx", "Zef6tQ8fY");
//		PrivateKey privateKey = RSAUtil.loadPrivateKey("d:\\Cert.p12", "111222");
		String currSign = RSAUtil.generateSignature(currData, privateKey);
		System.out.println(currSign);

		// 私钥解密
		 System.out.println(RSAUtil.decrypt(privateKey, currData));
		 
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
}
