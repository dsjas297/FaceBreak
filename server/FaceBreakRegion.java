package server;

import java.util.*;
import java.io.*;

import common.Post.RegionType;
import common.Post;
import common.Region;

public class FaceBreakRegion {
	
	private Region region;
	
	private ArrayList<Post> posts;
	
	private int regionID;
	private int viewerID;
	
	public static final String regionsFolder = "regions";
	private static final String regionPostsFile = "posts";
	private static final String regionInfoFile = "regionInfo";
	private static final int NUM_POSTS_TO_READ = 10;
	
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
					System.err.println("Cannot create a second public or private region");
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
	
	public int addToViewable(int friendID){
		try{
			if(checkViewable(friendID) || !FaceBreakUser.checkIfUserExists(friendID)){
				System.err.println("Error: User either doesn't exist or can already view board");
				return 1;
			}
			
			String ownerIDstr = Integer.toString(this.region.getOwnerId());
			String regionIDstr = Integer.toString(this.regionID);
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(ownerIDstr + "\\" + regionsFolder + "\\" +
					regionIDstr + "\\" + regionInfoFile, true));
			bWriter.write("\n" + Integer.toString(friendID));
			bWriter.close();
			
			FaceBreakUser fbuser = new FaceBreakUser(friendID);
			//this.region.getPermissibleUsers().add(fbuser.getUser());
			return 0;
		}catch(Exception e){
			System.err.println("Error: " + e.getMessage());
			return 0;
		}
	}
	
	private boolean checkViewable(int friendID){
		try{
			FileReader fReader = new FileReader(Integer.toString(this.region.getOwnerId()) +
					"\\" + regionsFolder + "\\" + Integer.toString(this.regionID) + "\\" + regionInfoFile);
			BufferedReader inputReader = new BufferedReader(fReader);
			String temp;
			while( (temp = inputReader.readLine()) != null){
				if(temp.equals(Integer.toString(friendID))){
					inputReader.close();
					return true;
				}
			}
			
			inputReader.close();
			return false;
		} catch(Exception e){
			System.err.println("Error: " + e.getMessage());
			return false;
		}
	}
	
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
	
	public Post[] viewAll(){
		try{
			return this.region.getPosts();
		}catch(Exception e){
			System.err.println("Error: " + e.getMessage());
			return null;
		}
	}
	
	
}
