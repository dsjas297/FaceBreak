package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import common.Post;
import common.Region;

public class ServerBackend {

	// Folder names
	public static final String globalUidCounter = FBFile.GLOBAL_UID.name;
	public static final String globalUsers = FBFile.GLOBAL_ALLUSERS.name;
	public static final String userInfoFile = "info";
	public static final String userFriendsFile = "friends";
	public static final String userUntrustworthyFile = "untrustworthy";
	public static final String imageFile = "avatar.jpg";

	public static final String regionsFolder = "regions";
	public static final String regionPostsFile = "posts";
	public static final String regionInfoFile = "regionInfo";

	public static void initDirTree() {
		File uidFile = new File(globalUidCounter);
		File usersFile = new File(globalUsers);

		if (!usersFile.exists()) {
			try {
				BufferedWriter bWriter = new BufferedWriter(new FileWriter(
						globalUsers, true));
				bWriter.write("");
				bWriter.close();
			} catch (IOException e) {
				System.err
						.println("OH NOES!! Cannot initialize file for users.");
				e.printStackTrace();
			}
		}

		if (!uidFile.exists()) {
			try {
				BufferedWriter bWriter = new BufferedWriter(new FileWriter(
						globalUidCounter, true));
				bWriter.write("1\n");
				bWriter.close();
			} catch (IOException ioe) {
				System.err
						.println("OH NOES!! Cannot initialize file for user ID counter.");
				ioe.printStackTrace();
			}
		}
	}

	public static boolean createPost(Post myPost) {
		int oid = myPost.getOwnerId();
		// int wid = myPost.getWriterId();
		int rid = myPost.getRegionId();

		try {
			String path = Integer.toString(oid) + "\\"
					+ regionsFolder + "\\" + Integer.toString(rid) + "\\"
					+ regionPostsFile;
			FileWriter fWriter = new FileWriter(path, true);

			BufferedWriter bw = new BufferedWriter(fWriter);
			
			String newPost = Long.toString((new Date()).getTime()) + ":"
					+ myPost.getWriterName() + ":" + myPost.getText() + "\n";
			bw.write(newPost);
			bw.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public static ArrayList<Post> viewPosts(int requestUid, Region r)
			throws FileNotFoundException {
		int oid = FaceBreakUser.checkIfUserExists(r.getOwnerName());
		int rid = r.getRegionId();
		
		// Get posts in array
		String path = Integer.toString(oid) + 
				"\\" + regionsFolder + 
				"\\" + Integer.toString(rid) + 
				"\\" + regionPostsFile;
		FileReader fReader = new FileReader(path);
		BufferedReader br = new BufferedReader(fReader);
		
		ArrayList<Post> allPosts = new ArrayList<Post>();

		try {
			if(br.readLine() == null)
				return null;

			String tmp;
			while((tmp = br.readLine()) != null) {
				String[] linesplit = tmp.split(":");
				if (linesplit.length > 1) {
					Post thisPost = new Post();
					String date = new Date(Long.parseLong(linesplit[0])).toString();
					thisPost.setDate(date);
					thisPost.setWriterName(linesplit[1]);
					thisPost.setText(linesplit[2]);
					allPosts.add(thisPost);
				}
			}
			br.close();
			return allPosts;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static int addFriend(int requestUid, String friendName) {
		try{
			int friendUid = FaceBreakUser.checkIfUserExists(friendName);
			if(friendUid == -1) {
				// do stuff here
			}
			
			String friendsFileName = Integer.toString(requestUid) + "\\" + userFriendsFile;
			FileReader fReader = new FileReader(friendsFileName);
			BufferedReader inputReader = new BufferedReader(fReader);
			
			String tmp;
			boolean exists = false;
			while((tmp = inputReader.readLine()) != null) {
				if(tmp.equals(Integer.toString(friendUid))) {
					exists = true;
					inputReader.close();
				}
			}
			
			if(!exists) {
				BufferedWriter bWriter = new BufferedWriter(new FileWriter(friendsFileName, true));
				bWriter.write(Integer.toString(friendUid) + "\n");
				bWriter.close();
			}
			return 0;
			
		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
			return -1;
		}
	}
	
	public static int deleteFriend(int requestUid, String friendName) {
		try{
			int friendUid = FaceBreakUser.checkIfUserExists(friendName);
			if(friendUid == -1) {
				// do stuff here
			}
			
			String friendsFileName = Integer.toString(requestUid) + "\\" + userFriendsFile;
			FileReader fReader = new FileReader(friendsFileName);
			BufferedReader inputReader = new BufferedReader(fReader);
			
			String tmp;
			ArrayList<String> friendList = new ArrayList<String>();
			while((tmp = inputReader.readLine()) != null) {
				if(!(tmp.equals(Integer.toString(friendUid)))) {
					friendList.add(tmp);
				}
			}
			inputReader.close();
			
			FileWriter fWriter = new FileWriter(friendsFileName);
			BufferedWriter writer = new BufferedWriter(fWriter);
			for(int i = 0; i < friendList.size(); i++){
				writer.write(friendList.get(i) + "\n");
			}
			writer.close();
			
			return 0;
		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
			return -1;
		}
	}
}
