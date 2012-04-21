/**
 * @author gd226
 * 
 * 
 */

package messages;

import java.io.Serializable;

public class Item<T> implements Serializable {

	private static final long serialVersionUID = -1615300875166801724L;
	private T t;

	public T get() {
		return t;
	}

	public void set(T t) {
		this.t = t;
	}
	
}
