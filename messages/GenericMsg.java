/**
 * @author gd226
 * 
 * Messages are secure objects that are sent over the network from client to server.
 * Message is either of type Request (client to server) or Reply (server to client)
 */

package messages;

import java.io.Serializable;


public class GenericMsg implements Serializable {
	private static final long serialVersionUID = -7370780819488374980L;
	private long timestamp;
	private long count;
	private Serializable details;
	private int id;
	private byte[] checksum;
	
	public GenericMsg() {
		count = 0;
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
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public void setDetails(Serializable details) {
		this.details = details;
	}
	
	public Serializable getDetails() {
		return details;
	}
	
	public byte[] getChecksum() {
		return checksum;
	}
}

