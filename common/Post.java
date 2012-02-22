package facebreak.common;

import java.io.Serializable;

public class Post extends GenericPost implements Serializable {
	private String ownerName;
	private String writerName;
	private RegionType region;

	public enum RegionType {
		PUBLIC, PRIVATE, COVERT;
	}
	
	public Post() {
		super();
		ownerName = null;
	}
	
	public Post(int pid, int oid, int rid, int wid, String text, long timestamp, String ownerName, String writerName) {
		super(pid, oid, rid, wid, text, timestamp);
		this.ownerName = ownerName;
		this.writerName = writerName;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getWriterName() {
		return writerName;
	}

	public void setWriterName(String writerName) {
		this.writerName = writerName;
	}

	public RegionType getRegion() {
		return region;
	}

	public void setRegion(RegionType region) {
		this.region = region;
	}
}
