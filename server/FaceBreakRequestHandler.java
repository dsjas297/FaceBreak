import java.net.*;
import java.io.*;

public class FaceBreakRequestHandler extends Thread
{ 
	/*
	ATTRIBUTES FOR MAIN METHOD
	*/
	private static final int PORT = 8080;

	
	/*
	MAIN METHOD
	*/
	
	public static void main(String [] args)
	{
		// Some sort of authorization will take place here
		
		// Spawn a thread to create a request handler for every logged in user
			// I think we might need this to keep track of session time for each user
			
		// Attempt to listen on a socket
		ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + PORT);
            System.exit(1);
        }
			
		while(1){
			Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Failed to accept client connection: " + PORT + ", " + e.getMessage());
                continue;
            }
            new KKMultiServerThread(clientSocket).start();
		}
		
		try {
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("Failed to close server socket: " + e.getMessage());
        }
	}

	/*
	ATTRIBUTES
	*/
	Socket clientSocket;
	
	/*
	METHODS
	*/
	private FaceBreakRequestHandler(Socket client){
	
	}
	
	private void handleRequest()
}