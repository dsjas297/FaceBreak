/**
 * GARBAGE CLASS
 * Used the following as reference/guid:
 * http://www.java2s.com/Tutorial/Java/0490__Security/ImplementingtheDiffieHellmankeyexchange.htm
 * http://www.exampledepot.com/egs/javax.crypto/KeyAgree.html
 */

package networking;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.*;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.*;

import common.FBClientUser;

public class DHKey {
	public static final int NUM_BITS = 1024;	// 128 bytes
	public static final String PROTOCOL = "DH";
	private static final String ALGORITHM = "AES";
	
	public static String generateKeyString() throws Exception {
		AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance(PROTOCOL);
	    paramGen.init(NUM_BITS);

	    AlgorithmParameters params = paramGen.generateParameters();
	    DHParameterSpec dhSpec = (DHParameterSpec) params.getParameterSpec(DHParameterSpec.class);

	    return dhSpec.getP() + "," + dhSpec.getG() + "," + dhSpec.getL();
	}

	public static void getKeys() throws Exception {
//	    String keyString = generateKeyString();
//	    String[] keys = keyString.split(",");
//	    
//	    BigInteger p = new BigInteger(keys[0]);
//	    BigInteger g = new BigInteger(keys[1]);
//	    int l = Integer.parseInt(keys[2]);
	    
	    KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");
		kpg.initialize(NUM_BITS);
		KeyPair kp = kpg.generateKeyPair();
		
		Class dhClass = Class.forName("javax.crypto.spec.DHParameterSpec");
		DHParameterSpec dhSpec = ((DHPublicKey) kp.getPublic()).getParams();
		
		BigInteger g = dhSpec.getG();
		BigInteger p = dhSpec.getP();
		int l = dhSpec.getL();
		byte[] publicKey = kp.getPublic().getEncoded();
		KeyAgreement ka = KeyAgreement.getInstance("DH");
		ka.init(kp.getPrivate());
	    
		byte[] remotePublic = null;
		
		KeyFactory kf = KeyFactory.getInstance("DH");
		X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(remotePublic);
		PublicKey pk = kf.generatePublic(x509Spec);
		ka.doPhase(pk, true);
		
		byte secret[] = ka.generateSecret();
		
		SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
		DESKeySpec desSpec = new DESKeySpec(secret);
		SecretKey key = skf.generateSecret(desSpec);
		
		// Step 7:  Alice encrypts data with the key and sends
		//		the encrypted data to Bob
		Cipher c = Cipher.getInstance("AES/");
		c.init(Cipher.ENCRYPT_MODE, key);
	}
	
	public static void bobPublicKey(BigInteger p, BigInteger g, int l, byte[] remotePublic) throws Exception {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");
		DHParameterSpec dhSpec = new DHParameterSpec(p, g, l);
		kpg.initialize(dhSpec);
		KeyPair kp = kpg.generateKeyPair();
		byte[] bobPublic = kp.getPublic().getEncoded();

		// Step 5 part 1:  Bob uses his private key to perform the
		//		first phase of the protocol
		KeyAgreement ka = KeyAgreement.getInstance("DH");
		ka.init(kp.getPrivate());

		// Step 5 part 2:  Bob uses Alice's public key to perform
		//		the second phase of the protocol.
		KeyFactory kf = KeyFactory.getInstance("DH");
		X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(remotePublic);
		PublicKey pk = kf.generatePublic(x509Spec);
		ka.doPhase(pk, true);

		// Step 5 part 3:  Bob generates the secret key
		byte secret[] = ka.generateSecret();

		// Step 6:  Bob generates a DES key
		SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
		DESKeySpec desSpec = new DESKeySpec(secret);
		SecretKey key = skf.generateSecret(desSpec);
		
		// Step 8:  Bob receives the encrypted text and decrypts it
		Cipher c = Cipher.getInstance("AES/CBC/PKCS7Padding");
		c.init(Cipher.DECRYPT_MODE, key);
	}
	
	public static void main(String[] argv) throws Exception {
		
		getKeys();
	    
	  }
	
	public static void misc() throws Exception {
	    
	    // Create Cipher
//	    Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
//	    desCipher.init(Cipher.ENCRYPT_MODE, secretKey);
//
//		FBClientUser user = new FBClientUser("hamster", "hello");
//		
//		ByteArrayOutputStream bout = new ByteArrayOutputStream();
//		CipherOutputStream cout = new CipherOutputStream(bout, desCipher);
//		
//		SealedObject sealedUser = new SealedObject(user, desCipher);
//		
//		ObjectOutputStream outStream = new ObjectOutputStream(null);
//		outStream.writeObject(sealedUser);

		String password = "fuckmylifeiwanttosleep";
		// Create Key
	    byte key[] = password.getBytes();
	    DESKeySpec desKeySpec = new DESKeySpec(key);
	    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
	    SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
	}
}
