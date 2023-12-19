package drlc.intermediate.ast.stop;

import org.eclipse.jdt.annotation.Nullable;

import drlc.intermediate.ast.ASTNode;
import drlc.node.Node;

public class ContinueNode extends StopNode {
	
	public final @Nullable String label;
	
	public ContinueNode(Node[] parseNodes, @Nullable String label) {
		super(parseNodes);
		this.label = label;
	}
	
	@Override
	public void setScopes(ASTNode<?, ?> parent) {
		scope = parent.scope;
	}
	
	@Override
	public void defineTypes(ASTNode<?, ?> parent) {
		
	}
	
	@Override
	public void declareExpressions(ASTNode<?, ?> parent) {
		routine = parent.routine;
		
		if (!scope.isBreakable(label)) {
			throw error("Can not use continue statement in non-iterative section!");
		}
	}
	
	@Override
	public void checkTypes(ASTNode<?, ?> parent) {
		
	}
	
	@Override
	public void foldConstants(ASTNode<?, ?> parent) {
		
	}
	
	@Override
	public void trackFunctions(ASTNode<?, ?> parent) {
		
	}
	
	@Override
	public void generateIntermediate(ASTNode<?, ?> parent) {
		routine.addAction(scope.getContinueJump(this, label));
	}
}
