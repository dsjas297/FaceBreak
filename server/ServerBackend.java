package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import common.Post;
import common.Region;

public class ServerBackend {

	// Folder names
	public static final String globalUidCounter = FBFile.GLOBAL_UID.name;
	public static final String globalUsers = FBFile.GLOBAL_ALLUSERS.name;
	public static final String userInfoFile = "info";
	public static final String userFriendsFile = "friends";
	public static final String userUntrustworthyFile = "untrustworthy";
	public static final String imageFile = "avatar.jpg";

	public static final String regionsFolder = "regions";
	public static final String regionPostsFile = "posts";
	public static final String regionInfoFile = "regionInfo";

	public static void initDirTree() {
		File uidFile = new File(globalUidCounter);
		File usersFile = new File(globalUsers);

		if (!usersFile.exists()) {
			try {
				BufferedWriter bWriter = new BufferedWriter(new FileWriter(
						globalUsers, true));
				bWriter.write("");
				bWriter.close();
			} catch (IOException e) {
				System.err
						.println("OH NOES!! Cannot initialize file for users.");
				e.printStackTrace();
			}
		}

		if (!uidFile.exists()) {
			try {
				BufferedWriter bWriter = new BufferedWriter(new FileWriter(
						globalUidCounter, true));
				bWriter.write("1\n");
				bWriter.close();
			} catch (IOException ioe) {
				System.err
						.println("OH NOES!! Cannot initialize file for user ID counter.");
				ioe.printStackTrace();
			}
		}
	}
}
