package networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;

import server.ServerBackend;

public class FBServer {
	private ServerSocket listener;
	
	private static final int port = 4444;

	public FBServer() {
		ServerBackend.initDirTree();
		
		// Password is: SrrEs5d7Um
		boolean passwordCorrect = false;
		byte[] passwordHash = {101, -122, 80, 50, 40, -53, 67, 46, 23, 19, 18, 
				-73, 79, 83, 13, -15, 60, 98, -109, -4, 84, -117, 48, 18, -74,
				-95, 82, 83, -78, -36, -60, -120, 0}; 
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		MessageDigest md = null;
		try{
			md = MessageDigest.getInstance("SHA-256");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		String password = "";
		byte[] hashed;
		
		while(!passwordCorrect){
			System.out.println("Enter password:");
				try{
					password = reader.readLine().trim();
					md.update(password.getBytes());
					hashed = md.digest();
					int i = 0;
					while( i < hashed.length){
						if(passwordHash[i] != hashed[i]){
							break;
						}
						i++;
					}
					Thread.sleep(2000);
					if(i == hashed.length){
						passwordCorrect = true;
					}
				}catch(Exception e){
					System.out.println(e.getMessage());
				}
		}
		
		System.out.println("Starting up server...");

		//ServerBackend.key = password.getBytes();
		
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
