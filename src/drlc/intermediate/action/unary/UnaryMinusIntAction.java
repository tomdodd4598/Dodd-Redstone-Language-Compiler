package drlc.intermediate.action.unary;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.data.DataId;

public class UnaryMinusIntAction extends UnaryOpAction {
	
	UnaryMinusIntAction(ASTNode node, DataId target, DataId arg) {
		super(node, UnaryActionType.MINUS_INT, target, arg);
	}
	
	@Override
	protected UnaryOpAction copy(DataId target, DataId arg) {
		return new UnaryMinusIntAction(null, target, arg);
	}
	
	@Override
	public String toString() {
		return target + " = -" + arg;
	}
}
