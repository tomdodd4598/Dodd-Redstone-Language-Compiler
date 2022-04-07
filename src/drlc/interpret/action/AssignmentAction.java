package drlc.interpret.action;

import java.util.Map;

import drlc.Helpers;
import drlc.interpret.component.DataId;
import drlc.node.Node;

public class AssignmentAction extends Action implements IValueAction {
	
	public final DataId target, arg;
	
	public AssignmentAction(Node node, DataId target, DataId arg) {
		super(node);
		if (target == null) {
			throw new IllegalArgumentException(String.format("Assignment action target was null! %s", node));
		}
		else {
			this.target = target;
		}
		if (arg == null) {
			throw new IllegalArgumentException(String.format("Assignment action argument was null! %s", node));
		}
		else {
			this.arg = arg;
		}
	}
	
	@Override
	public DataId[] lvalues() {
		return new DataId[] {target};
	}
	
	@Override
	public DataId[] rvalues() {
		return new DataId[] {arg};
	}
	
	@Override
	public boolean canRemove() {
		return true;
	}
	
	@Override
	public boolean canReplaceRvalue() {
		return true;
	}
	
	@Override
	public DataId getRvalueReplacer() {
		return arg;
	}
	
	@Override
	public Action replaceRvalue(DataId replaceTarget, DataId rvalueReplacer) {
		return new AssignmentAction(null, target, rvalueReplacer);
	}
	
	@Override
	public boolean canReplaceLvalue() {
		return true;
	}
	
	@Override
	public DataId getLvalueReplacer() {
		return target;
	}
	
	@Override
	public Action replaceLvalue(DataId replaceTarget, DataId lvalueReplacer) {
		return new AssignmentAction(null, lvalueReplacer, arg);
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
		DataId target = this.target.removeAllDereferences(), arg = this.arg.removeAllDereferences();
		if (Helpers.isRegId(target.raw) && regIdMap.containsKey(target)) {
			target = regIdMap.get(target);
		}
		if (Helpers.isRegId(arg.raw) && regIdMap.containsKey(arg)) {
			arg = regIdMap.get(arg);
		}
		
		if (!target.equalsOther(this.target, true) || !arg.equalsOther(this.arg, true)) {
			return new AssignmentAction(null, target.addDereferences(this.target.dereferenceLevel), arg.addDereferences(this.arg.dereferenceLevel));
		}
		else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return target.raw.concat(" = ").concat(arg.raw);
	}
}
