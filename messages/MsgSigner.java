package messages;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.SignedObject;

import javax.crypto.SealedObject;

public class MsgSigner {
	private PrivateKey privateKey;
	private PublicKey remotePublicKey;
	private Signature signingEngine;
	
	private static final int KEY_SIZE = 1024;
	
	public MsgSigner() {
		remotePublicKey = null;
		privateKey = null;
	}
	
	public PublicKey init() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
	    keyGen.initialize(KEY_SIZE);
	    KeyPair keypair = keyGen.genKeyPair();
	    privateKey = keypair.getPrivate();
	    signingEngine = Signature.getInstance(privateKey.getAlgorithm());

		return keypair.getPublic();
	}
	
	public PublicKey init(PublicKey remotePublicKey) throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
	    keyGen.initialize(KEY_SIZE);
	    KeyPair keypair = keyGen.genKeyPair();
	    privateKey = keypair.getPrivate();
	    signingEngine = Signature.getInstance(privateKey.getAlgorithm());

		this.remotePublicKey = remotePublicKey;
		return keypair.getPublic();
	}
	
	public void setRemotePublicKey(PublicKey remotePublicKey) {
		this.remotePublicKey = remotePublicKey;
	}
	
	public SignedObject sign(SealedObject sealedMsg) {
		try {
			return new SignedObject(sealedMsg, privateKey, signingEngine);
		} catch (InvalidKeyException | SignatureException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * @param signedMsg - the GenericMsg that is sealed and signed
	 * @return	returns the message if it has been verified; otherwise return null; 
	 */
	public SealedObject extract(SignedObject signedMsg) {
		try {
			if(signedMsg.verify(remotePublicKey, signingEngine))
				return (SealedObject) signedMsg.getObject();
		} catch (ClassNotFoundException | IOException | InvalidKeyException
				| SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
