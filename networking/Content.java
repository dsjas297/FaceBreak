package facebreak.networking;

import java.io.Serializable;

import facebreak.common.Post;
import facebreak.common.Profile;
import facebreak.common.Region;
import facebreak.common.User;

/*
 * Wrapper class
 */
public class Content implements Serializable {
	private User user;
	private Profile profile;
	private Post post;
	private Region board;
	private Post[] requestedPosts;
	
	public Content() {
		user = null;
		profile = null;
		post = null;
		board = null;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
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

	public Post[] getBoard() {
		return board.getRecent();
	}

	public void setBoard(Region board) {
		this.board = board;
	}
}
