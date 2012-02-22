package facebreak.networking;

public class AuthenticatedUser {
	private int id;
	private boolean isLoggedIn;
	private String username;
	// more stuff here in future
	
	protected AuthenticatedUser(String username) {
		this.username = username;
		isLoggedIn = false;
	}
	
	protected AuthenticatedUser(int id) {
		this.id = id;
		isLoggedIn = false;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	protected int getId() {
		return id;
	}
	
	protected void setId(int id) {
		this.id = id;
	}
	
	protected void logIn() {
		isLoggedIn = true;
	}
	
	protected void logOut() {
		isLoggedIn = false;
	}
	
	protected boolean isLoggedIn() {
		return isLoggedIn;
	}
}
