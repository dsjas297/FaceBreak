package facebreak.common;

import java.awt.image.BufferedImage;
import java.io.Serializable;

public class SerializableAvatar implements Serializable {
	public int width;
	public int height;
	public int imageType;
	public int[] pixels;
	
	public SerializableAvatar() {
		
	}
	
	public SerializableAvatar(int width, int height, int imageType, int[] pixels) {
		this.width = width;
		this.height = height;
		this.imageType = imageType;
		this.pixels = pixels;
	}
	
	public static SerializableAvatar bufImageToSerialAvatar(BufferedImage bi) {
		SerializableAvatar sa = new SerializableAvatar();
		
		return sa;
	}
	
	public static BufferedImage serialAvatarToBufImage(SerializableAvatar sa) {
		BufferedImage bi = new BufferedImage(sa.width, sa.height, sa.imageType);
		
		return bi;
	}
}
