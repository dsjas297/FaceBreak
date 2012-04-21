package messages;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.PublicKey;

public class KeyExchangeMsg implements Serializable {
	private static final long serialVersionUID = -8583780774745299419L;
	private BigInteger p;
	private BigInteger g;
	private int l;
	private byte[] publicKey;
	
	public KeyExchangeMsg() {
		p = null;
		g = null;
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
}