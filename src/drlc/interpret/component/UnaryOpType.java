package drlc.interpret.component;

import java.util.*;

public enum UnaryOpType {
	
	PLUS("+"),
	MINUS("-"),
	COMPLEMENT("~"),
	TO_BOOL("?"),
	NOT("!");
	
	public static final Map<String, UnaryOpType> NAME_MAP = new HashMap<>();
	
	static {
		for (UnaryOpType opType : UnaryOpType.values()) {
			NAME_MAP.put(opType.str, opType);
		}
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
