/**
 * @author gd226
 * 
 * Thread class to handle each incoming connection on server side.
 * Handles only a single connection (i.e., 1 instance of GUI/client)
 */

package networking;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignedObject;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;

import javax.crypto.KeyAgreement;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DHParameterSpec;

import server.FaceBreakRegion;
import server.FaceBreakUser;

import messages.ItemList;
import messages.Item;
import messages.KeyExchangeMsg;
import messages.MsgSealer;
import messages.MsgSigner;
import messages.Reply;
import messages.Request;
import messages.Request.RequestType;

import common.Error;
import common.FBClientUser;
import common.Notification;
import common.Post;
import common.Profile;
import common.Region;
import common.Title;

public class FBClientHandler extends Thread {
	private Socket clientSocket;
	private AuthenticatedUser authUser;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private boolean keepAlive;
	private boolean secureSession;
	private short retries;
	private long count;
	private MsgSealer sealer;
	
	private static final int NUM_BITS = 1024;
	private static final short MAX_NUM_RETRIES = 3; 
	private static final long TIMESTAMP_DIFF = 2000;	// 2 seconds for max delay between send/receive?
	private static final String PROTOCOL = "DH";
	
	protected FBClientHandler(Socket clientSocket) {
		this.clientSocket = clientSocket;
		keepAlive = true;
		secureSession = false;
		oos = null;
		ois = null;
		retries = 0;
		sealer = new MsgSealer();
	}
	
	private void initCount() {
		count = RandomGenerator.getLong();
	}

	/**
	 * Runs checksum to determine if message integrity has been compromised.
	 * Checks the 'count' on the message and timestamp to guard against replay
	 * attacks
	 * @param req - Client Request
	 * @return	true if passes integrity check, false otherwise
	 */
	private boolean verifyIntegrity(Request req) {
		long sentTime = req.getTimestamp();
		boolean correct_time = System.currentTimeMillis() - sentTime < TIMESTAMP_DIFF;
		boolean correct_count = req.getCount() == ++count;
		
		return correct_time && correct_count;
	}

	/*
	 * Parse a generic Request from client into login/logout/view/post/etc. requests
	 */
	private Reply parseRequest(Request req) {
		Reply myReply = new Reply();
		
		// TODO: FIX THIS!
		// sanity checks
//		if(!verifyIntegrity(req)) {
//			myReply.setReturnError(Error.MSG_INTEGRITY);
//			System.out.println("replay attack?");
//			return myReply;
//		}

		RequestType type = req.getRequestType();
		System.out.println("type: " + type);
		// if client handler receives request but user has not been logged in
		if(type != RequestType.LOGIN && type != RequestType.CREATE_USER) {
			if((authUser == null || !authUser.isLoggedIn())) {
				myReply.setReturnError(Error.LOGIN);
				return myReply;
			}
		}
		
		switch (type) {
			case LOGIN:
				myReply = processLogin(req);
				break;
			case LOGOUT:
				myReply = processLogout();
				break;
			case CREATE_USER:
				myReply = processCreateUser(req);
				break;
			case CHANGE_PWD:
				myReply = processChangePwd(req);
				break;
			case VIEW_PROFILE: 
				myReply = processViewProfile(req);
				break;
			case EDIT_PROFILE: 
				myReply = processEditProfile(req);
				break;
			case POST: 
				myReply = processCreatePost(req);
				break;
			case VIEW_BOARD: 
				myReply = processViewBoard(req);
				break;
			case DELETE_POST: 
				myReply = processDeletePost();
				break;
			case ADD_FRIEND: 
				myReply = processAddFriend(req);
				break;
			case DELETE_FRIEND: 
				myReply = processDeleteFriend(req);
				break;
			case GET_FRIENDS:
				myReply = processGetFriends(req);
				break;
			case GET_NOTIFICATIONS:
				myReply = processGetNotifications(req);
				break;
			case RESPOND_NOTIFICATIONS:
				myReply = processRespondNotify(req);
				break;
			case ADD_USER_TO_REGION:
				myReply = processAddToRegion(req);
				break;
			case GET_REGIONS:
				myReply = processGetRegions(req);
				break;
			default:
				myReply.setReturnError(Error.MALFORMED_REQUEST);
				break;
		}
		return myReply;
	}
	
	private Reply processLogin(Request req) {
		Reply r = new Reply();
		
		FBClientUser thisUser = ((Item<FBClientUser>)req.getDetails()).get();
		String username = thisUser.getUsername();
		
		if(thisUser == null || username == null) {
			r.setReturnError(Error.MALFORMED_REQUEST);
			return r;
		}

		// if thisUser has not logged in yet on this client
		if(authUser == null || 
				(!username.equals(authUser.getUsername()) && !authUser.isLoggedIn())) {
			retries = 1;
			authUser = new AuthenticatedUser(username);
		}
		// if thisUser has exceeded the number of retries
		else if(!authUser.isLoggedIn() && ++retries > MAX_NUM_RETRIES) {
			r.setReturnError(Error.PWD_EXCEED_RETRIES);
			return r;
		}
		
		int uid = FaceBreakUser.checkIfUserExists(username);
		// if not valid username/passwd combo, return only error
		if(uid == -1) 
			r.setReturnError(Error.USERNAME_PWD);
		else if(FaceBreakUser.verifyUser(uid, thisUser.getPassword())) {
			authUser.setId(uid);
			authUser.logIn();
			retries = 0;
			r.setReturnError(Error.SUCCESS);
		}
		else
			r.setReturnError(Error.USERNAME_PWD);
		
		return r;
	}

	private Reply processLogout() {
		authUser.logOut();
		keepAlive = false;
		secureSession = false;
		Reply r = new Reply();
		r.setReturnError(Error.SUCCESS);
		return r;
	}

	private Reply processCreateUser(Request req) {
		Reply r = new Reply();

		FBClientUser thisUser = ((Item<FBClientUser>)req.getDetails()).get();
		if(thisUser == null) {
			r.setReturnError(Error.MALFORMED_REQUEST);
			return r;
		}
		
		int uid = FaceBreakUser.checkIfUserExists(thisUser.getUsername());
		
		// if username already exists
		if(uid > 0) {
			r.setReturnError(Error.DUPLICATE_USER);
			authUser = null;
			return r;
		}
		
		// stuff default values into profile
		uid = FaceBreakUser.addUser(thisUser.getUsername(), Title.ASSOC, "Family", "fname", "lname", thisUser.getPassword());

		authUser = new AuthenticatedUser(thisUser.getUsername());
		authUser.setId(uid);
		authUser.logIn();

		System.out.println("Successfully created new user");
		r.setReturnError(Error.SUCCESS);
		return r;
	}

	/**
	 * TODO	implement this!
	 * @param req
	 * @return
	 */
	private Reply processChangePwd(Request req) {
		Reply r = new Reply();
		
		r.setReturnError(Error.SUCCESS);
		return r;
	}

	private Reply processViewProfile(Request req) {
		Reply r = new Reply();

		Profile requestedProf = ((Item<Profile>)req.getDetails()).get();
		
		int uid = FaceBreakUser.checkIfUserExists(requestedProf.getUsername());
		if(uid == -1) {
			r.setReturnError(Error.NO_USER);
			return r;
		}
		
		requestedProf = FaceBreakUser.getProfile(uid);
		
		if(requestedProf == null) {
			r.setReturnError(Error.NO_USER);
			return r;
		}

		Item<Profile> completeProf = new Item<Profile>();
		completeProf.set(requestedProf);
		r.setDetails(completeProf);
		r.setReturnError(Error.SUCCESS);
		
		return r;
	}

	private Reply processEditProfile(Request req) {
		Reply r = new Reply();

		Profile newProf = ((Item<Profile>)req.getDetails()).get();
		
		// cannot change own username!
		if(!newProf.getUsername().equals(authUser.getUsername())) {
			r.setReturnError(Error.MALFORMED_REQUEST);
			return r;
		}
		
		int err = FaceBreakUser.setProfile(authUser.getId(), newProf);
		r.setReturnError(Error.SUCCESS);
		
		return r;
	}

	private Reply processCreatePost(Request req) {
		Reply r = new Reply();
		
		Post newPost = ((Item<Post>)req.getDetails()).get();
		
		// do not allow for spoofing of writer id/username!
		newPost.setWriterId(authUser.getId());
		newPost.setWriterName(authUser.getUsername());
		
		int oid = FaceBreakUser.checkIfUserExists(newPost.getOwnerName());
		System.out.println(newPost.getOwnerName());
		if(oid == -1) {
			r.setReturnError(Error.MALFORMED_REQUEST);
			return r;
		}
		
		newPost.setOwnerId(oid);
		
		if(FaceBreakRegion.createPost(newPost))
			r.setReturnError(Error.SUCCESS);
		else
			r.setReturnError(Error.PRIVILEGE);
		
		return r;
	}

	private Reply processViewBoard(Request req) {
		Reply r = new Reply();

		Region region = ((Item<Region>)req.getDetails()).get();

		try {
			ArrayList<Post> allPosts = FaceBreakRegion.viewPosts(authUser.getId(), region);
			ItemList<Post> allPostsMsg = new ItemList<Post>();
			
			allPostsMsg.setArray(allPosts, Post.class);
			r.setDetails(allPostsMsg);
			
			r.setReturnError(Error.SUCCESS);
		} catch (FileNotFoundException e) {
			System.err.println("File not found.");
			r.setDetails(null);
			r.setReturnError(Error.NO_USER);	// board does not exist?? no user or privilege error?
		}
		return r;
	}
	
	/*
	 * TODO: not sure how this should be implemented?
	 */
	private Reply processDeletePost() {
		Reply r = new Reply();
		
		return r;
	}
	
	private Reply processAddFriend(Request req) {
		Reply r = new Reply();
		
		String friendName = ((Item<String>)req.getDetails()).get();
		
		int e = FaceBreakUser.addFriend(authUser.getId(), friendName);
		if(e == -1)
			r.setReturnError(Error.NO_USER);
		else if(e == 0)
			r.setReturnError(Error.SUCCESS);
		
		return r;
	}
	
	private Reply processDeleteFriend(Request req) {
		Reply r = new Reply();
		
		String exFriendName = ((Item<String>)req.getDetails()).get();
		
		int err = FaceBreakUser.deleteFriend(authUser.getId(), exFriendName);
		if(err == -1)
			r.setReturnError(Error.UNKNOWN_ERROR);
		else if(err == 0)
			r.setReturnError(Error.SUCCESS);
		
		return r;
	}
	
	/**
	 * TODO implement this!
	 * 
	 * @param req
	 * @return
	 */
	private Reply processGetFriends(Request req) {
		Reply r = new Reply();

		String myName = authUser.getUsername();
		// MAKE CALL TO SERVER HERE
		ArrayList<String> flist = new ArrayList<String>();
		
		ItemList<String> myFriends = new ItemList<String>();
		myFriends.setArray(flist, String.class);
		
		r.setDetails(myFriends);
		r.setReturnError(Error.SUCCESS);
		
		return r;
	}
	
	private Reply processGetNotifications(Request req) {
		Reply r = new Reply();
		
		String myName = authUser.getUsername();
		// MAKE CALL TO SERVER HERE
		
		ArrayList<Notification> notifications = new ArrayList<Notification>();
		
		ItemList<Notification> serializableNot = new ItemList<Notification>();
		serializableNot.setArray(notifications, Notification.class);
		
		r.setDetails(serializableNot);
		r.setReturnError(Error.SUCCESS);
		
		return r;
	}
	
	// TODO: sanity checks for increasing rank??
	private Reply processRespondNotify(Request req) {
		Reply r = new Reply();
		
		int nid = req.getId();
		
		// delete request nid
		
		String response = ((Item<String>)req.getDetails()).get();
		boolean approve = response.equals("Approve");
		
		// approve the message?? something else?
		
		r.setReturnError(Error.SUCCESS);
		return r;
	}
	
	private Reply processAddToRegion(Request req) {
		Reply r = new Reply();
		
		int rid = req.getId();
		String[] users = ((ItemList<String>)req.getDetails()).getArray();
		
		// ADD USERS TO REGION RID FOR THIS USER
		
		r.setReturnError(Error.SUCCESS);
		return r;
	}
	
	private Reply processGetRegions(Request req) {
		Reply r = new Reply();
		String owername = ((Item<String>)r.getDetails()).get();
		int oid = FaceBreakUser.checkIfUserExists(owername);
		
		// GET LIST OF VIEWABLE REGIONS
		ArrayList<Integer> rids = new ArrayList<Integer>();
		ItemList<Integer> serializableRids = new ItemList<Integer>();
		
		r.setDetails(serializableRids);
		r.setReturnError(Error.SUCCESS);
		
		return r;
	}
	
	private void closeConnection() throws IOException {
		if(ois != null) ois.close();
		if(oos != null)oos.close();
		if(clientSocket != null && !clientSocket.isClosed()) clientSocket.close();

		authUser = null;
		ois = null;
		oos = null;
		clientSocket = null;
		
		sealer.destroy();
	}

	/**
	 * TODO: CHANGE FROM DH TO PUBLIC KEY
	 * @param req
	 * @throws IOException 
	 */
//	private void establishSecureConn(Request req) throws Exception {
//		Reply r = new Reply();
//		
//		System.out.println("Establishing secure connection");
//		
//		if(req.getRequestType() != RequestType.EST_SECURE) {
//			r.setReturnError(Error.LOGIN);
//			oos.writeObject(r);
//			return;
//		}
//	    
//	    KeyPairGenerator kpg = KeyPairGenerator.getInstance(PROTOCOL);
//		kpg.initialize(NUM_BITS);
//		KeyPair kp = kpg.generateKeyPair();
//		
//		Class dhClass = Class.forName("javax.crypto.spec.DHParameterSpec");
//		DHParameterSpec dhSpec = ((DHPublicKey) kp.getPublic()).getParams();
//		
//		BigInteger g = dhSpec.getG();
//		BigInteger p = dhSpec.getP();
//		int l = dhSpec.getL();
//		byte[] publicKey = kp.getPublic().getEncoded();
//		KeyAgreement ka = KeyAgreement.getInstance(PROTOCOL);
//		ka.init(kp.getPrivate());
//		
//		KeyExchangeMsg dhMsg = new KeyExchangeMsg();
//		dhMsg.setG(g);
//		dhMsg.setP(p);
//		dhMsg.setL(l);
//		dhMsg.setPublicKey(publicKey);
//		r.setDetails(dhMsg);
//		oos.writeObject(r);
//		
//		Request clientReq = (Request)ois.readObject();
//		KeyExchangeMsg dhClient = (KeyExchangeMsg)clientReq.getDetails();
//		byte[] remotePublic = dhClient.getPublicKey();
//		
//		KeyFactory kf = KeyFactory.getInstance(PROTOCOL);
//		X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(remotePublic);
//		PublicKey pk = kf.generatePublic(x509Spec);
//		ka.doPhase(pk, true);
//		byte sharedSecret[] = ka.generateSecret();
//		sealer.init(sharedSecret);
//
//		r = new Reply();
//		initCount();		
//		r.setCount(count);
//		PublicKey myPublicSigner = signer.init();
//		KeyExchangeMsg signingInfo = new KeyExchangeMsg();
//		signingInfo.setSigningKey(myPublicSigner);
//		r.setDetails(signingInfo);
//		SealedObject sealedRep = sealer.encrypt(r);
//		oos.writeObject(sealedRep);
//
//		SealedObject sealedReq = (SealedObject) ois.readObject();
//		Request clientReq2 = (Request)sealer.decrypt(sealedReq);
//		KeyExchangeMsg remoteSigningInfo = (KeyExchangeMsg)clientReq2.getDetails();
//		signer.setRemotePublicKey(remoteSigningInfo.getSigningKey());
//		
//		secureSession = true;
//	}
	
	// TODO: CHANGE
//	private Request unpackRequest(SignedObject signedReq) {
//		System.out.println("Unpacking request!");
//		SealedObject sealedReq = signer.extract(signedReq);
//		if(sealedReq == null)
//			return null;
//		return (Request) sealer.decrypt(sealedReq);
//	}
	
//	private SignedObject packReply(Reply rep) {
//		SealedObject sealedRep = sealer.encrypt(rep);
//		return signer.sign(sealedRep);
//	}
	
	/**
	 * Receives client (delegated by server) and listens for requests;
	 * sends requests to be parsed; sends replies back to client.
	 * Keeps this connection alive until client is logged out or some security breach occurs
	 */
	public void run() {
		if(clientSocket == null)
			return;
		
		System.out.println("Accepted client on machine "
				+ clientSocket.getInetAddress().getHostName());
		try {
			ois = new ObjectInputStream(clientSocket.getInputStream());
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			
			while(clientSocket.isConnected() && keepAlive) {
				// examine client's request

				// TODO: THIS IS DEBUG STUFF; CHANGE TO REAL CODE
				Request clientReq = (Request)ois.readObject();

				Reply myReply = new Reply();
				if(clientReq == null) 
					myReply.setReturnError(Error.MSG_INTEGRITY);
				else 
					myReply = parseRequest(clientReq);
				myReply.setTimestamp();
				myReply.setCount(++count);
				oos.writeObject(myReply);
				
				/*
				if(!secureSession) {
					Request clientReq = (Request)ois.readObject();
					try {
						establishSecureConn(clientReq);
					} catch (Exception e) {
						System.err.println("COULD NOT ESTABLISH SECURE CONNECTION");
						e.printStackTrace();
					}
				}
				else {
					SignedObject signedReq = (SignedObject)ois.readObject();
					Request clientReq = unpackRequest(signedReq);
					
					Reply myReply = new Reply();
					if(clientReq == null) 
						myReply.setReturnError(Error.MSG_INTEGRITY);
					// parse client's request and send my reply
					else 
						myReply = parseRequest(clientReq);
					myReply.setTimestamp();
					myReply.setCount(++count);
					oos.writeObject(packReply(myReply));
				} */
			}
		} catch(IOException e) {
			System.err.println("Could not send message to client on output stream");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			Reply myReply = new Reply();
			myReply.setReturnError(Error.UNKNOWN_ERROR);
			myReply.setTimestamp();
			try {
				oos.writeObject(myReply);
			} catch (IOException e1) {
				System.err.println("Could not send message to client on output stream");
				e1.printStackTrace();
			}
		} finally {
			System.out.println("Closing connection.");
			try {
				closeConnection();
			} catch(IOException e) {
				System.err.println("Errors closing socket");
				e.printStackTrace();
			}
		}
	}
}
