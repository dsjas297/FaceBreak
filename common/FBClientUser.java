package common;

import java.io.Serializable;

public class FBClientUser implements Serializable {
	private String username;
	private String pwd;
	private String hashedPassword;
	
	public FBClientUser() {
		username = null;
		pwd = null;
	}
	
	public FBClientUser(String username, String pwd) {
		this.username = username;
		this.pwd = pwd;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return pwd;
	}
	public void setPassword(String pwd) {
		this.pwd = pwd;
	}
}
