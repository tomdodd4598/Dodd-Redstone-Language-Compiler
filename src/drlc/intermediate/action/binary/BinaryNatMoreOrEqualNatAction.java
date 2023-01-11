package drlc.intermediate.action.binary;

import drlc.intermediate.component.DataId;
import drlc.node.Node;

public class BinaryNatMoreOrEqualNatAction extends BinaryOpAction {
	
	BinaryNatMoreOrEqualNatAction(Node node, DataId target, DataId arg1, DataId arg2) {
		super(node, BinaryActionType.NAT_MORE_OR_EQUAL_NAT, target, arg1, arg2);
	}
	
	@Override
	protected BinaryOpAction copy(DataId target, DataId arg1, DataId arg2) {
		return new BinaryNatMoreOrEqualNatAction(null, target, arg1, arg2);
	}
	
	@Override
	protected BinaryOpAction commutated(DataId target, DataId arg1, DataId arg2) {
		return new BinaryNatLessOrEqualNatAction(null, target, arg1, arg2);
	}
	
	@Override
	public boolean canReorderRvalues() {
		return true;
	}
	
	@Override
	public String toString() {
		return target.raw.concat(" = ").concat(arg1.raw).concat(" >= ").concat(arg2.raw);
	}
}
