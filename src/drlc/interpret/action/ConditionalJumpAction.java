package drlc.interpret.action;

import drlc.Global;
import drlc.node.Node;

public class ConditionalJumpAction extends Action implements IJumpAction<ConditionalJumpAction> {
	
	public final String target;
	public final boolean jumpCondition;
	
	public ConditionalJumpAction(Node node, String target, boolean jumpCondition) {
		super(node);
		if (target == null) {
			throw new IllegalArgumentException(String.format("Conditional jump action target was null! %s", node));
		}
		else {
			this.target = target;
		}
		this.jumpCondition = jumpCondition;
	}
	
	@Override
	public boolean isConditional() {
		return true;
	}
	
	@Override
	public String getTarget() {
		return target;
	}
	
	@Override
	public ConditionalJumpAction copy(String target) {
		return new ConditionalJumpAction(null, target, jumpCondition);
	}
	
	@Override
	public String toString() {
		return (jumpCondition ? Global.CONDITIONAL_JUMP : Global.CONDITIONAL_NOT_JUMP).concat(" ").concat(target);
	}
}
