package drlc.intermediate.action;

import java.util.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.scope.Scope;

public class FunctionCallAction extends Action implements IValueAction {
	
	public final DataId target;
	public final DataId function;
	public final List<DataId> args;
	public final Scope scope;
	
	public FunctionCallAction(ASTNode<?> node, DataId target, DataId function, List<DataId> args, Scope scope) {
		super(node);
		if (target == null) {
			throw Helpers.nodeError(node, "Function call action target was null!");
		}
		else {
			this.target = target;
		}
		
		if (function == null) {
			throw Helpers.nodeError(node, "Function call action function was null!");
		}
		else {
			this.function = function;
		}
		
		if (args == null) {
			throw Helpers.nodeError(node, "Function call action argument list was null!");
		}
		else {
			this.args = args;
		}
		
		if (scope == null) {
			throw Helpers.nodeError(node, "Function call action scope was null!");
		}
		else {
			this.scope = scope;
		}
	}
	
	protected FunctionCallAction copy(ASTNode<?> node, DataId target, DataId function, List<DataId> args, Scope scope) {
		return new FunctionCallAction(node, target, function, args, scope);
	}
	
	@Override
	public DataId[] lvalues() {
		return new DataId[] {target};
	}
	
	@Override
	public DataId[] rvalues() {
		int argCount = args.size();
		DataId[] rvalues = new DataId[1 + argCount];
		rvalues[0] = function;
		for (int i = 0; i < argCount; ++i) {
			rvalues[i + 1] = args.get(i);
		}
		return rvalues;
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
	public FunctionCallAction replaceRvalue(DataId targetId, DataId rvalueReplacer) {
		DataIdReplaceResult functionResult = replaceDataId(function, targetId, rvalueReplacer);
		boolean success = functionResult.success;
		List<DataId> replaceArgs = new ArrayList<>();
		for (DataId arg : args) {
			DataIdReplaceResult argResult = replaceDataId(arg, targetId, rvalueReplacer);
			success |= argResult.success;
			replaceArgs.add(argResult.dataId);
		}
		if (success) {
			return copy(null, target, functionResult.dataId, replaceArgs, scope);
		}
		else {
			throw new IllegalArgumentException(String.format("No function call action rvalue %s, %s matched replacement data ID %s!", function, Helpers.listString(args), targetId));
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
	public FunctionCallAction replaceLvalue(DataId targetId, DataId lvalueReplacer) {
		return copy(null, lvalueReplacer, function, new ArrayList<>(args), scope);
	}
	
	@Override
	public Action setTransientLvalue() {
		return copy(null, target.getTransient(null), function, new ArrayList<>(args), scope);
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
		DataIdReplaceResult targetResult = replaceRegId(target, regIdMap), functionResult = replaceRegId(function, regIdMap);
		boolean success = targetResult.success || functionResult.success;
		List<DataId> replaceArgs = new ArrayList<>();
		for (DataId arg : args) {
			DataIdReplaceResult argResult = replaceRegId(arg, regIdMap);
			success |= argResult.success;
			replaceArgs.add(argResult.dataId);
		}
		if (success) {
			return copy(null, targetResult.dataId, functionResult.dataId, replaceArgs, scope);
		}
		else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return target + " = " + Global.CALL + " " + function + Helpers.listString(args);
	}
}
