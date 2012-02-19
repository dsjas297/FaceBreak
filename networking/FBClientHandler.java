package facebreak.networking;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class FBClientHandler extends Thread {
	private Socket client;
	private AuthenticatedUser user;
	private FileOutputStream fos;
	private ObjectOutputStream oos;
	private FileInputStream fis;
	private ObjectInputStream ois;
	
	public FBClientHandler(Socket client) {
		this.client = client;
		fos = null;
		oos = null;
		fis = null;
		ois = null;
	}

	public static Reply parseRequest(Request msg) {
		RequestType type = msg.getRequestType();

		Reply reply = new Reply();

		switch (type) {
		case REQUEST_LOGIN:
			processLogin(reply);
			break;
		case REQUEST_LOGOUT:
			processLogout(reply);
			break;
		case REQUEST_CREATE_USER:
			processCreateUser(reply);
		case REQUEST_CHANGE_PWD:
			processChangePassword(reply);
			break;
		case REQUEST_VIEW:
			processViewPost(reply);
			break;
		case REQUEST_POST:
			processCreatePost(reply);
			break;
		case REQUEST_DELETE:
			processDeletePost(reply);
			break;
		default:

		}
		return reply;
	}

	public static void sendReply(Reply reply) {
		
	}

	public static void processLogin(Reply reply) {
		
	}

	public static void processLogout(Reply reply) {
		
	}

	public static void processCreateUser(Reply reply) {
		
	}

	public static void processChangePassword(Reply reply) {
		
	}

	public static void processDeletePost(Reply reply) {
		
	}

	public static void processCreatePost(Reply reply) {
		
	}

	public static void processViewPost(Reply reply) {
		
	}

	public void run() {
		System.out.println("Accepted client on machine "
				+ client.getInetAddress().getHostName());

		try {
			ois = new ObjectInputStream(client.getInputStream());			
			
			while (true) {
				// do stuff here
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				client.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
}
