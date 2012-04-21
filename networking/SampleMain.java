/**
 * Functionality testing
 */

package networking;

import java.io.IOException;
import java.net.UnknownHostException;

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
	
	public static void testCorrectLogin(String username, String pwd) throws ClassNotFoundException, IOException {
		System.out.println("TEST: Logging in a pre-existing user");
		
		FBClient client = new FBClient();
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
	
	/*
	 * Login user via client; keep client alive to use for further testing
	 */
	public static void loginUser(FBClient client, String username, String pwd) throws ClassNotFoundException, IOException {
		Error e = client.login(username, pwd);
		e.print();
	}
	
	public static void testGetProfile(FBClient client, String username) throws ClassNotFoundException {
		System.out.println("TEST: view user's profile");
		
		Profile prof = new Profile(username);
		Error e = client.viewProfile(prof);
		e.print();
		
		if(e == Error.SUCCESS) {
			System.out.println("Profile for user " + prof.getUsername());
			System.out.println("Name: " + prof.getFname() + " " + prof.getLname());
			System.out.println("Family: " + prof.getFamily());
			System.out.println("Title: " + prof.getTitle());
		}
	}
	
	public static void testUpdateProfile(FBClient client, String username, Title title) throws ClassNotFoundException, IOException {
		Profile prof = new Profile(username);
		prof.setFname("Tony");
		prof.setLname("Soprano");
		prof.setFamily("godfather");
		prof.setTitle(title);
		
		Error e = client.editProfile(prof);
	}
	
	public static void testAddFriend(FBClient client, String friendName) throws ClassNotFoundException, IOException {
		System.out.println("TEST: adding a pre-existing user as friend");
		Error e = client.addFriend(friendName);
		e.print();
	}
	
	public static void testPost() throws ClassNotFoundException, IOException {
		
		
	}
	
	public static void testView() throws ClassNotFoundException, IOException {
		
	}
	
	public static void main(String args[]) throws IOException, ClassNotFoundException {
		System.out.println("Running test suite...");
		
//		FileSystem.cleanup();
		
//		testCreateUser(username1, pwd1);
//		testCreateUser(username2, pwd2);
		
		testCorrectLogin(username1, pwd1);
		
//		testFailedPassword();
		
		FBClient myClient = new FBClient();
		loginUser(myClient, username2, pwd2);
//		testAddFriend(myClient, username1);
		testUpdateProfile(myClient, username2, Title.BOSS);
		testGetProfile(myClient, username2);

		testUpdateProfile(myClient, username2, Title.ASSOC);
		
		myClient.logout();
		
		System.out.println();
		System.out.println("Finished test suite");
	}
	
	/*
	public static void main(String args[]) throws IOException, ClassNotFoundException {
		try {
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
