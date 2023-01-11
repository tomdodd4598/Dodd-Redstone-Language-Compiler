package drlc.intermediate.action.binary;

import drlc.intermediate.component.DataId;
import drlc.node.Node;

public class BinaryNatDivideNatAction extends BinaryOpAction {
	
	BinaryNatDivideNatAction(Node node, DataId target, DataId arg1, DataId arg2) {
		super(node, BinaryActionType.NAT_DIVIDE_NAT, target, arg1, arg2);
	}
	
	@Override
	protected BinaryOpAction copy(DataId target, DataId arg1, DataId arg2) {
		return new BinaryNatDivideNatAction(null, target, arg1, arg2);
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
		return target.raw.concat(" = ").concat(arg1.raw).concat(" / ").concat(arg2.raw);
	}
}
