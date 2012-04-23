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
import java.util.ArrayList;

import javax.crypto.Cipher;

import server.FileSystem;

import messages.SymmetricKEM;

public class AsymmetricKeys {
	private PublicKey myPublicKey;
	private PrivateKey myPrivateKey;
	private PublicKey remotePublicKey;
	private byte[] publicKeyMod;
	private byte[] publicKeyExp;
	
	private static final String ALG = "RSA";
	private static final int LEN = 2048;
	private static final String PUBLIC_KEY_FILE = "public.key";
	private static final String PRIVATE_KEY_FILE = "encryptedPrivate.key";
	
	protected void genMyKeys() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(LEN);
		KeyPair kp = kpg.genKeyPair();
		myPublicKey = kp.getPublic();
		myPrivateKey = kp.getPrivate();
		
		KeyFactory fact = KeyFactory.getInstance("RSA");
		RSAPublicKeySpec pub = fact.getKeySpec(kp.getPublic(),
		RSAPublicKeySpec.class);
		RSAPrivateKeySpec priv = fact.getKeySpec(kp.getPrivate(), RSAPrivateKeySpec.class);

		BigInteger mod = pub.getModulus();
		BigInteger exp = pub.getPublicExponent();
		
		publicKeyMod = mod.toByteArray();
		publicKeyExp = exp.toByteArray();
	}
	
	protected byte[] getPublicKeyMod() {
		return publicKeyMod;
	}
	
	protected byte[] getPublicKeyExp() {
		return publicKeyExp;
	}
	
	protected void setRemotePublicKey(PublicKey remotePublicKey) {
		this.remotePublicKey = remotePublicKey;
	}
	
	protected void genRemotePublicKey(byte[] mod, byte[] exp) throws NoSuchAlgorithmException, InvalidKeySpecException {
		BigInteger m = new BigInteger(mod);
		BigInteger e = new BigInteger(exp);
		RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
		KeyFactory fact = KeyFactory.getInstance(ALG);
		remotePublicKey = fact.generatePublic(keySpec);
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
		try {
			ArrayList<String> me = FileSystem.readSecure("encryptedPrivate.key");
			
			BigInteger m = new BigInteger(me.get(0));
			BigInteger e = new BigInteger(me.get(1));
			
			RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, e);
			KeyFactory fact = KeyFactory.getInstance("RSA");
			PrivateKey privKey = fact.generatePrivate(keySpec);
			
			return privKey;
		} catch (Exception e) {
			throw new RuntimeException("Spurious serialisation error", e);
		}
	}
}
