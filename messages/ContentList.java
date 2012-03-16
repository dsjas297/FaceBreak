/**
 * @author gd226
 * 
 * 
 */

package messages;

import java.lang.reflect.Array;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class ContentList<T> extends GenericContent {
	private T[] array;
	
	public ContentList() {
		super();
	}
	
	@SuppressWarnings("unchecked")
	public void setArray(ArrayList<T> list, Class<T> clazz) {
		int len = list.size();
		array = (T[]) Array.newInstance(clazz, len);
		for(int i = 0; i < len; i++) {
			array[i] = list.get(i);
		}
	}
	
	public T[] getArray() {
		return array;
	}
	
	public int getSize() {
		return array.length;
	}
}
