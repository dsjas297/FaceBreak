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
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
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

import networking.RandomGenerator;


public class MsgSealer {
	private Cipher encrypter;
	private Cipher decrypter;
	
	private PrivateKey privateKey;
	private PublicKey publicKey;
	private PublicKey remotePublicKey;
	
	private char[] secret;
	
	/* specify algorithm, mode, and padding
	 * AES because secure and fast; CBC because common and more secure than EBC
	 * PKCS5Padding
	 */
//	private static final String ALG_MODE_PAD = "AES/CBC/PKCS5Padding";
//	private static final String ALG_MODE_PAD = "RSA/ECB/PKCS1Padding";
	private static final String ALG_MODE_PAD = "RSA";
	private static final String ALGORITHM = "RSA";
	private static final String KEY_GEN_ALG = "PBKDF2WithHmacSHA1";
	private static final int KEY_SIZE = 512;
	
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

	public void genKeys() throws NoSuchAlgorithmException {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(KEY_SIZE);
		KeyPair kp = kpg.genKeyPair();
		publicKey = kp.getPublic();
		privateKey = kp.getPrivate();
	}
	
	public PublicKey getPublicKey() {
		return publicKey;
	}
	
	public void init() throws InvalidKeyException, Exception {
		encrypter.init(Cipher.ENCRYPT_MODE, privateKey);
		decrypter.init(Cipher.DECRYPT_MODE, remotePublicKey);
	}
	
	public void setRemotePublicKey(PublicKey pub) {
		remotePublicKey = pub;
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
