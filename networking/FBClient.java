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
import facebreak.common.FBClientUser;
import facebreak.networking.Request.RequestType;

public class FBClient {
	private Socket socket;
	private InetAddress serverAddr;
	private FBClientUser user;
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

	public void setCurrentUser(FBClientUser user) {
		this.user = user;
	}

	public void setCurrentUser(String username, String pwd) {
		user = new FBClientUser(username, pwd);
	}

	public FBClientUser getCurrentUser() {
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
		inStream = null;
		outStream = null;
		socket = null;
		user = null;
		System.out.println("Closing connection");
	}

	public Error login(String username, String pwd) throws ClassNotFoundException {
		// cannot login while there's another user logged in
		if(user != null)
			return Error.MALFORMED_REQUEST;
		
		user = new FBClientUser(username, pwd);
		return login();
	}
	
	/*
	 * Creates new socket and initializes input/output streams. If does not
	 * succeed, closes connection; must create new client
	 */
	public Error login() throws ClassNotFoundException {
		// open new connection
		try {
			socket = new Socket(serverAddr, port);
			outStream = new ObjectOutputStream(socket.getOutputStream());
			inStream = new ObjectInputStream(socket.getInputStream());

			// create request object
			Request login = new Request(RequestType.LOGIN);
			login.getDetails().setUser(user);
			login.setTimestamp(System.currentTimeMillis());

			outStream.writeObject(login);

			Reply serverReply = (Reply) inStream.readObject();
			Error e = serverReply.getReturnError();

			if (e == Error.SUCCESS)
				user.setId(serverReply.getContents().getUser().getId());
			else
				closeConnection();

			return e;
		} catch (IOException ioe) {
			return Error.CONNECTION;
		}
	}

	/*
	 * Logout of current session
	 */
	public Error logout() throws ClassNotFoundException {
		// sanity check
		if(socket == null || user == null)
			return Error.LOGIN;

		Request logout = new Request(user.getId(), RequestType.LOGOUT);
		logout.setTimestamp(System.currentTimeMillis());
		
		try {
			outStream.writeObject(logout);
			Error e = ((Reply)inStream.readObject()).getReturnError();
			System.out.println("Logging out.");
			closeConnection();
			
			return e;
		} catch (IOException ioe) {
			user = null;
			socket = null;
			return Error.CONNECTION;
		}
	}

	/*
	 * Cannot create a new user while another user is logged in on this machine.
	 * If error occurs, automatically closes connection
	 */
	public Error createUser(String username, String pwd) throws ClassNotFoundException {

		if(socket != null && outStream != null && inStream != null)
			return Error.MALFORMED_REQUEST;

		if(user != null)
			return Error.LOGIN;
		
		user = new FBClientUser(username, pwd);

		try {
			socket = new Socket(serverAddr, port);
			outStream = new ObjectOutputStream(socket.getOutputStream());
			inStream = new ObjectInputStream(socket.getInputStream());

			Request createUser = new Request(RequestType.CREATE_USER);
			createUser.getDetails().setUser(user);
			createUser.setTimestamp(System.currentTimeMillis());

			outStream.writeObject(createUser);

			Reply serverReply = (Reply) inStream.readObject();
			Error e = serverReply.getReturnError();

			if(e == Error.SUCCESS)
				user = serverReply.getContents().getUser();
			else
				closeConnection();
			
			return e;
		} catch(IOException e1) {
			return Error.CONNECTION;
		}
	}

	/*
	 * Change password by providing new password string
	 */
	public Error changePassword(String pwd) throws ClassNotFoundException {
		// sanity check
		if (socket == null || user == null)
			return Error.LOGIN;

		FBClientUser tmpUser = user;
		tmpUser.setPassword(pwd);
		Request changePwd = new Request(user.getId(), RequestType.CHANGE_PWD);
		changePwd.getDetails().setUser(tmpUser);
		changePwd.setTimestamp(System.currentTimeMillis());
		
		try {
			outStream.writeObject(changePwd);
			
			Reply serverReply = (Reply)inStream.readObject();
			Error e = serverReply.getReturnError();
			
			if (e == Error.SUCCESS)
				user.setPassword(pwd);
			return e;
		} catch (IOException e1) {
			return Error.CONNECTION;
		}
	}

	/*
	 * View someone's profile. Create new Profile object and set the username of
	 * person you want to view
	 */
	public Error viewProfile(Profile profile) throws ClassNotFoundException {
		// sanity check
		if (socket == null || user == null)
			return Error.LOGIN;

		Request viewProfile = new Request(user.getId(), RequestType.VIEW_PROFILE);
		viewProfile.getDetails().setProfile(profile);
		viewProfile.setTimestamp(System.currentTimeMillis());

		try {
			outStream.writeObject(viewProfile);
			
			Reply serverReply = (Reply)inStream.readObject();
			Error e = serverReply.getReturnError();
			
			if (e == Error.SUCCESS) {
				Profile tmp = serverReply.getContents().getProfile();
				profile.setFname(tmp.getFname());
				profile.setLname(tmp.getLname());
				profile.setFamily(tmp.getFamily());
				profile.setTitle(tmp.getTitle());
			}
			
			return e;
		} catch (IOException e1) {
			return Error.CONNECTION;
		}
	}

	/*
	 * Edit your own profile by creating new Profile object and editting
	 * whatever fields
	 */
	public Error editProfile(Profile myProfile) throws ClassNotFoundException {
		// sanity check
		if (socket == null || user == null)
			return Error.LOGIN;

		Request editProfile = new Request(user.getId(),	RequestType.EDIT_PROFILE);
		editProfile.getDetails().setProfile(myProfile);
		editProfile.setTimestamp(System.currentTimeMillis());
		
		try {
			outStream.writeObject(editProfile);
			
			Reply serverReply = (Reply)inStream.readObject();
			Error e = serverReply.getReturnError();
			
			if (e == Error.SUCCESS)
				myProfile = serverReply.getContents().getProfile();
			
			return e;
		} catch (IOException e1) {
			return Error.CONNECTION;
		}
	}
	
	/*
	 * Post a new item on someone's region creating new Post object and setting
	 * the necessary fields
	 */
	public Error post(Post newPost) throws ClassNotFoundException {
		// sanity check
		if (socket == null || user == null)
			return Error.LOGIN;

		newPost.setWriterId(user.getId());
		newPost.setWriterName(user.getUsername());
		Request postRequest = new Request(user.getId(), RequestType.POST);
		postRequest.getDetails().setPost(newPost);
		postRequest.setTimestamp(System.currentTimeMillis());
		
		try {
			outStream.writeObject(postRequest);
			Reply serverReply = (Reply)inStream.readObject();
			
			return serverReply.getReturnError();
		} catch (IOException ioe) {
			return Error.CONNECTION;
		}
	}

	/*
	 * TODO: implement view board...how to do this?
	 */
	public Error viewBoard(Region board) throws ClassNotFoundException {
		// sanity check
		if (socket == null || user == null)
			return Error.LOGIN;

		Request viewBoard = new Request(user.getId(), RequestType.VIEW_BOARD);
		viewBoard.getDetails().setBoard(board);
		viewBoard.setTimestamp(System.currentTimeMillis());

		try {
			outStream.writeObject(viewBoard);
			Reply serverReply = (Reply)inStream.readObject();
			Region tmp = serverReply.getContents().getBoard();
			board.setPosts(tmp.getPosts());
			
			return serverReply.getReturnError();
		} catch (IOException ioe) {
			return Error.CONNECTION;
		}
	}

	/*
	 * TODO: implement deletion
	 */
	public Error deletePost(int badPostId) {
		// sanity check
		if (socket == null || user == null)
			return Error.LOGIN;
		
		Request delete = new Request(user.getId(), RequestType.DELETE_POST);
		delete.getDetails().setPost(new Post());

		return Error.SUCCESS;
	}
}
