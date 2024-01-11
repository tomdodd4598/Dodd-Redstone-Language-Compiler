package drlc.intermediate.action;

import drlc.Helpers;
import drlc.intermediate.ast.ASTNode;

public abstract class BasicAction extends Action {
	
	public final String arg;
	
	public BasicAction(ASTNode<?> node, String arg) {
		super(node);
		if (arg == null) {
			throw Helpers.nodeError(node, "Basic action argument was null!");
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
