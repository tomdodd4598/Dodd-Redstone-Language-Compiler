package drlc.interpret.action;

import java.util.HashMap;
import java.util.Map;

import drlc.Helper;
import drlc.node.Node;

public class BinaryOpAction extends Action implements IValueAction {
	
	public final String target, arg1, arg2;
	public final BinaryOpType opType;
	
	public BinaryOpAction(Node node, String target, String arg1, BinaryOpType opType, String arg2) {
		this(node, target, arg1, opType.toString(), arg2);
	}
	
	public BinaryOpAction(Node node, String target, String arg1, String operation, String arg2) {
		super(node);
		if (target == null) {
			throw new IllegalArgumentException(String.format("Binary op action target was null! %s", node));
		}
		else {
			this.target = target;
		}
		
		if (arg1 == null) {
			throw new IllegalArgumentException(String.format("Binary op action first argument was null! %s", node));
		}
		else {
			this.arg1 = arg1;
		}
		
		if (operation == null) {
			throw new IllegalArgumentException(String.format("Binary op action operation type was null! %s", node));
		}
		else if (!OP_TYPE_MAP.containsKey(operation)) {
			throw new IllegalArgumentException(String.format("Binary op action operation type was not recognized! %s", node));
		}
		else {
			opType = OP_TYPE_MAP.get(operation);
		}
		
		if (arg2 == null) {
			throw new IllegalArgumentException(String.format("Binary op action second argument was null! %s", node));
		}
		else {
			this.arg2 = arg2;
		}
	}
	
	@Override
	public String[] lValues() {
		return new String[] {target};
	}
	
	@Override
	public String[] rValues() {
		return new String[] {arg1, arg2};
	}
	
	@Override
	public boolean canRemove() {
		return false;
	}
	
	@Override
	public boolean canReplaceRValue() {
		return true;
	}
	
	@Override
	public String getRValueReplacer() {
		return null;
	}
	
	@Override
	public Action replaceRValue(String replaceTarget, String rValueReplacer) {
		if (arg1.equals(replaceTarget)) {
			return new BinaryOpAction(null, target, rValueReplacer, opType, arg2);
		}
		else if (arg2.equals(replaceTarget)) {
			return new BinaryOpAction(null, target, arg1, opType, rValueReplacer);
		}
		else {
			throw new IllegalArgumentException(String.format("Neither binary op action argument [%s, %s] matched replacement target %s!", arg1, arg2, replaceTarget));
		}
	}
	
	@Override
	public boolean canReplaceLValue() {
		return true;
	}
	
	@Override
	public String getLValueReplacer() {
		return null;
	}
	
	@Override
	public Action replaceLValue(String replaceTarget, String lValueReplacer) {
		if (target.equals(replaceTarget)) {
			return new BinaryOpAction(null, lValueReplacer, arg1, opType, arg2);
		}
		else {
			throw new IllegalArgumentException(String.format("Binary op action target %s doesn't match replacement target %s!", target, replaceTarget));
		}
	}
	
	@Override
	public boolean canReorderRValues() {
		return COMMUTATED_TYPE_MAP.get(opType) != null;
	}
	
	@Override
	public Action swapRValues(int i, int j) {
		if ((i == 0 && j == 1) || (i == 1 && j == 0)) {
			return new BinaryOpAction(null, target, arg2, COMMUTATED_TYPE_MAP.get(opType), arg1);
		}
		else {
			return null;
		}
	}
	
	@Override
	public Action replaceRegIds(Map<String, String> regIdMap) {
		String target = this.target, arg1 = this.arg1, arg2 = this.arg2;
		if (Helper.isRegId(target) && regIdMap.containsKey(target)) {
			target = regIdMap.get(target);
		}
		if (Helper.isRegId(arg1) && regIdMap.containsKey(arg1)) {
			arg1 = regIdMap.get(arg1);
		}
		if (Helper.isRegId(arg2) && regIdMap.containsKey(arg2)) {
			arg2 = regIdMap.get(arg2);
		}
		
		if (!target.equals(this.target) || !arg1.equals(this.arg1) || !arg2.equals(this.arg2)) {
			return new BinaryOpAction(null, target, arg1, opType, arg2);
		}
		else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return target.concat(" = ").concat(arg1).concat(" ").concat(opType.toString()).concat(" ").concat(arg2);
	}
	
	// Operation Types
	
	static final Map<String, BinaryOpType> OP_TYPE_MAP = new HashMap<>();
	static final Map<BinaryOpType, BinaryOpType> COMMUTATED_TYPE_MAP = new HashMap<>();
	
	static {
		OP_TYPE_MAP.put("+", BinaryOpType.PLUS);
		OP_TYPE_MAP.put("&", BinaryOpType.AND);
		OP_TYPE_MAP.put("|", BinaryOpType.OR);
		OP_TYPE_MAP.put("^", BinaryOpType.XOR);
		OP_TYPE_MAP.put("-", BinaryOpType.MINUS);
		OP_TYPE_MAP.put("<<", BinaryOpType.LEFT_SHIFT);
		OP_TYPE_MAP.put(">>", BinaryOpType.RIGHT_SHIFT);
		OP_TYPE_MAP.put("*", BinaryOpType.MULTIPLY);
		OP_TYPE_MAP.put("==", BinaryOpType.EQUAL_TO);
		OP_TYPE_MAP.put("/", BinaryOpType.DIVIDE);
		OP_TYPE_MAP.put("%", BinaryOpType.MODULO);
		OP_TYPE_MAP.put("!=", BinaryOpType.NOT_EQUAL_TO);
		OP_TYPE_MAP.put("<", BinaryOpType.LESS_THAN);
		OP_TYPE_MAP.put("<=", BinaryOpType.LESS_OR_EQUAL);
		OP_TYPE_MAP.put(">", BinaryOpType.MORE_THAN);
		OP_TYPE_MAP.put(">=", BinaryOpType.MORE_OR_EQUAL);
		
		COMMUTATED_TYPE_MAP.put(BinaryOpType.PLUS, BinaryOpType.PLUS);
		COMMUTATED_TYPE_MAP.put(BinaryOpType.AND, BinaryOpType.AND);
		COMMUTATED_TYPE_MAP.put(BinaryOpType.OR, BinaryOpType.OR);
		COMMUTATED_TYPE_MAP.put(BinaryOpType.XOR, BinaryOpType.XOR);
		COMMUTATED_TYPE_MAP.put(BinaryOpType.MINUS, null);
		COMMUTATED_TYPE_MAP.put(BinaryOpType.LEFT_SHIFT, null);
		COMMUTATED_TYPE_MAP.put(BinaryOpType.RIGHT_SHIFT, null);
		COMMUTATED_TYPE_MAP.put(BinaryOpType.MULTIPLY, BinaryOpType.MULTIPLY);
		COMMUTATED_TYPE_MAP.put(BinaryOpType.EQUAL_TO, BinaryOpType.EQUAL_TO);
		COMMUTATED_TYPE_MAP.put(BinaryOpType.DIVIDE, null);
		COMMUTATED_TYPE_MAP.put(BinaryOpType.MODULO, null);
		COMMUTATED_TYPE_MAP.put(BinaryOpType.NOT_EQUAL_TO, BinaryOpType.NOT_EQUAL_TO);
		COMMUTATED_TYPE_MAP.put(BinaryOpType.LESS_THAN, BinaryOpType.MORE_THAN);
		COMMUTATED_TYPE_MAP.put(BinaryOpType.LESS_OR_EQUAL, BinaryOpType.MORE_OR_EQUAL);
		COMMUTATED_TYPE_MAP.put(BinaryOpType.MORE_THAN, BinaryOpType.LESS_THAN);
		COMMUTATED_TYPE_MAP.put(BinaryOpType.MORE_OR_EQUAL, BinaryOpType.LESS_OR_EQUAL);
	}
	
	public static enum BinaryOpType {
		PLUS("+"),
		AND("&"),
		OR("|"),
		XOR("^"),
		MINUS("-"),
		LEFT_SHIFT("<<"),
		RIGHT_SHIFT(">>"),
		MULTIPLY("*"),
		EQUAL_TO("=="),
		DIVIDE("/"),
		MODULO("%"),
		NOT_EQUAL_TO("!="),
		LESS_THAN("<"),
		LESS_OR_EQUAL("<="),
		MORE_THAN(">"),
		MORE_OR_EQUAL(">=");
		
		private final String string;
		
		private BinaryOpType(String string) {
			this.string = string;
		}
		
		@Override
		public String toString() {
			return string;
		}
	}
}
