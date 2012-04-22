/**
 * NOTE: MUST ENABLE ASSERT CHECKING!
 * 1) Windows > Preferences > Java > Installed JREs
 * 2) Default VM arguments: -ea
 */

package Tests;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import networking.FBClient;

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
	public static final String pwd1 = "LongPwd111";

	public static final String username2 = "friend";
	public static final String pwd2 = "OtherPwd222";
	
	/*
	 * Create a new user
	 */
	public static void testCreateUser(String username, String pwd) throws ClassNotFoundException, IOException {
		System.out.println("TEST: Creating a new user");
		
		FBClient client = new FBClient();

		Error e = client.createUser(username, pwd);
		assert(e == Error.SUCCESS || e == Error.DUPLICATE_USER);
		
		if(e == Error.SUCCESS) {
			e = client.logout();
			assert(e == Error.SUCCESS);
			e.print();
		}
	}
	
	/*
	 * This MUST be the CORRECT username/pwd combo
	 */
	public static void testCorrectLogin(String username, String pwd) throws ClassNotFoundException, IOException {
		System.out.println("TEST: Logging in a pre-existing user");
		
		FBClient client = new FBClient();
		Error e = client.login(username, pwd);
		
		if(e == Error.SUCCESS) {
			client.logout();
			assert(e == Error.SUCCESS);
		}
	}
	
	/*
	 * FAILED PASSWORD ATTEMPTS: 
	 * 1) unknown user, 2) incorrect password, 3) > 3 incorrect password attemps
	 */
	public static void testFailedPassword(String username) throws ClassNotFoundException, IOException {
		FBClient client = new FBClient();

		System.out.println("Attempt to log in nonexistent user");
		Error e = client.login("twilight_sparklezz", "brony");
		assert(e == Error.USERNAME_PWD);
		
		System.out.println("Testing incorrect password login 3x");
		e = client.login(username, "garbage");
		assert(e == Error.USERNAME_PWD);
		e.print();
		
		e = client.login(username, "moregarbage");
		assert(e == Error.USERNAME_PWD);
		e.print();
		
		e = client.login(username, "ffffuuuuu");
		assert(e == Error.USERNAME_PWD);
		e.print();
		
		// 4th try
		e = client.login(username, "finaltry");
		assert(e == Error.PWD_EXCEED_RETRIES);
		e.print();
	}
	
	/*
	 * Create 2 new users; log in correctly; attempt to log in with bad username/pwd combos
	 */
	public static void basicLoginTest() throws ClassNotFoundException, IOException {
		// create 2 new users
		testCreateUser(username1, pwd1);
		testCreateUser(username2, pwd2);
		
		testCorrectLogin(username1, pwd1);
		
		testFailedPassword(username1);
		testFailedPassword(username2);
	}
	
	/*
	 * Login user via client; keep client alive to use for further testing
	 */
	public static void loginUser(FBClient client, String username, String pwd) throws ClassNotFoundException, IOException {
		Error e = client.login(username, pwd);
		e.print();
	}
	
	/*
	 * Print users profile
	 */
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
	
	public static Error testUpdateProfile(FBClient client, String username, Title title) throws ClassNotFoundException, IOException {
		Profile prof = new Profile(username);
		prof.setFname("Tony");
		prof.setLname("Soprano");
		prof.setFamily("godfather");
		prof.setTitle(title);
		
		return client.editProfile(prof);
	}
	
	public static void testAddFriend(FBClient client, String friendName) throws ClassNotFoundException, IOException {
		System.out.println("TEST: adding a pre-existing user " + friendName + " as friend");
		Error e = client.addFriend(friendName);
		e.print();
		
		ArrayList<String> friends = new ArrayList<String>();
		e = client.getFriendsList(friends);
		e.print();
		
		if(e == Error.SUCCESS) {
			System.out.println("My friends list:");
			for(String name : friends)
				System.out.println(name);
		}
	}
	
	public static void testGetNotifications(FBClient client) throws ClassNotFoundException {
		ArrayList<Notification> notifications = new ArrayList<Notification>();
		Error e = client.getNotifications(notifications);
		e.print();
		System.out.println("Number of notifications: " + notifications.size());
		for(Notification n : notifications) {
			System.out.println("Id: " + n.getId());
			System.out.println("Type: " + n.getType().toString());
			System.out.println("Username: " + n.getUsername());
		}
	}
	
	/*
	 * Post to someone's public board; should ALWAYS return success because public
	 */
	public static void postViewTest(FBClient client, String username) throws ClassNotFoundException {
		Post post1 = new Post();
		post1.setRegionId(0);
		post1.setText("Yo yo yiggidy yo.");
		post1.setOwnerName(username);
		Error e = client.post(post1);
		assert(e == Error.SUCCESS);
		
		Post post2 = new Post();
		post2.setRegionId(0);
		post2.setText("ffffuuuuuuuu");
		post2.setOwnerName(username);
		e = client.post(post2);
		assert(e == Error.SUCCESS);
		
		Region reg = new Region(username, 0);
		e = client.viewRegion(reg);
		assert(e == Error.SUCCESS);
		
		Post[] board = reg.getPosts();
		for(int i = 0; i < board.length; i++) {
			System.out.println("Poster: " + board[i].getWriterName());
			System.out.println("Text: " + board[i].getText());
		}
	}
	
	/*
	 * Attempt to change rank to BOSS;
	 * BOSS attempts to lower their own rank;
	 * attempts to join new Family
	 */
	public static void basicProfileUpdate() throws ClassNotFoundException, IOException {
		String user1 = "holmes";
		String user2 = "watson";
		String pwd = "pwd";
//		testCreateUser(user1, pwd);
//		testCreateUser(user2, pwd);
		
		FBClient client = new FBClient();
//		loginUser(client, user1, pwd);
//		
//		Profile prof = new Profile(user1);
//		prof.setFname("Sherlock");
//		prof.setLname("Holmes");
//		prof.setFamily("221Baker");
//		prof.setTitle(Title.BOSS);
//		client.editProfile(prof);
//		client.logout();
//		
//		client = new FBClient();
//		loginUser(client, user2, pwd);
//		prof.setFname("John");
//		prof.setLname("Watson");
//		prof.setTitle(Title.CAPO);
//		client.editProfile(prof);
//		client.logout();
		
		client = new FBClient();
		loginUser(client, user1, pwd);
		testGetNotifications(client);
		client.logout();
	}
	
	public static void approveFriendTest() {
		
	}
	
	public static void approveChangeRankTest() throws ClassNotFoundException, IOException {
		FBClient myClient = new FBClient();
		loginUser(myClient, username2, pwd2);
		testUpdateProfile(myClient, username2, Title.BOSS);
		testGetProfile(myClient, username2);
		testUpdateProfile(myClient, username2, Title.ASSOC);
		testGetProfile(myClient, username2);
		
		myClient.logout();
		
		FBClient myClient2 = new FBClient();
		loginUser(myClient2, username1, pwd1);
		testUpdateProfile(myClient2, username1, Title.CAPO);
		testGetProfile(myClient2, username2);
		
		myClient2.logout();
	}
	
	public static void main(String args[]) throws IOException, ClassNotFoundException {
		System.out.println("Running test suite...");
		
//		FileSystem.cleanup();
		
//		basicLoginTest();
		
		FBClient myClient = new FBClient();
		
//		loginUser(myClient, username2, pwd2);
//		testAddFriend(myClient, username1);
//		postViewTest(myClient, username2);
//		myClient.logout();
		
//		myClient = new FBClient();
//		loginUser(myClient, username1, pwd1);
//		testGetNotifications(myClient);
//		Error e = myClient.logout();
//		e.print();
		
		basicProfileUpdate();
		
		System.out.println();
		System.out.println("Finished test suite");
	}
}
