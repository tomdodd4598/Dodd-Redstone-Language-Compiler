package drlc.interpret.component;

import java.util.*;

public enum BinaryOpType {
	
	LOGICAL_AND("&?", "&?"),
	LOGICAL_OR("|?", "|?"),
	LOGICAL_XOR("^?", "^?"),
	
	EQUAL_TO("==", "=="),
	NOT_EQUAL_TO("!=", "!="),
	
	LESS_THAN("<", ">"),
	LESS_OR_EQUAL("<=", ">="),
	MORE_THAN(">", "<"),
	MORE_OR_EQUAL(">=", "<="),
	
	PLUS("+", "+"),
	AND("&", "&"),
	OR("|", "|"),
	XOR("^", "^"),
	MINUS("-", null),
	
	ARITHMETIC_LEFT_SHIFT("<<", null),
	ARITHMETIC_RIGHT_SHIFT(">>", null),
	LOGICAL_RIGHT_SHIFT(">>>", null),
	CIRCULAR_LEFT_SHIFT("<</", null),
	CIRCULAR_RIGHT_SHIFT(">>/", null),
	
	MULTIPLY("*", "*"),
	DIVIDE("/", null),
	REMAINDER("%", null);
	
	public static final Map<String, BinaryOpType> NAME_MAP = new HashMap<>();
	public static final Map<BinaryOpType, BinaryOpType> COMMUTATION_MAP = new HashMap<>();
	
	static {
		for (BinaryOpType opType : BinaryOpType.values()) {
			NAME_MAP.put(opType.str, opType);
		}
		
		for (BinaryOpType opType : BinaryOpType.values()) {
			COMMUTATION_MAP.put(opType, NAME_MAP.get(opType.commutated));
		}
	}
	
	private final String str;
	private final String commutated;
	
	private BinaryOpType(String str, String commutated) {
		this.str = str;
		this.commutated = commutated;
	}
	
	@Override
	public String toString() {
		return str;
	}
}
