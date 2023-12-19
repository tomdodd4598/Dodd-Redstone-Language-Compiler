package drlc.intermediate.action.binary;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.data.DataId;

public class BinaryIntNotEqualToIntAction extends BinaryOpAction {
	
	BinaryIntNotEqualToIntAction(ASTNode<?, ?> node, DataId target, DataId arg1, DataId arg2) {
		super(node, BinaryActionType.INT_NOT_EQUAL_TO_INT, target, arg1, arg2);
	}
	
	@Override
	protected BinaryOpAction copy(DataId target, DataId arg1, DataId arg2) {
		return new BinaryIntNotEqualToIntAction(null, target, arg1, arg2);
	}
	
	@Override
	protected BinaryOpAction commutated(DataId target, DataId arg1, DataId arg2) {
		return new BinaryIntNotEqualToIntAction(null, target, arg1, arg2);
	}
	
	@Override
	public boolean canReorderRvalues() {
		return true;
	}
	
	@Override
	public String toString() {
		return target + " = " + arg1 + " != " + arg2;
	}
}
