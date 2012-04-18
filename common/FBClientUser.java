package common;

import java.io.Serializable;

public class FBClientUser implements Serializable {
	private static final long serialVersionUID = 1661379995107465209L;
	private String username;
	private String pwd;
	
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
