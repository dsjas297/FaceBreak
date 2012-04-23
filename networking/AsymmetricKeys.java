package networking;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream.GetField;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

public class AsymmetricKeys {
	private PublicKey myPublicKey;
	private PrivateKey myPrivateKey;
	private PublicKey remotePublicKey;
	
	private static final String ALG = "RSA";
	private static final int LEN = 1028;
	private static final String PUBLIC_KEY_FILE = "public.key";
	private static final String PRIVATE_KEY_FILE = "private.key";
	
	protected void genMyKeys() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(LEN);
		KeyPair kp = kpg.genKeyPair();
		myPublicKey = kp.getPublic();
		myPrivateKey = kp.getPrivate();
	}
	
	protected void setRemotePublicKey(PublicKey remotePublicKey) {
		this.remotePublicKey = remotePublicKey;
	}
	
	protected void setMyPrivateKey(PrivateKey privateKey) {
		myPrivateKey = privateKey;
	}
	
	protected byte[] encrypt(byte[] data) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, remotePublicKey);
		byte[] cipherData = cipher.doFinal(data);
		return cipherData;
	}
	
	protected byte[] decrypt(byte[] data) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, myPrivateKey);
		byte[] cipherData = cipher.doFinal(data);
		return cipherData;
	}
	
	protected static PublicKey readPublicKeyFromFile() throws IOException {
		InputStream in = AsymmetricKeys.class.getResourceAsStream(PUBLIC_KEY_FILE);
		ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(in));

		try {
			BigInteger m = (BigInteger) oin.readObject();
			BigInteger e = (BigInteger) oin.readObject();
			RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
			KeyFactory fact = KeyFactory.getInstance("RSA");
			return fact.generatePublic(keySpec);
		} catch (Exception e) {
			throw new RuntimeException("Spurious serialisation error", e);
		} finally {
			oin.close();
		}
	}
	
	protected static PrivateKey readPrivateKeyFromFile() throws IOException {
		InputStream in = AsymmetricKeys.class.getResourceAsStream(PRIVATE_KEY_FILE);
		ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(in));

		try {
			BigInteger m = (BigInteger) oin.readObject();
			BigInteger e = (BigInteger) oin.readObject();
			RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, e);
			KeyFactory fact = KeyFactory.getInstance("RSA");
			PrivateKey privKey = fact.generatePrivate(keySpec);
			return privKey;
		} catch (Exception e) {
			throw new RuntimeException("Spurious serialisation error", e);
		} finally {
			oin.close();
		}
	}
	
	public static void main(String[] args) {
		
	}
}
