package common;

import java.io.Serializable;
import java.util.ArrayList;

import common.Post.RegionType;

public class Region implements Serializable {
	private int ownerId;
	private int regionId;
	private String ownerName;
	private RegionType regionType;
	private Post[] allPosts;
//	private ArrayList<Post> posts;
//	private ArrayList<FBClientUser> permissibleUsers;
	
	public Region(int owner, RegionType regionType) {
		this.ownerId = owner;
		this.regionType = regionType;
	}

	public Region(int owner, int regionId) {
		this.ownerId = owner;
		this.regionId = regionId;
	}
	
	public Region(String ownerName) {
		this.ownerName = ownerName;
	}
	
	public Region(String ownerName, RegionType regionType) {
		this.ownerName = ownerName;
		this.regionType = regionType;
	}
	
	public Region(String ownerName, int regionId) {
		this.ownerName = ownerName;
		this.regionId = regionId;
	}
	
	public void setRegionId(int regionId) {
		this.regionId = regionId;
	}
	
	public int getRegionId() {
		return regionId;
	}
	
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	
	public void setOwnerId(int oid) {
		ownerId = oid;
	}
	public int getOwnerId() {
		return ownerId;
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
