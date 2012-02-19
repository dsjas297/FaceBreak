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
			String directory = userID + "\\" + "regions";
			(new File(directory)).mkdirs();
			
			// Fill in info for user
			String userInfo = userID + "\n" + userName + "\n" + rank + "\n" + family;
			bWriter = new BufferedWriter(new FileWriter(userID + "\\" + userInfoFile, false));
			bWriter.write(userInfo);
			bWriter.close();
			
			return 0;
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			return 1;
		}
	}
	
	private static boolean checkIfUserExists(String userID){
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
			
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public int addFriend(String friendID){
		return 0;
	}
	
	public int markUntrustworthy(String foeId){
		return 0;
	}
	
	public int markTrustworthy(String foeId){
		return 0;
	}
	
	public int post(String id, String region){
		return 0;
	}
	
	public int view(String id, String region){
		return 0;
	}
}
