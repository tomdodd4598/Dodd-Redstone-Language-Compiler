package drlc;

import java.util.*;

public class HierarchySet<K> {
	
	protected final Set<K> internal = new HashSet<>();
	protected final HierarchySet<K> prev;
	
	public HierarchySet(HierarchySet<K> prev) {
		this.prev = prev;
	}
	
	public boolean add(K key, boolean shadow) {
		if (shadow || prev == null || internal.contains(key)) {
			return internal.add(key);
		}
		else {
			return prev.add(key, shadow);
		}
	}
	
	public boolean contains(K key) {
		if (internal.contains(key)) {
			return true;
		}
		return prev == null ? false : prev.contains(key);
	}
}
