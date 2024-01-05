package drlc;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.*;

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
	
	public <T> Iterable<T> iterable(Function<? super Map<K, V>, ? extends Iterable<T>> mapFunction, BiFunction<? super Hierarchy<K, V>, ? super Boolean, ? extends Iterable<T>> hierarchyFunction, boolean shallow) {
		return () -> new Iterator<T>() {
			
			boolean start = true;
			Iterator<T> current = mapFunction.apply(internal).iterator();
			
			@Override
			public boolean hasNext() {
				if (start && !current.hasNext()) {
					if (shallow || parent == null) {
						return false;
					}
					start = false;
					current = hierarchyFunction.apply(parent, false).iterator();
				}
				return current.hasNext();
			}
			
			@Override
			public T next() {
				return current.next();
			}
		};
	}
	
	@SuppressWarnings("null")
	public Iterable<Entry<K, V>> entryIterable(boolean shallow) {
		return iterable(Map::entrySet, Hierarchy::entryIterable, shallow);
	}
	
	@SuppressWarnings("null")
	public Iterable<V> valueIterable(boolean shallow) {
		return iterable(Map::values, Hierarchy::valueIterable, shallow);
	}
	
	public void forEachEntry(BiConsumer<? super K, ? super V> consumer, boolean shallow) {
		internal.forEach(consumer);
		if (!shallow && parent != null) {
			parent.forEachEntry(consumer, false);
		}
	}
	
	public void forEachValue(Consumer<? super V> consumer, boolean shallow) {
		internal.values().forEach(consumer);
		if (!shallow && parent != null) {
			parent.forEachValue(consumer, false);
		}
	}
}
