package drlc.interpret.action;

import java.util.Map;

import drlc.*;
import drlc.node.Node;

public class DeclarationAction extends Action implements IValueAction {
	
	public final String target;
	
	public DeclarationAction(Node node, String target) {
		super(node);
		if (target == null) {
			throw new IllegalArgumentException(String.format("Initialisation action target was null! %s", node));
		}
		else {
			this.target = target;
		}
	}
	
	@Override
	public String[] lValues() {
		return new String[] {target};
	}
	
	@Override
	public String[] rValues() {
		return new String[] {};
	}
	
	@Override
	public boolean canRemove() {
		return false;
	}
	
	@Override
	public boolean canReplaceRValue() {
		return false;
	}
	
	@Override
	public String getRValueReplacer() {
		return null;
	}
	
	@Override
	public Action replaceRValue(String replaceTarget, String rValueReplacer) {
		return null;
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
		String target = this.target;
		if (Helper.isRegId(target) && regIdMap.containsKey(target)) {
			target = regIdMap.get(target);
		}
		
		if (!target.equals(this.target)) {
			return new DeclarationAction(null, target);
		}
		else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return Global.VAR.concat(" ").concat(target);
	}
}
