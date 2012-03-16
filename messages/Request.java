/**
 * @author gd226
 * 
 * Requests are messages sent from client to server representing user requests
 * received from the GUI: login, logout, view, post, etc.
 */

package messages;

import java.io.Serializable;


@SuppressWarnings("serial")
public class Request extends GenericMsg implements Serializable {
	private RequestType type;
	
	public Request(RequestType type) {
		super();
		this.type = type;
	}
	
	public RequestType getRequestType() {
		return type;
	}
	
	public void setRequestType(RequestType type) {
		this.type = type;
	}
	
	public enum RequestType {
		EST_SECURE,
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
		DELETE_FRIEND,
		LIST_FRIENDS;
	}
}
