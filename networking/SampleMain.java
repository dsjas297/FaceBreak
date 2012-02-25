package facebreak.networking;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import facebreak.common.Post;
import facebreak.common.Profile;
import facebreak.common.Region;
import facebreak.common.Title;
import facebreak.common.Post.RegionType;

public class SampleMain {
	
	public static void main(String args[]) throws IOException, ClassNotFoundException{
		try {
			System.out.println("Creating client socket.");
			FBClient myClient = new FBClient();
			
			String username = "godfather";
			
			myClient.createUser(username, "pwd");
			myClient.changePassword("pwd2");
			
			Profile myProfile = new Profile("godfather", "Vito", "Corleone");
			myProfile.setFamily("Notorious BJG");
			myProfile.setTitle(Title.BOSS);	
			
			Error e = myClient.editProfile(myProfile);
			
			Profile requestedProfile = new Profile("godfather");
			e = myClient.viewProfile(requestedProfile);
//			System.out.println(e.toString());
//			if (e == Error.SUCCESS) {
//				System.out.println("Username: "
//						+ requestedProfile.getUsername());
//				System.out.println("Name: " + requestedProfile.getFname() + " "
//						+ requestedProfile.getLname());
//				System.out.println("Family: " + requestedProfile.getFamily());
//				System.out.println("Title: "
//						+ requestedProfile.getTitle().toString());
//			}
			
			Post post1 = new Post();
			post1.setRegion(RegionType.PUBLIC);
			post1.setText("Yo yo yiggidy yo.");
			e = myClient.post(post1);
			
			Post post2 = new Post();
			post2.setRegion(RegionType.PUBLIC);
			post2.setText("ffffuuuuuuuu");
			e = myClient.post(post2);
			
			Region reg = new Region(username);
			e = myClient.viewBoard(reg);
			System.out.println(e.toString());
			
			Post[] board = reg.getPosts();
			for(int i = 0; i < board.length; i++) {
				System.out.println("Poster: " + board[i].getWriterName());
				System.out.println("Text: " + board[i].getText());
			}
			
			myClient.logout();
		} catch (UnknownHostException e) {
			System.out.println("Could not resolve host name");
			e.printStackTrace();
		}
	}
}
