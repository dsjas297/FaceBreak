package server;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.io.*;

import common.Post;
import common.Post.RegionType;
import common.Profile;
import common.Title;
import common.User;

public class FaceBreakUser {
	
	//private User user;
	//private Profile profile;
	//private Title title;
	//private ArrayList<Integer> friends;
	//private HashMap<Integer,ArrayList<String>> untrustworthy;
	
	public static final String usersListFile = ServerBackend.globalUsers;
	private static final String userIDFile = ServerBackend.globalUidCounter;
	
	private static final String userInfoFile = ServerBackend.userInfoFile;
	private static final String userFriendsFile = ServerBackend.userFriendsFile;
	private static final String userUntrustworthyFile = ServerBackend.userUntrustworthyFile;
	private static final String imageFile = ServerBackend.imageFile;
	
	public static int addUser(String userName, Title title, String family, String fname, String lname, String password){
		
		try{
			if(checkIfUserExists(userName) > 0){
				System.err.println("Error: User already exists");
				return -1;
			}
			
			int newUserID = getNewUserID();
			String newUserIDstr = Integer.toString(newUserID);
			
			if(ServerBackend.lockMap.get(usersListFile) == null){
				ServerBackend.lockMap.put(usersListFile, new ReentrantLock());
			}
			ServerBackend.lockMap.get(usersListFile).lock();
			// Add userId to users file
			String newEntry = "\n" + newUserIDstr + ":" + userName;
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(usersListFile, true));
			bWriter.write(newEntry);
			bWriter.close();
			// Create directory for user + their regions
			String directory = newUserIDstr + "\\" + FaceBreakRegion.regionsFolder;
			(new File(directory)).mkdirs();
			
			ServerBackend.lockMap.get(usersListFile).unlock();

			// Rest of the files do not need locks since this is the first time we are dealing with them
			
			// Fill in info for user
			String userInfo = newUserIDstr + "\n" + userName + "\n" + 
					Integer.toString(title.rank) + "\n" + family + "\n" + fname + "\n" +
					lname;
			ServerBackend.writeSecure(userInfo, newUserIDstr + "\\" + userInfoFile);
			
			// Give the user their first friend (himself!)
			ServerBackend.writeSecure(newUserIDstr, newUserIDstr + "\\" + userFriendsFile);
			
			// Initialize untrustworthy file
			/*
			bWriter = new BufferedWriter(new FileWriter(newUserIDstr + "\\" + userUntrustworthyFile, false));
			bWriter.write("");
			bWriter.close();
			*/
			ServerBackend.writeSecure("", newUserIDstr + "\\" + userUntrustworthyFile);
			
			// Create directory for user's regions
			File file = new File(newUserIDstr + "\\" + FaceBreakRegion.regionsFolder);
			file.mkdirs();
			// Instantiate the public and private regions
			FaceBreakRegion.addRegion(newUserID, RegionType.PUBLIC);
			FaceBreakRegion.addRegion(newUserID, RegionType.PRIVATE);
			
			// Set password file for user
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			byte[] salt = new byte[ServerBackend.SALT_LENGTH];// We set a salt on our own
			random.nextBytes(salt);
			
			byte[] passwd_bytes = password.getBytes();
			
			byte[] passwd_and_salt = new byte[salt.length + passwd_bytes.length];
			
			int i = 0;
			for(i = 0; i < salt.length; i++){
				passwd_and_salt[i] = salt[i];
			}
			for(i = 0; i < passwd_bytes.length; i++){
				passwd_and_salt[i + salt.length] = passwd_bytes[i];
			}
			
			// Get hash of salt and password
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(passwd_and_salt);
			byte[] hashed = md.digest();
			
			// Write salt and hash to file
			byte[] write_to_file = new byte[hashed.length + salt.length];
			for(i = 0; i < salt.length; i++){
				write_to_file[i] = salt[i];
			}
			for(i = 0; i < hashed.length; i++){
				passwd_and_salt[i + salt.length] = hashed[i];
			}
			
			FileOutputStream out = new FileOutputStream(newUserIDstr + "\\password");
			out.write(write_to_file);
			out.close();
			
			for(int j = 0; j < 25; j++){
				FaceBreakRegion.addRegion(newUserID, RegionType.COVERT);
			}
			
			return newUserID;
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			return -1;
		}
	}
	
	public static boolean verifyUser(int uid, String password){
		try{
			String userIDstr = Integer.toString(uid);
			
			File file = new File(userIDstr + "\\password");
			
			byte[] filebytes = new byte[(int)file.length()];
			FileInputStream in = new FileInputStream(userIDstr + "\\password");
			in.read(filebytes);
			in.close();
			
			byte[] salt = new byte[ServerBackend.SALT_LENGTH];
			byte[] hash_on_file = new byte[filebytes.length - salt.length];
			
			int i = 0;
			for(i = 0; i < salt.length; i++){
				salt[i] = filebytes[i];
			}
			for(i = 0; i < hash_on_file.length; i++){
				hash_on_file[i] = filebytes[i + salt.length];
			}
			
			// Get hash of password
			byte[] passwd_bytes = password.getBytes();
			
			byte[] passwd_and_salt = new byte[salt.length + passwd_bytes.length];
			
			for(i = 0; i < salt.length; i++){
				passwd_and_salt[i] = salt[i];
			}
			for(i = 0; i < passwd_bytes.length; i++){
				passwd_and_salt[i + salt.length] = passwd_bytes[i];
			}
			
			// Get hash of salt and password
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(passwd_and_salt);
			byte[] hashed = md.digest();
			
			// Loop through to check hash
			for(i = 0; i < hashed.length; i++){
				if(hashed[i] != hash_on_file[i]){
					return false;
				}
			}
			
			return true;
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static int checkIfUserExists(String userName){
		try{
			if(ServerBackend.lockMap.get(usersListFile) == null){
				ServerBackend.lockMap.put(usersListFile, new ReentrantLock());
			}
			ServerBackend.lockMap.get(usersListFile).lock();
			FileReader fReader = new FileReader(usersListFile);
			BufferedReader inputReader = new BufferedReader(fReader);
			String temp;
			while((temp = inputReader.readLine()) != null){
				String [] linesplit = temp.split(":");
				if(linesplit.length > 1){
					String existingName = linesplit[1].trim();
					int uid = Integer.parseInt(linesplit[0]);
					if(existingName.equals(userName)){
						inputReader.close();
						ServerBackend.lockMap.get(usersListFile).unlock();
						return uid;
					}
				}
			}
			inputReader.close();
			
			ServerBackend.lockMap.get(usersListFile).unlock();
			
			return -1;
		} catch(Exception e){
			System.err.println("Error: " + e.getMessage());
			return -2;
		}
	}
	
	public static boolean checkIfUserExists(int userID){
		try{
			if(ServerBackend.lockMap.get(usersListFile) == null){
				ServerBackend.lockMap.put(usersListFile, new ReentrantLock());
			}
			ServerBackend.lockMap.get(usersListFile).lock();
			
			String userIDstr = Integer.toString(userID);
			FileReader fReader = new FileReader(usersListFile);
			BufferedReader inputReader = new BufferedReader(fReader);
			String temp;
			while( (temp = inputReader.readLine()) != null){
				String [] linesplit = temp.split(":");
				if(linesplit.length > 1){
					String existingID = linesplit[0].trim();
					if(existingID.equals(userIDstr)){
						inputReader.close();
						ServerBackend.lockMap.get(usersListFile).unlock();
						return true;
					}
				}
			}
			inputReader.close();
			
			ServerBackend.lockMap.get(usersListFile).unlock();
			
			return false;
		} catch(Exception e){
			System.err.println("Error: " + e.getMessage());
			return false;
		}
	}
	
	/*
	// Load user from files
	public FaceBreakUser(int id){
		try{
			if(!checkIfUserExists(id)){
				System.err.println("Error: User does exist");
			}
			
			String idStr = Integer.toString(id);
			
			FileReader fReader = new FileReader(idStr + "\\" + userInfoFile);
			BufferedReader inputReader = new BufferedReader(fReader);
			int userID = Integer.parseInt(inputReader.readLine());
			String userName = inputReader.readLine();
			int rank = Integer.parseInt(inputReader.readLine());
			String family = inputReader.readLine();
			String fname = inputReader.readLine();
			String lname = inputReader.readLine();
			
			this.user = new User(userName);
			this.user.setId(userID);
			
			this.profile = new Profile(userName, fname, lname);
			
			this.profile.setFamily(family);
			
			switch(rank){
				case 0:
					this.title = Title.BOSS;
					break;
				case 1:
					this.title = Title.CAPO;
					break;
				case 2:
					this.title = Title.SOLDIER;
					break;
				case 3:
					this.title = Title.ASSOC;
					break;
			}
			
			this.profile.setTitle(this.title);
			
			inputReader.close();
			
			// Load friends file
			this.friends = new ArrayList<Integer>();
			fReader = new FileReader(idStr + "\\" + userFriendsFile);
			inputReader = new BufferedReader(fReader);
			String temp;
			while( (temp = inputReader.readLine()) != null){
				temp.trim();
				if(!temp.equals("")){
					this.friends.add(new Integer(Integer.parseInt(temp)));
				}
			}
			inputReader.close();
			
			// Load hashmap of untrustworthy people
			this.untrustworthy = new HashMap<Integer, ArrayList<String>>();
			fReader = new FileReader(idStr + "\\" + userUntrustworthyFile);
			inputReader = new BufferedReader(fReader);
			String [] linesplit;
			while( (temp = inputReader.readLine()) != null){
				linesplit = temp.split(":");
				if(linesplit.length > 1){
					Integer untrustworthyID = new Integer( Integer.parseInt(linesplit[0]));
					if(this.untrustworthy.get(untrustworthyID) == null){
						this.untrustworthy.put(untrustworthyID, new ArrayList<String>());
					}
					this.untrustworthy.get(untrustworthyID).add(linesplit[1]);
				}
			}
			inputReader.close();
			
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	*/
	
	/*
	public void setProfile(Profile prof){
		try{
			this.profile = prof;
			
			// Fill in info for user
			String userInfo = Integer.toString(this.user.getId()) + "\n" + user.getUsername() + "\n" + 
					Integer.toString(prof.getTitle().rank) + "\n" + prof.getFamily() + "\n" + prof.getFname() + "\n" +
					prof.getLname();
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(Integer.toString(this.user.getId()) + "\\" + userInfoFile, false));
			bWriter.write(userInfo);
			bWriter.close();
		}catch(Exception e){
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public Profile getProfile(){
		return profile;
	}
	*/
	/*
	public int addFriend(int requestUid, String username) {
		int id = checkIfUserExists(username);
		addFriend(requestUid, id);
		
		return 0;
	}
	
	public int addFriend(int requestUid, int friendID){
		try{
			if(checkIfFriendExists(friendID) || !checkIfUserExists(friendID)){
				System.err.println("Error: Friend already exists or is an invalid user");
				return 1;
			}
			
			// Append to friends file
			String newFriend = "\n" + Integer.toString(friendID);
			BufferedWriter bWriter = new BufferedWriter(
					new FileWriter(Integer.toString(requestUid) + "\\" + userFriendsFile, true));
			bWriter.write(newFriend);
			bWriter.close();
			
			// Append to friends list
			//this.friends.add(friendID);
			
			return 0;
			
		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
			return 1;
		}
	}
	*/
	
	public static boolean checkIfFriendExists(int uid, int friendID){
		try{
			if(ServerBackend.lockMap.get(Integer.toString(uid) + "\\" + userFriendsFile) == null){
				ServerBackend.lockMap.put(Integer.toString(uid) + "\\" + userFriendsFile, new ReentrantLock());
			}
			ServerBackend.lockMap.get(Integer.toString(uid) + "\\" + userFriendsFile).lock();
			
			ArrayList<String> listOfFriends = ServerBackend.readSecure(Integer.toString(uid) + "\\" + userFriendsFile);
			
			ServerBackend.lockMap.get(Integer.toString(uid) + "\\" + userFriendsFile).unlock();
			
			for( int i = 0; i < listOfFriends.size(); i ++){
				/*
				String [] linesplit = temp.split(":");
				if(linesplit.length > 1){
					String existingID = linesplit[0].trim();
					if(existingID.equals(Integer.toString(friendID))){
						inputReader.close();
						return true;
					}
				}
				*/
				if(listOfFriends.get(i).equals(Integer.toString(friendID))){
					return true;
				}
			}
			return false;
		} catch(Exception e){
			System.err.println("Error: " + e.getMessage());
			return false;
		}
	}
	
	// This is also used to mark users as trustworthy
	public static int markUntrustworthy(int uid, int foeID){
		try{
			if(!checkIfUserExists(foeID)){
				System.err.println("Error: Attempted to mark nonexistent user untrustworthy");
				return -1;
			}
			String timestamp = Long.toString((new Date()).getTime());
			String filename = Integer.toString(uid) + "\\" + userUntrustworthyFile;
			
			if(ServerBackend.lockMap.get(filename) == null){
				ServerBackend.lockMap.put(filename, new ReentrantLock());
			}
			ServerBackend.lockMap.get(filename).lock();
			
			// Append to friends file
			ArrayList<String> untrustworthyList = ServerBackend.readSecure(filename);
			untrustworthyList.add(Integer.toString(foeID) + ":"+ timestamp);
			
			String fileContents = "";
			
			for(int i = 0; i < untrustworthyList.size() - 1; i++){
				fileContents = fileContents + untrustworthyList.get(i) + "\n";
			}
			fileContents = fileContents + untrustworthyList.get(untrustworthyList.size() - 1);
			
			ServerBackend.writeSecure(fileContents, filename);
			
			ServerBackend.lockMap.get(filename).unlock();
			
			return 0;
			
		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
			return -1;
		}
	}
	
	/*
	public int post(int id, int ownerID, int regionID, String msg){
		FaceBreakRegion postingBoard = new FaceBreakRegion(id, ownerID, regionID);
		postingBoard.post(id, msg);
		return 0;
	}
	
	public int view(int id, int ownerID, int regionID){
		FaceBreakRegion postingBoard = new FaceBreakRegion(id, ownerID, regionID);
		Post[] msgs = postingBoard.viewAll();
		for(int i = 0; i < msgs.length; i++){
			System.out.println(msgs[i].getText());
		}
		return 0;
	}
	*/
	
	private static int getNewUserID(){
		try{
			if(ServerBackend.lockMap.get(userIDFile) == null){
				ServerBackend.lockMap.put(userIDFile, new ReentrantLock());
			}
			ServerBackend.lockMap.get(userIDFile).lock();
			
			FileReader fReader = new FileReader(userIDFile);
			BufferedReader inputReader = new BufferedReader(fReader);
			String temp = inputReader.readLine();
			int id = Integer.parseInt(temp);
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(userIDFile, false));
			bWriter.write(Integer.toString(id + 1));
			bWriter.close();
			
			ServerBackend.lockMap.get(userIDFile).unlock();
			
			return id;
		} catch(Exception e){
			System.err.println("Error: " + e.getMessage());
			return 0;
		}
	}
	
	/*
	public User getUser(){
		return user;
	}
	*/
	
	public static User getUser(int uid){
		try{
			if(!checkIfUserExists(uid)){
				return null;
			}
		
			String idStr = Integer.toString(uid);
			String filename = idStr + "\\" + userInfoFile;
		
			if(ServerBackend.lockMap.get(filename) == null){
				ServerBackend.lockMap.put(filename, new ReentrantLock());
			}
			ServerBackend.lockMap.get(filename).lock();
			
			ArrayList<String> temp = ServerBackend.readSecure(filename);

			int userID = Integer.parseInt(temp.get(0));
			String userName = temp.get(1);
		
			User user = new User(userName);
			user.setId(userID);
			
			ServerBackend.lockMap.get(filename).unlock();
			
			return user;
			
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			return null;
		}
	}
	
	public static Profile getProfile(int uid){
		try{
			if(!checkIfUserExists(uid)){
				return null;
			}
			
			String idStr = Integer.toString(uid);
			String filename = idStr + "\\" + userInfoFile;
		
			if(ServerBackend.lockMap.get(filename) == null){
				ServerBackend.lockMap.put(filename, new ReentrantLock());
			}
			ServerBackend.lockMap.get(filename).lock();
			
			ArrayList<String> temp = ServerBackend.readSecure(filename);
			
			int userID = Integer.parseInt(temp.get(0));
			String userName = temp.get(1);
			int rank = Integer.parseInt(temp.get(2));
			String family = temp.get(3);
			String fname = temp.get(4);
			String lname = temp.get(5);
			
			Profile profile = new Profile(userName, fname, lname);
			
			profile.setFamily(family);
			
			Title title = Title.ASSOC;
			
			switch(rank){
				case 0:
					title = Title.BOSS;
					break;
				case 1:
					title = Title.CAPO;
					break;
				case 2:
					title = Title.SOLDIER;
					break;
				case 3:
					title = Title.ASSOC;
					break;
			}
			profile.setTitle(title);
			
			ServerBackend.lockMap.get(filename).unlock();
			
			return profile;
			
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			return null;
		}
	}
	
	public static ArrayList<Integer> getFriends(int uid){
		try{
			if(!checkIfUserExists(uid)){
				return null;
			}
			
			String idStr = Integer.toString(uid);
			
			if(ServerBackend.lockMap.get(idStr + "\\" + userFriendsFile) == null){
				ServerBackend.lockMap.put(idStr + "\\" + userFriendsFile, new ReentrantLock());
			}
			ServerBackend.lockMap.get(idStr + "\\" + userFriendsFile).lock();
			
			// Load friends file
			ArrayList<Integer> friends = new ArrayList<Integer>();
			ArrayList<String> friendsStr = ServerBackend.readSecure(idStr + "\\" + userFriendsFile);
			
			for(int i = 0; i < friendsStr.size(); i++){
					friends.add(new Integer(Integer.parseInt(friendsStr.get(i))));
			}
			
			ServerBackend.lockMap.get(idStr + "\\" + userFriendsFile).unlock();
			
			return friends;
			
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			return null;
		}
	}
	
	public static HashMap<Integer, ArrayList<String>> getUntrustworthy(int uid){
		try{
			if(!checkIfUserExists(uid)){
				return null;
			}
			
			String idStr = Integer.toString(uid);
			
			// Load hashmap of untrustworthy people
			HashMap<Integer, ArrayList<String>> untrustworthy = new HashMap<Integer, ArrayList<String>>();

			if(ServerBackend.lockMap.get(idStr + "\\" + userUntrustworthyFile) == null){
				ServerBackend.lockMap.put(idStr + "\\" + userUntrustworthyFile, new ReentrantLock());
			}
			ServerBackend.lockMap.get(idStr + "\\" + userUntrustworthyFile).lock();
			
			ArrayList<String> lines = ServerBackend.readSecure(idStr + "\\" + userUntrustworthyFile);
			
			String [] linesplit;
			String temp;
			for(int i = 0; i < lines.size(); i++){
				temp = lines.get(i);
				linesplit = temp.split(":");
				if(linesplit.length > 1){
					Integer untrustworthyID = new Integer( Integer.parseInt(linesplit[0]));
					if(untrustworthy.get(untrustworthyID) == null){
						untrustworthy.put(untrustworthyID, new ArrayList<String>());
					}
					untrustworthy.get(untrustworthyID).add(linesplit[1]);
				}
			}
			
			ServerBackend.lockMap.get(idStr + "\\" + userUntrustworthyFile).unlock();
			
			return untrustworthy;
			
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			return null;
		}
	}
	
	public static int setProfile(int uid, Profile prof){
		try{
			if(!checkIfUserExists(uid)){
				return -1;
			}
			
			if(ServerBackend.lockMap.get(Integer.toString(uid) + "\\" + userInfoFile) == null){
				ServerBackend.lockMap.put(Integer.toString(uid) + "\\" + userInfoFile, new ReentrantLock());
			}
			ServerBackend.lockMap.get(Integer.toString(uid) + "\\" + userInfoFile).lock();
			
			// Fill in info for user
			String userInfo = Integer.toString(uid) + "\n" + getUser(uid).getUsername() + "\n" + 
					Integer.toString(prof.getTitle().rank) + "\n" + prof.getFamily() + "\n" + prof.getFname() + "\n" +
					prof.getLname();
			ServerBackend.writeSecure(userInfo,Integer.toString(uid) + "\\" + userInfoFile);
			
			ServerBackend.lockMap.get(Integer.toString(uid) + "\\" + userInfoFile).unlock();
			
			return 0;
		}catch(Exception e){
			System.err.println("Error: " + e.getMessage());
			return -1;
		}
	}
	
	public static int addFriend(int requestUid, String friendName) {
		try{
			int friendUid = FaceBreakUser.checkIfUserExists(friendName);
			if(friendUid == -1) {
				// do stuff here
			}
			
			String friendsFileName = Integer.toString(requestUid) + "\\" + userFriendsFile;
			
			if(ServerBackend.lockMap.get(friendsFileName) == null){
				ServerBackend.lockMap.put(friendsFileName, new ReentrantLock());
			}
			ServerBackend.lockMap.get(friendsFileName).lock();
			
			ArrayList<String> friends = ServerBackend.readSecure(friendsFileName);
			
			boolean exists = false;
			int i;
			for(i = 0; i < friends.size(); i++) {
				if(friends.get(i).equals(Integer.toString(friendUid))) {
					exists = true;
				}
			}
			
			if(!exists) {
				friends.add(Integer.toString(friendUid));
				String friendContents = "";
				
				for(i = 0; i < friends.size() - 1; i++){
					friendContents = friendContents + friends.get(i) + "\n";
				}
				friendContents = friendContents + friends.get(friends.size() - 1);
				
				ServerBackend.writeSecure(friendContents, friendsFileName);
			}
			
			ServerBackend.lockMap.get(friendsFileName).unlock();
			
			return 0;
			
		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
			return -1;
		}
	}
	
	public static int deleteFriend(int requestUid, String friendName) {
		try{
			int friendUid = FaceBreakUser.checkIfUserExists(friendName);
			if(friendUid == -1) {
				// do stuff here
			}
			
			String friendsFileName = Integer.toString(requestUid) + "\\" + userFriendsFile;

			if(ServerBackend.lockMap.get(friendsFileName) == null){
				ServerBackend.lockMap.put(friendsFileName, new ReentrantLock());
			}
			ServerBackend.lockMap.get(friendsFileName).lock();
			
			ArrayList<String> friends = ServerBackend.readSecure(friendsFileName);
			
			String friendContents = "";
			
			for(int i = 0; i < friends.size() - 1; i++){
				if(Integer.parseInt(friends.get(i)) != friendUid){
					friendContents = friendContents + friends.get(i) + "\n";
				}
			}
			// Need to deal with the last newline
			friendContents = friendContents.substring(0,friendContents.length() - 2);
			
			ServerBackend.writeSecure(friendContents, friendsFileName);
			
			ServerBackend.lockMap.get(friendsFileName).unlock();
			
			return 0;
		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
			return -1;
		}
	}
}
