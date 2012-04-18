package gui;

import javax.swing.JButton;

public class NotifButton extends JButton{
	private int notif_id; //notification ID
	private String username; //user who sent notification
	private String newtitle; //title change, if any
	
	//for "Accept" title changes
	public NotifButton(String text, int notif_id, String username, String newtitle){
		super(text);
		this.notif_id = notif_id;
		this.username = username;
		this.newtitle = newtitle;
	}
	//for everything else
	public NotifButton(String text, int notif_id, String username){
		super(text);
		this.notif_id = notif_id;
		this.username = username;
	}
	
	public int get_notif_id(){
		return notif_id;
	}
	public String get_username(){
		return username;
	}
	public String get_newtitle(){
		return newtitle;
	}
	
	
}
