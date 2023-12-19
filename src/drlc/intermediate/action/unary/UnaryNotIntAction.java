package drlc.intermediate.action.unary;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.data.DataId;

public class UnaryNotIntAction extends UnaryOpAction {
	
	UnaryNotIntAction(ASTNode<?, ?> node, DataId target, DataId arg) {
		super(node, UnaryActionType.NOT_INT, target, arg);
	}
	
	@Override
	protected UnaryOpAction copy(DataId target, DataId arg) {
		return new UnaryNotIntAction(null, target, arg);
	}
	
	@Override
	public String toString() {
		return target + " = !" + arg;
	}
}
