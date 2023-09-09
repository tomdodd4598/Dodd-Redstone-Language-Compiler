package drlc.intermediate.action;

import drlc.Global;
import drlc.intermediate.ast.ASTNode;

public class ConditionalJumpAction extends Action implements IJumpAction {
	
	public final String target;
	public final boolean jumpCondition;
	
	public ConditionalJumpAction(ASTNode node, String target, boolean jumpCondition) {
		super(node);
		if (target == null) {
			throw node.error("Conditional jump action target was null!");
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
		return (jumpCondition ? Global.CONDITIONAL_JUMP : Global.CONDITIONAL_NOT_JUMP) + ' ' + target;
	}
}
