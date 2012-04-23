package messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class AsymmetricKEM implements Serializable {
	private static final long serialVersionUID = 2044097433147411054L;
	private byte[] publicKeyMod;
	private byte[] publicKeyExp;
	
	public void setPublicKey(byte[] mod, byte[] exp) {
		int modlen = mod.length;
		int explen = exp.length;
		
		publicKeyMod = new byte[modlen];
		publicKeyExp = new byte[explen];
		
		for(int i = 0; i < modlen; i++)
			publicKeyMod[i] = mod[i];

		for(int i = 0; i < explen; i++)
			publicKeyExp[i] = exp[i];
	}
	
	public byte[] getPublicKeyMod() {
		return publicKeyMod;
	}
	
	public byte[] getPublicKeyExp() {
		return publicKeyExp;
	}

	public byte[] getBytes() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = new ObjectOutputStream(bos);   
		out.writeObject(this);
		out.close();
		bos.close();

		return bos.toByteArray();
	}
	
	public static AsymmetricKEM toKEMObject(byte[] byteArray) {
		AsymmetricKEM kem = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
			ObjectInputStream ois = new ObjectInputStream(bis);
			kem = (AsymmetricKEM) ois.readObject();
			return kem;
		} catch (Exception ex) {
			System.err.println("Cannot convert from byte array to KeyExchange Message");
		}
		return null;
	}
}
