package facebreak.common;

import java.io.Serializable;

public class Post implements Serializable {
	private int ownerId;			// id for owner of board
	private String ownername;
	private int pid;
	private RegionType region;
	private int writerId;		// id of writer of this post (not always same as owner)
	private String writer;		// username of the writer of this post 
	private String text;		// content, obviously, of the post

	public enum RegionType {
		PUBLIC, PRIVATE, COVERT;
	}
	
	public Post(int owner, RegionType region, String writer, String text) {
		this.ownerId = owner;
		this.region = region;
		this.writer = writer;
		this.text = text;
	}
	
	public void setPid(int pid) {
		this.pid = pid;
	}
	
	public int getPid() {
		return pid;
	}

	public RegionType getRegion() {
		return region;
	}

	public void setRegion(RegionType region) {
		this.region = region;
	}

	public int getWriterId() {
		return writerId;
	}

	public void setWriterId(int writerId) {
		this.writerId = writerId;
	}

	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
