package facebreak.networking;

import java.awt.image.BufferedImage;


public class UserProfile extends Content {
	private String username;
	private String fname;
	private String lname;
	private String family;
	private Title title;
	private BufferedImage photo;
	
	public UserProfile(String username) {
		this.username = username;
	}
	
	public UserProfile(String username, String fname, String lname) {
		this.username = username;
		this.fname = fname;
		this.lname = lname;
	}
	
	public String getUsername() {
		return username;
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
	
	public BufferedImage getPhoto() {
		return photo;
	}
	
	public void setPhoto(BufferedImage photo) {
		this.photo = photo;
	}
}
