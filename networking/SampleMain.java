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

	
	public static void testCreateUser() {
		
	}
	
	public static void testLogin() {
		
	}
	
	public static void testFailedPassword() {
		
	}
	
	public static void testUpdateProfile() {
		
	}
	
	public static void testAddFriend() {
		
	}
	
	public static void testPost() {
		
		
	}
	
	public static void testView() {
		
	}
	
	public static void main(String args[]) {
		Notification notif = new Notification(NotificationType.CHANGE_RANK);
		notif.setId(0);
		notif.setUsername("gdeng");
		notif.setRank(Title.CAPO.rank);
		
		String s = notif.toString();
		System.out.println(s);

		String[] notif_split = s.split("\\s+");
		NotificationType type = NotificationType.valueOf(notif_split[1]);
		System.out.println(type);
		if(type == NotificationType.CHANGE_RANK)
			System.out.println("changing rank");
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
