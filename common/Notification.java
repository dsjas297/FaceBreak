package common;

import java.io.Serializable;

public class Notification implements Serializable {

	private static final long serialVersionUID = 6613012381243813845L;
	private int nid;
	private NotificationType type;
	private String username;
	private int newRank;
	
	
	public Notification(NotificationType type) {
		this.type = type;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setRank(int newRank) {
		this.newRank = newRank;
	}
	
	public void setId(int nid) {
		this.nid = nid;
	}
	
	public String getUsername() {
		return username;
	}
	
	public NotificationType getType() {
		return type;
	}
	
	/*
	 * boss checks that getNewRank > Title.BOSS.rank (only one boss)
	 */
	public int getNewRank() {
		return newRank;
	}
	
	public int getId() {
		return nid;
	}
	
	public enum NotificationType {
		NewFriend,
		ChangeRank;
	}
}