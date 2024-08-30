package drlc.intermediate.action;

import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.data.*;

public class UnaryOpAction extends Action implements IValueAction {
	
	public final @NonNull UnaryActionType type;
	public final DataId target, arg;
	
	protected UnaryOpAction(ASTNode<?> node, @NonNull UnaryActionType type, DataId target, DataId arg) {
		super(node);
		this.type = type;
		
		if (target == null) {
			throw Helpers.nodeError(node, "Unary op action target was null!");
		}
		else {
			this.target = target;
		}
		
		if (arg == null) {
			throw Helpers.nodeError(node, "Unary op action argument was null!");
		}
		else {
			this.arg = arg;
		}
	}
	
	protected UnaryOpAction copy(DataId target, DataId arg) {
		return new UnaryOpAction(null, type, target, arg);
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
	public boolean canRemove(boolean compoundReplacement) {
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
	public UnaryOpAction replaceRvalue(DataId targetId, DataId rvalueReplacer) {
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
	public UnaryOpAction replaceLvalue(DataId targetId, DataId lvalueReplacer) {
		return copy(lvalueReplacer, arg);
	}
	
	@Override
	public Action setTransientLvalue() {
		return copy(target.getTransient(null), arg);
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
		if (arg instanceof ValueDataId valueDataId) {
			return new AssignmentAction(null, target, Main.generator.unaryOp(null, type.opType, valueDataId.value).dataId());
		}
		else {
			return null;
		}
	}
	
	@Override
	public Action replaceRegIds(Map<Long, Long> regIdMap) {
		DataIdReplaceResult targetResult = replaceRegId(target, regIdMap), argResult = replaceRegId(arg, regIdMap);
		if (targetResult.success || argResult.success) {
			return copy(targetResult.dataId, argResult.dataId);
		}
		else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return target + " = " + type + arg;
	}
}
