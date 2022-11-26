package encrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class Aes {

	//jdk默认只能使用16字节的秘钥，要使用24和32字节秘钥，需另下载jar包覆盖jdk自身的jar包(美国加密算法出口限制)
	public final static String secretKey = "Px0D34a7VnT6sqkn";// (128、192、256位即16字节，24字节，32字节)
	private static final String ALGORITHM = "AES";// 对称加密算法。可选AES，DES，DESede
	// "加密算法/加密模式/填充方式"
	private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";//"AES" 六种加密模式(ECB、CBC、CFB、OFB、CTR、PCBC)
	//填充方式：PKCS5Padding、PKCS7Padding、ISO10126Padding、NoPadding、ZerosPadding、ansix923
	private static final String TRANSFORMATION_FILE = "AES/ECB/PKCS5Padding";
	private final static String ENCODEING = "UTF-8";	//UTF-8、UTF-16、GBK、GB2312、ISO-8859-1、US-ASCII
	private static final String IV_STRING = "16-Bytes--String";
	//jdk默认只能使用16字节的秘钥，要使用24和32字节秘钥，需另下载jar包覆盖jdk自身的jar包(美国加密算法出口限制)
    private static final int KEY_SIZE = 128;//秘钥长度(128、192、256位即16字节，24字节，32字节)

//    CBC加密需要一个十六位的key(密钥)和一个十六位iv(偏移量)，CBC安全比较常用
//    ECB加密不需要iv，ECB是最简单的块密码加密模式。接收到的数据中只有密钥而没有偏移量，故使用ECB处理密文。
//    在除ECB以外的所有加密方式中，都需要用到偏移量IV对加密结果进行随机化。


	public static void main(String[] args) {
		String content = "打发打发的撒safdsafd1231232";
		String aesResult;
		try {
			System.out.println("加密内容： " + content);
			aesResult = encryptString(secretKey, content);
			System.out.println("AES加密结果： " + aesResult);
			System.out.println("AES解密结果： " + decryptString(secretKey, aesResult));
			
		    long begin = System.currentTimeMillis();		    
//	        encryptFile(secretKey,  "D:\\t1.mp4", "D:\\t1-encrypted1.mp4");
//	        encryptFile(secretKey,  "D:\\t1-encrypted1.mp4", "D:\\t1-encrypted2.mp4");
//	        decryptFile(secretKey,  "D:\\t1-encrypted2.mp4", "D:\\t1_decrypted1.mp4");
//          	decryptFile(secretKey,  "D:\\t1_decrypted1.mp4", "D:\\t1_decrypted2.mp4");
	        
	        long end = System.currentTimeMillis();
	        System.err.println("耗时：" + (end-begin)/1000 + "秒");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
    /**
     * <p>
     * 生成随机密钥
     * </p>
     * 
     * @param seed 密钥种子
     * @return
     * @throws Exception
     */
    public static String getSecretKey(String seed) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        SecureRandom secureRandom;
        if (seed != null && !"".equals(seed)) {
            secureRandom = new SecureRandom(seed.getBytes(ENCODEING));
        } else {
            secureRandom = new SecureRandom();
        }
        keyGenerator.init(KEY_SIZE, secureRandom); 
        SecretKey secretKey = keyGenerator.generateKey(); 
        return Base64.encode(secretKey.getEncoded());
    }
    
	/**
	 * 加密
	 * 
	 * @param content
	 * @return
	 */
	public static String encryptString(String secretKey, String content) {
		if ("".equals(content) || content == null) {
			return "";
		}
		try {
			byte[] byteContent = content.getBytes(ENCODEING);
			byte[] enCodeFormat = secretKey.getBytes(ENCODEING);
			SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, ALGORITHM);
			byte[] initParam = IV_STRING.getBytes(ENCODEING);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
			byte[] encryptedBytes = cipher.doFinal(byteContent);
			String retString = Base64.encode(encryptedBytes);
			retString = retString.replaceAll("\r\n", "");
			retString = retString.replaceAll("\r", "");
			retString = retString.replaceAll("\n", "");
			return retString;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";

//		try {
//			String urlEncodeStr = URLEncoder.encode("base64Str","UTF-8");
//		} catch (UnsupportedEncodingException e1) {
//			e1.printStackTrace();
//		}
	}

	/**
	 * 解密
	 *
	 * @param content
	 */
	public static String decryptString(String secretKey, String content) {
//		try {
//			String base64Str = URLDecoder.decode("content","UTF-8");
//		} catch (UnsupportedEncodingException e1) {
//			e1.printStackTrace();
//		}

		try {
			byte[] encryptedBytes = Base64.decode(content);
			byte[] enCodeFormat = secretKey.getBytes(ENCODEING);
			SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, ALGORITHM);
			byte[] initParam = IV_STRING.getBytes(ENCODEING);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
			byte[] result = cipher.doFinal(encryptedBytes);
			return new String(result, ENCODEING);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * <p>
	 * 文件加密
	 * </p>
	 * 
	 * @param key
	 * @param sourceFilePath
	 * @param destFilePath
	 * @throws Exception
	 */
	public static void encryptFile(String key, String sourceFilePath, String destFilePath) {
		try {
			File sourceFile = new File(sourceFilePath);
			File destFile = new File(destFilePath);
			if (sourceFile.exists() && sourceFile.isFile()) {
				if (!destFile.getParentFile().exists()) {
					destFile.getParentFile().mkdirs();
				}
				destFile.createNewFile();
				InputStream in = new FileInputStream(sourceFile);
				OutputStream out = new FileOutputStream(destFile);
				Key secretKey = new SecretKeySpec(key.getBytes(ENCODEING), ALGORITHM);
				Cipher cipher = Cipher.getInstance(TRANSFORMATION_FILE);
				cipher.init(Cipher.ENCRYPT_MODE, secretKey);
				CipherInputStream cin = new CipherInputStream(in, cipher);
				byte[] cache = new byte[1024];
				int nRead = 0;
				while ((nRead = cin.read(cache)) != -1) {
					out.write(cache, 0, nRead);
					out.flush();
				}
				out.close();
				cin.close();
				in.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * <p>
	 * 文件解密
	 * </p>
	 * 
	 * @param key
	 * @param sourceFilePath
	 * @param destFilePath
	 * @throws Exception
	 */
	public static void decryptFile(String key, String sourceFilePath, String destFilePath) {
		try {
			File sourceFile = new File(sourceFilePath);
			File destFile = new File(destFilePath);
			if (sourceFile.exists() && sourceFile.isFile()) {
				if (!destFile.getParentFile().exists()) {
					destFile.getParentFile().mkdirs();
				}
				destFile.createNewFile();
				FileInputStream in = new FileInputStream(sourceFile);
				FileOutputStream out = new FileOutputStream(destFile);
				Key secretKey = new SecretKeySpec(key.getBytes(ENCODEING), ALGORITHM);
				Cipher cipher = Cipher.getInstance(TRANSFORMATION_FILE);
				cipher.init(Cipher.DECRYPT_MODE, secretKey);
				CipherOutputStream cout = new CipherOutputStream(out, cipher);
				byte[] cache = new byte[1024];
				int nRead = 0;
				while ((nRead = in.read(cache)) != -1) {
					cout.write(cache, 0, nRead);
					cout.flush();
				}
				cout.close();
				out.close();
				in.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}