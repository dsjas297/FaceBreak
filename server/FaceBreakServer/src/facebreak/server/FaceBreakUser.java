package facebreak.server;

import java.util.*;
import java.io.*;
import facebreak.common.*;
import facebreak.common.Post.RegionType;

public class FaceBreakUser {
	
	private User user;
	private Title title;
	private String family;
	private ArrayList<Integer> friends;
	private HashMap<Integer,ArrayList<String>> untrustworthy;
	
	private static final String usersListFile = "users";
	private static final String userInfoFile = "info";
	private static final String userFriendsFile = "friends";
	private static final String userUntrustworthyFile = "untrustworthy";
	private static final String userIDFile = "userID";
	
	public static int addUser(String userName, Title title, String family){
		
		try{
			if(checkIfUserExists(userName)){
				System.err.println("Error: User already exists");
				return 1;
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
			String userInfo = newUserIDstr + "\n" + userName + "\n" + Integer.toString(title.getRank()) + "\n" + family;
			bWriter = new BufferedWriter(new FileWriter(newUserIDstr + "\\" + userInfoFile, false));
			bWriter.write(userInfo);
			bWriter.close();
			
			// Give the user their first friend (himself!)
			bWriter = new BufferedWriter(new FileWriter(newUserIDstr + "\\" + userFriendsFile, false));
			bWriter.write(newUserIDstr);
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
			
			return 0;
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			return 1;
		}
	}
	
	public static boolean checkIfUserExists(String userName){
		try{
			FileReader fReader = new FileReader(usersListFile);
			BufferedReader inputReader = new BufferedReader(fReader);
			String temp;
			while( (temp = inputReader.readLine()) != null){
				String existingName = temp.split(":")[1].trim();
				if(existingName.equals(userName)){
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
	
	public static boolean checkIfUserExists(int userID){
		try{
			String userIDstr = Integer.toString(userID);
			FileReader fReader = new FileReader(usersListFile);
			BufferedReader inputReader = new BufferedReader(fReader);
			String temp;
			while( (temp = inputReader.readLine()) != null){
				String existingName = temp.split(":")[1].trim();
				if(existingName.equals(userIDstr)){
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
			
			this.user = new User(userName, "");
			this.user.setId(userID);
			
			this.family = family;
			
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
			
			inputReader.close();
			
			// Load friends file
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
			fReader = new FileReader(idStr + "\\" + userUntrustworthyFile);
			inputReader = new BufferedReader(fReader);
			String [] linesplit;
			while( (temp = inputReader.readLine()) != null){
				linesplit = temp.split(":");
				Integer untrustworthyID = new Integer( Integer.parseInt(linesplit[0]));
				if(this.untrustworthy.get(untrustworthyID) == null){
					this.untrustworthy.put(untrustworthyID, new ArrayList<String>());
				}
				this.untrustworthy.get(untrustworthyID).add(linesplit[1]);
			}
			inputReader.close();
			
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public int addFriend(int friendID){
		try{
			if(checkIfFriendExists(friendID)){
				System.err.println("Error: Friend already exists");
				return 1;
			}
			
			// Append to friends file
			String newFriend = "\n" + Integer.toString(friendID);
			BufferedWriter bWriter = new BufferedWriter(
					new FileWriter(Integer.toString(this.user.getId()) + "\\" + userFriendsFile, true));
			bWriter.write(newFriend);
			bWriter.close();
			
			return 0;
			
		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
			return 1;
		}
	}
	
	private boolean checkIfFriendExists(int friendID){
		try{
			FileReader fReader = new FileReader(Integer.toString(this.user.getId()) + "\\" + userFriendsFile);
			BufferedReader inputReader = new BufferedReader(fReader);
			String temp;
			while( (temp = inputReader.readLine()) != null){
				String existingID = temp.split(":")[0].trim();
				if(existingID.equals(Integer.toString(friendID))){
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
	public int markUntrustworthy(int foeID){
		try{
			// Append to friends file
			String newFriend = "\n" + Integer.toString(foeID) + ":"
					+ Long.toString((new Date()).getTime());
			BufferedWriter bWriter = new BufferedWriter(
					new FileWriter(Integer.toString(this.user.getId()) + "\\" + userUntrustworthyFile, true));
			bWriter.write(newFriend);
			bWriter.close();
			
			return 0;
			
		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
			return 1;
		}
	}
	
	public int post(int id, int ownerID, int regionID, String msg){
		FaceBreakRegion postingBoard = new FaceBreakRegion(id, ownerID, regionID);
		postingBoard.post(id, msg);
		return 0;
	}
	
	public int view(int id, int ownerID, int regionID){
		FaceBreakRegion postingBoard = new FaceBreakRegion(id, ownerID, regionID);
		Post[] msgs = postingBoard.view();
		for(int i = 0; i < msgs.length; i++){
			System.out.println(msgs[i].getText());
		}
		return 0;
	}
	
	private static int getNewUserID(){
		try{
			FileReader fReader = new FileReader(userIDFile);
			BufferedReader inputReader = new BufferedReader(fReader);
			String temp = inputReader.readLine();
			int id = Integer.parseInt(temp);
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(usersListFile, true));
			bWriter.write(Integer.toString(id + 1));
			bWriter.close();
			return id;
		} catch(Exception e){
			System.err.println("Error: " + e.getMessage());
			return 0;
		}
	}
	
	public User getUser(){
		return user;
	}
}
