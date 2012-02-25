package facebreak.common;

import java.io.Serializable;

public class GenericPost implements Serializable {
	private int pid;			// unique id for this post
	private int oid;			// id for owner of board
	private int rid;			// region id?
	private int wid;			// id of writer of this post (not always same as owner)
	private String text;		// content, obviously, of the post
	private long timestamp;		// time it was written/modified
	
	public GenericPost() {
		text = null;
	}
	
	public GenericPost(int pid, int oid, int rid, int wid, String text, long timestamp) {
		this.pid = pid;
		this.oid = oid;
		this.rid = rid;
		this.wid = wid;
		this.text = text;
		this.timestamp = timestamp;
	}
	
	public int getPostId() {
		return pid;
	}
	public void setPostId(int pid) {
		this.pid = pid;
	}
	
	public int getOwnerId() {
		return oid;
	}
	public void setOwnerId(int oid) {
		this.oid = oid;
	}
	
	public int getRegionId() {
		return rid;
	}
	public void setRegionId(int rid) {
		this.rid = rid;
	}
	
	public int getWriterId() {
		return wid;
	}
	public void setWriterId(int wid) {
		this.wid = wid;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
