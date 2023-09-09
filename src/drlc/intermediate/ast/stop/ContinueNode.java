package drlc.intermediate.ast.stop;

import drlc.intermediate.ast.ASTNode;
import drlc.node.Node;

public class ContinueNode extends StopNode {
	
	public ContinueNode(Node[] parseNodes) {
		super(parseNodes);
	}
	
	@Override
	public void setScopes(ASTNode parent) {
		scope = parent.scope;
	}
	
	@Override
	public void defineTypes(ASTNode parent) {
		
	}
	
	@Override
	public void declareExpressions(ASTNode parent) {
		routine = parent.routine;
		
		if (!scope.isBreakable()) {
			throw error("Can not use continue statement in non-iterative section!");
		}
	}
	
	@Override
	public void checkTypes(ASTNode parent) {
		
	}
	
	@Override
	public void foldConstants(ASTNode parent) {
		
	}
}
