/**
 * @author gd226
 * 
 * Replies are messages from server to client in response to a request from the client
 * (i.e., no preemptive 'reply' from server). Typically returns an Error message and 
 * Content corresponding to client's request (e.g., a profile or region or list of friends)
 */

package messages;

import java.io.Serializable;

import common.Error;


@SuppressWarnings("serial")
public class Reply extends GenericMsg implements Serializable {
	private Error error;
	
	public Reply() {
		super();
	}
	
	public Error getReturnError() {
		return error;
	}
	public void setReturnError(Error error) {
		this.error = error;
	}
}
