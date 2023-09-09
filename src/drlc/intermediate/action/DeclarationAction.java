package drlc.intermediate.action;

import java.util.Map;

import drlc.Global;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.component.type.TypeInfo;

public class DeclarationAction extends Action implements IValueAction {
	
	public final DataId target;
	public final TypeInfo targetTypeInfo;
	
	public DeclarationAction(ASTNode node, DataId target, TypeInfo targetTypeInfo) {
		super(node);
		if (target == null) {
			throw node.error("Declaration action target was null!");
		}
		else {
			this.target = target;
		}
		if (targetTypeInfo == null) {
			throw node.error("Declaration action target type info was null!");
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
	public Action replaceRegRvalue(long targetId, DataId rvalueReplacer) {
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
	public Action replaceRegLvalue(long targetId, DataId lvalueReplacer) {
		return null;
	}
	
	@Override
	public Action setTransientLvalue() {
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
	public Action foldRvalues() {
		return null;
	}
	
	@Override
	public Action replaceRegIds(Map<Long, Long> regIdMap) {
		RegReplaceResult targetResult = replaceRegId(target, regIdMap);
		if (targetResult.success) {
			return new DeclarationAction(null, targetResult.dataId, targetTypeInfo);
		}
		else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return Global.LET + ' ' + target.declarationString();
	}
}
