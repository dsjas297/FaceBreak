package facebreak.common;

public enum Error {
	SUCCESS(0, "Success!"), 
	CONNECTION(1, "Could not connect to server. Closing connection."), 
	SERVER(2, "There was a problem on the server end. Please try again."),
	TIMEOUT(3, "Request has timed out."),	// not sure how to implement this
	INACTIVITY(4, "You have been logged out due to inactivity. Please log back in."),	// not sure how to implement this
	DUPLICATE_USER(5, "This user already exists. Please choose different username."),
	NO_USER(6, "This user does not exist."), 
	PRIVILEGE(7, "This user does not have the privilege."), 
	USERNAME_PWD(8,	"Incorrect username/password combination."), 
	PWD_EXCEED_RETRIES(9, "Too many login attempts."), 	
	MALFORMED_REQUEST(10, "Invalid request."),
	LOGIN(11, "Not logged in."),
	LOGGEDIN_USER(12, "There is a user already logged in."),
	UNKNOWN_ERROR(13, "Unknown error occurred."); // does this mean anything?

	public final int code;
	public final String msg;

	private Error(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	@Override
	public String toString() {
		if(code == SUCCESS.code)
			return msg;
		return "Error: " + msg;
	}
}
