import java.util.*;
import java.io.*;


public class FaceBreakUser {
	private String userID;
	private String userName;
	private String rank;
	private String family;
	private ArrayList<String> friends;
	private HashMap<String,ArrayList<String>> untrustworthy;

	private static final String usersListFile = "users";
	private static final String userInfoFile = "info";
	private static final String userFriendsFile = "friends";
	private static final String userUntrustworthyFile = "untrustworthy";
	
	public static int addUser(String userID, String userName, String rank, String family){
		
		userID = userID.trim();
		userName = userName.trim();
		rank = rank.trim();
		family = family.trim();
		
		try{
			if(checkIfUserExists(userID)){
				System.err.println("Error: User already exists");
				return 1;
			}
			
			// Add userId to users file
			String newEntry = "\n" + userID + ":" + userName;
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(usersListFile, true));
			bWriter.write(newEntry);
			bWriter.close();
			
			// Create directory for user + their regions
			String directory = userID + "\\" + FaceBreakRegion.regionsFolder;
			(new File(directory)).mkdirs();
			
			// Fill in info for user
			String userInfo = userID + "\n" + userName + "\n" + rank + "\n" + family;
			bWriter = new BufferedWriter(new FileWriter(userID + "\\" + userInfoFile, false));
			bWriter.write(userInfo);
			bWriter.close();
			
			// Make sure that the public and private regions exist
			File file = new File(userID + "\\" + FaceBreakRegion.regionsFolder + "\\" + "public");
			file.createNewFile();
			file = new File(userID + "\\" + FaceBreakRegion.regionsFolder + "\\" + "private");
			file.createNewFile();
			
			return 0;
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			return 1;
		}
	}
	
	public static boolean checkIfUserExists(String userID){
		try{
			FileReader fReader = new FileReader(usersListFile);
			BufferedReader inputReader = new BufferedReader(fReader);
			String temp;
			while( (temp = inputReader.readLine()) != null){
				String existingID = temp.split(":")[0].trim();
				if(existingID.equals(userID)){
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
	public FaceBreakUser(String id){
		id = id.trim();
		try{
			if(checkIfUserExists(id)){
				System.err.println("Error: User already exists");
			}
			
			FileReader fReader = new FileReader(userID + "\\" + userInfoFile);
			BufferedReader inputReader = new BufferedReader(fReader);
			this.userID = inputReader.readLine();
			this.userName = inputReader.readLine();
			this.rank = inputReader.readLine();
			this.family = inputReader.readLine();
			
			inputReader.close();
			
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public int addFriend(String friendID){
		try{
			if(checkIfFriendExists(friendID)){
				System.err.println("Error: Friend already exists");
				return 1;
			}
			
			// Append to friends file
			String newFriend = "\n" + friendID;
			BufferedWriter bWriter = new BufferedWriter(
					new FileWriter(this.userID + "\\" + userFriendsFile, true));
			bWriter.write(newFriend);
			bWriter.close();
			
			return 0;
			
		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
			return 1;
		}
	}
	
	private boolean checkIfFriendExists(String id){
		try{
			FileReader fReader = new FileReader(this.userID + "\\" + userFriendsFile);
			BufferedReader inputReader = new BufferedReader(fReader);
			String temp;
			while( (temp = inputReader.readLine()) != null){
				String existingID = temp.split(":")[0].trim();
				if(existingID.equals(id)){
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
	
	public int markUntrustworthy(String foeId){
		return 0;
	}
	
	public int markTrustworthy(String foeId){
		return 0;
	}
	
	public int post(String id, String region, String msg){
		FaceBreakRegion postingBoard = new FaceBreakRegion(id, region);
		postingBoard.post(id, msg);
		return 0;
	}
	
	public int view(String id, String region){
		FaceBreakRegion postingBoard = new FaceBreakRegion(id, region);
		ArrayList<String> msgs = postingBoard.view();
		for(int i = 0; i < msgs.size(); i++){
			System.out.println(msgs.get(i));
		}
		return 0;
	}
}
