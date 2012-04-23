package server;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.io.*;

import common.Notification;
import common.Post;
import common.Notification.NotificationType;
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
	
	public static final String usersListFile = FileSystem.globalUsers;
	private static final String userIDFile = FileSystem.globalUidCounter;
	
	private static final String userInfoFile = FileSystem.userInfoFile;
	private static final String userFriendsFile = FileSystem.userFriendsFile;
	private static final String userUntrustworthyFile = FileSystem.userUntrustworthyFile;
	private static final String imageFile = FileSystem.imageFile;
	private static final String notificationsFile = FileSystem.notificationsFile;
	
	private static final String familiesFile = FileSystem.familiesFile;
	
	public static int addUser(String userName, Title title, String family, String fname, String lname, String password){
		
		try{
			if(checkIfUserExists(userName) > 0){
				System.err.println("Error: User already exists");
				return -1;
			}
			
			int newUserID = getNewUserID();
			String newUserIDstr = Integer.toString(newUserID);
			
			if(FileSystem.lockMap.get(usersListFile) == null){
				FileSystem.lockMap.put(usersListFile, new ReentrantLock());
			}
			FileSystem.lockMap.get(usersListFile).lock();
			// Add userId to users file
			String newEntry = "\n" + newUserIDstr + ":" + userName;
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(usersListFile, true));
			bWriter.write(newEntry);
			bWriter.close();
			// Create directory for user + their regions
			String directory = newUserIDstr + "\\" + FaceBreakRegion.regionsFolder;
			(new File(directory)).mkdirs();
			
			FileSystem.lockMap.get(usersListFile).unlock();

			// Rest of the files do not need locks since this is the first time we are dealing with them
			
			// Fill in info for user
			String userInfo = newUserIDstr + "\n" + userName + "\n" + 
					Integer.toString(title.rank) + "\n" + family + "\n" + fname + "\n" +
					lname;
			FileSystem.writeSecure(userInfo, newUserIDstr + "\\" + userInfoFile);
			
			// Give the user their first friend (himself!)
			FileSystem.writeSecure(newUserIDstr, newUserIDstr + "\\" + userFriendsFile);
			
			// Initialize untrustworthy file
			/*
			bWriter = new BufferedWriter(new FileWriter(newUserIDstr + "\\" + userUntrustworthyFile, false));
			bWriter.write("");
			bWriter.close();
			*/
			FileSystem.writeSecure("", newUserIDstr + "\\" + notificationsFile);
			
			// Create directory for user's regions
			File file = new File(newUserIDstr + "\\" + FaceBreakRegion.regionsFolder);
			file.mkdirs();
			// Instantiate the public and private regions
			FaceBreakRegion.addRegion(newUserID, RegionType.PUBLIC);
			FaceBreakRegion.addRegion(newUserID, RegionType.PRIVATE);
			
			// Set password file for user
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			byte[] salt = new byte[FileSystem.SALT_LENGTH];// We set a salt on our own
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
				write_to_file[i + salt.length] = hashed[i];
			}
			
			FileOutputStream out = new FileOutputStream(newUserIDstr + "\\password");
			out.write(write_to_file);
			out.close();
			
			return newUserID;
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
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
			
			byte[] salt = new byte[FileSystem.SALT_LENGTH];
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
	
	// returns -1 if user does not exist; returns their uid otherwise
	public static int checkIfUserExists(String userName){
		try{
			if(FileSystem.lockMap.get(usersListFile) == null){
				FileSystem.lockMap.put(usersListFile, new ReentrantLock());
			}
			FileSystem.lockMap.get(usersListFile).lock();
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
						FileSystem.lockMap.get(usersListFile).unlock();
						return uid;
					}
				}
			}
			inputReader.close();
			
			FileSystem.lockMap.get(usersListFile).unlock();
			
			return -1;
		} catch(Exception e){
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
			return -2;
		}
	}
	
	public static boolean checkIfUserExists(int userID){
		try{
			if(FileSystem.lockMap.get(usersListFile) == null){
				FileSystem.lockMap.put(usersListFile, new ReentrantLock());
			}
			FileSystem.lockMap.get(usersListFile).lock();
			
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
						FileSystem.lockMap.get(usersListFile).unlock();
						return true;
					}
				}
			}
			inputReader.close();
			
			FileSystem.lockMap.get(usersListFile).unlock();
			
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
			if(FileSystem.lockMap.get(Integer.toString(uid) + "\\" + userFriendsFile) == null){
				FileSystem.lockMap.put(Integer.toString(uid) + "\\" + userFriendsFile, new ReentrantLock());
			}
			FileSystem.lockMap.get(Integer.toString(uid) + "\\" + userFriendsFile).lock();
			
			ArrayList<String> listOfFriends = FileSystem.readSecure(Integer.toString(uid) + "\\" + userFriendsFile);
            if(listOfFriends.get(0).equals("ERROR")){
            	FileSystem.lockMap.get(Integer.toString(uid) + "\\" + userFriendsFile).unlock();
				System.out.println("FILE INTEGRITY COMPROMISED");
				return false;
			}
			
			FileSystem.lockMap.get(Integer.toString(uid) + "\\" + userFriendsFile).unlock();
			
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
			
			if(FileSystem.lockMap.get(filename) == null){
				FileSystem.lockMap.put(filename, new ReentrantLock());
			}
			FileSystem.lockMap.get(filename).lock();
			
			// Append to friends file
			ArrayList<String> untrustworthyList = FileSystem.readSecure(filename);

			if(untrustworthyList.get(0).equals("ERROR")){
				FileSystem.lockMap.get(filename).unlock();
				System.out.println("FILE INTEGRITY COMPROMISED");
				return -1;
			}
			
			untrustworthyList.add(Integer.toString(foeID) + ":"+ timestamp);
			
			String fileContents = "";
			
			for(int i = 0; i < untrustworthyList.size() - 1; i++){
				fileContents = fileContents + untrustworthyList.get(i) + "\n";
			}
			fileContents = fileContents + untrustworthyList.get(untrustworthyList.size() - 1);
			
			FileSystem.writeSecure(fileContents, filename);
			
			FileSystem.lockMap.get(filename).unlock();
			
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
			if(FileSystem.lockMap.get(userIDFile) == null){
				FileSystem.lockMap.put(userIDFile, new ReentrantLock());
			}
			FileSystem.lockMap.get(userIDFile).lock();
			
			FileReader fReader = new FileReader(userIDFile);
			BufferedReader inputReader = new BufferedReader(fReader);
			String temp = inputReader.readLine();
			int id = Integer.parseInt(temp);
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(userIDFile, false));
			bWriter.write(Integer.toString(id + 1));
			bWriter.close();
			
			FileSystem.lockMap.get(userIDFile).unlock();
			
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
		
			if(FileSystem.lockMap.get(filename) == null){
				FileSystem.lockMap.put(filename, new ReentrantLock());
			}
			FileSystem.lockMap.get(filename).lock();
			
			ArrayList<String> temp = FileSystem.readSecure(filename);
            if(temp.get(0).equals("ERROR")){
            	FileSystem.lockMap.get(filename).unlock();
				System.out.println("FILE INTEGRITY COMPROMISED");
				return null;
			}

			int userID = Integer.parseInt(temp.get(0));
			String userName = temp.get(1);
		
			User user = new User(userName);
			user.setId(userID);
			
			FileSystem.lockMap.get(filename).unlock();
			
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
		
			if(FileSystem.lockMap.get(filename) == null){
				FileSystem.lockMap.put(filename, new ReentrantLock());
			}
			FileSystem.lockMap.get(filename).lock();
			
			ArrayList<String> temp = FileSystem.readSecure(filename);
			if(temp.get(0).equals("ERROR")){
				FileSystem.lockMap.get(filename).unlock();
				System.out.println("FILE INTEGRITY COMPROMISED");
				return null;
			}
			
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
			
			FileSystem.lockMap.get(filename).unlock();
			
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
			
			if(FileSystem.lockMap.get(idStr + "\\" + userFriendsFile) == null){
				FileSystem.lockMap.put(idStr + "\\" + userFriendsFile, new ReentrantLock());
			}
			FileSystem.lockMap.get(idStr + "\\" + userFriendsFile).lock();
			
			// Load friends file
			ArrayList<Integer> friends = new ArrayList<Integer>();
			ArrayList<String> friendsStr = FileSystem.readSecure(idStr + "\\" + userFriendsFile);
			
			if(friendsStr.get(0).equals("ERROR")){
				FileSystem.lockMap.get(idStr + "\\" + userFriendsFile).unlock();
				System.out.println("FILE INTEGRITY COMPROMISED");
				return null;
			}
			
			for(int i = 0; i < friendsStr.size(); i++){
					friends.add(new Integer(Integer.parseInt(friendsStr.get(i))));
			}
			
			FileSystem.lockMap.get(idStr + "\\" + userFriendsFile).unlock();
			
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

			if(FileSystem.lockMap.get(idStr + "\\" + userUntrustworthyFile) == null){
				FileSystem.lockMap.put(idStr + "\\" + userUntrustworthyFile, new ReentrantLock());
			}
			FileSystem.lockMap.get(idStr + "\\" + userUntrustworthyFile).lock();
			
			ArrayList<String> lines = FileSystem.readSecure(idStr + "\\" + userUntrustworthyFile);
			
			if(lines.get(0).equals("ERROR")){
				FileSystem.lockMap.get(idStr + "\\" + userUntrustworthyFile).unlock();
				System.out.println("FILE INTEGRITY COMPROMISED");
				return null;
			}
			
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
			
			FileSystem.lockMap.get(idStr + "\\" + userUntrustworthyFile).unlock();
			
			return untrustworthy;
			
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			return null;
		}
	}
	
	public static int setProfile(int uid, Profile prof){
		return setProfile(uid, prof, false);
	}
	
	public static int setProfile(int uid, Profile prof, boolean approved){
		try{
			if(!checkIfUserExists(uid)){
				return -1;
			}
			
			if(FileSystem.lockMap.get(Integer.toString(uid) + "\\" + userInfoFile) == null){
				FileSystem.lockMap.put(Integer.toString(uid) + "\\" + userInfoFile, new ReentrantLock());
			}
			
			// Make request to boss, encode string in request
			
			Profile oldProfile = getProfile(uid);
			
			int bossID = checkIfFamilyExists(prof.getFamily());
			
			if(bossID == -1){
				addFamily(uid, prof.getFamily());
				prof.setTitle(Title.BOSS);
			}	
			
			// do NOT allow Boss to change their title or family
			if(oldProfile.getTitle() == Title.BOSS) {
				prof.setTitle(Title.BOSS);
				prof.setFamily(oldProfile.getFamily());
				approved = true;
			}
			String info = "";
			if(oldProfile.getFamily().equals(prof.getFamily())){
				info = Integer.toString(uid) + "\n" + getUser(uid).getUsername() + "\n" + 
					Integer.toString(oldProfile.getTitle().rank) + "\n" + oldProfile.getFamily() + "\n" + prof.getFname() + "\n" +
					prof.getLname();
			} else {
				info = Integer.toString(uid) + "\n" + getUser(uid).getUsername() + "\n" + 
						Integer.toString(Title.ASSOC.rank) + "\n" + prof.getFamily() + "\n" + prof.getFname() + "\n" +
						prof.getLname();
			}
			FileSystem.writeSecure(info,Integer.toString(uid) + "\\" + userInfoFile);
			
			if(approved || bossID == -1 || prof.getTitle() == Title.ASSOC ||
					(prof.getTitle() == oldProfile.getTitle() &&
					 prof.getFamily().equals(oldProfile.getFamily()))) {
				FileSystem.lockMap.get(Integer.toString(uid) + "\\" + userInfoFile).lock();
				// Fill in info for user
				String userInfo = Integer.toString(uid) + "\n" + getUser(uid).getUsername() + "\n" + 
						Integer.toString(prof.getTitle().rank) + "\n" + prof.getFamily() + "\n" + prof.getFname() + "\n" +
						prof.getLname();
				FileSystem.writeSecure(userInfo,Integer.toString(uid) + "\\" + userInfoFile);
				FileSystem.lockMap.get(Integer.toString(uid) + "\\" + userInfoFile).unlock();
			}
			
			else{ // make request
				return notifyChangeTitle(prof.getUsername(), bossID, prof);
			}
			
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
				return -2;
			}
			
			String friendsFileName = Integer.toString(requestUid) + "\\" + userFriendsFile;
			
			if(FileSystem.lockMap.get(friendsFileName) == null){
				FileSystem.lockMap.put(friendsFileName, new ReentrantLock());
			}
			FileSystem.lockMap.get(friendsFileName).lock();
			
			ArrayList<String> friends = FileSystem.readSecure(friendsFileName);
			
			if(friends.get(0).equals("ERROR")){
				FileSystem.lockMap.get(friendsFileName).unlock();
				System.out.println("FILE INTEGRITY COMPROMISED");
				return -1;
			}
			
			boolean exists = false;
			int i;
			for(i = 0; i < friends.size(); i++) {
				if(friends.get(i).equals(Integer.toString(friendUid))) {
					exists = true;
					break;
				}
			}
			
			if(!exists) {
				friends.add(Integer.toString(friendUid));
				String friendContents = "";
				
				for(i = 0; i < friends.size() - 1; i++){
					friendContents = friendContents + friends.get(i) + "\n";
				}
				friendContents = friendContents + friends.get(friends.size() - 1);
				
				FileSystem.writeSecure(friendContents, friendsFileName);
			}
			
			FileSystem.lockMap.get(friendsFileName).unlock();
			
			if(!checkIfFriendExists(friendUid, requestUid)){
				notifyAddFriend(friendName, getUser(requestUid).getUsername());
			}
			
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

			if(FileSystem.lockMap.get(friendsFileName) == null){
				FileSystem.lockMap.put(friendsFileName, new ReentrantLock());
			}
			FileSystem.lockMap.get(friendsFileName).lock();
			
			ArrayList<String> friends = FileSystem.readSecure(friendsFileName);
			
			if(friends.get(0).equals("ERROR")){
				FileSystem.lockMap.get(friendsFileName).unlock();
				System.out.println("FILE INTEGRITY COMPROMISED");
				return -1;
			}
			
			String friendContents = "";
			
			for(int i = 0; i < friends.size(); i++){
				if(Integer.parseInt(friends.get(i)) != friendUid){
					friendContents = friendContents + friends.get(i) + "\n";
				}
			}
			// Need to deal with the last newline
			friendContents = friendContents.substring(0,friendContents.length() - 1);
			
			FileSystem.writeSecure(friendContents, friendsFileName);
			
			FileSystem.lockMap.get(friendsFileName).unlock();
			
			return 0;
		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
			return -1;
		}
	}
	
	public static int notifyAddFriend(String friendName, String requesterName) {
		try{
			int friendUid = FaceBreakUser.checkIfUserExists(friendName);
			if(friendUid == -1) {
				// do stuff here
				return 0;
			}
			
			int requesterID = FaceBreakUser.checkIfUserExists(requesterName);
			
			String notificationsFileName = Integer.toString(friendUid) + "\\" + notificationsFile;
				
			if(FileSystem.lockMap.get(notificationsFileName) == null){
				FileSystem.lockMap.put(notificationsFileName, new ReentrantLock());
			}
			FileSystem.lockMap.get(notificationsFileName).lock();
			
			ArrayList<String> friendings = FileSystem.readSecure(notificationsFileName);
			
			if(friendings.get(0).equals("ERROR")){
				System.out.println("FILE INTEGRITY COMPROMISED");
				FileSystem.lockMap.get(notificationsFileName).unlock();
				return -1;
			}
			
			// TODO: differing Notification object
//			Notification notif = new Notification(requesterName, requesterID,
//					Notification.NotificationType.FRIEND, "FRIEND REQUEST");
			Notification notif = new Notification(NotificationType.NEW_FRIEND);
			notif.setUsername(requesterName);
			
			friendings.add(notif.toString());
			
			String notificationContents = "";
			
			for(int i = 0; i < friendings.size(); i++){
				notificationContents = notificationContents + friendings.get(i) + "\n";
			}
			// Need to deal with the last newline
			notificationContents = notificationContents.substring(0,notificationContents.length() - 1);
			
			FileSystem.writeSecure(notificationContents, notificationsFileName);
			
			FileSystem.lockMap.get(notificationsFileName).unlock();
			
			return 0;
		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
			return -1;
		}
	}
	
	public static int notifyChangeTitle(String requesterName, int bossID, Profile prof) {
		try{
			int requesterID = FaceBreakUser.checkIfUserExists(requesterName);
			
			String notificationsFileName = Integer.toString(bossID) + "\\" + notificationsFile;
				
			if(FileSystem.lockMap.get(notificationsFileName) == null){
				FileSystem.lockMap.put(notificationsFileName, new ReentrantLock());
			}
			FileSystem.lockMap.get(notificationsFileName).lock();
			
			ArrayList<String> friendings = FileSystem.readSecure(notificationsFileName);
			
			if(friendings.size()>0 && friendings.get(0).equals("ERROR")){
				System.out.println("FILE INTEGRITY COMPROMISED");
				FileSystem.lockMap.get(notificationsFileName).unlock();
				return -1;
			}
			
			// TODO: differing Notification object
//			Notification notif = new Notification(requesterName, requesterID,
//					Notification.NotificationType.TITLE, prof.getTitle().toString() + " "
//													     + prof.getFname() + " "
//													     + prof.getLname() + " "
//													     + prof.getFamily() );
			Notification notif = new Notification(NotificationType.CHANGE_RANK);
			notif.setUsername(requesterName);
			notif.setRank(prof.getTitle().rank);
			
			friendings.add(notif.toString());
			
			String notificationContents = "";
			
			for(int i = 0; i < friendings.size(); i++){
				notificationContents = notificationContents + friendings.get(i) + "\n";
			}
			// Need to deal with the last newline
			notificationContents = notificationContents.substring(0,notificationContents.length() - 1);
			
			FileSystem.writeSecure(notificationContents, notificationsFileName);
			
			FileSystem.lockMap.get(notificationsFileName).unlock();
			
			return 0;
		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
			return -1;
		}
	}
	
	public static ArrayList<Notification> getNotifications(int requestUid) {
		try{
			if(!checkIfUserExists(requestUid)){
				return null;
			}
			
			String notificationsFileName = Integer.toString(requestUid) + "\\" + notificationsFile;
				
			if(FileSystem.lockMap.get(notificationsFileName) == null){
				FileSystem.lockMap.put(notificationsFileName, new ReentrantLock());
			}
			FileSystem.lockMap.get(notificationsFileName).lock();
			
			ArrayList<String> notif_strings = FileSystem.readSecure(notificationsFileName);

			if(notif_strings.get(0).equals("ERROR")){
				FileSystem.lockMap.get(notificationsFileName).unlock();
				System.out.println("FILE INTEGRITY COMPROMISED");
				return null;
			}
			
			ArrayList<Notification> notifications = new ArrayList<Notification>();
			
			for(int i = 0; i < notif_strings.size(); i++){
				String notif = notif_strings.get(i);
				String[] notif_split = notif.split("\\s+");
//				notifications.add(
//						new Notification(Integer.parseInt(notif_split[0]), notif_split[1],
//								         Integer.parseInt(notif_split[2]),
//								         Notification.NotificationType.valueOf(notif_split[3]),
//								         notif_split[4]));
				// String representation of the notifications:
				// nid_type_username_rank(optional)\n
				NotificationType type = NotificationType.valueOf(notif_split[1]);
				Notification tmp = new Notification(type);
				tmp.setId(Integer.parseInt(notif_split[0]));
				tmp.setUsername(notif_split[2]);
				if(type == NotificationType.CHANGE_RANK)
					tmp.setRank(Integer.parseInt(notif_split[3]));
				notifications.add(tmp);
			}
			
			FileSystem.lockMap.get(notificationsFileName).unlock();
			
			return notifications;
		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
			return null;
		}
	}
	
	public static int deleteNotification(int requestUid, int notificationID) {
		try{
			if(!checkIfUserExists(requestUid)){
				return -1;
			}
			
			String notificationsFileName = Integer.toString(requestUid) + "\\" + notificationsFile;
				
			if(FileSystem.lockMap.get(notificationsFileName) == null){
				FileSystem.lockMap.put(notificationsFileName, new ReentrantLock());
			}
			FileSystem.lockMap.get(notificationsFileName).lock();
			
			ArrayList<String> notifications = FileSystem.readSecure(notificationsFileName);
			if(notifications.get(0).equals("ERROR")){
				FileSystem.lockMap.get(notificationsFileName).lock();
				System.out.println("FILE INTEGRITY COMPROMISED");
				return -1;
			}
			
			String notificationContents = "";
			
			for(int i = 0; i < notifications.size(); i++){
				if(!(notifications.get(i).split(" ")[0].equals(Integer.toString(notificationID)))){
					notificationContents = notificationContents + notifications.get(i) + "\n";
				}
			}
			// Need to deal with the last newline
			notificationContents = notificationContents.substring(0,notificationContents.length() - 1);
			
			FileSystem.writeSecure(notificationContents, notificationsFileName);
			
			FileSystem.lockMap.get(notificationsFileName).unlock();
			
			return 0;
		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
			return -1;
		}
	}

	public static int approveNotification(int requestUid, int notificationID) {
		try{
			if(!checkIfUserExists(requestUid)){
				return -1;
			}
			
			String notificationsFileName = Integer.toString(requestUid) + "\\" + notificationsFile;
				
			if(FileSystem.lockMap.get(notificationsFileName) == null){
				FileSystem.lockMap.put(notificationsFileName, new ReentrantLock());
			}
			FileSystem.lockMap.get(notificationsFileName).lock();
			
			ArrayList<String> notifications = FileSystem.readSecure(notificationsFileName);
			
			String[] request = null;
			
			for(int i = 0; i < notifications.size(); i++){
				if(notifications.get(i).split(" ")[0].equals(Integer.toString(notificationID))){
					request = notifications.get(i).split(" ");
				}
			}
			
			if(request == null){
				return -1;
			}
			
			Profile profile = new Profile(request[1], request[5], request[6]);
			profile.setFamily(request[7]);
			profile.setTitle(Title.valueOf(request[4]));
			
			setProfile(checkIfUserExists(profile.getUsername()), profile, true);
			
			FileSystem.lockMap.get(notificationsFileName).unlock();
			
			return deleteNotification(requestUid, notificationID);
		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
			return -1;
		}
	}
	
	public static ArrayList<String> getFriendsList (int requestUid) {
		try{
			if(!checkIfUserExists(requestUid)){
				return null;
			}
			
			String friendsFile = Integer.toString(requestUid) + "\\" + userFriendsFile;
				
			if(FileSystem.lockMap.get(friendsFile) == null){
				FileSystem.lockMap.put(friendsFile, new ReentrantLock());
			}
			FileSystem.lockMap.get(friendsFile).lock();
			
			ArrayList<String> friendIDs = FileSystem.readSecure(friendsFile);
			
			ArrayList<String> friendNames = new ArrayList<String>();
			
			for(int i = 0; i < friendIDs.size(); i++){
				friendNames.add(getUser(Integer.parseInt(friendIDs.get(i))).getUsername());
			}
			
			FileSystem.lockMap.get(friendsFile).unlock();
			
			return friendNames;
		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
			return null;
		}
	}
	
	public static int checkIfFamilyExists(String familyName){
		try{
			if(FileSystem.lockMap.get(familiesFile) == null){
				FileSystem.lockMap.put(familiesFile, new ReentrantLock());
			}
			FileSystem.lockMap.get(familiesFile).lock();
			FileReader fReader = new FileReader(familiesFile);
			BufferedReader inputReader = new BufferedReader(fReader);
			String temp;
			while((temp = inputReader.readLine()) != null){
				String [] linesplit = temp.split(":");
				if(linesplit.length > 1){
					String existingName = linesplit[0].trim();
					int bossID = Integer.parseInt(linesplit[1]);
					if(existingName.equals(familyName)){
						inputReader.close();
						FileSystem.lockMap.get(familiesFile).unlock();
						return bossID;
					}
				}
			}
			inputReader.close();
			
			FileSystem.lockMap.get(familiesFile).unlock();
			
			return -1;
		} catch(Exception e){
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
			return -2;
		}
	}
	
	public static int addFamily(int uid, String familyName){
		try{
			if(FileSystem.lockMap.get(familiesFile) == null){
				FileSystem.lockMap.put(familiesFile, new ReentrantLock());
			}
			FileSystem.lockMap.get(familiesFile).lock();

			ArrayList<String> families = new ArrayList<String>();
			
			FileReader fReader = new FileReader(familiesFile);
			BufferedReader inputReader = new BufferedReader(fReader);
			String temp;
			while((temp = inputReader.readLine()) != null){
				families.add(temp);
			}
			inputReader.close();
			
			
			FileWriter fWriter = new FileWriter(familiesFile);
			BufferedWriter fileWriter = new BufferedWriter(fWriter);
			for(int i = 0; i < families.size() - 1; i++){
				fileWriter.write(temp + "\n");
			}
			fileWriter.write(familyName + ":" + Integer.toString(uid));
			fileWriter.close();
			
			FileSystem.lockMap.get(familiesFile).unlock();
			
			return -1;
		} catch(Exception e){
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
			return -2;
		}
	}
}
