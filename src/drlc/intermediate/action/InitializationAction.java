package drlc.intermediate.action;

import java.util.Map;

import drlc.Global;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.component.type.TypeInfo;

public class InitializationAction extends Action implements IValueAction {
	
	public final DataId target, arg;
	public final TypeInfo targetTypeInfo;
	
	public InitializationAction(ASTNode node, DataId target, TypeInfo targetTypeInfo, DataId arg) {
		super(node);
		if (target == null) {
			throw node.error("Initialization action target was null!");
		}
		else {
			this.target = target;
		}
		if (targetTypeInfo == null) {
			throw node.error("Initialization action target type info was null!");
		}
		else {
			this.targetTypeInfo = targetTypeInfo;
		}
		if (arg == null) {
			throw node.error("Initialization action argument was null!");
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
	public Action replaceRegRvalue(long targetId, DataId rvalueReplacer) {
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
		RegReplaceResult targetResult = replaceRegId(target, regIdMap), argResult = replaceRegId(arg, regIdMap);
		if (targetResult.success || argResult.success) {
			return new InitializationAction(null, targetResult.dataId, targetTypeInfo, argResult.dataId);
		}
		else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return Global.LET + ' ' + target.declarationString() + " = " + arg;
	}
}
