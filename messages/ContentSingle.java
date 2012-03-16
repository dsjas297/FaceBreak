/**
 * @author gd226
 * 
 * 
 */

package messages;

@SuppressWarnings("serial")
public class ContentSingle<T> extends GenericContent {
	private T t;
	
	public ContentSingle() {
		super();
	}

	public T get() {
		return t;
	}

	public void set(T t) {
		this.t = t;
	}
	
}
