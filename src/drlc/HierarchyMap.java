package drlc;

import java.util.*;

public class HierarchyMap<K, V> {
	
	protected final Map<K, V> internal;
	protected final HierarchyMap<K, V> parent;
	
	public HierarchyMap(HierarchyMap<K, V> parent) {
		this(parent, new HashMap<>());
	}
	
	public HierarchyMap(HierarchyMap<K, V> parent, Map<K, V> internal) {
		this.parent = parent;
		this.internal = internal;
	}
	
	public V put(K key, V value, boolean shadow) {
		if (shadow || parent == null || internal.containsKey(key)) {
			return internal.put(key, value);
		}
		else {
			return parent.put(key, value, shadow);
		}
	}
	
	public V get(K key) {
		if (internal.containsKey(key)) {
			return internal.get(key);
		}
		return parent == null ? null : parent.get(key);
	}
	
	public boolean containsKey(K key) {
		if (internal.containsKey(key)) {
			return true;
		}
		return parent == null ? false : parent.containsKey(key);
	}
}
