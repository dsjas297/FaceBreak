package facebreak.networking;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class FBClient {

	private Socket socket;
	private int port;
	private InetAddress serverAddr;
	private AuthenticatedUser myUser;
	private FileOutputStream fos;
	private ObjectOutputStream oos;
	private FileInputStream fis;
	private ObjectInputStream ois;
	
	public FBClient() throws UnknownHostException {
		port = 4444;
		serverAddr = InetAddress.getLocalHost();
		socket = null;
		fos = null;
		oos = null;
		fis = null;
		ois = null;
	}
	
	public FBClient(InetAddress serverAddr, int port) {
		this.port = port;
		this.serverAddr = serverAddr;
		socket = null;
	}
	
	public Error login(MyUser user) {
		
		try {
			socket = new Socket(serverAddr, port);
			
			
			
			Reply serverReply = new Reply();
			return serverReply.getReturnError();
		} catch (IOException e) {
			return Error.CONNECTION;
		}
	}

	public Error logout(MyUser user) {
		if(socket == null)
			return Error.LOGIN;
		
		try {
			oos.close();
			fos.close();
			ois.close();
			fis.close();
			
			socket.close();
			
		} catch (IOException e) {
			return Error.UNKNOWN_ERROR;
		}
		
		return Error.SUCCESS;
	}
	
	public Error post(MyUser user) {
		if(socket == null)
			return Error.LOGIN;
		
		return Error.SUCCESS;
	}

	public Error viewPost(MyUser user) {
		if(socket == null)
			return Error.LOGIN;
		
		return Error.SUCCESS;
	}
	
	public Error viewProfile(MyUser user) {
		if(socket == null)
			return Error.LOGIN;
		
		return Error.SUCCESS;
	}
	
	public Error editProfile(MyUser user) {
		if(socket == null)
			return Error.LOGIN;
		
		return Error.SUCCESS;
	}
	
	public Error delete(MyUser user) {
		if(socket == null)
			return Error.LOGIN;
		
		return Error.SUCCESS;
	}

	public Error createUser(MyUser user) {
		if(socket == null)
			return Error.LOGIN;
		
		
		return Error.SUCCESS;
	}
	
	public Error changePassword(MyUser user) {
		if(socket == null)
			return Error.LOGIN;
		
		return Error.SUCCESS;
	}
	
	public static void main(String args[]) throws IOException {
		
		int p = 4444;
		InetAddress addr = InetAddress.getLocalHost();
		
		Socket sock = new Socket(addr, p);
		
		
	}
}
