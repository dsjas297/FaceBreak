/**
 * @author gd226
 * 
 * This class encrypts and decrypts messages (Sealed Objects).
 * Use AES cipher:
 * http://www.javamex.com/tutorials/cryptography/ciphers.shtml
 */

package messages;

import java.security.InvalidKeyException;
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
	
	private char[] secret;
	
	/* specify algorithm, mode, and padding
	 * AES because secure and fast; CBC because common and more secure than EBC
	 * PKCS5Padding
	 */
	private static final String ALG_MODE_PAD = "AES/CBC/PKCS5Padding";
	private static final String ALGORITHM = "AES";
	private static final String KEY_GEN_ALG = "PBKDF2WithHmacSHA1";
	private static final int NUM_BYTES = 128;
	
	private static final byte[] SALT = { -66, 112, -76, -97, 120, -114, -107,
			114, -79, 40, 23, 52, -20, -43, -128, 51 };
	private static final byte[] INIT_VECTOR = { -13, 119, -85, -98, -57, 110,
			34, 33, 97, -60, -55, -45, -48, -103, -94, 72 };

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
		
		for(int i = 0; i < len; i++) 
			secret[i] = (char)array[i];
		
		SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_GEN_ALG);
		KeySpec spec = new PBEKeySpec(secret, SALT, 65536, NUM_BYTES);
		SecretKey tmp = factory.generateSecret(spec);
		SecretKey secret = new SecretKeySpec(tmp.getEncoded(), ALGORITHM);
		
		encrypter.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(INIT_VECTOR));
		decrypter.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(INIT_VECTOR));
	}
	
	public void destroy() {
		for(int i = 0; i < secret.length; i++)
			secret[i] = '0';
	}
	
	// "seals" a generic message
	public SealedObject encrypt(MsgWrapper obj) {
		try {
			return new SealedObject(obj, encrypter);
		} catch (Exception e) {
			System.err.println("COULD NTO ENCRYPT SERIALIZABLE OBJECT!");
			e.printStackTrace();
			return null;
		}
	}
	
	// "unseals" a sealed object to the serializable message passed between client/server
	public MsgWrapper decrypt(SealedObject sealedMsg) {
		try {
			return (MsgWrapper)sealedMsg.getObject(decrypter);
		} catch (Exception e) {
			System.err.println("COULD NOT DECRYPT SEALED OBJECT!");
			e.printStackTrace();
			return null;
		}
	}
}
