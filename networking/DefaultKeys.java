package networking;

public class DefaultKeys {
	private static final int NUM_BYTES = 128;
	private static final byte[] DEFAULT_PUBLIC = new byte[NUM_BYTES];
	private static final byte[] DEFAULT_PRIVATE = new byte[NUM_BYTES];
	
	public static void main(String[] args) {
		RandomGenerator.init();
		byte[] array = RandomGenerator.getByteArray(128);
		System.out.println(array);
		
		for(int i = 0; i < array.length; i++)
			System.out.println(array[i]);
	}
}
