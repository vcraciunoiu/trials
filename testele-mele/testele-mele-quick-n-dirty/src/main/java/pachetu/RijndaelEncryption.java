package pachetu;


import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.net.URLCodec;

/**
 * AES (Rijndael) encryption/decryption with a fixed key.
 * 
 * @author Bianca 
 * 
 */
public class RijndaelEncryption  {
	
	// fixed key used for encryption/decryption
	private static byte[] key = { -68, -46, -102, -11, 59, -58, 22, -71, -54, -3, -70, -20, -37, -67, -11, -128};
	/**
	 * encryption alg
	 */
	private static String ENCRYPTION_ALGORITHM = "AES";
	
	/**
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String password) throws Exception {
		if (password == null) {
			throw new IllegalArgumentException("Null argument.");
		}
		String encryptedPassword = "";
		try {
//			init SecretKeySpec using the fixed key
			SecretKeySpec skeySpec = new SecretKeySpec(key, ENCRYPTION_ALGORITHM);
			
			// Instantiate the cipher	
			Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);	
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
	
			byte[] encrypted = cipher.doFinal(password.getBytes());
//			byte[] encoded = Base64.encodeBase64(encrypted);
			
			URLCodec codec = new URLCodec();
			byte[] encoded = codec.encode(encrypted);
			
			encryptedPassword = new String(encoded);
		}
		catch (Exception e) {
			throw new Exception (e);
		}
		
		return encryptedPassword;
	}
	
	/**
	 * 
	 * @param encryptedPassword
	 * @throws Exception
	 * 
	 */
	public static String decrypt(String encryptedPassword) throws Exception {
		if (encryptedPassword == null) {
			throw new IllegalArgumentException("Null argument.");
		}
		String plainText = "";
		
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(key, ENCRYPTION_ALGORITHM);
	
			Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);	
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		
			byte[] encoded = Base64.decodeBase64(encryptedPassword.getBytes());
			byte[] clearBytes = cipher.doFinal(encoded);
			 plainText = new String(clearBytes);
		}
		catch (Exception e) {
			throw new Exception(e);
		}
		 
		return plainText;
	}
}
