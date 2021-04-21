package drlc.interpret.action;

import java.util.Map;

import drlc.Global;
import drlc.Helper;
import drlc.node.Node;

public class DereferenceAction extends Action implements IValueAction {
	
	public final String target, arg;
	public final int dereferenceLevel;
	
	public DereferenceAction(Node node, String target, int dereferenceLevel, String arg) {
		super(node);
		if (target == null) {
			throw new IllegalArgumentException(String.format("Dereference action target was null! %s", node));
		}
		else {
			this.target = target;
		}
		
		if (dereferenceLevel <= 0) {
			throw new IllegalArgumentException(String.format("Dereference action dereference level was non-positive! %s", node));
		}
		else {
			this.dereferenceLevel = dereferenceLevel;
		}
		
		if (arg == null) {
			throw new IllegalArgumentException(String.format("Dereference action argument was null! %s", node));
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
		return new DereferenceAction(null, target, dereferenceLevel, rValueReplacer);
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
		return new DereferenceAction(null, lValueReplacer, dereferenceLevel, arg);
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
			return new DereferenceAction(null, target, dereferenceLevel, arg);
		}
		else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return target.concat(" = ").concat(Helper.charLine(Global.DEREFERENCE, dereferenceLevel)).concat(arg);
	}
}
