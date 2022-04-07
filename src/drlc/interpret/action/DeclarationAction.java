package drlc.interpret.action;

import java.util.Map;

import drlc.*;
import drlc.interpret.component.DataId;
import drlc.interpret.component.info.type.TypeInfo;
import drlc.node.Node;

public class DeclarationAction extends Action implements IValueAction {
	
	public final DataId target;
	public final TypeInfo targetTypeInfo;
	
	public DeclarationAction(Node node, DataId target, TypeInfo targetTypeInfo) {
		super(node);
		if (target == null) {
			throw new IllegalArgumentException(String.format("Declaration action target was null! %s", node));
		}
		else {
			this.target = target;
		}
		if (targetTypeInfo == null) {
			throw new IllegalArgumentException(String.format("Declaration action target type info was null! %s", node));
		}
		else {
			this.targetTypeInfo = targetTypeInfo;
		}
	}
	
	@Override
	public DataId[] lvalues() {
		return new DataId[] {target};
	}
	
	@Override
	public DataId[] rvalues() {
		return new DataId[] {};
	}
	
	@Override
	public boolean canRemove() {
		return false;
	}
	
	@Override
	public boolean canReplaceRvalue() {
		return false;
	}
	
	@Override
	public DataId getRvalueReplacer() {
		return null;
	}
	
	@Override
	public Action replaceRvalue(DataId replaceTarget, DataId rvalueReplacer) {
		return null;
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
		DataId target = this.target.removeAllDereferences();
		if (Helpers.isRegId(target.raw) && regIdMap.containsKey(target)) {
			target = regIdMap.get(target);
		}
		
		if (!target.equalsOther(this.target, true)) {
			return new DeclarationAction(null, target.addDereferences(this.target.dereferenceLevel), targetTypeInfo);
		}
		else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return Global.VAR.concat(" ").concat(target.raw).concat(" ").concat(targetTypeInfo.toString());
	}
}
