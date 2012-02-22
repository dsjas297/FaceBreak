package facebreak.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import facebreak.common.Post;
import facebreak.common.Profile;
import facebreak.common.Region;
import facebreak.common.User;
import facebreak.common.Post.RegionType;
import facebreak.networking.Request.RequestType;

public class FBClient {
	private Socket socket;
	private InetAddress serverAddr;
	private User user;
	private ObjectOutputStream outStream;
	private ObjectInputStream inStream;
	
	private static final int port = 4444;
	
	public FBClient() throws UnknownHostException {
		serverAddr = InetAddress.getLocalHost();
		user = null;
		socket = null;
		outStream = null;
		inStream = null;
	}

	public FBClient(InetAddress serverAddr) {
		this.serverAddr = serverAddr;
		user = null;
		socket = null;
		outStream = null;
		inStream = null;
	}
	
	public FBClient(String username, String pwd) throws UnknownHostException {
		serverAddr = InetAddress.getLocalHost();
		user = new User(username, pwd);
		socket = null;
		outStream = null;
		inStream = null;
	}
	
	public void setCurrentUser(User user) {
		this.user = user;
	}

	public void setCurrentUser(String username, String pwd) {
		user = new User(username, pwd);
	}
	
	public User getCurrentUser() {
		return user;
	}
	
	/*
	 * Send myRequest over network; get reply back from server
	 */
	public Reply talkToServer(Request myRequest) {
		Reply serverReply;
		myRequest.setTimestamp(System.currentTimeMillis());
		try {
			// send request to server
			outStream.writeObject(myRequest);
		
			// get reply
			serverReply = (Reply) inStream.readObject();
		} catch (IOException e) {
			serverReply = new Reply();
			serverReply.setReturnError(Error.CONNECTION);
		} catch (ClassNotFoundException e) {
			serverReply = new Reply();
			serverReply.setReturnError(Error.UNKNOWN_ERROR);
		}
		
		return serverReply;
	}
	
	public void closeConnection() throws IOException {
		if(inStream != null)
			inStream.close();
		if(outStream != null)
			outStream.close();
		if(socket != null)
			socket.close();
		user = null;
	}
	
	/*
	 * Creates new socket and initializes input/output streams
	 */
	public Error login() throws IOException {
		// open new connection
		socket = new Socket(serverAddr, port);
		outStream = new ObjectOutputStream(socket.getOutputStream());
		inStream = new ObjectInputStream(socket.getInputStream());

		// sanity check
		if(socket == null || user == null)
			return Error.LOGIN;
		
		// create request object
		Request login = new Request(RequestType.LOGIN);
		login.getDetails().setUser(user);
		
		Reply serverReply =  talkToServer(login);
		Error e = serverReply.getReturnError();
		
		if(e == Error.SUCCESS) 
			user.setId(serverReply.getContents().getUser().getId());
		else 
			closeConnection();
		
		return e;
	}
	
	/*
	 * Logout of current session
	 */
	public Error logout() {
		// sanity check
		if(socket == null || user == null)
			return Error.LOGIN;
		
		Request logout = new Request(user.getId(), RequestType.LOGOUT);

		Error e = talkToServer(logout).getReturnError();
		
		try {
			closeConnection();
		} catch (IOException ioe) {
			return Error.CONNECTION;
		}

		return e;
	}

	/*
	 * Create a new user by creating a User object and setting the name and pwd fields
	 */
	public Error createUser(User newUser) {
		// sanity check
		if(socket == null || user == null)
			return Error.LOGIN;
		
		Request createUser = new Request(RequestType.CREATE_USER);
		createUser.getDetails().setUser(newUser);
		Reply serverReply = talkToServer(createUser);
		Error e = serverReply.getReturnError();
		
		if(e == Error.CONNECTION) {
			try {
				closeConnection();
			} catch (IOException ioe) {
				return e;
			}
		}
		else if(e == Error.SUCCESS) {
			user = serverReply.getContents().getUser();
		}
		return e;
	}
	
	/*
	 * Change password by providing new password string
	 */
	public Error changePassword(String pwd) {
		// sanity check
		if(socket == null || user == null)
			return Error.LOGIN;
		
		String oldpwd = user.getPassword();
		user.setPassword(pwd);
		Request changePwd = new Request(user.getId(), RequestType.CHANGE_PWD);
		changePwd.getDetails().setUser(user);
		
		Error e = talkToServer(changePwd).getReturnError();
		if(e != Error.SUCCESS)
			user.setPassword(oldpwd);
		
		return e;
	}
	
	/*
	 * Post a new item on someone's region creating new Post object and setting the 
	 * necessary fields
	 */
	public Error post(int uid, Post newPost) {
		// sanity check
		if(socket == null || user == null)
			return Error.LOGIN;
		
		Request postRequest = new Request(user.getId(), RequestType.POST);
		talkToServer(postRequest);
		
		return Error.SUCCESS;
	}

	/*
	 * View someone's board/region by creating new Region object and setting 
	 * ownername and regiontype fields
	 */
	public Error viewBoard(int uid, Region board) {
		// sanity check
		if(socket == null || user == null)
			return Error.LOGIN;
		
		Request viewBoard = new Request(uid, RequestType.VIEW_BOARD);
		
		return Error.SUCCESS;
	}
	
	/*
	 * View someone's profile. Create new Profile object and set the username of
	 * person you want to view
	 */
	public Error viewProfile(Profile profile) {
		// sanity check
		if(socket == null || user == null)
			return Error.LOGIN;
		
		Request viewProfile = new Request(user.getId(), RequestType.VIEW_PROFILE);
		viewProfile.getDetails().setProfile(profile);
		
		Reply serverReply = talkToServer(viewProfile);
		Error e = serverReply.getReturnError();
		
		if(e == Error.SUCCESS)
			profile = serverReply.getContents().getProfile();
		
		return e;
	}
	
	/*
	 * Edit your own profile by creating new Profile object and editting whatever fields
	 */
	public Error editProfile(int uid, Profile myProfile) {
		// sanity check
		if(socket == null || user == null)
			return Error.LOGIN;
		
		Request editProfile = new Request(user.getId(), RequestType.EDIT_PROFILE);
		editProfile.getDetails().setProfile(myProfile);
		
		Reply serverReply = talkToServer(editProfile);
		Error e = serverReply.getReturnError();
		
		if(e == Error.SUCCESS)
			myProfile = serverReply.getContents().getProfile();
		
		return e;
	}
	
	/*
	 * delete a post by creating new Post object and setting ownername, pid, and region fields
	 * Not sure how to do this one...
	 */
	public Error delete(int uid, Post badPost) {
		// sanity check
		if(socket == null || user == null)
			return Error.LOGIN;
		
		return Error.SUCCESS;
	}
}
