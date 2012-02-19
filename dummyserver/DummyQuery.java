package facebreak.dummyserver;

import facebreak.networking.MyUser;

public class DummyQuery {

	// does user with user id, uid, exist already?
	public boolean userExists(int uid) {
		
		return true;
	}
	
	// does user with username exist already?
	public boolean userExists(String username) {
		
		return true;
	}
	
	// is username/password combo correct?
	public boolean userLogin(MyUser user) {
		
		return true;
	}
	
	public boolean createUser(MyUser user) {
		if(userExists(user.getName()))
			return false;
		else {
			// create new user; generate uid
			return true;
		}
	}
	
	public int getUserId(MyUser user) {
		
		return 0;
	}
	
	// is userid allowed to view this post?
	public boolean canViewPost(int userid) {
		return true;
	}
}
