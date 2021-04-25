package drlc.interpret.action;

import java.util.*;

import drlc.Helper;
import drlc.node.Node;

public class UnaryOpAction extends Action implements IValueAction {
	
	public final String target, arg;
	public final UnaryOpType opType;
	
	public UnaryOpAction(Node node, String target, UnaryOpType opType, String arg) {
		this(node, target, opType.toString(), arg);
	}
	
	public UnaryOpAction(Node node, String target, String operation, String arg) {
		super(node);
		if (target == null) {
			throw new IllegalArgumentException(String.format("Unary op action target was null! %s", node));
		}
		else {
			this.target = target;
		}
		
		if (operation == null) {
			throw new IllegalArgumentException(String.format("Unary op action operation type was null! %s", node));
		}
		else if (!OP_TYPE_MAP.containsKey(operation)) {
			throw new IllegalArgumentException(String.format("Unary op action operation type was not recognized! %s", node));
		}
		else {
			opType = OP_TYPE_MAP.get(operation);
		}
		
		if (arg == null) {
			throw new IllegalArgumentException(String.format("Unary op action argument was null! %s", node));
		}
		else {
			this.arg = arg;
		}
	}
	
	@Override
	public String[] lValues() {
		return new String[] {target};
	}
	
	@Override
	public String[] rValues() {
		return new String[] {arg};
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
		return new UnaryOpAction(null, target, opType, rValueReplacer);
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
		return new UnaryOpAction(null, lValueReplacer, opType, arg);
	}
	
	@Override
	public boolean canReorderRValues() {
		return false;
	}
	
	@Override
	public Action swapRValues(int i, int j) {
		return null;
	}
	
	@Override
	public Action replaceRegIds(Map<String, String> regIdMap) {
		String target = this.target, arg = this.arg;
		if (Helper.isRegId(target) && regIdMap.containsKey(target)) {
			target = regIdMap.get(target);
		}
		if (Helper.isRegId(arg) && regIdMap.containsKey(arg)) {
			arg = regIdMap.get(arg);
		}
		
		if (!target.equals(this.target) || !arg.equals(this.arg)) {
			return new UnaryOpAction(null, target, opType, arg);
		}
		else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return target.concat(" = ").concat(opType.toString()).concat(arg);
	}
	
	// Operation Types
	
	static final Map<String, UnaryOpType> OP_TYPE_MAP = new HashMap<>();
	
	static {
		OP_TYPE_MAP.put("+", UnaryOpType.PLUS);
		OP_TYPE_MAP.put("-", UnaryOpType.MINUS);
		OP_TYPE_MAP.put("~", UnaryOpType.COMPLEMENT);
		OP_TYPE_MAP.put("?", UnaryOpType.TO_BOOL);
		OP_TYPE_MAP.put("!", UnaryOpType.NOT);
	}
	
	public static enum UnaryOpType {
		PLUS("+"),
		MINUS("-"),
		COMPLEMENT("~"),
		TO_BOOL("?"),
		NOT("!");
		
		private final String string;
		
		private UnaryOpType(String string) {
			this.string = string;
		}
		
		@Override
		public String toString() {
			return string;
		}
	}
}
