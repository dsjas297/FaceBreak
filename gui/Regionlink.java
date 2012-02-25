package facebreak.gui;

import javax.swing.JLabel;

public class Regionlink extends JLabel {
	int userID; //associated userID
	String username;
	int regionID;
	String regionname;
	
	public Regionlink(String region, int rid, String user, int uid){
		super(region);
		regionID = rid;
		username = user;
		userID = uid;
	}
	
	public int get_userid(){
		return userID;
	}
	public String get_username(){
		return username;
	}
	public int get_regionid(){
		return regionID;
	}
	public String get_regionname(){
		return regionname;
	}
}
