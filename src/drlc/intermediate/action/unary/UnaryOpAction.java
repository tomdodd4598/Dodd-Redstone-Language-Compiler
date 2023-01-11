package drlc.intermediate.action.unary;

import java.util.Map;

import drlc.Helpers;
import drlc.intermediate.action.*;
import drlc.intermediate.component.DataId;
import drlc.node.Node;

public abstract class UnaryOpAction extends Action implements IValueAction {
	
	public final UnaryActionType type;
	public final DataId target, arg;
	
	protected UnaryOpAction(Node node, UnaryActionType type, DataId target, DataId arg) {
		super(node);
		if (type == null) {
			throw new IllegalArgumentException(String.format("Unary op action type was null! %s", node));
		}
		else {
			this.type = type;
		}
		
		if (target == null) {
			throw new IllegalArgumentException(String.format("Unary op action target was null! %s", node));
		}
		else {
			this.target = target;
		}
		
		if (arg == null) {
			throw new IllegalArgumentException(String.format("Unary op action argument was null! %s", node));
		}
		else {
			this.arg = arg;
		}
	}
	
	protected abstract UnaryOpAction copy(DataId target, DataId arg);
	
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
		return copy(target, rvalueReplacer);
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
		return copy(lvalueReplacer, arg);
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
			return copy(target.addDereferences(this.target.dereferenceLevel), arg.addDereferences(this.arg.dereferenceLevel));
		}
		else {
			return null;
		}
	}
}
