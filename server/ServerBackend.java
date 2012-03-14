package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.*;
import java.security.spec.*;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.*;
import javax.crypto.spec.*;

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

	public static char[] password = null;
	private static String garbled = "daskjfjladsjfkldjaslkjonanocnaskld98973q2tg\n";
	
	private static int IV_LENGTH = 16;
	
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
	
	/* Design for following:
	 *   - Locks should not be used in write/readSecure() [Should be somewhere in call stack]
	 *   - writeSecure:
	 *   	- Use secure random number generate to get an extra number of lines
	 *   		- Array of 1-byte -> unsigned int from 0 to 256
	 *   	- Use symmetric key generated from hashed password
	 *   	- Question that remains is how to receive lines to be written
	 *   - readSecure:
	 *   	- Read first line to get number of extra lines
	 *   	- Retrieving the other lines is undetermined
	 *   		- Will probably return an ArrayList of valid lines as strings
	 */
	public static byte[] writeSecure(String fileContents){
		try {
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			byte bytes[] = new byte[1];
			random.nextBytes(bytes);
			
			int num_padding_lines = Math.abs((int)(bytes[0]) % 256);
			int i = 0;
			
			for(i = 0; i < num_padding_lines; i++){
				fileContents = garbled + fileContents;
			}
			
			fileContents = Integer.toString(num_padding_lines) + "\n" + fileContents;
			
			// With a little help from online source (stackoverflow),
			// crafted the following
			
			byte[] salt = new byte[8];// We set a salt on our own
			for(i = 0; i < 8; i++){salt[i] = 0;}
			
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec(password, salt, 65536, 128);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.ENCRYPT_MODE, secret);
			
			AlgorithmParameters params = c.getParameters();
			byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
			
			byte[] ciphertext = c.doFinal(fileContents.getBytes("UTF-8"));
			
			byte[] encrypted = new byte[IV_LENGTH + ciphertext.length];
			
			for(i = 0; i < IV_LENGTH; i++){
				encrypted[i] = iv[i];
			}
			for(i = IV_LENGTH; i < IV_LENGTH + ciphertext.length; i++){
				encrypted[i] = ciphertext[i - IV_LENGTH];
			}
			
			return encrypted;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static ArrayList<String> readSecure(byte[] encrypted){
		try {
			int i = 0;
			byte[] salt = new byte[8];// We set a salt on our own
			for(i = 0; i < 8; i++){salt[i] = 0;}
			
			byte[] iv = new byte[IV_LENGTH];
			for(i = 0; i < IV_LENGTH; i++){
				iv[i] = encrypted[i];
			}
			
			byte[] ciphertext = new byte[encrypted.length - IV_LENGTH];
			for(i = IV_LENGTH; i < encrypted.length; i++){
				ciphertext[i - IV_LENGTH] = encrypted[i];
			}
			
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec spec = new PBEKeySpec(password, salt, 65536, 128);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
			
			String plaintext = new String(c.doFinal(ciphertext), "UTF-8");
			
			String[] plaintext_lines = plaintext.split("\n");
			ArrayList<String> lines = new ArrayList<String>();
			
			int num_garbage = Integer.parseInt(plaintext_lines[0]);
			
			for(i = num_garbage + 1; i < plaintext_lines.length; i++){
				lines.add(plaintext_lines[i]);
			}
			
			return lines;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
