package drlc.intermediate.component;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Helpers;

public enum UnaryOpType {
	
	MINUS("-"),
	NOT("!");
	
	public static final Map<String, UnaryOpType> NAME_MAP = new HashMap<>();
	
	static {
		for (UnaryOpType opType : UnaryOpType.values()) {
			NAME_MAP.put(opType.str, opType);
		}
	}
	
	public static @NonNull UnaryOpType get(String str) {
		UnaryOpType opType = NAME_MAP.get(str);
		if (opType == null) {
			throw Helpers.nodeError(null, "Unary operator \"%s\" not defined!", str);
		}
		else {
			return opType;
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
