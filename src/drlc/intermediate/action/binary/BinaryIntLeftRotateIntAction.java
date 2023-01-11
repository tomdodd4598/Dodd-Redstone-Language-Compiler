package drlc.intermediate.action.binary;

import drlc.intermediate.component.DataId;
import drlc.node.Node;

public class BinaryIntLeftRotateIntAction extends BinaryOpAction {
	
	BinaryIntLeftRotateIntAction(Node node, DataId target, DataId arg1, DataId arg2) {
		super(node, BinaryActionType.INT_LEFT_ROTATE_INT, target, arg1, arg2);
	}
	
	@Override
	protected BinaryOpAction copy(DataId target, DataId arg1, DataId arg2) {
		return new BinaryIntLeftRotateIntAction(null, target, arg1, arg2);
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
		return target.raw.concat(" = ").concat(arg1.raw).concat(" <<< ").concat(arg2.raw);
	}
}
