package drlc.interpret.action;

import drlc.Global;
import drlc.node.Node;

public class ConditionalJumpAction extends Action implements IJumpAction<ConditionalJumpAction> {
	
	public final String target;
	
	public ConditionalJumpAction(Node node, String target) {
		super(node);
		if (target == null) {
			throw new IllegalArgumentException(String.format("Conditional jump action target was null! %s", node));
		}
		else {
			this.target = target;
		}
	}
	
	@Override
	public boolean conditional() {
		return true;
	}
	
	@Override
	public String getTarget() {
		return target;
	}
	
	@Override
	public ConditionalJumpAction copy(String target) {
		return new ConditionalJumpAction(null, target);
	}
	
	@Override
	public String toString() {
		return Global.CONDITIONAL_JUMP.concat(" ").concat(target);
	}
}
