import java.util.*;
import java.io.*;

public class FaceBreakRegion {
	
	private String userID;
	private String regionName;
	
	public static final String regionsFolder = "regions";
	
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
			String newPost = "\n" + userID + ":" + msg;
			BufferedWriter bWriter = new BufferedWriter(
					new FileWriter(userID + "\\" + regionsFolder + "\\" + regionName, true));
			bWriter.write(newPost);
			bWriter.close();
		}catch(Exception e){
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public ArrayList<String> view(String id){
		try{
			ArrayList<String> posts = new ArrayList<String>();
			return posts;
		}catch(Exception e){
			System.err.println("Error: " + e.getMessage());
			return null;
		}
	}
	
	
}
