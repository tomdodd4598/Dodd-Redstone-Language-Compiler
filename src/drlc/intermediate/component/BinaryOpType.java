package drlc.intermediate.component;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Helpers;

public enum BinaryOpType {
	
	// LOGICAL_AND("&?"),
	// LOGICAL_OR("|?"),
	
	EQUAL_TO("=="),
	NOT_EQUAL_TO("!="),
	
	LESS_THAN("<"),
	LESS_OR_EQUAL("<="),
	MORE_THAN(">"),
	MORE_OR_EQUAL(">="),
	
	PLUS("+"),
	AND("&"),
	OR("|"),
	XOR("^"),
	MINUS("-"),
	
	LEFT_SHIFT("<<"),
	RIGHT_SHIFT(">>"),
	LEFT_ROTATE("<<<"),
	RIGHT_ROTATE(">>>"),
	
	MULTIPLY("*"),
	DIVIDE("/"),
	REMAINDER("%");
	
	public static final Map<String, BinaryOpType> NAME_MAP = new HashMap<>();
	
	static {
		for (BinaryOpType opType : BinaryOpType.values()) {
			NAME_MAP.put(opType.str, opType);
		}
	}
	
	public static @NonNull BinaryOpType get(String str) {
		BinaryOpType opType = NAME_MAP.get(str);
		if (opType == null) {
			throw Helpers.nodeError(null, "Binary operator \"%s\" not defined!", str);
		}
		else {
			return opType;
		}
	}
	
	private final String str;
	
	private BinaryOpType(String str) {
		this.str = str;
	}
	
	public boolean isShift() {
		switch (this) {
			case LEFT_SHIFT:
			case RIGHT_SHIFT:
			case LEFT_ROTATE:
			case RIGHT_ROTATE:
				return true;
			default:
				return false;
		}
	}
	
	@Override
	public String toString() {
		return str;
	}
}
