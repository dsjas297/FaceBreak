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

import messages.ContentList;
import messages.ContentSingle;
import messages.KeyExchangeMsg;
import messages.MsgSealer;
import messages.MsgSigner;
import messages.Reply;
import messages.Request;
import messages.Request.RequestType;

import common.Error;
import common.FBClientUser;
import common.Post;
import common.Profile;
import common.Region;
import common.Title;

public class FBClientHandler extends Thread {
	private Socket clientSocket;
	private AuthenticatedUser authUser;
	private ObjectOutputStream outStream;
	private ObjectInputStream inStream;
	private boolean keepAlive;
	private boolean secureSession;
	private short retries;
	private long count;
	private MsgSealer sealer;
	private MsgSigner signer;
	
	private static final int NUM_BITS = 1024;
	private static final short MAX_NUM_RETRIES = 3; 
	private static final long TIMESTAMP_DIFF = 2000;	// 2 seconds for max delay between send/receive?
	private static final String PROTOCOL = "DH";
	
	protected FBClientHandler(Socket clientSocket) {
		this.clientSocket = clientSocket;
		keepAlive = true;
		secureSession = false;
		outStream = null;
		inStream = null;
		retries = 0;
		sealer = new MsgSealer();
		signer = new MsgSigner();
	}
	
	private void initCount() {
		count = RandomGenerator.getLong();
	}

	/**
	 * Runs checksum to determine if message integrity has been compromised.
	 * Checks the 'count' on the message to determine if is correct,
	 * also examine timestamp
	 * 
	 * @param req - Client Request
	 * @return	true if passes integrity check, false otherwise
	 */
	private boolean verifyIntegrity(Request req) {
		// REPLAY ATTACKS
		long sentTime = req.getTimestamp();
		boolean correct_time = System.currentTimeMillis() - sentTime < TIMESTAMP_DIFF;
		boolean correct_count = req.getCount() == ++count;
		
		return correct_time && correct_count;
	}
	
	private Request verify(SignedObject signedReq) {
		
//		SealedObject sealedReq = new SealedObject(arg0, arg1);
		
		return null;
	}
	
	private Reply parseRequest(Request req) {
		Reply myReply = new Reply();
		
		// sanity checks
		if(!verifyIntegrity(req)) {
			myReply.setReturnError(Error.MSG_INTEGRITY);
			System.out.println("replay attack?");
			return myReply;
		}

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
			case LIST_FRIENDS:
				myReply = processListFriends(req);
				break;
			default:
				myReply.setReturnError(Error.MALFORMED_REQUEST);
				break;
		}
		return myReply;
	}
	
	private Reply processLogin(Request req) {
		Reply r = new Reply();
		
		FBClientUser thisUser = ((ContentSingle<FBClientUser>)req.getDetails()).get();
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
		if(uid == -1) {
			r.setReturnError(Error.USERNAME_PWD);
			return r;
		}
		
		authUser.setId(uid);
		authUser.logIn();
		retries = 0;
		
		r.setReturnError(Error.SUCCESS);
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

		FBClientUser thisUser = ((ContentSingle<FBClientUser>)req.getDetails()).get();
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

		Profile requestedProf = ((ContentSingle<Profile>)req.getDetails()).get();
		
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

		ContentSingle<Profile> completeProf = new ContentSingle<Profile>();
		completeProf.set(requestedProf);
		r.setDetails(completeProf);
		r.setReturnError(Error.SUCCESS);
		
		return r;
	}

	private Reply processEditProfile(Request req) {
		Reply r = new Reply();

		Profile newProf = ((ContentSingle<Profile>)req.getDetails()).get();
		
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
		
		Post newPost = ((ContentSingle<Post>)req.getDetails()).get();
		
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

		Region region = ((ContentSingle<Region>)req.getDetails()).get();
		
		try {
			ArrayList<Post> allPosts = FaceBreakRegion.viewPosts(authUser.getId(), region);
			ContentList<Post> allPostsMsg = new ContentList<Post>();
			
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
		
		String friendName = ((ContentSingle<String>)req.getDetails()).get();
		
		int e = FaceBreakUser.addFriend(authUser.getId(), friendName);
		if(e == -1)
			r.setReturnError(Error.NO_USER);
		else if(e == 0)
			r.setReturnError(Error.SUCCESS);
		
		return r;
	}
	
	private Reply processDeleteFriend(Request req) {
		Reply r = new Reply();
		
		String exFriendName = ((ContentSingle<String>)req.getDetails()).get();
		
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
	private Reply processListFriends(Request req) {
		Reply r = new Reply();

		String friendName = ((ContentSingle<String>)req.getDetails()).get();
		
		return r;
	}
	
	private void closeConnection() throws IOException {
		if(inStream != null) inStream.close();
		if(outStream != null)outStream.close();
		if(clientSocket != null && !clientSocket.isClosed()) clientSocket.close();

		authUser = null;
		inStream = null;
		outStream = null;
		clientSocket = null;
		
		sealer.destroy();
	}

	/**
	 * 
	 * @param req
	 * @throws IOException 
	 */
	private void establishSecureConn(Request req) throws Exception {
		Reply r = new Reply();
		
		System.out.println("Establishing secure connection");
		
		if(req.getRequestType() != RequestType.EST_SECURE) {
			r.setReturnError(Error.LOGIN);
			outStream.writeObject(r);
			return;
		}
	    
	    KeyPairGenerator kpg = KeyPairGenerator.getInstance(PROTOCOL);
		kpg.initialize(NUM_BITS);
		KeyPair kp = kpg.generateKeyPair();
		
		Class dhClass = Class.forName("javax.crypto.spec.DHParameterSpec");
		DHParameterSpec dhSpec = ((DHPublicKey) kp.getPublic()).getParams();
		
		BigInteger g = dhSpec.getG();
		BigInteger p = dhSpec.getP();
		int l = dhSpec.getL();
		byte[] publicKey = kp.getPublic().getEncoded();
		KeyAgreement ka = KeyAgreement.getInstance(PROTOCOL);
		ka.init(kp.getPrivate());
		
		KeyExchangeMsg dhMsg = new KeyExchangeMsg();
		dhMsg.setG(g);
		dhMsg.setP(p);
		dhMsg.setL(l);
		dhMsg.setPublicKey(publicKey);
		r.setDetails(dhMsg);
		outStream.writeObject(r);
		
		Request clientReq = (Request)inStream.readObject();
		KeyExchangeMsg dhClient = (KeyExchangeMsg)clientReq.getDetails();
		byte[] remotePublic = dhClient.getPublicKey();
		
		KeyFactory kf = KeyFactory.getInstance(PROTOCOL);
		X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(remotePublic);
		PublicKey pk = kf.generatePublic(x509Spec);
		ka.doPhase(pk, true);
		byte sharedSecret[] = ka.generateSecret();
		sealer.init(sharedSecret);

		r = new Reply();
		initCount();		
		r.setCount(count);
		PublicKey myPublicSigner = signer.init();
		KeyExchangeMsg signingInfo = new KeyExchangeMsg();
		signingInfo.setSigningKey(myPublicSigner);
		r.setDetails(signingInfo);
		SealedObject sealedRep = sealer.encrypt(r);
		outStream.writeObject(sealedRep);

		SealedObject sealedReq = (SealedObject) inStream.readObject();
		Request clientReq2 = (Request)sealer.decrypt(sealedReq);
		KeyExchangeMsg remoteSigningInfo = (KeyExchangeMsg)clientReq2.getDetails();
		signer.setRemotePublicKey(remoteSigningInfo.getSigningKey());
		
		secureSession = true;
	}
	
	private Request unpackRequest(SignedObject signedReq) {
		System.out.println("Unpacking request!");
		SealedObject sealedReq = signer.extract(signedReq);
		if(sealedReq == null)
			return null;
		return (Request) sealer.decrypt(sealedReq);
	}
	
	private SignedObject packReply(Reply rep) {
		SealedObject sealedRep = sealer.encrypt(rep);
		return signer.sign(sealedRep);
	}
	
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
			inStream = new ObjectInputStream(clientSocket.getInputStream());
			outStream = new ObjectOutputStream(clientSocket.getOutputStream());
			
			while(clientSocket.isConnected() && keepAlive) {
				// examine client's request
				
				if(!secureSession) {
					Request clientReq = (Request)inStream.readObject();
					System.out.println(clientReq.getRequestType());
					try {
						establishSecureConn(clientReq);
					} catch (Exception e) {
						System.err.println("COULD NOT ESTABLISH SECURE CONNECTION");
						e.printStackTrace();
					}
				}
				else {
					SignedObject signedReq = (SignedObject)inStream.readObject();
					Request clientReq = unpackRequest(signedReq);
					
					Reply myReply = new Reply();
					if(clientReq == null) 
						myReply.setReturnError(Error.MSG_INTEGRITY);
					// parse client's request and send my reply
					else 
						myReply = parseRequest(clientReq);
					myReply.setTimestamp();
					myReply.setCount(++count);
					outStream.writeObject(packReply(myReply));
				}
			}
		} catch(IOException e) {
			System.err.println("Could not send message to client on output stream");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			Reply myReply = new Reply();
			myReply.setReturnError(Error.UNKNOWN_ERROR);
			myReply.setTimestamp();
			try {
				outStream.writeObject(myReply);
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
