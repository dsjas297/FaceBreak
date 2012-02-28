package networking;

import common.Error;
import common.Post;
import common.Profile;
import common.Region;

public interface Client {

	/**
	 * Logs in user with (username, pwd) combo. Fails if another user is already
	 * logged in. Creates new socket and initializes input/output streams; will
	 * close connection and close io streams on failure.
	 * @throws ClassNotFoundException 
	 */
	public Error login(String username, String pwd) throws ClassNotFoundException;

	/*
	 * Log out of current session
	 */
	public Error logout() throws ClassNotFoundException;

	/*
	 * Cannot create a new user while another user is logged in on this machine.
	 * If error occurs, automatically closes connection. Otherwise, creates user
	 * and automatically logs them in.
	 */
	public Error createUser(String username, String pwd) throws ClassNotFoundException;

	/*
	 * Change password by providing new password string
	 */
	public Error changePassword(String pwd) throws ClassNotFoundException;

	/*
	 * View someone's profile. Create new Profile object and set the username of
	 * person you want to view
	 */
	public Error viewProfile(Profile profile) throws ClassNotFoundException;

	/*
	 * Edit your own profile by creating new Profile object and setting whatever
	 * fields you want to change
	 */
	public Error editProfile(Profile myProfile) throws ClassNotFoundException;

	/*
	 * Post a new item on someone's region by creating new Post object and
	 * setting the necessary fields
	 */
	public Error post(Post newPost) throws ClassNotFoundException;

	/*
	 * View my or someone else's board/region by creating new Region object and
	 * setting necessary fields
	 */
	public Error viewBoard(Region board) throws ClassNotFoundException;

	/*
	 * TODO: implement deletion
	 */
	public Error deletePost(int badPostId) throws ClassNotFoundException;


	/*
	 * This user adds a friend of username
	 */
	public Error addFriend(String username) throws ClassNotFoundException;

	/*
	 * This user deletes a friend of username
	 */
	public Error deleteFriend(String username) throws ClassNotFoundException;
}
