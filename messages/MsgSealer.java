/**
 * @author gd226
 * 
 * This class encrypts and decrypts messages (Sealed Objects).
 * Use AES cipher:
 * http://www.javamex.com/tutorials/cryptography/ciphers.shtml
 */

package messages;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class MsgSealer {
	private Cipher encrypter;
	private Cipher decrypter;
	private char[] sharedSecret;
	
	/* specify algorithm, mode, and padding
	 * AES because secure and fast; CBC because common and more secure than EBC
	 * PKCS7Padding
	 */
	private static final String ALG_MODE_PAD = "AES/CBC/PKCS5Padding";
	private static final String ALGORITHM = "AES";
	
	public MsgSealer() {
		try {
			encrypter = Cipher.getInstance(ALG_MODE_PAD);
			decrypter = Cipher.getInstance(ALG_MODE_PAD);
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Invalid cipher algorithm!");
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			System.out.println("Invalid padding!");
			e.printStackTrace();
		}
	}
	
	public void init(byte[] array) throws InvalidKeyException, Exception {
		int len = array.length;
		sharedSecret = new char[len];
		
		byte[] salt = new byte[2];
		salt[0] = 0;
		salt[1] = 0;
		
		byte[] iv = new byte[16];
		for(int i = 0; i < iv.length; i++)
			iv[i] = 0;
		
		for(int i = 0; i < len; i++) 
			sharedSecret[i] = (char)array[i];
		
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		KeySpec spec = new PBEKeySpec(sharedSecret, salt, 65536, 128);
		SecretKey tmp = factory.generateSecret(spec);
		SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
		
		encrypter.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(iv));
		decrypter.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
	}
	
	// zero out byte array??
	public void destroy() {
		for(int i = 0; i < sharedSecret.length; i++)
			sharedSecret[i] = '0';
	}
	
	public SealedObject encrypt(GenericMsg obj) {
		try {
			return new SealedObject(obj, encrypter);
		} catch (IllegalBlockSizeException | IOException e) {
			System.err.println("Bad encryption algorithm: ");
			e.printStackTrace();
			return null;
		}
	}
	
	public GenericMsg decrypt(SealedObject sealedMsg) {
		try {
			return (GenericMsg) sealedMsg.getObject(decrypter);
		} catch (ClassNotFoundException | IllegalBlockSizeException
				| BadPaddingException | IOException e) {
			System.err.println("Bad decryption algorithm: ");
			e.printStackTrace();
			return null;
		}
	}
}
