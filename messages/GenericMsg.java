/**
 * @author gd226
 * 
 * Messages are secure objects that are sent over the network from client to server.
 * Message is either of type Request (client to server) or Reply (server to client)
 */

package messages;

import java.io.Serializable;


@SuppressWarnings("serial")
public class GenericMsg implements Serializable {
	private long timestamp;
	private long count;
	private GenericContent details;
	
	public GenericMsg() {
		details = new GenericContent();
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public void setTimestamp() {
		timestamp = System.currentTimeMillis();
	}
	
	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public void setDetails(GenericContent details) {
		this.details = details;
	}
	
	public GenericContent getDetails() {
		return details;
	}
}

