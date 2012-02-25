package facebreak.server;

import java.util.*;
import java.io.*;

import facebreak.common.*;
import facebreak.common.Post.RegionType;

public class FaceBreakRegion {
	
	private Region region;
	
	private int regionID;
	private int viewerID;
	
	public static final String regionsFolder = "regions";
	private static final String regionPostsFile = "posts";
	private static final String regionInfoFile = "regionInfo";
	private static final int NUM_POSTS_TO_READ = 10;
	
	public FaceBreakRegion(int posterID, int ownerID, int regionID){
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
			
			
			
			this.view();
		}
		else {
			region = null;
		}
	}
	
	public static int addRegion(int ownerID, RegionType regionType){
		// posts file indicates name of posts file, not folder
		// posts are stored in single file
		// Same directory as rest of user info, but different file
		try{
			String ownerIDstr = Integer.toString(ownerID);
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
					return 1;
				}
			}
			
			String path = ownerIDstr + "\\" + regionsFolder + "\\" +
					Integer.toString(regionID) + "\\" + regionPostsFile;
			File postsFile = new File(path);
			postsFile.getParentFile().mkdirs();
			postsFile.createNewFile();
			
			// Fill in info for user
			// For now this file only contains allowed viewers of the board
			String info = ownerIDstr;
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(ownerIDstr + "\\" + regionsFolder + "\\" +
					Integer.toString(regionID) + "\\" + regionInfoFile, false));
			bWriter.write(info);
			bWriter.close();
			
			return 0;
			
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
	
	public void addToViewable(int friendID){
		try{
			String ownerIDstr = Integer.toString(this.region.getOwnerId());
			String regionIDstr = Integer.toString(this.regionID);
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(ownerIDstr + "\\" + regionsFolder + "\\" +
					regionIDstr + "\\" + regionInfoFile, false));
			bWriter.write("\n" + Integer.toString(friendID));
			bWriter.close();
			
			FaceBreakUser fbuser = new FaceBreakUser(friendID);
			
			this.region.getPermissibleUsers().add(fbuser.getUser());
		}catch(Exception e){
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public void post(int posterID, String msg){
		try{
			String newPost = "\n" + Long.toString((new Date()).getTime())
					+ " " + Integer.toString(posterID) + ":" + msg;
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
			Post post = new Post(this.region.getOwnerId(), type, poster.getUser().getName(), msg);
			// Make sure to add this to the list of the region's posts
			region.getPosts().add(post);
			
		}catch(Exception e){
			System.err.println("Error: " + e.getMessage());
		}
	}
	
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
	
	
}
