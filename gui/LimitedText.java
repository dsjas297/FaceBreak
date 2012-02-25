package facebreak.gui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class LimitedText extends PlainDocument{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int length;

	public LimitedText(int l){
		length = l;
	}

	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {

		// insert the text
		super.insertString(offs, str, a);

		// truncate result
		String newResult = getText(0, getLength());
		if (newResult.length() > length) {
			newResult = newResult.substring(0, length);
			super.remove(0, getLength());
			super.insertString(0,newResult,a);
		}

	}

}