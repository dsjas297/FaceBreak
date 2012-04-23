package server;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.io.*;

import common.Post.RegionType;
import common.Post;
import common.Region;
import common.Title;

public class FaceBreakRegion {

	//private Region region;

	//private ArrayList<Post> posts;

	//private int regionID;
	//private int viewerID;

	public static final String regionsFolder = FileSystem.regionsFolder;
	private static final String regionPostsFile = FileSystem.regionPostsFile;
	private static final String regionInfoFile = FileSystem.regionInfoFile;
//	private static final int NUM_POSTS_TO_READ = 10;

	/*
	public FaceBreakRegion(int posterID, int ownerID, int regionID){
		try{
			if (FaceBreakUser.checkIfUserExists(ownerID) &&
					checkIfRegionExists(ownerID, regionID) &&
					FaceBreakUser.checkIfUserExists(posterID)){
				viewerID = posterID;
				this.regionID = regionID;

				RegionType type;
				switch(regionID){
				case 0:
					type = Post.RegionType.PUBLIC;
					break;
				case 1:
					type = Post.RegionType.PRIVATE;
					break;
				default:
					type = Post.RegionType.COVERT;
					break;
				}

				this.region = new Region(ownerID, type);

				// Get users that are allowed to read the file
				FileReader fReader = new FileReader(Integer.toString(ownerID) +
						"\\" + regionsFolder + "\\" + Integer.toString(regionID) +
						"\\" + regionInfoFile);
				BufferedReader inputReader = new BufferedReader(fReader);
				String temp;
				//this.region.setPermissibleUsers(new ArrayList<User>());
				while( (temp = inputReader.readLine()) != null){
					int allowedID = Integer.parseInt(temp);
					FaceBreakUser fbuser = new FaceBreakUser(allowedID);
					//this.region.getPermissibleUsers().add(fbuser.getUser());
				}

				// Get posts in array
				fReader = new FileReader(Integer.toString(ownerID) +
						"\\" + regionsFolder + "\\" + Integer.toString(regionID) +
						"\\" + regionPostsFile);
				inputReader = new BufferedReader(fReader);;
				this.posts = new ArrayList<Post>();
				while( (temp = inputReader.readLine()) != null){
					String [] linesplit = temp.split(":");
					if(linesplit.length > 1){
						FaceBreakUser poster = new FaceBreakUser(Integer.parseInt(linesplit[1]));
						Post post = new Post();
						post.setOwnerId(ownerID);
						post.setRegion(type);
						post.setWriterName(poster.getUser().getUsername());
						post.setWriterId(poster.getUser().getId());
						post.setText(linesplit[2]);
						posts.add(post);
					}
				}
				this.region.setPosts(this.posts);

				//this.view();
			}
			else {
				region = null;
			}
		} catch(Exception e){
			System.err.println("Error: " + e.getMessage());
			region = null;
		}
	}
	*/

	public static int addRegion(int ownerID, RegionType regionType){
		// posts file indicates name of posts file, not folder
		// posts are stored in single file
		// Same directory as rest of user info, but different file
		try{
			
			int maxRegions = 0; 
			switch(FaceBreakUser.getProfile(ownerID).getTitle()){
				case BOSS:
					maxRegions = 27;
					break;
				case CAPO:
					maxRegions = 12;
					break;
				case SOLDIER:
					maxRegions = 7;
					break;
				case ASSOC:
					maxRegions = 2;
					break;
			}
			
			String ownerIDstr = Integer.toString(ownerID);
			
			File userRegionsFolder = new File(ownerIDstr + "\\" + regionsFolder);
			int folderCount= 0;
			for (File file : userRegionsFolder.listFiles()) {
                if (file.isDirectory()) {
                        folderCount++;
                }
			}
			
			if(folderCount >= maxRegions){
				System.err.println("Cannot create additional regions");
				return 1;
			}
			
			
			int regionID = 1;
			File regionFolder;
			if(regionType == Post.RegionType.COVERT){
				do{
					regionID++;
					regionFolder = new File(ownerIDstr + "\\" + regionsFolder + 
						"\\" + Integer.toString(regionID));
				} while(regionFolder.exists());
			} else { // Public or private
				if(regionType == Post.RegionType.PUBLIC){
					regionID = 0;
				} else {
					regionID = 1;
				}
				if(checkIfRegionExists(ownerID, regionID)){
					System.err.println("Cannot create a second public or private region");
					return 1;
				}
			}

			String path = ownerIDstr + "\\" + regionsFolder + "\\" +
					Integer.toString(regionID) + "\\" + regionPostsFile;
			File postsFile = new File(path);
			postsFile.getParentFile().mkdirs();
			String newPost = Long.toString((new Date()).getTime()) + ":"
			+ "System Message" + ":" + "Board created";
			FileSystem.writeSecure(newPost, path);
			//postsFile.createNewFile();

			// Fill in info for user
			// For now this file only contains allowed viewers of the board
			String info = ownerIDstr;
			FileSystem.writeSecure(info, ownerIDstr + "\\" + regionsFolder + "\\" +
					Integer.toString(regionID) + "\\" + regionInfoFile);

			return regionID;

		} catch(Exception e){
			System.err.println("Error: " + e.getMessage());
			return 1;
		}
	}

	private static boolean checkIfRegionExists(int ownerID, int regionID){
		try{
			String ownerIDstr = Integer.toString(ownerID);
			String regionIDstr = Integer.toString(regionID);
			File regionFolder = new File(ownerIDstr + "\\" + regionsFolder + 
					"\\" + regionIDstr);
			return regionFolder.exists();

		} catch(Exception e){
			System.err.println("Error: " + e.getMessage());
			return false;
		}
	}

	public static int addToViewable(int uid, int regionID, int[] friendIDs){
		try{
			String ownerIDstr = Integer.toString(uid);
			String regionIDstr = Integer.toString(regionID);
			String filename = ownerIDstr + "\\" + regionsFolder + "\\" +
					regionIDstr + "\\" + regionInfoFile;
			
			if(FileSystem.lockMap.get(filename) == null){
				FileSystem.lockMap.put(filename, new ReentrantLock());
			}
			FileSystem.lockMap.get(filename).lock();
			
			ArrayList<String> allowed = FileSystem.readSecure(filename);
			
			if(allowed.get(0).equals("ERROR")){
				FileSystem.lockMap.get(filename).unlock();
				System.out.println("FILE INTEGRITY COMPROMISED");
				return -1;
			}
			
			for(int i = 0; i < friendIDs.length; i++){
				if(friendIDs[i] == -1)
					continue;
				if(!checkViewable(uid, regionID, friendIDs[i])
						&& FaceBreakUser.checkIfUserExists(friendIDs[i])){
					allowed.add(Integer.toString(friendIDs[i]));
				}
			}
			
			String fileContents = "";
			
			for(int i = 0; i < allowed.size() - 1; i++){
				fileContents = fileContents + allowed.get(i) + "\n";
			}
			fileContents = fileContents + allowed.get(allowed.size() - 1);
			
			FileSystem.writeSecure(fileContents, filename);
			
			FileSystem.lockMap.get(filename).unlock();
			
			return 0;
		}catch(Exception e){
			System.err.println("Error: " + e.getMessage());
			return -1;
		}
	}

	private static boolean checkViewable(int uid, int regionID, int friendID){
		try{
			
			String filename = Integer.toString(uid) +
					"\\" + regionsFolder + "\\" + Integer.toString(regionID) + "\\" + regionInfoFile;
			
			if(FileSystem.lockMap.get(filename) == null){
				FileSystem.lockMap.put(filename, new ReentrantLock());
			}
			FileSystem.lockMap.get(filename).lock();
			
			ArrayList<String> allowed = FileSystem.readSecure(filename);
			
			if(allowed.get(0).equals("ERROR")){
				FileSystem.lockMap.get(filename).unlock();
				System.out.println("FILE INTEGRITY COMPROMISED");
				return false;
			}

			FileSystem.lockMap.get(filename).unlock();
			
			for(int i = 0; i < allowed.size(); i++){
				if(allowed.get(i).equals(Integer.toString(friendID))){
					return true;
				}
			}

			return false;
		} catch(Exception e){
			System.err.println("Error: " + e.getMessage());
			return false;
		}
	}
	
	/*
	 * Get a list of region ids on the board owned by uid that are viewable by friendID
	 */
	public static ArrayList<Integer> getViewable(int uid, int friendID){
		try{
			
			ArrayList<Integer> viewableList = new ArrayList<Integer>();
			
			for(int regionID = 0; regionID < 27; regionID++){
				String filename = Integer.toString(uid) +
						"\\" + regionsFolder + "\\" + Integer.toString(regionID) + "\\" + regionInfoFile;
				
				File f = new File(filename);
				if(regionID == 0){
					viewableList.add(new Integer(regionID));
				} else if(regionID == 1){
					if(FaceBreakUser.getProfile(uid).getFamily().equals(FaceBreakUser.getProfile(friendID).getFamily())){
						viewableList.add(new Integer(regionID));
					}
				}else if(f.exists()){
					if(checkViewable(uid, regionID, friendID)){
						viewableList.add(new Integer(regionID));
					}
				}
			}

			return viewableList;
		} catch(Exception e){
			System.err.println("Error: " + e.getMessage());
			return null;
		}
	}
	
    /*
	public void post(int posterID, String msg){
		try{
			String newPost = "\n" + Long.toString((new Date()).getTime())
					+ ":" + Integer.toString(posterID) + ":" + msg;
			BufferedWriter bWriter = new BufferedWriter(
					new FileWriter(Integer.toString(this.region.getOwnerId())
							+ "\\" + regionsFolder + "\\" + Integer.toString(regionID) + 
							"\\" + regionPostsFile, true));
			bWriter.write(newPost);
			bWriter.close();

			FaceBreakUser poster = new FaceBreakUser(posterID);

			RegionType type;
			switch(regionID){
				case 0:
					type = Post.RegionType.PUBLIC;
					break;
				case 1:
					type = Post.RegionType.PRIVATE;
					break;
				default:
					type = Post.RegionType.COVERT;
					break;
			}
			Post post = new Post();
			post.setOwnerId(this.region.getOwnerId());
			post.setRegion(type);
			post.setWriterName(poster.getUser().getUsername());
			post.setWriterId(poster.getUser().getId());
			post.setText(msg);

			// Make sure to add this to the list of the region's posts
			this.posts.add(post);

			this.region.setPosts(this.posts);

		}catch(Exception e){
			System.err.println("Error: " + e.getMessage());
		}
	}
	*/
	/*
	public Post[] view(){
		try{
			ArrayList<Post> posts = this.region.getPosts();
			
			ArrayList<Post> postsToView = new ArrayList<Post>();
			
			int i = 0;
			Post [] viewable = new Post[NUM_POSTS_TO_READ];
			// Retrieve messages
			if (posts.size() > NUM_POSTS_TO_READ){
				i = posts.size() - NUM_POSTS_TO_READ;
			}
			int j = 0;
			while(i < posts.size()){
				postsToView.add(posts.get(i));
				viewable[j] = posts.get(i);
				i++; j++;
			}
			
			region.setRecent(viewable);
			
			return region.getRecent();
			
		}catch(Exception e){
			System.err.println("Error: " + e.getMessage());
			return null;
		}
	}
	*/

	/*
	public Post[] viewAll(){
		try{
			return this.region.getPosts();
		}catch(Exception e){
			System.err.println("Error: " + e.getMessage());
			return null;
		}
	}
	*/


	public static boolean createPost(Post myPost) {
		int oid = myPost.getOwnerId();
		// int wid = myPost.getWriterId();
		int rid = myPost.getRegionId();

		try {
			String path = Integer.toString(oid) + "\\"
					+ regionsFolder + "\\" + Integer.toString(rid) + "\\"
					+ regionPostsFile;
			
			if(FileSystem.lockMap.get(path) == null){
				FileSystem.lockMap.put(path, new ReentrantLock());
			}
			FileSystem.lockMap.get(path).lock();
			
			ArrayList<String> posts = FileSystem.readSecure(path);
			
			if(posts.get(0).equals("ERROR")){
				FileSystem.lockMap.get(path).unlock();
				System.out.println("FILE INTEGRITY COMPROMISED");
				return false;
			}
			
			String newPost = Long.toString((new Date()).getTime()) + ":"
					+ myPost.getWriterName() + ":" + myPost.getText() + "\n";

			posts.add(newPost);
			
			String fileContents = "";
			
			for(int i = 0; i < posts.size() - 1; i++){
				fileContents = fileContents + posts.get(i) + "\n";
			}
			fileContents = fileContents + posts.get(posts.size() - 1);
			
			FileSystem.writeSecure(fileContents, path);
			
			FileSystem.lockMap.get(path).unlock();
			
			return true;
		} catch (Exception e) {
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
		
		if(FileSystem.lockMap.get(path) == null){
			FileSystem.lockMap.put(path, new ReentrantLock());
		}
		FileSystem.lockMap.get(path).lock();
		
		ArrayList<String> postLines = FileSystem.readSecure(path);
		
		if(postLines.get(0).equals("ERROR")){
			FileSystem.lockMap.get(path).unlock();
			System.out.println("FILE INTEGRITY COMPROMISED");
			return null;
		}
		
		ArrayList<Post> allPosts = new ArrayList<Post>();

		try {
			String tmp;
			for(int i = 0; i < postLines.size(); i++) {
				tmp = postLines.get(i);
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
			
			FileSystem.lockMap.get(path).unlock();
			
			return allPosts;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}