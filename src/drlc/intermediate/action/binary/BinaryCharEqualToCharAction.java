package drlc.intermediate.action.binary;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.data.DataId;

public class BinaryCharEqualToCharAction extends BinaryOpAction {
	
	BinaryCharEqualToCharAction(ASTNode<?, ?> node, DataId target, DataId arg1, DataId arg2) {
		super(node, BinaryActionType.CHAR_EQUAL_TO_CHAR, target, arg1, arg2);
	}
	
	@Override
	protected BinaryOpAction copy(DataId target, DataId arg1, DataId arg2) {
		return new BinaryCharEqualToCharAction(null, target, arg1, arg2);
	}
	
	@Override
	protected BinaryOpAction commutated(DataId target, DataId arg1, DataId arg2) {
		return new BinaryCharEqualToCharAction(null, target, arg1, arg2);
	}
	
	@Override
	public boolean canReorderRvalues() {
		return true;
	}
	
	@Override
	public String toString() {
		return target + " = " + arg1 + " == " + arg2;
	}
}
