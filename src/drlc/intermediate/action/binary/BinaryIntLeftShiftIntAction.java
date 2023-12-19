package drlc.intermediate.action.binary;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.data.DataId;

public class BinaryIntLeftShiftIntAction extends BinaryOpAction {
	
	BinaryIntLeftShiftIntAction(ASTNode<?, ?> node, DataId target, DataId arg1, DataId arg2) {
		super(node, BinaryActionType.INT_LEFT_SHIFT_INT, target, arg1, arg2);
	}
	
	@Override
	protected BinaryOpAction copy(DataId target, DataId arg1, DataId arg2) {
		return new BinaryIntLeftShiftIntAction(null, target, arg1, arg2);
	}
	
	@Override
	protected BinaryOpAction commutated(DataId target, DataId arg1, DataId arg2) {
		return null;
	}
	
	@Override
	public boolean canReorderRvalues() {
		return false;
	}
	
	@Override
	public String toString() {
		return target + " = " + arg1 + " << " + arg2;
	}
}
