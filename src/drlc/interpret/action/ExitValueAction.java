package drlc.interpret.action;

import java.util.Map;

import drlc.*;
import drlc.interpret.component.DataId;
import drlc.node.Node;

public class ExitValueAction extends Action implements IDefiniteRedirectAction, IValueAction {
	
	public final DataId arg;
	
	public ExitValueAction(Node node, DataId arg) {
		super(node);
		if (arg == null) {
			throw new IllegalArgumentException(String.format("Exit value action argument was null! %s", node));
		}
		else {
			this.arg = arg;
		}
	}
	
	@Override
	public DataId[] lvalues() {
		return new DataId[] {};
	}
	
	@Override
	public DataId[] rvalues() {
		return new DataId[] {arg};
	}
	
	@Override
	public boolean canRemove() {
		return false;
	}
	
	@Override
	public boolean canReplaceRvalue() {
		return true;
	}
	
	@Override
	public DataId getRvalueReplacer() {
		return null;
	}
	
	@Override
	public Action replaceRvalue(DataId replaceTarget, DataId rvalueReplacer) {
		return new ExitValueAction(null, rvalueReplacer);
	}
	
	@Override
	public boolean canReplaceLvalue() {
		return false;
	}
	
	@Override
	public DataId getLvalueReplacer() {
		return null;
	}
	
	@Override
	public Action replaceLvalue(DataId replaceTarget, DataId lvalueReplacer) {
		return null;
	}
	
	@Override
	public boolean canReorderRvalues() {
		return false;
	}
	
	@Override
	public Action swapRvalues(int i, int j) {
		return null;
	}
	
	@Override
	public Action replaceRegIds(Map<DataId, DataId> regIdMap) {
		DataId arg = this.arg.removeAllDereferences();
		if (Helpers.isRegId(arg.raw) && regIdMap.containsKey(arg)) {
			arg = regIdMap.get(arg);
		}
		
		if (!arg.equalsOther(this.arg, true)) {
			return new ExitValueAction(null, arg.addDereferences(this.arg.dereferenceLevel));
		}
		else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return Global.EXIT.concat(" ").concat(arg.raw);
	}
}
