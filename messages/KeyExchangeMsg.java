package messages;

import java.math.BigInteger;
import java.security.PublicKey;

@SuppressWarnings("serial")
public class KeyExchangeMsg extends GenericContent {
	private BigInteger p;
	private BigInteger g;
	private int l;
	private byte[] publicKey;
	private PublicKey signingKey;
	
	public KeyExchangeMsg() {
		p = null;
		g = null;
		signingKey = null;
	}

	public BigInteger getP() {
		return p;
	}

	public void setP(BigInteger p) {
		this.p = p;
	}

	public BigInteger getG() {
		return g;
	}

	public void setG(BigInteger g) {
		this.g = g;
	}
	
	public void setL(int l) {
		this.l = l;
	}
	
	public int getL() {
		return l;
	}
	
	public void setPublicKey(byte[] arr) {
		int len = arr.length;
		publicKey = new byte[len];
		for(int i = 0; i < len; i++)
			publicKey[i] = arr[i];
	}
	
	public byte[] getPublicKey() {
		return publicKey;
	}

	public PublicKey getSigningKey() {
		return signingKey;
	}

	public void setSigningKey(PublicKey signingKey) {
		this.signingKey = signingKey;
	}
}