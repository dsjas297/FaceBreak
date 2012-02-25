package facebreak.networking;

import java.io.Serializable;

import facebreak.common.Post;
import facebreak.common.Profile;
import facebreak.common.Region;
import facebreak.common.FBClientUser;

/*
 * Wrapper class
 */
public class Content implements Serializable {
	private FBClientUser user;
	private Profile profile;
	private Post post;
	private Region board;
	
	public Content() {
		user = null;
		profile = null;
		post = null;
		board = null;
	}

	public FBClientUser getUser() {
		return user;
	}

	public void setUser(FBClientUser user) {
		this.user = user;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	public Region getBoard() {
		return board;
	}
	
	public void setBoard(Region board) {
		this.board = board;
	}
}
