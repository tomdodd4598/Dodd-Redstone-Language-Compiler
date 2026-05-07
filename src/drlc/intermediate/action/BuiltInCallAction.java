package drlc.intermediate.action;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.Function;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.scope.Scope;

public class BuiltInCallAction extends CallAction {
	
	public final @NonNull Function builtInFunction;
	
	public BuiltInCallAction(ASTNode<?> node, Scope scope, DataId target, DataId caller, List<DataId> args, @NonNull Function builtInFunction) {
		super(node, scope, target, caller, args, builtInFunction);
		this.builtInFunction = builtInFunction;
	}
	
	@Override
	protected BuiltInCallAction copy(ASTNode<?> node, Scope scope, DataId target, DataId caller, List<DataId> args) {
		return new BuiltInCallAction(node, scope, target, caller, args, builtInFunction);
	}
	
	@Override
	public @NonNull Function getDirectFunction() {
		return builtInFunction;
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
			return new BuiltInCallAction(null, scope, targetResult.dataId, functionResult.dataId, replaceArgs, builtInFunction);
		}
		else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return target + " = " + Global.CALL + " " + Global.BUILT_IN + " " + caller + Helpers.listString(args);
	}
}
