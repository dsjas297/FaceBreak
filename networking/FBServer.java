package facebreak.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FBServer {
	private ServerSocket listener;
	
	private static final int port = 4444;

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

		System.out.println("Listening for clients...");

		// Successfully created Server Socket. Now wait for connections.
		while(true) {
			try {
				Socket clientSocket = listener.accept();
				FBClientHandler clientThread = new FBClientHandler(clientSocket);
				clientThread.start();
			} catch (IOException ioe) {
				System.out.println("Error accepting client. Stack Trace:");
				ioe.printStackTrace();
			}
		}
	}

	public static void main(String args[]) {
		new FBServer();
	}
}
