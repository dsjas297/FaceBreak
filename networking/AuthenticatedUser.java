/**
 * @author gd226
 * 
 * An AuthenticatedUser is an object stored by the client handler (server side) to keep
 * track of the user at the client end.
 * It is instantiated once an secure connection is established with a valid user.
 * Once the user signs off, the authenticated user is destroyed.
 */

package networking;

public class AuthenticatedUser {
	private int id;
	private boolean isLoggedIn;
	private String username;
//	private short pwdRetries;
	
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
