/**
 * Functionality testing
 */

package networking;

import java.io.IOException;
import java.net.UnknownHostException;

import server.FileSystem;

import common.Error;
import common.Notification;
import common.Post;
import common.Profile;
import common.Region;
import common.Title;
import common.Notification.NotificationType;
import common.Post.RegionType;

public class SampleMain {
	public static final String username1 = "user";
	public static final String pwd1 = "null";

	public static final String username2 = "friend";
	public static final String pwd2 = "longpwd";
	
	public static void testCreateUser(String username, String pwd) throws ClassNotFoundException, IOException {
		System.out.println("TEST: Creating a new user");
		
		FBClient client = new FBClient();

		Error e = client.createUser(username, pwd);
		e.print();
		
		if(e == Error.SUCCESS) {
			e = client.logout();
			e.print();
		}
	}
	
	public static void testCorrectLogin(FBClient client, String username, String pwd) throws ClassNotFoundException, IOException {
		System.out.println("TEST: Logging in a pre-existing user");
		
		Error e = client.login(username, pwd);
		e.print();
		
		if(e == Error.SUCCESS) {
			client.logout();
			e.print();
		}
	}
	
	public static void testFailedPassword() throws ClassNotFoundException, IOException {
		FBClient client = new FBClient();

		System.out.println("Attempt to log in nonexistent user");
		Error e = client.login("randomdude", "pwd");
		e.print();
		
		System.out.println("Testing incorrect password login 3x");
		e = client.login(username1, "garbage");
		e.print();
		
		e = client.login(username1, "moregarbage");
		e.print();
		
		e = client.login(username1, "ffffuuuuu");
		e.print();
		
		e = client.login(username1, "finaltry");
		e.print();
		
		while(true);
	}
	
	public static void testUpdateProfile() throws ClassNotFoundException, IOException {
		
	}
	
	public static void testAddFriend() throws ClassNotFoundException, IOException {
		
	}
	
	public static void testPost() throws ClassNotFoundException, IOException {
		
		
	}
	
	public static void testView() throws ClassNotFoundException, IOException {
		
	}
	
	public static void main(String args[]) throws IOException, ClassNotFoundException {
		System.out.println("Running test suite...");
		
//		FileSystem.cleanup();
		
		testCreateUser(username1, pwd1);
		testCreateUser(username2, pwd2);
		
		FBClient myClient = new FBClient();
		testCorrectLogin(myClient, username1, pwd1);
//		testFailedPassword();
		
		System.out.println();
		System.out.println("Finished test suite");
	}
	
	/*
	public static void main(String args[]) throws IOException, ClassNotFoundException {
		try {
			
			Error e;
			System.out.println("Creating client socket.");
			FBClient myClient = new FBClient();
			
			String username = "hamster";
			e = myClient.login(username, "pwd");
			
			Profile profile = new Profile(username);
			profile.setFname("Tony");
			profile.setLname("Soprano");
			profile.setFamily("godfather");
			profile.setTitle(Title.BOSS);
			e = myClient.editProfile(profile);
			
			Profile myProf = new Profile(username);
			e = myClient.viewProfile(myProf);
			
			assert(e == Error.SUCCESS);
			System.out.println("Name: " + myProf.getFname() + " " + myProf.getLname());
			System.out.println("Title: " + myProf.getTitle().toString());
			
			Post post1 = new Post();
			post1.setRegionId(0);
			post1.setText("Yo yo yiggidy yo.");
			post1.setOwnerName(username);
			e = myClient.post(post1);
			
			Post post2 = new Post();
			post2.setRegion(RegionType.PUBLIC);
			post2.setRegionId(0);
			post2.setText("ffffuuuuuuuu");
			post2.setOwnerName(username);
			e = myClient.post(post2);
			
			Region reg = new Region(username, 0);
			e = myClient.viewRegion(reg);
			System.out.println(e.toString());
			
			Post[] board = reg.getPosts();
			for(int i = 0; i < board.length; i++) {
				System.out.println("Poster: " + board[i].getWriterName());
				System.out.println("Text: " + board[i].getText());
			}
			
			myClient.logout();
			
//			e = myClient.login("me", "somepwd");
//			System.out.println(e.toString());
//			myClient.logout();

//			
//			e = myClient.deleteFriend("godfather");
//			System.out.println(e.toString());
		} catch (UnknownHostException e) {
			System.out.println("Could not resolve host name");
			e.printStackTrace();
		}
	}*/
}
