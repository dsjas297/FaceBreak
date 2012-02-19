package facebreak.networking;

public enum Error {
	SUCCESS(0, "Success!"), 
	CONNECTION(1, "Could not connect to server."), 
	TIMEOUT(2, "Request has timed out."), // same as inactivity?
	INACTIVITY(2, "You have been logged out due to inactivity. Please log back in."), 
	DUPLICATE_USER(3, "This user already exists."),
	NO_USER(4, "This user does not exist."), 
	PRIVILEGE(5, "This user does not have the privilege."), 
	PASSWORD(6,	"Incorrect username/password combination."), 
	PASSWORD_TIMEOU(7, "Too many login attempts."), 
	UNKNOWN_REQUEST(7, "Invalid request."),
	LOGIN(8, "Not logged in."),
	UNKNOWN_ERROR(10, "Unknown error occurred."); // not sure what this is for?

	private final int code;
	private final String msg;

	private Error(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public String getDescription() {
		return msg;
	}

	public int getCode() {
		return code;
	}

	@Override
	public String toString() {
		return code + ": " + msg;
	}
}
