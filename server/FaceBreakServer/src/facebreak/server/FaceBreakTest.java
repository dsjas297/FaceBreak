package facebreak.server;

import java.io.File;

import facebreak.common.Post;
import facebreak.common.Title;
import facebreak.server.FaceBreakUser;
import facebreak.server.FaceBreakRegion;

public class FaceBreakTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			File file = new File(FaceBreakUser.usersListFile);
			file.createNewFile();
		
			FaceBreakUser.addUser("Jasdeep", Title.BOSS, "Hundal");
			FaceBreakUser.addUser("Boiar", Title.BOSS, "Qin");
			
			FaceBreakUser user = new FaceBreakUser(0);
			user.addFriend(0);
			user.addFriend(1);
			user.markUntrustworthy(5);
			user.markUntrustworthy(1);
			FaceBreakRegion.addRegion(0, Post.RegionType.PUBLIC);
			//FaceBreakRegion.addRegion(0, Post.RegionType.COVERT);
			FaceBreakRegion region = new FaceBreakRegion(0,0,0);
			region.addToViewable(0);
			region.addToViewable(1);
			user.post(0, 0, 0, "Hello! Does this work??");
			user.view(0, 0, 0);
			
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

}
