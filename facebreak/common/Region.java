package facebreak.common;

import java.io.Serializable;
import java.util.ArrayList;

import facebreak.common.Post.RegionType;

public class Region implements Serializable {
	private int owner;
	private String ownerName;
	private RegionType regionType;
	private Post[] allPosts;
//	private ArrayList<Post> posts;
//	private ArrayList<FBClientUser> permissibleUsers;
	
	public Region(int owner, RegionType regionType) {
		this.owner = owner;
		this.regionType = regionType;
	}
	
	public Region(String ownerName) {
		this.ownerName = ownerName;
	}
	
	public Region(String ownerName, RegionType regionType) {
		this.ownerName = ownerName;
		this.regionType = regionType;
	}
	
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	
	public int getOwnerId() {
		return owner;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public RegionType getRegionType() {
		return regionType;
	}
//	public ArrayList<Post> getPosts() {
//		return posts;
//	}
//	public void setPosts(ArrayList<Post> posts) {
//		this.posts = posts;
//	}
	
	public Post[] getPosts() {
		return allPosts;
	}
	
	public void setPosts(ArrayList<Post> posts) {
		allPosts = posts.toArray(new Post[posts.size()]);
	}
	
	public void setPosts(Post[] posts) {
		allPosts = new Post[posts.length];
		for(int i = 0; i < posts.length; i++)
			allPosts[i] = posts[i];
	}
	
//	public ArrayList<FBClientUser> getPermissibleUsers() {
//		return permissibleUsers;
//	}
//	public void setPermissibleUsers(ArrayList<FBClientUser> permissibleUsers) {
//		this.permissibleUsers = permissibleUsers;
//	}
}
