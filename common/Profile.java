package common;

import java.io.Serializable;

public class Profile implements Serializable {
	private static final long serialVersionUID = 1082744487895554393L;
	private String username;
	private String fname;
	private String lname;
	private String family;
	private Title title;
	
	public Profile(String username) {
		this.username = username;
		fname = null;
		lname = null;
		family = null;
		title = null;
	}
	
	public Profile(String username, String fname, String lname) {
		this.username = username;
		this.fname = fname;
		this.lname = lname;
		family = null;
		title = null;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getFname() {
		return fname;
	}
	
	public void setFname(String fname) {
		this.fname = fname;
	}
	
	public String getLname() {
		return lname;
	}
	
	public void setLname(String lname) {
		this.lname = lname;
	}
	
	public String getFamily() {
		return family;
	}
	
	public void setFamily(String family) {
		this.family = family;
	}
	
	public Title getTitle() {
		return title;
	}
	
	public void setTitle(Title title) {
		this.title = title;
	}
}
