package facebreak.dummyserver;

import facebreak.common.Post;
import facebreak.common.Profile;
import facebreak.common.Title;
import facebreak.common.User;
import facebreak.common.Post.RegionType;

/*
 * NOT FINALIZED YET!!
 * Note: most of the 
 */

public class DummyQuery {
	
	private static int id_counter = 0;

	// does user with id uid exist already?
	public static boolean userExists(int uid) {
		
		return true;
	}
	
	// does user with username exist already?
	public static boolean userExists(String username) {
		
		return true;
	}
	
	/*
	 *  is username/password combo correct?
	 *  If yes, return the user's id, otherwise return -1
	 */
	public static int loginUser(User user) {
		if(userExists(user.getName())) {
			int uid = id_counter++;	// return user id
			return uid;
		}
		else
			return -1;
	}
	
	/*
	 * Create new user as long as username does not exist already
	 * return -1 if user already exists
	 */
	public static int createUser(User user) {
		if(userExists(user.getName()))
			return -1;
		else {
			// create new user; generate uid
			int uid = id_counter++;
			return uid;
		}
	}
	
	// is userid allowed to view this post?
	public static boolean canViewPost(int userid) {
		return true;
	}
	
	/*
	 * TODO: Not sure how this is going to work. I.e., what params to pass, etc.
	 * Will need to discuss further.
	 */
	public static void editPost() {
		
	}
	
	/*
	 * TODO: Also now sure how this will work...
	 */
	public static Object getPost() {
		return null;
	}
	
	/*
	 * TODO: Not sure how to do this one either...
	 */
	public static void deletePost() {
		
	}
	
	/*
	 * add new post; return true if newPost.getWriter() has permission to 
	 * write on newPost.getOwner()'s board/region
	 */
	public static boolean newPost(Post newPost) {
		return true;
	}
	
	/*
	 * User with uid requestingUid calls to view
	 * the 10 most recent posts on boardOwner's board/region.
	 * See Posting class
	 */
	public static Post[] getBoard(String boardOwner, int requestingUid, RegionType r) {
		return null;
	}
	
	/*
	 * Returns user profile for username;
	 * Viewer has id requesterUid; ONLY return fields if requesterUid has permission
	 * to view them (i.e., if not in same family, don't set family/title) by setting fields on 
	 * profile object passed as param.
	 * Return -1 if username does not actually exist.
	 */
	public static boolean getProfile(int requesterUid, Profile profile) {
		if(!userExists(profile.getUsername()))
			return false;
		// else, set fields in Profile
		return true;
	}
	
	/*
	 * Updates user uid's profile with new information (see UserProfile class)
	 */
	public static void editProfile(int uid, Profile profile) {
		return;
	}
	
	
	public static void changePassword(User user) {
		// changes user's password
		// new password stored in User's pwd field
		String newPassword = user.getPassword();
	}
}
