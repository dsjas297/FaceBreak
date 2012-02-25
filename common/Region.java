package facebreak.common;

import java.io.Serializable;
import java.util.ArrayList;

import facebreak.common.Post.RegionType;

public class Region implements Serializable {
	private int owner;
	private String ownerName;
	private RegionType regionType;
	private ArrayList<Post> posts;
	private ArrayList<FBClientUser> permissibleUsers;
	
	public Region(int owner, RegionType regionType) {
		this.owner = owner;
		this.regionType = regionType;
	}
	
	public Region(String ownerName, RegionType regionType) {
		this.ownerName = ownerName;
		this.regionType = regionType;
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
	public ArrayList<Post> getPosts() {
		return posts;
	}
	public void setPosts(ArrayList<Post> posts) {
		this.posts = posts;
	}

	public ArrayList<FBClientUser> getPermissibleUsers() {
		return permissibleUsers;
	}
	public void setPermissibleUsers(ArrayList<FBClientUser> permissibleUsers) {
		this.permissibleUsers = permissibleUsers;
	}
}
