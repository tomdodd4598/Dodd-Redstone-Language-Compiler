package drlc.intermediate.action.binary;

import java.util.Map;

import drlc.Helpers;
import drlc.intermediate.action.*;
import drlc.intermediate.component.DataId;
import drlc.node.Node;

public abstract class BinaryOpAction extends Action implements IValueAction {
	
	public final BinaryActionType type;
	public final DataId target, arg1, arg2;
	
	protected BinaryOpAction(Node node, BinaryActionType type, DataId target, DataId arg1, DataId arg2) {
		super(node);
		if (type == null) {
			throw new IllegalArgumentException(String.format("Binary op action type was null! %s", node));
		}
		else {
			this.type = type;
		}
		
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
		
		if (arg2 == null) {
			throw new IllegalArgumentException(String.format("Binary op action second argument was null! %s", node));
		}
		else {
			this.arg2 = arg2;
		}
	}
	
	protected abstract BinaryOpAction copy(DataId target, DataId arg1, DataId arg2);
	
	protected abstract BinaryOpAction commutated(DataId target, DataId arg1, DataId arg2);
	
	@Override
	public DataId[] lvalues() {
		return new DataId[] {target};
	}
	
	@Override
	public DataId[] rvalues() {
		return new DataId[] {arg1, arg2};
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
		if (arg1.equals(replaceTarget)) {
			return copy(target, rvalueReplacer, arg2);
		}
		else if (arg2.equals(replaceTarget)) {
			return copy(target, arg1, rvalueReplacer);
		}
		else {
			throw new IllegalArgumentException(String.format("Neither binary op action argument [%s, %s] matched replacement target %s!", arg1, arg2, replaceTarget));
		}
	}
	
	@Override
	public boolean canReplaceLvalue() {
		return true;
	}
	
	@Override
	public DataId getLvalueReplacer() {
		return null;
	}
	
	@Override
	public Action replaceLvalue(DataId replaceTarget, DataId lvalueReplacer) {
		if (target.equals(replaceTarget)) {
			return copy(lvalueReplacer, arg1, arg2);
		}
		else {
			throw new IllegalArgumentException(String.format("Binary op action target %s doesn't match replacement target %s!", target, replaceTarget));
		}
	}
	
	@Override
	public Action swapRvalues(int i, int j) {
		if ((i == 0 && j == 1) || (i == 1 && j == 0)) {
			return commutated(target, arg2, arg1);
		}
		else {
			return null;
		}
	}
	
	@Override
	public Action replaceRegIds(Map<DataId, DataId> regIdMap) {
		DataId target = this.target.removeAllDereferences(), arg1 = this.arg1.removeAllDereferences(), arg2 = this.arg2.removeAllDereferences();
		if (Helpers.isRegId(target.raw) && regIdMap.containsKey(target)) {
			target = regIdMap.get(target);
		}
		if (Helpers.isRegId(arg1.raw) && regIdMap.containsKey(arg1)) {
			arg1 = regIdMap.get(arg1);
		}
		if (Helpers.isRegId(arg2.raw) && regIdMap.containsKey(arg2)) {
			arg2 = regIdMap.get(arg2);
		}
		
		if (!target.equalsOther(this.target, true) || !arg1.equalsOther(this.arg1, true) || !arg2.equalsOther(this.arg2, true)) {
			return copy(target.addDereferences(this.target.dereferenceLevel), arg1.addDereferences(this.arg1.dereferenceLevel), arg2.addDereferences(this.arg2.dereferenceLevel));
		}
		else {
			return null;
		}
	}
}
