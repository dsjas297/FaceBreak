package messages;

import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MsgWrapper implements Serializable {

	private static final long serialVersionUID = 987945632773869779L;
	GenericMsg msg;
	private byte[] checksum;
	
	public void setMsg(GenericMsg msg) {
		this.msg = msg;
	}
	
	public GenericMsg getMsg() {
		return msg;
	}
	
	public void setChecksum() throws NoSuchAlgorithmException, IOException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(msg.getBytes());

		checksum = md.digest();
	}
	
	public byte[] getChecksum() {
		return checksum;
	}
	
	public static boolean compareChecksum(byte[] arr1, byte[] arr2) {
		if(arr1.length != arr2.length)
			return false;
		
		for(int i = 0; i < arr1.length; i++) {
			if(arr1[i] != arr2[i])
				return false;
		}
		return true;
	}
}
