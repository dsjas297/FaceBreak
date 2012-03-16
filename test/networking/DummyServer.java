package networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class DummyServer {
	private static final int PORT_NUM = 4444;

	private ServerSocket listener;

	private DummyServer() {

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
				FBClientHandler clientThread = new FBClientHandler(clientSocket);
				clientThread.start();
			} catch (IOException ioe) {
				System.err.println("Error accepting client. Stack Trace:");
				ioe.printStackTrace();
			}
		}
	}

	public static void main(String args[]) {
		new DummyServer();
	}
}
