package facebreak.networking;

import java.io.IOException;
import java.net.UnknownHostException;

import facebreak.common.Post;
import facebreak.common.Profile;
import facebreak.common.Title;
import facebreak.common.Post.RegionType;

public class SampleMain {
	
	public static void main(String args[]) throws IOException, ClassNotFoundException{
		try {
			System.out.println("Creating client socket.");
			FBClient myClient = new FBClient();
			
			myClient.createUser("godfather", "pwd");
			myClient.changePassword("pwd2");
			
			Profile myProfile = new Profile("godfather", "Vito", "Corleone");
			myProfile.setFamily("Notorious BJG");
			myProfile.setTitle(Title.BOSS);	
			
			Error e = myClient.editProfile(myProfile);
			
			Profile requestedProfile = new Profile("godfather");
			e = myClient.viewProfile(requestedProfile);
			System.out.println(e.toString());
			if (e == Error.SUCCESS) {
				System.out.println("Username: "
						+ requestedProfile.getUsername());
				System.out.println("Name: " + requestedProfile.getFname() + " "
						+ requestedProfile.getLname());
				System.out.println("Family: " + requestedProfile.getFamily());
				System.out.println("Title: "
						+ requestedProfile.getTitle().toString());
			}
			
			Post newPost = new Post();
			newPost.setRegion(RegionType.PUBLIC);
			newPost.setText("Yo yo yiggidy yo.");
			e = myClient.post(newPost);
			System.out.println(e.toString());
			
			myClient.logout();
		} catch (UnknownHostException e) {
			System.out.println("Could not resolve host name");
			e.printStackTrace();
		}
	}
}
