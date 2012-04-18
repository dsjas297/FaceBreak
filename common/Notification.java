package common;

import java.io.Serializable;

public class Notification extends GenericPost implements Serializable {
	
	public enum NotificationType {
		TITLE, FRIEND;
	}
	
	private static int nextID = 0;
	private int id;
	private String requesterName;
	private int requesterID;
	private NotificationType type;
	private String requestMessage;
	
	public Notification(String requesterName, int requesterID, NotificationType type, String requestMessage) {
		this.id = nextID++;
		this.requesterName = requesterName;
		this.requesterID = requesterID;
		this.type = type;
		this.requestMessage = requestMessage;
	}
	
	public Notification(int id, String requesterName, int requesterID, NotificationType type, String requestMessage) {
		this.id = id;
		this.requesterName = requesterName;
		this.requesterID = requesterID;
		this.type = type;
		this.requestMessage = requestMessage;
	}
	
	public String toString(){
		return Integer.toString(id) + " " + this.requesterName + " "
				+ Integer.toString(this.requesterID) + " " + type + " " + this.requestMessage;
	}
}