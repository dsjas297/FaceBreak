package facebreak.dummyserver;

import java.util.ArrayList;

import facebreak.common.Post;
import facebreak.common.Profile;
import facebreak.common.Title;
<<<<<<< HEAD
<<<<<<< HEAD
import facebreak.common.FBClientUser;
=======
import facebreak.common.User;
>>>>>>> 6f1c32b9fe3498d590cf21f7b1f8afe07a4d1a7c
=======
import facebreak.common.FBClientUser;
>>>>>>> cfce48f535447b86f36cb68f21750d723283bd7f
import facebreak.common.Post.RegionType;

/*
 * NOT FINALIZED YET!!
 * Note: most of the 
 */

public class DummyQuery {
	
<<<<<<< HEAD
<<<<<<< HEAD
	private static int id_counter = 1;
	private static Profile tmpProfile;
=======
	private static int id_counter = 0;
>>>>>>> 6f1c32b9fe3498d590cf21f7b1f8afe07a4d1a7c

=======
	private static int id_counter = 1;
	private static Profile tmpProfile;
	private static ArrayList<Post> myBoard;
	
>>>>>>> cfce48f535447b86f36cb68f21750d723283bd7f
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
<<<<<<< HEAD
<<<<<<< HEAD
	public static int loginUser(FBClientUser user) {
		if(userExists(user.getUsername())) {
=======
	public static int loginUser(User user) {
		if(userExists(user.getName())) {
>>>>>>> 6f1c32b9fe3498d590cf21f7b1f8afe07a4d1a7c
=======
	public static int loginUser(FBClientUser user) {
		if(userExists(user.getUsername())) {
>>>>>>> cfce48f535447b86f36cb68f21750d723283bd7f
			int uid = id_counter++;	// return user id
			return uid;
		}
		else
			return -1;
<<<<<<< HEAD
	}
	
	/*
	 * Create new user as long as username does not exist already
	 * return -1 if user already exists
	 */
	public static int createUser(FBClientUser user) {
//		if(userExists(user.getUsername()))
//			return -1;
//		else {
//			// create new user; generate uid
//			int uid = id_counter++;
//			return uid;
//		}
		return id_counter++;
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
=======
	}
	
	/*
	 * Create new user as long as username does not exist already
	 * return -1 if user already exists
	 */
	public static int createUser(FBClientUser user) {
//		if(userExists(user.getUsername()))
//			return -1;
//		else {
//			// create new user; generate uid
//			int uid = id_counter++;
//			return uid;
//		}
		return id_counter++;
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
>>>>>>> 6f1c32b9fe3498d590cf21f7b1f8afe07a4d1a7c
		
	}
	
	/*
<<<<<<< HEAD
=======
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
>>>>>>> 6f1c32b9fe3498d590cf21f7b1f8afe07a4d1a7c
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
	public static ArrayList<Post> getBoard(String boardOwner, int requestingUid, RegionType r) {
		
		return null;
	}
	
	/*
	 * Returns user profile for username;
	 * Viewer has id requesterUid; ONLY return fields if requesterUid has permission
	 * to view them (i.e., if not in same family, don't set family/title) by setting fields on 
	 * profile object passed as param.
	 * Return -1 if username does not actually exist.
	 */
<<<<<<< HEAD
<<<<<<< HEAD
	public static Profile getProfile(int requesterUid, Profile profile) {
//		if(!userExists(profile.getUsername()))
//			return false;
		// else, set fields in Profile
		return tmpProfile;
=======
	public static boolean getProfile(int requesterUid, Profile profile) {
		if(!userExists(profile.getUsername()))
			return false;
		// else, set fields in Profile
		return true;
>>>>>>> 6f1c32b9fe3498d590cf21f7b1f8afe07a4d1a7c
=======
	public static Profile getProfile(int requesterUid, Profile profile) {
//		if(!userExists(profile.getUsername()))
//			return false;
		// else, set fields in Profile
		return tmpProfile;
>>>>>>> cfce48f535447b86f36cb68f21750d723283bd7f
	}
	
	/*
	 * Updates user uid's profile with new information (see UserProfile class)
	 */
	public static void editProfile(int uid, Profile profile) {
<<<<<<< HEAD
<<<<<<< HEAD
		tmpProfile = profile;
=======
>>>>>>> 6f1c32b9fe3498d590cf21f7b1f8afe07a4d1a7c
=======
		tmpProfile = profile;
>>>>>>> cfce48f535447b86f36cb68f21750d723283bd7f
		return;
	}
	
	
<<<<<<< HEAD
<<<<<<< HEAD
	public static void changePassword(FBClientUser user) {
=======
	public static void changePassword(User user) {
>>>>>>> 6f1c32b9fe3498d590cf21f7b1f8afe07a4d1a7c
=======
	public static void changePassword(FBClientUser user) {
>>>>>>> cfce48f535447b86f36cb68f21750d723283bd7f
		// changes user's password
		// new password stored in User's pwd field
		String newPassword = user.getPassword();
	}
}
