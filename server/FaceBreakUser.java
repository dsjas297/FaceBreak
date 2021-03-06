package server;

import java.util.*;
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
	
	public static int addUser(String userName, Title title, String family, String fname, String lname){
		
		try{
			if(checkIfUserExists(userName) > 0){
				System.err.println("Error: User already exists");
				return -1;
			}
			
			int newUserID = getNewUserID();
			String newUserIDstr = Integer.toString(newUserID);
			
			// Add userId to users file
			String newEntry = "\n" + newUserIDstr + ":" + userName;
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(usersListFile, true));
			bWriter.write(newEntry);
			bWriter.close();
			// Create directory for user + their regions
			String directory = newUserIDstr + "\\" + FaceBreakRegion.regionsFolder;
			(new File(directory)).mkdirs();
			
			// Fill in info for user
			String userInfo = newUserIDstr + "\n" + userName + "\n" + 
					Integer.toString(title.rank) + "\n" + family + "\n" + fname + "\n" +
					lname;
			bWriter = new BufferedWriter(new FileWriter(newUserIDstr + "\\" + userInfoFile, false));
			bWriter.write(userInfo);
			bWriter.close();
			
			// Give the user their first friend (himself!)
			bWriter = new BufferedWriter(new FileWriter(newUserIDstr + "\\" + userFriendsFile, false));
			bWriter.write(newUserIDstr + "\n");
			bWriter.close();
			
			// Initialize untrustworthy file
			bWriter = new BufferedWriter(new FileWriter(newUserIDstr + "\\" + userUntrustworthyFile, false));
			bWriter.write("");
			bWriter.close();
			
			
			// Create directory for user's regions
			File file = new File(newUserIDstr + "\\" + FaceBreakRegion.regionsFolder);
			file.mkdirs();
			// Instantiate the public and private regions
			FaceBreakRegion.addRegion(newUserID, RegionType.PUBLIC);
			FaceBreakRegion.addRegion(newUserID, RegionType.PRIVATE);
			
			for(int j = 0; j < 25; j++){
				FaceBreakRegion.addRegion(newUserID, RegionType.COVERT);
			}
			
			return newUserID;
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			return -1;
		}
	}
	
	public static int checkIfUserExists(String userName){
		try{
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
						return uid;
					}
				}
			}
			
			inputReader.close();
			return -1;
		} catch(Exception e){
			System.err.println("Error: " + e.getMessage());
			return -2;
		}
	}
	
	public static boolean checkIfUserExists(int userID){
		try{
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
						return true;
					}
				}
			}
			
			inputReader.close();
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
			FileReader fReader = new FileReader(Integer.toString(uid) + "\\" + userFriendsFile);
			BufferedReader inputReader = new BufferedReader(fReader);
			String temp;
			while( (temp = inputReader.readLine()) != null){
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
				if(temp.equals(Integer.toString(friendID))){
					inputReader.close();
					return true;
				}
			}
			
			inputReader.close();
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
			// Append to friends file
			String newFoe = "\n" + Integer.toString(foeID) + ":"
					+ timestamp;
			BufferedWriter bWriter = new BufferedWriter(
					new FileWriter(Integer.toString(uid) + "\\" + userUntrustworthyFile, true));
			bWriter.write(newFoe);
			bWriter.close();
			
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
			FileReader fReader = new FileReader(userIDFile);
			BufferedReader inputReader = new BufferedReader(fReader);
			String temp = inputReader.readLine();
			int id = Integer.parseInt(temp);
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(userIDFile, false));
			bWriter.write(Integer.toString(id + 1));
			bWriter.close();
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
		
			FileReader fReader = new FileReader(idStr + "\\" + userInfoFile);
			BufferedReader inputReader = new BufferedReader(fReader);
			int userID = Integer.parseInt(inputReader.readLine());
			String userName = inputReader.readLine();
		
			User user = new User(userName);
			user.setId(userID);
			inputReader.close();
			
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
			
			FileReader fReader = new FileReader(idStr + "\\" + userInfoFile);
			BufferedReader inputReader = new BufferedReader(fReader);
			int userID = Integer.parseInt(inputReader.readLine());
			String userName = inputReader.readLine();
			int rank = Integer.parseInt(inputReader.readLine());
			String family = inputReader.readLine();
			String fname = inputReader.readLine();
			String lname = inputReader.readLine();
			
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
			
			inputReader.close();
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
			
			// Load friends file
			ArrayList<Integer> friends = new ArrayList<Integer>();
			FileReader fReader = new FileReader(idStr + "\\" + userFriendsFile);
			BufferedReader inputReader = new BufferedReader(fReader);
			String temp;
			while( (temp = inputReader.readLine()) != null){
				temp.trim();
				if(!temp.equals("")){
					friends.add(new Integer(Integer.parseInt(temp)));
				}
			}
			inputReader.close();
			
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
			FileReader fReader = new FileReader(idStr + "\\" + userUntrustworthyFile);
			BufferedReader inputReader = new BufferedReader(fReader);
			String [] linesplit;
			String temp;
			while( (temp = inputReader.readLine()) != null){
				linesplit = temp.split(":");
				if(linesplit.length > 1){
					Integer untrustworthyID = new Integer( Integer.parseInt(linesplit[0]));
					if(untrustworthy.get(untrustworthyID) == null){
						untrustworthy.put(untrustworthyID, new ArrayList<String>());
					}
					untrustworthy.get(untrustworthyID).add(linesplit[1]);
				}
			}
			inputReader.close();
			
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
			
			// Fill in info for user
			String userInfo = Integer.toString(uid) + "\n" + getUser(uid).getUsername() + "\n" + 
					Integer.toString(prof.getTitle().rank) + "\n" + prof.getFamily() + "\n" + prof.getFname() + "\n" +
					prof.getLname();
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(Integer.toString(uid) + "\\" + userInfoFile, false));
			bWriter.write(userInfo);
			bWriter.close();
			
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
			FileReader fReader = new FileReader(friendsFileName);
			BufferedReader inputReader = new BufferedReader(fReader);
			
			String tmp;
			boolean exists = false;
			while((tmp = inputReader.readLine()) != null) {
				if(tmp.equals(Integer.toString(friendUid))) {
					exists = true;
					inputReader.close();
				}
			}
			
			if(!exists) {
				BufferedWriter bWriter = new BufferedWriter(new FileWriter(friendsFileName, true));
				bWriter.write(Integer.toString(friendUid) + "\n");
				bWriter.close();
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
			FileReader fReader = new FileReader(friendsFileName);
			BufferedReader inputReader = new BufferedReader(fReader);
			
			String tmp;
			ArrayList<String> friendList = new ArrayList<String>();
			while((tmp = inputReader.readLine()) != null) {
				if(!(tmp.equals(Integer.toString(friendUid)))) {
					friendList.add(tmp);
				}
			}
			inputReader.close();
			
			FileWriter fWriter = new FileWriter(friendsFileName);
			BufferedWriter writer = new BufferedWriter(fWriter);
			for(int i = 0; i < friendList.size(); i++){
				writer.write(friendList.get(i) + "\n");
			}
			writer.close();
			
			return 0;
		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
			return -1;
		}
	}
}
