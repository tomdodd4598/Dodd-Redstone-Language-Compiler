package drlc.intermediate.action;

import drlc.intermediate.ast.ASTNode;

public abstract class BasicAction extends Action {
	
	public final String arg;
	
	public BasicAction(ASTNode node, String arg) {
		super(node);
		if (arg == null) {
			throw node.error("Basic action argument was null!");
		}
		else {
			this.arg = arg;
		}
	}
	
	@Override
	public String toString() {
		return arg;
	}
}
