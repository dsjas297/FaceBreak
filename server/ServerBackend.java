package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
	private static String userInfoFile = FileSystem.user_info_file;
	private static String friendsFile = FileSystem.user_friends_file;
	private static String untrustworthyUsers = FileSystem.user_untrustworthy_file;

	private static String regionsDir = FileSystem.region_dir;
	private static String regionPosts = FileSystem.region_posts_file;
	private static String regionInfo = FileSystem.region_info_file;

	public static boolean createPost(Post myPost) {
		int oid = myPost.getOwnerId();
		// int wid = myPost.getWriterId();
		int rid = myPost.getRegionId();

		try {
			String path = Integer.toString(oid) + "\\"
					+ regionsDir + "\\" + Integer.toString(rid) + "\\"
					+ regionPosts;
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
				"\\" + regionsDir + 
				"\\" + Integer.toString(rid) + 
				"\\" + regionPosts;
		FileReader fReader = new FileReader(path);
		BufferedReader br = new BufferedReader(fReader);
		
		ArrayList<Post> allPosts = new ArrayList<Post>();

		try {
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
			
			String path = Integer.toString(requestUid) + "\\" + friendsFile;
			FileReader fReader = new FileReader(path);
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
				BufferedWriter bWriter = new BufferedWriter(new FileWriter(path, true));
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
			
			String path = Integer.toString(requestUid) + "\\" + friendsFile;
			FileReader fReader = new FileReader(path);
			BufferedReader inputReader = new BufferedReader(fReader);
			
			String tmp;
			ArrayList<String> friendList = new ArrayList<String>();
			while((tmp = inputReader.readLine()) != null) {
				if(!(tmp.equals(Integer.toString(friendUid)))) {
					friendList.add(tmp);
				}
			}
			inputReader.close();
			
			FileWriter fWriter = new FileWriter(path);
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
