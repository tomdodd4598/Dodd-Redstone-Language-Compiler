package drlc.intermediate.ast.stop;

import drlc.intermediate.ast.ASTNode;
import drlc.node.Node;

public class ExitNode extends StopNode {
	
	public ExitNode(Node[] parseNodes) {
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
		
		scope.definiteLocalReturn = true;
	}
	
	@Override
	public void checkTypes(ASTNode parent) {
		
	}
	
	@Override
	public void foldConstants(ASTNode parent) {
		
	}
	
	@Override
	public void generateIntermediate(ASTNode parent) {
		routine.addExitAction(this);
	}
}
