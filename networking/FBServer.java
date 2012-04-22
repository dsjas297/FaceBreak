/**
 * @author gd226, jsh263
 * 
 * Starts server (needs password), initializes filesystem, waits for incoming connections
 * to hand off to FBClientHandler thread.
 * 
 * SERVER PWD: SrrEs5d7Um
 */

package networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import server.FileSystem;

public class FBServer {
	private static final int PORT_NUM = 4444;
	private static final short MAX_PWD_RETRIES = 3;

	private ServerSocket listener;

	public FBServer() {
		 validatePassword();

		// create file system
		FileSystem.initDirTree();
		// initialize secure random number generator
		RandomGenerator.init();

		System.out.println("Starting up server...");

		try {
			// Create server socket.
			listener = new ServerSocket(PORT_NUM);
			System.out.println("Successfully created server on port "
					+ PORT_NUM);
		} catch (IOException ioe) {
			System.err.println("Could not create server socket on port "
					+ PORT_NUM + ".");
			System.err.println("Exiting...");
			System.exit(-1);
		}

		System.out.println("Listening for clients...");

		// Successfully created Server Socket. Now wait for connections.
		while (true) {
			try {
				Socket clientSocket = listener.accept();
				System.out
						.println("Spinning up thread for new client connection.");
				FBClientHandler clientThread = new FBClientHandler(clientSocket);
				clientThread.start();
			} catch (IOException ioe) {
				System.err.println("Error accepting client. Stack Trace:");
				ioe.printStackTrace();
			}
		}
	}

	private static void validatePassword() {
		short numRetries = 0;

		// Password is: SrrEs5d7Um
		byte[] passwordHash = { 101, -122, 80, 50, 40, -53, 67, 46, 23, 19, 18,
				-73, 79, 83, 13, -15, 60, 98, -109, -4, 84, -117, 48, 18, -74,
				-95, 82, 83, -78, -36, -60, -120, 0 };
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		char[] passwd = new char[50];
		byte[] passwd_bytes = null; 
		int numChars = 0;
		
		String password = "";
		byte[] hashed;

		while(true) {
			if(++numRetries > MAX_PWD_RETRIES) {
				System.out.println("Too many password attempts. Exiting.");
				System.exit(0);
			}
			
			System.out.println("Enter password:");
			try {
				numChars = 0;
				if(passwd_bytes != null){
					for(int i = 0; i < passwd_bytes.length; i++){
						passwd_bytes[i] = 0;
					}
				}
				for(int i = 0; i < passwd.length; i++){
					passwd[i] = 0;
				}
				//password = reader.readLine().trim();
				numChars = reader.read(passwd);
				//System.out.println(numChars);
				passwd_bytes = new byte[numChars - 2];
				for(int i = 0; i < passwd_bytes.length; i++){
					passwd_bytes[i] = (byte)(passwd[i]);
				}
				md.update(passwd_bytes);
				//md.update(password.getBytes());
				hashed = md.digest();
				int i = 0;
				while (i < hashed.length) {
					if (passwordHash[i] != hashed[i]) {
						break;
					}
					i++;
				}
				if (i == hashed.length) 
					break;
				Thread.sleep(2000);
			} catch (Exception e) {
				numChars = 0;
				if(passwd_bytes != null){
					for(int i = 0; i < passwd_bytes.length; i++){
						passwd_bytes[i] = 0;
					}
				}
				for(int i = 0; i < passwd.length; i++){
					passwd[i] = 0;
				}
				System.out.println(e.getMessage());
			}
		}
		
		numChars = 0;
		if(passwd_bytes != null){
			for(int i = 0; i < passwd_bytes.length; i++){
				passwd_bytes[i] = 0;
			}
		}
		for(int i = 0; i < passwd.length; i++){
			passwd[i] = 0;
		}
		
		try {
			md = MessageDigest.getInstance("SHA-512");
			md.update(password.getBytes());
			hashed = md.digest();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		FileSystem.password = hashed;
		FileSystem.lockMap = new HashMap<String, ReentrantLock>();
		
		// clear password hash
		for(int j = 0; j < passwordHash.length; j++)
			passwordHash[j] = 0;
	}
}
