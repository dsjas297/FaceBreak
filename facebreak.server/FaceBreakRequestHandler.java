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
		
		System.out.println("Server started...");
			
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
            new FaceBreakRequestHandler(clientSocket).start();
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
		clientSocket = client;
	}
	
	private void run(){
		System.out.println("Client connected")
		
		// Set up streams
		String inputLn, outputLn;
		
		BufferedReader clientReader = new BufferedReader(
			new InputStreamReader(clientSocket.getInputStream()));
			
		PrintWriter clientWrite = new PrintWriter(
			clientSocket.getOutputStream(), true);
		
		
		
		// Determine whether trying to login or create a new account
		
		// Load up the user
		
		
		// Start up the request loop
		while ((inputLn = clientReader.readLine()) != null)
		{   
			outputLn = process(inputLn);
			clientWrit.println(outputLine);
		}
		
		// When we are done, we can just clean up and exit run
	}
}