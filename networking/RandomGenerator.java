/**
 * Monitor class for Secure Random Number Generator
 */

package networking;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class RandomGenerator {

	private static SecureRandom rand;
	
	protected static void init() {
		try {
			rand = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Cannot create secure random number generator!");
			System.exit(-1);
		}
	}
	
	protected static synchronized int getInteger() {
		return rand.nextInt();
	}
	
	protected static synchronized long getLong() {
		return rand.nextLong();
	}
	
	protected static synchronized byte[] getByteArray(int numBytes) {
		return rand.generateSeed(numBytes);
	}
}
