package facebreak.networking;

public class Request {
	private int uid;
	private long timestamp;
	private RequestType type;
	private Content details;
	
	public Request(RequestType type) {
		this.type = type;
		details = new Content();
	}
	
	public Request(int uid) {
		this.uid = uid;
		details = new Content();
	}

	public Request(int uid, RequestType type) {
		this.uid = uid;
		this.type = type;
		details = new Content();
	}
	
	public int getUserId() {
		return uid;
	}
	
	public void setUserId(int uid) {
		this.uid = uid;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public RequestType getRequestType() {
		return type;
	}
	
	public void setRequestType(RequestType type) {
		this.type = type;
	}
	
	public void setDetails(Content details) {
		this.details = details;
	}
	
	public Content getDetails() {
		return details;
	}
	
	public enum RequestType {
		LOGIN,
		LOGOUT,
		CREATE_USER,
		CHANGE_PWD,
		VIEW_PROFILE,
		EDIT_PROFILE,
		VIEW_BOARD,
		POST,
		DELETE_POST;
	}
}
