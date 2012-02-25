import java.util.*;
import java.io.*;

public class FaceBreakRegion {
	
	private String userID;
	private String regionName;
	
	public static final String regionsFolder = "regions";
	private static final int NUM_POSTS_TO_READ = 10;
	
	public FaceBreakRegion(String id, String region){
		if (FaceBreakUser.checkIfUserExists(id) && checkIfRegionExists(id, region)){
			userID = id;
			regionName = region;
		}
		else {
			userID = null;
			regionName = null;
		}
	}
	
	private static boolean checkIfRegionExists(String userID, String region){
		try{
			File regionFolder = new File(userID + "\\" + regionsFolder);
			File[] regionList = regionFolder.listFiles();
			for(int i = 0; i < regionList.length; i++){
				if (regionList[i].isFile() &&
						regionList[i].equals(region)) {
			        return true;
			    }
			}
			return false;
		} catch(Exception e){
			System.err.println("Error: " + e.getMessage());
			return false;
		}
	}
		
	public void post(String posterID, String msg){
		try{
			String newPost = "\n" + "TIMESTAMP" + " " + userID + ":" + msg;
			BufferedWriter bWriter = new BufferedWriter(
					new FileWriter(userID + "\\" + regionsFolder + "\\" + regionName, true));
			bWriter.write(newPost);
			bWriter.close();
		}catch(Exception e){
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public ArrayList<String> view(){
		try{
			ArrayList<String> posts = new ArrayList<String>();
			
			// Determine number of lines/messages in the region's file
			FileReader fReader = new FileReader(userID + "\\" + regionsFolder + "\\" + regionName);
			BufferedReader inputReader = new BufferedReader(fReader);
			int lineCount = 0;
			while( inputReader.readLine() != null){
				lineCount++;
			}
			inputReader.close();
			
			// Retrieve messages
			fReader = new FileReader(userID + "\\" + regionsFolder + "\\" + regionName);
			inputReader = new BufferedReader(fReader);
			if (lineCount > NUM_POSTS_TO_READ){
				for(int i = 0; i < lineCount - NUM_POSTS_TO_READ;){
					inputReader.readLine();
					i++;
				}
			}
			String temp;
			while( (temp = inputReader.readLine()) != null){
				posts.add(temp);
			}
			
			inputReader.close();
			
			return posts;
			
		}catch(Exception e){
			System.err.println("Error: " + e.getMessage());
			return null;
		}
	}
	
	
}
