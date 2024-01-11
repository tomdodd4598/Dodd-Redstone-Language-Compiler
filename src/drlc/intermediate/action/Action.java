package drlc.intermediate.action;

import drlc.intermediate.ast.ASTNode;

public abstract class Action {
	
	public Action(ASTNode<?> node) {}
	
	@Override
	public abstract String toString();
}
