/**
 * @author gd226
 * 
 * Client class for GUI; abstracts away socket/network layer.
 * One client corresponds to one GUI instance.
 */

package networking;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;

import javax.crypto.SealedObject;

import messages.AsymmetricKEM;
import messages.ItemList;
import messages.Item;
import messages.MsgSealer;
import messages.MsgWrapper;
import messages.Reply;
import messages.Request;
import messages.Request.RequestType;
import messages.SymmetricKEM;

import common.Error;
import common.FBClientUser;
import common.Notification;
import common.Post;
import common.Profile;
import common.Region;

public class FBClient implements Client {
	private Socket socket;
	private InetAddress serverAddr;
	private FBClientUser user;
	private ObjectOutputStream outStream;
	private ObjectInputStream inStream;
	private long count;
	private MsgSealer sealer;
	private boolean isSecure;

	private static final int PORT_NUM = 4444;
	private static final int TIMESTAMP_DIFF = 2000;
	
	/*
	 * Automatically sets server address to localhost
	 */
	public FBClient() throws UnknownHostException {
		serverAddr = InetAddress.getLocalHost();
		sealer = new MsgSealer();
		user = null;
		socket = null;
		outStream = null;
		inStream = null;
		isSecure = false;
	}

	public Socket getSocket(){
		return socket;
	}

	/**
	 * 
	 * @param signedReply
	 * @return
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 */
	private boolean verifyMsg(Reply rep, byte[] checksum) throws NoSuchAlgorithmException, IOException {
		byte[] msgHash = rep.getHash();
		boolean passChecksum = MsgWrapper.compareChecksum(msgHash, checksum);
		
		boolean correct_count = rep.getCount() == ++count;
		boolean correct_time = System.currentTimeMillis() - rep.getTimestamp() < TIMESTAMP_DIFF;
		
		return passChecksum && correct_count && correct_time;
	}
	
	private Reply sendRequest(Request req) throws ClassNotFoundException {
		Reply nullRep = new Reply();	// if misc errors occur, return a 'null' reply
		nullRep.setReturnError(Error.CONNECTION);
		
		if(inStream == null || outStream == null)
			return nullRep;
		
		try {
			req.setTimestamp();
			req.setCount(++count);
			MsgWrapper wrapper = new MsgWrapper();
			wrapper.setMsg(req);
			wrapper.setChecksum();
			
			SealedObject sealedReq = sealer.encrypt(wrapper);
			outStream.writeObject(sealedReq);

			SealedObject sealedRep = (SealedObject)inStream.readObject();
			MsgWrapper unsealedRep = (MsgWrapper)sealer.decrypt(sealedRep);
			
			if(unsealedRep == null) {
				nullRep.setReturnError(Error.MSG_INTEGRITY);
				return nullRep;
			}
			
			Reply rep = (Reply) unsealedRep.getMsg();
			if(!verifyMsg(rep, unsealedRep.getChecksum())) {
				nullRep.setReturnError(Error.MSG_INTEGRITY);
				return nullRep;
			}
			return rep;
		} catch (Exception e) {
			return nullRep;
		}
	}
	
	/*
	 * Closes socket and object streams; sets all fields to null in preparation for new user
	 */
	private void closeConnection() throws IOException {
//		if(inStream != null) inStream.close();
//		if(outStream != null) outStream.close();
		if(socket != null && !socket.isClosed()) socket.close();
		
		user = null;
		System.out.println("Closed connection on client end.");
	}
	
	private boolean establishSecureConnection() throws Exception {
		AsymmetricKeys defaultKeys = new AsymmetricKeys();
		defaultKeys.genMyKeys();
		PublicKey serverKey = AsymmetricKeys.readPublicKeyFromFile();
		defaultKeys.setRemotePublicKey(serverKey);
		
		// send public key to server
		byte[] mod = defaultKeys.getPublicKeyMod();
		byte[] exp = defaultKeys.getPublicKeyExp();
		
		AsymmetricKEM kem = new AsymmetricKEM();
		kem.setPublicKey(mod, exp);
		byte[] encryptedKem = defaultKeys.encrypt(kem.getBytes());
		
		// WRITE TO WIRE
		OutputStream socketOutputStream = socket.getOutputStream();
		socketOutputStream.write(encryptedKem);
		
		DataInputStream dis = new DataInputStream(socket.getInputStream());
		byte[] sharedKeyBa = new byte[256];
		for (int i = 0; i < 256; i++) {
			sharedKeyBa[i] = dis.readByte();
		}
		
		byte[] decryptedServerKem = defaultKeys.decrypt(sharedKeyBa);
		SymmetricKEM serverKem = SymmetricKEM.toKEMObject(decryptedServerKem);
		sealer.init(serverKem.getSharedKey());
		
		// READ FROM WIRE
		SealedObject sealedInitMsg = (SealedObject)inStream.readObject();
		MsgWrapper wrappedMsg = (MsgWrapper)sealer.decrypt(sealedInitMsg);
		Reply initMsg = (Reply)wrappedMsg.getMsg();
		count = initMsg.getCount();
		isSecure = true;
		
		return true;
	}
	
	/**
	 * Logs in user with (username, pwd) combo. Fails if another user is already logged in.
	 * Creates new socket and initializes input/output streams;
	 * will close connection and close io streams on failure.
	 * @throws IOException 
	 */
	public Error login(String username, String pwd) throws ClassNotFoundException, IOException {
		// cannot login while there's another user logged in
//		if(user != null)
//			return Error.MALFORMED_REQUEST;

		try {
			socket = new Socket(serverAddr, PORT_NUM);
			outStream = new ObjectOutputStream(socket.getOutputStream());
			inStream = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e1) {
			closeConnection();
			return Error.CONNECTION;
		}

		if(!isSecure) {
			try {
				if (!establishSecureConnection()) {
					closeConnection();
					return Error.CONNECTION;
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				closeConnection();
				return Error.CONNECTION;
			}
		}
		
		// create request object
		Request login = new Request(RequestType.LOGIN);
		Item<FBClientUser> userInfo = new Item<FBClientUser>();
		userInfo.set(new FBClientUser(username, pwd));
		login.setDetails(userInfo);

		Reply serverReply = sendRequest(login);
		Error e = serverReply.getReturnError();

		if(e == Error.SUCCESS) 
			user = new FBClientUser(username, pwd);
		
		return e;
	}

	/*
	 * Log out of current session
	 */
	public Error logout() throws ClassNotFoundException {
		// sanity check
		if(socket == null || user == null)
			return Error.LOGIN;

		Request logout = new Request(RequestType.LOGOUT);
		Reply serverReply = sendRequest(logout);
		try {
			closeConnection();
		} catch (IOException e) {
			System.err.println("Problem closing socket/iostream");
			e.printStackTrace();
		}
		return serverReply.getReturnError();
	}

	/*
	 * Cannot create a new user while another user is logged in on this machine.
	 * If error occurs, automatically closes connection.
	 * Otherwise, creates user and automatically logs them in.
	 */
	public Error createUser(String username, String pwd) throws ClassNotFoundException, IOException {
		if(user != null)
			return Error.LOGIN;
		
		try {
			System.out.println("Creating new socket!");
			socket = new Socket(serverAddr, PORT_NUM);
			outStream = new ObjectOutputStream(socket.getOutputStream());
			inStream = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e1) {
			closeConnection();
			return Error.CONNECTION;
		}
		
		if(!isSecure) {
			try {
				if (!establishSecureConnection()) {
					closeConnection();
					return Error.CONNECTION;
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				closeConnection();
				return Error.CONNECTION;
			}
		}
		
		Request createUser = new Request(RequestType.CREATE_USER);
		Item<FBClientUser> userInfo = new Item<FBClientUser>();
		userInfo.set(new FBClientUser(username, pwd));
		createUser.setDetails(userInfo);
		
		Reply serverReply = sendRequest(createUser);
		Error e = serverReply.getReturnError();

		if(e == Error.SUCCESS) 
			user = new FBClientUser(username, pwd);
		
		return e;
	}

	/*
	 * Change password by providing new password string
	 */
	public Error changePassword(String pwd) throws ClassNotFoundException {
		// sanity check
		if (socket == null || user == null)
			return Error.LOGIN;

		Request changePwd = new Request(RequestType.CHANGE_PWD);
		Item<FBClientUser> userInfo = new Item<FBClientUser>();
		userInfo.set(new FBClientUser(user.getUsername(), pwd));
		changePwd.setDetails(userInfo);
		
		Reply serverReply = sendRequest(changePwd);
		Error e = serverReply.getReturnError();

		if(e == Error.SUCCESS) 
			user.setPassword(pwd);
		
		return e;
	}

	/*
	 * View someone's profile. Create new Profile object and set the username of
	 * person you want to view
	 */
	public Error viewProfile(Profile profile) throws ClassNotFoundException {
		// sanity check
		if (socket == null || user == null)
			return Error.LOGIN;

		Request viewProfile = new Request(RequestType.VIEW_PROFILE);
		Item<Profile> profileReq = new Item<Profile>();
		profileReq.set(profile);
		viewProfile.setDetails(profileReq);
		
		Reply serverReply = sendRequest(viewProfile);
		Error e = serverReply.getReturnError();
		
		if(e == Error.SUCCESS) {
			Profile tmp = ((Item<Profile>) serverReply.getDetails()).get();
			profile.setFname(tmp.getFname());
			profile.setLname(tmp.getLname());
			profile.setFamily(tmp.getFamily());
			profile.setTitle(tmp.getTitle());
		}
		return e;
	}

	/*
	 * Edit your own profile by creating new Profile object and setting
	 * whatever fields you want to change
	 */
	public Error editProfile(Profile myProfile) throws ClassNotFoundException {
		// sanity check
		if (socket == null || user == null)
			return Error.LOGIN;

		Request editProfile = new Request(RequestType.EDIT_PROFILE);
		Item<Profile> profileReq = new Item<Profile>();
		Profile newProfile = new Profile(user.getUsername(), 
				myProfile.getFname(), 
				myProfile.getLname(),
				myProfile.getFamily(),
				myProfile.getTitle());
		profileReq.set(newProfile);
		editProfile.setDetails(profileReq);
		
		Reply serverReply = sendRequest(editProfile);
		Error e = serverReply.getReturnError();
		newProfile.setUsername("");
		return e;
	}
	
	/*
	 * Post a new item on someone's region by creating new Post object and setting
	 * the necessary fields
	 */
	public Error post(Post newPost) throws ClassNotFoundException {
		// sanity check
		if (socket == null || user == null)
			return Error.LOGIN;

		Request post = new Request(RequestType.POST);
		newPost.setWriterName(user.getUsername());
		Item<Post> postInfo = new Item<Post>();
		postInfo.set(newPost);
		post.setDetails(postInfo);
		
		Reply serverReply = sendRequest(post);
		Error e = serverReply.getReturnError();
		return e;
	}

	/*
	 * View my or someone else's board/region by creating new Region object and
	 * setting necessary fields
	 */
	public Error viewRegion(Region region) throws ClassNotFoundException {
		// sanity check
		if (socket == null || user == null)
			return Error.LOGIN;

		Request view = new Request(RequestType.VIEW_BOARD);
		Item<Region> regionInfo = new Item<Region>();
		regionInfo.set(region);
		view.setDetails(regionInfo);
		
		Reply serverReply = sendRequest(view);
		Error e = serverReply.getReturnError();
		
		if(e == Error.SUCCESS) {
			ItemList<Post> tmp = (ItemList<Post>)serverReply.getDetails();
			if(tmp.getSize() > 0) {
				region.setPosts(tmp.getArray());
				return e;
			}
		}
		region = null;
		return e;
	}
	
	/*
	 * This user adds a friend of username
	 */
	public Error addFriend(String friendName) throws ClassNotFoundException {
		// sanity check
		if (socket == null || user == null)
			return Error.LOGIN;

		Request addFriend = new Request(RequestType.ADD_FRIEND);
		Item<String> friendInfo = new Item<String>();
		friendInfo.set(friendName);
		addFriend.setDetails(friendInfo);
		
		Reply serverReply = sendRequest(addFriend);
		Error e = serverReply.getReturnError();
		return e;
	}
	
	/*
	 * This user deletes a friend of username
	 */
	public Error deleteFriend(String exFriendName) throws ClassNotFoundException {
		// sanity check
		if(socket == null || user == null)
			return Error.LOGIN;

		Request deleteFriend = new Request(RequestType.DELETE_FRIEND);
		Item<String> exFriendInfo = new Item<String>();
		exFriendInfo.set(exFriendName);
		deleteFriend.setDetails(exFriendInfo);
		
		Reply serverReply = sendRequest(deleteFriend);
		Error e = serverReply.getReturnError();
		return e;
	}
	
	/**
	 * Create empty arraylist friends; getFriendsList will populate arraylist with
	 * usernames of all thisuser's friends
	 * @param friends - empty ArrayList
	 * @return	Error
	 * @throws ClassNotFoundException
	 */
	public Error getFriendsList(ArrayList<String> friends) throws ClassNotFoundException {
		// sanity check
		if(socket == null || user == null)
			return Error.LOGIN;
		
		Request getFriendsList = new Request(RequestType.GET_FRIENDS);
		
		Reply serverReply = sendRequest(getFriendsList);
		Error e = serverReply.getReturnError();
		
		if(e == Error.SUCCESS) {
			ItemList<String> tmp = (ItemList<String>)serverReply.getDetails();
			String[] friendsArray = tmp.getArray();
			for(int i = 0; i < friendsArray.length; i++)
				friends.add(friendsArray[i]);
		}
		
		return e;
	}
	
	/**
	 * 
	 * @param notifications	- empty ArrayList
	 * @return
	 * @throws ClassNotFoundException
	 */
	public Error getNotifications(ArrayList<Notification> notifications) throws ClassNotFoundException {
		// sanity check
		if(socket == null || user == null)
			return Error.LOGIN;
		
		Request getNotifications = new Request(RequestType.GET_NOTIFICATIONS);
		
		Reply serverReply = sendRequest(getNotifications);
		Error e = serverReply.getReturnError();
		
		if(e == Error.SUCCESS) {
			ItemList<Notification> tmp = (ItemList<Notification>)serverReply.getDetails();
			Notification[] notifyArray = tmp.getArray();
			for(int i = 0; i < tmp.getSize(); i++)
				notifications.add(notifyArray[i]);
		}
		return e;
	}

	public Error respondToNotification(int id, boolean approve) throws ClassNotFoundException {
		// sanity check
		if (socket == null || user == null)
			return Error.LOGIN;

		String response = approve ? "Approve" : "Deny";
		
		Request readNotification = new Request(RequestType.RESPOND_NOTIFICATIONS);
		readNotification.setId(id);
		Item<String> approval = new Item<String>();
		approval.set(response);
		readNotification.setDetails(approval);

		Reply serverReply = sendRequest(readNotification);
		Error e = serverReply.getReturnError();

		return e;
	}
	
	public Error addToCovert(int rid, ArrayList<String> usernames) throws ClassNotFoundException {
		// sanity check
		if (socket == null || user == null)
			return Error.LOGIN;

		Request addToCovert = new Request(RequestType.ADD_USER_TO_REGION);
		addToCovert.setId(rid);
		ItemList<String> newCovertUsers = new ItemList<String>();
		newCovertUsers.setArray(usernames, String.class);
		addToCovert.setDetails(newCovertUsers);
		
		Reply serverReply = sendRequest(addToCovert);
		
		return serverReply.getReturnError();
	}
	
	/**
	 * Gets list of all regions belonging to owername that _this_ user can see
	 * @param owername - username of the owner of the board/regions
	 * @param rids - empty arraylist; will be populated with the region id of
	 * viewable regions
	 * @return
	 * @throws ClassNotFoundException
	 */
	public Error getViewableRegions(String owername, ArrayList<Integer> rids) throws ClassNotFoundException {
		// sanity check
		if(socket == null || user == null)
			return Error.LOGIN;
		
		Request getRegions = new Request(RequestType.GET_REGIONS);
		Item<String> username = new Item<String>();
		username.set(owername);
		getRegions.setDetails(username);
		
		Reply serverReply = sendRequest(getRegions);
		Error e = serverReply.getReturnError();
		
		if(e == Error.SUCCESS) {
			ItemList<Integer> tmp = (ItemList<Integer>)serverReply.getDetails();
			Integer[] notifyArray = tmp.getArray();
			for(int i = 0; i < notifyArray.length; i++)
				rids.add(notifyArray[i]);
		}
		return e;
	}
}
