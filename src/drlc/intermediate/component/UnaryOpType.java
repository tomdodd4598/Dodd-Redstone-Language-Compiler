package drlc.intermediate.component;

import java.util.*;

public enum UnaryOpType {
	
	MINUS("-"),
	NOT("!");
	
	public static final Map<String, UnaryOpType> NAME_MAP = new HashMap<>();
	
	static {
		for (UnaryOpType opType : UnaryOpType.values()) {
			NAME_MAP.put(opType.str, opType);
		}
	}
	
	public static UnaryOpType getOpType(String str) {
		return NAME_MAP.get(str);
	}
	
	private final String str;
	
	private UnaryOpType(String str) {
		this.str = str;
	}
	
	@Override
	public String toString() {
		return str;
	}
}
