package server;

public enum FBFile {
	GLOBAL_UID(1, "uid_counter"),
	GLOBAL_ALLUSERS(1, "users"),
	USER_INFO(1, "info"),
	USER_FRIENDS(1, "friends"),
	USER_UNTRUST(1, "untrustworthy"),
	USER_IMAGE(1, "avatar.jpg"),
	REGION_POSTS(1, "regionInfo"),
	REGION_INFO(1, "posts"),
	REGION(0, "regions");
	
	protected int code;
	protected String name;
	
	private FBFile(int code, String name) {
		this.name = name;
	}
}
