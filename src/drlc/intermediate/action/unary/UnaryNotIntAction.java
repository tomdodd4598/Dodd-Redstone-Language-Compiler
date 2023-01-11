package drlc.intermediate.action.unary;

import drlc.intermediate.component.DataId;
import drlc.node.Node;

public class UnaryNotIntAction extends UnaryOpAction {
	
	UnaryNotIntAction(Node node, DataId target, DataId arg) {
		super(node, UnaryActionType.NOT_INT, target, arg);
	}
	
	@Override
	protected UnaryOpAction copy(DataId target, DataId arg) {
		return new UnaryNotIntAction(null, target, arg);
	}
	
	@Override
	public String toString() {
		return target.raw.concat(" = !").concat(arg.raw);
	}
}
