package drlc;

import java.util.*;

import org.eclipse.jdt.annotation.Nullable;

public class HierarchyMap<K, V> {
	
	protected final Map<K, V> internal = new HashMap<>();
	protected final HierarchyMap<K, V> prev;
	
	public HierarchyMap(HierarchyMap<K, V> prev) {
		this.prev = prev;
	}
	
	public @Nullable V put(K key, V value, boolean shadow) {
		if (shadow || prev == null || internal.containsKey(key)) {
			return internal.put(key, value);
		}
		else {
			return prev.put(key, value, shadow);
		}
	}
	
	public @Nullable V get(K key) {
		if (internal.containsKey(key)) {
			return internal.get(key);
		}
		return prev == null ? null : prev.get(key);
	}
	
	public boolean containsKey(K key) {
		if (internal.containsKey(key)) {
			return true;
		}
		return prev == null ? false : prev.containsKey(key);
	}
}
