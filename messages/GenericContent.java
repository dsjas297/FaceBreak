/**
 * @author gd226
 * 
 * Contents of Message
 */

package messages;

import java.io.Serializable;

/*
 * Wrapper class
 */
@SuppressWarnings("serial")
public class GenericContent implements Serializable {
	private String padding;
	
	public GenericContent() {
		padding = null;
	}
	
	public void setPadding(int len) {
		padding = null;
	}
	
	public void stripPadding() {
		
	}
}
