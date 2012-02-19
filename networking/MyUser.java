package facebreak.networking;

public class MyUser {
	private int id;
	private String name;
	private String password;
	private String hashedPassword;
	
	public MyUser() {
		this.name = new String();
		this.password = new String();
	}
	
	public MyUser(String name, String password) {
		this.name = new String(name);
		this.password = new String(password);
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
