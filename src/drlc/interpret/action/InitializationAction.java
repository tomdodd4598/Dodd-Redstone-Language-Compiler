package drlc.interpret.action;

import java.util.Map;

import drlc.*;
import drlc.interpret.component.DataId;
import drlc.interpret.component.info.type.TypeInfo;
import drlc.node.Node;

public class InitializationAction extends Action implements IValueAction {
	
	public final DataId target, arg;
	public final TypeInfo targetTypeInfo;
	
	public InitializationAction(Node node, DataId target, TypeInfo targetTypeInfo, DataId arg) {
		super(node);
		if (target == null) {
			throw new IllegalArgumentException(String.format("Initialization action target was null! %s", node));
		}
		else {
			this.target = target;
		}
		if (targetTypeInfo == null) {
			throw new IllegalArgumentException(String.format("Initialization action target type info was null! %s", node));
		}
		else {
			this.targetTypeInfo = targetTypeInfo;
		}
		if (arg == null) {
			throw new IllegalArgumentException(String.format("Initialization action argument was null! %s", node));
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
		return false;
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
		return new InitializationAction(null, target, targetTypeInfo, rvalueReplacer);
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
		DataId target = this.target.removeAllDereferences(), arg = this.arg.removeAllDereferences();
		if (Helpers.isRegId(target.raw) && regIdMap.containsKey(target)) {
			target = regIdMap.get(target);
		}
		if (Helpers.isRegId(arg.raw) && regIdMap.containsKey(arg)) {
			arg = regIdMap.get(arg);
		}
		
		if (!target.equalsOther(this.target, true) || !arg.equalsOther(this.arg, true)) {
			return new InitializationAction(null, target.addDereferences(this.target.dereferenceLevel), targetTypeInfo, arg.addDereferences(this.arg.dereferenceLevel));
		}
		else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return Global.VAR.concat(" ").concat(target.raw).concat(" ").concat(targetTypeInfo.toString()).concat(" = ").concat(arg.raw);
	}
}
