package facebreak.common;

import java.io.Serializable;

public class FBClientUser extends User implements Serializable {
	private String pwd;
	private String hashedPassword;
	
	public FBClientUser(String username) {
		super(username);
	}
	
	public FBClientUser(String username, String password) {
		super(username);
		this.pwd = new String(password);
	}

	public String getPassword() {
		return pwd;
	}
	public void setPassword(String pwd) {
		this.pwd = pwd;
	}
}
