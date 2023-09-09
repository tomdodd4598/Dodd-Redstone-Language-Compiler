package drlc.intermediate.action.unary;

import java.util.Map;

import drlc.Main;
import drlc.intermediate.action.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.data.*;

public abstract class UnaryOpAction extends Action implements IValueAction {
	
	public final UnaryActionType type;
	public final DataId target, arg;
	
	protected UnaryOpAction(ASTNode node, UnaryActionType type, DataId target, DataId arg) {
		super(node);
		if (type == null) {
			throw node.error("Unary op action type was null!");
		}
		else {
			this.type = type;
		}
		
		if (target == null) {
			throw node.error("Unary op action target was null!");
		}
		else {
			this.target = target;
		}
		
		if (arg == null) {
			throw node.error("Unary op action argument was null!");
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
	public Action replaceRegRvalue(long targetId, DataId rvalueReplacer) {
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
	public Action replaceRegLvalue(long targetId, DataId lvalueReplacer) {
		return copy(lvalueReplacer, arg);
	}
	
	@Override
	public Action setTransientLvalue() {
		return copy(target.getTransient(), arg);
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
		if (arg instanceof ValueDataId) {
			return new AssignmentAction(null, target, new ValueDataId(Main.generator.unaryOp(null, type.opType, ((ValueDataId) arg).value)));
		}
		else {
			return null;
		}
	}
	
	@Override
	public Action replaceRegIds(Map<Long, Long> regIdMap) {
		RegReplaceResult targetResult = replaceRegId(target, regIdMap), argResult = replaceRegId(arg, regIdMap);
		if (targetResult.success || argResult.success) {
			return copy(targetResult.dataId, argResult.dataId);
		}
		else {
			return null;
		}
	}
}
