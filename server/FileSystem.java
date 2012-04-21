package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileSystem {
	protected static final String global_uid_counter_file = "uid_counter";
	protected static final String global_users_file = "users";
	protected static final String user_info_file = "info";
	protected static final String user_friends_file = "friends";
	protected static final String user_untrustworthy_file = "untrustworthy";
	protected static final String user_avatar_file = "avatar.jpg";

	protected static final String region_dir = "regions";
	protected static final String region_posts_file = "posts";
	protected static final String region_info_file = "regionInfo";

	public static boolean initDirTree() {
		File uidFile = new File(global_uid_counter_file);
		File usersFile = new File(global_users_file);

		if (!usersFile.exists()) {
			try {
				BufferedWriter bWriter = new BufferedWriter(new FileWriter(
						global_users_file, true));
				bWriter.write("");
				bWriter.close();
			} catch (IOException e) {
				System.err
						.println("OH NOES!! Cannot initialize file for users.");
				e.printStackTrace();
				return false;
			}
		}

		if (!uidFile.exists()) {
			try {
				BufferedWriter bWriter = new BufferedWriter(new FileWriter(
						global_uid_counter_file, true));
				bWriter.write("1\n");
				bWriter.close();
			} catch (IOException ioe) {
				System.err
						.println("OH NOES!! Cannot initialize file for user ID counter.");
				ioe.printStackTrace();
				return false;
			}
		}
		return true;
	} 
	
	/*
	 * delete file system
	 */
	public static void cleanup() {
		File uidFile = new File(global_uid_counter_file);
		File usersFile = new File(global_users_file);
		
		uidFile.delete();
		usersFile.delete();
	}
}
