package drlc.intermediate.action;

import drlc.Global;
import drlc.intermediate.ast.ASTNode;

public class JumpAction extends Action implements IJumpAction, IDefiniteRedirectAction {
	
	protected int target;
	
	public JumpAction(ASTNode<?, ?> node, int target) {
		super(node);
		this.target = target;
	}
	
	@Override
	public boolean isConditional() {
		return false;
	}
	
	@Override
	public int getTarget() {
		return target;
	}
	
	public void setTarget(int target) {
		this.target = target;
	}
	
	@Override
	public JumpAction copy(int target) {
		return new JumpAction(null, target);
	}
	
	@Override
	public String toString() {
		return Global.JUMP + " " + getTarget();
	}
}
