package gui;

import javax.swing.JLabel;

public class Userlink extends JLabel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int userID; //associated userID
	String username;
	
	public Userlink(String user, int uid){
		super(user);
		userID = uid;
		username = user;
	}
	
	public int get_userid(){
		return userID;
	}
	public String get_username(){
		return username;
	}
}
