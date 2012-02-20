package facebreak.common;

import java.io.Serializable;

public class User implements Serializable {
	private int uid;
	private String name;
	private String pwd;
	private String hashedPassword;
	
	public User() {
		this.name = new String();
		this.pwd = new String();
	}
	
	public User(String name, String password) {
		this.name = new String(name);
		this.pwd = new String(password);
	}
	
	public int getId() {
		return uid;
	}
	public void setId(int uid) {
		this.uid = uid;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return pwd;
	}
	public void setPassword(String pwd) {
		this.pwd = pwd;
	}
}
