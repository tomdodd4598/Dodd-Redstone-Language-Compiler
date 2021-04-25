package drlc.interpret.action;

import java.util.Map;

import drlc.*;
import drlc.node.Node;

public class ReturnValueAction extends Action implements IStopAction, IValueAction {
	
	public final String arg;
	
	public ReturnValueAction(Node node, String arg) {
		super(node);
		if (arg == null) {
			throw new IllegalArgumentException(String.format("Return action argument was null! %s", node));
		}
		else {
			this.arg = arg;
		}
	}
	
	@Override
	public String[] lValues() {
		return new String[] {};
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
		return new ReturnValueAction(null, rValueReplacer);
	}
	
	@Override
	public boolean canReplaceLValue() {
		return false;
	}
	
	@Override
	public String getLValueReplacer() {
		return null;
	}
	
	@Override
	public Action replaceLValue(String replaceTarget, String lValueReplacer) {
		return null;
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
		String arg = this.arg;
		if (Helper.isRegId(arg) && regIdMap.containsKey(arg)) {
			arg = regIdMap.get(arg);
		}
		
		if (!arg.equals(this.arg)) {
			return new ReturnValueAction(null, arg);
		}
		else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return Global.RETURN.concat(" ").concat(arg);
	}
}
