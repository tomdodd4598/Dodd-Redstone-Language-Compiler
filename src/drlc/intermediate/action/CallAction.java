package drlc.intermediate.action;

import java.util.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.scope.Scope;

public class CallAction extends Action implements IValueAction {
	
	public final Scope scope;
	public final DataId target;
	public final DataId caller;
	public final List<DataId> args;
	
	public CallAction(ASTNode<?> node, Scope scope, DataId target, DataId caller, List<DataId> args) {
		super(node);
		if (scope == null) {
			throw Helpers.nodeError(node, "Function call action scope was null!");
		}
		else {
			this.scope = scope;
		}
		
		if (target == null) {
			throw Helpers.nodeError(node, "Function call action target was null!");
		}
		else {
			this.target = target;
		}
		
		if (caller == null) {
			throw Helpers.nodeError(node, "Function call action caller was null!");
		}
		else {
			this.caller = caller;
		}
		
		if (args == null) {
			throw Helpers.nodeError(node, "Function call action argument list was null!");
		}
		else {
			this.args = args;
		}
	}
	
	protected CallAction copy(ASTNode<?> node, Scope scope, DataId target, DataId function, List<DataId> args) {
		return new CallAction(node, scope, target, function, args);
	}
	
	@Override
	public DataId[] lvalues() {
		return new DataId[] {target};
	}
	
	@Override
	public DataId[] rvalues() {
		int argCount = args.size();
		DataId[] rvalues = new DataId[1 + argCount];
		rvalues[0] = caller;
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
	public CallAction replaceRvalue(DataId targetId, DataId rvalueReplacer) {
		DataIdReplaceResult functionResult = replaceDataId(caller, targetId, rvalueReplacer);
		boolean success = functionResult.success;
		List<DataId> replaceArgs = new ArrayList<>();
		for (DataId arg : args) {
			DataIdReplaceResult argResult = replaceDataId(arg, targetId, rvalueReplacer);
			success |= argResult.success;
			replaceArgs.add(argResult.dataId);
		}
		if (success) {
			return copy(null, scope, target, functionResult.dataId, replaceArgs);
		}
		else {
			throw new IllegalArgumentException(String.format("No function call action rvalue %s, %s matched replacement data ID %s!", caller, Helpers.listString(args), targetId));
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
	public CallAction replaceLvalue(DataId targetId, DataId lvalueReplacer) {
		return copy(null, scope, lvalueReplacer, caller, new ArrayList<>(args));
	}
	
	@Override
	public Action setTransientLvalue() {
		return copy(null, scope, target.getTransient(null), caller, new ArrayList<>(args));
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
		DataIdReplaceResult targetResult = replaceRegId(target, regIdMap), functionResult = replaceRegId(caller, regIdMap);
		boolean success = targetResult.success || functionResult.success;
		List<DataId> replaceArgs = new ArrayList<>();
		for (DataId arg : args) {
			DataIdReplaceResult argResult = replaceRegId(arg, regIdMap);
			success |= argResult.success;
			replaceArgs.add(argResult.dataId);
		}
		if (success) {
			return copy(null, scope, targetResult.dataId, functionResult.dataId, replaceArgs);
		}
		else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return target + " = " + Global.CALL + " " + caller + Helpers.listString(args);
	}
}
