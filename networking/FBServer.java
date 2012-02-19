package facebreak.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class FBServer {
	private ServerSocket listener;
	private int port;

	private HashMap<Integer, AuthenticatedUser> connectedUsers;

	public FBServer() {
		System.out.println("Starting up server...");

		try {
			// Create the server socket.
			listener = new ServerSocket(port);
			System.out.println("Successfully created server on port " + port);
		} catch (IOException ioe) {
			System.out.println("Could not create server socket on port " + port
					+ ".");
			System.out.println("Exiting...");
			System.exit(-1);
		}

		System.out.println("Listening for clients on 12111...");

		// Successfully created Server Socket. Now wait for connections.
		while (true) {
			try {
				Socket clientSocket = listener.accept();
				FBClientHandler clientThread = new FBClientHandler(clientSocket);
				clientThread.start();
			} catch (IOException ioe) {
				System.out
						.println("Exception encountered on accept. Stack Trace:");
				ioe.printStackTrace();
			}
		}
	}

	public synchronized void connectToUser(AuthenticatedUser user) {
		connectedUsers.put(user.getId(), user);
	}

	public synchronized void disconnectToUser(AuthenticatedUser user) {
		user.logOut();
		connectedUsers.put(user.getId(), user);
	}

	public static void main(String args[]) {

	}
}
