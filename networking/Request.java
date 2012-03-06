package networking;

import java.io.Serializable;

public class Request implements Serializable {
	private long timestamp;
	private RequestType type;
	
	public Request(RequestType type) {
		this.type = type;
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
	
	public enum RequestType {
		LOGIN,
		LOGOUT,
		CREATE_USER,
		CHANGE_PWD,
		VIEW_PROFILE,
		EDIT_PROFILE,
		VIEW_BOARD,
		POST,
		DELETE_POST,
		ADD_FRIEND,
		DELETE_FRIEND;
	}
}
