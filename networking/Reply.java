package networking;

import java.io.Serializable;

import common.Error;

public class Reply implements Serializable {
	private long timestamp;
	private Error error;
	
	public Reply() {
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public Error getReturnError() {
		return error;
	}
	
	public void setReturnError(Error error) {
		this.error = error;
	}
}
