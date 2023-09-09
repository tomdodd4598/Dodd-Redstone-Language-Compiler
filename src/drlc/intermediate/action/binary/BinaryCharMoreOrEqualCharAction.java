package drlc.intermediate.action.binary;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.data.DataId;

public class BinaryCharMoreOrEqualCharAction extends BinaryOpAction {
	
	BinaryCharMoreOrEqualCharAction(ASTNode node, DataId target, DataId arg1, DataId arg2) {
		super(node, BinaryActionType.CHAR_MORE_OR_EQUAL_CHAR, target, arg1, arg2);
	}
	
	@Override
	protected BinaryOpAction copy(DataId target, DataId arg1, DataId arg2) {
		return new BinaryCharMoreOrEqualCharAction(null, target, arg1, arg2);
	}
	
	@Override
	protected BinaryOpAction commutated(DataId target, DataId arg1, DataId arg2) {
		return new BinaryCharLessOrEqualCharAction(null, target, arg1, arg2);
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
