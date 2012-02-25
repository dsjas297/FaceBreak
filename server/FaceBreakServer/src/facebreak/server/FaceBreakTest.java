package facebreak.server;

import java.io.File;

import facebreak.common.Title;
import facebreak.server.FaceBreakUser;

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
			
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

}
