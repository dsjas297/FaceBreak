package facebreak.common;

import java.io.Serializable;

public class User implements Serializable {
	private int uid;
	private String username;
	
	public User(String username) {
		this.username = username;
	}
	
	public void setId(int uid) {
		this.uid = uid;
	}
	
	public int getId() {
		return uid;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
}
