package drlc.intermediate.action.binary;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.data.DataId;

public class BinaryBoolMoreOrEqualBoolAction extends BinaryOpAction {
	
	BinaryBoolMoreOrEqualBoolAction(ASTNode<?, ?> node, DataId target, DataId arg1, DataId arg2) {
		super(node, BinaryActionType.BOOL_MORE_OR_EQUAL_BOOL, target, arg1, arg2);
	}
	
	@Override
	protected BinaryOpAction copy(DataId target, DataId arg1, DataId arg2) {
		return new BinaryBoolMoreOrEqualBoolAction(null, target, arg1, arg2);
	}
	
	@Override
	protected BinaryOpAction commutated(DataId target, DataId arg1, DataId arg2) {
		return new BinaryBoolLessOrEqualBoolAction(null, target, arg1, arg2);
	}
	
	@Override
	public boolean canReorderRvalues() {
		return true;
	}
	
	@Override
	public String toString() {
		return target + " = " + arg1 + " >= " + arg2;
	}
}
