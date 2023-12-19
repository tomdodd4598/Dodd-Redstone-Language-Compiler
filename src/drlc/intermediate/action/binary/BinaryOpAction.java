package drlc.intermediate.action.binary;

import java.util.Map;

import drlc.*;
import drlc.intermediate.action.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.data.*;

public abstract class BinaryOpAction extends Action implements IValueAction {
	
	public final BinaryActionType type;
	public final DataId target, arg1, arg2;
	
	protected BinaryOpAction(ASTNode<?, ?> node, BinaryActionType type, DataId target, DataId arg1, DataId arg2) {
		super(node);
		if (type == null) {
			throw Helpers.nodeError(node, "Binary op action type was null!");
		}
		else {
			this.type = type;
		}
		
		if (target == null) {
			throw Helpers.nodeError(node, "Binary op action target was null!");
		}
		else {
			this.target = target;
		}
		
		if (arg1 == null) {
			throw Helpers.nodeError(node, "Binary op action first argument was null!");
		}
		else {
			this.arg1 = arg1;
		}
		
		if (arg2 == null) {
			throw Helpers.nodeError(node, "Binary op action second argument was null!");
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
	public BinaryOpAction replaceRvalue(DataId targetId, DataId rvalueReplacer) {
		DataIdReplaceResult arg1Result = replaceDataId(arg1, targetId, rvalueReplacer), arg2Result = replaceDataId(arg2, targetId, rvalueReplacer);
		if (arg1Result.success || arg2Result.success) {
			return copy(target, arg1Result.dataId, arg2Result.dataId);
		}
		else {
			throw new IllegalArgumentException(String.format("Neither binary op action argument %s, %s matched replacement data ID %s!", arg1, arg2, targetId));
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
	public BinaryOpAction replaceLvalue(DataId targetId, DataId lvalueReplacer) {
		return copy(lvalueReplacer, arg1, arg2);
	}
	
	@Override
	public Action setTransientLvalue() {
		return copy(target.getTransient(null), arg1, arg2);
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
	public Action foldRvalues() {
		if (arg1 instanceof ValueDataId && arg2 instanceof ValueDataId) {
			return new AssignmentAction(null, target, new ValueDataId(Main.generator.binaryOp(null, ((ValueDataId) arg1).value, type.opType, ((ValueDataId) arg2).value)));
		}
		else {
			return null;
		}
	}
	
	@Override
	public Action replaceRegIds(Map<Long, Long> regIdMap) {
		DataIdReplaceResult targetResult = replaceRegId(target, regIdMap), arg1Result = replaceRegId(arg1, regIdMap), arg2Result = replaceRegId(arg2, regIdMap);
		if (targetResult.success || arg1Result.success || arg2Result.success) {
			return copy(targetResult.dataId, arg1Result.dataId, arg2Result.dataId);
		}
		else {
			return null;
		}
	}
}
