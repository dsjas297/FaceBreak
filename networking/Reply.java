package facebreak.networking;

import java.io.Serializable;
import java.util.Date;

public class Reply implements Serializable {
	
	private long timestamp;
	private Error error;
	private Content contents;
	
	public Reply() {
		contents = new Content();
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
	
	public Content getContents() {
		return contents;
	}
	
	public void setContents(Content contents) {
		this.contents = contents;
	}
}
