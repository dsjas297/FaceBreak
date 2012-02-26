package facebreak.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import facebreak.common.Post;
import facebreak.common.Profile;
import facebreak.common.FBClientUser;
import facebreak.common.Region;
import facebreak.dummyserver.DummyQuery;
import facebreak.networking.Request.RequestType;

public class FBClientHandler extends Thread {
	private Socket clientSocket;
	private AuthenticatedUser authUser;
	private ObjectOutputStream outStream;
	private ObjectInputStream inStream;
	
	public FBClientHandler(Socket clientSocket) {
		this.clientSocket = clientSocket;
		outStream = null;
		inStream = null;
	}

	public Reply parseRequest(Request r) {
		RequestType type = r.getRequestType();
		FBClientUser clientUser = r.getDetails().getUser();
		Reply myReply = new Reply();
		
		// sanity checks
		if (clientSocket == null) {
			myReply.setReturnError(Error.LOGIN);
			return myReply;
		}
		
		if(type != RequestType.LOGIN && type != RequestType.CREATE_USER) {
			if((authUser == null || !authUser.isLoggedIn())) {
				myReply.setReturnError(Error.LOGIN);
				return myReply;
			}
			// spoofing user ids?
//			else if(clientUser.getId() != authUser.getId()) {
//				myReply.setReturnError(Error.MALFORMED_REQUEST);
//				return myReply;
//			}
		}
		
		switch (type) {
			case LOGIN:
				myReply = processLogin(clientUser);
				break;
			case LOGOUT:
				myReply = processLogout();
				break;
			case CREATE_USER:
				myReply = processCreateUser(clientUser);
				break;
			case CHANGE_PWD:
				myReply = processChangePwd(clientUser);
				break;
			case VIEW_BOARD: {
				Region region = r.getDetails().getBoard();
				if(region == null)
					myReply.setReturnError(Error.MALFORMED_REQUEST);
				else
					myReply = processViewBoard(region);
				break;
			}
			case VIEW_PROFILE: {
				Profile profile = r.getDetails().getProfile();
				if(profile == null)
					myReply.setReturnError(Error.MALFORMED_REQUEST);
				else
					myReply = processViewProfile(profile);
				break;
			}
			case EDIT_PROFILE: {
				Profile profile = r.getDetails().getProfile();
				if(profile == null)
					myReply.setReturnError(Error.MALFORMED_REQUEST);
				else
					myReply = processEditProfile(profile);
				break;
			}
			case POST: {
				Post post = r.getDetails().getPost();
				if(post == null)
					myReply.setReturnError(Error.MALFORMED_REQUEST);
				else
					myReply = processCreatePost(post);
				break;
			}
			case DELETE_POST: {
				Post post = r.getDetails().getPost();
				if(post == null)
					myReply.setReturnError(Error.MALFORMED_REQUEST);
				else
					myReply = processDeletePost();
				break;
			}
			default:
				myReply.setReturnError(Error.MALFORMED_REQUEST);
				break;
		}
		myReply.setTimestamp(System.currentTimeMillis());
		return myReply;
	}
	
	public Reply processLogin(FBClientUser client) {
		Reply r = new Reply();
		
		int uid = DummyQuery.loginUser(client);
		// if not valid username/passwd combo, return only error
		if(uid == -1) {
			r.setReturnError(Error.USERNAME_PWD);
			authUser = null;
			return r;
		}
		
		// otherwise authenticate user and reply with success
		authUser = new AuthenticatedUser(client.getUsername());
		authUser.setId(uid);
		authUser.logIn();
		
		client.setId(uid);
		r.getContents().setUser(client);
		r.setReturnError(Error.SUCCESS);
		return r;
	}

	public Reply processLogout() {
		Reply r = new Reply();
		authUser.logOut();
		r.setReturnError(Error.SUCCESS);
		return r;
	}

	public Reply processCreateUser(FBClientUser client) {
		Reply r = new Reply();
		
		int uid = DummyQuery.createUser(client);
		// if username already exists
		if(uid == -1) {
			r.setReturnError(Error.DUPLICATE_USER);
			authUser = null;
			return r;
		}
		
		authUser = new AuthenticatedUser(client.getUsername());
		
		// assumes when new user is created successfully, 
		// user is logged in automatically
		authUser.setId(uid);
		authUser.logIn();

		client.setId(uid);
		r.getContents().setUser(client);
		r.setReturnError(Error.SUCCESS);
		
		return r;
	}

	public Reply processChangePwd(FBClientUser client) {
		Reply r = new Reply();
		
		DummyQuery.changePassword(client);
		r.setReturnError(Error.SUCCESS);

		return r;
	}

	/*
	 * TODO: not sure how this should be implemented?
	 */
	public Reply processDeletePost() {
		Reply r = new Reply();
		
		return r;
	}

	public Reply processCreatePost(Post newPost) {
		Reply r = new Reply();
		newPost.setWriterId(authUser.getId());
		newPost.setWriterName(authUser.getUsername());
		
		if(!DummyQuery.newPost(newPost))
			r.setReturnError(Error.PRIVILEGE);
		else
			r.setReturnError(Error.SUCCESS);
		
		return r;
	}

	/*
	 * TODO: not sure how this should be implemented
	 */
	public Reply processViewBoard(Region region) {
		Reply r = new Reply();
		
		ArrayList<Post> board = DummyQuery.getBoard();
		
		if(board == null)
			r.setReturnError(Error.PRIVILEGE);
		
		region.setPosts(board);
		r.getContents().setBoard(region);
		r.setReturnError(Error.SUCCESS);
		
		return r;
	}

	public Reply processViewProfile(Profile requestedProf) {
		Reply r = new Reply();
		
		requestedProf = DummyQuery.getProfile(authUser.getId(), requestedProf);
		if(requestedProf == null)
			r.setReturnError(Error.NO_USER);
		else {
			r.getContents().setProfile(requestedProf);
			r.setReturnError(Error.SUCCESS);
		}
		
		return r;
	}

	public Reply processEditProfile(Profile newProfile) {
		Reply r = new Reply();
		
		// cannot change own username!
		if(!newProfile.getUsername().equals(authUser.getUsername())) {
			r.setReturnError(Error.MALFORMED_REQUEST);
			return r;
		}
		
		DummyQuery.editProfile(authUser.getId(), newProfile);
		r.setReturnError(Error.SUCCESS);
		
		return r;
	}
	
	public void closeConnection() throws IOException {
		authUser = null;
		if(inStream != null)
			inStream.close();
		if(outStream != null)
			outStream.close();
		if(clientSocket != null)
			clientSocket.close();
		inStream = null;
		outStream = null;
		clientSocket = null;
	}
	
	public void run() {
		if(clientSocket == null)
			return;
		
		System.out.println("Accepted client on machine "
				+ clientSocket.getInetAddress().getHostName());

		try {
			inStream = new ObjectInputStream(clientSocket.getInputStream());
			outStream = new ObjectOutputStream(clientSocket.getOutputStream());
			
			boolean loop = true;

//			Request clientRequest = (Request)inStream.readObject();
//			Reply myReply = parseRequest(clientRequest);
//			outStream.writeObject(myReply);
			
			while(clientSocket.isConnected() && loop) {
				// examine client's request
				Request clientRequest = (Request) inStream.readObject();
				System.out.println("Request type: "
						+ clientRequest.getRequestType().toString());

				Reply myReply = parseRequest(clientRequest);

				// send reply back to client
				outStream.writeObject(myReply);

				// exit loop on logout
				if (clientRequest.getRequestType() == RequestType.LOGOUT)
					loop = false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Class not found!");
			e.printStackTrace();
		} finally {
			System.out.println("Closing connection.");
			try {
				closeConnection();
			} catch (IOException e) {
				System.out.println("Errors closing socket");
				e.printStackTrace();
			}
		}
	}
}
