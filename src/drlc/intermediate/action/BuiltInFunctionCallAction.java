package drlc.intermediate.action;

import java.util.List;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.scope.Scope;

public class BuiltInFunctionCallAction extends FunctionCallAction {
	
	public BuiltInFunctionCallAction(ASTNode<?> node, DataId target, DataId function, List<DataId> args, Scope scope) {
		super(node, target, function, args, scope);
	}
	
	@Override
	protected BuiltInFunctionCallAction copy(ASTNode<?> node, DataId target, DataId function, List<DataId> args, Scope scope) {
		return new BuiltInFunctionCallAction(node, target, function, args, scope);
	}
	
	@Override
	public String toString() {
		return target + " = " + Global.CALL + " " + Global.BUILT_IN + " " + function + Helpers.listString(args);
	}
}
