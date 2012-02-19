package facebreak.networking;


public class Posting extends Content {
	private int pid;	// id for particular post
	private int uid;	// id for user (owner of post)
	private int rid; 	// region id
	private String contents;
	private RegionAccess region;

	public enum RegionAccess {
		PUBLIC, PRIVATE, COVERT;
	}

	
	public Posting(int pid, int uid, int rid) {
		this.pid = pid;
		this.uid = uid;
		this.rid = rid;
	}
	
	public Posting(String contents) {
		this.contents = contents;
	}
}
