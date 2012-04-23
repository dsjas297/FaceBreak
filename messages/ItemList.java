/**
 * @author gd226
 * 
 * 
 */

package messages;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class ItemList<T> implements Serializable {

	private static final long serialVersionUID = -2422372207778533437L;
	private T[] array;

	@SuppressWarnings("unchecked")
	public void setArray(ArrayList<T> list, Class<T> clazz) {
		if(list == null) {
			return;
		}
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
		return (array == null) ? 0 : array.length;
	}
}
