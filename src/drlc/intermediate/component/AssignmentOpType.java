package drlc.intermediate.component;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.Helpers;

public enum AssignmentOpType {
	
	EQUALS("=", null),
	
	PLUS_EQUALS("+=", BinaryOpType.PLUS),
	AND_EQUALS("&=", BinaryOpType.AND),
	OR_EQUALS("|=", BinaryOpType.OR),
	XOR_EQUALS("^=", BinaryOpType.XOR),
	MINUS_EQUALS("-=", BinaryOpType.MINUS),
	
	MULTIPLY_EQUALS("*=", BinaryOpType.MULTIPLY),
	DIVIDE_EQUALS("/=", BinaryOpType.DIVIDE),
	REMAINDER_EQUALS("%=", BinaryOpType.REMAINDER),
	
	LEFT_SHIFT_EQUALS("<<=", BinaryOpType.LEFT_SHIFT),
	RIGHT_SHIFT_EQUALS(">>=", BinaryOpType.RIGHT_SHIFT),
	LEFT_ROTATE_EQUALS("<<<=", BinaryOpType.LEFT_ROTATE),
	RIGHT_ROTATE_EQUALS(">>>=", BinaryOpType.RIGHT_ROTATE);
	
	public static final Map<String, AssignmentOpType> NAME_MAP = new HashMap<>();
	
	static {
		for (AssignmentOpType opType : AssignmentOpType.values()) {
			NAME_MAP.put(opType.str, opType);
		}
	}
	
	public static @NonNull AssignmentOpType get(String str) {
		AssignmentOpType opType = NAME_MAP.get(str);
		if (opType == null) {
			throw Helpers.error("Assignment operator \"%s\" not defined!", str);
		}
		else {
			return opType;
		}
	}
	
	private final String str;
	public final @Nullable BinaryOpType binaryOpType;
	
	private AssignmentOpType(String str, @Nullable BinaryOpType binaryOpType) {
		this.str = str;
		this.binaryOpType = binaryOpType;
	}
	
	@Override
	public String toString() {
		return str;
	}
}
