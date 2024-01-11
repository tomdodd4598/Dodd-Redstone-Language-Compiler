package drlc;

import java.util.*;

public class Hierarchy<K, V> {
	
	protected final Map<K, V> internal = new LinkedHashMap<>();
	protected final Hierarchy<K, V> parent;
	
	public Hierarchy(Hierarchy<K, V> parent) {
		this.parent = parent;
	}
	
	public V put(K key, V value, boolean shadow) {
		if (shadow || parent == null || internal.containsKey(key)) {
			return internal.put(key, value);
		}
		else {
			return parent.put(key, value, shadow);
		}
	}
	
	public V get(K key, boolean shallow) {
		if (internal.containsKey(key)) {
			return internal.get(key);
		}
		return shallow || parent == null ? null : parent.get(key, false);
	}
	
	public V remove(K key, boolean shallow) {
		V remove;
		if ((remove = internal.remove(key)) != null) {
			return remove;
		}
		return shallow || parent == null ? null : parent.remove(key, false);
	}
	
	public boolean remove(K key, V value, boolean shallow) {
		if (internal.remove(key, value)) {
			return true;
		}
		return shallow || parent == null ? false : parent.remove(key, value, false);
	}
	
	public boolean containsKey(K key, boolean shallow) {
		if (internal.containsKey(key)) {
			return true;
		}
		return shallow || parent == null ? false : parent.containsKey(key, false);
	}
	
	public boolean containsValue(K key, boolean shallow) {
		if (internal.containsKey(key)) {
			return true;
		}
		return shallow || parent == null ? false : parent.containsKey(key, false);
	}
}
