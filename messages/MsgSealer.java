/**
 * @author gd226
 * 
 * This class encrypts and decrypts messages (Sealed Objects).
 * Use AES cipher:
 * http://www.javamex.com/tutorials/cryptography/ciphers.shtml
 */

package messages;

import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class MsgSealer {
	private Cipher encrypter;
	private Cipher decrypter;
	
	private PrivateKey privateKey;
	private PublicKey publicKey;
	private char[] secret;
	
	/* specify algorithm, mode, and padding
	 * AES because secure and fast; CBC because common and more secure than EBC
	 * PKCS5Padding
	 */
	private static final String ALG_MODE_PAD = "AES/CBC/PKCS5Padding";
	private static final String ALGORITHM = "AES";
	private static final String KEY_GEN_ALG = "PBKDF2WithHmacSHA1";
	private static final int NUM_BYTES = 128;
	
	public MsgSealer() {
		try {
			encrypter = Cipher.getInstance(ALG_MODE_PAD);
			decrypter = Cipher.getInstance(ALG_MODE_PAD);
		} catch (Exception e) {
			System.err.print("Could not initialize message sealer object. ");
			System.err.println("Invalid algorithm or padding!");
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void init(byte[] array) throws InvalidKeyException, Exception {
		int len = array.length;
		secret = new char[len];
		
		byte[] salt = new byte[2];
		salt[0] = 0;
		salt[1] = 0;
		
		byte[] iv = new byte[16];
		for(int i = 0; i < iv.length; i++)
			iv[i] = 0;
		
		for(int i = 0; i < len; i++) 
			secret[i] = (char)array[i];
		
		SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_GEN_ALG);
		KeySpec spec = new PBEKeySpec(secret, salt, 65536, NUM_BYTES);
		SecretKey tmp = factory.generateSecret(spec);
		SecretKey secret = new SecretKeySpec(tmp.getEncoded(), ALGORITHM);
		
		encrypter.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(iv));
		decrypter.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
	}
	
	// zero out byte array??
	public void destroy() {
		for(int i = 0; i < secret.length; i++)
			secret[i] = '0';
	}
	
	// "seals" a generic message
	public SealedObject encrypt(Serializable obj) {
		try {
			return new SealedObject(obj, encrypter);
		} catch (Exception e) {
			System.err.println("COULD NTO ENCRYPT SERIALIZABLE OBJECT!");
			e.printStackTrace();
			return null;
		}
	}
	
	// "unseals" a sealed object to the serializable message passed between client/server
	public Serializable decrypt(SealedObject sealedMsg) {
		try {
			return (Serializable)sealedMsg.getObject(decrypter);
		} catch (Exception e) {
			System.err.println("COULD NOT DECRYPT SEALED OBJECT!");
			e.printStackTrace();
			return null;
		}
	}
	
//	public SealedObject encrypt(KeyExchangeMsg obj) {
//		try {
//			return new SealedObject(obj, encrypter);
//		} catch (IllegalBlockSizeException | IOException e) {
//			System.err.println("Bad encryption algorithm: ");
//			e.printStackTrace();
//			return null;
//		}
//	}
//	
//	public KeyExchangeMsg decryptKEM(SealedObject sealedMsg) {
//		try {
//			return (KeyExchangeMsg) sealedMsg.getObject(decrypter);
//		} catch (ClassNotFoundException | IllegalBlockSizeException
//				| BadPaddingException | IOException e) {
//			System.err.println("Bad decryption algorithm: ");
//			e.printStackTrace();
//			return null;
//		}
//	}
}
