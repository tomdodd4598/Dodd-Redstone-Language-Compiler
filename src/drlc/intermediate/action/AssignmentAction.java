package drlc.intermediate.action;

import java.util.Map;

import drlc.Helpers;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.data.DataId;

public class AssignmentAction extends Action implements IValueAction {
	
	public final DataId target, arg;
	
	public AssignmentAction(ASTNode<?, ?> node, DataId target, DataId arg) {
		super(node);
		if (target == null) {
			throw Helpers.nodeError(node, "Assignment action target was null!");
		}
		else {
			this.target = target;
		}
		
		if (arg == null) {
			throw Helpers.nodeError(node, "Assignment action argument was null!");
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
		return target.dereferenceLevel == 0 && arg.dereferenceLevel <= 0;
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
	public AssignmentAction replaceRvalue(DataId targetId, DataId rvalueReplacer) {
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
	public AssignmentAction replaceLvalue(DataId targetId, DataId lvalueReplacer) {
		return new AssignmentAction(null, lvalueReplacer, arg);
	}
	
	@Override
	public Action setTransientLvalue() {
		return new AssignmentAction(null, target.getTransient(null), arg);
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
		DataIdReplaceResult targetResult = replaceRegId(target, regIdMap), argResult = replaceRegId(arg, regIdMap);
		if (targetResult.success || argResult.success) {
			return new AssignmentAction(null, targetResult.dataId, argResult.dataId);
		}
		else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return target + " = " + arg;
	}
}
