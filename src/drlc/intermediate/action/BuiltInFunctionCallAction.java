package drlc.intermediate.action;

import java.util.List;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.data.DataId;

public class BuiltInFunctionCallAction extends FunctionCallAction {
	
	public BuiltInFunctionCallAction(ASTNode<?, ?> node, DataId target, DataId function, List<DataId> args) {
		super(node, target, function, args);
	}
	
	@Override
	protected BuiltInFunctionCallAction copy(ASTNode<?, ?> node, DataId target, DataId function, List<DataId> args) {
		return new BuiltInFunctionCallAction(node, target, function, args);
	}
	
	@Override
	public String toString() {
		return target + " = " + Global.CALL + ' ' + Global.BUILT_IN + ' ' + function + Helpers.listString(args);
	}
}
