package drlc.intermediate.component;

import java.util.*;

import drlc.*;

public class Path {
	
	public final List<String> prefix;
	public final String name;
	
	public final List<String> segments;
	
	public Path(List<String> prefix, String name) {
		this.prefix = prefix;
		this.name = name;
		
		segments = new ArrayList<>(prefix);
		segments.add(name);
	}
	
	public Path(List<String> segments) {
		int size = segments.size();
		this.prefix = new ArrayList<>(segments.subList(0, size - 1));
		this.name = segments.get(size - 1);
		
		this.segments = segments;
	}
	
	@Override
	public String toString() {
		return Helpers.collectionString(segments, Global.PATH_SEPARATOR, "", "");
	}
}
