/**
 * @author gd226
 * 
 * Thread class to handle each incoming connection on server side.
 * Handles only a single connection (i.e., 1 instance of GUI/client)
 */

package networking;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.ArrayList;

import javax.crypto.SealedObject;

import server.FaceBreakRegion;
import server.FaceBreakUser;

import messages.AsymmetricKEM;
import messages.ItemList;
import messages.Item;
import messages.MsgSealer;
import messages.MsgWrapper;
import messages.Reply;
import messages.Request;
import messages.SymmetricKEM;
import messages.Request.RequestType;

import common.Error;
import common.FBClientUser;
import common.Notification;
import common.Post;
import common.Post.RegionType;
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
	
	private static final short MAX_NUM_RETRIES = 3; 
	private static final long TIMESTAMP_DIFF = 2000;	// 2 seconds for max delay between send/receive?
	
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
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 */
	private boolean verifyIntegrity(Request req, byte[] checksum) throws NoSuchAlgorithmException, IOException {
		byte[] msgHash = req.getHash();
		boolean passChecksum = MsgWrapper.compareChecksum(msgHash, checksum);
		
		long sentTime = req.getTimestamp();
		boolean correct_time = System.currentTimeMillis() - sentTime < TIMESTAMP_DIFF;
		boolean correct_count = req.getCount() == ++count;
		
		return passChecksum && correct_time && correct_count;
	}

	/*
	 * Parse a generic Request from client into login/logout/view/post/etc. requests
	 */
	private Reply parseRequest(MsgWrapper wrapper) {
		Reply myReply = new Reply();
		
		Request req = (Request)wrapper.getMsg();
		byte[] checksum = wrapper.getChecksum();
		
		// sanity checks
		try {
			if(!verifyIntegrity(req, checksum)) {
				myReply.setReturnError(Error.MSG_INTEGRITY);
				System.out.println("replay attack?");
				return myReply;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("Could not check message integrity! No algorithm or IO problem!");
			e.printStackTrace();
			return null;
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
			

			System.out.println("Logged in user: " + username);
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

	// TODO: UPDATE PROCESS CREATE USER
	private Reply processCreateUser(Request req) {
		Reply r = new Reply();

		FBClientUser thisUser = ((Item<FBClientUser>)req.getDetails()).get();
		String username = thisUser.getUsername();
		
		if(thisUser == null || username == null) {
			r.setReturnError(Error.MALFORMED_REQUEST);
			return r;
		}
		
		int uid = FaceBreakUser.checkIfUserExists(username);
		
		// if username already exists
		if(uid > 0) {
			r.setReturnError(Error.DUPLICATE_USER);
			authUser = null;
			keepAlive = false;
			return r;
		}
		
		// stuff default values into profile
		uid = FaceBreakUser.addUser(thisUser.getUsername(), Title.ASSOC, "Default_Family", "First", "Last", thisUser.getPassword());

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
	 * @param req
	 * @return
	 */
	private Reply processGetFriends(Request req) {
		Reply r = new Reply();
		
		ArrayList<String> flist = FaceBreakUser.getFriendsList(authUser.getId());
		flist.remove(0);
		
		ItemList<String> serializableFlist = new ItemList<String>();
		serializableFlist.setArray(flist, String.class);
		
		r.setDetails(serializableFlist);
		r.setReturnError(Error.SUCCESS);
		
		return r;
	}
	
	private Reply processGetNotifications(Request req) {
		Reply r = new Reply();
		
		ArrayList<Notification> notifications = FaceBreakUser.getNotifications(authUser.getId());
//		System.out.println("New notifications: " + notifications.size());
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
		int uid = authUser.getId();
		
		String response = ((Item<String>)req.getDetails()).get();
		boolean approve = response.equals("Approve");
		
		int err = approve ? FaceBreakUser.approveNotification(uid, nid)
				: FaceBreakUser.deleteNotification(uid, nid);

		if(err == 0)
			r.setReturnError(Error.SUCCESS);
		else
			r.setReturnError(Error.UNKNOWN_ERROR);
		return r;
	}
	
	private Reply processAddToRegion(Request req) {
		Reply r = new Reply();
		
		int rid = FaceBreakRegion.addRegion(authUser.getId(), RegionType.COVERT);
		if(rid < 2) {
			r.setReturnError(Error.PRIVILEGE);
			return r;
		}
		
		String[] friendsNames = ((ItemList<String>)req.getDetails()).getArray();
		int[] friendsUids = new int[friendsNames.length];
		for(int i = 0; i < friendsNames.length; i++) {
			int uid = FaceBreakUser.checkIfUserExists(friendsNames[i]);
			friendsUids[i] = uid;
		}
		
		int err = FaceBreakRegion.addToViewable(authUser.getId(), rid, friendsUids);
		
		if(err == 0)
			r.setReturnError(Error.SUCCESS);
		else
			r.setReturnError(Error.UNKNOWN_ERROR);
		
		return r;
	}
	
	private Reply processGetRegions(Request req) {
		Reply r = new Reply();
		String owername = ((Item<String>)req.getDetails()).get();
		int oid = FaceBreakUser.checkIfUserExists(owername);
		
		ArrayList<Integer> rids = FaceBreakRegion.getViewable(oid, authUser.getId());
		ItemList<Integer> serializableRids = new ItemList<Integer>();
		serializableRids.setArray(rids, Integer.class);
		
		r.setDetails(serializableRids);
		r.setReturnError(Error.SUCCESS);
		
		return r;
	}
	
	private void closeConnection() throws IOException {
		if(ois != null) ois.close();
		if(oos != null) oos.close();
		if(clientSocket != null && !clientSocket.isClosed()) clientSocket.close();

		authUser = null;
		ois = null;
		oos = null;
		clientSocket = null;
	}

	private Reply establishSecureConn() {
		try {
			// READ FROM WIRE
			DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
			OutputStream socketOutputStream = clientSocket.getOutputStream();
			
			byte[] kemByteArray = new byte[512];
			for (int i = 0; i < 512; i++) {
				kemByteArray[i] = dis.readByte();
			}
			
			AsymmetricKeys defaultKeys = new AsymmetricKeys();
			PrivateKey privateKey = AsymmetricKeys.readPrivateKeyFromFile();
			defaultKeys.setMyPrivateKey(privateKey);
			
			byte[] decryptedKem = defaultKeys.decrypt(kemByteArray);
			AsymmetricKEM kem = AsymmetricKEM.toKEMObject(decryptedKem);
			byte[] mod = kem.getPublicKeyMod();
			byte[] exp = kem.getPublicKeyExp();
			defaultKeys.genRemotePublicKey(mod, exp);

			// generate shared key
			byte[] sharedKey = RandomGenerator.getByteArray(128);
			SymmetricKEM reply = new SymmetricKEM();
			reply.setSharedKey(sharedKey);
			
			// WRITE TO WIRE
			byte[] encryptedSharedKey = defaultKeys.encrypt(reply.getBytes());
			socketOutputStream.write(encryptedSharedKey);
			
			sealer.init(sharedKey);
			initCount();
			secureSession = true;
			
			Reply r = new Reply();
			r.setReturnError(Error.SUCCESS);
			
			return r;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
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
//	private MsgWrapper unpackRequest(SealedObject sealed) {
//		System.out.println("Unpacking request!");
//		MsgWrapper wrappedMsg = (MsgWrapper)sealer.decrypt(sealed);
//		return wrappedMsg;
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
				Reply myReply = new Reply();
				
				if(!secureSession) {
					myReply = establishSecureConn();
					if(myReply == null)
						keepAlive = false;
				}
				else {
					SealedObject incoming = (SealedObject)ois.readObject();
					MsgWrapper wrappedMsg = (MsgWrapper)sealer.decrypt(incoming);
	
					if(wrappedMsg == null) 
						myReply.setReturnError(Error.MSG_INTEGRITY);
					else 
						myReply = parseRequest(wrappedMsg);
				}
				myReply.setTimestamp();
				myReply.setCount(++count);
				MsgWrapper wrappedReply = new MsgWrapper();
				wrappedReply.setMsg(myReply);
				wrappedReply.setChecksum();
				SealedObject sealedReply = sealer.encrypt(wrappedReply);
				oos.writeObject(sealedReply);
				
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
