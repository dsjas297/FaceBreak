package messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SymmetricKEM implements Serializable {
	private static final long serialVersionUID = -961867854462646856L;
	private byte[] sharedKey;
	
	public void setSharedKey(byte[] arr) {
		int len = arr.length;
		sharedKey = new byte[len];
		
		for(int i = 0; i < len; i++)
			sharedKey[i] = arr[i];
	}
	
	public byte[] getSharedKey() {
		return sharedKey;
	}
	
	public byte[] getBytes() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = new ObjectOutputStream(bos);   
		out.writeObject(this);
		out.close();
		bos.close();

		return bos.toByteArray();
	}
	
	public void clearSharedKey() {
		for(int i = 0; i < sharedKey.length; i++)
			sharedKey[i] = 0;
	}

	public static SymmetricKEM toKEMObject(byte[] byteArray) {
		SymmetricKEM kem = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
			ObjectInputStream ois = new ObjectInputStream(bis);
			kem = (SymmetricKEM) ois.readObject();
			return kem;
		} catch (Exception ex) {
			System.err.println("Cannot convert from byte array to KeyExchange Message");
		}
		return null;
	}
}
