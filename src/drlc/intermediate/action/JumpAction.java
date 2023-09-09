package drlc.intermediate.action;

import drlc.Global;
import drlc.intermediate.ast.ASTNode;

public class JumpAction extends Action implements IJumpAction, IDefiniteRedirectAction {
	
	public final String target;
	
	public JumpAction(ASTNode node, String target) {
		super(node);
		if (target == null) {
			throw node.error("Jump action target was null!");
		}
		else {
			this.target = target;
		}
	}
	
	@Override
	public boolean isConditional() {
		return false;
	}
	
	@Override
	public String getTarget() {
		return target;
	}
	
	@Override
	public JumpAction copy(String target) {
		return new JumpAction(null, target);
	}
	
	@Override
	public String toString() {
		return Global.JUMP + ' ' + target;
	}
}
