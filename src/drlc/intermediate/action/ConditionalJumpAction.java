package drlc.intermediate.action;

import drlc.Global;
import drlc.intermediate.ast.ASTNode;

public class ConditionalJumpAction extends Action implements IJumpAction {
	
	protected int target;
	public final boolean jumpCondition;
	
	public ConditionalJumpAction(ASTNode<?, ?> node, int target, boolean jumpCondition) {
		super(node);
		this.target = target;
		this.jumpCondition = jumpCondition;
	}
	
	@Override
	public boolean isConditional() {
		return true;
	}
	
	@Override
	public int getTarget() {
		return target;
	}
	
	public void setTarget(int target) {
		this.target = target;
	}
	
	@Override
	public ConditionalJumpAction copy(int target) {
		return new ConditionalJumpAction(null, target, jumpCondition);
	}
	
	@Override
	public String toString() {
		return (jumpCondition ? Global.CONDITIONAL_JUMP : Global.CONDITIONAL_NOT_JUMP) + ' ' + getTarget();
	}
}
