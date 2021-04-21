package drlc.interpret.action;

import java.util.Map;

import drlc.Global;
import drlc.Helper;
import drlc.node.Node;

public class InitialisationAction extends Action implements IValueAction {
	
	public final String target, arg;
	
	public InitialisationAction(Node node, String target, String arg) {
		super(node);
		if (target == null) {
			throw new IllegalArgumentException(String.format("Initialisation action target was null! %s", node));
		}
		else {
			this.target = target;
		}
		if (arg == null) {
			throw new IllegalArgumentException(String.format("Initialisation action argument was null! %s", node));
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
		return arg;
	}
	
	@Override
	public Action replaceRValue(String replaceTarget, String rValueReplacer) {
		return new InitialisationAction(null, target, rValueReplacer);
	}
	
	@Override
	public boolean canReplaceLValue() {
		return false;
	}
	
	@Override
	public String getLValueReplacer() {
		return target;
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
		String target = this.target, arg = this.arg;
		if (Helper.isRegId(target) && regIdMap.containsKey(target)) {
			target = regIdMap.get(target);
		}
		if (Helper.isRegId(arg) && regIdMap.containsKey(arg)) {
			arg = regIdMap.get(arg);
		}
		
		if (!target.equals(this.target) || !arg.equals(this.arg)) {
			return new InitialisationAction(null, target, arg);
		}
		else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return Global.VAR.concat(" ").concat(target).concat(" = ").concat(arg);
	}
}
