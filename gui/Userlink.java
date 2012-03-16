package gui;

import javax.swing.JLabel;

public class Userlink extends JLabel{
	/**
	 * Userlink is a JLabel that contains information about the associated user
	 */
	private static final long serialVersionUID = 1L;
	int userID; //associated userID
	String username;
	
	public Userlink(String name, String user, int uid){
		super(name);
		userID = uid;
		username = user;
	}
	public Userlink(String name, String user){
		super(name);
		username = user;
	}
	
	public int get_userid(){
		return userID;
	}
	public String get_username(){
		return username;
	}
}
