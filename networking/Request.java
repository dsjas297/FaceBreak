package facebreak.networking;

import java.util.Date;

public class Request {
	private MyUser user;
	private Date timestamp;
	private RequestType type;
	
	public Request() {
		user = null;
	}
	
	public Request(MyUser user) {
		this.user = user;
	}
	
	public MyUser getUser() {
		return user;
	}
	
	public void setUser(MyUser user) {
		this.user = user;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public RequestType getRequestType() {
		return type;
	}
	
	public void setRequestType(RequestType type) {
		this.type = type;
	}
}
