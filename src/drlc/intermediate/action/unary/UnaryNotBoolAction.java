package drlc.intermediate.action.unary;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.data.DataId;

public class UnaryNotBoolAction extends UnaryOpAction {
	
	UnaryNotBoolAction(ASTNode node, DataId target, DataId arg) {
		super(node, UnaryActionType.NOT_BOOL, target, arg);
	}
	
	@Override
	protected UnaryOpAction copy(DataId target, DataId arg) {
		return new UnaryNotBoolAction(null, target, arg);
	}
	
	@Override
	public String toString() {
		return target + " = !" + arg;
	}
}
