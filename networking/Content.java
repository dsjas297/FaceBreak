package networking;

import java.io.Serializable;

import common.Board;
import common.FBClientUser;
import common.Post;
import common.Profile;
import common.Region;



/*
 * Wrapper class
 */
public class Content implements Serializable {
	private FBClientUser user;
	private String requestedUsername;
	private Profile profile;
	private Post post;
	private Region region;
	private Board board;
	
	public Content() {
		user = null;
		profile = null;
		post = null;
		region = null;
		board = null;
	}

	public FBClientUser getUser() {
		return user;
	}

	public void setUser(FBClientUser user) {
		this.user = user;
	}
	
	public void setRequestedUser(String requestedUsername) {
		this.requestedUsername = requestedUsername;
	}
	
	public String getRequestedUser() {
		return requestedUsername;
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
	public Region getRegion(){
		return region;
	}
	public void setRegion(Region region){
		this.region = region;
	}
	public Board getBoard() {
		return board;
	}
	
	public void setBoard(Board board) {
		this.board = board;
	}
}
