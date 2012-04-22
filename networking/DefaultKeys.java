package networking;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.SealedObject;

import common.FBClientUser;

import server.FaceBreakUser;

import messages.GenericMsg;
import messages.Item;
import messages.MsgSealer;
import messages.MsgWrapper;

public class DefaultKeys {
	
	public static void saveToFile(String fileName, BigInteger mod, BigInteger exp)
			throws IOException {
		BufferedOutputStream boos = new BufferedOutputStream(new FileOutputStream(fileName));
		ObjectOutputStream oout = new ObjectOutputStream(boos);
		try {
			oout.writeObject(mod);
			oout.writeObject(exp);
		} catch (Exception e) {
			throw new IOException("Unexpected error", e);
		} finally {
			oout.close();
		}
	}
	/*
	public static void saveKeys() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(1024);
		KeyPair kp = kpg.genKeyPair();
		PublicKey publicKey = kp.getPublic();
		PrivateKey privateKey = kp.getPrivate();
		
		KeyFactory fact = KeyFactory.getInstance("RSA");
		RSAPublicKeySpec pub = fact.getKeySpec(publicKey, RSAPublicKeySpec.class);
		RSAPrivateKeySpec priv = fact.getKeySpec(privateKey, RSAPrivateKeySpec.class);
		
		String publicKeyFile = "publicKey.txt";
		String privateKeyFile = "privateKey.txt";
		
		saveToFile(publicKeyFile, pub.getModulus(), pub.getPublicExponent());
		saveToFile(privateKeyFile, priv.getModulus(), priv.getPrivateExponent());
	} */
	
	public static void main(String[] args) throws InvalidKeyException, Exception {
		MsgSealer sealer = new MsgSealer();
		sealer.genKeys();
		PublicKey pub = sealer.getPublicKey();
		sealer.setRemotePublicKey(pub);
		sealer.init();
		
		GenericMsg msg = new GenericMsg();
		msg.setTimestamp();
		msg.setId(0);
		Item<FBClientUser> user = new Item<FBClientUser>();
		user.set(new FBClientUser("Gaomin", "pwd"));
		msg.setDetails(user);
		
		SealedObject sealedMsg = sealer.encrypt(msg);
	} 
}
