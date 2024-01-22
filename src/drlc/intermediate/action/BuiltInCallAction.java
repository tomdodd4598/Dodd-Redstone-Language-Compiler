package drlc.intermediate.action;

import java.util.List;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.scope.Scope;

public class BuiltInCallAction extends CallAction {
	
	public BuiltInCallAction(ASTNode<?> node, Scope scope, DataId target, DataId caller, List<DataId> args) {
		super(node, scope, target, caller, args);
	}
	
	@Override
	protected BuiltInCallAction copy(ASTNode<?> node, Scope scope, DataId target, DataId caller, List<DataId> args) {
		return new BuiltInCallAction(node, scope, target, caller, args);
	}
	
	@Override
	public String toString() {
		return target + " = " + Global.CALL + " " + Global.BUILT_IN + " " + caller + Helpers.listString(args);
	}
}
