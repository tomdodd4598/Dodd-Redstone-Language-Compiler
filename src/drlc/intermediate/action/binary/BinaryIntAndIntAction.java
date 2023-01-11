package drlc.intermediate.action.binary;

import drlc.intermediate.component.DataId;
import drlc.node.Node;

public class BinaryIntAndIntAction extends BinaryOpAction {
	
	BinaryIntAndIntAction(Node node, DataId target, DataId arg1, DataId arg2) {
		super(node, BinaryActionType.INT_AND_INT, target, arg1, arg2);
	}
	
	@Override
	protected BinaryOpAction copy(DataId target, DataId arg1, DataId arg2) {
		return new BinaryIntAndIntAction(null, target, arg1, arg2);
	}
	
	@Override
	protected BinaryOpAction commutated(DataId target, DataId arg1, DataId arg2) {
		return new BinaryIntAndIntAction(null, target, arg1, arg2);
	}
	
	@Override
	public boolean canReorderRvalues() {
		return true;
	}
	
	@Override
	public String toString() {
		return target.raw.concat(" = ").concat(arg1.raw).concat(" & ").concat(arg2.raw);
	}
}
