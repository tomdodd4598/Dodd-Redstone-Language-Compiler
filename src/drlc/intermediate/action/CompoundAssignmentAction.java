package drlc.intermediate.action;

import java.util.*;

import drlc.Helpers;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.data.DataId;

public class CompoundAssignmentAction extends Action implements IValueAction {
	
	public final DataId target;
	public final List<DataId> args;
	
	public CompoundAssignmentAction(ASTNode<?, ?> node, DataId target, List<DataId> args) {
		super(node);
		if (target == null) {
			throw Helpers.nodeError(node, "Compound assignment action target was null!");
		}
		else {
			this.target = target;
		}
		
		if (args == null) {
			throw Helpers.nodeError(node, "Compound assignment action argument list was null!");
		}
		else {
			this.args = args;
		}
	}
	
	@Override
	public DataId[] lvalues() {
		return new DataId[] {target};
	}
	
	@Override
	public DataId[] rvalues() {
		return args.toArray(new DataId[0]);
	}
	
	@Override
	public boolean canRemove(boolean compoundReplacement) {
		return compoundReplacement && target.dereferenceLevel == 0 && args.stream().allMatch(x -> x.dereferenceLevel <= 0);
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
	public CompoundAssignmentAction replaceRvalue(DataId targetId, DataId rvalueReplacer) {
		boolean success = false;
		List<DataId> replaceArgs = new ArrayList<>();
		for (DataId arg : args) {
			DataIdReplaceResult argResult = replaceDataId(arg, targetId, rvalueReplacer);
			success |= argResult.success;
			replaceArgs.add(argResult.dataId);
		}
		if (success) {
			return new CompoundAssignmentAction(null, target, replaceArgs);
		}
		else {
			throw new IllegalArgumentException(String.format("No compound assignment action argument %s matched replacement data ID %s!", Helpers.listString(args), targetId));
		}
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
	public CompoundAssignmentAction replaceLvalue(DataId targetId, DataId lvalueReplacer) {
		return new CompoundAssignmentAction(null, lvalueReplacer, new ArrayList<>(args));
	}
	
	@Override
	public Action setTransientLvalue() {
		return new CompoundAssignmentAction(null, target.getTransient(null), new ArrayList<>(args));
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
		DataIdReplaceResult targetResult = replaceRegId(target, regIdMap);
		boolean success = targetResult.success;
		List<DataId> replaceArgs = new ArrayList<>();
		for (DataId arg : args) {
			DataIdReplaceResult argResult = replaceRegId(arg, regIdMap);
			success |= argResult.success;
			replaceArgs.add(argResult.dataId);
		}
		if (success) {
			return new CompoundAssignmentAction(null, targetResult.dataId, replaceArgs);
		}
		else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return target + " = " + Helpers.listString(args);
	}
}
