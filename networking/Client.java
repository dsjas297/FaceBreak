package networking;

import java.io.IOException;
import java.util.ArrayList;

import common.Notification;
import common.Post;
import common.Profile;
import common.Region;
import common.Error;

public interface Client {

    /**
     * Logs in user with (username, pwd) combo. Fails if another user is already logged in.
     * Creates new socket and initializes input/output streams;
     * will close connection and close io streams on failure.
     * @throws IOException 
     */
    public Error login(String username, String pwd) throws ClassNotFoundException, IOException;

    /*
     * Log out of current session
     */
    public Error logout() throws ClassNotFoundException; 

    /*
     * Cannot create a new user while another user is logged in on this machine.
     * If error occurs, automatically closes connection.
     * Otherwise, creates user and automatically logs them in.
     */
    public Error createUser(String username, String pwd) throws ClassNotFoundException, IOException;

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
     * Edit your own profile by creating new Profile object and setting
     * whatever fields you want to change
     */
    public Error editProfile(Profile myProfile) throws ClassNotFoundException; 

    /*
     * Post a new item on someone's region by creating new Post object and setting
     * the necessary fields
     */
    public Error post(Post newPost) throws ClassNotFoundException; 

    /*
     * View my or someone else's board/region by creating new Region object and
     * setting necessary fields
     */
    public Error viewRegion(Region region) throws ClassNotFoundException; 

    /*
     * TODO: implement deletion
     */
    public Error deletePost(int badPostId); 

    /*
     * This user adds a friend of username
     */
    public Error addFriend(String friendName) throws ClassNotFoundException; 

    /*
     * This user deletes a friend of username
     */
    public Error deleteFriend(String exFriendName) throws ClassNotFoundException; 

    /**
     * Create empty arraylist friends; getFriendsList will populate arraylist with
     * usernames of all thisuser's friends
     * @param friends - empty ArrayList
     * @return	Error
     * @throws ClassNotFoundException
     */
    public Error getFriendsList(ArrayList<String> friends) throws ClassNotFoundException;

    /**
     * 
     * @param notifications	- empty ArrayList
     * @return
     * @throws ClassNotFoundException
     */
    public Error getNotifications(ArrayList<Notification> notifications) throws ClassNotFoundException; 

    /**
     * 
     * @param id - id of the notification
     * @param approve - True for approve, False for disapprove
     * @return 
     * @throws ClassNotFoundException
     */
    public Error respondToNotification(int id, boolean approve) throws ClassNotFoundException;

    /**
     * 
     * @param rid - id of the region
     * @param usernames	- usernames of all the people to be granted permission for this region
     * @return
     * @throws ClassNotFoundException
     */
	public Error addToCovert(int rid, ArrayList<String> usernames) throws ClassNotFoundException;
}

