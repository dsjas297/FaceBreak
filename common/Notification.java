package common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.concurrent.locks.ReentrantLock;

import server.FileSystem;

public class Notification implements Serializable {

	private static final long serialVersionUID = 6613012381243813845L;
	private int nid;
	private NotificationType type;
	private String username;
	private int newRank;
	
	private static final String notificationIDFile = FileSystem.notificationIDFile;
	
	private static int getNewNotifID(){
		try{
			if(FileSystem.lockMap.get(notificationIDFile) == null){
				FileSystem.lockMap.put(notificationIDFile, new ReentrantLock());
			}
			FileSystem.lockMap.get(notificationIDFile).lock();
			
			FileReader fReader = new FileReader(notificationIDFile);
			BufferedReader inputReader = new BufferedReader(fReader);
			String temp = inputReader.readLine();
			int id = Integer.parseInt(temp);
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(notificationIDFile, false));
			bWriter.write(Integer.toString(id + 1));
			bWriter.close();
			
			FileSystem.lockMap.get(notificationIDFile).unlock();
			
			return id;
		} catch(Exception e){
			System.err.println("Error: " + e.getMessage());
			return 0;
		}
	}
	
	public Notification(NotificationType type) {
		this.type = type;
		this.nid = getNewNotifID();
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setRank(int newRank) {
		this.newRank = newRank;
	}
	
	public void setId(int nid) {
		this.nid = nid;
	}
	
	public String getUsername() {
		return username;
	}
	
	public NotificationType getType() {
		return type;
	}
	
	/*
	 * boss checks that getNewRank > Title.BOSS.rank (only one boss)
	 */
	public int getNewRank() {
		return newRank;
	}
	
	public int getId() {
		return nid;
	}
	
	@Override
	public String toString() {
		String stringRep = Integer.toString(nid) + " " +
							type.toString() + " " +
							username;
		if(type == NotificationType.CHANGE_RANK) 
			stringRep += " " + Integer.toString(newRank);
		
		return stringRep + "\n";
	}
	
	public enum NotificationType {
		NEW_FRIEND,
		CHANGE_RANK;
	}
}
